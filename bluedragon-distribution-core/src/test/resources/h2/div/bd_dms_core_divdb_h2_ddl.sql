/*
Navicat MySQL Data Transfer

Source Server         : 192.168.183.84（直连库）
Source Server Version : 50626
Source Host           : 192.168.183.84:3358
Source Database       : bd_dms_core0

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2020-02-26 15:56:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for box
-- ----------------------------
DROP TABLE IF EXISTS `box`;
CREATE TABLE `box` (
  `BOX_ID` bigint(20) unsigned NOT NULL,
  `BOX_CODE` varchar(50) DEFAULT NULL,
  `BOX_TYPE` varchar(2) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(100) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `RECEIVE_SITE_NAME` varchar(100) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `TIMES` bigint(20) unsigned DEFAULT NULL,
  `BOX_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `TRANSPORT_TYPE` bigint(20) DEFAULT NULL,
  `MIX_BOX_TYPE` bigint(20) DEFAULT NULL COMMENT '混包类型0不混1可混',
  `LENGTH` bigint(20) DEFAULT NULL COMMENT '箱号长度',
  `WIDTH` bigint(20) DEFAULT NULL COMMENT '箱号宽度',
  `HEIGHT` bigint(20) DEFAULT NULL COMMENT '箱号高度',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  `PREDICT_SEND_TIME` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ROUTER` varchar(200) DEFAULT NULL,
  `ROUTER_NAME` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`BOX_ID`),
  KEY `IND_BOX_BC` (`BOX_CODE`),
  KEY `IND_BOX_CRSITE_P` (`CREATE_SITE_CODE`,`RECEIVE_SITE_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for business_code
-- ----------------------------
DROP TABLE IF EXISTS `business_code`;
CREATE TABLE `business_code` (
	`CODE` VARCHAR(50) NOT NULL COMMENT '编号',
	`NODE_TYPE` VARCHAR(10) NOT NULL COMMENT '业务节点类型',
	`FROM_SOURCE` VARCHAR(30) NOT NULL COMMENT '创建来源',
	`CREATE_USER` VARCHAR(50) NOT NULL COMMENT '创建人',
	`CREATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`UPDATE_USER` VARCHAR(50) NOT NULL COMMENT '更新人',
	`UPDATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`IS_DELETE` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '数据删除标志',
	`TS` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
	PRIMARY KEY (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务编码主表';

-- ----------------------------
-- Table structure for business_code_attribute
-- ----------------------------
DROP TABLE IF EXISTS `business_code_attribute`;
CREATE TABLE `business_code_attribute` (
	`ID` BIGINT(20) UNSIGNED NOT NULL COMMENT '主键',
	`CODE` VARCHAR(50) NOT NULL COMMENT '业务编码',
	`ATTRIBUTE_KEY` VARCHAR(30) NOT NULL COMMENT '属性键值KEY',
	`ATTRIBUTE_VALUE` VARCHAR(30) NOT NULL COMMENT '属性键值VALUE',
	`FROM_SOURCE` VARCHAR(30) NOT NULL COMMENT '属性来源',
	`CREATE_USER` VARCHAR(50) NULL DEFAULT NULL COMMENT '属性创建人',
	`CREATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '属性创建时间',
	`UPDATE_USER` VARCHAR(50) NULL DEFAULT NULL,
	`UPDATE_TIME` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '属性更新时间',
	`IS_DELETE` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '属性删除标志',
	`TS` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
	PRIMARY KEY (`ID`),
	UNIQUE INDEX `UNQ_CODE_ATTR` (`CODE`, `ATTRIBUTE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务编码属性附表';

-- ----------------------------
-- Table structure for cen_confirm
-- ----------------------------
DROP TABLE IF EXISTS `cen_confirm`;
CREATE TABLE `cen_confirm` (
  `CONFIRM_ID` bigint(20) unsigned NOT NULL COMMENT '收货确认表ID',
  `SEND_CODE` varchar(30) DEFAULT NULL COMMENT '发货交接单号',
  `WAYBILL_CODE` varchar(30) DEFAULT NULL COMMENT '运单号',
  `BOX_CODE` varchar(30) DEFAULT NULL COMMENT '箱号',
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL COMMENT '包裹号',
  `CONFIRM_TYPE` bigint(20) DEFAULT NULL COMMENT '收货类型(10正向 20逆向 30第三方)',
  `RECEIVE_USER` varchar(32) DEFAULT NULL COMMENT '创建人（收货人）',
  `RECEIVE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人ID（收货人ID）',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间（收货时间）',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位（收货单位）ID',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `YN` bigint(20) DEFAULT '1' COMMENT 'DF',
  `CONFIRM_STATUS` bigint(20) DEFAULT '0' COMMENT '0 待处理  1 处理完成',
  `THIRD_WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT 'POP第三方收货运单号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '收货单位编号',
  `INSPECTION_USER` varchar(32) DEFAULT NULL COMMENT '操作人',
  `INSPECTION_USER_CODE` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `INSPECTION_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `PICKUP_CODE` varchar(32) DEFAULT NULL COMMENT '取件单号',
  `OPERATE_TYPE` bigint(20) DEFAULT NULL COMMENT '操作类型(1.跨分拣中心收货2.库房交接3.pop交接4.三方验货5.返调度再投6.取件7.站点退货8.三方退货)',
  `EXCUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `EXCUTE_TIME` datetime DEFAULT NULL COMMENT '执行时间',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `RECEIVE_TIME` datetime DEFAULT NULL COMMENT '收货时间',
  `OPERATE_USER` varchar(32) DEFAULT NULL COMMENT '操作人',
  `OPERATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`CONFIRM_ID`),
  KEY `IND_CEN_CONFIRM_CSTAT_D_P` (`CONFIRM_STATUS`),
  KEY `IND_CEN_CONFIRM_PCODE_P` (`PACKAGE_BARCODE`),
  KEY `IND_CEN_CONFIRM_SITE_CT` (`CREATE_SITE_CODE`,`CREATE_TIME`),
  KEY `IND_CEN_CONFIRM_WCODE_P` (`WAYBILL_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收货确认表';

-- ----------------------------
-- Table structure for dms_out_weight_volume
-- ----------------------------
DROP TABLE IF EXISTS `dms_out_weight_volume`;
CREATE TABLE `dms_out_weight_volume` (
  `ID` bigint(20) unsigned NOT NULL COMMENT 'ID',
  `BARCODE` varchar(30) DEFAULT NULL COMMENT '包裹号/箱号/板号',
  `BARCODE_TYPE` tinyint(1) DEFAULT NULL COMMENT '包裹号/箱号/板号',
  `WEIGHT` double DEFAULT '0' COMMENT '重量,单位kg',
  `VOLUME` double DEFAULT '0' COMMENT '体积,单位cm³',
  `LENGTH` double DEFAULT '0' COMMENT '长,单位cm',
  `WIDTH` double DEFAULT '0' COMMENT '宽,单位cm',
  `HEIGHT` double DEFAULT '0' COMMENT '高,单位cm',
  `OPERATE_TYPE` tinyint(1) DEFAULT NULL COMMENT '操作类型,1静态称重量方 2动态称重量方',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '操作站点',
  `WEIGHT_USER_CODE` bigint(20) DEFAULT NULL COMMENT '称重人编码',
  `WEIGHT_USER_NAME` varchar(50) DEFAULT NULL COMMENT '称重人姓名',
  `WEIGHT_TIME` datetime DEFAULT NULL COMMENT '称重时间',
  `MEASURE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '量方人编码',
  `MEASURE_USER_NAME` varchar(50) DEFAULT NULL COMMENT '量方人姓名',
  `MEASURE_TIME` datetime DEFAULT NULL COMMENT '量方时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`ID`),
  KEY `IND_BARCODE` (`CREATE_SITE_CODE`,`BARCODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分拣中心出仓重量体积表';

-- ----------------------------
-- Table structure for dynamic_rule
-- ----------------------------
DROP TABLE IF EXISTS `dynamic_rule`;
CREATE TABLE `dynamic_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sharding_key` varchar(50) DEFAULT NULL COMMENT '分片的key',
  `sharding_value` varchar(50) DEFAULT NULL COMMENT '分片的value',
  `tb_index` int(11) DEFAULT NULL COMMENT '表的索引',
  `group_index` int(11) DEFAULT NULL COMMENT '组的索引',
  `transfer_group_index` int(11) DEFAULT NULL COMMENT '传输组的索引',
  `transfer_tb_index` int(11) DEFAULT NULL COMMENT '传输表的索引',
  `table_name` varchar(200) DEFAULT NULL COMMENT '逻辑表名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1296 DEFAULT CHARSET=utf8 COMMENT='动态规则';

-- ----------------------------
-- Table structure for dynamic_rule_copy
-- ----------------------------
DROP TABLE IF EXISTS `dynamic_rule_copy`;
CREATE TABLE `dynamic_rule_copy` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sharding_key` varchar(50) DEFAULT NULL COMMENT '分片的key',
  `sharding_value` varchar(50) DEFAULT NULL COMMENT '分片的value',
  `tb_index` int(11) DEFAULT NULL COMMENT '表的索引',
  `group_index` int(11) DEFAULT NULL COMMENT '组的索引',
  `transfer_group_index` int(11) DEFAULT NULL COMMENT '传输组的索引',
  `transfer_tb_index` int(11) DEFAULT NULL COMMENT '传输表的索引',
  `table_name` varchar(200) DEFAULT NULL COMMENT '逻辑表名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1296 DEFAULT CHARSET=utf8 COMMENT='动态规则';

-- ----------------------------
-- Table structure for inspection
-- ----------------------------
DROP TABLE IF EXISTS `inspection`;
CREATE TABLE `inspection` (
  `INSPECTION_ID` bigint(20) unsigned NOT NULL COMMENT '验货表ID',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '运单号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL COMMENT '包裹号',
  `INSPECTION_STATUS` bigint(20) DEFAULT NULL COMMENT '0 待处理  1 处理完成 2 处理中  3 异常',
  `EXCEPTION_TYPE` varchar(50) DEFAULT NULL COMMENT '异常类型',
  `INSPECTION_TYPE` bigint(20) DEFAULT NULL COMMENT '验货类型',
  `CREATE_USER` varchar(32) DEFAULT NULL COMMENT '操作人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '操作单位ID',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '收货单位编号',
  `UPDATE_USER` varchar(32) DEFAULT NULL COMMENT '最后修改人name',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '最后修改人code',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '最后修改时间',
  `YN` bigint(20) DEFAULT '1' COMMENT 'DF',
  `THIRD_WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT 'POP第三方运单号',
  `POP_FLAG` bigint(20) DEFAULT NULL COMMENT '操作标识，0或空代表系统处理，1代表人工处理',
  `POP_SUP_ID` bigint(20) DEFAULT NULL COMMENT 'POP商家ID',
  `POP_SUP_NAME` varchar(128) DEFAULT NULL COMMENT 'POP商家名称',
  `QUANTITY` bigint(20) DEFAULT NULL COMMENT '包裹数量',
  `CROSS_CODE` varchar(16) DEFAULT NULL COMMENT '滑道号',
  `WAYBILL_TYPE` bigint(20) DEFAULT NULL COMMENT '运单类型',
  `OPERATE_TYPE` bigint(20) DEFAULT NULL COMMENT '操作类型',
  `POP_RECEIVE_TYPE` bigint(20) DEFAULT NULL COMMENT 'POP收货类型:1,商家直送；2,托寄送货',
  `QUEUE_NO` varchar(32) DEFAULT NULL COMMENT 'POP排队号',
  `DRIVER_CODE` varchar(32) DEFAULT NULL COMMENT '司机编号',
  `DRIVER_NAME` varchar(32) DEFAULT NULL COMMENT '司机名称',
  `BUSI_ID` bigint(20) DEFAULT NULL COMMENT 'B商家ID',
  `BUSI_NAME` varchar(128) DEFAULT NULL COMMENT 'B商家名称',
  `LENGTH` bigint(20) DEFAULT NULL COMMENT '长',
  `WIDTH` bigint(20) DEFAULT NULL COMMENT '宽',
  `HIGH` bigint(20) DEFAULT NULL COMMENT '高',
  `VOLUMN` bigint(20) DEFAULT NULL COMMENT '体积',
  `OPERATE_TIME` datetime DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`INSPECTION_ID`),
  KEY `IND_INSPECTION_BCODE_PL` (`BOX_CODE`),
  KEY `IND_INSPECTION_CS_CTIME` (`CREATE_SITE_CODE`,`CREATE_TIME`),
  KEY `IND_INSPECTION_PCODE_D` (`PACKAGE_BARCODE`),
  KEY `IND_INSPECTION_STATUS_P` (`INSPECTION_STATUS`),
  KEY `IND_INSPECTION_WCODE_D` (`WAYBILL_CODE`),
  KEY `idx_waybill_code_package_barcode` (`WAYBILL_CODE`,`PACKAGE_BARCODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for kv_index
-- ----------------------------
DROP TABLE IF EXISTS `kv_index`;
CREATE TABLE `kv_index` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `keyword` varchar(255) NOT NULL COMMENT 'key运单、包裹或者箱号',
  `value` varchar(255) NOT NULL COMMENT '始发分拣中心ID',
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY (`id`),
  KEY `idx_key` (`keyword`)
) ENGINE=InnoDB AUTO_INCREMENT=1044007 DEFAULT CHARSET=utf8 COMMENT='分库索引表';

-- ----------------------------
-- Table structure for send_d
-- ----------------------------
DROP TABLE IF EXISTS `send_d`;
CREATE TABLE `send_d` (
  `SEND_D_ID` bigint(20) unsigned NOT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `BOX_CODE` varchar(30) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL,
  `WAYBILL_CODE` varchar(30) DEFAULT NULL,
  `SEND_TYPE` bigint(20) DEFAULT NULL,
  `SENDD_STATUS` bigint(20) DEFAULT '0',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `WEIGHT` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT '1',
  `IS_CANCEL` bigint(20) DEFAULT '0',
  `PICKUP_CODE` varchar(30) DEFAULT NULL,
  `EXCUTE_COUNT` bigint(20) DEFAULT NULL,
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `TOFA_STATUS` bigint(20) DEFAULT NULL,
  `PACKAGE_NUM` bigint(20) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `SPARE_REASON` varchar(128) DEFAULT NULL,
  `SPARE_TRAN_CODE` varchar(32) DEFAULT NULL,
  `IS_LOSS` bigint(20) DEFAULT NULL,
  `FEATURE_TYPE` bigint(20) DEFAULT NULL,
  `WH_REVERSE` bigint(20) DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  `biz_source` tinyint(3) unsigned DEFAULT NULL COMMENT '发货业务来源',
  PRIMARY KEY (`SEND_D_ID`),
  KEY `IND_SEND_D_BCPB` (`BOX_CODE`,`PACKAGE_BARCODE`),
  KEY `IND_SEND_D_PB_P` (`PACKAGE_BARCODE`),
  KEY `IND_SEND_D_SCODE_P` (`SEND_CODE`),
  KEY `IND_SEND_D_SITE_OT_PL` (`CREATE_SITE_CODE`,`RECEIVE_SITE_CODE`,`OPERATE_TIME`),
  KEY `IND_SEND_D_STAT_CT` (`SENDD_STATUS`,`CREATE_TIME`),
  KEY `IND_SEND_D_WCODE_P` (`WAYBILL_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for send_m
-- ----------------------------
DROP TABLE IF EXISTS `send_m`;
CREATE TABLE `send_m` (
  `SEND_M_ID` bigint(20) unsigned NOT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `BOX_CODE` varchar(50) DEFAULT NULL,
  `SEND_USER` varchar(50) DEFAULT NULL,
  `SEND_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `SEND_TYPE` bigint(20) DEFAULT NULL,
  `CAR_CODE` varchar(50) DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATER_USER` varchar(50) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `SHIELDS_CAR_ID` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT '1',
  `SENDM_STATUS` bigint(20) DEFAULT '0',
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `EXCUTE_COUNT` bigint(20) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `TURNOVERBOX_CODE` varchar(30) DEFAULT NULL,
  `TRANSPORT_TYPE` bigint(20) DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  `board_code` varchar(30) DEFAULT NULL COMMENT '组板板号',
  `biz_source` tinyint(3) unsigned DEFAULT NULL COMMENT '发货业务来源',
  PRIMARY KEY (`SEND_M_ID`),
  KEY `IND_SEND_M_SC` (`SEND_CODE`),
  KEY `IND_SEND_M_BCODE_BCODE_P` (`BOX_CODE`),
  KEY `IND_SEND_M_BCODE_CSITE_CODE` (`CREATE_SITE_CODE`,`RECEIVE_SITE_CODE`,`BOX_CODE`),
  KEY `IND_SEND_M_BCODE_CSITE_OT` (`CREATE_SITE_CODE`,`RECEIVE_SITE_CODE`,`OPERATE_TIME`),
  KEY `IND_SEND_M_SCAR_ID` (`SHIELDS_CAR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sorting
-- ----------------------------
DROP TABLE IF EXISTS `sorting`;
CREATE TABLE `sorting` (
  `SORTING_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `PACKAGE_CODE` varchar(32) DEFAULT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `SORTING_TYPE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(16) DEFAULT NULL,
  `CREATE_TIME` datetime NOT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `SORTING_STATUS` bigint(20) DEFAULT NULL,
  `IS_CANCEL` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `PICKUP_CODE` varchar(32) DEFAULT NULL,
  `EXCUTE_COUNT` bigint(20) DEFAULT NULL,
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `SPARE_REASON` varchar(128) DEFAULT NULL,
  `IS_LOSS` bigint(20) DEFAULT NULL,
  `FEATURE_TYPE` bigint(20) DEFAULT NULL,
  `WH_REVERSE` bigint(20) DEFAULT NULL,
  `BSEND_CODE` varchar(50) DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SORTING_ID`),
  KEY `IND_SORTING_BCODE_P` (`BOX_CODE`),
  KEY `IND_SORTING_BSCODE_P` (`BSEND_CODE`),
  KEY `IND_SORTING_PCODE_P` (`PACKAGE_CODE`),
  KEY `IND_SORTING_WCODE_P` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=1197705912833130497 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=1;
