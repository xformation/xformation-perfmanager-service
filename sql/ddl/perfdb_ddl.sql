CREATE TABLE collector
(
   id bigint PRIMARY KEY NOT NULL,
   name varchar(255),
   type varchar(255),
   datasource varchar(255),
   description varchar(5000),
   created_by varchar(255),
   created_on timestamp,
   updated_by varchar(255),
   updated_on timestamp
);