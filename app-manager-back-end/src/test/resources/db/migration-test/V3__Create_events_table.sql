create table if not exists events
(
    id                  bigint
                        constraint events_pk primary key,
    name                varchar(50)                   not null,
    extra_information   varchar(255),
    creation_time       timestamp without time zone   not null,
    application_id      bigint                        not null
                        constraint events_applications_id_fk
                        references applications
                        on delete cascade
);

create unique index events_id_uindex
    on events (id);
