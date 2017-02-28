-- -----------------------------------------------------
-- Table `animalkeeping`.`databaseuser`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `animal_keeping`.`databaseuser` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL UNIQUE,
  `type_id` INT NOT NULL,
  `person_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `person_id`
    FOREIGN KEY (`person_id`)
    REFERENCES `animal_keeping`.`census_person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `type_id`
    FOREIGN KEY (`type_id`)	
    REFERENCES `animal_keeping`.`DatabaseUserType` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
