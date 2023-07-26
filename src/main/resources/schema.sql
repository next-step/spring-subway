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
    constraint line_id_fk foreign key (line_id) references LINE(id) on delete cascade,
    constraint up_station_id_fk foreign key (up_station_id) references STATION(id),
    constraint down_station_id_fk foreign key (down_station_id) references STATION(id)
);
