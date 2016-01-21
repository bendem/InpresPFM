create table staff (
    staff_id number constraint pk_staff primary key not null,
    last_name varchar2(255) not null,
    first_name varchar2(255) not null,
    login varchar2(255) unique not null,
    password varchar2(255) not null,
    internal_email varchar2(255) not null,
    internal_phone_number varchar2(255),
    allocation varchar2(255) not null,
    duty varchar2(255) not null,
    pay_scale number not null,
    hire_date date default current_date not null,
    account_number varchar2(255)
);

create table bills (
    bill_id number constraint pk_bills primary key not null,
    company_id number not null,
    bill_date date default current_date not null,
    total_price_excluding_vat number not null,
    total_price_including_vat number not null,
    validated char(1) default 0 not null,
    accountant_validater varchar2(255),
    sent char(1) default 0 not null,
    bill_support varchar2(255) not null, -- mail / paper
    paid char(1) default 0 not null
);

create table bill_items (
    item_id number constraint pk_bill_items primary key not null,
    bill_id number not null,
    movement_id number not null,
    container_id number not null,
    destination_id number not null,
    price number not null
);

create table salaries (
    salary_id number constraint pk_salaries primary key not null,
    staff_id number constraint fk_salaries_staff_id references staff(staff_id) not null,
    due_date date default current_date not null,
    amount number not null,
    onss_fee number not null,
    deduction number not null,
    sent char(1) default 0 not null,
    paid char(1) default 0 not null
);

create table bonuses (
    bonus_id number constraint pk_bonuses primary key not null,
    amount number not null,
    bonus_date date default current_date not null,
    reason varchar2(255) not null,
    source varchar2(255) not null,
    paid char(1) default 0 not null
);

create table prices(
    price_id number constraint pk_prices primary key not null,
    price_type varchar2(255) not null, -- don't ask, I don't know either
    stuff_used varchar2(255) not null,
    oil char(1) default 0 not null,
    last_update timestamp default current_timestamp not null
);





create sequence staff_seq;
create or replace trigger staff_autoinc
before insert on staff
for each row begin
    select staff_seq.nextval into :new.staff_id from dual;
end;
/

create sequence bills_seq;
create or replace trigger bills_autoinc
before insert on bills
for each row begin
    select bills_seq.nextval into :new.bill_id from dual;
end;
/

create sequence bill_items_seq;
create or replace trigger bill_items_autoinc
before insert on bill_items
for each row begin
    select bill_items_seq.nextval into :new.item_id from dual;
end;
/

create sequence salaries_seq;
create or replace trigger salaries_autoinc
before insert on salaries
for each row begin
    select salaries_seq.nextval into :new.salary_id from dual;
end;
/

create sequence bonuses_seq;
create or replace trigger bonuses_autoinc
before insert on bonuses
for each row begin
    select bonuses_seq.nextval into :new.bonus_id from dual;
end;
/

create sequence prices_seq;
create or replace trigger prices_autoinc
before insert on prices
for each row begin
    select prices_seq.nextval into :new.price_id from dual;
end;
/
