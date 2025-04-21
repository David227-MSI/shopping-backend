USE ProjectDB;

-- test cases --------------------------------------------------------------------------------
-- 會員
INSERT INTO user_level (id, level_name, min_spent, discount_rate) VALUES 
    (1, 'bronze',  0, 95.00),
    (2, 'bronze', 10, 95.00),
    (3, 'silver', 11, 90.00),
    (4, 'silver', 20, 90.00),
    (5, 'gold',   21, 85.00);

INSERT INTO user_member (level_id,line_id, google_id, email, password, user_name, birthday, phone, address, status,carrier) VALUES 
    (1, 'line_user_001', 'google_user_001', 'user001@example.com', 'hashed_password_001', '王小明', '1990-05-15', '0912345678', '台北市信義區信義路五段7號', 'active', '/ABC123'),
    (3, 'line_user_002', 'google_user_002', 'user002@example.com', 'hashed_password_002', '林美麗', '1988-10-20', '0923456789', '台北市大安區復興南路一段390號', 'active', '/DEF456'),
    (5, 'line_user_003', 'google_user_003', 'user003@example.com', 'hashed_password_003', '張大華', '1985-03-08', '0934567890', '台北市中山區南京東路三段219號', 'active', '/GHI789'),
    (2, 'line_user_004', NULL, 'user004@example.com', 'hashed_password_004', '李小華', '1995-12-25', '0945678901',  NULL, 'active', NULL),
    (1, 'line_user_005', 'google_user_005', 'user005@example.com', 'hashed_password_005', '陳問題', '1992-07-30', '0956789012', '台北市松山區民生東路四段133號', 'active', '/JKL012');

INSERT INTO customer_service (parent_id,user_id, question_type, purpose,content,admin_id) VALUES 
    (NULL, 1001, '商品問題', '退換貨', '我購買的商品有瑕疵，想申請退貨', NULL),
    (NULL, 1002, '訂單問題', '配送狀態', '我的訂單已經一週沒有更新狀態了', 5),
    (2, 1002, '訂單問題', '配送狀態', '謝謝回覆，但我還有後續問題', NULL),
    (NULL, 1003, '帳戶問題', '密碼重設', '我無法登入我的帳戶，需要重設密碼', 8);





-- 後台
-- brand
INSERT INTO brand (brand_name, brand_type, tax_id, address, photo_url, email, phone, fax, contact_name, contact_email, contact_phone, brand_status, valid_report_count) VALUES 
    ('Dior','香水', '24581379', N'台北市大安區復興南路一段88號', 'mushen.jpg', 'service@mushenlife.com', '02-27112233', '02-27113344', N'張小姐', 'zhang@mushenlife.com', '0932123456', 'active', 0 ),
    ('Zara','衣服', '38204617', N'新北市板橋區文化路二段166號', 'lekang.jpg', 'contact@lekangdaily.com', '02-29667788', NULL, N'王先生', 'w.wang@lekangdaily.com', '0967888999', 'active', 1 ),
    ('Apple','手機', '50713962', N'台中市西屯區市政北二路88號', 'cleanhome.jpg', 'info@cleanhome.com.tw', '04-22558877', '04-22559966', N'陳經理', 'chenc@cleanhome.com.tw', '0988234567', 'active', 2 ),
    (N'品潔生活股份有限公司', N'生活用品', '26841573', N'新北市新店區中央路100號', 'pinjie.jpg', 'hello@pinjiehome.com', '02-22112211', NULL, N'高小姐', 'k.gao@pinjiehome.com', '0911122333', 'active', 0 ),
    (N'悅然日用品有限公司', N'生活用品', '35679214', N'桃園市中壢區中山路200號', 'yueran.jpg', 'info@yueranlife.com.tw', '03-33114422', '03-33115566', N'陳主任', 'chen@yueranlife.com.tw', '0922567890', 'inactive', 6);

-- admin_user
INSERT INTO admin_user (username, full_name, role, password, account_status) VALUES 
    ('amy', N'張艾米', 'manager', 'passpass', 'active'),
    ('bob', N'林柏翰', 'staff', '1234', 'active' ),
    ('chris', N'陳信宇', 'staff', '123456', 'pending');








-- 商品種類
INSERT INTO category (parent_id, name) VALUES
    (NULL, N'美妝'),
    (1,    N'香水'),
    (2,    N'女香'),
    (2,    N'男香'),
    (NULL, N'3C'),
    (5,    N'手機'),
    (5,    N'筆電'),
    (5,    N'耳機'),
    (NULL, N'服飾'),
    (9,    N'男裝'),
    (9,    N'女裝');

INSERT INTO product (brand_id, category_id, name, unit_price, stock) VALUES
    (1, 1, N'香水 A', 1800, 100),
    (1, 1, N'香水 B', 2200, 50),
    (2, 2, N'iPhone 15 Pro Max', 43900, 300),
    (3, 3, N'黑色棉T', 890, 150);

