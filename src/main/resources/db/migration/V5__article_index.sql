CREATE INDEX idx_articles_board_deleted_reported_created ON public.articles (board_id, deleted_at, reported_at, created_at DESC);

CREATE INDEX idx_replies_article_deleted_reported ON public.replies (article_id, deleted_at, reported_at);

CREATE INDEX idx_article_imgs_article_type_deleted ON public.article_imgs (article_id, type, deleted_at);

CREATE INDEX idx_article_likes_article_id ON public.article_likes (article_id);