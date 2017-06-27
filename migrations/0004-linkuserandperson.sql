-- -----------------------------------------------------
-- Table `databaseuser`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `databaseuser` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL UNIQUE,
  `type_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `du_person_id`
    FOREIGN KEY (`id`)
    REFERENCES `census_person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `du_type_id`
    FOREIGN KEY (`type_id`)	
    REFERENCES `DatabaseUserType` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
