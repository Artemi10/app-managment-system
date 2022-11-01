create table if not exists users
(
    id                  bigint
                        constraint users_pk primary key,
    email               varchar(255) not null,
    password            varchar(255) not null,
    authority           varchar(20)   not null,
    reset_token         varchar(8),
    refresh_token       varchar(128) not null,
    oauth_enter_token   varchar(128)
);
create unique index users_email_uindex
    on users (email);

create unique index users_id_uindex
    on users (id);
