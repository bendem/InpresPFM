create table users (
    user_id    number constraint pk_users primary key,
    username   varchar2(63),
    password   varchar2(63)
);

create sequence user_seq;
create or replace trigger user_autoinc
before insert on users
for each row begin
    select user_seq.nextval into :new.user_id from dual;
end;
/

create table companies (
    company_id number constraint pk_companies primary key,
    name       varchar2(63),
    mail       varchar2(63),
    phone      varchar2(63),
    address    varchar2(63)
);

create sequence company_seq;
create or replace trigger company_autoinc
before insert on companies
for each row begin
    select company_seq.nextval into :new.company_id from dual;
end;
/

create table containers (
    container_id varchar2(63) constraint pk_containers primary key,
    company_id   number       constraint fk_containers_company_id references companies(company_id),
    content_type varchar2(63),
    dangers      varchar2(63)
);

create table parcs (
    x            number(4, 0),
    y            number(4, 0),
    container_id varchar2(63) constraint fk_parcs_container_id references containers(container_id),
    constraint pk_parcs primary key (x, y)
);

create table reservations (
    date_arrival date,
    x number(4,0),
    y number(4,0),
    destination_id number,
    reservation_id varchar2(22),
    constraint fk_reservations_parcs foreign key (x,y) references parcs(x,y),
    constraint pk_reservations primary key (x, y, date_arrival)
);

create table transporters (
    transporter_id varchar2(63) constraint pk_transporters primary key,
    company_id     number       constraint fk_transporters_company_id references companies(company_id),
    info           varchar2(127)
);

create table destinations (
    destination_id number constraint pk_destinations primary key,
    city           varchar2(63),
    distance_road  number,
    distance_boat  number,
    distance_train number
);

create sequence destination_seq;
create or replace trigger destination_autoinc
before insert on destinations
for each row begin
    select destination_seq.nextval into :new.destination_id from dual;
end;
/

create table movements (
    movement_id        number       constraint pk_movements primary key,
    container_id       varchar2(63) constraint fk_movements_container_id references containers(container_id),
    company_id         number       constraint fk_movements_company_id references companies(company_id),
    transporter_id_in  varchar2(63) constraint fk_movements_transporter_id_in references transporters(transporter_id),
    transporter_id_out varchar2(63) constraint fk_movements_transporter_id_ou references transporters(transporter_id),
    date_arrival       date,
    date_departure     date,
    weight             number,
    destination_id     number       constraint fk_movements_destination_id references destinations(destination_id)
);

create sequence movement_seq;
create or replace trigger movement_autoinc
before insert on movements
for each row begin
    select movement_seq.nextval into :new.movement_id from dual;
end;
/

exit
