-- Fix message table foreign key constraints
-- This script ensures that the message table has proper foreign key constraints
-- and that we're using valid user IDs for system messages

-- 1. Verify the admin user exists
SELECT id, username, role FROM user WHERE id = 1;

-- 2. If the admin user doesn't exist, create it
-- Uncomment the following lines if needed:
-- INSERT INTO user (id, username, password, email, role, status) 
-- VALUES (1, 'admin', 'admin123', 'admin@example.com', 'admin', 1);

-- 3. Verify the message table structure
DESCRIBE message;

-- 4. Verify foreign key constraints
SHOW CREATE TABLE message;

-- 5. Test inserting a message with admin user as sender
-- This should work now
INSERT INTO message (from_user_id, to_user_id, target_id, target_type, title, content, type, is_read) 
VALUES (1, 1, 1, 'project', 'Test Message', 'This is a test message', 'system', 0);

-- 6. Delete the test message
DELETE FROM message WHERE title = 'Test Message';

-- 7. Check if there are any existing messages with from_user_id = 0
-- These would need to be updated
SELECT * FROM message WHERE from_user_id = 0;