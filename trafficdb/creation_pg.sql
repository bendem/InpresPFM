create table users (
    user_id    serial constraint pk_users primary key not null,
    username   character varying(63) constraint u_users_username unique not null,
    password   character varying(63) not null
);

create table companies (
    company_id serial constraint pk_companies primary key not null,
    name       character varying(63) unique not null,
    mail       character varying(63),
    phone      character varying(63),
    address    character varying(63)
);

create table containers (
    container_id character varying(63) constraint pk_containers primary key not null,
    company_id   integer       constraint fk_containers_company_id references companies(company_id) not null,
    content_type character varying(63) not null,
    dangers      character varying(63)
);

create table parcs (
    x            integer not null,
    y            integer not null,
    container_id character varying(63) constraint fk_parcs_container_id references containers(container_id),
    constraint pk_parcs primary key (x, y)
);

create table destinations (
    destination_id integer constraint pk_destinations primary key not null,
    city           character varying(63) not null,
    distance_road  numeric,
    distance_boat  numeric,
    distance_train numeric
);

create table reservations (
    reservation_id character varying(50) primary key,
    date_arrival date not null,
    destination_id integer references destinations(destination_id) not null,
    company_id integer references companies(company_id)
);

create table reservations_containers (
    reservation_id character varying(50),
    x integer not null,
    y integer not null,
    container_id character varying(63) references containers(container_id) not null,
    constraint fk_reservations_parcs foreign key (x,y) references parcs(x,y),
    constraint pk_reservations_containers primary key (reservation_id, x, y)
);

create table transporters (
    transporter_id character varying(63) constraint pk_transporters primary key not null,
    company_id     integer constraint fk_transporters_company_id references companies(company_id) not null,
    info character varying(127)
);


create table movements (
    movement_id        serial       constraint pk_movements primary key not null,
    container_id       character varying(63) constraint fk_movements_container_id references containers(container_id) not null,
    company_id         integer     constraint fk_movements_company_id references companies(company_id) not null,
    transporter_id_in  character varying(63) constraint fk_movements_transporter_id_in references transporters(transporter_id),
    transporter_id_out character varying(63) constraint fk_movements_transporter_id_ou references transporters(transporter_id),
    date_arrival       date,
    date_departure     date,
    weight             numeric,
    destination_id     integer       constraint fk_movements_destination_id references destinations(destination_id) not null
);

create or replace view free_empl as
select x, y from parcs where (x,y) not in (select x,y from reservations_containers);

create or replace view movements_light as
select movement_id, container_id, name, city, date_arrival, date_departure, weight from movements natural join destinations natural join companies order by random();

create or replace view container_per_dest_year as
select count(*) count, city, extract (year from date_arrival) as year from movements_light group by city, extract (year from date_arrival);

create or replace view container_per_dest_month as
select count(*) count, city, extract (month from date_arrival) as month from movements_light group by city, extract (month from date_arrival);

create or replace view container_per_dest_quarter as
select count(*) count, city, extract (year from date_arrival) as year, to_char(date_arrival, 'Q') quarter from movements_light group by to_char(date_arrival, 'Q'), city, extract (year from date_arrival);

create or replace view container_leaving as select distinct movement_id, container_id, name, city, date_arrival, weight
from movements natural join destinations natural join companies
where date_departure is null
order by date_arrival;

create or replace view container_left as select distinct movement_id, container_id, name, city, date_arrival, date_departure, weight
from movements natural join destinations natural join companies
where date_departure is not null
order by date_arrival;

create or replace view container_incoming as
select container_id, city, x, y from containers natural join reservations_containers natural join reservations natural join destinations;
