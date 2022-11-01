create table if not exists applications
(
    id              bigint
                    constraint applications_pk    primary key,
    name            varchar(255)                  not null,
    creation_time   timestamp without time zone   not null,
    user_id         bigint                        not null
                    constraint applications_users_id_fk
                    references users
                    on delete cascade
);

create unique index applications_id_uindex
    on applications (id);
