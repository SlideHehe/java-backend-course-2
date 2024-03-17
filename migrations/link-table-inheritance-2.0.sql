--liquibase formatted sql

--changeset SlideHehe:2.0
create table link_type
(
    type varchar(63) primary key
);

insert into link_type (type)
values ('STACKOVERFLOW'),
       ('GITHUB');


alter table only link
    add column type varchar(63) generated always as ( upper(substring(link.url from '//([^/]+)\.')) ) stored;

alter table only link
    alter column type drop expression;

alter table only link
    alter column type set not null;

alter table only link
    add constraint fk_link_type foreign key (type) references link_type (type) on update cascade;


alter table only link
    add column answer_count int default null;

alter table only link
    add column comment_count int default null;

alter table only link
    add column pull_request_count int default null;

alter table only link
    add column commit_count int default null;
