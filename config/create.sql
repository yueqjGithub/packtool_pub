/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.21 : Database - Avalon_Packer
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`Avalon_Packer` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `Avalon_Packer`;

/*Table structure for table `T_App` */

DROP TABLE IF EXISTS `T_App`;

CREATE TABLE `T_App` (
  `id` varchar(64) NOT NULL,
  `app_id` varchar(100) NOT NULL,
  `app_name` varchar(100) DEFAULT NULL,
  `screen_orientation` enum('portrait','landscape') DEFAULT NULL COMMENT 'portrait是竖屏 landscape是横屏',
  `source_path` varchar(255) DEFAULT NULL COMMENT '母包ftp路径',
  `sign_file_path` varchar(500) DEFAULT NULL,
  `sign_file_keystore_password` varchar(100) DEFAULT NULL,
  `sign_file_key_password` varchar(100) DEFAULT NULL,
  `sign_file_alias` varchar(100) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mother_ftp_paths` varchar(1024) DEFAULT NULL COMMENT 'App对应Ftp存放母包的路径',
  `version_code` int(11) DEFAULT NULL COMMENT 'version',
  PRIMARY KEY (`id`,`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Channel` */

DROP TABLE IF EXISTS `T_Channel`;

CREATE TABLE `T_Channel` (
  `id` varchar(64) NOT NULL,
  `channel_code` varchar(20) DEFAULT NULL,
  `channel_name` varchar(250) DEFAULT NULL,
  `public_Area` varchar(64) DEFAULT NULL,
  `server_config_doc` varchar(1024) DEFAULT NULL,
  `client_config_doc` varchar(1024) DEFAULT NULL,
  `version_num` varchar(10) DEFAULT NULL,
  `extra` varchar(1024) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `description` text COMMENT '渠道描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `channel_id` (`channel_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Channel_Media_Package` */

DROP TABLE IF EXISTS `T_Channel_Media_Package`;

CREATE TABLE `T_Channel_Media_Package` (
  `id` varchar(64) NOT NULL,
  `hr_id` varchar(64) NOT NULL,
  `package_name` varchar(500) DEFAULT NULL,
  `media_name` varchar(250) DEFAULT NULL,
  `down_url` varchar(500) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_History_Record` */

DROP TABLE IF EXISTS `T_History_Record`;

CREATE TABLE `T_History_Record` (
  `id` varchar(64) NOT NULL,
  `config_id` varchar(64) NOT NULL,
  `app_id` varchar(64) DEFAULT NULL,
  `app` varchar(200) DEFAULT NULL COMMENT 'App的简称',
  `channel_id` varchar(64) DEFAULT NULL,
  `channel_code` varchar(255) DEFAULT NULL,
  `channel_name` varchar(255) DEFAULT NULL,
  `channel_version` varchar(255) DEFAULT NULL,
  `supersdk_version` varchar(255) DEFAULT NULL,
  `mother_short_name` varchar(255) DEFAULT NULL,
  `mother_is_ftp` int(11) DEFAULT NULL,
  `mother_name` varchar(255) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `plugin_list` varchar(1000) DEFAULT NULL COMMENT '插件code集合',
  `package_name` varchar(255) DEFAULT NULL COMMENT 'http://aiki.avalongames.com:8090/pages/viewpage.action?pageId=62590016',
  `env_code` varchar(64) DEFAULT NULL,
  `build_num` int(255) DEFAULT NULL,
  `ops_user` varchar(300) DEFAULT NULL COMMENT '操作人',
  `env_desc` varchar(300) DEFAULT NULL COMMENT '打包环境',
  `pack_status` int(10) DEFAULT NULL COMMENT '打包状态,1-打包中,2-打包成功,3-打包失败',
  `reason` text COMMENT '失败原因',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Media_Flag` */

DROP TABLE IF EXISTS `T_Media_Flag`;

CREATE TABLE `T_Media_Flag` (
  `id` varchar(64) NOT NULL,
  `code` varchar(100) NOT NULL,
  `media_name` varchar(250) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`,`code`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Mother_Packages` */

DROP TABLE IF EXISTS `T_Mother_Packages`;

CREATE TABLE `T_Mother_Packages` (
  `id` varchar(64) NOT NULL,
  `app_id` varchar(250) NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `package_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Packer_Record` */

DROP TABLE IF EXISTS `T_Packer_Record`;

CREATE TABLE `T_Packer_Record` (
  `config_name` varchar(64) NOT NULL COMMENT '配置名称',
  `id` varchar(64) NOT NULL,
  `app_id` varchar(64) NOT NULL,
  `sign_file_alias` varchar(100) DEFAULT NULL,
  `sign_file_key_password` varchar(100) DEFAULT NULL,
  `sign_file_keystore_password` varchar(100) DEFAULT NULL,
  `sign_file_path` varchar(500) DEFAULT NULL,
  `public_area` varchar(64) DEFAULT NULL,
  `packer_name` varchar(255) DEFAULT NULL COMMENT '渠道包名',
  `channel_id` varchar(64) DEFAULT NULL,
  `base_config` varchar(10240) DEFAULT NULL COMMENT '渠道参数配置',
  `plugins_config` varchar(1024) DEFAULT NULL COMMENT '插件参数配置',
  `game_name` varchar(64) DEFAULT NULL COMMENT '游戏名',
  `result_type` varchar(64) DEFAULT NULL COMMENT '打包产物 apk/aab',
  `icon_url` varchar(255) DEFAULT NULL COMMENT 'iCON地址',
  `splash_url` varchar(1024) DEFAULT NULL COMMENT '闪屏文件地址',
  `build_num` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `could_pack` int(11) DEFAULT NULL COMMENT '配置是否完成',
  `channel_version` varchar(64) DEFAULT NULL COMMENT '渠道版本',
  `env_code` varchar(64) DEFAULT NULL COMMENT '环境编码',
  `mother_is_ftp` int(2) DEFAULT '0' COMMENT '母包是否为FTP存储',
  `ftp_path` varchar(250) DEFAULT NULL COMMENT 'ftp存储路径',
  `last_update_as` varchar(64) DEFAULT NULL COMMENT '最近一次修改人',
  `last_ops` varchar(64) DEFAULT NULL COMMENT '最近一次打包人',
  `last_pack_time` timestamp NULL DEFAULT NULL COMMENT '最近一次打包时间',
  `last_his_id` varchar(64) DEFAULT NULL COMMENT '最近一次打包历史ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Packer_Status` */

DROP TABLE IF EXISTS `T_Packer_Status`;

CREATE TABLE `T_Packer_Status` (
  `id` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
  `app_id` varchar(64) CHARACTER SET utf8mb4 NOT NULL,
  `record_id` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0.未打包，1.打包中，2.打包成功，3.打包失败',
  `fail_reason` varchar(1000) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '失败原因',
  `result` varchar(1000) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '打包结果',
  `version` int(11) DEFAULT '0' COMMENT '打包版本',
  `version_code` int(64) DEFAULT '0',
  `reason` varchar(1000) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '错误原因',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci;

/*Table structure for table `T_Plugins` */

DROP TABLE IF EXISTS `T_Plugins`;

CREATE TABLE `T_Plugins` (
  `id` varchar(64) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(64) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `server_config_doc` varchar(1024) DEFAULT NULL,
  `client_config_doc` varchar(1024) DEFAULT NULL,
  `extra` varchar(1024) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL COMMENT '插件描述',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Record_Media` */

DROP TABLE IF EXISTS `T_Record_Media`;

CREATE TABLE `T_Record_Media` (
  `id` varchar(64) NOT NULL,
  `record_id` varchar(64) NOT NULL,
  `media_id` varchar(64) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Record_Plugins` */

DROP TABLE IF EXISTS `T_Record_Plugins`;

CREATE TABLE `T_Record_Plugins` (
  `id` varchar(64) NOT NULL,
  `record_id` varchar(64) NOT NULL,
  `plugins_id` varchar(64) NOT NULL,
  `plugins_config` tinytext,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_System_Env` */

DROP TABLE IF EXISTS `T_System_Env`;

CREATE TABLE `T_System_Env` (
  `env_code` varchar(255) NOT NULL,
  `env_desc` varchar(255) NOT NULL,
  `supersdk_url` varchar(200) NOT NULL,
  `avalonsdk_url` varchar(255) DEFAULT NULL,
  `sort_num` int(11) DEFAULT NULL,
  `enable` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`env_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `T_Upload_File` */

DROP TABLE IF EXISTS `T_Upload_File`;

CREATE TABLE `T_Upload_File` (
  `id` varchar(64) NOT NULL,
  `name` varchar(250) DEFAULT NULL,
  `type` varchar(250) DEFAULT NULL,
  `belong_id` varchar(64) DEFAULT NULL,
  `file_size` bigint(20) DEFAULT NULL,
  `path` varchar(500) NOT NULL COMMENT '本地路径',
  `ori_name` varchar(500) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `relative_path` varchar(255) DEFAULT NULL COMMENT '网络路径',
  PRIMARY KEY (`id`,`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
