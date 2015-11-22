create table users (
    user_id    number constraint pk_users primary key not null,
    username   varchar2(63) constraint u_users_username unique not null,
    password   varchar2(63) not null
);

create sequence user_seq;
create or replace trigger user_autoinc
before insert on users
for each row begin
    select user_seq.nextval into :new.user_id from dual;
end;
/

create table companies (
    company_id number constraint pk_companies primary key not null,
    name       varchar2(63) unique not null,
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
    container_id varchar2(63) constraint pk_containers primary key not null,
    company_id   number       constraint fk_containers_company_id references companies(company_id) not null,
    content_type varchar2(63) not null,
    dangers      varchar2(63)
);

create table parcs (
    x            number(4, 0) not null,
    y            number(4, 0) not null,
    container_id varchar2(63) constraint fk_parcs_container_id references containers(container_id),
    constraint pk_parcs primary key (x, y)
);

create table destinations (
    destination_id number constraint pk_destinations primary key not null,
    city           varchar2(63) not null,
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

create table reservations (
    reservation_id varchar2(50) primary key,
    date_arrival date not null,
    destination_id number references destinations(destination_id) not null,
    company_id number references companies(company_id)
);

create sequence reservations_seq;
create or replace trigger reservation_autoinc
before insert on reservations
for each row begin
    select :new.reservation_id || reservations_seq.nextval into :new.reservation_id from dual;
end;
/

create table reservations_containers (
    reservation_id varchar2(50),
    x number(4,0) not null,
    y number(4,0) not null,
    container_id varchar2(63) references containers(container_id) not null,
    constraint fk_reservations_parcs foreign key (x,y) references parcs(x,y),
    constraint pk_reservations_containers primary key (reservation_id, x, y)
);

create table transporters (
    transporter_id varchar2(63) constraint pk_transporters primary key not null,
    company_id     number       constraint fk_transporters_company_id references companies(company_id) not null,
    info           varchar2(127)
);


create table movements (
    movement_id        number       constraint pk_movements primary key not null,
    container_id       varchar2(63) constraint fk_movements_container_id references containers(container_id) not null,
    company_id         number       constraint fk_movements_company_id references companies(company_id) not null,
    transporter_id_in  varchar2(63) constraint fk_movements_transporter_id_in references transporters(transporter_id),
    transporter_id_out varchar2(63) constraint fk_movements_transporter_id_ou references transporters(transporter_id),
    date_arrival       date,
    date_departure     date,
    weight             number,
    destination_id     number       constraint fk_movements_destination_id references destinations(destination_id) not null
);

create sequence movement_seq;
create or replace trigger movement_autoinc
before insert on movements
for each row begin
    select movement_seq.nextval into :new.movement_id from dual;
end;

create or replace view free_empl as select x, y from parcs where (x,y) not in (select x,y from reservations_containers);
create or replace view movements_light as select movement_id, container_id, name, city, date_arrival, date_departure from movements natural join destinations natural join companies;
/

exit
