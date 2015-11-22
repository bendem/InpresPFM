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
