create role role_shop not identified;
grant alter session to role_shop;
grant create database link to role_shop;
grant create session to role_shop;
grant create procedure to role_shop;
grant create sequence to role_shop;
grant create table to role_shop;
grant create trigger to role_shop;
grant create type to role_shop;
grant create synonym to role_shop;
grant create view to role_shop;
grant create job to role_shop;
grant create materialized view to role_shop;

create user dbshop identified by bleh
    default tablespace users
    temporary tablespace temp
    profile default
    account unlock;
alter user dbshop quota unlimited on users;
grant role_shop to dbshop;
