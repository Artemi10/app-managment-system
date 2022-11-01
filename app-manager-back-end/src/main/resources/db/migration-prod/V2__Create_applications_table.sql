create table applications
(
    id              bigserial
                    constraint applications_pk
                    primary key,
    name            varchar(255)                  not null,
    creation_time   timestamp with time zone      not null,
    user_id         bigint                        not null
                    constraint applications_users_id_fk
                    references users
                    on delete cascade
);

alter table applications
    owner to bvqsouejthnzku;

create unique index applications_id_uindex
    on applications (id);
