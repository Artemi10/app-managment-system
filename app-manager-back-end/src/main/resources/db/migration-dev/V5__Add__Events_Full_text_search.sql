alter table events add column name_ts tsvector
    generated always as(to_tsvector('english', name)) stored;

alter table events add column extra_information_ts tsvector
    generated always as(to_tsvector('english', extra_information)) stored;
