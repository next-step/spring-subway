insert into LINE(`id`, `name`, `color`)
values (DEFAULT, '1호선', '남색'),
       (DEFAULT, '2호선', '초록색'),
       (DEFAULT, '3호선', '주황색');

insert into STATION(`id`, `name`)
values (10, '소요산'),
       (11, '동두천'),
       (12, '보산'),
       (13, '동두천중앙'),
       (14, '지행'),
       (15, '덕정'),
       (16, '덕계'),
       (17, '양주'),
       (18, '녹양'),
       (19, '가능'),

       (20, '구로디지털단지'),
       (21, '신대방'),
       (22, '신림'),
       (23, '봉천'),
       (24, '서울대입구'),
       (25, '낙성대'),
       (27, '사당'),
       (26, '방배'),
       (28, '서초'),
       (29, '교대'),

       (30, '대화'),
       (31, '주엽'),
       (32, '정발산'),
       (33, '마두'),
       (34, '백석'),
       (35, '대곡'),
       (36, '화정'),
       (37, '원당'),
       (38, '원흥'),
       (39, '삼송');

insert into SECTION(`id`, `line_id`, `up_station_id`, `down_station_id`, `distance`, `next_section_id`,
                    `prev_section_id`)
values (DEFAULT, 1, 11, 12, 777, null, null),

       (DEFAULT, 2, 23, 24, 777, null, null),
       (DEFAULT, 2, 24, 25, 777, 2, null),

       (DEFAULT, 3, 36, 37, 777, null, null),
       (DEFAULT, 3, 37, 38, 777, 4, null);


