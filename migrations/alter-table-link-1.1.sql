--liquibase formatted sql

--changeset SlideHehe:1.1
alter table only link
    add column checked_at timestamp with time zone default current_timestamp not null;

alter table only link
    alter column updated_at set default current_timestamp;
