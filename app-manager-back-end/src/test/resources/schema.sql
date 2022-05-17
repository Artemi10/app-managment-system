create table if not exists users
(
    id          bigint
        constraint users_pk primary key,
    email       varchar(255) not null,
    password    varchar(255) not null,
    authority   varchar(20)   not null,
    reset_token varchar(8),
    refresh_token varchar(128) not null
);

create table if not exists applications
(
    id            bigint
        constraint applications_pk primary key,
    name          varchar(255) not null,
    creation_time timestamp    not null,
    user_id       bigint       not null
        constraint applications_users_id_fk
            references users
            on delete cascade
);

create unique index if not exists applications_id_uindex
    on applications (id);

create unique index if not exists users_email_uindex
    on users (email);

create unique index if not exists users_id_uindex
    on users (id);

create table if not exists events
(
    id                bigint
        constraint events_pk primary key,
    name              varchar(50) not null,
    extra_information varchar(255),
    time              timestamp   not null,
    application_id    bigint      not null
        constraint events_applications_id_fk
            references applications
            on delete cascade
);

create unique index if not exists events_id_uindex
    on events (id);
