create table Manufacturers
(
    Code int  not null
        primary key,
    Name text not null
);


create table Products
(
    Code         int    not null
        primary key,
    Name         text   not null,
    Price        float not null,
    Manufacturer int    not null,
    constraint Products_ibfk_1
        foreign key (Manufacturer) references Manufacturers (Code)
);


