create table events
(
    id                  bigserial
                        constraint events_pk
                        primary key,
    name                varchar(50)                   not null,
    extra_information   varchar(255),
    creation_time       timestamp with time zone      not null,
    application_id      bigint                        not null
                        constraint events_applications_id_fk
                        references applications
                        on delete cascade
);

alter table events
    owner to bvqsouejthnzku;

create unique index events_id_uindex
    on events (id);
