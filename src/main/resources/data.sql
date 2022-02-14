insert into guest(id, name) values(null, 'Roger Federer');
insert into guest(id, name) values(null, 'Rafael Nadal');
insert into guest(id, name) values(null, 'Simona');

insert into tennis_court(id, name) values(null, 'Roland Garros - Court Philippe-Chatrier');
insert into tennis_court(id, name) values(null, 'Wimbledon - Court');

insert into schedule(id, start_date_time, end_date_time, tennis_court_id) values(null, '2020-12-20T20:00:00.0', '2020-02-20T21:00:00.0', 1);
insert into schedule(id, start_date_time, end_date_time, tennis_court_id) values(null, '2022-12-09T05:00:00.0', '2022-12-09T06:00:00.0', 1);
insert into schedule(id, start_date_time, end_date_time, tennis_court_id) values(null, '2022-11-09T07:00:00.0', '2022-11-09T08:00:00.0', 1);
insert into schedule(id, start_date_time, end_date_time, tennis_court_id) values(null, '2022-10-09T04:00:00.0', '2022-10-09T05:00:00.0', 2);
insert into schedule(id, start_date_time, end_date_time, tennis_court_id) values(null, '2022-12-09T08:00:00.0', '2022-12-09T09:00:00.0', 2);
insert into schedule(id, start_date_time, end_date_time, tennis_court_id) values(null, '2022-10-09T10:00:00.0', '2022-10-09T11:00:00.0', 2);
insert into schedule(id, start_date_time, end_date_time, tennis_court_id) values(null, '2022-12-09T09:00:00.0', '2022-12-09T10:00:00.0', 1);
insert into schedule(id, start_date_time, end_date_time, tennis_court_id) values(null, '2022-11-09T00:00:00.0', '2022-03-09T01:00:00.0', 2);

insert into reservation(id, refund_value, reservation_status, value, guest_id, schedule_id) values(null, null, 0, 10, 2, 3);
insert into reservation(id, refund_value, reservation_status, value, guest_id, schedule_id) values(null, null, 0, 10, 2, 4);
insert into reservation(id, refund_value, reservation_status, value, guest_id, schedule_id) values(null, null, 1, 10, 1, 8);