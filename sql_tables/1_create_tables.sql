USE ProjectDB;

-- 會員
CREATE TABLE user_level (
    id INT PRIMARY KEY,
    level_name VARCHAR(10) NOT NULL,
    min_spent INT NOT NULL,
    discount_rate DECIMAL(5, 2) NOT NULL,
    updated_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE user_member (
    id INT PRIMARY KEY IDENTITY(1001,1),
    level_id INT DEFAULT 1,
    line_id VARCHAR(255) UNIQUE,
    google_id VARCHAR(255) UNIQUE,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_name NVARCHAR(50) NOT NULL,
    birthday DATE NOT NULL,
    phone VARCHAR(15) UNIQUE,
    address NVARCHAR(255),
    status VARCHAR(10) DEFAULT 'active',
    carrier VARCHAR(8),
    bonus_point INT DEFAULT 0,
    good_count INT DEFAULT 0,
    bad_count INT DEFAULT 0,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    refresh_token VARCHAR(MAX),
    token_expires_at DATETIME2,
);

CREATE TABLE customer_service (
    id INT PRIMARY KEY IDENTITY(1,1),
    parent_id INT,
    user_id INT,
    question_type NVARCHAR(20),
    purpose NVARCHAR(50),
    content NVARCHAR(1000),
    admin_id INT,
    contact_date DATETIME2,
    case_status NVARCHAR(20) DEFAULT 'asking'
);





-- 商品種類
CREATE TABLE category (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) UNIQUE NOT NULL,
    description NVARCHAR(MAX),
    parent_id INT NULL,
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) DEFAULT SYSDATETIME(),
    FOREIGN KEY (parent_id) REFERENCES category(id)
);

CREATE TABLE product (
    id INT PRIMARY KEY IDENTITY(101, 1),
    brand_id INT NOT NULL,
    category_id INT NOT NULL,
    name NVARCHAR(255) NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0),
    sold_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    description NVARCHAR(MAX),
    is_active BIT NOT NULL DEFAULT 1,
    start_time DATETIME2(0),
    end_time DATETIME2(0),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) DEFAULT SYSDATETIME(),
);

CREATE TABLE attribute (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) UNIQUE NOT NULL,
    description NVARCHAR(MAX),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) DEFAULT SYSDATETIME()
);

CREATE TABLE attribute_value (
    id INT PRIMARY KEY IDENTITY(1,1),
    category_id INT NOT NULL,
    product_id INT NOT NULL,
    attribute_id INT NOT NULL,
    value NVARCHAR(255) NOT NULL,
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) DEFAULT SYSDATETIME(),  
);






-- 商品評論 媒體
CREATE TABLE product_review (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    order_item_id INT NOT NULL,
    review_text NVARCHAR(1000),
    review_images NVARCHAR(1000),
    score_quality INT NOT NULL CHECK (score_quality BETWEEN 1 AND 5),
    score_description INT NOT NULL CHECK (score_description BETWEEN 1 AND 5),
    score_delivery INT NOT NULL CHECK (score_delivery BETWEEN 1 AND 5),
    is_verified_purchase BIT NOT NULL DEFAULT 1,
    is_visible BIT NOT NULL DEFAULT 1,
    helpful_count INT NOT NULL DEFAULT 0,
    tag_name NVARCHAR(200) NOT NULL DEFAULT N'',
    created_at DATETIME2(0) DEFAULT GETDATE(),
    updated_at DATETIME2(0),
    UNIQUE (user_id, order_item_id)
);

CREATE TABLE product_media (
    id INT PRIMARY KEY IDENTITY(1,1),
    product_id INT NOT NULL,
    media_type VARCHAR(10) NOT NULL CHECK (media_type IN ('image', 'video')),
    media_url VARCHAR(500) NOT NULL,
    alt_text NVARCHAR(100),
    media_order INT NOT NULL DEFAULT 0,
    is_main BIT NOT NULL DEFAULT 0,
    created_at DATETIME2(0) DEFAULT GETDATE(),
    updated_at DATETIME2(0)
);

CREATE TABLE review_like (
    id INT PRIMARY KEY IDENTITY(1,1),
    review_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at DATETIME2(0) DEFAULT GETDATE(),
    UNIQUE (review_id, user_id)
);














-- 後台
-- brand -------------------------------------------------------------------------------------
CREATE TABLE brand (
    id INT PRIMARY KEY IDENTITY(1,1),
    brand_name NVARCHAR(100) NOT NULL UNIQUE,
	brand_type NVARCHAR(50) NOT NULL,
    tax_id CHAR(8) UNIQUE,
    address NVARCHAR(255) NOT NULL,
    photo_url VARCHAR(225) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE, 
    phone VARCHAR(20) NOT NULL,
    fax VARCHAR(20),
    contact_name NVARCHAR(100) NOT NULL,
    contact_email VARCHAR(255) NOT NULL, 
    contact_phone VARCHAR(30) NOT NULL,
    brand_status VARCHAR(10) NOT NULL DEFAULT 'active' CHECK (LOWER(brand_status) IN ('active', 'inactive', 'warning')),
    valid_report_count INT NOT NULL DEFAULT 0,
    created_at DATETIME2(0) DEFAULT GETDATE(),
    updated_at DATETIME2(0)
);

