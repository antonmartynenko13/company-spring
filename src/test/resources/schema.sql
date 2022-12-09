DROP TABLE IF EXISTS project_position;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS report;

CREATE TABLE department (
  id SERIAL PRIMARY KEY,
  title text NOT NULL UNIQUE
);

CREATE TABLE users (
   id SERIAL PRIMARY KEY,
   first_name text NOT NULL,
   last_name text NOT NULL,
   email text NOT NULL UNIQUE,
   --password text NOT NULL,
   job_title text NOT NULL,
   department_id integer NOT NULL REFERENCES department
);

CREATE TABLE project (
  id SERIAL PRIMARY KEY,
  title text NOT NULL UNIQUE,
  start_date DATE NOT NULL,
  end_date DATE
);

CREATE TABLE project_position (
  id SERIAL PRIMARY KEY,
  user_id integer REFERENCES users,
  project_id integer REFERENCES project,
  position_start_date DATE NOT NULL,
  position_end_date DATE,
  position_title text NOT NULL,
  occupation text NOT NULL,
  UNIQUE(id, user_id, project_id)
);

--CREATE TYPE report_type_enum AS ENUM ('WORKLOAD', 'AVAILABILITY');

CREATE TABLE report (
  id SERIAL PRIMARY KEY,
  --report_type report_type_enum NOT NULL,
  report_type text NOT NULL,
  binary_data bytea NOT NULL,
  creation_date DATE default current_date
);