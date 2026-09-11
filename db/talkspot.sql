-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: talkspot
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `call_log`
--

DROP TABLE IF EXISTS `call_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `call_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `caller_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `call_type` enum('VOICE','VIDEO') NOT NULL DEFAULT 'VOICE',
  `status` enum('MISSED','ACCEPTED','REJECTED') NOT NULL DEFAULT 'MISSED',
  `channel_name` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_call_caller` (`caller_id`),
  KEY `fk_call_receiver` (`receiver_id`),
  CONSTRAINT `fk_call_caller` FOREIGN KEY (`caller_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_call_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `call_log`
--

LOCK TABLES `call_log` WRITE;
/*!40000 ALTER TABLE `call_log` DISABLE KEYS */;
INSERT INTO `call_log` VALUES (10,35,33,'VOICE','MISSED','talkspot_channel_1789083995515','2026-09-11 05:17:06',NULL,0),(11,34,36,'VOICE','MISSED','talkspot_cancelled','2026-09-11 05:46:15',NULL,0),(12,36,34,'VOICE','MISSED','talkspot_channel_1789085771822','2026-09-11 05:46:42',NULL,0);
/*!40000 ALTER TABLE `call_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `files` longtext NOT NULL,
  `message` longtext NOT NULL,
  `status` varchar(30) DEFAULT NULL,
  `from_user` int DEFAULT NULL,
  `to_user` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_qydhn8kg94hqya41okkq4h5ng` (`from_user`),
  KEY `FK_e6od82vcl84c3j9hawauu8xwn` (`to_user`),
  CONSTRAINT `FK_e6od82vcl84c3j9hawauu8xwn` FOREIGN KEY (`to_user`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_qydhn8kg94hqya41okkq4h5ng` FOREIGN KEY (`from_user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat`
--

LOCK TABLES `chat` WRITE;
/*!40000 ALTER TABLE `chat` DISABLE KEYS */;
INSERT INTO `chat` VALUES (1,'2025-10-05 17:34:22','2025-10-08 16:41:54','FILE:','Hello MK?','READ',1,32),(2,'2025-10-05 18:47:27','2025-10-05 18:56:35','FILE:','MK','READ',1,32),(47,'2025-10-05 18:56:53','2025-10-09 17:21:06','FILE:','Hello','READ',23,30),(48,'2025-10-05 18:59:17','2025-10-05 18:59:17','FILE:','Hi mk','READ',23,30),(49,'2025-10-06 22:18:47','2025-10-06 22:18:47','FILE:','Machan','READ',32,23),(50,'2025-10-06 22:24:12','2025-10-08 16:41:54','FILE:','Halo darshana','READ',14,30),(51,'2025-10-08 16:18:58','2025-10-08 16:18:58','FILE:','Hello dini','READ',2,30),(52,'2025-10-08 16:51:39','2025-10-08 16:51:39','FILE:','Hello kollo','SENT',30,1),(53,'2025-10-09 22:49:25','2025-10-09 22:49:25','FILE:','Hello','DELIVERED',30,23),(54,'2025-10-09 22:49:47','2025-10-09 22:49:47','FILE:','Hello ','SENT',30,18),(55,'2025-10-09 22:56:31','2025-10-09 22:56:31','FILE:','Hey','SENT',30,2),(56,'2025-10-09 22:58:24','2025-10-09 22:58:24','FILE:','Hi','SENT',30,18),(57,'2025-10-09 23:34:05','2025-10-09 23:34:05','FILE:','Hello','SENT',30,2),(58,'2025-10-09 23:34:25','2025-10-09 23:34:25','FILE:','Hi','SENT',30,16),(59,'2025-10-11 15:23:37','2025-10-11 15:23:37','FILE:','Hey','SENT',32,23),(60,'2026-09-11 02:07:52','2026-09-11 02:23:19','FILE:','Hi shaviru aiya','READ',34,33),(61,'2026-09-11 02:12:52','2026-09-11 02:23:19','FILE:','Shaviru mk ?','DELIVERED',35,33),(62,'2026-09-11 02:13:41','2026-09-11 02:48:57','FILE:','Nangi adil mk?','DELIVERED',35,34),(63,'2026-09-11 02:23:35','2026-09-11 02:48:57','FILE:','Hi nangi','DELIVERED',33,34),(64,'2026-09-11 02:53:06','2026-09-11 03:38:14','FILE:','Nangi','READ',36,34),(65,'2026-09-11 02:53:57','2026-09-11 02:55:23','FILE:','Abi aiye','READ',36,35),(66,'2026-09-11 02:54:46','2026-09-11 03:36:16','FILE:','Aiyee mk','DELIVERED',36,33),(67,'2026-09-11 03:07:50','2026-09-11 03:38:14','FILE:','Adil ko','READ',36,34),(69,'2026-09-11 04:50:37','2026-09-11 04:50:37','FILE:','Hi','SENT',35,33),(70,'2026-09-11 04:51:09','2026-09-11 05:18:17','FILE:','O hi','READ',35,36),(74,'2026-09-11 05:32:49','2026-09-11 05:32:49','FILE:','Hi','SENT',36,35),(79,'2026-09-11 20:44:02','2026-09-11 20:44:02','FILE:','Hi','SENT',36,34),(80,'2026-09-11 20:44:08','2026-09-11 20:44:08','FILE:','Hello','SENT',36,33);
/*!40000 ALTER TABLE `chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_list`
--

DROP TABLE IF EXISTS `friend_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend_list` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_status` varchar(30) DEFAULT NULL,
  `friend_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_t35f03kjx6389385fthfry288` (`friend_id`),
  KEY `FK_rmlr6b76l6606kgyo9uim7maf` (`user_id`),
  CONSTRAINT `FK_rmlr6b76l6606kgyo9uim7maf` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_t35f03kjx6389385fthfry288` FOREIGN KEY (`friend_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_list`
--

LOCK TABLES `friend_list` WRITE;
/*!40000 ALTER TABLE `friend_list` DISABLE KEYS */;
INSERT INTO `friend_list` VALUES (1,'ACTIVE\r\n',1,32,'Dini'),(2,'ACTIVE',2,32,'Malindu'),(3,'ACTIVE',23,32,NULL),(10,'ACTIVE',16,32,'Kumari'),(11,'ACTIVE',17,32,NULL),(12,'ACTIVE',18,32,'dfefe'),(13,'ACTIVE',19,32,NULL),(14,'ACTIVE',30,32,NULL),(15,'ACTIVE',30,32,NULL),(16,'ACTIVE',30,2,NULL),(17,'ACTIVE',30,16,NULL),(18,'ACTIVE',32,23,NULL),(19,'ACTIVE',34,33,'Shurya Dewanshi'),(20,'ACTIVE',33,34,'Shaviru Aiya'),(21,'ACTIVE',33,35,'Shaviru  Brayan'),(22,'ACTIVE',35,33,NULL),(23,'ACTIVE',34,35,'Shaurya Nangi'),(24,'ACTIVE',35,34,NULL),(25,'ACTIVE',34,36,'Shaurya Nangi'),(26,'ACTIVE',36,34,NULL),(27,'ACTIVE',35,36,'Abi Aiya'),(28,'ACTIVE',36,35,NULL),(29,'ACTIVE',33,36,'Shaviru Aiya'),(30,'ACTIVE',36,33,NULL);
/*!40000 ALTER TABLE `friend_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `contact_no` varchar(45) NOT NULL,
  `country_code` varchar(5) NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `status` varchar(45) NOT NULL,
  `about` longtext,
  `links` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_lo565olv8by15v9qgmqq8ajh8` (`contact_no`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2025-10-05 17:30:21','2025-10-05 17:30:23','787654567','+94','Dini','Paba','ONLINE',NULL,NULL),(2,'2025-10-05 17:32:39','2025-10-05 17:32:40','765555678','+94','Mali','Praboda','OFFLINE',NULL,NULL),(14,'2025-10-05 18:31:53','2025-10-05 22:47:51','763956284','+94','Darshana','Chinthaka','ONLINE',NULL,NULL),(15,'2025-10-06 15:56:49','2025-10-06 15:56:49','765852632','+94','Dini','Pabasara','ONLINE',NULL,NULL),(16,'2025-10-06 16:57:20','2025-10-06 16:57:20','787632549','+94','Ashi','Kumari','ONLINE',NULL,NULL),(17,'2025-10-06 19:49:22','2025-10-06 20:02:47','786523951','+94','Inoka','Kumari','ONLINE',NULL,NULL),(18,'2025-10-06 21:36:53','2025-10-06 22:03:48','762589874','+94','Warad','Shaneru','OFFLINE',NULL,NULL),(19,'2025-10-06 22:06:53','2025-10-06 22:25:42','725809786','+94','Pandula','Basnayaka','OFFLINE',NULL,NULL),(20,'2025-10-07 21:46:56','2025-10-07 21:50:13','758547325','+94','Wednesday','Addams','OFFLINE',NULL,NULL),(21,'2025-10-08 14:17:57','2025-10-08 14:43:09','725806985','+94','Tharindu','Bandara','OFFLINE',NULL,NULL),(23,'2025-10-08 16:15:29','2025-10-08 17:54:11','765896325','+94','Dammika','Perera','ONLINE',NULL,NULL),(27,'2025-10-09 21:31:48','2025-10-09 21:50:12','758632586','+94','Manel','Malkumari','OFFLINE',NULL,NULL),(28,'2025-10-09 21:53:09','2025-10-09 21:58:29','789852147','+94','Moda','Tharindu','OFFLINE',NULL,NULL),(29,'2025-10-09 22:02:46','2025-10-09 22:18:17','765896322','+94','Sepalika','Kahakumari','OFFLINE',NULL,NULL),(30,'2025-10-09 22:44:41','2025-10-09 23:39:26','762580000','+94','Mahagama','Sekara','OFFLINE',NULL,NULL),(31,'2025-10-10 13:25:19','2025-10-10 13:26:34','765807777','+94','Kumara','Munasingha','OFFLINE',NULL,NULL),(32,'2025-10-11 15:14:46','2025-10-11 15:27:43','765800005','+94','Kumara','Kumara','OFFLINE',NULL,NULL),(33,'2026-09-10 01:17:13','2026-09-11 04:31:08','787766325','+94','Shaviru','Brayan','OFFLINE','Hey there! I am shaviru','shviru.lk'),(34,'2026-09-10 02:08:10','2026-09-11 05:30:21','758866528','+94','Shaurya','Dewanshi','OFFLINE','Hey there! ','shurya@gmail.com'),(35,'2026-09-11 02:11:48','2026-09-11 05:17:56','787745454','+94','Aryan','Abinaw','OFFLINE','Hey there! I am using TalkSpot.',''),(36,'2026-09-11 02:52:24','2026-09-11 20:44:20','788877777','+94','Adil','Thishakya','OFFLINE','Hey i\'m Adil','adil@gmail.com');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_status`
--

DROP TABLE IF EXISTS `user_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `media_url` varchar(500) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_9onp4jxeo4eyc7i5124wp38me` (`user_id`),
  CONSTRAINT `FK_9onp4jxeo4eyc7i5124wp38me` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_user_status_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_status`
--

LOCK TABLES `user_status` WRITE;
/*!40000 ALTER TABLE `user_status` DISABLE KEYS */;
INSERT INTO `user_status` VALUES (1,33,'file:///data/user/0/host.exp.exponent/cache/ImagePicker/8a5113c6-7117-40b1-8f90-da78126f2904.jpeg','2026-09-11 03:36:37',NULL),(2,33,'file:///data/user/0/host.exp.exponent/cache/ImagePicker/26999bb6-b962-4a4a-8bed-bef1872627c4.jpeg','2026-09-11 03:37:16',NULL),(3,33,'file:///data/user/0/host.exp.exponent/cache/ImagePicker/0e441a0a-cfc7-4151-a3ea-d876fee35493.jpeg','2026-09-11 03:37:30',NULL),(4,33,'file:///data/user/0/host.exp.exponent/cache/ImagePicker/5db7efcd-a9b3-4e81-9710-fa07d172fb0f.jpeg','2026-09-11 03:37:47',NULL),(5,34,'file:///data/user/0/host.exp.exponent/cache/ImagePicker/dcbd3811-4506-4ccb-9ca9-70c6baad694f.jpeg','2026-09-11 03:51:21',NULL);
/*!40000 ALTER TABLE `user_status` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-11 21:59:42
