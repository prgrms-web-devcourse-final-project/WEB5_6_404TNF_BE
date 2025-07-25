package com.grepp.teamnotfound.app.model.board;

import com.grepp.teamnotfound.app.controller.api.article.payload.ArticleDetailResponse;
import com.grepp.teamnotfound.app.controller.api.article.payload.ArticleListRequest;
import com.grepp.teamnotfound.app.controller.api.article.payload.ArticleListResponse;
import com.grepp.teamnotfound.app.controller.api.article.payload.ArticleRequest;
import com.grepp.teamnotfound.app.controller.api.article.payload.LikeResponse;
import com.grepp.teamnotfound.app.controller.api.article.payload.PageInfo;
import com.grepp.teamnotfound.app.controller.api.mypage.payload.UserProfileArticleResponse;
import com.grepp.teamnotfound.app.model.board.code.ProfileBoardType;
import com.grepp.teamnotfound.app.model.board.code.SortType;
import com.grepp.teamnotfound.app.model.board.dto.ArticleListDto;
import com.grepp.teamnotfound.app.model.board.dto.UserArticleListDto;
import com.grepp.teamnotfound.app.model.board.entity.Article;
import com.grepp.teamnotfound.app.model.board.entity.ArticleImg;
import com.grepp.teamnotfound.app.model.board.entity.ArticleLike;
import com.grepp.teamnotfound.app.model.board.entity.Board;
import com.grepp.teamnotfound.app.model.board.repository.ArticleImgRepository;
import com.grepp.teamnotfound.app.model.board.repository.ArticleLikeRepository;
import com.grepp.teamnotfound.app.model.board.repository.ArticleRepository;
import com.grepp.teamnotfound.app.model.board.repository.BoardRepository;
import com.grepp.teamnotfound.app.model.reply.repository.ReplyRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.code.ImgType;
import com.grepp.teamnotfound.infra.error.exception.AuthException;
import com.grepp.teamnotfound.infra.error.exception.BoardException;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.CommonException;
import com.grepp.teamnotfound.infra.error.exception.code.BoardErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.CommonErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import com.grepp.teamnotfound.infra.util.file.FileDto;
import com.grepp.teamnotfound.infra.util.file.GoogleStorageManager;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    private final GoogleStorageManager fileManager;
    private final ArticleImgRepository articleImgRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ReplyRepository replyRepository;
    private final RedisLikeService redisLikeService;

    @Transactional
    public ArticleListResponse getArticles(ArticleListRequest request) {

        Page<ArticleListDto> articleListPage = articleRepository.findArticleListWithMeta(
            request.getPage() - 1,
            request.getSize(),
            request.getBoardType(),
            request.getSortType(),
            request.getSearchType(),
            request.getKeyword()
        );

        return ArticleListResponse.builder()
            .articleList(articleListPage.toList())
            .pageInfo(PageInfo.fromPage(articleListPage))
            .build();
    }

    @Transactional
    public Long writeArticle(ArticleRequest request, List<MultipartFile> images, Long userId) {
        // 토큰 발급 시점과 현재 DB 사이의 무결성 검증
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        Board board = boardRepository.findByName(request.getBoardType().name())
            .orElseThrow(() -> new BoardException(BoardErrorCode.BOARD_NOT_FOUND));

        if (user.getSuspensionEndAt() != null && user.getSuspensionEndAt().isAfter(OffsetDateTime.now())) {
            throw new BusinessException(UserErrorCode.USER_REPORTED);
        }

        Article article = Article.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .views(0)
            .user(user)
            .board(board)
            .build();

        Article savedArticle = articleRepository.save(article);
        uploadAndSaveImgs(images, savedArticle);

        return savedArticle.getArticleId();
    }

    // 읽기 + 쓰기
    @Transactional
    public ArticleDetailResponse findByArticleIdAndUserId(Long articleId, Long userId) {
        // 로그인한 회원일 경우 DB 와 정합성 검증
        if (userId != null) {
            userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));
        }

        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        // JOIN 해서 상세 정보를 모두 가져오기 전에 빠른 리턴
        if (article.getDeletedAt() != null) {
            throw new BoardException(BoardErrorCode.ARTICLE_DELETED);
        }

        if (article.getReportedAt() != null) {
            throw new BoardException(BoardErrorCode.ARTICLE_REPORTED);
        }

        ArticleDetailResponse response = articleRepository.findDetailById(articleId, userId);

        // Redis 카운터 캐시 값으로 덮어씀
        Integer redisLikeCount = getArticleLikeCount(articleId);
        response.setLikes(redisLikeCount);

        articleRepository.plusViewById(articleId);
        response.setViews(response.getViews() + 1);

        return response;
    }

    @Transactional
    public void updateArticle(Long articleId, ArticleRequest request, List<MultipartFile> images,
        Long userId) {

        User requestUser = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        Article beforeArticle = articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        if (!beforeArticle.getUser().getUserId().equals(requestUser.getUserId())) {
            throw new BoardException(BoardErrorCode.ARTICLE_FORBIDDEN);
        }

        if (!beforeArticle.getBoard().getName().equals(request.getBoardType().name())) {
            throw new BoardException(BoardErrorCode.BOARD_CONFLICT);
        }

        if (requestUser.getSuspensionEndAt() != null &&
            requestUser.getSuspensionEndAt().isAfter(OffsetDateTime.now())) {
            throw new BusinessException(UserErrorCode.USER_REPORTED);
        }

        beforeArticle.setTitle(request.getTitle());
        beforeArticle.setContent(request.getContent());
        beforeArticle.setUpdatedAt(OffsetDateTime.now());
        Article savedArticle = articleRepository.save(beforeArticle);

        // TODO 스토리지에 저장된 불필요한 사진 파일들에 대한 배치 스케줄러 도입 필요
        articleImgRepository.softDeleteByArticleId(articleId, OffsetDateTime.now());
        uploadAndSaveImgs(images, savedArticle);
    }

    @Transactional
    public void deleteArticle(Long articleId, Long userId) {

        User requestUser = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        if (!article.getUser().getUserId().equals(requestUser.getUserId())) {
            throw new BoardException(BoardErrorCode.ARTICLE_FORBIDDEN);
        }

        // 게시글을 삭제하면 댓글, 이미지 모두 soft delete
        OffsetDateTime deletedTime = OffsetDateTime.now();
        article.setDeletedAt(deletedTime);
        article.setUpdatedAt(deletedTime);
        articleImgRepository.softDeleteByArticleId(articleId, deletedTime);
        articleLikeRepository.hardDeleteByArticleId(articleId);
        replyRepository.softDeleteByArticleId(articleId, deletedTime);
    }

    private void uploadAndSaveImgs(List<MultipartFile> images, Article targetArticle) {
        if (images == null || images.isEmpty()) {
            return;
        }

        try {
            // GCP bucket 에 업로드
            List<FileDto> imgList = fileManager.upload(images, "article");

            // repository 에 저장
            List<ArticleImg> articleImgs = imgList
                .stream()
                .map(fileDto -> {
                    ImgType type = ImgType.DESC;
                    // NOTE 게시글을 작성할 때 썸네일을 설정할 수 있게? 아니면 첫번째 이미지?
                    if (imgList.getFirst().originName().equals(fileDto.originName())) {
                        type = ImgType.THUMBNAIL;
                    }
                    ArticleImg articleImg = ArticleImg.fromFileDto(type, fileDto);
                    articleImg.setArticle(targetArticle);
                    return articleImg;
                })
                .toList();

            articleImgRepository.saveAll(articleImgs);

        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Transactional
    public LikeResponse likeArticle(Long articleId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        boolean exists = articleLikeRepository.existsByArticle_ArticleIdAndUser_UserId(articleId,
            userId);
        if (exists) {
            // 중복 요청은 무시
            Integer count = articleLikeRepository.countByArticle_ArticleId(articleId);
            return new LikeResponse(articleId, count, true);
        }

        ArticleLike articleLike = ArticleLike.builder()
            .article(article)
            .user(user)
            .createdAt(OffsetDateTime.now())
            .build();

        articleLikeRepository.save(articleLike);
        Integer totalCount = articleLikeRepository.countByArticle_ArticleId(articleId);

        return new LikeResponse(articleId, totalCount, true);
    }

    @Transactional
    public LikeResponse unlikeArticle(Long articleId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        Optional<ArticleLike> result = articleLikeRepository.findByArticle_ArticleIdAndUser_UserId(
            articleId, userId);
        if (result.isEmpty()) {
            // 좋아요 한 적 없었던 경우
            Integer count = articleLikeRepository.countByArticle_ArticleId(articleId);
            return new LikeResponse(articleId, count, false);
        }

        articleLikeRepository.delete(result.get());
        Integer totalCount = articleLikeRepository.countByArticle_ArticleId(articleId);
        return new LikeResponse(articleId, totalCount, false);
    }

    @Transactional
    public LikeResponse likeWithRedis(Long articleId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        // Redis 캐시를 우선적으로 확인(빠른 응답)
        // 중복 요청이면 무시
        boolean isLikedInRedis = redisLikeService.isUserLikedInRedis(articleId, userId);
        if (isLikedInRedis) {
            Integer totalCount = getArticleLikeCount(articleId);
            return new LikeResponse(articleId, totalCount, true);
        }

        redisLikeService.addLikeRequest(articleId, userId);
        redisLikeService.setUserLikedStatus(articleId, userId, true);
        redisLikeService.incrementArticleLikesCount(articleId); // 게시글 총 좋아요 카운터 증가

        Integer totalCount = getArticleLikeCount(articleId);
        boolean isLiked = redisLikeService.isUserLikedInRedis(articleId, userId);

        return new LikeResponse(articleId, totalCount, isLiked);
    }

    @Transactional
    public LikeResponse unlikeWithRedis(Long articleId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        // Redis 캐시를 우선적으로 확인(빠른 응답)
        // 중복 요청이면 무시
        boolean isLikedInRedis = redisLikeService.isUserLikedInRedis(articleId, userId);
        if (!isLikedInRedis) {
            Integer totalCount = getArticleLikeCount(articleId);
            return new LikeResponse(articleId, totalCount, false);
        }

        redisLikeService.addUnlikeRequest(articleId, userId);
        redisLikeService.setUserLikedStatus(articleId, userId, false);
        redisLikeService.decrementArticleLikesCount(articleId);

        Integer totalCount = getArticleLikeCount(articleId);
        boolean isLiked = redisLikeService.isUserLikedInRedis(articleId, userId);

        return new LikeResponse(articleId, totalCount, isLiked);
    }

    // 캐시에서 좋아요 수 먼저 조회
    private Integer getArticleLikeCount(Long articleId) {
        // NOTE 게시글 리스트 조회에서도 실시간 좋아요 수가 필요할까?
        Long count = redisLikeService.getArticleLikesCount(articleId);

        if (count == null) {
            Integer dbLikeCount = articleLikeRepository.countByArticle_ArticleId(articleId);
            count = dbLikeCount.longValue();

            // 캐시 저장
            redisLikeService.setArticleLikesCount(articleId, count);
        }

        return count.intValue();
    }

    // DB 에서 댓글 수 조회
    @Transactional(readOnly = true)
    public Integer getReplyCount(Long articleId) {
        articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        return replyRepository.countByArticle_ArticleIdAndDeletedAtIsNullAndReportedAtIsNull(
            articleId);
    }

    // DB 에서 좋아요 수 조회
    @Transactional(readOnly = true)
    public Integer getActualLikeCount(Long articleId) {
        articleRepository.findById(articleId)
            .orElseThrow(() -> new BoardException(BoardErrorCode.ARTICLE_NOT_FOUND));

        return articleRepository.countByArticleIdAndDeletedAtIsNullAndReportedAtIsNull(articleId);
    }

    @Transactional
    public UserProfileArticleResponse getUsersArticles(
        Long userId,
        ProfileBoardType type,
        int page,
        int size,
        SortType sortType
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        Page<UserArticleListDto> articleListPage = articleRepository.findUserArticleListWithMeta(
            page - 1,
            size,
            sortType,
            type,
            user.getUserId()
        );

        return UserProfileArticleResponse.builder()
            .articles(articleListPage.toList())
            .pageInfo(PageInfo.fromPage(articleListPage))
            .build();
    }
}
