-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 16, 2025 at 04:55 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `perfstoredb`
--

-- --------------------------------------------------------

--
-- Table structure for table `ilanlar`
--

CREATE TABLE `ilanlar` (
  `id` int(11) NOT NULL,
  `aciklama` text DEFAULT NULL,
  `ilan_adi` varchar(255) DEFAULT NULL,
  `ilan_fiyati` double NOT NULL,
  `ilan_sahibi` varchar(255) DEFAULT NULL,
  `ilan_tarihi` datetime(6) DEFAULT NULL,
  `image_file_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ilanlar`
--

INSERT INTO `ilanlar` (`id`, `aciklama`, `ilan_adi`, `ilan_fiyati`, `ilan_sahibi`, `ilan_tarihi`, `image_file_name`) VALUES
(1, 'Anında teslim 5000 gerçek aktif takipçi', 'Instagram 5000 Takipçi', 499, 'Tugistore', '2025-12-09 20:38:42.000000', 'instatakipci.webp'),
(2, 'Reklamsız youtube deneyimi', '1 Aylık Youtube Premium', 25, 'Alpstore', '2025-12-09 20:38:42.000000', 'youtubepremium.png'),
(3, 'Uygun fiyata windows aktivasyon anahtarı', 'Windows 10/11 Key', 20, 'admin', '2025-12-10 00:03:59.000000', '1765314239147 windows-10-11-pro-retail-lisans-key-24314592.webp'),
(4, '20-200 tl arası değerde valorant skinleri', 'Rastegele valorant silah skini', 45, 'admin', '2025-12-10 00:05:45.000000', '1765314345541 Valorant-skins-bundle.jpg');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ilanlar`
--
ALTER TABLE `ilanlar`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ilanlar`
--
ALTER TABLE `ilanlar`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
