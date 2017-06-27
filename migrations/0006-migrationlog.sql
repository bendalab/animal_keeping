-- -----------------------------------------------------
-- Table `migrations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `migrations` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `patch_name` VARCHAR(200) NOT NULL UNIQUE,
  `migrate_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


INSERT INTO `migrations` (`patch_name`) VALUES ('0001-initial.sql');
INSERT INTO `migrations` (`patch_name`) VALUES ('0002-changelog.sql');
INSERT INTO `migrations` (`patch_name`) VALUES ('0003-databaseuser.sql');
INSERT INTO `migrations` (`patch_name`) VALUES ('0004-linkuserandperson.sql');
INSERT INTO `migrations` (`patch_name`) VALUES ('0005-extendedchangelog.sql');
INSERT INTO `migrations` (`patch_name`) VALUES ('0006-migrationlog.sql');


