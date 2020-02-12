/*
 Navicat Premium Data Transfer

 Source Server         : my腾讯云
 Source Server Type    : MySQL
 Source Server Version : 50723
 Source Host           : 193.112.74.20:3306
 Source Schema         : ai_openbase

 Target Server Type    : MySQL
 Target Server Version : 50723
 File Encoding         : 65001

 Date: 08/10/2018 10:35:56
*/
use ai_openbase;
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

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '题目id',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '题目内容',
  `a` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'a选项',
  `b` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'b选项',
  `c` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'c选项',
  `d` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'd选项',
  `answer` int(1) NOT NULL COMMENT '答案',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, '在下列陈述中对人工智能描述不准确的是()', '人工智能是研究、开发用于模拟、延伸和扩展人的智能的理论、方法、技术及应用系统的一门新的技术科学。', '人工智能是研究使计算机来模拟人的某些思维过程和智能行为（如学习、推理、思考、规划等）的学科。', '人工智能能根据大量的历史资料和实时时察找出对于未来预测性的洞察。', '人工智能是通过射频识别、红外感应器、全球定位系统、激光扫描器等信息传感设备，按约定的协议，把任何物品与互联网相连接，以实现智能化识别、定位、监控和管理的一种网络概念。', 4);
INSERT INTO `question` VALUES (2, '下列领域中不属于人工智能领域的是()', '模式识别', '知识图谱', '神经网络', '自然识别', 4);
INSERT INTO `question` VALUES (3, '()是一种模拟人类专家解决领域问题的计算机程序系统', '专家系统', '进化算法', '遗传算法', '禁忌搜索', 1);
INSERT INTO `question` VALUES (4, '利用已知的某些有关具体问题领域的信息进行搜索的方法称为()', '逐个搜索', '随机搜索', '启发式搜素', '遍历搜索', 3);
INSERT INTO `question` VALUES (5, '目前的知识表示形式有（ ）、语义网和框架法等。', '机器学习', '产生式规则', '数据结构', '离散法', 1);
INSERT INTO `question` VALUES (6, '人工智能的含义最早由一位科学家于1950年提出，并且同时提出一个机器智能的测试模型，请问这个科学家是()', '明斯基', '扎德', '图灵', '冯 诺依曼', 3);
INSERT INTO `question` VALUES (7, '如果问题存在最优解，则下面几种搜索算法中，（）必然可以得到最优解。', '广度优先算法', '深度优先算法', '有界深度优先算法', '启发式搜索', 1);
INSERT INTO `question` VALUES (8, '或图通常称为()', '博弈图', '状态图', '语义图', '框架网络', 2);
INSERT INTO `question` VALUES (9, '一般来讲，下列语言属于人工智能语言的是()', 'VB', 'C#', 'Logo', 'Prolog', 4);
INSERT INTO `question` VALUES (10, '问题归约法的组成部分包括一个初始问题描述()、一套本原问题描述。', '中间状态描述', '一套把问题变换成子问题的操作符', '目标状态描述', '问题变量描述', 2);
INSERT INTO `question` VALUES (11, '非结构化的知识的表示法是', '语义网络表述', '框架表示', '谓词逻辑表示', '面向对象表示', 3);
INSERT INTO `question` VALUES (12, '自然语言理解是人工智能的重要应用领域，下面列举中的()不是它要实现的目标', '理解别人讲的话', '对自然语言表示的信息进行分析概况或编辑', '欣赏音乐', '机器翻译', 3);
INSERT INTO `question` VALUES (13, '如果把知识按照表达内容来分，下述()不在分类的范围中。', '元知识', '显性知识', '过程性知识', '事实性知识', 3);
INSERT INTO `question` VALUES (14, '下列哪种情况是图灵测试的内容？', '当机器与人对话，两者相互询问，人分不清机器还是人的时候，说明它通过了图灵测试。', '当机器骗过测试者，使得询问者分不清机器还是人的时候，说明他通过了图灵测试。', '当人与人对话，其中一人的智力超过另一人时，说明智机器者通过了图灵测试。', '两机器对话时，其中一机的智力超过另一机时，说明智者机器通过了图灵测试。', 2);
INSERT INTO `question` VALUES (15, '专家系统是以()为基础，以推理为核心的系统。', '专家', '软件', '知识', '解决问题', 3);
INSERT INTO `question` VALUES (16, '机器翻译属于下列哪个领域的应用()', '自然语言系统', '机器学习', '专家吸引', '人类感官模拟', 1);
INSERT INTO `question` VALUES (17, '自动识别系统属于人工智能哪个应用领域？', '机器学习', '人类感官模拟', '专家系统', '模式识别', 2);
INSERT INTO `question` VALUES (18, '知识图谱生命周期不包括以下哪个()', '知识存储', '知识抽取', '本体定义', '射频识别', 4);
INSERT INTO `question` VALUES (19, '实体关系识别技术中不包括以下哪个()', '监督学习', '半监督学习', '无监督学习', '本体抽取', 4);
INSERT INTO `question` VALUES (20, '实体链接技术不包括以下哪个()', '基于概率生产模型方法', '基于符号逻辑的推理方法', '基于主题模型方法', '基于图的方法', 2);

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
