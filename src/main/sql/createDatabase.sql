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
DeliveryNumber varchar(10),
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
Version int not null,
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
( 'Внос'), ('Износ'), ('Фр-Фр');

create table transportation
(
Id int not null primary key auto_increment,
WeekNumber int not null,
`Year` int not null,
StartDate date not null
);


create table truckGroup(
Id int not null primary key auto_increment,
`Name` varchar(30) not null
);

insert into truckGroup(Name) values
('1'),
('2'),
('3'),
('4'),
('5'),
('6'),
('7'),
('8'),
('9'),
('10')
;

create table booksPackage
(
Id int not null primary key auto_increment,
PackageNumber varchar(20) not null,
Country varchar(25) not null,
PostalCode varchar(15) not null,
PhoneNumber varchar(15) not null,
Email varchar(50) not null,
Merchant varchar(30),
Client varchar(70),
DeliveryDate date,
DeliveryAddress varchar(100),
Remarks varchar(500),
Version int not null,
TransportationId int not null,
TruckGroupId int,
foreign key (TransportationId) references transportation(Id),
foreign key (TruckGroupId) references truckGroup(Id)
);

create table book
(
Id int not null primary key auto_increment,
BookNumber varchar(30) not null,
Title varchar(50) not null,
`Count` int not null,
Weight double not null,
WeightPerBook double not null,
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


create table pulsioDetails(
Id int not null primary key auto_increment,
Contact1 varchar(20) not null,
Contact2 varchar(20) not null,
Logo blob,
Signature blob
);


Insert into pulsioDetails (Contact1, Contact2, Logo) values(
'09 70 44 06 46', '09 70 40 75 02');


-- ALTER BOOKSPACKAGE SCRIPT
--alter table bookspackage 
-- add Version integer not null,
-- add Country varchar(25) not null,
-- add PostalCode varchar(15) not null,
-- add PhoneNumber varchar(15) not null,
-- add Email varchar(50) not null;