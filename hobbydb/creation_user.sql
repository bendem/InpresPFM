create role role_hobby not identified;
grant alter session to role_hobby;
grant create database link to role_hobby;
grant create session to role_hobby;
grant create procedure to role_hobby;
grant create sequence to role_hobby;
grant create table to role_hobby;
grant create trigger to role_hobby;
grant create type to role_hobby;
grant create synonym to role_hobby;
grant create view to role_hobby;
grant create job to role_hobby;
grant create materialized view to role_hobby;

create user dbhobby identified by bleh
    default tablespace users
    temporary tablespace temp
    profile default
    account unlock;
alter user dbhobby quota unlimited on users;
grant role_hobby to dbhobby;

exit
