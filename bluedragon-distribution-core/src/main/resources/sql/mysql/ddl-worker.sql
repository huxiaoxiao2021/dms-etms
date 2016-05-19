/*
SQLyog v10.2 
MySQL - 5.6.27 : Database - dms_worker
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `pamirs_schedule_queue` */

DROP TABLE IF EXISTS `pamirs_schedule_queue`;

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
) ENGINE=InnoDB AUTO_INCREMENT=1034 DEFAULT CHARSET=utf8;

/*Table structure for table `pamirs_schedule_server` */

DROP TABLE IF EXISTS `pamirs_schedule_server`;

CREATE TABLE `pamirs_schedule_server` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `UUID` varchar(100) NOT NULL,
  `TASK_TYPE` varchar(50) NOT NULL,
  `OWN_SIGN` varchar(50) NOT NULL,
  `BASE_TASK_TYPE` varchar(50) NOT NULL,
  `IP` varchar(50) NOT NULL,
  `HOST_NAME` varchar(50) NOT NULL,
  `MANAGER_PORT` int(11) NOT NULL,
  `THREAD_NUM` int(11) NOT NULL,
  `REGISTER_TIME` datetime NOT NULL,
  `HEARTBEAT_TIME` datetime NOT NULL,
  `VERSION` int(11) NOT NULL,
  `JMX_URL` varchar(200) DEFAULT NULL,
  `DEALINFO_DESC` text,
  `NEXT_RUN_START_TIME` varchar(100) DEFAULT NULL,
  `NEXT_RUN_END_TIME` varchar(100) DEFAULT NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IND_PAMIRS_SCHEDULE_UUID` (`UUID`)
) ENGINE=InnoDB AUTO_INCREMENT=1508018360 DEFAULT CHARSET=utf8;

/*Table structure for table `pamirs_schedule_server_his` */

DROP TABLE IF EXISTS `pamirs_schedule_server_his`;

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
  `gmt_modified` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `pamirs_schedule_tasktrun` */

DROP TABLE IF EXISTS `pamirs_schedule_tasktrun`;

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
) ENGINE=InnoDB AUTO_INCREMENT=3638717942 DEFAULT CHARSET=utf8;

/*Table structure for table `pamirs_schedule_tasktype` */

DROP TABLE IF EXISTS `pamirs_schedule_tasktype`;

CREATE TABLE `pamirs_schedule_tasktype` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `TASK_TYPE` varchar(100) NOT NULL,
  `DEAL_BEAN_NAME` varchar(100) NOT NULL,
  `HEARTBEAT_RATE` double NOT NULL,
  `JUDGE_DEAD_INTERVAL` double NOT NULL,
  `THREAD_NUMBER` int(11) NOT NULL,
  `EXECUTE_NUMBER` int(11) DEFAULT NULL,
  `FETCH_NUMBER` int(11) DEFAULT NULL,
  `SLEEP_TIME_NODATA` double DEFAULT NULL,
  `SLEEP_TIME_INTERVAL` double DEFAULT NULL,
  `PROCESSOR_TYPE` varchar(20) DEFAULT NULL,
  `PERMIT_RUN_START_TIME` varchar(100) DEFAULT NULL,
  `PERMIT_RUN_END_TIME` varchar(100) DEFAULT NULL,
  `LAST_ASSIGN_TIME` datetime DEFAULT NULL,
  `LAST_ASSIGN_UUID` varchar(100) DEFAULT NULL,
  `EXPIRE_OWN_SIGN_INTERVAL` double DEFAULT NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IND_PAMIRS_TASKTYPE_TASKTYPE` (`TASK_TYPE`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
