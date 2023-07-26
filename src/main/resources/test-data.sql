INSERT INTO STATION (id, name)
values (default, '서울대입구역');
INSERT INTO STATION (id, name)
values (default, '잠실역');

INSERT INTO LINE (id, name, color)
values (default, '2호선', 'green');
INSERT INTO LINE (id, name, color)
values (default, '4호선', 'cyan');

INSERT INTO SECTION (id, up_station_id, down_station_id, line_id, distance)
values (default, 1, 2, 1, 10);

INSERT INTO SECTION (id, up_station_id, down_station_id, line_id, distance)
values (default, 1, 2, 2, 10);