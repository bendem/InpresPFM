select * from free_empl;
select x, y from parcs where (x,y) not in (select x,y from reservations_containers);

insert into users(username, password) values ('a', 'b');

insert into parcs values (1,1,null);
insert into parcs values (1,2,null);
insert into parcs values (1,3,null);
insert into parcs values (2,1,null);
insert into parcs values (2,2,null);
insert into parcs values (2,3,null);
insert into parcs values (3,1,null);
insert into parcs values (3,2,null);
insert into parcs values (3,3,null);
insert into parcs values (4,1,null);
insert into parcs values (4,2,null);
insert into parcs values (4,3,null);

insert into companies(name) values ('company 1');
insert into companies(name) values ('company 2');
insert into companies(name) values ('company 3');
insert into companies(name) values ('company 4');

insert into containers(container_id, content_type, company_id) values ('c11', 't1', 1);
insert into containers(container_id, content_type, company_id) values ('c12', 't1', 1);
insert into containers(container_id, content_type, company_id) values ('c13', 't1', 1);
insert into containers(container_id, content_type, company_id) values ('c14', 't1', 1);
insert into containers(container_id, content_type, company_id) values ('c21', 't1', 2);
insert into containers(container_id, content_type, company_id) values ('c22', 't1', 2);
insert into containers(container_id, content_type, company_id) values ('c23', 't1', 2);
insert into containers(container_id, content_type, company_id) values ('c24', 't1', 2);
insert into containers(container_id, content_type, company_id) values ('c31', 't1', 3);
insert into containers(container_id, content_type, company_id) values ('c32', 't1', 3);
insert into containers(container_id, content_type, company_id) values ('c33', 't1', 3);
insert into containers(container_id, content_type, company_id) values ('c34', 't1', 3);

insert into destinations values (1,'rome',5,5,5);
insert into destinations values (2,'paris',5,5,5);
insert into destinations values (3,'brussels',5,5,5);
insert into destinations values (4,'new york',5,5,5);

insert into reservations values ('reservation1', current_date, 1, 1);
insert into reservations values ('reservation2', current_date, 2, 2);
insert into reservations values ('reservation3', current_date, 3, 3);

insert into reservations_containers values ('reservation11', 1, 1, 'c11');
insert into reservations_containers values ('reservation11', 1, 2, 'c12');
insert into reservations_containers values ('reservation22', 2, 1, 'c21');
insert into reservations_containers values ('reservation22', 2, 2, 'c22');
insert into reservations_containers values ('reservation33', 3, 1, 'c31');
insert into reservations_containers values ('reservation33', 3, 2, 'c32');
insert into reservations_containers values ('reservation33', 3, 3, 'c33');


insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 1, to_date('05/06/2015', 'dd/mm/YYYY'), to_date('15/06/2015', 'dd/mm/YYYY'), 150);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 1, to_date('06/06/2015', 'dd/mm/YYYY'), to_date('15/06/2015', 'dd/mm/YYYY'), 151);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 2, to_date('25/06/2015', 'dd/mm/YYYY'), to_date('04/07/2015', 'dd/mm/YYYY'), 250);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 2, to_date('26/06/2015', 'dd/mm/YYYY'), to_date('04/07/2015', 'dd/mm/YYYY'), 251);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 3, to_date('14/05/2015', 'dd/mm/YYYY'), to_date('30/05/2015', 'dd/mm/YYYY'), 50);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 3, to_date('07/02/2015', 'dd/mm/YYYY'), to_date('15/02/2015', 'dd/mm/YYYY'), 320);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 3, to_date('09/09/2015', 'dd/mm/YYYY'), to_date('17/09/2015', 'dd/mm/YYYY'), 660);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 4, to_date('24/02/2015', 'dd/mm/YYYY'), to_date('27/02/2015', 'dd/mm/YYYY'), 450);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 4, to_date('05/05/2015', 'dd/mm/YYYY'), to_date('15/05/2015', 'dd/mm/YYYY'), 148);
insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
('c11', 1, null, null, 4, to_date('01/05/2015', 'dd/mm/YYYY'), to_date('09/05/2015', 'dd/mm/YYYY'), 148);


CREATE FUNCTION rand(x int)
RETURNS int AS $$
BEGIN
    return floor((random() * x))::int;
END;
$$ LANGUAGE plpgsql;

create or replace FUNCTION stuff() returns int AS $$
declare
  day integer;
  day_after integer;
  month integer;
  month_after integer;
  year integer;
  year_after integer;
  date_after date;
  scontainer_id varchar(20);
begin
  year := 2016;
  for i in 0..1000 loop
    day := rand(28) + 1;
    day_after := day + round(abs(random() * 2) + 10);
    month := round(rand(12) + 1);
    if day_after >=30 then
      day_after := 30 - day;
      if month = 12 then
        year_after := year+1;
        month_after := 1;
      else
        year_after := year;
        month_after := month+1;
      end if;
    else
      month_after := month;
      year_after := year;
    end if;

    if (round(rand(1)) = 1) then
      date_after := to_date(day||'/'||month||'/'||year, 'dd/mm/YYYY');
    else
      date_after := null;
    end if;

    select container_id into scontainer_id from (select * from containers order by random()) as x limit 1;

    insert into movements(container_id, company_id, transporter_id_in, transporter_id_out, destination_id, date_arrival, date_departure, weight) values
    (scontainer_id, (select company_id from containers where container_id = scontainer_id), null, null, round(rand(4) + 1), to_date(day||'/'||month||'/'||year, 'dd/mm/YYYY'), date_after, round(rand(650) + 50));

  end loop;

  return 0;
end;
$$ LANGUAGE plpgsql;
