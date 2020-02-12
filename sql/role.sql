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

 Date: 08/10/2018 10:35:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(2) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员');
INSERT INTO `role` VALUES (2, '审核员');
INSERT INTO `role` VALUES (3, '验收员');
INSERT INTO `role` VALUES (4, '游客');

SET FOREIGN_KEY_CHECKS = 1;
