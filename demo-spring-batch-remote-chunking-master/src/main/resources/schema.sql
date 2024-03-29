
create table user (
  id uuid not null,
  username varchar(100) not null,
  password varchar(200) not null,
  name varchar(200) not null,
  email varchar(200) not null,
  address varchar(200) null,
  city varchar(200) null,
  country varchar(200) null,
  postcode varchar(20) null,
  coordinates varchar(200) null,
  timezone varchar(200) null,
  nationality char(2) null,
  created_by varchar(200) not null
);

alter table user add constraint user_pk primary key (id);
