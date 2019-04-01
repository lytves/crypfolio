-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema crypfolio
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `crypfolio` ;

-- -----------------------------------------------------
-- Schema crypfolio
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `crypfolio` DEFAULT CHARACTER SET utf8 ;
USE `crypfolio` ;

-- -----------------------------------------------------
-- Table `crypfolio`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `crypfolio`.`users` ;

CREATE TABLE IF NOT EXISTS `crypfolio`.`users` (
  `us_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `us_email` VARCHAR(255) NOT NULL,
  `us_password` VARCHAR(64) NOT NULL,
  `us_signup_datetime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `us_is_email_verified` TINYINT(1) NOT NULL DEFAULT 0,
  `us_email_verif_code` VARCHAR(36) NULL,
  `us_email_verif_code_request_datetime` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `us_password_reset_code` VARCHAR(36) NULL,
  `us_password_reset_code_datetime` DATETIME NULL,
  PRIMARY KEY (`us_id`),
  UNIQUE INDEX `us_email_UNIQUE` (`us_email` ASC),
  INDEX `us_email_verif_code_INDEX` (`us_email_verif_code` ASC),
  INDEX `us_password_reset_code_INDEX` (`us_password_reset_code` ASC),
  UNIQUE INDEX `us_id_UNIQUE` (`us_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `crypfolio`.`portfolios`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `crypfolio`.`portfolios` ;

CREATE TABLE IF NOT EXISTS `crypfolio`.`portfolios` (
  `users_us_id` BIGINT(20) NOT NULL,
  `port_name` VARCHAR(128) NOT NULL,
  `port_showed_currency` ENUM('USD', 'EUR', 'BTC', 'ETH') NOT NULL DEFAULT 'USD',
  `port_is_share` TINYINT(1) NOT NULL DEFAULT 0,
  `port_share_link` VARCHAR(8) NOT NULL,
  `port_is_showed_amounts` TINYINT(1) NOT NULL DEFAULT 0,
  `port_net_cost_usd` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `port_net_cost_eur` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `port_net_cost_btc` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `port_net_cost_eth` DECIMAL(20,8) NOT NULL DEFAULT 0,
  PRIMARY KEY (`users_us_id`),
  INDEX `fk_portfolios_users1_idx` (`users_us_id` ASC),
  UNIQUE INDEX `users_us_id_UNIQUE` (`users_us_id` ASC),
  CONSTRAINT `fk_portfolios_users1`
    FOREIGN KEY (`users_us_id`)
    REFERENCES `crypfolio`.`users` (`us_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `crypfolio`.`coins`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `crypfolio`.`coins` ;

CREATE TABLE IF NOT EXISTS `crypfolio`.`coins` (
  `coin_id` BIGINT(20) NOT NULL,
  `coin_name` VARCHAR(255) NOT NULL,
  `coin_symbol` VARCHAR(127) NOT NULL,
  `coin_slug` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`coin_id`),
  UNIQUE INDEX `coin_slug_UNIQUE` (`coin_slug` ASC),
  UNIQUE INDEX `coin_id_UNIQUE` (`coin_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `crypfolio`.`items`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `crypfolio`.`items` ;

CREATE TABLE IF NOT EXISTS `crypfolio`.`items` (
  `item_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `item_amount` DECIMAL(20,8) NOT NULL,
  `item_showed_currency` ENUM('USD', 'EUR', 'BTC', 'ETH') NOT NULL DEFAULT 'USD',
  `item_net_cost_usd` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `item_net_cost_eur` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `item_net_cost_btc` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `item_net_cost_eth` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `item_is_archived` TINYINT(1) NOT NULL DEFAULT 0,
  `item_average_bought_price_usd` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `item_average_bought_price_eur` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `item_average_bought_price_btc` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `item_average_bought_price_eth` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `coins_coin_id` BIGINT(20) NOT NULL,
  `portfolios_users_us_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`item_id`),
  INDEX `fk_items_coins1_idx` (`coins_coin_id` ASC),
  INDEX `fk_items_portfolios1_idx` (`portfolios_users_us_id` ASC),
  UNIQUE INDEX `item_id_UNIQUE` (`item_id` ASC),
  CONSTRAINT `fk_items_coins1`
    FOREIGN KEY (`coins_coin_id`)
    REFERENCES `crypfolio`.`coins` (`coin_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_items_portfolios1`
    FOREIGN KEY (`portfolios_users_us_id`)
    REFERENCES `crypfolio`.`portfolios` (`users_us_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `crypfolio`.`transactions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `crypfolio`.`transactions` ;

CREATE TABLE IF NOT EXISTS `crypfolio`.`transactions` (
  `trans_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `trans_amount` DECIMAL(20,8) NOT NULL,
  `trans_bought_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `trans_type` ENUM('BUY', 'SELL') NOT NULL DEFAULT 'BUY',
  `trans_bought_currency` ENUM('USD', 'EUR', 'BTC', 'ETH') NOT NULL DEFAULT 'USD',
  `trans_bought_price_usd` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `trans_bought_price_eur` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `trans_bought_price_btc` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `trans_bought_price_eth` DECIMAL(20,8) NOT NULL DEFAULT 0,
  `trans_comment` TEXT NULL,
  `items_item_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`trans_id`),
  INDEX `fk_positions_items1_idx` (`items_item_id` ASC),
  CONSTRAINT `fk_positions_items1`
    FOREIGN KEY (`items_item_id`)
    REFERENCES `crypfolio`.`items` (`item_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `crypfolio`.`users_watch_coins`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `crypfolio`.`users_watch_coins` ;

CREATE TABLE IF NOT EXISTS `crypfolio`.`users_watch_coins` (
  `users_us_id` BIGINT(20) NOT NULL,
  `coins_coin_id` BIGINT(20) NOT NULL,
  `us_watchcoin_currency` ENUM('USD', 'EUR', 'BTC', 'ETH') NOT NULL DEFAULT 'USD',
  PRIMARY KEY (`users_us_id`, `coins_coin_id`),
  INDEX `fk_users_has_coins_coins1_idx` (`coins_coin_id` ASC),
  INDEX `fk_users_has_coins_users1_idx` (`users_us_id` ASC),
  CONSTRAINT `fk_users_has_coins_users1`
    FOREIGN KEY (`users_us_id`)
    REFERENCES `crypfolio`.`users` (`us_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_has_coins_coins1`
    FOREIGN KEY (`coins_coin_id`)
    REFERENCES `crypfolio`.`coins` (`coin_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `crypfolio`.`users_has_users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `crypfolio`.`users_has_users` ;

CREATE TABLE IF NOT EXISTS `crypfolio`.`users_has_users` (
  `users_us_id` BIGINT(20) NOT NULL,
  `users_us_followee_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`users_us_id`, `users_us_followee_id`),
  INDEX `fk_users_has_users_users2_idx` (`users_us_followee_id` ASC),
  INDEX `fk_users_has_users_users1_idx` (`users_us_id` ASC),
  CONSTRAINT `fk_users_has_users_users1`
    FOREIGN KEY (`users_us_id`)
    REFERENCES `crypfolio`.`users` (`us_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_has_users_users2`
    FOREIGN KEY (`users_us_followee_id`)
    REFERENCES `crypfolio`.`users` (`us_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
