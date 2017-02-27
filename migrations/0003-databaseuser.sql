-- -----------------------------------------------------
-- Table `animalkeeping`.`DatabaseUserType`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `animal_keeping`.`DatabaseUserType` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  `privileges` VARCHAR(500) NOT NULL,
  PRIMARY KEY (`id`)) 
ENGINE = InnoDB;
