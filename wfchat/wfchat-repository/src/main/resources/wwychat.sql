CREATE DATABASE `wfchat` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
CREATE TABLE `friend` (
  `requestid` int(11) NOT NULL AUTO_INCREMENT,
  `fromusername` varchar(45) NOT NULL,
  `tousername` varchar(45) NOT NULL,
  `requesttime` datetime NOT NULL,
  `isread` tinyint(4) unsigned DEFAULT NULL,
  `isaccept` tinyint(4) unsigned DEFAULT NULL,
  `latestmessageid` int(11) DEFAULT NULL,
  PRIMARY KEY (`requestid`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
CREATE TABLE `message` (
  `messageid` int(11) NOT NULL AUTO_INCREMENT,
  `fromusername` varchar(45) NOT NULL,
  `tousername` varchar(45) NOT NULL,
  `message` varchar(300) NOT NULL,
  `sendtime` datetime NOT NULL,
  `isread` tinyint(4) unsigned DEFAULT NULL,
  PRIMARY KEY (`messageid`)
) ENGINE=InnoDB AUTO_INCREMENT=211 DEFAULT CHARSET=utf8;
CREATE TABLE `user` (
  `id` bigint(45) NOT NULL AUTO_INCREMENT,
  `username` varchar(10) NOT NULL,
  `password` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `sex` int(2) NOT NULL,
  `profile` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;