-- admin -------------------------------------------------------------------------------------
CREATE TABLE admin_user (
    id INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name NVARCHAR(50) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'staff' CHECK (LOWER(role) IN ('staff', 'manager')),
    password VARCHAR(255) NOT NULL,
    account_status VARCHAR(10) NOT NULL DEFAULT 'pending' CHECK (LOWER(account_status) IN ('pending', 'active', 'suspended', 'inactive')),
    created_at DATETIME2(0) DEFAULT GETDATE(),
    updated_at DATETIME2(0),
    first_login_at DATETIME2(0) 
);
CREATE TABLE admin_login_log (
    id INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) ,
    login_time DATETIME2(0) DEFAULT GETDATE(),
    ip_address VARCHAR(45),
    success BIT NOT NULL,
    message VARCHAR(255)
);

-- sales -------------------------------------------------------------------------------------
CREATE TABLE sales_report (
    id INT PRIMARY KEY IDENTITY(1,1),
    report_month CHAR(7) NOT NULL,
    version INT NOT NULL DEFAULT 1,
    brand_id INT NOT NULL,
    brand_name NVARCHAR(100) NOT NULL,
    product_id INT NOT NULL,
    product_name NVARCHAR(100) NOT NULL,
    average_price DECIMAL(10, 2) NOT NULL,
    quantity_sold INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    is_exported BIT NOT NULL DEFAULT 0,
    exported_at DATETIME2(0),
    generated_at DATETIME2(0) DEFAULT GETDATE()
);

-- contact -----------------------------------------------------------------------------------
CREATE TABLE contact_message (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100)  NOT NULL,
    email  VARCHAR(255) NOT NULL,
    subject NVARCHAR(100) NOT NULL,
    message NVARCHAR(2000) NOT NULL,
    created_at DATETIME2(0) DEFAULT GETDATE(),
    is_handled BIT NOT NULL DEFAULT 0,
    handled_at DATETIME2(0),
    handled_by VARCHAR(50),
    note NVARCHAR(2000)
);

-- optional ----------------------------------------------------------------------------------
CREATE TABLE report_case (
    id INT PRIMARY KEY IDENTITY(1,1),
    target_review_id INT ,
    target_product_id INT ,
    target_user_id INT ,
    target_brand_id INT,
    reporter_user_id INT NOT NULL,
    reason NVARCHAR(2000),
    created_at DATETIME2(0) DEFAULT GETDATE(),
    is_valid BIT NOT NULL DEFAULT 0,
    is_handled BIT NOT NULL DEFAULT 0,
    handled_at DATETIME2(0),
    handled_by VARCHAR(50),
    note NVARCHAR(2000)
);
CREATE TABLE admin_action_log (
    id INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) NOT NULL,
    action_time DATETIME2(0) NOT NULL,
    action_type VARCHAR(20) NOT NULL,
    target_table VARCHAR(20) NOT NULL,
    target_id INT,
    details NVARCHAR(2000)
);














--車單
-- cart_items --------------------------------------------------------------------------------
CREATE TABLE cart_items (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    is_checked_out BIT DEFAULT 0,
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) DEFAULT SYSDATETIME(),
    UNIQUE (user_id, product_id, is_checked_out)
);

-- orders ------------------------------------------------------------------------------------
CREATE TABLE orders (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    coupon_published_id CHAR(36) NULL,
    status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50),
    paid_at DATETIME2(0),
    transaction_number VARCHAR(100),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) DEFAULT SYSDATETIME()
);

-- orders items ------------------------------------------------------------------------------
CREATE TABLE order_items (
    id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price_at_the_time DECIMAL(10, 2) NOT NULL,
    product_name_at_the_time VARCHAR(255),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) DEFAULT SYSDATETIME()
);

-- order status logs -------------------------------------------------------------------------
CREATE TABLE order_status_logs (
    id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    changed_at DATETIME2(0) DEFAULT SYSDATETIME(),
    changed_by VARCHAR(50) DEFAULT 'system'
);





















