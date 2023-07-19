create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
);

create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
);


create table if not exists SECTION
(
    id bigint auto_increment not null,
    line_id bigint not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance bigint not null,
    next_section_id bigint,
    prev_section_id bigint,
    primary key(id),
    foreign key (line_id) references LINE(id),
    foreign key (up_station_id) references STATION(id),
    foreign key (down_station_id) references STATION(id),
    foreign key (next_section_id) references SECTION(id) on delete set null,
    foreign key (prev_section_id) references SECTION(id) on delete set null
);
