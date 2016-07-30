CREATE TABLE course (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  description TEXT NOT NULL,
  owner VARCHAR(128) NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT course_unique_name_owner UNIQUE(name, owner)
);

CREATE TABLE course_user (
  course_id BIGINT NOT NULL REFERENCES course(id) ON DELETE CASCADE ON UPDATE CASCADE,
  user_id VARCHAR(128) NOT NULL,
  role INT NOT NULL
);

CREATE TABLE lab (
  course_id BIGINT NOT NULL REFERENCES course(id) ON DELETE CASCADE ON UPDATE CASCADE,
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  description TEXT NOT NULL,
  created_at DATETIME NOT NULL,
  created_by VARCHAR(128) NOT NULL,
  config MEDIUMBLOB DEFAULT NULL,
  published BOOLEAN DEFAULT FALSE,
  submission_deadline DATETIME DEFAULT NULL,
  allow_late_submissions BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (course_id, id),
  KEY lab_key_id (id),
  CONSTRAINT lab_unique_course_id_name UNIQUE(course_id, name)
);

CREATE TABLE submission (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user_id VARCHAR(128) NOT NULL,
  lab_id BIGINT NOT NULL REFERENCES lab(id) ON DELETE CASCADE ON UPDATE CASCADE,
  submitted_at DATETIME NOT NULL,
  submission_type VARCHAR(32) NOT NULL,
  submission_data MEDIUMBLOB NOT NULL
);