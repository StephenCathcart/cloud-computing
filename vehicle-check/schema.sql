create table vehiclechecks (
    id int not null IDENTITY(1, 1),
    registration varchar(10) not null,
    stolen bit not null,
    checked_date date not null
    primary key (id)
);
