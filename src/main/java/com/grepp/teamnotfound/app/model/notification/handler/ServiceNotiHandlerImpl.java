package com.grepp.teamnotfound.app.model.notification.handler;

import com.grepp.teamnotfound.app.model.board.entity.ArticleLike;
import com.grepp.teamnotfound.app.model.board.repository.ArticleLikeRepository;
import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import com.grepp.teamnotfound.app.model.notification.dto.NotiServiceCreateDto;
import com.grepp.teamnotfound.app.model.notification.dto.NotiUserDto;
import com.grepp.teamnotfound.app.model.notification.entity.ServiceNoti;
import com.grepp.teamnotfound.app.model.notification.repository.ServiceNotiRepository;
import com.grepp.teamnotfound.app.model.reply.entity.Reply;
import com.grepp.teamnotfound.app.model.reply.repository.ReplyRepository;
import com.grepp.teamnotfound.app.model.report.entity.Report;
import com.grepp.teamnotfound.app.model.report.repository.ReportRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.BoardErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.ReportErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceNotiHandlerImpl implements ServiceNotiHandler {

    @Autowired
    private ServiceNotiRepository serviceNotiRepository;
    @Autowired
    private ArticleLikeRepository articleLikeRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private ReportRepository reportRepository;

    @Override
    public NotiUserDto handle(User user, NotiType notiType, NotiServiceCreateDto dto) {
        ServiceNoti noti = new ServiceNoti();

        noti.setNotificationType(notiType);
        noti.setUser(user);

        switch (notiType) {
            case LIKE: {
                ArticleLike al = articleLikeRepository.findById(dto.getTargetId())
                    .orElseThrow(() -> new BusinessException(BoardErrorCode.ARTICLE_NOT_LIKED_YET));
                String alNickname = al.getUser().getNickname();
                noti.setContent(alNickname + "님에게 좋아요를 받았습니다.");
                noti.setTargetId(al.getArticle().getArticleId()); // 좋아요 ID를 받아왔지만 클라이언트에는 해당 게시물을 넘겨주기로
                break;
            }
            case COMMENT: {
                Reply reply = replyRepository.findById(dto.getTargetId())
                    .orElseThrow(() -> new BusinessException(BoardErrorCode.REPLY_NOT_FOUND));
                String replyNickname = reply.getUser().getNickname();
                noti.setContent(replyNickname + "님에게 댓글을 받았습니다.");
                noti.setTargetId(reply.getArticle().getArticleId()); // 댓글 ID를 받아왔지만 클라이언트에는 해당 게시물을 넘겨주기로
                break;
            }
            case REPORT_SUCCESS: {
                Report reportSuc = reportRepository.findByReportId(dto.getTargetId())
                    .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_NOT_FOUND));
                String adminReasonSuc = reportSuc.getAdminReason();
                noti.setContent(adminReasonSuc + " 처리되어 신고가 정상 접수되었습니다.");
                break;
            }
            case REPORT_FAIL: {
                Report reportFail = reportRepository.findByReportId(dto.getTargetId())
                    .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_NOT_FOUND));
                String adminReasonFail = reportFail.getAdminReason();
                noti.setContent(adminReasonFail + " 처리되어 신고가 기각됐습니다.");
                break;
            }
            case REPORTED: {
                Report reported = reportRepository.findByReportId(dto.getTargetId())
                    .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_NOT_FOUND));
                String adminReasonReported = reported.getAdminReason();
                noti.setContent(adminReasonReported + "로 인해 내가 작성한 게시글/댓글이 숨김처리 되었습니다.");
                break;
            }
            // TODO : 맞춤형제안 PR 머지되면 서비스 끼워넣기
            case RECOMMEND:
                noti.setContent("오늘치 맞춤형 제안이 생성되었습니다.");
                break;
            default:
                noti.setContent("알 수 없는 알림이 도착했습니다.");
                break;
        }

        ServiceNoti saved = serviceNotiRepository.save(noti);

        return NotiUserDto.builder()
            .notiId(saved.getServiceNotiId())
            .content(saved.getContent())
            .targetId(saved.getTargetId())
            .type(notiType)
            .isRead(saved.getIsRead())
            .createdAt(saved.getCreatedAt())
            .build();
    }
}
