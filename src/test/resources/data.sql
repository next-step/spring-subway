insert into LINE(`id`, `name`, `color`)
values (DEFAULT, '1호선', '남색'),
       (DEFAULT, '2호선', '초록색'),
       (DEFAULT, '3호선', '주황색');

insert into STATION(`id`, `name`)
values (11, '시청'),
       (12, '몽촌토성'),
       (13, '학여울'),
       (23, '잠실'),
       (24, '송파'),
       (25, '방이'),
       (36, '가락시장'),
       (37, '수서'),
       (38, '대청');


insert into SECTION(`id`, `line_id`, `up_station_id`, `down_station_id`, `distance`, `pre_section_id`,
                    `post_section_id`)
values (DEFAULT, 1, 11, 12, 777, null, null),
       (DEFAULT, 2, 23, 24, 777, null, null),
       (DEFAULT, 2, 24, 25, 777, 2, null),
       (DEFAULT, 3, 36, 37, 777, null, null),
       (DEFAULT, 3, 37, 38, 777, 4, null);


