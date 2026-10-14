CREATE DATABASE  IF NOT EXISTS `fashionstore` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `fashionstore`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: fashionstore
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `count` int NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `variant_id` bigint DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl70asp4l4w0jmbm1tqyofho4o` (`user_id`),
  KEY `FKennwa8klqyqrkq70971aw5m3q` (`variant_id`),
  CONSTRAINT `FKennwa8klqyqrkq70971aw5m3q` FOREIGN KEY (`variant_id`) REFERENCES `product_variant` (`id`),
  CONSTRAINT `FKl70asp4l4w0jmbm1tqyofho4o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Áo Nam','fa-tshirt',NULL,NULL),(2,'Quần Nam','fa-user-tie',NULL,NULL),(3,'Giày Thể Thao','fa-shoe-prints',NULL,NULL),(4,'Phụ Kiện Thời Trang','fa-glasses',NULL,NULL);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_transaction`
--

DROP TABLE IF EXISTS `inventory_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `note` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `quantity_after` int DEFAULT NULL,
  `quantity_before` int DEFAULT NULL,
  `total_amount` decimal(18,2) DEFAULT NULL,
  `transaction_date` datetime DEFAULT NULL,
  `transaction_type` varchar(20) DEFAULT NULL,
  `unit_price` decimal(18,2) DEFAULT NULL,
  `updated_at` datetime NOT NULL,
  `variant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK57c0ev9ayycpjqxc0qytuywqy` (`variant_id`),
  CONSTRAINT `FK57c0ev9ayycpjqxc0qytuywqy` FOREIGN KEY (`variant_id`) REFERENCES `product_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_transaction`
--

LOCK TABLES `inventory_transaction` WRITE;
/*!40000 ALTER TABLE `inventory_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `inventory_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `booking_date` date DEFAULT NULL,
  `country` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `fullname` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `note` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `payment_status` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `payment_method` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `phone` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `status` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `total` decimal(19,2) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcpl0mjoeqhxvgeeeq5piwpd3i` (`user_id`),
  KEY `idx_order_user_id` (`user_id`),
  KEY `idx_order_booking_date` (`booking_date`),
  CONSTRAINT `FKcpl0mjoeqhxvgeeeq5piwpd3i` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `count` int DEFAULT NULL,
  `price` decimal(19,2) DEFAULT NULL,
  `total` decimal(19,2) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `variant_id` bigint DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `ted_at` date DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs234mi6jususbx4b37k44cipy` (`order_id`),
  KEY `FKcvr1aqkc2suhg2lvuwpfe0q0s` (`variant_id`),
  CONSTRAINT `FKcvr1aqkc2suhg2lvuwpfe0q0s` FOREIGN KEY (`variant_id`) REFERENCES `product_variant` (`id`),
  CONSTRAINT `FKs234mi6jususbx4b37k44cipy` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` date DEFAULT NULL,
  `description` varchar(11111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `is_active` int DEFAULT NULL,
  `is_selling` int DEFAULT NULL,
  `product_name` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `sold` int DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1mtsbur82frn64de7balymq9s` (`category_id`),
  CONSTRAINT `FK1mtsbur82frn64de7balymq9s` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'2026-09-28','Áo thun 100% cotton thoáng mát, thấm hút mồ hôi tốt',1,1,'Áo Thun Cotton Unisex Standard',0,1,NULL,NULL),(2,'2026-10-06','Áo polo lịch sự thích hợp đi làm và đi chơi',1,1,'Áo Polo Nam Form Regular',0,1,NULL,NULL),(3,'2025-10-25','Chất liệu đũi cao cấp chống nhăn nhẹ',1,1,'Áo Sơ Mi Trắng Cổ Tàu',0,1,NULL,NULL),(4,'2026-01-01','Phong cách bụi bặm, bền đẹp theo thời gian',1,1,'Áo Khoác Denim Vintage',0,1,NULL,NULL),(5,'2026-10-11','Áo hoodie nỉ ngoại dầy dặn giữ ấm tốt',1,1,'Áo Hoodie Fleece Oversize',0,1,NULL,NULL),(6,'2026-02-01','Kiểu dáng năng động, phối đồ cực dễ',1,1,'Áo Sweater Nỉ Bông Basic',0,1,NULL,NULL),(7,'2026-04-28','Co giãn 4 chiều chuyên dụng tập gym',1,1,'Áo Tanktop Thể Thao Gymer',0,1,NULL,NULL),(8,'2026-07-25','Form dáng suông trẻ trung hiện đại',1,1,'Áo Blazer Nam Hàn Quốc',0,1,NULL,NULL),(9,'2026-01-24','Chất vải dầy dặn khoác ngoài phong cách',1,1,'Áo Sơ Mi Ca Rô Flannel',0,1,NULL,NULL),(10,'2025-11-06','Giữ ấm cơ thể tối đa vào mùa lạnh',1,1,'Áo Len Cổ Lọ Winter',0,1,NULL,NULL),(11,'2026-04-07','Nhiều túi tiện lợi cho các chuyến dã ngoại',1,1,'Áo Gilet Kaki Pocket',0,1,NULL,NULL),(12,'2025-12-26','Chống nước nhẹ và cản gió hiệu quả',1,1,'Áo Khoác Dù Windbreaker',0,1,NULL,NULL),(13,'2026-05-08','Hình in sắc nét không lo bong tróc',1,1,'Áo Thun In Họa Tiết Streetwear',0,1,NULL,NULL),(14,'2026-01-01','Bo cổ cứng dặn lịch sự',1,1,'Áo Polo Pique Co Giãn',0,1,NULL,NULL),(15,'2026-03-07','Họa tiết nhiệt đới rực rỡ đi biển',1,1,'Áo Sơ Mi Short-Sleeve Summer',0,1,NULL,NULL),(16,'2026-02-08','Phong cách nhẹ nhàng lãng mạn',1,1,'Áo Khoác Cardigan Len',0,1,NULL,NULL),(17,'2026-03-16','Tôn dáng ngực và vai',1,1,'Áo Thun Cổ V Slimfit',0,1,NULL,NULL),(18,'2025-11-30','Da PU cao cấp không nổ da',1,1,'Áo Khoác Da Biker Classic',0,1,NULL,NULL),(19,'2026-03-07','Chất vải dầy dặn chuẩn công sở',1,1,'Áo Sơ Mi Oxford Dài Tay',0,1,NULL,NULL),(20,'2026-05-15','Siêu nhẹ, thoát khí nhanh chóng',1,1,'Áo Thể Thao Running Pro',0,1,NULL,NULL),(21,'2026-07-07','Lót bông nhẹ, kiểu dáng phi công',1,1,'Áo Khoác Bomber Flight',0,1,NULL,NULL),(22,'2026-09-07','Phong cách học sinh sinh viên',1,1,'Áo Thun Tay Lỡ Form Rộng',0,1,NULL,NULL),(23,'2026-02-03','Vải đũi tự nhiên 100%',1,1,'Áo Sơ Mi Linen Thoáng Khí',0,1,NULL,NULL),(24,'2026-08-14','Thích hợp chơi tennis, golf',1,1,'Áo Polo Sport Quick-Dry',0,1,NULL,NULL),(25,'2026-01-13','Chất jean dầy dặn co giãn nhẹ thoải mái',1,1,'Quần Jeans Slimfit Co Giãn',0,2,NULL,NULL),(26,'2026-07-11','Form quần chuẩn, đứng dáng',1,1,'Quần Tây Công Sở Co Giãn',0,2,NULL,NULL),(27,'2026-09-27','Độ dài ngang gối năng động',1,1,'Quần Short Kaki Basic',0,2,NULL,NULL),(28,'2026-05-01','Chất vải thun co giãn vận động dễ dàng',1,1,'Quần Jogger Thể Thao Bo Gấu',0,2,NULL,NULL),(29,'2026-08-26','Phong cách hầm hố dã ngoại',1,1,'Quần Cargo Nhiều Túi Lõi',0,2,NULL,NULL),(30,'2026-06-23','Thoáng mát mùa hè',1,1,'Quần Đũi Dài Ống Rộng',0,2,NULL,NULL),(31,'2026-08-21','Có lót trong chuyên chạy bộ',1,1,'Quần Short Running Nhanh Khô',0,2,NULL,NULL),(32,'2025-12-21','Xu hướng thời trang trẻ',1,1,'Quần Jeans Ống Rộng Baggy',0,2,NULL,NULL),(33,'2026-03-02','Dễ phối cùng áo sơ mi và polo',1,1,'Quần Kaki Chino Hàn Quốc',0,2,NULL,NULL),(34,'2026-02-18','Cá tính năng động',1,1,'Quần Short Jeans Rách Gối',0,2,NULL,NULL),(35,'2026-05-20','Giữ nhiệt cực tốt',1,1,'Quần Nỉ Bo Ống Thu Đông',0,2,NULL,NULL),(36,'2026-09-20','Cạp chun thoải mái khi đeo',1,1,'Quần Baggy Kaki Cạp Co Giãn',0,2,NULL,NULL),(37,'2026-09-01','Chống cọ xát đùi khi tập luyện',1,1,'Quần Shorts Thể Thao 2 Lớp',0,2,NULL,NULL),(38,'2026-05-25','Giữ màu tốt qua nhiều lần giặt',1,1,'Quần Jeans Đen Không Phai',0,2,NULL,NULL),(39,'2026-03-13','Phong cách cổ điển',1,1,'Quần Tây Xếp Li Vintage',0,2,NULL,NULL),(40,'2026-01-01','Mềm mại dễ chịu',1,1,'Quần Short Thun Mặc Nhà',0,2,NULL,NULL),(41,'2026-08-22','Thiết kế túi khóa hiện đại',1,1,'Quần Jogger Kaki Khóa Zip',0,2,NULL,NULL),(42,'2026-05-28','Lịch sự trang nhã',1,1,'Quần Kaki Ống Đứng Regular',0,2,NULL,NULL),(43,'2026-04-24','Thích hợp đi bơi, đi biển',1,1,'Quần Short Dù Chống Nước',0,2,NULL,NULL),(44,'2026-07-26','Bụi bặm vừa phải',1,1,'Quần Jeans Rách Nhẹ Vintage',0,2,NULL,NULL),(45,'2026-02-07','Màu sắc trung tính tôn dáng',1,1,'Quần Tây Xám Ghi Slim',0,2,NULL,NULL),(46,'2026-01-14','Chuyên dụng mùa đông',1,1,'Quần Jogger Nỉ Ngoại Cực Dày',0,2,NULL,NULL),(47,'2026-02-02','Nhiều ngăn chứa đồ',1,1,'Quần Short Khóa Kéo Tải Trọng',0,2,NULL,NULL),(48,'2026-07-21','Thoải mái khi ngồi lâu',1,1,'Quần Kaki Đàn Hồi Công Sở',0,2,NULL,NULL),(49,'2026-09-14','Mẫu sneaker quốc dân dễ phối đồ',1,1,'Giày Sneaker Trắng Classic',0,3,NULL,NULL),(50,'2026-01-28','Đệm khí êm ái hỗ trợ chạy bộ dài',1,1,'Giày Running Cushion Ultra',0,3,NULL,NULL),(51,'2026-06-26','Bảo vệ cổ chân cực tốt',1,1,'Giày Basketball High-Top',0,3,NULL,NULL),(52,'2026-05-27','Đế bằng bám sàn chắc chắn',1,1,'Giày Tập Gym Cross-Training',0,3,NULL,NULL),(53,'2026-10-10','Đế cao su lưu hóa bền bỉ',1,1,'Giày Skateboarding Canvas',0,3,NULL,NULL),(54,'2025-11-16','Không cần buộc dây tiện lợi',1,1,'Giày Slip-On Lười Nhanh Gọn',0,3,NULL,NULL),(55,'2026-04-11','Đế gai sâu bám đường địa hình',1,1,'Giày Trekking Chống Nước Outdoor',0,3,NULL,NULL),(56,'2025-12-18','Đế hầm hố tăng chiều cao 5cm',1,1,'Giày Sneaker Chunky Retro',0,3,NULL,NULL),(57,'2026-03-14','Đinh TF bám sân chống trượt',1,1,'Giày Đá Bóng Sân Nhân Tạo',0,3,NULL,NULL),(58,'2026-04-27','Đế chống lật cổ chân',1,1,'Giày Cầu Lông Chuyên Dụng',0,3,NULL,NULL),(59,'2026-03-22','Chịu ma sát cao trên sân cứng',1,1,'Giày Tennis Court Pro',0,3,NULL,NULL),(60,'2026-05-09','Đế siêu nhẹ cho người đi bộ nhiều',1,1,'Giày Walking Thở Khí Superlight',0,3,NULL,NULL),(61,'2026-04-28','Chất liệu da lộn sang trọng',1,1,'Giày Sneaker Cổ Thấp Suede',0,3,NULL,NULL),(62,'2026-10-05','Mặt lưới ôm sát chân thoáng khí',1,1,'Giày Lầy Bằng Vải Dệt Flyknit',0,3,NULL,NULL),(63,'2026-01-22','Thích hợp đi mưa đi biển',1,1,'Giày Sandal Thể Thao Quai Hậu',0,3,NULL,NULL),(64,'2026-03-23','Nổi bật trong đêm an toàn',1,1,'Giày Sneaker Phản Quang Night Run',0,3,NULL,NULL),(65,'2026-02-27','Đế hạt cao su êm ái',1,1,'Giày Mọi Da Thật Driving',0,3,NULL,NULL),(66,'2026-04-30','Tích hợp tấm carbon trợ lực',1,1,'Giày Chạy Bộ Carbon Plate',0,3,NULL,NULL),(67,'2026-05-21','Màu đen nguyên khối ngầu',1,1,'Giày Sneaker Đen Mono All-Black',0,3,NULL,NULL),(68,'2026-02-26','Đế cứng tối ưu lực đạp',1,1,'Giày Đạp Xe Road Cycling',0,3,NULL,NULL),(69,'2025-10-30','Quai thun co giãn dễ tháo xỏ',1,1,'Giày Lười Thể Thao Elastic',0,3,NULL,NULL),(70,'2025-11-25','Phong cách hoài cổ',1,1,'Giày Sneaker Vintage 1970s',0,3,NULL,NULL),(71,'2026-03-24','Màu sắc tươi trẻ năng động',1,1,'Giày Thể Thao Trẻ Em / Teen',0,3,NULL,NULL),(72,'2026-08-23','Kết hợp giữa boots và sneaker',1,1,'Giày Boots Thể Thao Cao Cổ',0,3,NULL,NULL),(73,'2026-09-30','Da bò nguyên tấm cao cấp bền đẹp',1,1,'Thắt Lưng Da Bò Thật Khóa Kim',0,4,NULL,NULL),(74,'2026-01-11','Form nón chuẩn đẹp tôn khuôn mặt',1,1,'Mũ Lưỡi Trai Nón Sơn Basic',0,4,NULL,NULL),(75,'2026-02-10','Chống tia UV bảo vệ mắt tối đa',1,1,'Kính Mát Phân Cực UV400',0,4,NULL,NULL),(76,'2026-09-08','Nhiều ngăn đựng thẻ và tiền mặt',1,1,'Ví Nam Dáng Ngang Da Thật',0,4,NULL,NULL),(77,'2026-04-24','Có cổng sạc USB tiện lợi',1,1,'Balo Laptop 15.6 Inch Chống Nước',0,4,NULL,NULL),(78,'2026-09-19','Đeo ngực hoặc đeo hông cá tính',1,1,'Túi Bao Tử Khóa Zip Canvas',0,4,NULL,NULL),(79,'2025-11-09','Combo 5 đôi khử mùi mồ hôi',1,1,'Tất / Vớ Cổ Cao Cotton Kháng Khuẩn',0,4,NULL,NULL),(80,'2026-05-06','Chống nước 3ATM phong cách thanh lịch',1,1,'Đồng Hồ Nam Dây Da Quartz',0,4,NULL,NULL),(81,'2026-05-14','Đội được 2 mặt thay đổi phong cách',1,1,'Mũ Bucket 2 Mặt Cotton',0,4,NULL,NULL),(82,'2026-01-06','Thép không gỉ 316L sáng bóng',1,1,'Vòng Tay Tiên Phong Thép Cốt',0,4,NULL,NULL),(83,'2026-03-14','Khăn len giữ ấm thời trang',1,1,'Khăn Choàng Cổ Len Winter Warm',0,4,NULL,NULL),(84,'2026-02-25','Sử dụng điện thoại không cần tháo găng',1,1,'Găng Tay Da Cảm Ứng Smartphone',0,4,NULL,NULL),(85,'2026-05-21','Vừa vặn đựng điện thoại ví tiền',1,1,'Túi Crossbody Đeo Chéo Nhỏ Gọn',0,4,NULL,NULL),(86,'2026-09-07','Thiết kế tinh xảo sang trọng',1,1,'Móc Khóa Da Bò Handmade',0,4,NULL,NULL),(87,'2026-06-22','Thích hợp mặc cùng áo sơ mi slimfit',1,1,'Cà Vạt Nam Lụa Bản Nhỏ 6cm',0,4,NULL,NULL),(88,'2026-07-15','Khóa gạt cá tính tùy chỉnh độ dài',1,1,'Thắt Lưng Vải Dù Tự Do',0,4,NULL,NULL),(89,'2026-06-20','Chống ánh sáng xanh khi làm việc máy tính',1,1,'Kính Giả Cận Trong Suốt Anti-Blue',0,4,NULL,NULL),(90,'2025-12-10','Phong cách thời trang mùa lạnh',1,1,'Mũ Len Beanie Hàn Quốc',0,4,NULL,NULL),(91,'2026-07-11','Dung tích lớn có ngăn để giày riêng',1,1,'Túi Du Lịch Gym Holdall',0,4,NULL,NULL),(92,'2026-01-04','Nhiều ngăn tiện dụng',1,1,'Ví Dài Nam Đựng Giấy Tờ Xe',0,4,NULL,NULL),(93,'2026-09-09','Không hoen gỉ, phong cách tối giản',1,1,'Nhẫn Nam Thép Titan Trơn',0,4,NULL,NULL),(94,'2026-08-17','Khóa tự động sang trọng',1,1,'Dây Lưng Da Cá Sấu Cao Cấp',0,4,NULL,NULL),(95,'2026-04-15','Mắt kính gọng kim loại mạ vàng',1,1,'Kính Mát Phi Công Aviator Classic',0,4,NULL,NULL),(96,'2026-10-08','Có đệm cao su chống tuột gót',1,1,'Combo 3 Đôi Tất Lười Thể Thao',0,4,NULL,NULL);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `url_image` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `product_variant_id` bigint DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1hvhgw23qikjmnwflw36japbi` (`product_variant_id`),
  CONSTRAINT `FK1hvhgw23qikjmnwflw36japbi` FOREIGN KEY (`product_variant_id`) REFERENCES `product_variant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
INSERT INTO `product_image` VALUES (1,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712397/vqq6epjulpyywlkq2er4.jpg',1,NULL,NULL),(3,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712522/j1pwgesrzs87ry4wt6bq.jpg',3,NULL,NULL),(4,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712567/njgoikzls3ousfqukud7.jpg',4,NULL,NULL),(5,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712594/sjurzahyyx2joyi7x2la.jpg',5,NULL,NULL),(6,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712630/ajubsyfxu506iaolgnls.jpg',6,NULL,NULL),(7,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712657/kjpslqv6mlhl5bvv30j9.jpg',7,NULL,NULL),(8,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712681/lgcguvp8fb5qlhmgbs3g.jpg',8,NULL,NULL),(9,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712717/q0ylb45i8hh5eo66gaj7.jpg',9,NULL,NULL),(10,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712752/zlzldtb7ln6skyuangq6.png',10,NULL,NULL),(11,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712780/vicz2r0fbwbkt3oci8od.jpg',11,NULL,NULL),(12,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712823/d7g3rbjucpgxjxgggfqi.jpg',12,NULL,NULL),(13,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712861/odibghxbqbkpd7jodc7f.jpg',13,NULL,NULL),(14,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712896/gdkpelfepcvvitfjtagg.jpg',14,NULL,NULL),(15,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712934/bvghyajwjwd3uqyp6hny.jpg',15,NULL,NULL),(16,'http://res.cloudinary.com/drlroexjl/image/upload/v1786712978/jp2gyrgh4tenntfwuvmv.jpg',16,NULL,NULL),(17,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713004/mcsbhgvttbdnwqjsbyo3.jpg',17,NULL,NULL),(18,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713032/ks3dmvgmqfilmby9nkq5.jpg',18,NULL,NULL),(19,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713058/garhrchrhuydt48j0dkc.jpg',19,NULL,NULL),(20,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713087/xfchkwutpojjyldjkui2.jpg',20,NULL,NULL),(21,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713120/jsqnjz77eastdsfsygkf.jpg',21,NULL,NULL),(22,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713150/bvemfdzk8lqg7zwyzb3s.jpg',22,NULL,NULL),(23,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713193/oa92n8wojirgjmwefh6q.png',23,NULL,NULL),(24,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713226/fi68xf3o6gek74etlie0.jpg',24,NULL,NULL),(25,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713254/btq8t5yks6yre95bovwn.jpg',25,NULL,NULL),(26,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713298/fuoeqq9koqlo9zkfsmja.jpg',26,NULL,NULL),(27,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713327/b62tdwhpmhydal6s9evv.jpg',27,NULL,NULL),(28,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713356/xg9vpk5i9ab4ckoe7qhf.jpg',28,NULL,NULL),(29,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713418/o1jzbdlvwy2gskrycgck.jpg',29,NULL,NULL),(30,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713446/hrgy5vjxeumxtwj32crp.jpg',30,NULL,NULL),(31,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713474/czn0taecqgpeieh7v35m.jpg',31,NULL,NULL),(32,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713508/ntsye52y0uggadkpmnh0.jpg',32,NULL,NULL),(33,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713534/zj0g1kic088tj5mkjdxe.jpg',33,NULL,NULL),(34,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713589/uwmxxj1go5m6mhah6lyj.jpg',34,NULL,NULL),(35,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713623/c6wsdrccw9wvjbymsht6.jpg',35,NULL,NULL),(36,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713647/ltcfdtsjxryyd8tcfnnq.jpg',36,NULL,NULL),(37,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713677/fc0oornpqcivjopz6adk.jpg',37,NULL,NULL),(38,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713701/ncvfiaffjuxxvbm3qz0a.jpg',38,NULL,NULL),(39,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713729/rr6imet9gfz6ler87do8.jpg',39,NULL,NULL),(40,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713790/sd6vpnpjgaz8pxaltfs2.jpg',40,NULL,NULL),(41,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713829/s8kl1zjp0se94rrgwniy.jpg',41,NULL,NULL),(42,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713864/m3ehgyvfdslx0adqtpqi.jpg',42,NULL,NULL),(43,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713889/sw1dkckavksmovdgmr5p.jpg',43,NULL,NULL),(44,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713917/tifawybcmd4lodlaarqo.jpg',44,NULL,NULL),(45,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713941/twvr2htkjtlcj22pok45.jpg',45,NULL,NULL),(46,'http://res.cloudinary.com/drlroexjl/image/upload/v1786713966/umzsxuy6m5epewnsjcwv.jpg',46,NULL,NULL),(47,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714024/u1zzcrrh0pkktovhip8b.jpg',47,NULL,NULL),(48,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714050/apafnglf4nwlfabeyinj.jpg',48,NULL,NULL),(49,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714078/yu3udaocmaqimyqbljvp.jpg',49,NULL,NULL),(50,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714110/pdznkwykm2mj6tr8szcu.jpg',50,NULL,NULL),(51,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714135/g9hxh3fjaddmu1iui2ar.jpg',51,NULL,NULL),(52,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714157/htcyjhdjj8lyj2pzyz1w.jpg',52,NULL,NULL),(53,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714179/yzjhpiaejtjyto2rv23m.jpg',53,NULL,NULL),(54,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714292/zyhnkjihvmduk4who8s2.jpg',54,NULL,NULL),(55,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714322/ivdgaqzqjpqne5y33eja.jpg',55,NULL,NULL),(56,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714359/vakvqa7ujpi1jgb0aku4.jpg',56,NULL,NULL),(57,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714387/hnigbmmcbeokzhwxyj6g.jpg',57,NULL,NULL),(58,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714411/sb090j6yeudz1ufvmv5y.jpg',58,NULL,NULL),(59,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714440/yxmwaap2xl0pldfp9zrw.jpg',59,NULL,NULL),(60,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714468/vl5mfpefk7m255k6oiwn.jpg',60,NULL,NULL),(61,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714494/a5ufiimok6ujde6ac7ea.jpg',61,NULL,NULL),(62,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714516/q1sqhemdwb10he763cn0.jpg',62,NULL,NULL),(63,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714543/d6fna2iygkp1fsadpqyy.jpg',63,NULL,NULL),(64,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714569/szilb5noyno7o8chaljn.jpg',64,NULL,NULL),(65,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714598/ifrr1urupwai1di6dyyn.jpg',65,NULL,NULL),(66,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714628/istposvpgr952gwgnypv.jpg',66,NULL,NULL),(67,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714655/luazmtkfqi181z6r0fuf.jpg',67,NULL,NULL),(68,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714692/t93zgmzg05qalejkdaof.jpg',68,NULL,NULL),(69,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714720/vkzsblgjaxf5uvfo1tg2.jpg',69,NULL,NULL),(70,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714744/optszuzgtwlfamupkky7.jpg',70,NULL,NULL),(71,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714773/wm1uwh17eeoxb5jozd0g.jpg',71,NULL,NULL),(72,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714819/tfsrvfbbprumhdsi97ut.jpg',72,NULL,NULL),(73,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714856/nj9roikpyzvtiecocgow.jpg',73,NULL,NULL),(74,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714881/e6bkx1j8iuse3kjno5gb.jpg',74,NULL,NULL),(75,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714901/zixlsqkscioxasdqq2mp.jpg',75,NULL,NULL),(76,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714924/gacfa2rjunf7gnqqilqe.jpg',76,NULL,NULL),(77,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714947/z8ifjkldqb40rthlqi8q.jpg',77,NULL,NULL),(78,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714973/ketbxhvowng4rhi9odzz.jpg',78,NULL,NULL),(79,'http://res.cloudinary.com/drlroexjl/image/upload/v1786714994/b3qba26qkl7w4qufszyf.jpg',79,NULL,NULL),(80,'http://res.cloudinary.com/drlroexjl/image/upload/v1786715024/vvivhq3q2dlrroufxggg.jpg',80,NULL,NULL),(81,'http://res.cloudinary.com/drlroexjl/image/upload/v1786870822/ie7wtd0ozvzqnqidzwzj.jpg',81,NULL,NULL),(82,'http://res.cloudinary.com/drlroexjl/image/upload/v1786870844/ozzcuotlydqaytoo4jzx.jpg',82,NULL,NULL),(83,'http://res.cloudinary.com/drlroexjl/image/upload/v1786870873/ypyjffonlxg5rbldky11.jpg',83,NULL,NULL),(84,'http://res.cloudinary.com/drlroexjl/image/upload/v1786870898/qubeuaeu0chhkylemwrr.jpg',84,NULL,NULL),(85,'http://res.cloudinary.com/drlroexjl/image/upload/v1786870933/nveaoumlgsio7jfuxvr7.jpg',85,NULL,NULL),(86,'http://res.cloudinary.com/drlroexjl/image/upload/v1786870960/xd3jn6kisfafms3aojza.jpg',86,NULL,NULL),(87,'http://res.cloudinary.com/drlroexjl/image/upload/v1786870983/lh7wnmxyfnlmfn43kcsv.jpg',87,NULL,NULL),(88,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871008/vnixclulkffu1xag50uy.jpg',88,NULL,NULL),(89,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871031/jy1xp8prs4xb4ptjsyuo.jpg',89,NULL,NULL),(90,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871055/critcpay8orgkvy6kw4g.jpg',90,NULL,NULL),(91,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871078/tzxew7mnl2hasrm0u02h.jpg',91,NULL,NULL),(92,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871110/l3vwfknnpgghzoeuqhkb.jpg',92,NULL,NULL),(93,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871140/popivgbpmqlz598w9qnk.jpg',93,NULL,NULL),(94,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871165/fu9h7hvwkt2j7mrhhpuc.jpg',94,NULL,NULL),(95,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871188/owpnsvlugqtq6yxs6xhb.jpg',95,NULL,NULL),(96,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871213/f0qwp4eqghk3z09md6z3.jpg',96,NULL,NULL),(97,'http://res.cloudinary.com/drlroexjl/image/upload/v1786871237/pgb9byxsvqljgc3fnhbn.jpg',97,NULL,NULL);
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_variant`
--

DROP TABLE IF EXISTS `product_variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_variant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `color` varchar(255) DEFAULT NULL,
  `import_price` decimal(19,2) DEFAULT NULL,
  `price` decimal(19,2) DEFAULT NULL,
  `quantity` int NOT NULL,
  `size` varchar(255) DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgrbbs9t374m9gg43l6tq1xwdj` (`product_id`),
  CONSTRAINT `FKgrbbs9t374m9gg43l6tq1xwdj` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_variant`
--

LOCK TABLES `product_variant` WRITE;
/*!40000 ALTER TABLE `product_variant` DISABLE KEYS */;
INSERT INTO `product_variant` VALUES (1,'Navy',123000.00,168000.00,12,'M',1,NULL,NULL),(3,'Trắng',180000.00,210000.00,12,'M',2,NULL,NULL),(4,'Trắng',168000.00,200000.00,12,'L',3,NULL,NULL),(5,'Navy',250000.00,400000.00,43,'M',4,NULL,NULL),(6,'Nâu',400000.00,560000.00,23,'L',5,NULL,NULL),(7,'Navy',230000.00,400000.00,23,'L',6,NULL,NULL),(8,'Đen',230000.00,260000.00,20,'M',7,NULL,NULL),(9,'Trắng',123456.00,245000.00,12,'M',25,NULL,NULL),(10,'Trắng',560000.00,700000.00,12,'L',49,NULL,NULL),(11,'Nâu',450000.00,600000.00,12,'M',73,NULL,NULL),(12,'Navy',123456.00,200000.00,12,'M',37,NULL,NULL),(13,'Đen',80000.00,150000.00,12,'Freesize',74,NULL,NULL),(14,'Hồng Đen',2300000.00,2700000.00,5,'41',50,NULL,NULL),(15,'Xanh dương',150000.00,250000.00,5,'Freesize',75,NULL,NULL),(16,'Trắng',150000.00,260000.00,5,'M',8,NULL,NULL),(17,'Hồng',560000.00,780000.00,12,'L',9,NULL,NULL),(18,'Trắng',156000.00,270000.00,12,'M',10,NULL,NULL),(19,'Xanh lá',145000.00,280000.00,12,'M',11,NULL,NULL),(20,'Nâu',560000.00,780000.00,5,'M',12,NULL,NULL),(21,'Trắng',234000.00,300000.00,12,'M',13,NULL,NULL),(22,'Đen',450000.00,600000.00,45,'L',14,NULL,NULL),(23,'Xám',250000.00,380000.00,12,'M',26,NULL,NULL),(24,'Đen',450000.00,600000.00,12,'M',38,NULL,NULL),(25,'Trắng',1234000.00,1500000.00,5,'40',51,NULL,NULL),(26,'Nâu',145000.00,280000.00,12,'L',39,NULL,NULL),(27,'Đen',450000.00,500000.00,12,'L',27,NULL,NULL),(28,'Xám xanh',134000.00,250000.00,12,'L',24,NULL,NULL),(29,'Đen',1450000.00,1600000.00,5,'39',72,NULL,NULL),(30,'Đen',450000.00,550000.00,5,'Freesize',76,NULL,NULL),(31,'Vàng',450000.00,500000.00,5,'M',15,NULL,NULL),(32,'Xanh lá',230000.00,300000.00,5,'M',20,NULL,NULL),(33,'Trắng',145000.00,245000.00,12,'M',16,NULL,NULL),(34,'Trắng',450000.00,600000.00,12,'M',23,NULL,NULL),(35,'Vàng',230000.00,260000.00,12,'M',22,NULL,NULL),(36,'Trắng',450000.00,600000.00,12,'M',21,NULL,NULL),(37,'Trắng',250000.00,300000.00,12,'L',17,NULL,NULL),(38,'Đen',450000.00,600000.00,5,'M',18,NULL,NULL),(39,'Đen',450000.00,600000.00,12,'M',28,NULL,NULL),(40,'Đen',45000.00,50000.00,12,'Freesize',96,NULL,NULL),(41,'Đen',230000.00,260000.00,12,'M',19,NULL,NULL),(42,'Đen',230000.00,260000.00,12,'M',29,NULL,NULL),(43,'Trắng',230000.00,260000.00,12,'M',36,NULL,NULL),(44,'Đen',230000.00,260000.00,3,'M',35,NULL,NULL),(45,'Đen',250000.00,300000.00,12,'M',34,NULL,NULL),(46,'Trắng',450000.00,600000.00,12,'M',33,NULL,NULL),(47,'Trắng',123456.00,168000.00,12,'L',30,NULL,NULL),(48,'Xanh',450000.00,600000.00,5,'L',32,NULL,NULL),(49,'Xanh',123456.00,168000.00,5,'M',31,NULL,NULL),(50,'Xám',230000.00,260000.00,12,'M',40,NULL,NULL),(51,'Nâu',230000.00,260000.00,12,'M',48,NULL,NULL),(52,'Đen',250000.00,300000.00,12,'M',47,NULL,NULL),(53,'Trắng',230000.00,260000.00,12,'M',46,NULL,NULL),(54,'Đen',230000.00,300000.00,12,'Freesize',77,NULL,NULL),(55,'Trắng',1230000.00,1340000.00,5,'40',61,NULL,NULL),(56,'Xám',1000000.00,1200000.00,5,'43',52,NULL,NULL),(57,'Xám',450000.00,600000.00,2,'41',62,NULL,NULL),(58,'Đen',560000.00,800000.00,3,'39',53,NULL,NULL),(59,'Navy',134000.00,180000.00,12,'M',41,NULL,NULL),(60,'Trắng',450000.00,600000.00,5,'L',45,NULL,NULL),(61,'Trắng',450000.00,600000.00,12,'M',42,NULL,NULL),(62,'Xám',230000.00,260000.00,12,'M',43,NULL,NULL),(63,'Xanh',250000.00,300000.00,5,'M',44,NULL,NULL),(64,'Đen',450000.00,600000.00,12,'40',54,NULL,NULL),(65,'Đen',760000.00,90000.00,12,'42',60,NULL,NULL),(66,'Trắng',750000.00,850000.00,5,'40',59,NULL,NULL),(67,'Xanh',450000.00,600000.00,4,'39',58,NULL,NULL),(68,'Xám',550000.00,750000.00,4,'41',56,NULL,NULL),(69,'Đen',960000.00,1200000.00,12,'44',55,NULL,NULL),(70,'Đen',450000.00,600000.00,12,'40',57,NULL,NULL),(71,'Đen',230000.00,260000.00,12,'41',63,NULL,NULL),(72,'Đen',450000.00,600000.00,12,'Freesize',78,NULL,NULL),(73,'Xám',13000.00,25000.00,12,'Freesize',79,NULL,NULL),(74,'Trắng',450000.00,600000.00,12,'40',64,NULL,NULL),(75,'Đen',450000.00,600000.00,12,'39',65,NULL,NULL),(76,'Đen',450000.00,600000.00,5,'36',71,NULL,NULL),(77,'Trắng',1200000.00,1500000.00,5,'40',66,NULL,NULL),(78,'Trắng',450000.00,600000.00,5,'40',70,NULL,NULL),(79,'Đen',450000.00,600000.00,5,'40',69,NULL,NULL),(80,'Đen',450000.00,600000.00,5,'Freesize',95,NULL,NULL),(81,'Đen',450000.00,600000.00,4,'Freesize',80,NULL,NULL),(82,'Đen',230000.00,260000.00,4,'Freesize',85,NULL,NULL),(83,'Đen',250000.00,280000.00,5,'Freesize',86,NULL,NULL),(84,'Đỏ',230000.00,260000.00,2,'Freesize',94,NULL,NULL),(85,'Bạc',450000.00,600000.00,5,'Freesize',93,NULL,NULL),(86,'Xanh',450000.00,600000.00,5,'Freesize',87,NULL,NULL),(87,'Xanh lá',230000.00,260000.00,5,'Freesize',88,NULL,NULL),(88,'Đen',230000.00,260000.00,5,'Freesize',92,NULL,NULL),(89,'Trắng',450000.00,600000.00,5,'Freesize',89,NULL,NULL),(90,'Xanh',450000.00,600000.00,6,'Freesize',90,NULL,NULL),(91,'Đen',450000.00,600000.00,4,'Freesize',91,NULL,NULL),(92,'Đen',450000.00,600000.00,5,'Freesize',81,NULL,NULL),(93,'Bạc',230000.00,260000.00,5,'Freesize',82,NULL,NULL),(94,'Đen',450000.00,600000.00,5,'Freesize',84,NULL,NULL),(95,'Bạc',230000.00,260000.00,5,'Freesize',83,NULL,NULL),(96,'Đen',450000.00,600000.00,2,'40',68,NULL,NULL),(97,'Đen',450000.00,600000.00,4,'39',67,NULL,NULL);
/*!40000 ALTER TABLE `product_variant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sepay_transaction`
--

DROP TABLE IF EXISTS `sepay_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sepay_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) DEFAULT NULL,
  `bank_transaction_code` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `expired_at` datetime DEFAULT NULL,
  `note` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `qr_code` varchar(2000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `reconciled` bit(1) DEFAULT NULL,
  `reconciled_at` datetime DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `transaction_at` datetime DEFAULT NULL,
  `transaction_code` varchar(255) DEFAULT NULL,
  `transfer_content` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `updated_at` datetime NOT NULL,
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK99ikyt5nlkoipt8ae9wfejg28` (`order_id`),
  CONSTRAINT `FK99ikyt5nlkoipt8ae9wfejg28` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sepay_transaction`
--

LOCK TABLES `sepay_transaction` WRITE;
/*!40000 ALTER TABLE `sepay_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `sepay_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `avatar` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `login_type` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `password` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `phone_number` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `role` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `user_name` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('admin','https://haycafe.vn/wp-content/uploads/2022/02/Avatar-trang-den.png','admin@gmail.com','default','MTIzNDU2YUBB',NULL,'admin','admin',NULL,NULL),('chuyendizz','https://haycafe.vn/wp-content/uploads/2022/02/Avatar-trang-den.png','chuyendizz@gmail.com','default','MTIzNDU2YUBB',NULL,'user','Dizz Chuyen',NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'fashionstore'
--

--
-- Dumping routines for database 'fashionstore'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-14 16:38:04
