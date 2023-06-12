-- reset tables
delete from section;
delete from station;
delete from line;

-- line insert
insert into line(id, name, color) values(1, '3호선', 'bg-orange-600');
insert into line(id, name, color) values(2, '신분당선', 'bg-red-600');
insert into line(id, name, color) values(3, '수인분당선', 'bg-yellow-600');
insert into line(id, name, color) values(4, '7호선', 'bg-green-600');

-- station insert
insert into station(id, name) values(1, '교대역');
insert into station(id, name) values(2, '남부터미널역');
insert into station(id, name) values(3, '양재역');
insert into station(id, name) values(4, '매봉역');
insert into station(id, name) values(5, '도곡역');
insert into station(id, name) values(6, '대치역');
insert into station(id, name) values(7, '학여울역');
insert into station(id, name) values(8, '강남역');
insert into station(id, name) values(9, '양재시민의숲역');
insert into station(id, name) values(10, '청계산입구역');
insert into station(id, name) values(11, '판교역');
insert into station(id, name) values(12, '선릉역');
insert into station(id, name) values(13, '한티역');
insert into station(id, name) values(14, '구룡역');
insert into station(id, name) values(15, '개포동역');
insert into station(id, name) values(16, '수내역');
insert into station(id, name) values(17, '총신대입구역');
insert into station(id, name) values(18, '내방역');

-- section insert
insert into section(line_id, up_station_id, down_station_id, distance) values(1, 1, 2, 2);
insert into section(line_id, up_station_id, down_station_id, distance) values(1, 2, 3, 3);
insert into section(line_id, up_station_id, down_station_id, distance) values(1, 3, 4, 6);
insert into section(line_id, up_station_id, down_station_id, distance) values(1, 4, 5, 7);
insert into section(line_id, up_station_id, down_station_id, distance) values(1, 5, 6, 50);
insert into section(line_id, up_station_id, down_station_id, distance) values(1, 6, 7, 10);

insert into section(line_id, up_station_id, down_station_id, distance) values(2, 8, 3, 5);
insert into section(line_id, up_station_id, down_station_id, distance) values(2, 3, 9, 7);
insert into section(line_id, up_station_id, down_station_id, distance) values(2, 9, 10, 3);
insert into section(line_id, up_station_id, down_station_id, distance) values(2, 10, 11, 4);

insert into section(line_id, up_station_id, down_station_id, distance) values(3, 12, 13, 1);
insert into section(line_id, up_station_id, down_station_id, distance) values(3, 13, 5, 2);
insert into section(line_id, up_station_id, down_station_id, distance) values(3, 5, 14, 6);
insert into section(line_id, up_station_id, down_station_id, distance) values(3, 14, 15, 2);
insert into section(line_id, up_station_id, down_station_id, distance) values(3, 15, 16, 3);

insert into section(line_id, up_station_id, down_station_id, distance) values(4, 17, 18, 4);
