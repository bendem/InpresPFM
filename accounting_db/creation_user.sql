create role role_accounting not identified;
grant alter session to role_accounting;
grant create database link to role_accounting;
grant create session to role_accounting;
grant create procedure to role_accounting;
grant create sequence to role_accounting;
grant create table to role_accounting;
grant create trigger to role_accounting;
grant create type to role_accounting;
grant create synonym to role_accounting;
grant create view to role_accounting;
grant create job to role_accounting;
grant create materialized view to role_accounting;

create user accounting identified by bleh
    default tablespace users
    temporary tablespace temp
    profile default
    account unlock;
alter user accounting quota unlimited on users;
grant role_accounting to accounting;

exit
