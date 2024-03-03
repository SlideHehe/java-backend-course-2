--liquibase formatted sql

--changeset SlideHehe:1
create table chat
(
    id bigint primary key
);

create table link
(
    id         bigserial primary key,
    url        varchar                  not null unique,
    updated_at timestamp with time zone not null
);

create table chat_link
(
    chat_id bigint references chat (id) on delete cascade,
    link_id bigint references link (id) on delete cascade,

    primary key (chat_id, link_id)
);
