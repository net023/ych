/*
Navicat MariaDB Data Transfer

Source Server         : localhost
Source Server Version : 100014
Source Host           : localhost:3306
Source Database       : ych

Target Server Type    : MariaDB
Target Server Version : 100014
File Encoding         : 65001

Date: 2015-05-22 09:26:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mtser
-- ----------------------------
DROP TABLE IF EXISTS `mtser`;
CREATE TABLE `mtser` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '保养服务类名',
  `type` smallint(2) DEFAULT NULL COMMENT '0大保养 1小保养',
  `p_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='保养服务 ：maintenance service ；';

-- ----------------------------
-- Records of mtser
-- ----------------------------
INSERT INTO `mtser` VALUES ('1', '大保养', '1', null);
INSERT INTO `mtser` VALUES ('2', '小保养', '1', null);
INSERT INTO `mtser` VALUES ('3', '换防冻液', '1', null);
INSERT INTO `mtser` VALUES ('4', '换刹车片', '1', null);
INSERT INTO `mtser` VALUES ('5', '换刹车油', '1', null);
INSERT INTO `mtser` VALUES ('6', '换火花塞', '1', null);
INSERT INTO `mtser` VALUES ('7', '前/后刹车片', '1', '4');
INSERT INTO `mtser` VALUES ('8', '前刹车片', '1', '4');
INSERT INTO `mtser` VALUES ('9', '后刹车片', '1', '4');

-- ----------------------------
-- Table structure for mtser_brand_filter
-- ----------------------------
DROP TABLE IF EXISTS `mtser_brand_filter`;
CREATE TABLE `mtser_brand_filter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ms_id` int(11) DEFAULT NULL COMMENT 'mtser 保养服务id',
  `brand_filter` varchar(50) DEFAULT NULL COMMENT '保养品牌过滤',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of mtser_brand_filter
-- ----------------------------
INSERT INTO `mtser_brand_filter` VALUES ('44', '1', 'a');
INSERT INTO `mtser_brand_filter` VALUES ('45', '1', 'b');
INSERT INTO `mtser_brand_filter` VALUES ('46', '1', 'c');
INSERT INTO `mtser_brand_filter` VALUES ('47', '2', 'b');
INSERT INTO `mtser_brand_filter` VALUES ('48', '2', 'c');
INSERT INTO `mtser_brand_filter` VALUES ('49', '2', 'e');
INSERT INTO `mtser_brand_filter` VALUES ('50', '3', 'd');
INSERT INTO `mtser_brand_filter` VALUES ('51', '3', 'c');
INSERT INTO `mtser_brand_filter` VALUES ('52', '3', 'e');
INSERT INTO `mtser_brand_filter` VALUES ('53', '3', 'f');
INSERT INTO `mtser_brand_filter` VALUES ('62', '5', 'd');
INSERT INTO `mtser_brand_filter` VALUES ('63', '5', 'c');
INSERT INTO `mtser_brand_filter` VALUES ('64', '5', 'e');
INSERT INTO `mtser_brand_filter` VALUES ('65', '5', 'f');
INSERT INTO `mtser_brand_filter` VALUES ('66', '6', 'd');
INSERT INTO `mtser_brand_filter` VALUES ('67', '6', 'c');
INSERT INTO `mtser_brand_filter` VALUES ('68', '6', 'e');
INSERT INTO `mtser_brand_filter` VALUES ('69', '6', 'f');

-- ----------------------------
-- Table structure for mtser_unit_cost
-- ----------------------------
DROP TABLE IF EXISTS `mtser_unit_cost`;
CREATE TABLE `mtser_unit_cost` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ms_id` int(11) DEFAULT NULL COMMENT 'mtser 保养服务id',
  `unit_cost` decimal(10,0) DEFAULT NULL COMMENT '保养工时费',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of mtser_unit_cost
-- ----------------------------
INSERT INTO `mtser_unit_cost` VALUES ('1', '1', '20');
INSERT INTO `mtser_unit_cost` VALUES ('2', '2', '21');
INSERT INTO `mtser_unit_cost` VALUES ('3', '3', '22');
INSERT INTO `mtser_unit_cost` VALUES ('4', '4', '25');
INSERT INTO `mtser_unit_cost` VALUES ('5', '5', '26');
INSERT INTO `mtser_unit_cost` VALUES ('10', '6', '30');
INSERT INTO `mtser_unit_cost` VALUES ('11', '7', '40');
INSERT INTO `mtser_unit_cost` VALUES ('12', '8', '50');
INSERT INTO `mtser_unit_cost` VALUES ('13', '9', '60');
