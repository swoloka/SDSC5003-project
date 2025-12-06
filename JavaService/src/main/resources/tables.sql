-- 1. 创建 users (用户表)
CREATE TABLE users (
                       username VARCHAR(50) NOT NULL COMMENT '用户名 (主键)',
                       password VARCHAR(255) NOT NULL COMMENT '密码',
                       district VARCHAR(100) COMMENT '地区/区域',
                       contact VARCHAR(100) COMMENT '联系方式',
                       create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       PRIMARY KEY (username)
) COMMENT='用户表';

-- 2. 创建 pet_profiles (动物档案)
-- 外键关联 users 表
CREATE TABLE pet_profiles (
                              profile_id INT AUTO_INCREMENT NOT NULL COMMENT '档案ID (主键)',
                              username VARCHAR(50) NOT NULL COMMENT '外键: 关联用户',
                              pet_name VARCHAR(50) NOT NULL COMMENT '名字',
                              pet_type VARCHAR(50) COMMENT '宠物类型 (如: 猫、狗)',
                              breed VARCHAR(50) COMMENT '品种',
                              description TEXT COMMENT '描述',
                              PRIMARY KEY (profile_id),
                              CONSTRAINT fk_pet_user FOREIGN KEY (username) REFERENCES users(username)
                                  ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='动物档案';

-- 3. 创建 service_profiles (服务档案)
-- 外键关联 users 表
CREATE TABLE service_profiles (
                                  profile_id INT AUTO_INCREMENT NOT NULL COMMENT '档案ID (主键)',
                                  username VARCHAR(50) NOT NULL COMMENT '外键: 关联用户',
                                  pet_type VARCHAR(50) COMMENT '宠物类型 (如: 猫、狗)',
                                  service_type VARCHAR(50) COMMENT '服务类型',
                                  price DECIMAL(10, 2) COMMENT '价格',
                                  description TEXT COMMENT '描述',
                                  PRIMARY KEY (profile_id),
                                  CONSTRAINT fk_service_user FOREIGN KEY (username) REFERENCES users(username)
                                      ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='服务档案';

-- 4. 创建 posts (帖子表)
-- 外键关联 users 表
CREATE TABLE posts (
                       post_id INT AUTO_INCREMENT NOT NULL COMMENT '帖子ID (主键)',
                       username VARCHAR(50) NOT NULL COMMENT '外键: 发布者',
                       role VARCHAR(50) COMMENT '角色',
                       title VARCHAR(200) NOT NULL COMMENT '标题',

                       pet_name VARCHAR(50) COMMENT '名字',
                       breed VARCHAR(50) COMMENT '品种',

                       service_type VARCHAR(50) COMMENT '服务类型',

                       price DECIMAL(10, 2) COMMENT '价格',
                       description TEXT COMMENT '描述',
                       pet_type VARCHAR(50) COMMENT '宠物类型 (如: 猫、狗)',

                       district VARCHAR(100) COMMENT '地区',
                       contact VARCHAR(100) COMMENT '联系方式',

                       create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
                       PRIMARY KEY (post_id),
                       CONSTRAINT fk_post_user FOREIGN KEY (username) REFERENCES users(username)
                           ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='帖子表';