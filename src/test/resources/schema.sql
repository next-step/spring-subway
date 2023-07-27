DROP TABLE if exists section;
DROP TABLE if exists station;
DROP TABLE if exists line;

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

create table if not exists section
(
    id bigint auto_increment not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    line_id bigint not null,
    distance integer not null,
    primary key(id)
);

alter table section
    add constraint section_up_station_fk
    foreign key (up_station_id)
    references station (id);

alter table section
    add constraint section_down_station_fk
    foreign key (down_station_id)
    references station (id);

alter table section
    add constraint section_line_fk
    foreign key (line_id)
    references line (id);
