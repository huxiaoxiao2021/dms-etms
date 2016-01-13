/*
SQLyog v10.2 
MySQL - 5.6.27 : Database - dms
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `abnormal_order` */

DROP TABLE IF EXISTS `abnormal_order`;

CREATE TABLE `abnormal_order` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `ORDER_ID` varchar(20) DEFAULT NULL,
  `ABNORMAL_CODE1` int(11) DEFAULT NULL,
  `ABNORMAL_REASON1` varchar(128) DEFAULT NULL,
  `ABNORMAL_CODE2` int(11) DEFAULT NULL,
  `ABNORMAL_REASON2` varchar(128) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER_ERP` varchar(32) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(128) DEFAULT NULL,
  `IS_CANCEL` tinyint(4) DEFAULT NULL,
  `MEMO` text,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=222 DEFAULT CHARSET=utf8;

/*Table structure for table `alert_config` */

DROP TABLE IF EXISTS `alert_config`;

CREATE TABLE `alert_config` (
  `ALERT_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ENABLED` tinyint(4) DEFAULT NULL,
  `THRESHOLD` double DEFAULT NULL,
  `EXECUTE_SQL` text,
  `PHONE` varchar(200) DEFAULT NULL,
  `PHONE_FORMAT` varchar(140) DEFAULT NULL,
  `EMAIL` text,
  `EMAIL_FORMAT` text,
  `YN` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(128) DEFAULT NULL,
  `CREATE_USER_CODE` double DEFAULT NULL,
  `UPDATE_USER` varchar(128) DEFAULT NULL,
  `UPDATE_USER_CODE` double DEFAULT NULL,
  `ALERT_NAME` varchar(100) DEFAULT NULL,
  `MEMO` text,
  PRIMARY KEY (`ALERT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `batch_send` */

DROP TABLE IF EXISTS `batch_send`;

