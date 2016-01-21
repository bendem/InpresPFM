drop table langauges;
drop table reservations;
drop table order_reservation;
drop table order_item;
drop table items;
drop table orders;
drop table users;
drop sequence item_seq;

create table users (
    username   varchar2(63) constraint pk_users primary key,
    password   varchar2(63) not null,
    email      varchar2(63)
);

create table orders (
    order_id number constraint pk_orders primary key,
    username varchar2(63) constraint fk_orders_users references users(username) not null
);

create table order_reservation(
    order_id number,
    reservation_day date,
    number_place number,
    constraint pk_order_reservation primary key (order_id, reservation_day)
);

create table items (
    item_id number constraint pk_items primary key,
    name    varchar2(63) not null,
    price   number,
    stock   number
);

create sequence item_seq;
create or replace trigger item_autoinc
before insert on items
for each row begin
    select item_seq.nextval into :new.item_id from dual;
end;
/

create table order_item (
    order_id number constraint fk_orders references orders(order_id),
    item_id number constraint fk_items references items(item_id),
    quantity number not null,
    constraint pk_orders_items primary key (order_id, item_id)
);

create table reservations (
    reservation_day date constraint pk_reservation primary key,
    place_sold number
);

create table languages (
    language_id varchar2(7) constraint pk_languages primary key,
    language_name varchar2(63)
);
/
