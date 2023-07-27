insert into STATION ( name) values ('서울대입구역');
insert into STATION (name) values ('상도역');

insert into LINE (id,  name, color) values ( default, '2호선', 'green' );
insert into SECTION (up_station_id, down_station_id,line_id,distance) values (1L, 2L, 1L, 10 );
