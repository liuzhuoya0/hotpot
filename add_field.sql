USE db_diancan;

ALTER TABLE t_order ADD COLUMN IF NOT EXISTS number_of_diners INT DEFAULT 1;
