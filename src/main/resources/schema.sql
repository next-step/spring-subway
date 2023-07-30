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
    foreign key(up_station_id) references STATION(id),
    foreign key(down_station_id) references STATION(id),
    foreign key(line_id) references LINE(id) on delete cascade
);

create table if not exists PATH
(
    id bigint auto_increment not null,
    departure_id bigint not null,
    arrival_id bigint not null,
    distance int not null,
    primary key(id),
    foreign key(departure_id) references STATION(id),
    foreign key(arrival_id) references STATION(id)
    );

create table if not exists DETAIL_PATH
(
    id bigint auto_increment not null,
    path_id bigint not null,
    waypoint_id bigint not null,
    sequence int not null,
    primary key(id),
    foreign key(path_id) references PATH(id) on delete cascade,
    foreign key(waypoint_id) references STATION(id)
    );
