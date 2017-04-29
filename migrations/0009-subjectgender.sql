-- -----------------------------------------------------
-- ALTER table `animal_keeping`.`census_subject`
-- -----------------------------------------------------
ALTER TABLE `animal_keeping`.`census_subject` ADD COLUMN `birthday` date AFTER `alias`;
ALTER TABLE `animal_keeping`.`census_subject` ADD COLUMN `gender` ENUM('female', 'male', 'hermaphrodite', 'unknown') NOT NULL DEFAULT 'unknown' AFTER `birthday`;


INSERT INTO `animal_keeping`.`migrations` (`patch_name`) VALUES ('0009-subjectgender.sql');
