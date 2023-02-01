-- 给母包表增加是否为mac系统列
ALTER TABLE T_Mother_Packages ADD is_mac TINYINT(1) DEFAULT 0 NULL COMMENT 'mac系统标识';
-- 给配置表增加是否为mac系统列
ALTER TABLE T_Packer_Record ADD is_mac TINYINT(1) DEFAULT 0 NULL COMMENT 'mac系统标识';
-- 给渠道表增加是否为mac系统列
ALTER TABLE T_Channel ADD is_mac TINYINT(1) DEFAULT 0 NULL COMMENT 'mac系统标识';
-- 给应用表增加描述文件列
ALTER TABLE T_App ADD desc_file_name VARCHAR(1000) NULL COMMENT 'mac系统时，描述文件名称';
-- 给配置表增加描述文件列
ALTER TABLE T_Packer_Record ADD desc_file_name VARCHAR(1000) NULL COMMENT 'mac系统时，描述文件名称';
-- app表下mac证书
ALTER TABLE T_App ADD mac_sign_file VARCHAR(1000) NULL COMMENT 'mac证书';
-- 配置表下mac证书
ALTER TABLE T_Packer_Record ADD mac_sign_file TEXT(1000) NULL COMMENT 'mac证书';
-- 配置表增加其他文件上传
ALTER TABLE T_Packer_Record ADD (
	other_file TEXT(500) NULL COMMENT 'linux其他文件',
	mac_other_file TEXT(500) NULL COMMENT 'mac其他文件'
);
-- 配置表增加发布方式
ALTER TABLE T_Packer_Record ADD public_type INT NULL COMMENT 'IOS发布方式';
-- 创建配置历史版本表
CREATE TABLE `T_Config_Version` (
                                    `id` varchar(64) NOT NULL COMMENT 'id',
                                    `config_id` varchar(64) NOT NULL COMMENT '配置id',
                                    `version` int(11) DEFAULT NULL COMMENT '版本',
                                    `config` text COMMENT '配置转md5存放',
                                    PRIMARY KEY (`id`),
                                    KEY `UNIQUE` (`config_id`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 成果包增加md5字段
ALTER TABLE T_Channel_Media_Package ADD md5_val VARCHAR(64) NULL COMMENT 'ipa的md5验证值';
-- 成果包增加打包方式字段
ALTER TABLE T_Channel_Media_Package ADD public_type INT NULL COMMENT 'ios发布方式';

ALTER TABLE T_Channel_Media_Package ADD fail_reason TEXT(248) NULL COMMENT 'ios上传失败原因';

ALTER TABLE T_Packer_Record ADD mac_cert_pwd VARCHAR(30) NULL COMMENT 'ios证书密码';

ALTER TABLE T_App ADD mac_cert_pwd VARCHAR(30) NULL COMMENT 'ios证书密码';
-- 更新渠道表channelcode长度
ALTER TABLE T_Channel MODIFY channel_code VARCHAR(35);