INSERT INTO attribute (name) VALUES
    (N'容量'),
    (N'香調'),
    (N'濃度'),
    (N'適用性別'),
    (N'螢幕尺寸'),
    (N'處理器'),
    (N'記憶體（RAM）'),
    (N'容量（儲存空間）'),
    (N'材質'),
    (N'版型'),
    (N'顏色'),
    (N'產地'),
    (N'保固');

INSERT INTO attribute_value (product_id, category_id, attribute_id, value) VALUES
    (101, 1, 1, N'50ml'),
    (101, 1, 2, N'木質香調'),
    (101, 1, 3, N'香精'),
    (102, 1, 1, N'100ml'),
    (102, 1, 2, N'花果香調'),
    (103, 2, 5, N'6.7 吋'),
    (103, 2, 6, N'A17 Pro'),
    (103, 2, 7, N'8GB'),
    (103, 2, 8, N'256GB'),
    (103, 2, 11, N'鈦金屬藍'),
    (103, 2, 13, N'1 年'),
    (104, 3, 9, N'100% 純棉'),
    (104, 3, 10, N'修身'),
    (104, 3, 11, N'黑色'),
    (104, 3, 12, N'台灣製造');

INSERT INTO attribute_value (product_id, category_id, attribute_id, value) VALUES
    (101, 1, 1, N'50ml'),
    (101, 1, 2, N'木質香調'),
    (101, 1, 3, N'香精');

-- 香水 B（product_id = 102）
INSERT INTO attribute_value (product_id, category_id, attribute_id, value) VALUES
    (102, 1, 1, N'100ml'),
    (102, 1, 2, N'花果香調');

-- iPhone 15 Pro Max（product_id = 103）
INSERT INTO attribute_value (product_id, category_id, attribute_id, value) VALUES
    (103, 2, 5, N'6.7 吋'),
    (103, 2, 6, N'A17 Pro'),
    (103, 2, 7, N'8GB'),
    (103, 2, 8, N'256GB'),
    (103, 2, 11, N'鈦金屬藍'),
    (103, 2, 13, N'1 年');

-- 黑色棉T（product_id = 104）
INSERT INTO attribute_value (product_id, category_id, attribute_id, value) VALUES
    (104, 3, 9, N'100% 純棉'),
    (104, 3, 10, N'修身'),
    (104, 3, 11, N'黑色'),
    (104, 3, 12, N'台灣製造');













-- 活動優惠
-- event -------------------------------------------------------------------------------------
INSERT INTO [event] (event_name, min_spend, max_entries, start_time, end_time, announce_time, event_status, established_by, created_at) VALUES
    ('Spring Lucky Draw', 500.00, 2, '2025-05-01 00:00:00', '2025-05-07 23:59:59', '2025-05-08 10:00:00', 'ANNOUNCED', 'Marketing Team', '2025-04-01 10:00:00'),
    ('Summer Sale Raffle', 1000.00, 3, '2025-07-01 00:00:00', '2025-07-07 23:59:59', '2025-07-08 10:00:00', 'ACTIVED', 'Sales Team', '2025-04-01 10:01:00'),
    ('Tech Expo Giveaway', 0.00, 5, '2025-09-01 00:00:00', '2025-09-03 23:59:59', '2025-09-04 10:00:00', 'ACTIVED', 'Tech Department', '2025-04-01 10:02:00'),
    ('Winter Prize Fest', 2000.00, 1, '2025-12-01 00:00:00', '2025-12-07 23:59:59', '2025-12-08 10:00:00', 'ANNOUNCED', 'Event Planner', '2025-04-01 10:03:00'),
    ('New Year Lottery', 800.00, 4, '2026-01-01 00:00:00', '2026-01-03 23:59:59', '2026-01-04 10:00:00', 'END', 'Marketing Team', '2025-04-01 10:04:00');

-- coupon_template ----------------------------------------------------------------------------
INSERT INTO [coupon_template] (applicable_id, applicable_type, min_spend, discount_type, discount_value, max_discount, tradeable, start_time, end_time, created_at) VALUES
    (NULL, 'ALL', 0.00, 'VALUE', 100.00, NULL, 0, '2025-05-01 00:00:00', '2025-12-31 23:59:59', '2025-04-01 10:00:00'),
    (1, 'BRAND', 500.00, 'PERCENTAGE', 10.00, 200.00, 1, '2025-05-01 00:00:00', '2025-12-31 23:59:59', '2025-04-01 10:01:00'),
    (2, 'BRAND', 1000.00, 'VALUE', 200.00, NULL, 0, '2025-05-01 00:00:00', '2025-12-31 23:59:59', '2025-04-01 10:02:00'),
    (101, 'PRODUCT', 300.00, 'PERCENTAGE', 15.00, 150.00, 1, '2025-05-01 00:00:00', '2025-12-31 23:59:59', '2025-04-01 10:03:00'),
    (NULL, 'ALL', 0.00, 'VALUE', 50.00, NULL, 0, '2025-05-01 00:00:00', '2025-12-31 23:59:59', '2025-04-01 10:04:00');

