
-- Insert some initial users
INSERT INTO App_User (username, password, roles) VALUES ('user1', '$2a$10$gfMWNqmM654M4G6QkZw9RukpiaQncqvjTdxugvOf9jodSm2/XEy.e', 'ROLE_USER'); -- user11
INSERT INTO App_User (username, password, roles) VALUES ('user2', '$2a$10$lJMxx5J8kqaUoTg4cUadXuZhWTlixHGFYZdOMssQIezeemPq9WzBW', 'ROLE_USER'); -- user22
INSERT INTO App_User (username, password, roles) VALUES ('admin', '$2a$10$eQqLNn41uTQK3Omnqgm9w.CzlBkFoXDjHCs5IpDK2vUlt5lvn3dse', 'ROLE_ADMIN'); -- adminadmin

-- Sample data for Achievement
INSERT INTO Achievement (name, description) VALUES ('Top Performer', 'Achieved highest grades in all subjects');
INSERT INTO Achievement (name, description) VALUES ('Excellent Teacher Award', 'Recognized for outstanding teaching skills');
INSERT INTO Achievement (name, description) VALUES ('Student of the Month', 'Selected for exceptional academic performance');

-- Sample data for Achievement_AppUser
INSERT INTO Achievement_App_User (achievement_id, user_id) VALUES (1, 1);
INSERT INTO Achievement_App_User (achievement_id, user_id) VALUES (2, 2);
INSERT INTO Achievement_App_User (achievement_id, user_id) VALUES (3, 3);
