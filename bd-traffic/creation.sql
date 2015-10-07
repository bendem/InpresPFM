-- parc
-- -------
create table parc (
    x number,
    y number,
    container_id varchar2(63),
    constraint pk_parc primary key (x, y)
);

-- company
-- -------
create table company (
    company_id number constraint pk_company primary key,
    name varchar2(63),
    mail varchar2(63),
    phone varchar2(63),
    address varchar2(63)
);

-- container
-- ---------
create table container (
    container_id varchar2(63) constraint pk_container primary key,
    company_id number constraint fk_container_company_id references company(company_id),
    content_type varchar2(63),
    dangers varchar2(63)
);

-- transporter
-- ---------
create table transporter (
    transporter_id varchar2(63) constraint pk_transporter primary key,
    company_id number constraint fk_transporter_company_id references company(company_id),
    info varchar2(127)
);

-- destination
-- -------
create table destination (
    destination_id number constraint pk_destination primary key,
    town varchar2(63),
    distance_road number,
    distance_boat number,
    distance_train number
);

-- movement
-- ---------
create table movement (
    movement_id number constraint pk_movement primary key,
    container_id varchar2(63) constraint fk_movement_container_id references container(container_id),
    company_id number constraint fk_movement_company_id references company(company_id),
    transporter_id_in varchar2(63) constraint fk_movement_transporter_id_in references transporter(transporter_id),
    transporter_id_out varchar2(63) constraint fk_movement_transporter_id_out references transporter(transporter_id),
    date_arrival date,
    date_departure date,
    weight number,
    destination_id number constraint fk_movement_destination_id references destination(destination_id)
);

exit
