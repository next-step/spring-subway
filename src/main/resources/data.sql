insert into LINE (id, name, color)
values (1, '2호선', 'red');

insert into STATION (id, name)
values (1, '신대방역');
insert into STATION (id, name)
values (2, '서울대입구역');
insert into STATION (id, name)
values (3, '상도역');

insert into SECTION (id, upward_id, downward_id, line_id, distance)
values (default, 1, 2, 1, 5);
insert into SECTION (id, upward_id, downward_id, line_id, distance)
values (default, 2, 3, 1, 6);