-- 活動優惠
-- event -------------------------------------------------------------------------------------
CREATE TABLE [event] (
    id INT PRIMARY KEY IDENTITY(1,1),
    event_name NVARCHAR(100) NOT NULL,
    min_spend DECIMAL(15, 2) NOT NULL DEFAULT 0.00 CHECK (min_spend >= 0),
    max_entries INT NOT NULL DEFAULT 1 CHECK (max_entries >= 0),
    start_time DATETIME2(0) NOT NULL,
    end_time DATETIME2(0) NOT NULL,
    announce_time DATETIME2(0) NOT NULL,
    event_status VARCHAR(10) NOT NULL DEFAULT 'ANNOUNCED' CHECK (event_status IN ('ANNOUNCED', 'ACTIVED', 'END')),
    established_by NVARCHAR(100) NOT NULL,
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0)
);

-- event_prize -------------------------------------------------------------------------------
CREATE TABLE [event_prize] (
    id INT PRIMARY KEY IDENTITY(1,1),
    event_id INT NOT NULL,
    item_id INT NOT NULL,
    item_type VARCHAR(20) NOT NULL CHECK (item_type IN ('PRODUCT', 'COUPON_TEMPLATE')),
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity >= 0),
    win_rate DECIMAL(18, 4) NOT NULL DEFAULT 0.2000 CHECK (win_rate BETWEEN 0 AND 1),
    total_slots INT NOT NULL CHECK (total_slots > 0),
    remaining_slots INT NOT NULL CHECK (remaining_slots >= 0),
    title NVARCHAR(100) NOT NULL,
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0)
);

-- event_participant -------------------------------------------------------------------------
CREATE TABLE [event_participant] (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    event_prize_id INT NOT NULL,
    event_id INT NOT NULL,
    participate_status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED' CHECK (participate_status IN ('REGISTERED', 'CANCELLED', 'WINNER', 'LOST')),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0)
);

-- coupon_template ----------------------------------------------------------------------------
CREATE TABLE [coupon_template] (
    id INT PRIMARY KEY IDENTITY(1,1),
    applicable_id INT,
    applicable_type VARCHAR(10) NOT NULL CHECK (applicable_type IN ('ALL', 'BRAND', 'PRODUCT')),
    min_spend DECIMAL(15, 2) NOT NULL DEFAULT 0 CHECK (min_spend >= 0),
    discount_type VARCHAR(10) NOT NULL CHECK (discount_type IN ('VALUE', 'PERCENTAGE')),
    discount_value DECIMAL(15, 2) NOT NULL CHECK (discount_value > 0),
    max_discount DECIMAL(15, 2) CHECK (max_discount > 0),
    tradeable BIT NOT NULL DEFAULT 0,
    start_time DATETIME2(0),
    end_time DATETIME2(0),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0),
    CONSTRAINT chk_max_discount CHECK (
        (discount_type = 'PERCENTAGE' AND max_discount IS NOT NULL AND max_discount > 0) OR
        (discount_type = 'VALUE' AND max_discount IS NULL)
    )
);

-- coupon_published --------------------------------------------------------------------------
CREATE TABLE [coupon_published] (
    id CHAR(36) PRIMARY KEY,
    coupon_id INT NOT NULL,
    user_id INT NOT NULL,
    is_used BIT NOT NULL DEFAULT 0,
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0)    
);

-- notification_tamplate ---------------------------------------------------------------------
CREATE TABLE [notification_template] (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX),
    notice_type VARCHAR(20) NOT NULL CHECK (notice_type IN ('ORDER', 'PROMOTION', 'WISHLIST', 'SUBSCRIPTION')),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    expired_at DATETIME2(0)
);

-- notification_published --------------------------------------------------------------------
CREATE TABLE [notification_published] (
    id INT PRIMARY KEY IDENTITY(1,1),
    notification_id INT NOT NULL,
    user_id INT NOT NULL,
    is_read BIT NOT NULL DEFAULT 0,
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    expired_at DATETIME2(0)
);

-- subscribe list ----------------------------------------------------------------------------
CREATE TABLE [subscribe_list] (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    item_type VARCHAR(10) NOT NULL CHECK (item_type IN ('PRODUCT', 'BRAND')),
    is_subscribing BIT NOT NULL DEFAULT 1,
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0)
);

-- event_media -------------------------------------------------------------------------------
CREATE TABLE [event_media] (
    id INT PRIMARY KEY IDENTITY(1,1),
    event_id INT NOT NULL,
    media_data VARBINARY(MAX) NOT NULL,
    media_type VARCHAR(50) NOT NULL CHECK (media_type IN ('image/jpeg', 'image/png', 'image/gif', 'video/mp4')),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0)
);

-- coupon_media ------------------------------------------------------------------------------
CREATE TABLE [coupon_media] (
    id INT PRIMARY KEY IDENTITY(1,1),
    coupon_id INT NOT NULL,
    media_data VARBINARY(MAX) NOT NULL,
    media_type VARCHAR(50) NOT NULL CHECK (media_type IN ('image/jpeg', 'image/png', 'image/gif', 'video/mp4')),
    created_at DATETIME2(0) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0)    
);