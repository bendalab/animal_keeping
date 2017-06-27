-- -----------------------------------------------------
-- Table `changelog`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `changelog` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `action` VARCHAR(500) NOT NULL,
  `created_time` DATETIME NOT NULL,
  `target_type` VARCHAR(200) NOT NULL,
  `target_id` INT NOT NULL, 
  PRIMARY KEY (`id`)) 
ENGINE = InnoDB;
