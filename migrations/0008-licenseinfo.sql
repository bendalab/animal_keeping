-- -----------------------------------------------------
-- ALTER table `license`
-- -----------------------------------------------------
ALTER TABLE `census_license` ADD COLUMN `agency` varchar(512) AFTER `name`;
ALTER TABLE `census_license` CHANGE `agency` `agency` varchar(512) COMMENT 'The filing agency.';
ALTER TABLE `census_license` ADD COLUMN `person_id` int(11) AFTER `number`;
ALTER TABLE `census_license` CHANGE `person_id` `person_id` int(11) COMMENT 'The responsible person.';
ALTER TABLE `census_license` ADD COLUMN `deputy_person_id` int(11) AFTER `person_id`;
ALTER TABLE `census_license` CHANGE `deputy_person_id` `deputy_person_id` int(11) COMMENT 'The deputy of the responsible person.';

ALTER TABLE `census_license` ADD FOREIGN KEY (`person_id`) REFERENCES `census_person`(`id`); 
ALTER TABLE `census_license` ADD FOREIGN KEY (`deputy_person_id`) REFERENCES `census_person`(`id`); 

INSERT INTO `migrations` (`patch_name`) VALUES ('0008-licenseinfo.sql');
