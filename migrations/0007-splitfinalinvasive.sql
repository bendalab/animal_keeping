-- -----------------------------------------------------
-- ALTER table `animalkeeping`.`treatment_type`
-- -----------------------------------------------------
ALTER TABLE `animal_keeping`.`census_treatmenttype` ADD COLUMN `isfinal` tinyint(1) NOT NULL DEFAULT 0 AFTER `invasive`;
ALTER TABLE `animal_keeping`.`census_treatmenttype` ADD COLUMN `target` ENUM(`subject`, `housing` NOT NULL) DEFAULT `subject`;

UPDATE `animal_keeping`.`census_treatmenttype` SET `isfinal` = 1 where `invasive` = 1;

INSERT INTO `animal_keeping`.`migrations` (`patch_name`) VALUES ('0007-splitfinalinvasive.sql');

