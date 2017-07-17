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
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8 COMMENT='跨中转箱号任务表';
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
  KEY `IDX_TASK_FAILQUEUE_BID_P` (`BUSI_ID`),
  KEY `IDX_TASK_FAILQUEUE_FS` (`FAIL_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='失败及综合任务表';
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
  KEY `IDX_TASK_HANDOVER_FP` (`FINGERPRINT`),
  KEY `IDX_TASK_HANDOVER_KWORD` (`KEYWORD2`),
  KEY `IDX_TASK_HANDOVER_STATUS` (`TASK_STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='分拣相关任务表';
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
  KEY `IDX_TASK_INSPECTION_FP_P` (`FINGERPRINT`),
  KEY `IDX_TASK_INSPECTION_STATUS` (`TASK_STATUS`),
  KEY `idx_task_inspection_statuscount` (`TASK_STATUS`,`EXECUTE_COUNT`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='验货任务表';
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
) ENGINE=InnoDB AUTO_INCREMENT=2798 DEFAULT CHARSET=utf8 COMMENT='记录异步消息信息';
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
) ENGINE=InnoDB AUTO_INCREMENT=492327 DEFAULT CHARSET=utf8 COMMENT='离线任务表';
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
  KEY `IND_TASK_POP_STATUS` (`TASK_STATUS`),
  KEY `idx_task_pop_statuscount` (`TASK_STATUS`,`EXECUTE_COUNT`)
) ENGINE=InnoDB AUTO_INCREMENT=256792799 DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=765165 DEFAULT CHARSET=utf8 COMMENT='运单状态相关任务表';
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
) ENGINE=InnoDB AUTO_INCREMENT=2773397 DEFAULT CHARSET=utf8;
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
  KEY `IND_TASK_SEND_TS` (`TASK_STATUS`),
  KEY `idx_box_code` (`BOX_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=32759312 DEFAULT CHARSET=utf8;
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
  KEY `IND_TASK_SORTING_STATUS` (`TASK_STATUS`),
  KEY `idx_task_sorting_statuscount` (`TASK_STATUS`,`EXECUTE_COUNT`)
) ENGINE=InnoDB AUTO_INCREMENT=130667216 DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=297 DEFAULT CHARSET=utf8 COMMENT='分拣相关任务表';
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
) ENGINE=InnoDB AUTO_INCREMENT=169360375 DEFAULT CHARSET=utf8;
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
  KEY `IND_TASK_WEIGHT_STAT` (`TASK_STATUS`),
  KEY `idx_status_count` (`TASK_STATUS`,`EXECUTE_COUNT`)
) ENGINE=InnoDB AUTO_INCREMENT=18175450 DEFAULT CHARSET=utf8 COMMENT='称重相关任务表';
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