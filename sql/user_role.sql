/*
 Navicat Premium Data Transfer

 Source Server         : my腾讯云
 Source Server Type    : MySQL
 Source Server Version : 50723
 Source Host           : 193.112.74.20:3306
 Source Schema         : wikibase

 Target Server Type    : MySQL
 Target Server Version : 50723
 File Encoding         : 65001

 Date: 08/10/2018 10:35:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` int(10) NOT NULL,
  `role` int(2) NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
/*INSERT INTO `user_role` VALUES (1, 1);
INSERT INTO `user_role` VALUES (1, 3);
INSERT INTO `user_role` VALUES (1, 4);
INSERT INTO `user_role` VALUES (1, 2);
*/
SET FOREIGN_KEY_CHECKS = 1;
