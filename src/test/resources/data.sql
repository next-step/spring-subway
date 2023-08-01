drop table if exists section;
drop table if exists line;
drop table if exists station;

create table if not exists station
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
    );

create table if not exists line
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

insert into line(name, color) values ('7호선', '주황');

insert into station(name) values ('부천시청역');
insert into station(name) values ('신중동역');
insert into station(name) values ('춘의역');
insert into station(name) values ('부천종합운동장역');

insert into section(up_station_id, down_station_id, line_id, distance) values (1, 2, 1, 10);
