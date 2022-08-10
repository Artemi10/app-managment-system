

INSERT INTO users (id, email, password, authority, refresh_token)
VALUES (1, 'lyah.artem10@mail.ru', '$2y$10$ZPgg5k.SQaJIxjGF7AU15.GNVF2U7MVJJWgMxkyuXjW550XIEEK52', 'ACTIVE', 'f49a1380-4e9b-4a81-b5ee-735c59f35fed');

INSERT INTO users (id, email, password, authority, refresh_token)
VALUES (2, 'lyah.artem10@gmail.com', '$2y$10$hkUwTHYaxqemq.NMf8l66OD.4M1RSCwCT9dl461J4gKAMdaNU1MkO', 'ACTIVE', 'f49a1370-4e9b-4a81-b5ee-735c59f359ed');

INSERT INTO users (id, email, password, authority, refresh_token)
VALUES (3, 'd10@gmail.com', '$2y$10$x.jaNOvtBnsMqyhehZ5ituZzUAGnrHiSXzme1/i0EzrcWgRHMl0Ve', 'UPDATE_CONFIRMED', 'f49a1320-4e9b-4a81-b5ee-835c59f35fed');

INSERT INTO users (id, email, password, authority, reset_token, refresh_token)
VALUES (4, 'd11@gmail.com', '$2a$10$ypeGI68R5lQHc..n1TymQe1Xn6paLc.CPafoEM.u5R0q/qhuLc8Zq', 'UPDATE_NOT_CONFIRMED', 'DVhclnWO', 'f49a1320-4e9b-4a81-b5ee-835c59f35fed');


INSERT INTO applications (id, name, user_id, creation_time)
VALUES (1, 'Simple CRUD App', 1, '2022-03-13 03:14:07');

INSERT INTO applications (id, name, user_id, creation_time)
VALUES (2, 'Todo list', 1, '2022-03-14 13:14:07');

INSERT INTO applications (id, name, user_id, creation_time)
VALUES (3, 'Flight Timetable', 1, '2022-03-15 10:14:07');

INSERT INTO applications (id, name, user_id, creation_time)
VALUES (4, 'User chat', 1, '2022-03-16 8:14:07');


INSERT INTO events (id, name, extra_information, creation_time, application_id)
VALUES (1, 'User successfully signed up', 'Extra information', '2022-03-14 22:14:07', 2);

INSERT INTO events (id, name, extra_information, creation_time, application_id)
VALUES (2, 'User successfully logged in', 'Extra information', '2022-03-14 23:15:07', 2);

INSERT INTO events (id, name, extra_information, creation_time, application_id)
VALUES (3, 'Add new note', 'Extra information', '2022-03-17 23:15:47', 2);

INSERT INTO events (id, name, extra_information, creation_time, application_id)
VALUES (4, 'Update note', 'Extra information', '2022-04-14 23:39:07', 2);

