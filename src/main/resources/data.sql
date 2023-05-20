insert into station (id, name) values (1, '강남역');
insert into station (id, name) values (2, '진해역');
insert into station (id, name) values (3, '동해역');
insert into station (id, name) values (4, '서해역');

insert into line (id, name, color) values (1, '1호선', 'red');
insert into line (id, name, color) values (2, '2호선', 'blue');

insert into section(id, line_id, up_station_id, down_station_id, distance) values (1, 1, 1, 2, 10);
insert into section(id, line_id, up_station_id, down_station_id, distance) values (2, 1, 2, 3, 10);
insert into section(id, line_id, up_station_id, down_station_id, distance) values (3, 1, 3, 4, 10);
