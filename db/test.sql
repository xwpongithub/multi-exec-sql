/*
 Navicat Premium Data Transfer

 Source Server         : Localhost
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 10/09/2020 18:50:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for classes
-- ----------------------------
DROP TABLE IF EXISTS `classes`;
CREATE TABLE `classes`  (
  `id` bigint(20) NOT NULL,
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `class_no` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classes
-- ----------------------------
INSERT INTO `classes` VALUES (1, '理科1班', 'L20200701');
INSERT INTO `classes` VALUES (2, '理科2班', 'L20200702');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `id` bigint(20) NOT NULL,
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `user_no` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `class_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, '李思思', '1993-01-30', '2020070101', 1);
INSERT INTO `student` VALUES (2, '肖楠颖', '1993-07-06', '2020070201', 2);
INSERT INTO `student` VALUES (3, '刘淑怡', '1994-09-30', '2020070202', 2);

-- ----------------------------
-- Function structure for func_query_user
-- ----------------------------
DROP FUNCTION IF EXISTS `func_query_user`;
delimiter ;;
CREATE DEFINER=`root`@`%` FUNCTION `func_query_user`(id int(10)) RETURNS int(11)
    READS SQL DATA
begin
    return (select id from user where id = id);
end
;;
delimiter ;

-- ----------------------------
-- Function structure for func_query_user_by_name
-- ----------------------------
DROP FUNCTION IF EXISTS `func_query_user_by_name`;
delimiter ;;
CREATE DEFINER=`root`@`%` FUNCTION `func_query_user_by_name`(name varchar(16)) RETURNS int(11)
    READS SQL DATA
begin
    return (select id from user where name = name);
end
;;
delimiter ;

-- ----------------------------
-- Function structure for fun_add_user
-- ----------------------------
DROP FUNCTION IF EXISTS `fun_add_user`;
delimiter ;;
CREATE DEFINER=`root`@`%` FUNCTION `fun_add_user`(name VARCHAR(16),sex int(1),age int(3)) RETURNS int(10) unsigned
BEGIN
INSERT user(name,sex,age) VALUES(name,sex,age);
RETURN LAST_INSERT_ID();
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for insert_user
-- ----------------------------
DROP PROCEDURE IF EXISTS `insert_user`;
delimiter ;;
CREATE DEFINER=`root`@`%` PROCEDURE `insert_user`(OUT u_id INTEGER,IN u_name VARCHAR(20),IN u_sex VARCHAR(20),IN u_age INTEGER)
BEGIN
INSERT INTO user (name,sex,age) VALUES (u_name,u_sex,u_age);
SET u_id=LAST_INSERT_ID();
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
