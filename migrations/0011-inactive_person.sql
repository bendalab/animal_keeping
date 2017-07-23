-- -----------------------------------------------------
-- ALTER table `census_person`
-- -----------------------------------------------------
ALTER TABLE `census_person` ADD COLUMN `active` tinyint(1) NOT NULL DEFAULT 1 AFTER `email`;

-- -----------------------------------------------------
-- ALTER table `census_subject`
-- -----------------------------------------------------
ALTER TABLE `census_subject` ADD COLUMN `person_id` int(11) DEFAULT NULL AFTER `source_id`;
ALTER TABLE `census_subject` ADD FOREIGN KEY (`person_id`) REFERENCES `census_person`(`id`);


INSERT INTO `migrations` (`patch_name`) VALUES ('0011-inactiveperson.sql');