-- event_prize -------------------------------------------------------------------------------
INSERT INTO [event_prize] (event_id, item_id, item_type, quantity, win_rate, total_slots, remaining_slots, title, created_at) VALUES
    (1, 1, 'COUPON_TEMPLATE', 10, 0.2000, 50, 50, '100 Off Coupon Prize', '2025-04-01 10:00:00'),
    (2, 2, 'COUPON_TEMPLATE', 5, 0.1500, 30, 30, '10% Off Nike Prize', '2025-04-01 10:01:00'),
    (3, 3, 'COUPON_TEMPLATE', 8, 0.3000, 40, 40, '200 Off Apple Prize', '2025-04-01 10:02:00'),
    (4, 101, 'PRODUCT', 2, 0.1000, 10, 10, 'Smartphone X Prize', '2025-04-01 10:03:00'),
    (5, 102, 'PRODUCT', 3, 0.2500, 15, 15, 'Wireless Earbuds Prize', '2025-04-01 10:04:00');

-- event_participant -------------------------------------------------------------------------
INSERT INTO [event_participant] (user_id, event_prize_id, event_id, participate_status, created_at) VALUES
    (1001, 1, 1, 'REGISTERED', '2025-04-01 10:00:00'),
    (1002, 2, 2, 'WINNER', '2025-04-01 10:01:00'),
    (1003, 3, 3, 'LOST', '2025-04-01 10:02:00'),
    (1004, 4, 4, 'CANCELLED', '2025-04-01 10:03:00'),
    (1005, 5, 5, 'REGISTERED', '2025-04-01 10:04:00');

-- coupon_published --------------------------------------------------------------------------
INSERT INTO [coupon_published] (id, coupon_id, user_id, is_used, created_at) VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 1, 1001, 0, '2025-04-01 10:00:00'),
    ('550e8400-e29b-41d4-a716-446655440001', 2, 1002, 1, '2025-04-01 10:01:00'),
    ('550e8400-e29b-41d4-a716-446655440002', 3, 1003, 0, '2025-04-01 10:02:00'),
    ('550e8400-e29b-41d4-a716-446655440003', 4, 1004, 0, '2025-04-01 10:03:00'),
    ('550e8400-e29b-41d4-a716-446655440004', 5, 1005, 0, '2025-04-01 10:04:00');

-- notification_tamplate ---------------------------------------------------------------------
INSERT INTO [notification_template] (title, content, notice_type, created_at, expired_at) VALUES
    ('Order Confirmation', 'Your order #123 has been confirmed.', 'ORDER', '2025-04-01 10:00:00', '2025-12-31 23:59:59'),
    ('Summer Sale', 'Don''t miss our summer discounts!', 'PROMOTION', '2025-04-01 10:01:00', '2025-07-31 23:59:59'),
    ('Wishlist Update', 'An item in your wishlist is now on sale.', 'WISHLIST', '2025-04-01 10:02:00', NULL),
    ('Subscription Renewal', 'Your subscription has been renewed.', 'SUBSCRIPTION', '2025-04-01 10:03:00', '2025-12-31 23:59:59'),
    ('New Coupon', 'You received a new coupon!', 'PROMOTION', '2025-04-01 10:04:00', '2025-12-31 23:59:59');

-- notification_published --------------------------------------------------------------------
INSERT INTO [notification_published] (notification_id, user_id, is_read, created_at, expired_at) VALUES
    (1, 1001, 0, '2025-04-01 10:00:00', '2025-12-31 23:59:59'),
    (2, 1002, 1, '2025-04-01 10:01:00', '2025-07-31 23:59:59'),
    (3, 1003, 0, '2025-04-01 10:02:00', NULL),
    (4, 1004, 0, '2025-04-01 10:03:00', '2025-12-31 23:59:59'),
    (5, 1005, 0, '2025-04-01 10:04:00', '2025-12-31 23:59:59');

-- subscribe list ----------------------------------------------------------------------------
INSERT INTO [subscribe_list] (user_id, item_id, item_type, is_subscribing, created_at) VALUES
    (1001, 101, 'PRODUCT', 1, '2025-04-01 10:00:00'),
    (1002, 102, 'PRODUCT', 1, '2025-04-01 10:01:00'),
    (1003, 1, 'BRAND', 0, '2025-04-01 10:02:00'),
    (1004, 2, 'BRAND', 1, '2025-04-01 10:03:00'),
    (1005, 102, 'PRODUCT', 1, '2025-04-01 10:04:00');

