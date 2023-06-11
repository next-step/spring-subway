create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
);

create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
);

create table if not exists SECTION
(
    id bigint auto_increment not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    line_id bigint not null,
    distance int not null,
    primary key(id),
    foreign key(line_id) references LINE(id),
    foreign key(up_station_id) references STATION(id),
    foreign key(down_station_id) references STATION(id)
);

insert into STATION (name) values ('사당');
insert into STATION (name) values ('방배');
insert into STATION (name) values ('서초');
insert into STATION (name) values ('교대');
insert into STATION (name) values ('강남');
insert into STATION (name) values ('남태령');
insert into STATION (name) values ('양재');

insert into LINE (name, color) values ('2호선', 'GREEN');
insert into LINE (name, color) values ('4호선', 'BLUE');
insert into LINE (name, color) values ('3호선', 'ORANGE');
insert into LINE (name, color) values ('신분당선', 'RED');
