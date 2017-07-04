-- MySQL dump 10.13  Distrib 5.6.26, for Linux (x86_64)
--
-- Host: localhost    Database: bd_dms_core
-- ------------------------------------------------------
-- Server version	5.6.26-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `abnormal_order`
--

DROP TABLE IF EXISTS `abnormal_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `abnormal_order` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `ORDER_ID` varchar(20) DEFAULT NULL,
  `ABNORMAL_CODE1` bigint(20) DEFAULT NULL,
  `ABNORMAL_REASON1` varchar(128) DEFAULT NULL,
  `ABNORMAL_CODE2` bigint(20) DEFAULT NULL,
  `ABNORMAL_REASON2` varchar(128) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER_ERP` varchar(32) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(128) DEFAULT NULL,
  `IS_CANCEL` bigint(20) DEFAULT NULL,
  `MEMO` varchar(256) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SYSTEM_ID`),
  UNIQUE KEY `PK_ABNORMAL_ORDER` (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=200000471 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alert_config`
--

DROP TABLE IF EXISTS `alert_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alert_config` (
  `ALERT_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ENABLED` bigint(20) DEFAULT NULL COMMENT '是否开启',
  `THRESHOLD` bigint(20) DEFAULT NULL COMMENT '阀值',
  `EXECUTE_SQL` varchar(1000) DEFAULT NULL COMMENT '查询SQL',
  `PHONE` varchar(200) DEFAULT NULL COMMENT '短信手机列表',
  `PHONE_FORMAT` varchar(140) DEFAULT NULL COMMENT '短信格式',
  `EMAIL` varchar(400) DEFAULT NULL COMMENT '邮件地址列表',
  `EMAIL_FORMAT` varchar(400) DEFAULT NULL COMMENT '邮件格式',
  `YN` bigint(20) DEFAULT NULL COMMENT '删除标志',
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(128) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(128) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `ALERT_NAME` varchar(100) DEFAULT NULL COMMENT '预警名称',
  `MEMO` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ALERT_ID`),
  UNIQUE KEY `PK_ALERT_ID` (`ALERT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `batch_send`
--

DROP TABLE IF EXISTS `batch_send`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `batch_send` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `BATCH_CODE` varchar(50) DEFAULT NULL COMMENT '波次号',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '始发地编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '目的地编号',
  `SEND_CODE` varchar(50) DEFAULT NULL COMMENT '发货批次号',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '操作人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '操作人编号',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '更新操作人',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '更新操作人编号',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `SEND_STATUS` bigint(20) DEFAULT NULL COMMENT '发货状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  `SEND_CAR_STATE` bigint(20) DEFAULT NULL COMMENT '批次发车状态【0:初始状态；1：已发车；2：已取消发车】',
  `SEND_CAR_OPERATE_TIME` datetime DEFAULT NULL COMMENT '发车及取消发车操作时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PK_BATCH_SEND` (`ID`),
  UNIQUE KEY `IDX_BATCHSEND_BCRSC` (`BATCH_CODE`,`RECEIVE_SITE_CODE`),
  KEY `IND_BATCH_SEND_BCODE` (`BATCH_CODE`),
  KEY `IND_BATCH_SEND_SCODE` (`SEND_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=1958882 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `batchinfo`
--

DROP TABLE IF EXISTS `batchinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `batchinfo` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `BATCH_CODE` varchar(50) DEFAULT NULL COMMENT '波次号',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '始发地编号',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '操作人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '操作人编号',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '更新操作人',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '更新操作人编号',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PK_BATCHINFO` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1548 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_config`
--

DROP TABLE IF EXISTS `client_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_config` (
  `CONFIG_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SITE_CODE` varchar(20) DEFAULT NULL COMMENT '分拣中心编号',
  `PROGRAM_TYPE` bigint(20) DEFAULT NULL COMMENT '应用程序类型（0-PDA,1-Winform）',
  `VERSION_CODE` varchar(20) DEFAULT NULL COMMENT '版本号',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`CONFIG_ID`),
  UNIQUE KEY `PK_CLIENT_CONFIG` (`CONFIG_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=272 DEFAULT CHARSET=utf8 COMMENT='客户端版本配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_config_history`
--

DROP TABLE IF EXISTS `client_config_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_config_history` (
  `CONFIG_HISTORY_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SITE_CODE` varchar(20) DEFAULT NULL COMMENT '分拣中心编号',
  `PROGRAM_TYPE` bigint(20) DEFAULT NULL COMMENT '应用程序类型（0-PDA,1-Winform）',
  `VERSION_CODE` varchar(20) DEFAULT NULL COMMENT '版本号',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`CONFIG_HISTORY_ID`),
  UNIQUE KEY `PK_CLIENT_CONFIG_HISTORY` (`CONFIG_HISTORY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1422 DEFAULT CHARSET=utf8 COMMENT='客户端版本配置变更历史';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_version`
--

DROP TABLE IF EXISTS `client_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_version` (
  `VERSION_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `VERSION_CODE` varchar(20) DEFAULT NULL COMMENT '版本号',
  `VERSION_TYPE` bigint(20) DEFAULT NULL COMMENT '版本类型（0-PDA,1-Winform）',
  `DOWNLOAD_URL` varchar(256) DEFAULT NULL COMMENT '下载地址',
  `MEMO` varchar(256) DEFAULT NULL COMMENT '版本说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`VERSION_ID`),
  UNIQUE KEY `PK_CLIENT_VERSION` (`VERSION_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='客户端的版本（PDA、Wwinform）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cross_box`
--

DROP TABLE IF EXISTS `cross_box`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cross_box` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ORIGINAL_DMS_ID` int(11) DEFAULT NULL COMMENT '始发分拣中心编号',
  `ORIGINAL_DMS_NAME` varchar(64) DEFAULT NULL COMMENT '始发分拣中心名称',
  `DESTINATION_DMS_ID` int(11) DEFAULT NULL COMMENT '目的分拣中心编号',
  `DESTINATION_DMS_NAME` varchar(64) DEFAULT NULL COMMENT '目的分拣中心名称',
  `TRANSFER_ONE_ID` int(11) DEFAULT NULL COMMENT '中转1编号',
  `TRANSFER_ONE_NAME` varchar(64) DEFAULT NULL COMMENT '中转1名称',
  `TRANSFER_TWO_ID` int(11) DEFAULT NULL COMMENT '中转2编号',
  `TRANSFER_TWO_NAME` varchar(64) DEFAULT NULL COMMENT '中转2名称',
  `TRANSFER_THREE_ID` int(11) DEFAULT NULL COMMENT '中转3编号',
  `TRANSFER_THREE_NAME` varchar(64) DEFAULT NULL COMMENT '中转3名称',
  `FULL_LINE` varchar(160) DEFAULT NULL COMMENT '完整路线',
  `CREATE_OPERATOR_NAME` varchar(32) DEFAULT NULL COMMENT '创建人名称',
  `UPDATE_OPERATOR_NAME` varchar(32) DEFAULT NULL COMMENT '修改人名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `REMARK` varchar(200) DEFAULT NULL COMMENT '备注',
  `EXPIRY_DATE` datetime DEFAULT NULL COMMENT '失效时间',
  `EFFECTIVE_DATE` datetime DEFAULT NULL COMMENT '生效时间',
  `ORIGIN_ID` int(11) DEFAULT NULL COMMENT '源序号',
  `YN` tinyint(4) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5990 DEFAULT CHARSET=utf8 COMMENT='跨中转箱号';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cross_sorting`
--

DROP TABLE IF EXISTS `cross_sorting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cross_sorting` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ORG_ID` bigint(20) DEFAULT NULL COMMENT '机构ID',
  `CREATE_DMS_CODE` bigint(20) DEFAULT NULL COMMENT '建包/发货分拣中心编码',
  `CREATE_DMS_NAME` varchar(50) DEFAULT NULL COMMENT '建包/发货分拣中心',
  `DESTINATION_DMS_CODE` bigint(20) DEFAULT NULL COMMENT '目的分拣中心编码',
  `DESTINATION_DMS_NAME` varchar(50) DEFAULT NULL COMMENT '目的分拣中心',
  `MIX_DMS_CODE` bigint(20) DEFAULT NULL COMMENT '可混装分拣中心ID',
  `MIX_DMS_NAME` varchar(50) DEFAULT NULL COMMENT '可混装分拣中心',
  `TYPE` bigint(20) DEFAULT NULL COMMENT '规则类型,10代表建包,20代表发货',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人编码',
  `CREATE_USER_NAME` varchar(50) DEFAULT NULL COMMENT '创建人',
  `DELETE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '删除人编码',
  `DELETE_USER_NAME` varchar(50) DEFAULT NULL COMMENT '删除人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `DELETE_TIME` datetime DEFAULT NULL COMMENT '删除时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '有效标识',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SYS_C0011875` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='跨区分拣校验规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `db_schedule`
--

DROP TABLE IF EXISTS `db_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `db_schedule` (
  `TABLE_NAME` varchar(50) NOT NULL,
  `MAX_ID` bigint(20) NOT NULL,
  `SCHEDULE_STATUS` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dbs_objectid`
--

DROP TABLE IF EXISTS `dbs_objectid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbs_objectid` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '箱号ID生成器',
  `OBJECTNAME` varchar(255) DEFAULT NULL COMMENT '序列标识',
  `FIRSTID` bigint(20) DEFAULT NULL COMMENT 'ID计算参考值',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PK_DBS_OBJECTID_ID` (`ID`),
  UNIQUE KEY `idx_object_name` (`OBJECTNAME`)
) ENGINE=InnoDB AUTO_INCREMENT=139134 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `departure_car`
--

DROP TABLE IF EXISTS `departure_car`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `departure_car` (
  `DEPARTURE_CAR_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `CAR_CODE` varchar(30) DEFAULT NULL COMMENT '车号',
  `SHIELDS_CAR_CODE` varchar(30) DEFAULT NULL COMMENT '封车号',
  `SEND_USER_CODE` bigint(20) DEFAULT NULL COMMENT '司机编码',
  `SEND_USER` varchar(50) DEFAULT NULL COMMENT '司机姓名',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位编码',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '创建人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人编码',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `WEIGHT` bigint(20) DEFAULT NULL COMMENT '重量',
  `VOLUME` bigint(20) DEFAULT NULL COMMENT '体积',
  `YN` bigint(20) DEFAULT '1' COMMENT '是否删除 ''0'' 删除 ''1'' 使用',
  `SEND_USER_TYPE` bigint(20) DEFAULT NULL COMMENT '承运商类型',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `OLD_CAR_CODE` varchar(30) DEFAULT NULL COMMENT '转车车号',
  `DEPART_TYPE` bigint(20) DEFAULT '1' COMMENT '发车类型 1发车 2转车',
  `RUN_NUMBER` bigint(20) DEFAULT NULL COMMENT '发车班次号',
  `RECEIVE_SITE_CODES` varchar(512) DEFAULT NULL COMMENT '目的站点编号，多个使用逗号进行分割',
  `PRINT_TIME` datetime DEFAULT NULL COMMENT '打印时间',
  `CAPACITY_CODE` varchar(50) DEFAULT NULL COMMENT '运力编码',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`DEPARTURE_CAR_ID`),
  UNIQUE KEY `PK_DEPARTURE_CAR` (`DEPARTURE_CAR_ID`),
  KEY `IND_DEPARTURE_CAR_CCODE` (`SHIELDS_CAR_CODE`),
  KEY `IND_DEPARTURE_CAR_FPRINT` (`FINGERPRINT`)
) ENGINE=InnoDB AUTO_INCREMENT=200002031 DEFAULT CHARSET=utf8 COMMENT='发车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `departure_log_bk`
--

DROP TABLE IF EXISTS `departure_log_bk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `departure_log_bk` (
  `DEPARTURE_LOG_ID` bigint(20) NOT NULL,
  `DISTRIBUTE_CODE` bigint(20) DEFAULT NULL,
  `DISTRIBUTE_NAME` varchar(50) DEFAULT NULL,
  `OPERATOR_CODE` bigint(20) DEFAULT NULL,
  `OPERATOR_NAME` varchar(50) DEFAULT NULL,
  `DEPARTURE_TIME` datetime DEFAULT NULL,
  `RECEIVE_TIME` datetime DEFAULT NULL,
  `DEPARTURE_CAR_ID` bigint(20) DEFAULT NULL,
  `CAPACITY_CODE` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`DEPARTURE_LOG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `departure_send`
--

DROP TABLE IF EXISTS `departure_send`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `departure_send` (
  `DEPARTURE_SEND_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `DEPARTURE_CAR_ID` bigint(20) DEFAULT NULL COMMENT '转发车批次ID',
  `SEND_CODE` varchar(50) DEFAULT NULL COMMENT '批次号',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '创建人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人编码',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT '1' COMMENT 'YN',
  `THIRD_WAYBILL_CODE` varchar(50) DEFAULT NULL,
  `CAPACITY_CODE` varchar(50) DEFAULT NULL COMMENT '运力编码',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`DEPARTURE_SEND_ID`),
  UNIQUE KEY `PK_DEPARTURE_SEND` (`DEPARTURE_SEND_ID`),
  KEY `IND_DEPARTURE_SEND_SCODE` (`SEND_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200002005 DEFAULT CHARSET=utf8 COMMENT='发转车批次对应关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `departure_tmp`
--

DROP TABLE IF EXISTS `departure_tmp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `departure_tmp` (
  `DEPARTURE_TMP_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `BATCH_CODE` varchar(50) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `THIRD_WAYBILL_CODE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`DEPARTURE_TMP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fbarcode`
--

DROP TABLE IF EXISTS `fbarcode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fbarcode` (
  `FBARCODE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `FBARCODE_CODE` varchar(16) DEFAULT NULL COMMENT 'F条码号',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点编号',
  `CREATE_SITE_NAME` varchar(100) DEFAULT NULL COMMENT '创建站点名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人编号',
  `CREATE_USER` varchar(16) DEFAULT NULL COMMENT '创建人',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '修改人编号',
  `UPDATE_USER` varchar(16) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `TIMES` bigint(20) DEFAULT NULL COMMENT '打印次数',
  `FBARCODE_STATUS` bigint(20) DEFAULT NULL COMMENT '状态 ''0'' 新增  ''1’打印完毕',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除 ''0'' 删除 ''1'' 使用',
  PRIMARY KEY (`FBARCODE_ID`),
  UNIQUE KEY `PK_FBARCODE` (`FBARCODE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=30427 DEFAULT CHARSET=utf8 COMMENT='F条码信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fresh_waybill`
--

DROP TABLE IF EXISTS `fresh_waybill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fresh_waybill` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `PACKAGE_CODE` varchar(20) NOT NULL COMMENT '包裹号',
  `BOX_TYPE` bigint(20) DEFAULT NULL COMMENT '保温箱类型',
  `SLAB_TYPE` bigint(20) DEFAULT NULL COMMENT '冰板类型',
  `SLAB_NUM` bigint(20) DEFAULT NULL COMMENT '冰板数量',
  `PACKAGE_TEMP` bigint(20) DEFAULT NULL COMMENT '商品温度',
  `SLAB_TEMP` bigint(20) DEFAULT NULL COMMENT '冰板温度',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `USER_CODE` bigint(20) DEFAULT NULL COMMENT '修改人ID',
  `USER_NAME` varchar(40) DEFAULT NULL COMMENT '录入人姓名',
  `USER_DMS_CODE` bigint(20) DEFAULT NULL COMMENT '操作人分拣中心',
  `USER_DMS_NAME` varchar(40) DEFAULT NULL COMMENT '操作人所在分拣名称',
  `USER_ORG_CODE` bigint(20) DEFAULT NULL COMMENT '操作人所属机构ID',
  `USER_ORG_NAME` varchar(40) DEFAULT NULL COMMENT '操作人所属机构名称',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`ID`),
  KEY `IDX_FRESH_PACKAGE` (`PACKAGE_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录生鲜订单温度';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gantry_device_config`
--

DROP TABLE IF EXISTS `gantry_device_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gantry_device_config` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `MACHINE_ID` bigint(20) DEFAULT NULL,
  `OPERATE_USER_ID` bigint(20) DEFAULT NULL,
  `OPERATE_USER_ERP` varchar(32) DEFAULT NULL,
  `OPERATE_USER_NAME` varchar(32) DEFAULT NULL,
  `UPDATE_USER_ERP` varchar(32) DEFAULT NULL,
  `UPDATE_USER_NAME` varchar(32) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(32) DEFAULT NULL,
  `BUSINESS_TYPE` bigint(20) DEFAULT NULL,
  `BUSINESS_TYPE_REMARK` varchar(32) DEFAULT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `LOCK_STATUS` bigint(20) DEFAULT NULL,
  `LOCK_USER_ERP` varchar(32) DEFAULT NULL,
  `LOCK_USER_NAME` varchar(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2796 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gantry_device_info`
--

DROP TABLE IF EXISTS `gantry_device_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gantry_device_info` (
  `MACHINE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SERIAL_NUMBER` varchar(32) DEFAULT NULL,
  `ORG_CODE` bigint(20) DEFAULT NULL,
  `ORG_NAME` varchar(32) DEFAULT NULL,
  `SITE_CODE` bigint(20) DEFAULT NULL,
  `SITE_NAME` varchar(32) DEFAULT NULL,
  `SUPPLIER` varchar(32) DEFAULT NULL,
  `MODEL_NUMBER` varchar(32) DEFAULT NULL,
  `MODEL_TYPE` varchar(30) DEFAULT NULL,
  `MARK` varchar(64) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `OPERATE_NAME` varchar(16) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`MACHINE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=643 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inspection_e_c`
--

DROP TABLE IF EXISTS `inspection_e_c`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inspection_e_c` (
  `CHECK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '验货异常比对表主键',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '运单号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL COMMENT '包裹号',
  `INSPECTION_E_C_TYPE` bigint(20) DEFAULT NULL COMMENT '验货异常类型(0:正常,1:少验,2:多验)',
  `EXCEPTION_STATUS` bigint(20) DEFAULT NULL COMMENT 'exception_status 状态(0:未处理,1:异常比较完成,3:超区退回,4:多验退回,5:少验取消,6:多验直接配送)',
  `INSPECTION_TYPE` bigint(20) DEFAULT NULL COMMENT '验货类型',
  `EXCEPTION_TYPE` varchar(50) DEFAULT NULL COMMENT '异常类型(用于退货)',
  `CREATE_USER` varchar(32) DEFAULT NULL COMMENT '创建人（收货人）',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人ID（收货人ID）',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间（收货时间）',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位（操作单位）',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '收货单位编号',
  `UPDATE_USER` varchar(32) DEFAULT NULL COMMENT '最后修改人name',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '最后修改人code',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '最后修改时间',
  `YN` bigint(20) DEFAULT '1',
  PRIMARY KEY (`CHECK_ID`),
  UNIQUE KEY `UNQ_INSPECTION_E_C_P` (`CHECK_ID`,`CREATE_TIME`),
  KEY `INSPECTION_E_C_CI` (`CREATE_SITE_CODE`,`INSPECTION_E_C_TYPE`),
  KEY `INSPECTION_E_C_WC` (`WAYBILL_CODE`),
  KEY `IND_INSPECTION_E_C_BCODE_P` (`BOX_CODE`),
  KEY `IND_INSPECTION_E_C_PCODE_P` (`PACKAGE_BARCODE`)
) ENGINE=InnoDB AUTO_INCREMENT=512593 DEFAULT CHARSET=utf8 COMMENT='验货异常比对表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `load_bill`
--

DROP TABLE IF EXISTS `load_bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `load_bill` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `LOAD_ID` varchar(50) DEFAULT NULL COMMENT '装载单ID(随机号码)',
  `WAREHOUSE_ID` varchar(50) DEFAULT NULL COMMENT '仓库ID',
  `WAYBILL_CODE` varchar(30) DEFAULT NULL COMMENT '运单号',
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL COMMENT '包裹号',
  `PACKAGE_AMOUNT` bigint(20) DEFAULT NULL COMMENT '包裹数量',
  `ORDER_ID` varchar(50) DEFAULT NULL COMMENT '订单号',
  `BOX_CODE` varchar(50) DEFAULT NULL COMMENT '箱号',
  `DMS_CODE` bigint(20) DEFAULT NULL COMMENT '分拣中心编号',
  `DMS_NAME` varchar(50) DEFAULT NULL COMMENT '分拣中心名称',
  `SEND_TIME` datetime DEFAULT NULL COMMENT '发货时间',
  `SEND_CODE` varchar(50) DEFAULT NULL COMMENT '发货批次号',
  `TRUCK_NO` varchar(50) DEFAULT NULL COMMENT '车牌号',
  `APPROVAL_CODE` bigint(20) DEFAULT NULL COMMENT '审批编号,10初始,20已申请,30已放行,40未放行',
  `APPROVAL_TIME` datetime DEFAULT NULL COMMENT '审批时间',
  `CTNO` varchar(50) DEFAULT NULL COMMENT '申报海关编码。默认：5165南沙旅检',
  `GJNO` varchar(50) DEFAULT NULL COMMENT '申报国检编码。默认：000069申报地国检',
  `TPL` varchar(50) DEFAULT NULL COMMENT '物流企业编码。默认：京配编号',
  `WEIGHT` bigint(20) DEFAULT NULL COMMENT '重量(精确小数点2位，单位kg)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `GEN_TIME` datetime DEFAULT NULL COMMENT '装载单生成时间',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '创建人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人编码',
  `PACKAGE_USER` varchar(50) DEFAULT NULL COMMENT '打包人',
  `PACKAGE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '打包人编号',
  `PACKAGE_TIME` datetime DEFAULT NULL COMMENT '打包时间',
  `REMARK` varchar(100) DEFAULT NULL COMMENT '备注',
  `YN` bigint(20) DEFAULT NULL COMMENT '有效标识',
  PRIMARY KEY (`ID`),
  KEY `IDX_APPROVAL_CODE` (`APPROVAL_CODE`),
  KEY `IDX_DMS_CODE` (`DMS_CODE`),
  KEY `IDX_LOADID_ORDERID_WAREHOUSEID` (`LOAD_ID`,`ORDER_ID`,`WAREHOUSE_ID`),
  KEY `IDX_PACKAGE_BARCODE` (`PACKAGE_BARCODE`),
  KEY `IDX_SEND_CODE` (`SEND_CODE`),
  KEY `IDX_SEND_TIME` (`SEND_TIME`),
  KEY `IDX_LOADBILL_BOXCODE` (`BOX_CODE`),
  KEY `IND_LOAD_BILL_WCODE` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=5986835 DEFAULT CHARSET=utf8 COMMENT='全球购业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `load_bill_report`
--

DROP TABLE IF EXISTS `load_bill_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `load_bill_report` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `REPORT_ID` varchar(50) DEFAULT NULL COMMENT '装载单申报ID',
  `LOAD_ID` varchar(500) DEFAULT NULL COMMENT '装载单ID(随机号码),多个以逗号隔开',
  `WAREHOUSE_ID` varchar(50) DEFAULT NULL COMMENT '仓库ID',
  `PROCESS_TIME` datetime DEFAULT NULL COMMENT '放行或作废时间(yyyy-mm-dd HH:mi:ss)',
  `STATUS` bigint(20) DEFAULT NULL COMMENT '状态 1:成功，2:作废',
  `NOTES` varchar(1000) DEFAULT NULL COMMENT '如作废，则作废信息',
  `ORDER_ID` varchar(500) DEFAULT NULL COMMENT '成功的订单号,多个以逗号分割',
  `YN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=152937 DEFAULT CHARSET=utf8 COMMENT='装载单状态回传表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `loss_order`
--

DROP TABLE IF EXISTS `loss_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `loss_order` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ORDER_ID` bigint(20) DEFAULT NULL COMMENT '订单号',
  `LOSS_TYPE` bigint(20) DEFAULT NULL COMMENT '报损类型',
  `PRODUCT_ID` bigint(20) DEFAULT NULL COMMENT '商品编号',
  `PRODUCT_NAME` varchar(1024) DEFAULT NULL COMMENT '商品名称',
  `PRODUCT_QUANTITY` bigint(6) DEFAULT NULL COMMENT '商品数量',
  `USER_ERP` varchar(43) DEFAULT NULL COMMENT '报丢操作人ERP',
  `USER_NAME` varchar(64) DEFAULT NULL COMMENT '报丢操作人姓名',
  `LOSS_QUANTITY` bigint(20) DEFAULT NULL COMMENT '报丢数量',
  `LOSS_TIME` datetime DEFAULT NULL COMMENT '报丢时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `LOSS_CODE` bigint(20) DEFAULT NULL COMMENT '报损单号',
  PRIMARY KEY (`SYSTEM_ID`),
  UNIQUE KEY `LOSS_ORDER_PK` (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=18873 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mc_message_task`
--

DROP TABLE IF EXISTS `mc_message_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mc_message_task` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `QUEUE_ID` varchar(100) DEFAULT NULL,
  `OWNSGN` varchar(100) DEFAULT NULL,
  `MESSAGE_NAME` varchar(100) DEFAULT NULL,
  `PRODUCER_SYSTEMID` varchar(100) DEFAULT NULL,
  `DESTINATION_CODE` varchar(100) DEFAULT NULL,
  `DESTINATION_TYPE` varchar(5) DEFAULT NULL,
  `CONTENT` varchar(4000) DEFAULT NULL,
  `BUSINESS_ID` varchar(100) DEFAULT NULL,
  `IS_SPLIT` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `SUCCESS` bigint(20) DEFAULT NULL,
  `FAILED_COUNT` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_MC_MESSAGE_TASK_QUERY` (`QUEUE_ID`,`MESSAGE_NAME`,`BUSINESS_ID`,`SUCCESS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mc_message_task_part`
--

DROP TABLE IF EXISTS `mc_message_task_part`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mc_message_task_part` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CONTENT` varchar(4000) DEFAULT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_MESSAGE_TASK_PART_TASK_ID` (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `offline_log`
--

DROP TABLE IF EXISTS `offline_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offline_log` (
  `OFFLINE_LOG_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '离线日志表ID',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '运单号',
  `PACKAGE_CODE` varchar(32) DEFAULT NULL COMMENT '包裹号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BUSINESS_TYPE` bigint(20) DEFAULT NULL COMMENT '业务类型（10正向 20逆向 ）',
  `CREATE_USER` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位ID',
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL COMMENT '创建单位名称',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '目的单位ID',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `TURNOVERBOX_CODE` varchar(30) DEFAULT NULL COMMENT '周转箱号',
  `WEIGHT` bigint(20) DEFAULT NULL COMMENT '重量',
  `VOLUME` bigint(20) DEFAULT NULL COMMENT '体积',
  `EXCEPTION_TYPE` varchar(50) DEFAULT NULL COMMENT '异常类型',
  `SEND_CODE` varchar(30) DEFAULT NULL COMMENT '发货交接单号',
  `SEAL_BOX_CODE` varchar(32) DEFAULT NULL COMMENT '封箱号',
  `SEAL_VEHICLE_CODE` varchar(32) DEFAULT NULL COMMENT '封车号',
  `VEHICLE_CODE` varchar(32) DEFAULT NULL COMMENT '车号',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '任务类型',
  `SEND_USER` varchar(32) DEFAULT NULL COMMENT '发货人',
  `SEND_USER_CODE` bigint(20) DEFAULT NULL COMMENT '发货人ID',
  `YN` bigint(20) DEFAULT '1' COMMENT 'DF',
  `OPERATE_TYPE` bigint(20) DEFAULT NULL COMMENT '逆向操作类型',
  `STATUS` bigint(20) DEFAULT NULL COMMENT '状态0失败1成功',
  PRIMARY KEY (`OFFLINE_LOG_ID`),
  UNIQUE KEY `UNQ_OFFLINE_LOG_ID` (`OFFLINE_LOG_ID`,`CREATE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=3506062 DEFAULT CHARSET=utf8 COMMENT='离线日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `operation_log_0`
--

DROP TABLE IF EXISTS `operation_log_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operation_log_0` (
  `LOG_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '操作日志表ID',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '运单号',
  `PICKUP_CODE` varchar(32) DEFAULT NULL COMMENT '取件单号',
  `PACKAGE_CODE` varchar(32) DEFAULT NULL COMMENT '包裹号',
  `LOG_TYPE` bigint(20) DEFAULT NULL COMMENT '日志类型',
  `SEND_CODE` varchar(50) DEFAULT NULL COMMENT '批次号',
  `CREATE_USER` varchar(128) DEFAULT NULL COMMENT '创建人姓名',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人code',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点code',
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL COMMENT '创建站点名称',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收站点code',
  `RECEIVE_SITE_NAME` varchar(64) DEFAULT NULL COMMENT '接收站点名称',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除 ''0'' 删除 ''1'' 使用',
  `REMARK` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`LOG_ID`),
  UNIQUE KEY `UNQ_OPERATION_LOG_0_P` (`LOG_ID`,`CREATE_TIME`),
  KEY `IND_OPERATION_LOG_0_BCODE_P` (`BOX_CODE`),
  KEY `IND_OPERATION_LOG_0_PCODE_P` (`PACKAGE_CODE`),
  KEY `IND_OPERATION_LOG_0_WCODE_P` (`WAYBILL_CODE`),
  KEY `IND_OPERATION_L_PCODE_PL` (`PICKUP_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pamirs_schedule_queue`
--

DROP TABLE IF EXISTS `pamirs_schedule_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pamirs_schedule_queue` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `TASK_TYPE` varchar(50) NOT NULL,
  `QUEUE_ID` varchar(50) NOT NULL,
  `OWN_SIGN` varchar(50) NOT NULL,
  `BASE_TASK_TYPE` varchar(50) DEFAULT NULL,
  `CUR_SERVER` varchar(100) DEFAULT NULL,
  `REQ_SERVER` varchar(100) DEFAULT NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IND_PAMIRS_SCHEDULE_QUEUEID` (`TASK_TYPE`,`QUEUE_ID`,`OWN_SIGN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pamirs_schedule_server`
--

DROP TABLE IF EXISTS `pamirs_schedule_server`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pamirs_schedule_server` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `UUID` varchar(100) NOT NULL,
  `TASK_TYPE` varchar(50) NOT NULL,
  `OWN_SIGN` varchar(50) NOT NULL,
  `BASE_TASK_TYPE` varchar(50) NOT NULL,
  `IP` varchar(50) NOT NULL,
  `HOST_NAME` varchar(50) NOT NULL,
  `MANAGER_PORT` bigint(20) NOT NULL,
  `THREAD_NUM` bigint(20) NOT NULL,
  `REGISTER_TIME` datetime NOT NULL,
  `HEARTBEAT_TIME` datetime NOT NULL,
  `VERSION` bigint(20) NOT NULL,
  `JMX_URL` varchar(200) DEFAULT NULL,
  `DEALINFO_DESC` varchar(1000) DEFAULT NULL,
  `NEXT_RUN_START_TIME` varchar(100) DEFAULT NULL,
  `NEXT_RUN_END_TIME` varchar(100) DEFAULT NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IND_PAMIRS_SCHEDULE_UUID` (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pamirs_schedule_server_his`
--

DROP TABLE IF EXISTS `pamirs_schedule_server_his`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pamirs_schedule_server_his` (
  `id` bigint(20) DEFAULT NULL,
  `uuid` varchar(100) DEFAULT NULL,
  `task_type` varchar(100) DEFAULT NULL,
  `own_sign` varchar(100) DEFAULT NULL,
  `base_task_type` varchar(100) DEFAULT NULL,
  `ip` varchar(100) DEFAULT NULL,
  `host_name` varchar(100) DEFAULT NULL,
  `manager_port` int(11) DEFAULT NULL,
  `thread_num` int(11) DEFAULT NULL,
  `register_time` datetime DEFAULT NULL,
  `heartbeat_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `jmx_url` varchar(100) DEFAULT NULL,
  `dealinfo_desc` varchar(100) DEFAULT NULL,
  `next_run_start_time` varchar(100) DEFAULT NULL,
  `next_run_end_time` varchar(100) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pamirs_schedule_tasktrun`
--

DROP TABLE IF EXISTS `pamirs_schedule_tasktrun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pamirs_schedule_tasktrun` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `TASK_TYPE` varchar(100) NOT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `BASE_TASK_TYPE` varchar(50) DEFAULT NULL,
  `LAST_ASSIGN_TIME` datetime DEFAULT NULL,
  `LAST_ASSIGN_UUID` varchar(100) DEFAULT NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IND_PAMIRS_SCHEDULE_RUN_TASK` (`TASK_TYPE`,`OWN_SIGN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pamirs_schedule_tasktype`
--

DROP TABLE IF EXISTS `pamirs_schedule_tasktype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pamirs_schedule_tasktype` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `TASK_TYPE` varchar(100) NOT NULL,
  `DEAL_BEAN_NAME` varchar(100) NOT NULL,
  `HEARTBEAT_RATE` bigint(20) NOT NULL,
  `JUDGE_DEAD_INTERVAL` bigint(20) NOT NULL,
  `THREAD_NUMBER` bigint(20) NOT NULL,
  `EXECUTE_NUMBER` bigint(20) DEFAULT NULL,
  `FETCH_NUMBER` bigint(20) DEFAULT NULL,
  `SLEEP_TIME_NODATA` bigint(20) DEFAULT NULL,
  `SLEEP_TIME_INTERVAL` bigint(20) DEFAULT NULL,
  `PROCESSOR_TYPE` varchar(20) DEFAULT NULL,
  `PERMIT_RUN_START_TIME` varchar(100) DEFAULT NULL,
  `PERMIT_RUN_END_TIME` varchar(100) DEFAULT NULL,
  `LAST_ASSIGN_TIME` datetime DEFAULT NULL,
  `LAST_ASSIGN_UUID` varchar(100) DEFAULT NULL,
  `EXPIRE_OWN_SIGN_INTERVAL` bigint(20) DEFAULT NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IND_PAMIRS_TASKTYPE_TASKTYPE` (`TASK_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partner_waybill`
--

DROP TABLE IF EXISTS `partner_waybill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partner_waybill` (
  `RELATION_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '运单包裹(订单)关联表ID',
  `PARTNER_WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '第三方运单号',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '运单号(订单号)',
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL COMMENT '包裹号',
  `PARTNER_STATUS` bigint(20) DEFAULT '0' COMMENT '0 待处理  1 处理完成  3 异常',
  `PARTNER_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '三方编码',
  `CREATE_USER` varchar(32) DEFAULT NULL COMMENT '创建人（收货人）',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人ID（收货人ID）',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位（操作单位）ID',
  `UPDATE_USER` varchar(32) DEFAULT NULL COMMENT '最后更新人name',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '最后更新人code',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT '1',
  PRIMARY KEY (`RELATION_ID`),
  UNIQUE KEY `PK_PARTNER_WAYBILL` (`RELATION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='三方运单关联信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pick_ware`
--

DROP TABLE IF EXISTS `pick_ware`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pick_ware` (
  `PICKWARE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '备件库取件单表ID',
  `ORG_ID` bigint(20) DEFAULT NULL COMMENT '机构ID',
  `ORDER_ID` bigint(20) DEFAULT NULL COMMENT '订单号',
  `PACKAGE_CODE` varchar(32) DEFAULT NULL COMMENT '包裹号(取件单面单号)',
  `PICKWARE_CODE` varchar(32) DEFAULT NULL COMMENT '取件单号',
  `OPERATE_TYPE` bigint(20) DEFAULT NULL COMMENT '操作类型(1.交接 2.拆包)',
  `OPERATOR` varchar(32) DEFAULT NULL COMMENT '操作人',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `CAN_RECEIVE` bigint(20) DEFAULT NULL COMMENT '是否可收(0.拒收 1.可收)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`PICKWARE_ID`),
  UNIQUE KEY `PK_PICKWARE` (`PICKWARE_ID`),
  KEY `IND_PICK_WARE_FINGPT` (`FINGERPRINT`)
) ENGINE=InnoDB AUTO_INCREMENT=200298811 DEFAULT CHARSET=utf8 COMMENT='备件库取件单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pop_abnormal`
--

DROP TABLE IF EXISTS `pop_abnormal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pop_abnormal` (
  `ABNORMAL_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '递增主键',
  `ORG_CODE` bigint(20) DEFAULT NULL COMMENT '机构CODE',
  `ORG_NAME` varchar(64) DEFAULT NULL COMMENT '机构名称',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点CODE',
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL COMMENT '创建站点名称',
  `MAIN_TYPE` bigint(20) DEFAULT NULL COMMENT '一级差异类型ID',
  `MAIN_TYPE_NAME` varchar(128) DEFAULT NULL COMMENT '一级差异类型名称',
  `SUB_TYPE` bigint(20) DEFAULT NULL COMMENT '二级差异类型ID',
  `SUB_TYPE_NAME` varchar(128) DEFAULT NULL COMMENT '二级差异类型名称',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '订单号',
  `POP_SUP_NO` varchar(32) DEFAULT NULL COMMENT '商家ID',
  `POP_SUP_NAME` varchar(128) DEFAULT NULL COMMENT '商家名称',
  `ABNORMAL_STATUE` bigint(20) DEFAULT NULL COMMENT '处理状态',
  `ATTR1` varchar(128) DEFAULT NULL COMMENT '字段1',
  `ATTR2` varchar(128) DEFAULT NULL COMMENT '字段2',
  `ATTR3` varchar(128) DEFAULT NULL COMMENT '字段3',
  `ATTR4` varchar(512) DEFAULT NULL COMMENT '字段4',
  `OPERATOR_NAME` varchar(32) DEFAULT NULL COMMENT '发起人姓名',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  `WAYBILL_TYPE` bigint(20) DEFAULT NULL COMMENT '订单类型',
  `ORDER_CODE` varchar(32) DEFAULT NULL COMMENT '订单号',
  PRIMARY KEY (`ABNORMAL_ID`),
  UNIQUE KEY `PK_POP_ABNORMAL` (`ABNORMAL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=35181 DEFAULT CHARSET=utf8 COMMENT='POP差异列表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pop_abnormal_detail`
--

DROP TABLE IF EXISTS `pop_abnormal_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pop_abnormal_detail` (
  `ABNORMAL_DETAIL_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '递增主键ID',
  `ABNORMAL_ID` bigint(20) DEFAULT NULL COMMENT '差异列表对应ID',
  `OPERATOR_CODE` bigint(20) DEFAULT NULL COMMENT '操作人CODE',
  `OPERATOR_NAME` varchar(32) DEFAULT NULL COMMENT '操作人名称',
  `OPERATOR_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `REMARK` varchar(512) DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`ABNORMAL_DETAIL_ID`),
  UNIQUE KEY `PK_POP_ABNORMAL_DETAIL` (`ABNORMAL_DETAIL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=43037 DEFAULT CHARSET=utf8 COMMENT='POP差异明细列表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pop_abnormal_order`
--

DROP TABLE IF EXISTS `pop_abnormal_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pop_abnormal_order` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `SERIAL_NUMBER` varchar(32) NOT NULL COMMENT '流水单号',
  `ABNORMAL_TYPE` bigint(20) NOT NULL COMMENT '类型',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '运单号',
  `ORDER_CODE` varchar(32) DEFAULT NULL COMMENT '订单号',
  `POP_SUP_NO` varchar(32) DEFAULT NULL COMMENT '商家编号',
  `CURRENT_NUM` bigint(20) NOT NULL COMMENT '原始包裹数量',
  `ACTUAL_NUM` bigint(20) NOT NULL COMMENT '实际包裹数量',
  `CONFIRM_NUM` bigint(20) DEFAULT NULL COMMENT '商家确认包裹数量',
  `OPERATOR_CODE` bigint(20) DEFAULT NULL COMMENT '操作人Code',
  `OPERATOR_NAME` varchar(32) DEFAULT NULL COMMENT '操作人名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CONFIRM_TIME` datetime DEFAULT NULL COMMENT '商家审核通过时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `ABNORMAL_STATE` bigint(20) NOT NULL COMMENT '1,已发申请，商家未处理\r\n            2,商家审核完毕，已处理',
  `MEMO` varchar(256) DEFAULT NULL COMMENT '备注',
  `RSV1` varchar(64) DEFAULT NULL COMMENT '扩展字段1',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '操作人站点编号',
  `CREATE_SITE_NAME` varchar(50) DEFAULT NULL COMMENT '操作人站点名称',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PK_POP_ABNORMAL_ORDER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='POP异常订单申请单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pop_pickup`
--

DROP TABLE IF EXISTS `pop_pickup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pop_pickup` (
  `PICKUP_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'POP上门取货表ID',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '运单号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL COMMENT '包裹号',
  `PICKUP_STATUS` bigint(20) DEFAULT NULL COMMENT '0 待处理 1 处理完成 2 处理中 3 异常',
  `PICKUP_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `PACKAGE_NUMBER` bigint(20) DEFAULT NULL COMMENT '包裹数量',
  `POP_BUSINESS_CODE` varchar(128) DEFAULT NULL COMMENT 'POP商家编码',
  `POP_BUSINESS_NAME` varchar(128) DEFAULT NULL COMMENT 'POP商家名称',
  `CREATE_USER` varchar(32) DEFAULT NULL COMMENT '操作人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点编码',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '收货单位编号',
  `UPDATE_USER` varchar(32) DEFAULT NULL COMMENT '最后操作人',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '最后修改人编码',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '最后修改时间',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `YN` bigint(20) DEFAULT '1' COMMENT 'DF',
  `CAR_CODE` varchar(64) DEFAULT NULL COMMENT '车牌号',
  `WAYBILL_TYPE` bigint(20) DEFAULT NULL COMMENT '运单类型',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`PICKUP_ID`),
  UNIQUE KEY `PK_POP_PICKUP` (`PICKUP_ID`),
  KEY `IND_POP_PICKUP_CID_OTIME` (`CREATE_SITE_CODE`,`OPERATE_TIME`),
  KEY `IND_POP_PICKUP_WCODE` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200086358 DEFAULT CHARSET=utf8 COMMENT='POP上门取货表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pop_print`
--

DROP TABLE IF EXISTS `pop_print`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pop_print` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `PRINT_PACK_CODE` bigint(20) DEFAULT NULL,
  `PRINT_PACK_TIME` datetime DEFAULT NULL,
  `PRINT_INVOICE_CODE` bigint(20) DEFAULT NULL,
  `PRINT_INVOICE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `POP_SUP_ID` bigint(20) DEFAULT NULL COMMENT 'POP商家ID',
  `POP_SUP_NAME` varchar(128) DEFAULT NULL COMMENT 'POP商家名称',
  `PRINT_PACK_USER` varchar(32) DEFAULT NULL COMMENT '打印包裹人姓名',
  `PRINT_INVOICE_USER` varchar(32) DEFAULT NULL COMMENT '打印发票人姓名',
  `QUANTITY` bigint(20) DEFAULT NULL COMMENT '包裹数量',
  `CROSS_CODE` varchar(16) DEFAULT NULL COMMENT '滑道号',
  `WAYBILL_TYPE` bigint(20) DEFAULT NULL COMMENT '运单类型',
  `POP_RECEIVE_TYPE` bigint(20) DEFAULT NULL COMMENT 'POP收货类型:1,商家直送；2,托寄送货',
  `PRINT_COUNT` bigint(20) DEFAULT NULL COMMENT 'POP运单号打印包裹次数',
  `THIRD_WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT 'POP第三方运单号',
  `QUEUE_NO` varchar(32) DEFAULT NULL COMMENT 'POP排队号',
  `OPERATE_TYPE` bigint(20) DEFAULT NULL COMMENT '操作类型：1、打印包裹；2、打印发票',
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL COMMENT '包裹号',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人编号',
  `CREATE_USER` varchar(32) DEFAULT NULL COMMENT '创建人名称',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `DRIVER_CODE` varchar(32) DEFAULT NULL COMMENT '司机编号',
  `DRIVER_NAME` varchar(32) DEFAULT NULL COMMENT '司机名称',
  `BUSI_ID` bigint(20) DEFAULT NULL COMMENT 'B商家ID',
  `BUSI_NAME` varchar(128) DEFAULT NULL COMMENT 'B商家名称',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`ID`),
  KEY `IND_POP_PRINT_WC` (`WAYBILL_CODE`),
  KEY `IDX_POP_PRINT_C_C` (`CREATE_SITE_CODE`,`CREATE_TIME`),
  KEY `IDX_POP_PRINT_C` (`CREATE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=702159596 DEFAULT CHARSET=utf8 COMMENT='POP打印记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pop_queue`
--

DROP TABLE IF EXISTS `pop_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pop_queue` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `QUEUE_NO` varchar(32) NOT NULL,
  `CREATE_SITE_CODE` bigint(20) NOT NULL,
  `CREATE_SITE_NAME` varchar(32) DEFAULT NULL,
  `QUEUE_TYPE` bigint(20) DEFAULT NULL COMMENT '1、商家直送\r\n            2、快递公司',
  `EXPRESS_CODE` varchar(32) DEFAULT NULL,
  `EXPRESS_NAME` varchar(64) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `QUEUE_STATUS` bigint(20) DEFAULT NULL COMMENT '1、排号中\r\n            2、正在收货\r\n            3、收货完成',
  `START_TIME` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `WAIT_NO` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_POP_QUEUE_ID` (`ID`,`CREATE_TIME`),
  KEY `IND_POP_QUEUE_QNO` (`QUEUE_NO`)
) ENGINE=InnoDB AUTO_INCREMENT=200000562 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pop_receive`
--

DROP TABLE IF EXISTS `pop_receive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pop_receive` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `RECEIVE_TYPE` bigint(20) DEFAULT NULL COMMENT '收货类型',
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '订单号',
  `THIRD_WAYBILL_CODE` varchar(128) DEFAULT NULL COMMENT '三方运单号',
  `ORIGINAL_NUM` bigint(20) DEFAULT NULL COMMENT '应收包裹数',
  `ACTUAL_NUM` bigint(20) DEFAULT NULL COMMENT '实收包裹数',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '操作站点ID',
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL COMMENT '操作站点名称',
  `OPERATOR_CODE` bigint(20) DEFAULT NULL COMMENT '操作人CODE',
  `OPERATOR_NAME` varchar(32) DEFAULT NULL COMMENT '操作人名称',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '系统创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '系统更新时间',
  `IS_REVERSE` bigint(20) DEFAULT NULL COMMENT '是否收全',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '防重码',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SYSTEM_ID`),
  UNIQUE KEY `UNQ_POP_RECEIVE_ID` (`SYSTEM_ID`,`CREATE_TIME`),
  KEY `IND_POP_RECEIVE_FPRINT` (`FINGERPRINT`),
  KEY `IND_POP_RECEIVE_WCODE` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200000000 DEFAULT CHARSET=utf8 COMMENT='分拣中心客户端POP收货记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pop_signin`
--

DROP TABLE IF EXISTS `pop_signin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pop_signin` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `QUEUE_NO` varchar(32) DEFAULT NULL,
  `THIRD_WAYBILL_CODE` varchar(32) NOT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `EXPRESS_CODE` varchar(32) DEFAULT NULL,
  `EXPRESS_NAME` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_POP_SIGNIN_ID` (`ID`,`CREATE_TIME`),
  KEY `IND_POP_SIGNIN_QNO` (`QUEUE_NO`),
  KEY `IND_POP_SIGNIN_TWCODE` (`THIRD_WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200000646 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reassign_waybill`
--

DROP TABLE IF EXISTS `reassign_waybill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reassign_waybill` (
  `REASSIGN_WAYBILL_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL COMMENT '包裹号',
  `ADDRESS` varchar(500) DEFAULT NULL COMMENT '运单地址',
  `RECEIVE_SITE_NAME` varchar(64) DEFAULT NULL COMMENT '预分拣目的站点名称',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '预分拣目的站点编号 ',
  `CHANGE_SITE_NAME` varchar(64) DEFAULT NULL COMMENT '现场调度站点名称',
  `CHANGE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '现场调度站点编号',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '调度时间',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '调度人编号',
  `CREATE_USER` varchar(16) DEFAULT NULL COMMENT '调度人',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '操作站点编号',
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL COMMENT '操作站点名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  `WAYBILL_CODE` varchar(30) DEFAULT NULL COMMENT '运单号',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`REASSIGN_WAYBILL_ID`),
  UNIQUE KEY `PK_REASSIGN_WAYBILL` (`REASSIGN_WAYBILL_ID`),
  KEY `IND_RE_WAYBILL_PCODE` (`PACKAGE_BARCODE`),
  KEY `IND_RE_WAYBILL_WCODE` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200018399 DEFAULT CHARSET=utf8 COMMENT='返调度重打包裹标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `receive`
--

DROP TABLE IF EXISTS `receive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `receive` (
  `RECEIVE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '收货表ID',
  `WAYBILL_CODE` varchar(30) DEFAULT NULL COMMENT '运单号',
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL COMMENT '包裹号',
  `BOX_CODE` varchar(30) DEFAULT NULL COMMENT '箱号',
  `RECEIVE_TYPE` bigint(20) DEFAULT NULL COMMENT '收货类型（10正向 20逆向 ）',
  `BOXING_TYPE` bigint(20) DEFAULT NULL COMMENT '装箱类型（1 箱包装 2 单件包裹）',
  `RECEIVE_STATUS` bigint(20) DEFAULT '0' COMMENT '0未处理  1已处理',
  `TASK_EXE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `CREATE_USER` varchar(32) DEFAULT NULL COMMENT '创建人（收货人）',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人ID（收货人ID）',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间（收货时间）',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位（收货单位）ID',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '最后修改时间',
  `YN` bigint(20) DEFAULT '1' COMMENT 'DF',
  `TURNOVERBOX_CODE` varchar(30) DEFAULT NULL COMMENT '周转箱号',
  `QUEUENO` varchar(64) DEFAULT NULL COMMENT '排队号',
  `DEPARTURE_CAR_ID` bigint(20) DEFAULT NULL COMMENT '发车号',
  `SHIELDS_CAR_TIME` datetime DEFAULT NULL COMMENT '收箱扫描发车条码时间',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`RECEIVE_ID`),
  UNIQUE KEY `UNQ_RECEIVE_P` (`RECEIVE_ID`,`CREATE_TIME`),
  KEY `IND_RECEIVE_BCODE` (`BOX_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200253523 DEFAULT CHARSET=utf8 COMMENT='收货表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reverse_label`
--

DROP TABLE IF EXISTS `reverse_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reverse_label` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `LABEL_TYPE` bigint(20) DEFAULT NULL COMMENT '打印类型',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '始发地编号',
  `CREATE_SITE_NAME` varchar(100) DEFAULT NULL COMMENT '始发地名称',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '目的地编号',
  `RECEIVE_SITE_NAME` varchar(100) DEFAULT NULL COMMENT '目的地名称',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '操作人',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '操作人编号',
  `OPERATOR_NAME` varchar(100) DEFAULT NULL COMMENT '操作单位',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `PRINT_NUM` bigint(20) DEFAULT NULL COMMENT '打印数量',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PK_REVERSE_LABEL` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4957 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reverse_receive`
--

DROP TABLE IF EXISTS `reverse_receive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reverse_receive` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增序列',
  `FINGERPRINT` varchar(60) DEFAULT NULL COMMENT '数据指纹',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '收货时间',
  `OPERATOR` varchar(32) DEFAULT NULL COMMENT '收货人',
  `CAN_RECEIVE` bigint(20) DEFAULT NULL COMMENT '收货标识',
  `REJECT_CODE` bigint(20) DEFAULT NULL COMMENT '拒收编码',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '最后一次更新时间',
  `BUSINESS_TYPE` bigint(20) DEFAULT NULL,
  `REJECT_MESSAGE` varchar(500) DEFAULT NULL COMMENT '拒收信息',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OPERATOR_CODE` varchar(32) DEFAULT NULL COMMENT '收货人编号',
  `SEND_CODE` varchar(30) DEFAULT NULL COMMENT '发货交接单号',
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL COMMENT '包裹号',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SYSTEM_ID`),
  UNIQUE KEY `REVERSE_RECEIVE_PK` (`SYSTEM_ID`),
  KEY `IND_REVERSE_RECEIVE_PCODE` (`PACKAGE_BARCODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200088935 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reverse_reject`
--

DROP TABLE IF EXISTS `reverse_reject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reverse_reject` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `CKY2` bigint(20) DEFAULT NULL COMMENT '配送中心标识',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '库房ID',
  `ORDER_ID` varchar(30) DEFAULT NULL COMMENT '订单编号',
  `PACKAGE_CODE` varchar(64) DEFAULT NULL COMMENT '包裹编号',
  `OPERATOR_CODE` varchar(32) DEFAULT NULL COMMENT '操作人编号',
  `OPERATOR` varchar(32) DEFAULT NULL COMMENT '操作人',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `BUSINESS_TYPE` bigint(20) DEFAULT NULL COMMENT '业务类型',
  `INSPECTOR` varchar(32) DEFAULT NULL COMMENT '验货人',
  `INSPECT_TIME` datetime DEFAULT NULL COMMENT '验货时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '最有一次修改时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `INSPECTOR_CODE` varchar(32) DEFAULT NULL COMMENT '验货人编号',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点',
  `CREATE_SITE_NAME` varchar(32) DEFAULT NULL COMMENT '创建站点名称',
  `PICKWARE_CODE` varchar(32) DEFAULT NULL COMMENT '取件单号',
  `ORG_ID` bigint(20) DEFAULT NULL COMMENT '机构ID',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SYSTEM_ID`),
  UNIQUE KEY `PK_REVERSE_REJECT_NEW` (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=200002177 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reverse_spare`
--

DROP TABLE IF EXISTS `reverse_spare`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reverse_spare` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SPARE_CODE` varchar(32) DEFAULT NULL,
  `SEND_CODE` varchar(30) DEFAULT NULL,
  `WAYBILL_CODE` varchar(30) DEFAULT NULL,
  `PRODUCT_ID` varchar(32) DEFAULT NULL,
  `PRODUCT_CODE` varchar(32) DEFAULT NULL,
  `PRODUCT_NAME` varchar(512) DEFAULT NULL,
  `ARRT_CODE1` bigint(20) DEFAULT NULL,
  `ARRT_DESC1` varchar(32) DEFAULT NULL,
  `ARRT_CODE2` bigint(20) DEFAULT NULL,
  `ARRT_DESC2` varchar(32) DEFAULT NULL,
  `ARRT_CODE3` bigint(20) DEFAULT NULL,
  `ARRT_DESC3` varchar(32) DEFAULT NULL,
  `ARRT_CODE4` bigint(20) DEFAULT NULL,
  `ARRT_DESC4` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `PRODUCT_PRICE` bigint(20) DEFAULT NULL,
  `SPARE_TRAN_CODE` varchar(32) DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SYSTEM_ID`),
  UNIQUE KEY `PK_REVERSE_SPARE_NEW` (`SYSTEM_ID`),
  KEY `IND_REVERSE_SPARE_CODE_NEW` (`SPARE_CODE`),
  KEY `IND_REVERSE_SPARE_STCODE_N` (`SPARE_TRAN_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200009315 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seal_box`
--

DROP TABLE IF EXISTS `seal_box`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seal_box` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `SEAL_CODE` varchar(32) DEFAULT NULL COMMENT '封箱编号',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收站点编号',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人编号',
  `CREATE_USER` varchar(16) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '修改人编号',
  `UPDATE_USER` varchar(16) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除 ''0'' 删除 ''1'' 使用',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SYSTEM_ID`),
  UNIQUE KEY `UNQ_SEAL_BOX_P` (`SYSTEM_ID`,`CREATE_TIME`),
  KEY `IND_SEAL_BOX_SC` (`SEAL_CODE`),
  KEY `IND_SEAL_BOX_BCODE_PL` (`BOX_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200067153 DEFAULT CHARSET=utf8 COMMENT='封箱信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seal_vehicle`
--

DROP TABLE IF EXISTS `seal_vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seal_vehicle` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `VEHICLE_CODE` varchar(32) DEFAULT NULL COMMENT '车辆编号',
  `SEAL_CODE` varchar(32) DEFAULT NULL COMMENT '封车编号',
  `DRIVER_CODE` varchar(16) DEFAULT NULL COMMENT '司机编号',
  `DRIVER` varchar(50) DEFAULT NULL COMMENT '司机',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收站点编号',
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '创建人编号',
  `CREATE_USER` varchar(50) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL COMMENT '修改人编号',
  `UPDATE_USER` varchar(50) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除 ''0'' 删除 ''1'' 使用',
  `SEND_CODE` varchar(50) DEFAULT NULL COMMENT '批次号',
  `VOLUME` bigint(20) DEFAULT NULL COMMENT '体积',
  `WEIGHT` bigint(20) DEFAULT NULL COMMENT '重量',
  `PACKAGE_NUM` bigint(20) DEFAULT NULL COMMENT '包裹数',
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SYSTEM_ID`),
  UNIQUE KEY `PK_SEAL_VEHICLE` (`SYSTEM_ID`),
  KEY `IND_SEAL_VEHICLE_SC` (`SEAL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200004907 DEFAULT CHARSET=utf8 COMMENT='封车信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `send_barcode`
--

DROP TABLE IF EXISTS `send_barcode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `send_barcode` (
  `SEND_BARCODE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SEND_BARCODE_CODE` varchar(50) DEFAULT NULL,
  `SEND_BARCODE_TYPE` varchar(2) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(100) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_NAME` varchar(100) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `TIMES` bigint(20) DEFAULT NULL,
  `SEND_BARCODE_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SEND_BARCODE_ID`),
  KEY `IND_SEND_BARCODE_BC` (`SEND_BARCODE_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `send_query`
--

DROP TABLE IF EXISTS `send_query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `send_query` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(50) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `IP_ADDRESS` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PK_SEND_QUERY` (`ID`),
  KEY `IND_SEND_QUERY_SEND_CODE` (`SEND_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=12643 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_0`
--

DROP TABLE IF EXISTS `sequence_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_0` (
  `seqName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `currentValue` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL,
  PRIMARY KEY (`seqName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_1`
--

DROP TABLE IF EXISTS `sequence_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_1` (
  `seqName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `currentValue` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL,
  PRIMARY KEY (`seqName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_2`
--

DROP TABLE IF EXISTS `sequence_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_2` (
  `seqName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `currentValue` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL,
  PRIMARY KEY (`seqName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_3`
--

DROP TABLE IF EXISTS `sequence_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_3` (
  `seqName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `currentValue` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL,
  PRIMARY KEY (`seqName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_4`
--

DROP TABLE IF EXISTS `sequence_4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_4` (
  `seqName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `currentValue` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL,
  PRIMARY KEY (`seqName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_5`
--

DROP TABLE IF EXISTS `sequence_5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_5` (
  `seqName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `currentValue` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL,
  PRIMARY KEY (`seqName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_6`
--

DROP TABLE IF EXISTS `sequence_6`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_6` (
  `seqName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `currentValue` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL,
  PRIMARY KEY (`seqName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_7`
--

DROP TABLE IF EXISTS `sequence_7`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_7` (
  `seqName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `currentValue` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL,
  PRIMARY KEY (`seqName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shields_error`
--

DROP TABLE IF EXISTS `shields_error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shields_error` (
  `ERROR_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BOX_CODE` varchar(30) DEFAULT NULL,
  `SHIELDS_CODE` varchar(30) DEFAULT NULL,
  `CAR_CODE` varchar(30) DEFAULT NULL,
  `SHIELDS_ERROR` varchar(50) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT '1',
  `BUSINESS_TYPE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ERROR_ID`),
  UNIQUE KEY `PK_SHIELDS_ERROR` (`ERROR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sorting_ec`
--

DROP TABLE IF EXISTS `sorting_ec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sorting_ec` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `BUSINESS_TYPE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(50) DEFAULT NULL,
  `PACKAGE_CODE` varchar(50) DEFAULT NULL,
  `EXCEPTION_CODE` bigint(20) DEFAULT NULL,
  `EXCEPTION_MSG` varchar(100) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER_NAME` varchar(50) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER_NAME` varchar(50) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PK_SORTING_EC` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3839 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sorting_ret`
--

DROP TABLE IF EXISTS `sorting_ret`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sorting_ret` (
  `SORTING_RET_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `WAYBILL_CODE` varchar(50) DEFAULT NULL,
  `PACKAGE_CODE` varchar(50) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_USER_NAME` varchar(16) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `RET_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `RET_TYPE` bigint(20) DEFAULT NULL,
  `SHIELDS_ERROR` varchar(30) DEFAULT NULL,
  `SHIELDS_TYPE` bigint(20) DEFAULT NULL,
  `EXCUTE_COUNT` bigint(20) DEFAULT NULL,
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `ts` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '数据库时间',
  PRIMARY KEY (`SORTING_RET_ID`),
  UNIQUE KEY `PK_SORTING_RET` (`SORTING_RET_ID`),
  KEY `IND_SORTING_RET_PCODE` (`PACKAGE_CODE`),
  KEY `IND_SORTING_RET_WCODE` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=200038224 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spare`
--

DROP TABLE IF EXISTS `spare`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spare` (
  `SPARE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SPARE_CODE` varchar(32) DEFAULT NULL,
  `SPARE_STATUS` bigint(20) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `TIMES` bigint(20) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(32) DEFAULT NULL,
  `SPARE_TYPE` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`SPARE_ID`),
  UNIQUE KEY `SPARE_PK` (`SPARE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=147868 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spare_sale`
--

DROP TABLE IF EXISTS `spare_sale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spare_sale` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SPARE_CODE` varchar(32) DEFAULT NULL,
  `PRODUCT_ID` bigint(20) DEFAULT NULL,
  `PRODUCT_NAME` varchar(512) DEFAULT NULL,
  `SALE_TIME` datetime DEFAULT NULL,
  `SALE_AMOUNT` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PK_SPARE_SALE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sysconfig`
--

DROP TABLE IF EXISTS `sysconfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysconfig` (
  `CONFIG_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CONFIG_TYPE` bigint(20) DEFAULT NULL,
  `CONFIG_NAME` varchar(200) DEFAULT NULL,
  `CONFIG_CONTENT` varchar(5000) DEFAULT NULL COMMENT '配置字段值',
  `CONFIG_ORDER` bigint(20) DEFAULT NULL,
  `MEMO` varchar(200) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`CONFIG_ID`),
  UNIQUE KEY `PK_SYSCONFIG` (`CONFIG_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6210 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_log`
--

DROP TABLE IF EXISTS `system_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_log` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `KEYWORD1` varchar(32) DEFAULT NULL,
  `KEYWORD2` varchar(32) DEFAULT NULL,
  `KEYWORD3` varchar(64) DEFAULT NULL,
  `KEYWORD4` bigint(20) DEFAULT NULL,
  `CONTENT` varchar(4000) DEFAULT NULL,
  `TYPE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=728278 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_boundary`
--

DROP TABLE IF EXISTS `task_boundary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_boundary` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词2',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '数据内容',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OWN_SIGN` varchar(50) DEFAULT 'DMS' COMMENT '部署环境',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `PK_TASK_BOUNDARY` (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='对外服务相关任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_crossbox`
--

DROP TABLE IF EXISTS `task_crossbox`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_crossbox` (
  `TASK_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT 'KEYWORD1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT 'KEYWORD2',
  `CREATE_SITE_CODE` int(11) DEFAULT NULL COMMENT '创建站点ID',
  `RECEIVE_SITE_CODE` int(11) DEFAULT NULL COMMENT '接收站点ID',
  `BOX_CODE` varchar(64) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(1000) DEFAULT NULL COMMENT '主体',
  `EXECUTE_COUNT` int(11) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` int(11) DEFAULT NULL COMMENT '任务类型',
  `TASK_STATUS` int(11) DEFAULT NULL COMMENT '任务状态',
  `OWN_SIGN` varchar(32) DEFAULT NULL COMMENT 'OWN_SIGN',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '执行时间',
  `YN` tinyint(4) DEFAULT NULL COMMENT '有效标示',
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='跨中转箱号任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_failqueue`
--

DROP TABLE IF EXISTS `task_failqueue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_failqueue` (
  `FAILQUEUE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `BUSI_ID` bigint(20) DEFAULT NULL COMMENT '业务主键',
  `BUSI_TYPE` bigint(20) DEFAULT NULL COMMENT '业务类型',
  `BODY` varchar(1000) DEFAULT NULL COMMENT 'JSON序列内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL COMMENT '删除标志',
  `FAIL_STATUS` bigint(20) DEFAULT NULL COMMENT '失败状态',
  `EXCUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `EXCUTE_TIME` datetime DEFAULT NULL COMMENT '执行时间',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`FAILQUEUE_ID`),
  KEY `IND_TASK_FAILQUEUE_BID_P` (`BUSI_ID`),
  KEY `IND_TASK_FAILQUEUE_FS_P` (`FAIL_STATUS`),
  KEY `IND_TASK_FAILQUEUE_FS` (`FAIL_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=9118596 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_global_trade`
--

DROP TABLE IF EXISTS `task_global_trade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_global_trade` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(64) DEFAULT NULL,
  `BODY` varchar(1000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_handover`
--

DROP TABLE IF EXISTS `task_handover`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_handover` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '分拣中心编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(64) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(1000) DEFAULT NULL COMMENT '数据内容',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OWN_SIGN` varchar(50) DEFAULT NULL COMMENT '部署环境',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `UNQ_TASK_HANDOVER` (`TASK_ID`,`CREATE_TIME`),
  KEY `IND_TASK_HANDOVER_FP` (`FINGERPRINT`),
  KEY `IND_TASK_HANDOVER_KWORD` (`KEYWORD2`),
  KEY `IND_TASK_HANDOVER_STAT` (`TASK_STATUS`),
  KEY `IND_TASK_HANDOVER_STATUS` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=5276310 DEFAULT CHARSET=utf8 COMMENT='分拣相关任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_inspection`
--

DROP TABLE IF EXISTS `task_inspection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_inspection` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` varchar(16) DEFAULT NULL,
  `BODY` varchar(2000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`),
  KEY `IND_TASK_INSPECTION_FP_P` (`FINGERPRINT`),
  KEY `IND_TASK_INSPECTION_STATUS_P` (`TASK_STATUS`),
  KEY `IND_TASK_INSPECTION_STATUS` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=60154839 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_message`
--

DROP TABLE IF EXISTS `task_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_message` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT 'keyword1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT 'keyword2',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建站点',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收站点',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '主体',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '任务类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '任务状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  `OWN_SIGN` varchar(50) DEFAULT 'DMS' COMMENT '域',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '执行时间',
  PRIMARY KEY (`TASK_ID`),
  KEY `IDX_TASK_STATUS` (`TASK_STATUS`),
  KEY `IDX_FINGERPRINT` (`FINGERPRINT`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8 COMMENT='记录异步消息信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_offline`
--

DROP TABLE IF EXISTS `task_offline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_offline` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词2',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '数据内容',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `OWN_SIGN` varchar(50) DEFAULT NULL COMMENT '部署环境',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '执行时间',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '防重标识',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `UNQ_TASK_OFFLINE_ID` (`TASK_ID`,`CREATE_TIME`),
  KEY `IND_TASK_OFFLINE_STAT` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=492317 DEFAULT CHARSET=utf8 COMMENT='离线任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_pda`
--

DROP TABLE IF EXISTS `task_pda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_pda` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` varchar(2000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_pop`
--

DROP TABLE IF EXISTS `task_pop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_pop` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` varchar(2000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `EXECUTE_TIME` datetime DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`),
  KEY `IND_TASK_POP_KEYWORD1` (`KEYWORD1`),
  KEY `IND_TASK_INSPECTION_STATUS` (`TASK_STATUS`),
  KEY `IND_TASK_POP_STATUS` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=23546628 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_pop_recieve_count`
--

DROP TABLE IF EXISTS `task_pop_recieve_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_pop_recieve_count` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL COMMENT '订单号',
  `THIRD_WAYBILL_CODE` varchar(128) DEFAULT NULL COMMENT '三方运单号',
  `EXPRESS_CODE` varchar(32) DEFAULT NULL COMMENT '快递公司编号',
  `EXPRESS_NAME` varchar(64) DEFAULT NULL COMMENT '快递公司名称',
  `ACTUAL_NUM` bigint(20) DEFAULT NULL COMMENT '实收数量',
  `OPERATE_TIME` datetime DEFAULT NULL COMMENT '收货时间',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '回传时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `YN` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '任务状态',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `OWN_SIGN` varchar(50) DEFAULT NULL COMMENT '部署环境',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `PK_TASK_POP_RECIEVE_COUNT` (`TASK_ID`),
  KEY `IND_TASK_POP_RE_CNT_STAT` (`OWN_SIGN`,`TASK_STATUS`),
  KEY `IND_TASK_POP_RE_CNT_WCODE` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_receive_exception`
--

DROP TABLE IF EXISTS `task_receive_exception`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_receive_exception` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '数据内容',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OWN_SIGN` varchar(50) DEFAULT 'DMS' COMMENT '部署环境',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `PK_TASK_RECEIVE_EXCEPTION` (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='接货中心异常相关任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_receive_pickup`
--

DROP TABLE IF EXISTS `task_receive_pickup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_receive_pickup` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '数据内容',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OWN_SIGN` varchar(50) DEFAULT 'DMS' COMMENT '部署环境',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `PK_TASK_RECEIVE_PICKUP` (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='接货相关任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_receive_receive`
--

DROP TABLE IF EXISTS `task_receive_receive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_receive_receive` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '数据内容',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OWN_SIGN` varchar(50) DEFAULT 'DMS' COMMENT '部署环境',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `PK_TASK_RECEIVE_RECEIVE` (`TASK_ID`),
  KEY `IND_TASK_RECEIVE_RECEIVE_FS_P` (`TASK_STATUS`,`TASK_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收货验货相关任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_reverse`
--

DROP TABLE IF EXISTS `task_reverse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_reverse` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '创建单位编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '数据内容',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OWN_SIGN` varchar(50) DEFAULT 'DMS' COMMENT '部署环境',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`TASK_ID`),
  KEY `IND_TASK_REVERSE_TS_P` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=160866 DEFAULT CHARSET=utf8 COMMENT='运单状态相关任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_scanner_frame`
--

DROP TABLE IF EXISTS `task_scanner_frame`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_scanner_frame` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` varchar(2000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=673228 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_send`
--

DROP TABLE IF EXISTS `task_send`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_send` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(50) DEFAULT NULL,
  `BODY` varchar(2000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`),
  KEY `IND_TASK_SEND_FP_P` (`FINGERPRINT`),
  KEY `IND_TASK_SEND_TS_P` (`TASK_STATUS`),
  KEY `IND_TASK_SEND_TS` (`TASK_STATUS`),
  KEY `idx_box_code` (`BOX_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=31918346 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_sorting`
--

DROP TABLE IF EXISTS `task_sorting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_sorting` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` varchar(2000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `EXECUTE_TIME` datetime DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`),
  KEY `IND_TASK_SORTING_BCODE_P` (`BOX_CODE`),
  KEY `IND_TASK_SORTING_FP_P` (`FINGERPRINT`),
  KEY `IND_TASK_SORTING_STATUS_P` (`TASK_STATUS`),
  KEY `IND_TASK_SORTING_STATUS` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=50335831 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_sorting_ec`
--

DROP TABLE IF EXISTS `task_sorting_ec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_sorting_ec` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '分拣中心编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '数据内容',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OWN_SIGN` varchar(50) DEFAULT 'DMS' COMMENT '部署环境',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `PK_TASK_SORTING_EC` (`TASK_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=284 DEFAULT CHARSET=utf8 COMMENT='分拣相关任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_waybill`
--

DROP TABLE IF EXISTS `task_waybill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_waybill` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` varchar(2000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`),
  KEY `IND_TASK_WAYBILL_FP_P` (`FINGERPRINT`),
  KEY `IND_TASK_WAYBILL_TS_P` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=160693700 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_weight`
--

DROP TABLE IF EXISTS `task_weight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_weight` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '全局唯一ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `KEYWORD1` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `KEYWORD2` varchar(64) DEFAULT NULL COMMENT '关键词1',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '分拣中心编号',
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL COMMENT '接收单位编号',
  `BOX_CODE` varchar(32) DEFAULT NULL COMMENT '箱号',
  `BODY` varchar(2000) DEFAULT NULL COMMENT '数据内容',
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL COMMENT '执行次数',
  `TASK_TYPE` bigint(20) DEFAULT NULL COMMENT '类型',
  `TASK_STATUS` bigint(20) DEFAULT NULL COMMENT '状态',
  `YN` bigint(20) DEFAULT NULL COMMENT '是否删除',
  `OWN_SIGN` varchar(50) DEFAULT 'DMS' COMMENT '部署环境',
  `FINGERPRINT` varchar(64) DEFAULT NULL COMMENT '信息指纹',
  `EXECUTE_TIME` datetime DEFAULT NULL COMMENT '下次执行时间',
  PRIMARY KEY (`TASK_ID`),
  UNIQUE KEY `UNQ_TASK_WEIGHT` (`TASK_ID`,`CREATE_TIME`),
  KEY `IND_TASK_WEIGHT_STAT` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=4275496 DEFAULT CHARSET=utf8 COMMENT='称重相关任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_weight1`
--

DROP TABLE IF EXISTS `task_weight1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_weight1` (
  `TASK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` varchar(2000) DEFAULT NULL,
  `EXECUTE_COUNT` bigint(20) DEFAULT NULL,
  `TASK_TYPE` bigint(20) DEFAULT NULL,
  `TASK_STATUS` bigint(20) DEFAULT NULL,
  `YN` bigint(20) DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `turnoverbox`
--

DROP TABLE IF EXISTS `turnoverbox`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turnoverbox` (
  `TURNOVERBOX_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `TURNOVERBOX_CODE` varchar(30) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` bigint(20) DEFAULT '1',
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL,
  `RECEIVE_SITE_NAME` varchar(64) DEFAULT NULL,
  `OPERATE_TYPE` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`TURNOVERBOX_ID`),
  UNIQUE KEY `PK_TURNOVERBOX` (`TURNOVERBOX_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=59542 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-10-20 11:04:31

CREATE TABLE TASK_DELIVERY_TO_FINANCE (
	`task_id` bigint (20) UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增id',
	`create_time` datetime DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime DEFAULT NULL COMMENT '更新时间',
	`keyword1` VARCHAR (64) DEFAULT NULL COMMENT '关键字1',
	`keyword2` VARCHAR (64) DEFAULT NULL COMMENT '关键字2',
	`create_site_code` bigint (20)  UNSIGNED DEFAULT NULL COMMENT '始发分拣中心',
	`receive_site_code` bigint (20) UNSIGNED DEFAULT NULL COMMENT '目的分拣中心',
	`box_code` VARCHAR (32) DEFAULT NULL COMMENT '箱号',
	`body` VARCHAR (2000) DEFAULT NULL COMMENT '任务主体',
	`execute_count` bigint (20)  UNSIGNED DEFAULT NULL COMMENT '执行次数',
	`task_type` bigint (20) UNSIGNED  DEFAULT NULL COMMENT '任务烈性',
	`task_status` bigint (20) UNSIGNED DEFAULT NULL COMMENT '任务状态',
	`yn` bigint (20) UNSIGNED DEFAULT NULL COMMENT '是否生效',
	`own_sign` VARCHAR (50) DEFAULT 'DMS' COMMENT '部署环境',
	`execute_time` datetime DEFAULT NULL COMMENT '执行时间',
	`fingerprint` VARCHAR (64) DEFAULT NULL COMMENT '指纹信息',
	PRIMARY KEY (`task_id`),
	KEY `idx_box_code` (`box_code`),
	KEY `idx_status_count` (`task_status`,`execute_count`)
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT="第三方发货数据推财务任务表";


CREATE TABLE TASK_DELIVERY_TO_FINANCE_BATCH (
	`task_id` bigint (20) UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增id',
	`create_time` datetime DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime DEFAULT NULL COMMENT '更新时间',
	`keyword1` VARCHAR (64) DEFAULT NULL COMMENT '关键字1',
	`keyword2` VARCHAR (64) DEFAULT NULL COMMENT '关键字2',
	`create_site_code` bigint (20)  UNSIGNED DEFAULT NULL COMMENT '始发分拣中心',
	`receive_site_code` bigint (20) UNSIGNED DEFAULT NULL COMMENT '目的分拣中心',
	`box_code` VARCHAR (32) DEFAULT NULL COMMENT '箱号',
	`body` VARCHAR (2000) DEFAULT NULL COMMENT '任务主体',
	`execute_count` bigint (20)  UNSIGNED DEFAULT NULL COMMENT '执行次数',
	`task_type` bigint (20) UNSIGNED  DEFAULT NULL COMMENT '任务烈性',
	`task_status` bigint (20) UNSIGNED DEFAULT NULL COMMENT '任务状态',
	`yn` bigint (20) UNSIGNED DEFAULT NULL COMMENT '是否生效',
	`own_sign` VARCHAR (50) DEFAULT 'DMS' COMMENT '部署环境',
	`execute_time` datetime DEFAULT NULL COMMENT '执行时间',
	`fingerprint` VARCHAR (64) DEFAULT NULL COMMENT '指纹信息',
	PRIMARY KEY (`task_id`),
	KEY `idx_status_count` (`task_status`,`execute_count`)
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT="第三方发货数据推财务批处理任务表";