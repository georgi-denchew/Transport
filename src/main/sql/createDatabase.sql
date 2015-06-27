
Drop database if exists transport;

CREATE DATABASE transport;
use transport;

create table deliverydirection
(
Id int not null primary key auto_increment,
Direction varchar(100) not null
) ENGINE=InnoDB;

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
foreign key (DeliveryDirectionId) references deliverydirection(Id)
) ENGINE=InnoDB;

create table attachment
(
Id int not null primary key auto_increment,
FilePath varchar(1000),
ContentType varchar(100),
DeliveryId int,
foreign key (DeliveryId) references delivery(Id)
) ENGINE=InnoDB;

insert into deliverydirection (Direction) values
( 'Внос'), ('Износ'), ('Фр-Фр');

create table transportation
(
Id int not null primary key auto_increment,
WeekNumber int not null,
`Year` int not null,
StartDate date not null
) ENGINE=InnoDB;


create table truckGroup(
Id int not null primary key auto_increment,
`Name` varchar(30) not null
) ENGINE=InnoDB;

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
('10'),
('11'),
('12'),
('13')
;

create table bookspackage
(
Id int not null primary key auto_increment,
PackageNumber varchar(20),
Country varchar(50),
PostalCode varchar(15),
PhoneNumber varchar(100),
Email varchar(100),
Merchant varchar(30),
Client varchar(100),
PricePerKilogram decimal(7,2) default 1,
Priority varchar(30),
DeliveryDate date,
DeliveryAddress varchar(500),
RemarksSales varchar(1000),
RemarksLogistics varchar(1000),
PrintDeliveryDay varchar(20),
LoadingDay varchar(20),
Version int default 1,
TransportationId int,
TruckGroupId int,
foreign key (TransportationId) references transportation(Id),
foreign key (TruckGroupId) references truckGroup(Id)
) ENGINE=InnoDB;


create table bookspackagehistory
(
Id int not null primary key auto_increment,
LastModification timestamp not null,
Country varchar(50),
PostalCode varchar(15),
PhoneNumber varchar(100),
Email varchar(100),
Merchant varchar(30),
Client varchar(100),
PricePerKilogram decimal(7,2) default 1,
Priority varchar(30),
DeliveryDate date,
DeliveryAddress varchar(500),
RemarksSales varchar(1000),
RemarksLogistics varchar(1000),
PrintDeliveryDay varchar(20),
LoadingDay varchar(20),
TruckGroupName varchar(100),
PackageId int not null,
foreign key (PackageId) references bookspackage(Id)
) ENGINE=InnoDB;

create table printinghouse(
Id int not null primary key auto_increment,
Name varchar(150)
) ENGINE=InnoDB;

create table book
(
Id int not null primary key auto_increment,
BookNumber int not null,
Title varchar(200),
`Count` int not null,
Weight double not null,
WeightPerBook double not null,
DeliveryAddress varchar(500),
Discarded bit not null default 0,
PackageId int not null,
TransportationId int not null,
PrintingHouseId int,
foreign key (PackageId) references bookspackage(Id),
foreign key (TransportationId) references transportation(Id),
foreign key (PrintingHouseId) references printinghouse(Id)
) ENGINE=InnoDB;

create table box 
(
Id int not null primary key auto_increment,
BooksCount int not null,
BoxesCount int not null,
BookId int not null,
PackageId int not null,
foreign key(BookId) references book(Id),
foreign key (PackageId) references bookspackage(Id)
) ENGINE=InnoDB;

create table pulsiodetails(
Id int not null primary key auto_increment,
Contact1 varchar(20),
Contact2 varchar(20),
Password varchar(30),
Logo blob,
Signature blob
) ENGINE=InnoDB;

Insert into pulsiodetails (Contact1, Contact2, Password) values(
'09 70 44 06 46', '09 70 40 75 02', 'm4r70');

create table additionalcost(
Id int not null primary key auto_increment,
PackageNumber varchar(20),
Payer varchar(100),
Price decimal (15,2),
Description varchar(500),
TransportationId int not null,
foreign key (TransportationId) references transportation(Id)
) ENGINE=InnoDB;


-- alter table bookspackage modify country varchar(50);
-- alter table bookspackage modify phoneNumber varchar(100);
-- alter table bookspackage modify email varchar(100);
-- alter table bookspackage modify client varchar(100);
-- 
-- alter table bookspackagehistory modify country varchar(50);
-- alter table bookspackagehistory modify phoneNumber varchar(100);
-- alter table bookspackagehistory modify email varchar(100);
-- alter table bookspackagehistory modify client varchar(100);
-- alter table bookspackagehistory modify truckGroupName varchar(100);
-- 
-- alter table book modify title varchar(200);

-- alter table bookspackage add Priority varchar(30);
-- alter table bookspackagehistory add Priority varchar(30);
-- alter table book add Discarded bit not null default 0;
-- 
-- 
-- alter table bookspackage add RemarksSales varchar(1000);
-- alter table bookspackagehistory add RemarksSales varchar(1000);
-- 
-- alter table bookspackage add RemarksLogistics varchar(1000);
-- alter table bookspackagehistory add RemarksLogistics varchar(1000);
-- 
-- alter table book add PrintingHouseId int;
-- alter table book add constraint foreign key (PrintingHouseId) references printinghouse(Id);
-- 
-- alter table bookspackage add PricePerKilogram decimal(7,2);
-- alter table bookspackagehistory add PricePerKilogram decimal(7,2);
-- 
-- alter table additionalcosts add Description varchar(500);

-- alter table book add DeliveryAddress varchar(500);

-- alter table bookspackage add column LoadingDay varchar(20);
-- alter table bookspackagehistory add column LoadingDay varchar(20);