CREATE TABLE `batch_send` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BATCH_CODE` varchar(50) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_USER` varchar(50) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `SEND_STATUS` tinyint(4) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `SEND_CAR_STATE` tinyint(4) DEFAULT NULL,
  `SEND_CAR_OPERATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDX_BATCHSEND_BCRSC` (`BATCH_CODE`,`RECEIVE_SITE_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8;

/*Table structure for table `batchinfo` */

DROP TABLE IF EXISTS `batchinfo`;

CREATE TABLE `batchinfo` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BATCH_CODE` varchar(50) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_USER` varchar(50) DEFAULT NULL,
  `UPDATE_USER_CODE` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

/*Table structure for table `box` */

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
  `TIMES` int(10) unsigned DEFAULT NULL,
  `BOX_STATUS` tinyint(4) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `TRANSPORT_TYPE` tinyint(4) DEFAULT NULL,
  `MIX_BOX_TYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`BOX_ID`),
  KEY `IND_BOX_BC` (`BOX_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cen_confirm` */

DROP TABLE IF EXISTS `cen_confirm`;

CREATE TABLE `cen_confirm` (
  `CONFIRM_ID` bigint(20) unsigned NOT NULL,
  `SEND_CODE` varchar(30) DEFAULT NULL,
  `WAYBILL_CODE` varchar(30) DEFAULT NULL,
  `BOX_CODE` varchar(30) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL,
  `CONFIRM_TYPE` tinyint(4) DEFAULT NULL,
  `RECEIVE_USER` varchar(32) DEFAULT NULL,
  `RECEIVE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(3) unsigned DEFAULT '1',
  `CONFIRM_STATUS` tinyint(3) unsigned DEFAULT '0',
  `THIRD_WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `INSPECTION_USER` varchar(32) DEFAULT NULL,
  `INSPECTION_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `INSPECTION_TIME` datetime DEFAULT NULL,
  `PICKUP_CODE` varchar(32) DEFAULT NULL,
  `OPERATE_TYPE` tinyint(3) unsigned DEFAULT NULL,
  `EXCUTE_COUNT` int(10) unsigned DEFAULT NULL,
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `RECEIVE_TIME` datetime DEFAULT NULL,
  `OPERATE_USER` varchar(32) DEFAULT NULL,
  `OPERATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`CONFIRM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `client_config` */

DROP TABLE IF EXISTS `client_config`;

CREATE TABLE `client_config` (
  `CONFIG_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SITE_CODE` varchar(20) DEFAULT NULL,
  `PROGRAM_TYPE` tinyint(3) unsigned DEFAULT NULL,
  `VERSION_CODE` varchar(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`CONFIG_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

/*Table structure for table `client_config_history` */

DROP TABLE IF EXISTS `client_config_history`;

CREATE TABLE `client_config_history` (
  `CONFIG_HISTORY_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SITE_CODE` varchar(20) DEFAULT NULL,
  `PROGRAM_TYPE` tinyint(3) unsigned DEFAULT NULL,
  `VERSION_CODE` varchar(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`CONFIG_HISTORY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `client_version` */

DROP TABLE IF EXISTS `client_version`;

CREATE TABLE `client_version` (
  `VERSION_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `VERSION_CODE` varchar(20) DEFAULT NULL,
  `VERSION_TYPE` tinyint(3) unsigned DEFAULT NULL,
  `DOWNLOAD_URL` text,
  `MEMO` text,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`VERSION_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

/*Table structure for table `cross_sorting` */

DROP TABLE IF EXISTS `cross_sorting`;

CREATE TABLE `cross_sorting` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ORG_ID` bigint(20) unsigned DEFAULT NULL,
  `CREATE_DMS_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_DMS_NAME` varchar(50) DEFAULT NULL,
  `DESTINATION_DMS_CODE` bigint(20) unsigned DEFAULT NULL,
  `DESTINATION_DMS_NAME` varchar(50) DEFAULT NULL,
  `MIX_DMS_CODE` bigint(20) unsigned DEFAULT NULL,
  `MIX_DMS_NAME` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_USER_NAME` varchar(50) DEFAULT NULL,
  `DELETE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `DELETE_USER_NAME` varchar(50) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `DELETE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=705 DEFAULT CHARSET=utf8;

/*Table structure for table `db_schedule` */

DROP TABLE IF EXISTS `db_schedule`;

CREATE TABLE `db_schedule` (
  `TABLE_NAME` varchar(50) NOT NULL,
  `MAX_ID` double NOT NULL,
  `SCHEDULE_STATUS` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dbs_objectid` */

DROP TABLE IF EXISTS `dbs_objectid`;

CREATE TABLE `dbs_objectid` (
  `ID` double NOT NULL,
  `OBJECTNAME` varchar(255) DEFAULT NULL,
  `FIRSTID` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `departure_car` */

DROP TABLE IF EXISTS `departure_car`;

CREATE TABLE `departure_car` (
  `DEPARTURE_CAR_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CAR_CODE` varchar(30) DEFAULT NULL,
  `SHIELDS_CAR_CODE` varchar(30) DEFAULT NULL,
  `SEND_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `SEND_USER` varchar(50) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `WEIGHT` double DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `YN` tinyint(3) unsigned DEFAULT '1',
  `SEND_USER_TYPE` tinyint(3) unsigned DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `OLD_CAR_CODE` varchar(30) DEFAULT NULL,
  `DEPART_TYPE` tinyint(3) unsigned DEFAULT '1',
  `RUN_NUMBER` int(10) unsigned DEFAULT NULL,
  `RECEIVE_SITE_CODES` text,
  `PRINT_TIME` datetime DEFAULT NULL,
  `CAPACITY_CODE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`DEPARTURE_CAR_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=232 DEFAULT CHARSET=utf8;

/*Table structure for table `departure_log_bk` */

DROP TABLE IF EXISTS `departure_log_bk`;

CREATE TABLE `departure_log_bk` (
  `DEPARTURE_LOG_ID` double NOT NULL,
  `DISTRIBUTE_CODE` double DEFAULT NULL,
  `DISTRIBUTE_NAME` varchar(50) DEFAULT NULL,
  `OPERATOR_CODE` double DEFAULT NULL,
  `OPERATOR_NAME` varchar(50) DEFAULT NULL,
  `DEPARTURE_TIME` datetime DEFAULT NULL,
  `RECEIVE_TIME` datetime DEFAULT NULL,
  `DEPARTURE_CAR_ID` double DEFAULT NULL,
  `CAPACITY_CODE` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`DEPARTURE_LOG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `departure_send` */

DROP TABLE IF EXISTS `departure_send`;

CREATE TABLE `departure_send` (
  `DEPARTURE_SEND_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `DEPARTURE_CAR_ID` bigint(20) unsigned DEFAULT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT '1',
  `THIRD_WAYBILL_CODE` varchar(50) DEFAULT NULL,
  `CAPACITY_CODE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`DEPARTURE_SEND_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Table structure for table `departure_tmp` */

DROP TABLE IF EXISTS `departure_tmp`;

CREATE TABLE `departure_tmp` (
  `DEPARTURE_TMP_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(3) unsigned DEFAULT NULL,
  `BATCH_CODE` varchar(50) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `THIRD_WAYBILL_CODE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`DEPARTURE_TMP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2053 DEFAULT CHARSET=utf8;

/*Table structure for table `fbarcode` */

DROP TABLE IF EXISTS `fbarcode`;

CREATE TABLE `fbarcode` (
  `FBARCODE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `FBARCODE_CODE` varchar(16) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(100) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `TIMES` int(10) unsigned DEFAULT NULL,
  `FBARCODE_STATUS` tinyint(4) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`FBARCODE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Table structure for table `inspection` */

DROP TABLE IF EXISTS `inspection`;

CREATE TABLE `inspection` (
  `INSPECTION_ID` bigint(20) unsigned NOT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL,
  `INSPECTION_STATUS` tinyint(4) DEFAULT NULL,
  `EXCEPTION_TYPE` varchar(50) DEFAULT NULL,
  `INSPECTION_TYPE` tinyint(4) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` date DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(32) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` date DEFAULT NULL,
  `YN` tinyint(4) DEFAULT '1',
  `THIRD_WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `POP_FLAG` tinyint(4) DEFAULT NULL,
  `POP_SUP_ID` bigint(20) DEFAULT NULL,
  `POP_SUP_NAME` varchar(128) DEFAULT NULL,
  `QUANTITY` double DEFAULT NULL,
  `CROSS_CODE` varchar(16) DEFAULT NULL,
  `WAYBILL_TYPE` tinyint(4) DEFAULT NULL,
  `OPERATE_TYPE` tinyint(4) DEFAULT NULL,
  `POP_RECEIVE_TYPE` tinyint(4) DEFAULT NULL,
  `QUEUE_NO` varchar(32) DEFAULT NULL,
  `DRIVER_CODE` varchar(32) DEFAULT NULL,
  `DRIVER_NAME` varchar(32) DEFAULT NULL,
  `BUSI_ID` bigint(20) DEFAULT NULL,
  `BUSI_NAME` varchar(32) DEFAULT NULL,
  `LENGTH` double DEFAULT NULL,
  `HIGH` double DEFAULT NULL,
  `WIDTH` double DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`INSPECTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `inspection_e_c` */

DROP TABLE IF EXISTS `inspection_e_c`;

CREATE TABLE `inspection_e_c` (
  `CHECK_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL,
  `INSPECTION_E_C_TYPE` tinyint(4) DEFAULT NULL,
  `EXCEPTION_STATUS` tinyint(4) DEFAULT NULL,
  `INSPECTION_TYPE` tinyint(4) DEFAULT NULL,
  `EXCEPTION_TYPE` varchar(50) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) unsigned DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_USER` varchar(32) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`CHECK_ID`),
  KEY `INSPECTION_E_C_CI` (`CREATE_SITE_CODE`,`INSPECTION_E_C_TYPE`),
  KEY `INSPECTION_E_C_WC` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8;

/*Table structure for table `load_bill` */

DROP TABLE IF EXISTS `load_bill`;

CREATE TABLE `load_bill` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `LOAD_ID` varchar(50) DEFAULT NULL,
  `WAREHOUSE_ID` varchar(50) DEFAULT NULL,
  `WAYBILL_CODE` varchar(30) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL,
  `PACKAGE_AMOUNT` double DEFAULT NULL,
  `ORDER_ID` varchar(50) DEFAULT NULL,
  `BOX_CODE` varchar(50) DEFAULT NULL,
  `DMS_CODE` bigint(20) unsigned DEFAULT NULL,
  `DMS_NAME` varchar(50) DEFAULT NULL,
  `SEND_TIME` datetime DEFAULT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `TRUCK_NO` varchar(50) DEFAULT NULL,
  `APPROVAL_CODE` bigint(20) unsigned DEFAULT NULL,
  `APPROVAL_TIME` datetime DEFAULT NULL,
  `CTNO` varchar(50) DEFAULT NULL,
  `GJNO` varchar(50) DEFAULT NULL,
  `TPL` varchar(50) DEFAULT NULL,
  `WEIGHT` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `GEN_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `PACKAGE_USER` varchar(50) DEFAULT NULL,
  `PACKAGE_USER_CODE` bigint(20) DEFAULT NULL,
  `PACKAGE_TIME` datetime DEFAULT NULL,
  `REMARK` varchar(100) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_APPROVAL_CODE` (`APPROVAL_CODE`),
  KEY `IDX_BOX_CODE` (`BOX_CODE`),
  KEY `IDX_DMS_CODE` (`DMS_CODE`),
  KEY `IDX_LOADID_ORDERID_WAREHOUSEID` (`LOAD_ID`,`ORDER_ID`,`WAREHOUSE_ID`),
  KEY `IDX_PACKAGE_BARCODE` (`PACKAGE_BARCODE`),
  KEY `IDX_SEND_CODE` (`SEND_CODE`),
  KEY `IDX_SEND_TIME` (`SEND_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=27426 DEFAULT CHARSET=utf8;

/*Table structure for table `load_bill_report` */

DROP TABLE IF EXISTS `load_bill_report`;

CREATE TABLE `load_bill_report` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `REPORT_ID` varchar(50) DEFAULT NULL,
  `LOAD_ID` text,
  `WAREHOUSE_ID` varchar(50) DEFAULT NULL,
  `PROCESS_TIME` datetime DEFAULT NULL,
  `STATUS` tinyint(4) DEFAULT NULL,
  `NOTES` text,
  `ORDER_ID` text,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2986 DEFAULT CHARSET=utf8;

/*Table structure for table `loss_order` */

DROP TABLE IF EXISTS `loss_order`;

CREATE TABLE `loss_order` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ORDER_ID` bigint(20) DEFAULT NULL,
  `LOSS_TYPE` tinyint(4) DEFAULT NULL,
  `PRODUCT_ID` bigint(20) DEFAULT NULL,
  `PRODUCT_NAME` text,
  `PRODUCT_QUANTITY` smallint(6) DEFAULT NULL,
  `USER_ERP` varchar(43) DEFAULT NULL,
  `USER_NAME` varchar(64) DEFAULT NULL,
  `LOSS_QUANTITY` double DEFAULT NULL,
  `LOSS_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `LOSS_CODE` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=172 DEFAULT CHARSET=utf8;

/*Table structure for table `mc_message_task` */

DROP TABLE IF EXISTS `mc_message_task`;

CREATE TABLE `mc_message_task` (
  `ID` double NOT NULL,
  `QUEUE_ID` varchar(100) DEFAULT NULL,
  `OWNSGN` varchar(100) DEFAULT NULL,
  `MESSAGE_NAME` varchar(100) DEFAULT NULL,
  `PRODUCER_SYSTEMID` varchar(100) DEFAULT NULL,
  `DESTINATION_CODE` varchar(100) DEFAULT NULL,
  `DESTINATION_TYPE` varchar(5) DEFAULT NULL,
  `CONTENT` text,
  `BUSINESS_ID` varchar(100) DEFAULT NULL,
  `IS_SPLIT` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `SUCCESS` double DEFAULT NULL,
  `FAILED_COUNT` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_MC_MESSAGE_TASK_QUERY` (`QUEUE_ID`,`MESSAGE_NAME`,`BUSINESS_ID`,`SUCCESS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mc_message_task_part` */

DROP TABLE IF EXISTS `mc_message_task_part`;

CREATE TABLE `mc_message_task_part` (
  `ID` double NOT NULL,
  `CONTENT` text,
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_MESSAGE_TASK_PART_TASK_ID` (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `offline_log` */

DROP TABLE IF EXISTS `offline_log`;

CREATE TABLE `offline_log` (
  `OFFLINE_LOG_ID` double NOT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `PACKAGE_CODE` varchar(32) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BUSINESS_TYPE` double DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `TURNOVERBOX_CODE` varchar(30) DEFAULT NULL,
  `WEIGHT` double DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `EXCEPTION_TYPE` varchar(50) DEFAULT NULL,
  `SEND_CODE` varchar(30) DEFAULT NULL,
  `SEAL_BOX_CODE` varchar(32) DEFAULT NULL,
  `SEAL_VEHICLE_CODE` varchar(32) DEFAULT NULL,
  `VEHICLE_CODE` varchar(32) DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `SEND_USER` varchar(32) DEFAULT NULL,
  `SEND_USER_CODE` double DEFAULT NULL,
  `YN` double DEFAULT '1',
  `OPERATE_TYPE` double DEFAULT NULL,
  `STATUS` double DEFAULT NULL,
  PRIMARY KEY (`OFFLINE_LOG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `operation_log_0` */

DROP TABLE IF EXISTS `operation_log_0`;

CREATE TABLE `operation_log_0` (
  `LOG_ID` bigint NOT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `PICKUP_CODE` varchar(32) DEFAULT NULL,
  `PACKAGE_CODE` varchar(32) DEFAULT NULL,
  `LOG_TYPE` double DEFAULT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `CREATE_USER` varchar(128) DEFAULT NULL,
  `CREATE_USER_CODE` double DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_NAME` varchar(64) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `REMARK` text,
  PRIMARY KEY (`LOG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `partner_waybill` */

DROP TABLE IF EXISTS `partner_waybill`;

CREATE TABLE `partner_waybill` (
  `RELATION_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PARTNER_WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL,
  `PARTNER_STATUS` tinyint(4) DEFAULT '0',
  `PARTNER_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(32) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`RELATION_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=172 DEFAULT CHARSET=utf8;

/*Table structure for table `pick_ware` */

DROP TABLE IF EXISTS `pick_ware`;

CREATE TABLE `pick_ware` (
  `PICKWARE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ORG_ID` bigint(20) unsigned DEFAULT NULL,
  `ORDER_ID` bigint(20) unsigned DEFAULT NULL,
  `PACKAGE_CODE` varchar(32) DEFAULT NULL,
  `PICKWARE_CODE` varchar(32) DEFAULT NULL,
  `OPERATE_TYPE` tinyint(4) DEFAULT NULL,
  `OPERATOR` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `CAN_RECEIVE` tinyint(4) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`PICKWARE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8;

/*Table structure for table `pop_abnormal` */

DROP TABLE IF EXISTS `pop_abnormal`;

CREATE TABLE `pop_abnormal` (
  `ABNORMAL_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ORG_CODE` bigint(20) DEFAULT NULL,
  `ORG_NAME` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL,
  `MAIN_TYPE` int(10) unsigned DEFAULT NULL,
  `MAIN_TYPE_NAME` varchar(128) DEFAULT NULL,
  `SUB_TYPE` int(10) unsigned DEFAULT NULL,
  `SUB_TYPE_NAME` varchar(128) DEFAULT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `POP_SUP_NO` varchar(32) DEFAULT NULL,
  `POP_SUP_NAME` varchar(128) DEFAULT NULL,
  `ABNORMAL_STATUE` tinyint(4) DEFAULT NULL,
  `ATTR1` varchar(128) DEFAULT NULL,
  `ATTR2` varchar(128) DEFAULT NULL,
  `ATTR3` varchar(128) DEFAULT NULL,
  `ATTR4` text,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `OPERATOR_NAME` varchar(32) DEFAULT NULL,
  `WAYBILL_TYPE` tinyint(4) DEFAULT NULL,
  `ORDER_CODE` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ABNORMAL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;

/*Table structure for table `pop_abnormal_detail` */

DROP TABLE IF EXISTS `pop_abnormal_detail`;

CREATE TABLE `pop_abnormal_detail` (
  `ABNORMAL_DETAIL_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ABNORMAL_ID` bigint(20) unsigned DEFAULT NULL,
  `OPERATOR_CODE` bigint(20) unsigned DEFAULT NULL,
  `OPERATOR_NAME` varchar(32) DEFAULT NULL,
  `OPERATOR_TIME` datetime DEFAULT NULL,
  `REMARK` text,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`ABNORMAL_DETAIL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;

/*Table structure for table `pop_abnormal_order` */

DROP TABLE IF EXISTS `pop_abnormal_order`;

CREATE TABLE `pop_abnormal_order` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SERIAL_NUMBER` varchar(32) NOT NULL,
  `ABNORMAL_TYPE` tinyint(4) NOT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `ORDER_CODE` varchar(32) DEFAULT NULL,
  `POP_SUP_NO` varchar(32) DEFAULT NULL,
  `CURRENT_NUM` int(11) NOT NULL,
  `ACTUAL_NUM` int(11) NOT NULL,
  `CONFIRM_NUM` int(11) DEFAULT NULL,
  `OPERATOR_CODE` bigint(20) DEFAULT NULL,
  `OPERATOR_NAME` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CONFIRM_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `ABNORMAL_STATE` tinyint(4) NOT NULL,
  `MEMO` text,
  `RSV1` varchar(64) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8;

/*Table structure for table `pop_pickup` */

DROP TABLE IF EXISTS `pop_pickup`;

CREATE TABLE `pop_pickup` (
  `PICKUP_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL,
  `PICKUP_STATUS` tinyint(4) DEFAULT NULL,
  `PICKUP_TYPE` tinyint(4) DEFAULT NULL,
  `PACKAGE_NUMBER` int(11) DEFAULT NULL,
  `POP_BUSINESS_CODE` varchar(128) DEFAULT NULL,
  `POP_BUSINESS_NAME` varchar(128) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(32) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT '1',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CAR_CODE` varchar(64) DEFAULT NULL,
  `WAYBILL_TYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`PICKUP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2588 DEFAULT CHARSET=utf8;

/*Table structure for table `pop_print` */

DROP TABLE IF EXISTS `pop_print`;

CREATE TABLE `pop_print` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `PRINT_PACK_CODE` bigint(20) DEFAULT NULL,
  `PRINT_PACK_TIME` datetime DEFAULT NULL,
  `PRINT_INVOICE_CODE` bigint(20) DEFAULT NULL,
  `PRINT_INVOICE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `POP_SUP_ID` bigint(20) DEFAULT NULL,
  `POP_SUP_NAME` varchar(128) DEFAULT NULL,
  `PRINT_PACK_USER` varchar(32) DEFAULT NULL,
  `PRINT_INVOICE_USER` varchar(32) DEFAULT NULL,
  `QUANTITY` int(11) DEFAULT NULL,
  `CROSS_CODE` varchar(16) DEFAULT NULL,
  `WAYBILL_TYPE` int(11) DEFAULT NULL,
  `POP_RECEIVE_TYPE` tinyint(4) DEFAULT NULL,
  `PRINT_COUNT` int(11) DEFAULT NULL,
  `THIRD_WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `QUEUE_NO` varchar(32) DEFAULT NULL,
  `OPERATE_TYPE` int(11) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `DRIVER_CODE` varchar(32) DEFAULT NULL,
  `DRIVER_NAME` varchar(32) DEFAULT NULL,
  `BUSI_ID` int(11) DEFAULT NULL,
  `BUSI_NAME` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IND_POP_PRINT_WC` (`WAYBILL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=527 DEFAULT CHARSET=utf8;

/*Table structure for table `pop_queue` */

DROP TABLE IF EXISTS `pop_queue`;

CREATE TABLE `pop_queue` (
  `ID` double NOT NULL,
  `QUEUE_NO` varchar(32) NOT NULL,
  `CREATE_SITE_CODE` double NOT NULL,
  `CREATE_SITE_NAME` varchar(32) DEFAULT NULL,
  `QUEUE_TYPE` double DEFAULT NULL,
  `EXPRESS_CODE` varchar(32) DEFAULT NULL,
  `EXPRESS_NAME` varchar(64) DEFAULT NULL,
  `CREATE_USER_CODE` double DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `QUEUE_STATUS` double DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `WAIT_NO` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `pop_receive` */

DROP TABLE IF EXISTS `pop_receive`;

CREATE TABLE `pop_receive` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `RECEIVE_TYPE` tinyint(4) DEFAULT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `THIRD_WAYBILL_CODE` varchar(128) DEFAULT NULL,
  `ORIGINAL_NUM` int(11) DEFAULT NULL,
  `ACTUAL_NUM` int(11) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL,
  `OPERATOR_CODE` bigint(20) DEFAULT NULL,
  `OPERATOR_NAME` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `IS_REVERSE` tinyint(4) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Table structure for table `pop_signin` */

DROP TABLE IF EXISTS `pop_signin`;

CREATE TABLE `pop_signin` (
  `ID` double unsigned NOT NULL AUTO_INCREMENT,
  `QUEUE_NO` varchar(32) DEFAULT NULL,
  `THIRD_WAYBILL_CODE` varchar(32) NOT NULL,
  `CREATE_USER_CODE` double DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `EXPRESS_CODE` varchar(32) DEFAULT NULL,
  `EXPRESS_NAME` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=206 DEFAULT CHARSET=utf8;

/*Table structure for table `reassign_waybill` */

DROP TABLE IF EXISTS `reassign_waybill`;

CREATE TABLE `reassign_waybill` (
  `REASSIGN_WAYBILL_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `PACKAGE_BARCODE` varchar(32) DEFAULT NULL,
  `ADDRESS` text,
  `RECEIVE_SITE_NAME` varchar(64) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CHANGE_SITE_NAME` varchar(64) DEFAULT NULL,
  `CHANGE_SITE_CODE` bigint(20) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(16) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `WAYBILL_CODE` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`REASSIGN_WAYBILL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8;

/*Table structure for table `receive` */

DROP TABLE IF EXISTS `receive`;

CREATE TABLE `receive` (
  `RECEIVE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `WAYBILL_CODE` varchar(30) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL,
  `BOX_CODE` varchar(30) DEFAULT NULL,
  `RECEIVE_TYPE` int(11) DEFAULT NULL,
  `BOXING_TYPE` int(11) DEFAULT NULL,
  `RECEIVE_STATUS` tinyint(4) DEFAULT '0',
  `TASK_EXE_COUNT` int(11) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` date DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` date DEFAULT NULL,
  `YN` tinyint(4) DEFAULT '1',
  `TURNOVERBOX_CODE` varchar(30) DEFAULT NULL,
  `QUEUENO` varchar(64) DEFAULT NULL,
  `DEPARTURE_CAR_ID` bigint(20) DEFAULT NULL,
  `SHIELDS_CAR_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`RECEIVE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;

/*Table structure for table `reverse_label` */

DROP TABLE IF EXISTS `reverse_label`;

CREATE TABLE `reverse_label` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `LABEL_TYPE` int(11) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(100) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_NAME` varchar(100) DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `PRINT_NUM` int(11) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `OPERATOR_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

/*Table structure for table `reverse_receive` */

DROP TABLE IF EXISTS `reverse_receive`;

CREATE TABLE `reverse_receive` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `FINGERPRINT` varchar(60) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `OPERATOR` varchar(32) DEFAULT NULL,
  `CAN_RECEIVE` tinyint(4) DEFAULT NULL,
  `REJECT_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `REJECT_MESSAGE` text,
  `YN` tinyint(4) DEFAULT NULL,
  `OPERATOR_CODE` varchar(32) DEFAULT NULL,
  `SEND_CODE` varchar(30) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL,
  `BUSINESS_TYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8;

/*Table structure for table `reverse_reject` */

DROP TABLE IF EXISTS `reverse_reject`;

CREATE TABLE `reverse_reject` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CKY2` bigint(20) DEFAULT NULL,
  `STORE_ID` bigint(20) DEFAULT NULL,
  `ORDER_ID` varchar(30) DEFAULT NULL,
  `PACKAGE_CODE` varchar(64) DEFAULT NULL,
  `OPERATOR_CODE` varchar(32) DEFAULT NULL,
  `OPERATOR` varchar(32) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `BUSINESS_TYPE` int(11) DEFAULT NULL,
  `INSPECTOR` varchar(32) DEFAULT NULL,
  `INSPECT_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `INSPECTOR_CODE` varchar(32) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(32) DEFAULT NULL,
  `PICKWARE_CODE` varchar(32) DEFAULT NULL,
  `ORG_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=402 DEFAULT CHARSET=utf8;

/*Table structure for table `reverse_spare` */

DROP TABLE IF EXISTS `reverse_spare`;

CREATE TABLE `reverse_spare` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SPARE_CODE` varchar(32) DEFAULT NULL,
  `SEND_CODE` varchar(30) DEFAULT NULL,
  `WAYBILL_CODE` varchar(30) DEFAULT NULL,
  `PRODUCT_ID` varchar(32) DEFAULT NULL,
  `PRODUCT_CODE` varchar(32) DEFAULT NULL,
  `PRODUCT_NAME` text,
  `ARRT_CODE1` int(11) DEFAULT NULL,
  `ARRT_DESC1` varchar(32) DEFAULT NULL,
  `ARRT_CODE2` int(11) DEFAULT NULL,
  `ARRT_DESC2` varchar(32) DEFAULT NULL,
  `ARRT_CODE3` int(11) DEFAULT NULL,
  `ARRT_DESC3` varchar(32) DEFAULT NULL,
  `ARRT_CODE4` int(11) DEFAULT NULL,
  `ARRT_DESC4` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `PRODUCT_PRICE` double DEFAULT NULL,
  `SPARE_TRAN_CODE` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=288 DEFAULT CHARSET=utf8;

/*Table structure for table `seal_box` */

DROP TABLE IF EXISTS `seal_box`;

CREATE TABLE `seal_box` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `SEAL_CODE` varchar(32) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(16) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_ID`),
  KEY `IND_SEAL_BOX_SC` (`SEAL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Table structure for table `seal_vehicle` */

DROP TABLE IF EXISTS `seal_vehicle`;

CREATE TABLE `seal_vehicle` (
  `SYSTEM_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `VEHICLE_CODE` varchar(32) DEFAULT NULL,
  `SEAL_CODE` varchar(32) DEFAULT NULL,
  `DRIVER_CODE` varchar(16) DEFAULT NULL,
  `DRIVER` varchar(50) DEFAULT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `WEIGHT` double DEFAULT NULL,
  `PACKAGE_NUM` int(11) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(50) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` double DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_ID`),
  KEY `IND_SEAL_VEHICLE_SC` (`SEAL_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=8273 DEFAULT CHARSET=utf8;

/*Table structure for table `send_barcode` */

DROP TABLE IF EXISTS `send_barcode`;

CREATE TABLE `send_barcode` (
  `SEND_BARCODE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SEND_BARCODE_CODE` varchar(50) DEFAULT NULL,
  `SEND_BARCODE_TYPE` varchar(2) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_NAME` varchar(100) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_NAME` varchar(100) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER_CODE` double DEFAULT NULL,
  `CREATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `TIMES` int(11) DEFAULT NULL,
  `SEND_BARCODE_STATUS` tinyint(4) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`SEND_BARCODE_ID`),
  KEY `IND_SEND_BARCODE_BC` (`SEND_BARCODE_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `send_d` */

DROP TABLE IF EXISTS `send_d`;

CREATE TABLE `send_d` (
  `SEND_D_ID` bigint(20) unsigned NOT NULL,
  `SEND_CODE` varchar(30) DEFAULT NULL,
  `BOX_CODE` varchar(30) DEFAULT NULL,
  `PACKAGE_BARCODE` varchar(30) DEFAULT NULL,
  `WAYBILL_CODE` varchar(30) DEFAULT NULL,
  `SEND_TYPE` tinyint(4) DEFAULT NULL,
  `SENDD_STATUS` tinyint(4) DEFAULT '0',
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` date DEFAULT NULL,
  `WEIGHT` double DEFAULT NULL,
  `YN` tinyint(4) DEFAULT '1',
  `IS_CANCEL` tinyint(4) DEFAULT '0',
  `PICKUP_CODE` varchar(30) DEFAULT NULL,
  `EXCUTE_COUNT` int(11) DEFAULT NULL,
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `TOFA_STATUS` tinyint(4) DEFAULT NULL,
  `PACKAGE_NUM` int(11) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `SPARE_REASON` varchar(128) DEFAULT NULL,
  `SPARE_TRAN_CODE` varchar(32) DEFAULT NULL,
  `IS_LOSS` tinyint(4) DEFAULT NULL,
  `FEATURE_TYPE` int(11) DEFAULT NULL,
  `WH_REVERSE` double DEFAULT NULL,
  PRIMARY KEY (`SEND_D_ID`),
  KEY `IND_SEND_D_BCPB` (`BOX_CODE`,`PACKAGE_BARCODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `send_m` */

DROP TABLE IF EXISTS `send_m`;

CREATE TABLE `send_m` (
  `SEND_M_ID` bigint(20) unsigned NOT NULL,
  `SEND_CODE` varchar(50) DEFAULT NULL,
  `BOX_CODE` varchar(50) DEFAULT NULL,
  `SEND_USER` varchar(50) DEFAULT NULL,
  `SEND_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `SEND_TYPE` int(11) DEFAULT NULL,
  `CAR_CODE` varchar(50) DEFAULT NULL,
  `CREATE_USER` varchar(50) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATER_USER` varchar(50) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `SHIELDS_CAR_ID` bigint(20) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT '1',
  `SENDM_STATUS` tinyint(4) DEFAULT '0',
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `EXCUTE_COUNT` int(11) DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `TURNOVERBOX_CODE` varchar(30) DEFAULT NULL,
  `TRANSPORT_TYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`SEND_M_ID`),
  KEY `IND_BOX_CODE` (`BOX_CODE`),
  KEY `IND_SEND_M_SC` (`SEND_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `send_query` */

DROP TABLE IF EXISTS `send_query`;

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
  `YN` tinyint(4) DEFAULT NULL,
  `IP_ADDRESS` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IND_SEND_QUERY_SEND_CODE` (`SEND_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=153160 DEFAULT CHARSET=utf8;

/*Table structure for table `shields_error` */

DROP TABLE IF EXISTS `shields_error`;

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
  `YN` tinyint(4) DEFAULT '1',
  `BUSINESS_TYPE` tinyint(4) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ERROR_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;

/*Table structure for table `sorting` */

DROP TABLE IF EXISTS `sorting`;

CREATE TABLE `sorting` (
  `SORTING_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `PACKAGE_CODE` varchar(32) DEFAULT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `SORTING_TYPE` int(11) DEFAULT NULL,
  `CREATE_SITE_CODE` bigint(20) DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(16) DEFAULT NULL,
  `CREATE_TIME` datetime NOT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(16) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `SORTING_STATUS` tinyint(4) DEFAULT NULL,
  `IS_CANCEL` tinyint(4) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `PICKUP_CODE` varchar(32) DEFAULT NULL,
  `EXCUTE_COUNT` int(11) DEFAULT NULL,
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `SPARE_REASON` varchar(128) DEFAULT NULL,
  `IS_LOSS` tinyint(4) DEFAULT NULL,
  `FEATURE_TYPE` int(11) DEFAULT NULL,
  `WH_REVERSE` double DEFAULT NULL,
  `BSEND_CODE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`SORTING_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3302 DEFAULT CHARSET=utf8;

/*Table structure for table `sorting_ec` */

DROP TABLE IF EXISTS `sorting_ec`;

CREATE TABLE `sorting_ec` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `RECEIVE_SITE_CODE` bigint(20) unsigned DEFAULT NULL,
  `BUSINESS_TYPE` int(11) DEFAULT NULL,
  `BOX_CODE` varchar(50) DEFAULT NULL,
  `PACKAGE_CODE` varchar(50) DEFAULT NULL,
  `EXCEPTION_CODE` tinyint(4) DEFAULT NULL,
  `EXCEPTION_MSG` varchar(100) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER_NAME` varchar(50) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER_NAME` varchar(50) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Table structure for table `sorting_ret` */

DROP TABLE IF EXISTS `sorting_ret`;

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
  `RET_STATUS` tinyint(4) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `RET_TYPE` int(11) DEFAULT NULL,
  `SHIELDS_ERROR` varchar(30) DEFAULT NULL,
  `SHIELDS_TYPE` int(11) DEFAULT NULL,
  `EXCUTE_COUNT` int(11) DEFAULT NULL,
  `EXCUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`SORTING_RET_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6111 DEFAULT CHARSET=utf8;

/*Table structure for table `spare` */

DROP TABLE IF EXISTS `spare`;

CREATE TABLE `spare` (
  `SPARE_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SPARE_CODE` varchar(32) DEFAULT NULL,
  `SPARE_STATUS` tinyint(4) DEFAULT NULL,
  `CREATE_USER_CODE` bigint(20) DEFAULT NULL,
  `CREATE_USER` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `TIMES` int(11) DEFAULT NULL,
  `UPDATE_USER_CODE` bigint(20) DEFAULT NULL,
  `UPDATE_USER` varchar(32) DEFAULT NULL,
  `SPARE_TYPE` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`SPARE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=297 DEFAULT CHARSET=utf8;

/*Table structure for table `spare_sale` */

DROP TABLE IF EXISTS `spare_sale`;

CREATE TABLE `spare_sale` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SPARE_CODE` varchar(32) DEFAULT NULL,
  `PRODUCT_ID` double DEFAULT NULL,
  `PRODUCT_NAME` text,
  `SALE_TIME` datetime DEFAULT NULL,
  `SALE_AMOUNT` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `sysconfig` */

DROP TABLE IF EXISTS `sysconfig`;

CREATE TABLE `sysconfig` (
  `CONFIG_ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CONFIG_TYPE` tinyint(4) DEFAULT NULL,
  `CONFIG_NAME` varchar(200) DEFAULT NULL,
  `CONFIG_CONTENT` varchar(64) DEFAULT NULL,
  `CONFIG_ORDER` int(11) DEFAULT NULL,
  `MEMO` varchar(200) DEFAULT NULL,
  `YN` tinyint(4) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`CONFIG_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8;

/*Table structure for table `system_log` */

DROP TABLE IF EXISTS `system_log`;

CREATE TABLE `system_log` (
  `ID` double NOT NULL DEFAULT '0',
  `KEYWORD1` varchar(32) DEFAULT NULL,
  `KEYWORD2` varchar(32) DEFAULT NULL,
  `KEYWORD3` varchar(64) DEFAULT NULL,
  `KEYWORD4` double DEFAULT NULL,
  `CONTENT` text,
  `TYPE` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `YN` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_boundary` */

DROP TABLE IF EXISTS `task_boundary`;

CREATE TABLE `task_boundary` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_failqueue` */

DROP TABLE IF EXISTS `task_failqueue`;

CREATE TABLE `task_failqueue` (
  `FAILQUEUE_ID` double NOT NULL,
  `BUSI_ID` double DEFAULT NULL,
  `BUSI_TYPE` double DEFAULT NULL,
  `BODY` text,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `FAIL_STATUS` double DEFAULT NULL,
  `EXCUTE_COUNT` double DEFAULT NULL,
  `EXCUTE_TIME` datetime DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`FAILQUEUE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_global_trade` */

DROP TABLE IF EXISTS `task_global_trade`;

CREATE TABLE `task_global_trade` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(64) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_handover` */

DROP TABLE IF EXISTS `task_handover`;

CREATE TABLE `task_handover` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(64) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_inspection` */

DROP TABLE IF EXISTS `task_inspection`;

CREATE TABLE `task_inspection` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` varchar(16) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_offline` */

DROP TABLE IF EXISTS `task_offline`;

CREATE TABLE `task_offline` (
  `TASK_ID` double NOT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `YN` double DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_pda` */

DROP TABLE IF EXISTS `task_pda`;

CREATE TABLE `task_pda` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_pop` */

DROP TABLE IF EXISTS `task_pop`;

CREATE TABLE `task_pop` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_pop_recieve_count` */

DROP TABLE IF EXISTS `task_pop_recieve_count`;

CREATE TABLE `task_pop_recieve_count` (
  `TASK_ID` double NOT NULL,
  `WAYBILL_CODE` varchar(32) DEFAULT NULL,
  `THIRD_WAYBILL_CODE` varchar(128) DEFAULT NULL,
  `EXPRESS_CODE` varchar(32) DEFAULT NULL,
  `EXPRESS_NAME` varchar(64) DEFAULT NULL,
  `ACTUAL_NUM` double DEFAULT NULL,
  `OPERATE_TIME` datetime DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_receive_exception` */

DROP TABLE IF EXISTS `task_receive_exception`;

CREATE TABLE `task_receive_exception` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_receive_pickup` */

DROP TABLE IF EXISTS `task_receive_pickup`;

CREATE TABLE `task_receive_pickup` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_receive_receive` */

DROP TABLE IF EXISTS `task_receive_receive`;

CREATE TABLE `task_receive_receive` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_reverse` */

DROP TABLE IF EXISTS `task_reverse`;

CREATE TABLE `task_reverse` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_send` */

DROP TABLE IF EXISTS `task_send`;

CREATE TABLE `task_send` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_sorting` */

DROP TABLE IF EXISTS `task_sorting`;

CREATE TABLE `task_sorting` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_sorting_ec` */

DROP TABLE IF EXISTS `task_sorting_ec`;

CREATE TABLE `task_sorting_ec` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_waybill` */

DROP TABLE IF EXISTS `task_waybill`;

CREATE TABLE `task_waybill` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT NULL,
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_weight` */

DROP TABLE IF EXISTS `task_weight`;

CREATE TABLE `task_weight` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_weight1` */

DROP TABLE IF EXISTS `task_weight1`;

CREATE TABLE `task_weight1` (
  `TASK_ID` double NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `KEYWORD1` varchar(64) DEFAULT NULL,
  `KEYWORD2` varchar(64) DEFAULT NULL,
  `CREATE_SITE_CODE` double DEFAULT NULL,
  `RECEIVE_SITE_CODE` double DEFAULT NULL,
  `BOX_CODE` varchar(32) DEFAULT NULL,
  `BODY` text,
  `EXECUTE_COUNT` double DEFAULT NULL,
  `TASK_TYPE` double DEFAULT NULL,
  `TASK_STATUS` double DEFAULT NULL,
  `YN` double DEFAULT NULL,
  `OWN_SIGN` varchar(50) DEFAULT 'DMS',
  `FINGERPRINT` varchar(64) DEFAULT NULL,
  `EXECUTE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `turnoverbox` */

DROP TABLE IF EXISTS `turnoverbox`;

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
  `YN` tinyint(4) DEFAULT '1',
  `CREATE_SITE_NAME` varchar(64) DEFAULT NULL,
  `RECEIVE_SITE_NAME` varchar(64) DEFAULT NULL,
  `OPERATE_TYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`TURNOVERBOX_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
