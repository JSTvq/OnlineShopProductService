create table if not exists product (
    id serial primary key,
    category varchar(100) not null,
    name varchar(100) not null,
    price numeric(19, 2) not null,
    stock_quantity integer not null,
    created_at timestamp,
    updated_at timestamp
    );