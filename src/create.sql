
CREATE TABLE IF NOT EXISTS `student`(
    `id` INT UNSIGNED AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `company` VARCHAR(40),
    `date` timestamp,
    PRIMARY KEY ( `id` )
    );