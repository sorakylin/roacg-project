CREATE DATABASE `roacg-service-system` CHARACTER SET UTF8MB4;


-- ==============================================================
-- -------OAuth2 table script start--------
-- ==============================================================

CREATE TABLE `clientdetails`
(
    `appId`                  varchar(128) NOT NULL,
    `resourceIds`            varchar(256)  DEFAULT NULL,
    `appSecret`              varchar(256)  DEFAULT NULL,
    `scope`                  varchar(256)  DEFAULT NULL,
    `grantTypes`             varchar(256)  DEFAULT NULL,
    `redirectUrl`            varchar(256)  DEFAULT NULL,
    `authorities`            varchar(256)  DEFAULT NULL,
    `access_token_validity`  int(11)       DEFAULT NULL,
    `refresh_token_validity` int(11)       DEFAULT NULL,
    `additionalInformation`  varchar(4096) DEFAULT NULL,
    `autoApproveScopes`      varchar(256)  DEFAULT NULL,
    PRIMARY KEY (`appId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `oauth_access_token`
(
    `token_id`          varchar(256) DEFAULT NULL,
    `token`             blob,
    `authentication_id` varchar(128) NOT NULL,
    `user_name`         varchar(256) DEFAULT NULL,
    `client_id`         varchar(256) DEFAULT NULL,
    `authentication`    blob,
    `refresh_token`     varchar(256) DEFAULT NULL,
    PRIMARY KEY (`authentication_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `oauth_approvals`
(
    `userId`         varchar(256)   DEFAULT NULL,
    `clientId`       varchar(256)   DEFAULT NULL,
    `scope`          varchar(256)   DEFAULT NULL,
    `status`         varchar(10)    DEFAULT NULL,
    `expiresAt`      timestamp NULL DEFAULT NULL,
    `lastModifiedAt` timestamp NULL DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `oauth_client_details`
(
    `client_id`               varchar(128) NOT NULL,
    `resource_ids`            varchar(256)  DEFAULT NULL,
    `client_secret`           varchar(256)  DEFAULT NULL,
    `scope`                   varchar(256)  DEFAULT NULL,
    `authorized_grant_types`  varchar(256)  DEFAULT NULL,
    `web_server_redirect_uri` varchar(256)  DEFAULT NULL,
    `authorities`             varchar(256)  DEFAULT NULL,
    `access_token_validity`   int(11)       DEFAULT NULL,
    `refresh_token_validity`  int(11)       DEFAULT NULL,
    `additional_information`  varchar(4096) DEFAULT NULL,
    `autoapprove`             varchar(256)  DEFAULT NULL,
    PRIMARY KEY (`client_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `oauth_client_token`
(
    `token_id`          varchar(256) DEFAULT NULL,
    `token`             blob,
    `authentication_id` varchar(128) NOT NULL,
    `user_name`         varchar(256) DEFAULT NULL,
    `client_id`         varchar(256) DEFAULT NULL,
    PRIMARY KEY (`authentication_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `oauth_code`
(
    `code`           varchar(256) DEFAULT NULL,
    `authentication` blob
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `oauth_refresh_token`
(
    `token_id`       varchar(256) DEFAULT NULL,
    `token`          blob,
    `authentication` blob
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;CREATE DATABASE `roacg-service-system` CHARACTER SET UTF8MB4;


-- 初始化一个客户端 client_id=0001 client_secret=123456
INSERT INTO `roacg-service-system`.oauth_client_details
(client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES ('0001', null, '$2a$10$9ZhDOBp.sRKat4l14ygu/.LscxrMUcDAfeVOEPiYwbcRkoB09gCmi', 'web', 'authorization_code,password,refresh_token', 'https://www.github.com', null, null, null, null, null)


-- ==============================================================
-- -------OAuth2 table script end--------
-- ==============================================================



-- ==============================================================
-- -------System user table script start--------
-- ==============================================================

CREATE TABLE `tb_resource_permission` (
  `permission_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL COMMENT '资源路径',
  `method` varchar(8) NOT NULL COMMENT '访问方法',
  `name` varchar(64) NOT NULL COMMENT '权限名称',
  `en_name` varchar(64) NOT NULL COMMENT '权限英文名称',
  `description` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL,
  `update_time` datetime NULL,
  `create_at` varchar(64) NULL,
  `update_at` varchar(64) NULL,
  `update_id` bigint(20) NULL,
  PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT='资源权限表';

CREATE TABLE `tb_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父角色',
  `name` varchar(64) NOT NULL COMMENT '角色名称',
  `en_name` varchar(64) NOT NULL COMMENT '角色英文名称',
  `description` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL,
  `update_time` datetime NULL,
  `create_at` varchar(64) NULL,
  `update_at` varchar(64) NULL,
  `update_id` bigint(20) NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT='角色表';




CREATE TABLE `tb_role_resource_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL COMMENT '角色 ID',
  `permission_id` bigint(20) NOT NULL COMMENT '资源权限 ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT='角色-资源权限表';


CREATE TABLE `tb_ro_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) binary NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码，加密存储',
  `phone` varchar(128) NULL COMMENT '电话号码',
  `email` varchar(128) binary NULL COMMENT '邮箱',
  `create_time` datetime NULL,
  `update_time` datetime NULL,
  `create_at` varchar(64) NULL,
  `update_at` varchar(64) NULL,
  `update_id` bigint(20) NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`user_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT='用户表';


CREATE TABLE `tb_rouser_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT='用户角色表';

-- 默认加个用户, 密码123456
INSERT INTO `tb_ro_user`
VALUES (null,
        'ro',
        '$2a$10$9ZhDOBp.sRKat4l14ygu/.LscxrMUcDAfeVOEPiYwbcRkoB09gCmi',
        null,
        null,
        NOW(),
        NOW(),
        null,
        null,
        null,
        0);

-- ==============================================================
-- -------System user table script start--------
-- ==============================================================


