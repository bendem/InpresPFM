create role role_decision not identified;
grant alter session to role_decision;
grant create database link to role_decision;
grant create session to role_decision;
grant create procedure to role_decision;
grant create sequence to role_decision;
grant create table to role_decision;
grant create trigger to role_decision;
grant create type to role_decision;
grant create synonym to role_decision;
grant create view to role_decision;
grant create job to role_decision;
grant create materialized view to role_decision;

create user dbdecision identified by bleh
    default tablespace users
    temporary tablespace temp
    profile default
    account unlock;
alter user dbdecision quota unlimited on users;
grant role_decision to dbdecision;

exit
