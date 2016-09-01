-- select 'drop ' || object_type || ' ' || object_name || ';'
-- from all_objects
-- where owner = 'ACCOUNTING'
--   and object_type in ('SEQUENCE', 'TABLE', 'TRIGGER');

create table staff (
    staff_id serial constraint pk_staff primary key not null,
    last_name character varying(255) not null,
    first_name character varying(255) not null,
    login character varying(255) unique not null,
    password character varying(255) not null,
    internal_email character varying(255) not null,
    internal_phone_number character varying(255),
    allocation character varying(255) not null,
    duty character varying(255) not null,
    pay_scale numeric not null,
    hire_date date default current_date not null,
    account_number character varying(255)
);

create table bills (
    bill_id serial constraint pk_bills primary key not null,
    company_id numeric not null,
    bill_date date default current_date not null,
    total_price_excluding_vat numeric not null,
    total_price_including_vat numeric not null,
    validated char(1) default 0 not null,
    accountant_validater character varying(255),
    sent char(1) default 0 not null,
    bill_support character varying(255) not null, -- mail / paper
    paid char(1) default 0 not null
);

create table bill_items (
    item_id serial constraint pk_bill_items primary key not null,
    bill_id numeric not null,
    movement_id numeric not null,
    container_id numeric not null,
    destination_id numeric not null,
    price numeric not null
);

create table salaries (
    salary_id serial constraint pk_salaries primary key not null,
    staff_id integer constraint fk_salaries_staff_id references staff(staff_id) not null,
    due_date date default current_date not null,
    amount numeric not null,
    onss_fee numeric not null,
    deduction numeric not null,
    sent char(1) default 0 not null,
    paid char(1) default 0 not null
);

create table bonuses (
    bonus_id serial constraint pk_bonuses primary key not null,
    amount numeric not null,
    bonus_date date default current_date not null,
    reason character varying(255) not null,
    source character varying(255) not null,
    paid char(1) default 0 not null
);

create table prices(
    price_id serial constraint pk_prices primary key not null,
    price_type character varying(255) not null, -- don't ask, I don't know either
    stuff_used character varying(255) not null,
    oil char(1) default 0 not null,
    last_update timestamp default current_timestamp not null
);





insert into staff (
    last_name, first_name, login, password, internal_email, allocation, duty, pay_scale, account_number
) values (
    'Bobington', 'Bob', 'bob', 'bob', 'bob@bob', 'somewhere', 'accountant', 1, 'huehuehue'
);

insert into bills (
    company_id, total_price_excluding_vat, total_price_including_vat, bill_support
) values (1, 520, 629.2, 'mail');
