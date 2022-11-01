alter table applications add column ts tsvector
    generated always as(to_tsvector('english', name)) stored;
