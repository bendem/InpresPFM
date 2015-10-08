create role role_traffic not identified;
grant alter session to role_traffic;
grant create database link to role_traffic;
grant create session to role_traffic;
grant create procedure to role_traffic;
grant create sequence to role_traffic;
grant create table to role_traffic;
grant create trigger to role_traffic;
grant create type to role_traffic;
grant create synonym to role_traffic;
grant create view to role_traffic;
grant create job to role_traffic;
grant create materialized view to role_traffic;

create user dbtraffic identified by bleh
    default tablespace users
    temporary tablespace temp
    profile default
    account unlock;
alter user dbtraffic quota unlimited on users;
grant role_traffic to dbtraffic;

exit
