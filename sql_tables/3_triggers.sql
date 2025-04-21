USE ProjectDB;

-- 觸發器 1
CREATE TRIGGER trg_user_member_profile_update
ON user_member
AFTER UPDATE
AS
BEGIN
    IF @@NESTLEVEL > 1
        RETURN;
        
    IF UPDATE(email) OR UPDATE(password) OR UPDATE(user_name) OR UPDATE(birthday) 
       OR UPDATE(phone) OR UPDATE(address) OR UPDATE(carrier)
    BEGIN
        UPDATE user_member
        SET updated_at = GETDATE()
        FROM user_member um
        INNER JOIN inserted i ON um.id = i.id
        WHERE i.updated_at = um.updated_at  
    END
END;

-- 觸發器 2
CREATE TRIGGER trg_user_member_bad_count
ON user_member
AFTER UPDATE
AS
BEGIN
    IF @@NESTLEVEL > 1
        RETURN;
        
    IF UPDATE(bad_count)
    BEGIN
        UPDATE user_member
        SET status = 'invalid',
            updated_at = GETDATE()  
        FROM user_member um
        INNER JOIN inserted i ON um.id = i.id
        WHERE i.bad_count > 20
          AND um.status <> 'invalid'  
    END
END;

-- 觸發器 3
CREATE TRIGGER trg_user_level_update
ON user_level
AFTER UPDATE
AS
BEGIN

    IF @@NESTLEVEL > 1
        RETURN;
        
    IF UPDATE(level_name) OR UPDATE(min_spent) OR UPDATE(discount_rate)
    BEGIN
        UPDATE user_level
        SET updated_at = GETDATE()
        FROM user_level ul
        INNER JOIN inserted i ON ul.id = i.id
        WHERE ul.updated_at = i.updated_at  
    END
END;

-- 觸發器 4
CREATE TRIGGER trg_user_member_auto_level
ON user_member
AFTER UPDATE
AS
BEGIN

    IF @@NESTLEVEL > 1
        RETURN;
        
    IF UPDATE(good_count)
    BEGIN

        UPDATE user_member
        SET level_id = 
            CASE
                WHEN i.good_count BETWEEN 0 AND 10 THEN 
                    (SELECT TOP 1 id FROM user_level WHERE level_name = 'bronze' ORDER BY id)
                WHEN i.good_count BETWEEN 11 AND 20 THEN 
                    (SELECT TOP 1 id FROM user_level WHERE level_name = 'silver' ORDER BY id)
                ELSE 
                    (SELECT TOP 1 id FROM user_level WHERE level_name = 'gold' ORDER BY id)
            END,
            updated_at = GETDATE()
        FROM user_member um
        INNER JOIN inserted i ON um.id = i.id;


        UPDATE user_member
        SET bonus_point = ul.discount_rate,
            updated_at = GETDATE()
        FROM user_member um
        INNER JOIN user_level ul ON um.level_id = ul.id
        INNER JOIN inserted i ON um.id = i.id;
    END
END;

-- 觸發器5
CREATE TRIGGER trg_CustomerService_AutoQuestionDate
ON customer_service
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE cs
    SET contact_date = GETDATE()
    FROM customer_service cs
    INNER JOIN inserted i ON cs.id = i.id
    WHERE i.contact_date IS NULL;
END;

-- 觸發器6
CREATE TRIGGER trg_CustomerService_AdminReply
ON customer_service
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE cs
    SET 
        case_status = 'replied',
        contact_date = CASE WHEN cs.contact_date IS NULL THEN GETDATE() ELSE cs.contact_date END
    FROM customer_service cs
    INNER JOIN inserted i ON cs.id = i.id
    WHERE i.admin_id IS NOT NULL AND 
          (cs.case_status IS NULL OR cs.case_status <> 'replied');
END;