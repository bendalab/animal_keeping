-- -----------------------------------------------------
-- Table `changelog`
-- -----------------------------------------------------

ALTER TABLE `changelog` ADD `change_set` VARCHAR(1024) AFTER who;

INSERT INTO `migrations` (`patch_name`) VALUES ('0010-changelogquery.sql');
