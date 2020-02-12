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

 Date: 08/10/2018 10:35:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role_rule
-- ----------------------------
DROP TABLE IF EXISTS `role_rule`;
CREATE TABLE `role_rule`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` int(2) NOT NULL COMMENT '角色id',
  `rule_id` int(10) NOT NULL COMMENT '该角色拥有权限',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_rule
-- ----------------------------
INSERT INTO `role_rule` VALUES (1, 1, 1);
INSERT INTO `role_rule` VALUES (2, 1, 2);
INSERT INTO `role_rule` VALUES (3, 1, 3);
INSERT INTO `role_rule` VALUES (4, 1, 4);
INSERT INTO `role_rule` VALUES (5, 2, 1);
INSERT INTO `role_rule` VALUES (6, 2, 2);
INSERT INTO `role_rule` VALUES (7, 2, 3);
INSERT INTO `role_rule` VALUES (8, 2, 4);
INSERT INTO `role_rule` VALUES (9, 3, 1);
INSERT INTO `role_rule` VALUES (10, 3, 2);
INSERT INTO `role_rule` VALUES (11, 3, 3);
INSERT INTO `role_rule` VALUES (12, 3, 4);
INSERT INTO `role_rule` VALUES (13, 4, 1);
INSERT INTO `role_rule` VALUES (14, 4, 2);
INSERT INTO `role_rule` VALUES (15, 4, 3);
INSERT INTO `role_rule` VALUES (16, 4, 4);
INSERT INTO `role_rule` VALUES (17, 4, 5);
INSERT INTO `role_rule` VALUES (18, 4, 6);

INSERT INTO `role_rule` VALUES (19, 1, 7);
INSERT INTO `role_rule` VALUES (20, 2, 7);
INSERT INTO `role_rule` VALUES (21, 3, 7);
INSERT INTO `role_rule` VALUES (22, 4, 7);

INSERT INTO `role_rule` VALUES (23, 2, 11);
INSERT INTO `role_rule` VALUES (24, 3, 21);
INSERT INTO `role_rule` VALUES (25, 1, 11);
INSERT INTO `role_rule` VALUES (26, 1, 21);
INSERT INTO `role_rule` VALUES (27, 1, 31);
INSERT INTO `role_rule` VALUES (28, 2, 31);
INSERT INTO `role_rule` VALUES (29, 3, 31);
INSERT INTO `role_rule` VALUES (30, 4, 31);

SET FOREIGN_KEY_CHECKS = 1;
