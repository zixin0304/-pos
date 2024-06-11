-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- 主機： 127.0.0.1
-- 產生時間： 2024-06-11 16:22:26
-- 伺服器版本： 10.4.32-MariaDB
-- PHP 版本： 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `db_pos`
--

-- --------------------------------------------------------

--
-- 資料表結構 `order_detail`
--

CREATE TABLE `order_detail` (
  `id` int(11) NOT NULL,
  `order_num` varchar(20) NOT NULL,
  `product_id` varchar(20) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `condensedmilk` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- 傾印資料表的資料 `order_detail`
--

INSERT INTO `order_detail` (`id`, `order_num`, `product_id`, `quantity`, `condensedmilk`) VALUES
(1, 'ord-101', 'p-i-103', 2, 0),
(2, 'ord-102', 'p-t-203', 3, 0),
(3, 'ord-102', 'p-i-105', 1, 0),
(4, 'ord-103', 'p-i-102', 1, 1),
(5, 'ord-103', 'p-i-102', 1, 0);

-- --------------------------------------------------------

--
-- 資料表結構 `product`
--

CREATE TABLE `product` (
  `product_id` varchar(20) NOT NULL,
  `category` varchar(50) NOT NULL,
  `name` varchar(150) NOT NULL,
  `price` int(11) NOT NULL,
  `photo` varchar(200) NOT NULL,
  `description` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- 傾印資料表的資料 `product`
--

INSERT INTO `product` (`product_id`, `category`, `name`, `price`, `photo`, `description`) VALUES
('p-i-101', '經典雪花冰', '牛奶雪花冰', 45, '牛奶雪花冰.jpg', '產品描述'),
('p-i-102', '經典雪花冰', '草莓雪花冰', 55, '草莓雪花冰.jpg', '產品描述'),
('p-i-103', '經典雪花冰', '巧克力雪花冰', 55, '巧克力雪花冰.jpg', '產品描述'),
('p-i-104', '經典雪花冰', '香檳葡萄雪花冰', 60, '香檳葡萄雪花冰.jpg', '產品描述'),
('p-i-105', '水果雪花冰', '新鮮草莓雪花冰', 75, '新鮮草莓雪花冰.jpg', '產品描述'),
('p-i-106', '水果雪花冰', '新鮮芒果雪花冰', 75, '新鮮芒果雪花冰.png', '產品描述'),
('p-i-107', '水果雪花冰', '新鮮西瓜雪花冰', 85, '新鮮西瓜雪花冰.jpg', '產品描述'),
('p-t-201', '配料', '珍珠', 10, '珍珠.jpg', '產品描述'),
('p-t-202', '配料', '抹茶凍', 15, '抹茶凍.jpg', '產品描述'),
('p-t-203', '配料', '布丁', 15, '布丁.jpg', '產品描述');

-- --------------------------------------------------------

--
-- 資料表結構 `sale_order`
--

CREATE TABLE `sale_order` (
  `order_num` varchar(20) NOT NULL,
  `order_date` datetime NOT NULL DEFAULT current_timestamp(),
  `total_price` double(22,0) NOT NULL DEFAULT 0,
  `customer_name` varchar(150) DEFAULT NULL,
  `customer_address` varchar(250) DEFAULT NULL,
  `customer_phone` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- 傾印資料表的資料 `sale_order`
--

INSERT INTO `sale_order` (`order_num`, `order_date`, `total_price`, `customer_name`, `customer_address`, `customer_phone`) VALUES
('ord-101', '2024-06-09 01:47:16', 110, '李大同', '高雄市楠梓區大學路一號', '093256789'),
('ord-102', '2024-06-09 01:47:16', 120, '路人甲', '無地址', '無電話'),
('ord-103', '2024-06-09 01:48:23', 110, '無姓名', '無地址', '無電話');

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `order_detail`
--
ALTER TABLE `order_detail`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_order_detail_product` (`product_id`),
  ADD KEY `FK_order_detail_sale_order` (`order_num`);

--
-- 資料表索引 `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`);

--
-- 資料表索引 `sale_order`
--
ALTER TABLE `sale_order`
  ADD PRIMARY KEY (`order_num`);

--
-- 在傾印的資料表使用自動遞增(AUTO_INCREMENT)
--

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `order_detail`
--
ALTER TABLE `order_detail`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- 已傾印資料表的限制式
--

--
-- 資料表的限制式 `order_detail`
--
ALTER TABLE `order_detail`
  ADD CONSTRAINT `FK_order_detail_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  ADD CONSTRAINT `FK_order_detail_sale_order` FOREIGN KEY (`order_num`) REFERENCES `sale_order` (`order_num`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
