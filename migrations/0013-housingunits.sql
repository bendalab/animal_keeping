-- -----------------------------------------------------
-- ALTER table `census_quota`
-- -----------------------------------------------------
ALTER TABLE `census_housingtype` ADD COLUMN `can_have_child_units` TINYINT(1) NOT NULL DEFAULT 1 AFTER `description`;
ALTER TABLE `census_housingtype` ADD COLUMN `can_hold_subjects` TINYINT(1) NOT NULL DEFAULT 1 AFTER `description`;

INSERT INTO `migrations` (`patch_name`) VALUES ('0013-housingunits.sql');
