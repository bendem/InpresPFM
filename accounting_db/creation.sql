create table staff (
    staff_id number constraint pk_staff primary key not null,
    last_name varchar2(255) not null,
    first_name varchar2(255) not null,
    login varchar2(255) unique not null,
    password varchar2(255) not null,
    internal_email varchar2(255) not null,
    internal_phone_number varchar2(255)
);

create sequence staff_seq;
create or replace trigger staff_autoinc
before insert on staff
for each row begin
    select staff_seq.nextval into :new.staff_id from dual;
end;
/
