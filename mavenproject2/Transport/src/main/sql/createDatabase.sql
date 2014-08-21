Drop database if exists transport;

CREATE DATABASE transport;
use transport;

create table deliverydirection
(
Id int not null primary key auto_increment,
Direction varchar(100) not null
);

create table delivery
(
Id int not null primary key auto_increment,
RequestedBy varchar(150),
RequestDate date,
PickUpDate date,
DeliveryDate date,
Uuid varchar(20) not null,
State varchar(100),
Volume varchar(100),
Weight decimal(7,2),
Billing varchar(1000),
PickUpAddress varchar(250),
DeliveryAddress varchar(250),
Driver varchar(100),
Executant varchar(100),
PriceTaken decimal(15,2),
PriceForUs decimal(15,2),
Remarks varchar(500),
DeliveryDirectionId int not null,
foreign key (DeliveryDirectionId) references deliveryDirection(Id)
);

create table attachment
(
Id int not null primary key auto_increment,
FilePath varchar(1000),
ContentType varchar(100),
DeliveryId int,
foreign key (DeliveryId) references delivery(Id)
);

insert into deliveryDirection (Direction) values
( 'Внос'), ('Износ'), ('Вътрешно за Франция');

create table transportation
(
Id int not null primary key auto_increment,
WeekNumber int not null,
`Year` int not null,
StartDate date not null
);

create table booksPackage
(
Id int not null primary key auto_increment,
PackageNumber varchar(20) not null,
Merchant varchar(30),
Client varchar(70),
DeliveryDate date,
DeliveryAddress varchar(100),
Remarks varchar(500),
TransportationId int not null,
foreign key (TransportationId) references transportation(Id)
);

create table book
(
Id int not null primary key auto_increment,
BookNumber varchar(30) not null,
Title varchar(50) not null,
`Count` int not null,
Weight double not null,
PackageId int not null,
TransportationId int not null,
foreign key (PackageId) references booksPackage(Id),
foreign key (TransportationId) references transportation(Id)
);

create table box 
(
Id int not null primary key auto_increment,
BooksCount int not null,
BoxesCount int not null,
BookId int not null,
PackageId int not null,
foreign key(BookId) references book(Id),
foreign key (PackageId) references booksPackage(Id)
);