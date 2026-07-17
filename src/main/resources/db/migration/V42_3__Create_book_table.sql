create table if not exists book
(
    id          uuid
        constraint book_pk primary key,
    isbn        varchar not null,
    title       varchar,
    author      varchar,
    description text,
    constraint book_isbn_unique unique (isbn)
);
