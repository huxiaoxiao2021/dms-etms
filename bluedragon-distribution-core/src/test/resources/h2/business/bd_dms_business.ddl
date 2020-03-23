/*
Navicat MySQL Data Transfer

Source Server         : 192.168.180.112
Source Server Version : 50726
Source Host           : 192.168.180.112:3306
Source Database       : bd_dms_core

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2020-03-03 16:18:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dms_material_receive
-- ----------------------------
DROP TABLE IF EXISTS `dms_material_receive`;
CREATE TABLE `dms_material_receive` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_code` varchar(30) NOT NULL DEFAULT '' COMMENT '物资编号',
  `material_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '物资类型；1：保温箱',
  `receive_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '收货方式；1：按物资单个收货；2：按容器收货',
  `receive_code` varchar(30) NOT NULL DEFAULT '' COMMENT '收货方式编号',
  `receive_num` int(11) NOT NULL DEFAULT '0' COMMENT '收货数量',
  `create_site_code` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作机构ID',
  `create_site_type` int(11) NOT NULL DEFAULT '0' COMMENT '操作机构类型',
  `create_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人ERP',
  `create_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人名称',
  `update_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人ERP',
  `update_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `yn` tinyint(1) NOT NULL DEFAULT '1' COMMENT '删除标识，1:未删除；0：已删除',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_material` (`create_site_code`,`receive_code`,`material_code`,`material_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物资收货表';
;

-- ----------------------------
-- Table structure for dms_material_receive_flow
-- ----------------------------
DROP TABLE IF EXISTS `dms_material_receive_flow`;
CREATE TABLE `dms_material_receive_flow` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_code` varchar(30) NOT NULL DEFAULT '' COMMENT '物资编号',
  `material_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '物资类型；1：保温箱',
  `receive_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '收货类型；1：按物资单个收货；2：按容器收货',
  `receive_code` varchar(30) NOT NULL DEFAULT '' COMMENT '收货类型编号',
  `receive_num` int(11) NOT NULL DEFAULT '0' COMMENT '收货数量',
  `create_site_code` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作机构ID',
  `create_site_type` int(11) NOT NULL DEFAULT '0' COMMENT '操作机构类型',
  `create_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人ERP',
  `create_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人名称',
  `update_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人ERP',
  `update_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `yn` tinyint(1) NOT NULL DEFAULT '1' COMMENT '删除标识，1:未删除；0：已删除',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物资收货流水表';
;

-- ----------------------------
-- Table structure for dms_material_relation
-- ----------------------------
DROP TABLE IF EXISTS `dms_material_relation`;
CREATE TABLE `dms_material_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_code` varchar(30) NOT NULL DEFAULT '' COMMENT '物资编号',
  `material_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '物资类型；1：保温箱',
  `receive_code` varchar(30) NOT NULL DEFAULT '' COMMENT '收货方式编号',
  `receive_num` int(11) NOT NULL DEFAULT '0' COMMENT '收货数量',
  `create_site_code` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建机构ID',
  `create_site_type` int(11) NOT NULL DEFAULT '0' COMMENT '创建机构类型',
  `update_site_code` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作机构ID',
  `update_site_type` int(11) NOT NULL DEFAULT '0' COMMENT '操作机构类型',
  `create_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人ERP',
  `create_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人名称',
  `update_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人ERP',
  `update_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `yn` tinyint(1) NOT NULL DEFAULT '1' COMMENT '删除标识，1:未删除；0：已删除',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_material_r` (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物资绑定关系表';
;

-- ----------------------------
-- Table structure for dms_material_send
-- ----------------------------
DROP TABLE IF EXISTS `dms_material_send`;
CREATE TABLE `dms_material_send` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_code` varchar(30) NOT NULL DEFAULT '' COMMENT '物资编号',
  `material_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '物资类型；1：保温箱',
  `send_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发货方式；1：按物资单个发货；2：按容器发货',
  `send_code` varchar(30) NOT NULL DEFAULT '' COMMENT '发货编号',
  `send_num` int(11) NOT NULL DEFAULT '0' COMMENT '发货数量',
  `create_site_code` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作机构',
  `create_site_type` int(11) NOT NULL DEFAULT '0' COMMENT '操作机构类型',
  `receive_site_code` bigint(20) NOT NULL DEFAULT '0' COMMENT '收货机构',
  `receive_site_type` int(11) NOT NULL DEFAULT '0' COMMENT '收货机构类型',
  `create_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人ERP',
  `create_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人名称',
  `update_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人ERP',
  `update_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `yn` tinyint(1) NOT NULL DEFAULT '1' COMMENT '删除标识，1:未删除；0：已删除',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_material_s` (`create_site_code`,`send_code`,`material_code`,`material_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物资发货表';
;

-- ----------------------------
-- Table structure for dms_material_send_flow
-- ----------------------------
DROP TABLE IF EXISTS `dms_material_send_flow`;
CREATE TABLE `dms_material_send_flow` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_code` varchar(30) NOT NULL DEFAULT '' COMMENT '物资编号',
  `material_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '物资类型；1：保温箱',
  `send_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发货方式；1：按物资单个发货；2：按容器发货',
  `send_code` varchar(30) NOT NULL DEFAULT '' COMMENT '发货编号',
  `send_num` int(11) NOT NULL DEFAULT '0' COMMENT '发货数量',
  `create_site_code` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作机构',
  `create_site_type` int(11) NOT NULL DEFAULT '0' COMMENT '操作机构类型',
  `receive_site_code` bigint(20) NOT NULL DEFAULT '0' COMMENT '收货机构',
  `receive_site_type` int(11) NOT NULL DEFAULT '0' COMMENT '收货机构类型',
  `create_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人ERP',
  `create_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人名称',
  `update_user_erp` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人ERP',
  `update_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `yn` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识，1:未删除；0：已删除',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物资发货流水表';
;
