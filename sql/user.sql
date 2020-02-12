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
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` int PRIMARY KEY AUTO_INCREMENT,
  `user_fullname` varchar(20),
  `user_email` varchar(50),
  `user_mobile` varchar(20) NOT NULL,
  `user_password` varchar(15) NOT NULL,
  `user_organization` varchar(50),
  `user_favourite` varchar(50),
  `user_photo` varchar(255)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
SET FOREIGN_KEY_CHECKS = 1;
