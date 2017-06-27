-- -----------------------------------------------------
-- ALTER table `treatment_type`
-- -----------------------------------------------------
ALTER TABLE `census_treatmenttype` ADD COLUMN `isfinal` tinyint(1) NOT NULL DEFAULT 0 AFTER `invasive`;
ALTER TABLE `census_treatmenttype` ADD COLUMN `target` ENUM('subject', 'housing') NOT NULL DEFAULT 'subject';

UPDATE `census_treatmenttype` SET `isfinal` = 1 where `invasive` = 1;

INSERT INTO `migrations` (`patch_name`) VALUES ('0007-splitfinalinvasive.sql');