-- event_media -------------------------------------------------------------------------------
INSERT INTO [event_media] (event_id, media_data, media_type, created_at) VALUES
    (1, CAST('spring_draw_poster' AS VARBINARY(MAX)), 'image/jpeg', '2025-04-01 10:00:00'),
    (2, CAST('summer_raffle_video' AS VARBINARY(MAX)), 'video/mp4', '2025-04-01 10:01:00'),
    (3, CAST('tech_expo_banner' AS VARBINARY(MAX)), 'image/png', '2025-04-01 10:02:00'),
    (4, CAST('winter_fest_gif' AS VARBINARY(MAX)), 'image/gif', '2025-04-01 10:03:00'),
    (5, CAST('new_year_poster' AS VARBINARY(MAX)), 'image/jpeg', '2025-04-01 10:04:00');

-- coupon_media ------------------------------------------------------------------------------
INSERT INTO [coupon_media] (coupon_id, media_data, media_type, created_at) VALUES
    (1, CAST('100off_coupon_image' AS VARBINARY(MAX)), 'image/jpeg', '2025-04-01 10:00:00'),
    (2, CAST('10off_nike_gif' AS VARBINARY(MAX)), 'image/gif', '2025-04-01 10:01:00'),
    (3, CAST('200off_apple_image' AS VARBINARY(MAX)), 'image/png', '2025-04-01 10:02:00'),
    (4, CAST('15off_smartphone_video' AS VARBINARY(MAX)), 'video/mp4', '2025-04-01 10:03:00'),
    (5, CAST('50off_coupon_image' AS VARBINARY(MAX)), 'image/jpeg', '2025-04-01 10:04:00');





-- 車單
-- cart_items
INSERT INTO cart_items (user_id, product_id, quantity, is_checked_out) VALUES 
    (1001, 102, 2, 0),
    (1001, 101, 1, 0),
    (1002, 102, 3, 1),
    (1003, 103, 1, 0),
    (1003, 101, 2, 1);

-- orders
INSERT INTO orders (user_id, total_amount, discount_amount, coupon_published_id, status, payment_status, payment_method) VALUES
    (1001, 200.00, 20.00, '550e8400-e29b-41d4-a716-446655440000', '已完成', '已付款', '信用卡'),
    (1002, 150.00,  0.00, NULL, '已完成', '未付款', '信用卡'),
    (1001, 300.00, 50.00, NULL, '已完成', '已付款', '信用卡'),
    (1003, 120.00,  0.00, '550e8400-e29b-41d4-a716-446655440003', '已完成', '已付款', '信用卡'),
    (1004, 450.00, 30.00, '550e8400-e29b-41d4-a716-446655440004', '已取消', '未付款', '信用卡');

-- order_items
INSERT INTO order_items (order_id, product_id, quantity, price_at_the_time, product_name_at_the_time) VALUES
    (2, 101, 2, 100.00, 'T-shirt'),
    (1, 101, 1,  50.00, 'Hat'),
    (2, 102, 3,  40.00, 'Bag'),
    (3, 101, 1,  80.00, 'Shoes'),
    (4, 102, 2,  60.00, 'Pen');

-- order_status_logs
INSERT INTO order_status_logs (order_id, status, changed_by) VALUES
    (2, '訂單成立', 'system'),
    (1, '已出貨'  , 'system'),
    (2, '訂單成立', 'admin'),
    (3, '訂單成立', 'system'),
    (3, '配送中'  , 'admin');













-- 商品評論 媒體
-- product_review
INSERT INTO product_review (user_id, order_item_id, review_text, review_images, score_quality, score_description, score_delivery, tag_name) VALUES
    (1001, 1, N'商品品質不錯，出貨也快', N'["https://cdn.com/img1.jpg","https://cdn.com/咖啡杯.jpg"]', 5, 5, 5, N'出貨快,品質好'),
    (1002, 2, N'與描述略有落差，但包裝完整', N'["https://cdn.com/img2.jpg"]', 3, 2, 4, N'包裝完整,描述相符'),
    (1003, 3, N'CP值很高，下次還會回購', N'["https://cdn.com/img3.jpg"]', 4, 4, 4, N'CP值高,回購意願');

-- product_media
INSERT INTO product_media (product_id, media_type, media_url, is_main, media_order, alt_text) VALUES 
    (101, 'image', 'https://picsum.photos/seed/product1/400/400', 1, 1, N'主圖'),
    (101, 'image', 'https://picsum.photos/seed/product2/400/400', 0, 2, N'側面'),
    (102, 'video', 'https://samplelib.com/lib/preview/mp4/sample-5s.mp4', 0, 1, N'示範影片');

-- review_like
INSERT INTO review_like (review_id, user_id) VALUES
    (1, 1001),
    (1, 1002),
    (2, 1003);