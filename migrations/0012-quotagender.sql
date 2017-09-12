-- -----------------------------------------------------
-- ALTER table `census_quota`
-- -----------------------------------------------------
ALTER TABLE `census_quota` ADD COLUMN `gender` ENUM('female', 'male', 'hermaphrodite', 'unknown') NOT NULL DEFAULT 'unknown' AFTER `species_id`;


INSERT INTO `migrations` (`patch_name`) VALUES ('0012-quotagender.sql');
