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

 Date: 08/10/2018 10:35:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rule
-- ----------------------------
DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限名称',
  `api` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '对应接口',
  `status` int(1) NOT NULL COMMENT '权限状态, 0,启用,1不启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule
-- ----------------------------
INSERT INTO `rule` VALUES (1, '登录', 'login', 0);
INSERT INTO `rule` VALUES (2, '注册', 'register', 0);
INSERT INTO `rule` VALUES (3, '更新用户信息', 'update', 0);
INSERT INTO `rule` VALUES (4, '获取用户信息', 'findById', 0);
INSERT INTO `rule` VALUES (5, '获取题目', 'apply', 0);
INSERT INTO `rule` VALUES (6, '答题验证', 'check', 0);
INSERT INTO `rule` VALUES (7, '上传用户头像', 'uploadpicture', 0);
INSERT INTO `rule` VALUES (11, '审核权限', 'review', 0);
INSERT INTO `rule` VALUES (21, '验收权限', 'check', 0);
INSERT INTO `rule` VALUES (31, '图谱浏览', 'ViewKG', 0);

SET FOREIGN_KEY_CHECKS = 1;
