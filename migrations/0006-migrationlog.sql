-- -----------------------------------------------------
-- Table `animalkeeping`.`migrations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `animal_keeping`.`migrations` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `patch_name` VARCHAR(200) NOT NULL UNIQUE,
  `migrate_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


INSERT INTO `animal_keeping`.`migrations` (`patch_name`) VALUES ('0001-initial.sql');
INSERT INTO `animal_keeping`.`migrations` (`patch_name`) VALUES ('0002-changelog.sql');
INSERT INTO `animal_keeping`.`migrations` (`patch_name`) VALUES ('0003-databaseuser.sql');
INSERT INTO `animal_keeping`.`migrations` (`patch_name`) VALUES ('0004-linkuserandperson.sql');
INSERT INTO `animal_keeping`.`migrations` (`patch_name`) VALUES ('0005-extendedchangelog.sql');
INSERT INTO `animal_keeping`.`migrations` (`patch_name`) VALUES ('0006-migrationlog.sql');


