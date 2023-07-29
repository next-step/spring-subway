drop table if exists SECTION;
drop table if exists STATION;
drop table if exists LINE;

create table LINE
(
    id    bigint auto_increment not null,
    name  varchar(255)          not null unique,
    color varchar(20)           not null,
    primary key (id)
);

create table STATION
(
    id   bigint auto_increment not null,
    name varchar(255)          not null unique,
    primary key (id)
);

create table SECTION
(
    id              bigint auto_increment not null,
    line_id         bigint                not null,
    up_station_id   bigint                not null,
    down_station_id bigint                not null,
    distance        bigint                not null,
    primary key (id),
    foreign key (line_id) references LINE (id) on delete cascade,
    foreign key (up_station_id) references STATION (id) on delete cascade,
    foreign key (down_station_id) references STATION (id) on delete cascade,
    unique (line_id, up_station_id, down_station_id)
);

insert into STATION(name)
values ('잠실'),
       ('구로디지털단지'),
       ('강남'),
       ('역삼'),
       ('선릉'),
       ('교대'),
       ('신대방'),
       ('봉천'),
       ('서초'),
       ('종합운동장');

insert into LINE(name, color)
values ('1호선', '남색'),
       ('2호선', '초록색'),
       ('3호선', '주황색');

insert into SECTION(line_id, up_station_id, down_station_id, distance)
values (1, 1, 2, 16),
       (1, 2, 1, 1),
       (1, 1, 3, 9),
       (1, 1, 4, 35),
       (2, 2, 4, 12),
       (2, 2, 5, 25),
       (3, 3, 4, 15),
       (3, 3, 6, 22),
       (3, 4, 5, 14),
       (3, 4, 6, 17),
       (3, 4, 7, 19),
       (3, 5, 7, 8),
       (3, 6, 7, 14),
       (3, 8, 9, 777);
