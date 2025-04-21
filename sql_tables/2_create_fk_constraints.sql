USE ProjectDB;

-- 會員
ALTER TABLE user_member
    ADD CONSTRAINT fk_user_member_user_level FOREIGN KEY (level_id) REFERENCES user_level(id);

ALTER TABLE customer_service
    ADD CONSTRAINT fk_customer_service_user_member FOREIGN KEY (user_id) REFERENCES user_member(id);





-- 商品種類
ALTER TABLE [category]
    ADD CONSTRAINT fk_category_self FOREIGN KEY (id) REFERENCES [category](id);

ALTER TABLE [product]
    ADD CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES brand(id),
        CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id);

ALTER TABLE [attribute_value]
    ADD CONSTRAINT fk_attribute_value_product FOREIGN KEY (product_id) REFERENCES product(id),
        CONSTRAINT fk_attribute_value_attribute FOREIGN KEY (attribute_id) REFERENCES attribute(id);

        -- CONSTRAINT UQ_ProductAttribute UNIQUE (product_id, attribute_id);






-- 商品評論 媒體
ALTER TABLE product_review
    ADD CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES [user_member](id);

ALTER TABLE product_review
    ADD CONSTRAINT fk_review_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id);

ALTER TABLE product_media
    ADD CONSTRAINT fk_media_product FOREIGN KEY (product_id) REFERENCES product(id);

ALTER TABLE review_like
    ADD CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES [user_member](id);

ALTER TABLE review_like
    ADD CONSTRAINT fk_like_review FOREIGN KEY (review_id) REFERENCES product_review(id);






-- 後台
-- admin_login_log --
ALTER TABLE admin_login_log
    ADD CONSTRAINT fk_login_log_admin FOREIGN KEY (username) REFERENCES admin_user(username);

-- sales_report --
ALTER TABLE sales_report
    ADD CONSTRAINT fk_sales_report_brand FOREIGN KEY (brand_id) REFERENCES brand(id),
        CONSTRAINT fk_sales_report_product FOREIGN KEY (product_id) REFERENCES product(id);  -- 此表由其他人負責

-- contact_message --
ALTER TABLE contact_message
    ADD CONSTRAINT fk_contact_message_handler FOREIGN KEY (handled_by) REFERENCES admin_user(username);

-- report_case --
ALTER TABLE report_case
    ADD CONSTRAINT fk_report_case_target_review FOREIGN KEY (target_review_id) REFERENCES product_review(id),
        CONSTRAINT fk_report_case_target_product FOREIGN KEY (target_product_id) REFERENCES product(id),
        CONSTRAINT fk_report_case_target_user FOREIGN KEY (target_user_id) REFERENCES [user_member](id),
        CONSTRAINT fk_report_case_target_brand FOREIGN KEY (target_brand_id) REFERENCES brand(id),
        CONSTRAINT fk_report_case_reporter FOREIGN KEY (reporter_user_id) REFERENCES [user_member](id),
        CONSTRAINT fk_report_case_handled_by FOREIGN KEY (handled_by) REFERENCES admin_user(username);

-- admin_action_log --
ALTER TABLE admin_action_log
    ADD CONSTRAINT fk_action_log_admin FOREIGN KEY (username) REFERENCES admin_user(username);





-- 車單
-- cart items, orders
ALTER TABLE cart_items 
    ADD CONSTRAINT FK_cart_user FOREIGN KEY (user_id) REFERENCES [user_member](id),
        CONSTRAINT FK_cart_product FOREIGN KEY (product_id) REFERENCES [product](id);

ALTER TABLE orders 
    ADD CONSTRAINT FK_orders_user FOREIGN KEY (user_id) REFERENCES [user_member](id),
        CONSTRAINT FK_orders_coupon FOREIGN KEY (coupon_published_id) REFERENCES [coupon_published](id);

ALTER TABLE order_items 
    ADD CONSTRAINT FK_order_items_order FOREIGN KEY (order_id) REFERENCES [orders](id),
        CONSTRAINT FK_order_items_product FOREIGN KEY (product_id) REFERENCES [product](id);

ALTER TABLE order_status_logs 
    ADD CONSTRAINT FK_order_status_order FOREIGN KEY (order_id) REFERENCES [orders](id);























-- 活動優惠
-- event, coupon, notification, subscribe
ALTER TABLE [event_prize]
    ADD CONSTRAINT fk_event_prize_event FOREIGN KEY (event_id) REFERENCES [event](id);

ALTER TABLE [event_participant]
    ADD CONSTRAINT fk_event_participant_user FOREIGN KEY (user_id) REFERENCES [user_member](id),
        CONSTRAINT fk_event_participant_event_prize FOREIGN KEY (event_prize_id) REFERENCES [event_prize](id),
        CONSTRAINT fk_event_participant_event FOREIGN KEY (event_id) REFERENCES [event](id);

ALTER TABLE [coupon_published]
    ADD CONSTRAINT fk_coupon_published_coupon_template FOREIGN KEY (coupon_id) REFERENCES [coupon_template](id),
        CONSTRAINT fk_coupon_published_user FOREIGN KEY (user_id) REFERENCES [user_member](id);

ALTER TABLE [notification_published]
    ADD CONSTRAINT fk_notification_published_notification_template FOREIGN KEY (notification_id) REFERENCES [notification_template](id),
        CONSTRAINT fk_notification_published_user FOREIGN KEY (user_id) REFERENCES [user_member](id);

ALTER TABLE [subscribe_list]
    ADD CONSTRAINT fk_subscribe_list_user FOREIGN KEY (user_id) REFERENCES [user_member](id);

ALTER TABLE [event_media]
    ADD CONSTRAINT fk_event_media_event FOREIGN KEY (event_id) REFERENCES [event](id);

ALTER TABLE [coupon_media]
    ADD CONSTRAINT fk_coupon_media_coupon_template FOREIGN KEY (coupon_id) REFERENCES [coupon_template](id);
