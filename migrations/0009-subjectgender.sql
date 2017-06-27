-- -----------------------------------------------------
-- ALTER table `census_subject`
-- -----------------------------------------------------
ALTER TABLE `census_subject` ADD COLUMN `birthday` date AFTER `alias`;
ALTER TABLE `census_subject` ADD COLUMN `gender` ENUM('female', 'male', 'hermaphrodite', 'unknown') NOT NULL DEFAULT 'unknown' AFTER `birthday`;


INSERT INTO `migrations` (`patch_name`) VALUES ('0009-subjectgender.sql');
