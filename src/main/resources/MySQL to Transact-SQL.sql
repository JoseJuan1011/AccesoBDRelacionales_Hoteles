create database bdHoteles
go

use bdHoteles
go

go
create table hoteles (
	codHotel char(6) primary key,
	nomHotel varchar(60)
)
go

go
create table habitaciones (
	codHotel char(6),
	numHabitacion char(4),
	primary key (codHotel, numHabitacion),
	foreign key (codHotel) references hoteles(codHotel),
	capacidad tinyint default 2,
	preciodia int,
	activa tinyint default 0
)
go

go
create table regimenes (
	codRegimen int identity primary key,
	codHotel char(6), foreign key (codHotel) references hoteles(codHotel),
	tipo char(2),
	precio int
)
go

go
create table clientes (
	coddnionie char(9) primary key,
    nombre varchar(50),
    nacionalidad varchar(40)
)
go

go
create table estancias (
	codEstancia int identity primary key,
    coddnionie char(9), foreign key (coddnionie) references clientes(coddnionie),
    codHotel char(6),
    numHabitacion char(4), foreign key (codHotel,numHabitacion) references habitaciones(codHotel,numHabitacion),
    fechaInicio date,
    fechaFin date,
    codRegimen int, foreign key (codRegimen) references regimenes(codRegimen),
    ocupantes tinyint default 2,
    precioestancia int, 
    pagado tinyint default 1
)
go

go
insert into hoteles values ('111111', 'Barceló Canarias')
insert into hoteles values ('222222', 'Iberostar Heritage')
go

select * from hoteles

go
insert into habitaciones values ('111111',1,2,30,default)
insert into habitaciones values ('222222',2,1,40,1)
go

go
create procedure procHabitacionesHotel (@nomHotel varchar(60))
as
	select numHabitacion, capacidad, preciodia, activa from habitaciones
    inner join hoteles on hoteles.codHotel = habitaciones.codHotel
    where hoteles.nomHotel = @nomHotel;
go

go
exec procHabitacionesHotel @nomHotel = 'Barceló Canarias';
go

go
create procedure proc_insert_habitacion (
	@codHotel char(6), @numHabitacion char(4), @capacidad tinyint, @preciodia int, @activa tinyint,
    @hotel_exists tinyint output, @validate_insert tinyint output
)
as
	begin try
		if (select count(*) from hoteles where hoteles.codHotel=codHotel)>0 
		begin
			set @hotel_exists = 1;
			insert into habitaciones values (@codHotel, @numHabitacion, @capacidad, @preciodia, @activa);
			set @validate_insert = 1
		end
		else 
		begin
			set @hotel_exists = 0;
			set @validate_insert = 0;
		end
	end try
	begin catch
		 set @validate_insert = 0;
	end catch
go

go
declare @hotel_exists tinyint, @validate_insert tinyint
exec proc_insert_habitacion '111111', 3, 4, 50, 1, @hotel_exists output, @validate_insert output
select @hotel_exists, @validate_insert
go

--delete from habitaciones where codHotel = '111111' and numHabitacion=3;
--select * from habitaciones;

go
create procedure proc_cantidad_habitaciones (
	@nomHotel varchar(60), @preciodia int,
	@cantidadTotal tinyint output, @cantidadTotal_preciodia tinyint output
)
as
	set @cantidadTotal = (select count(*) from habitaciones
    inner join hoteles on hoteles.codHotel = habitaciones.codHotel
    where hoteles.nomHotel = @nomHotel)
    
	set @cantidadTotal_preciodia = (select count(*) from habitaciones
    inner join hoteles on hoteles.codHotel = habitaciones.codHotel
    where hoteles.nomHotel = @nomHotel and habitaciones.preciodia < @preciodia and activa = 1)
go

--drop procedure proc_cantidad_habitaciones

--select * from hoteles;
--select * from habitaciones;

go
declare @cantidadTotal tinyint, @cantidadTotal_preciodia tinyint
exec proc_cantidad_habitaciones 'Barceló Canarias', 100, @cantidadTotal output, @cantidadTotal_preciodia output
select @cantidadTotal as CantidadTotal, @cantidadTotal_preciodia as CantidadTotal_preciodia
go

go
insert into clientes values ('11111111A', 'Lolo', 'Angolés');
select * from clientes;

insert into regimenes values ('111111','PC',60);
select * from regimenes;

insert into estancias values ('11111111A','111111',1,'2021-08-22','2021-09-22', 5, default, 60, 1);
insert into estancias values ('11111111A','111111',1,'2021-07-22','2021-08-22', 5, default, 60, 1);
select * from estancias;
go

go
create function sp_dni_suma (@dnionie char(9)) returns int
as
begin
	return (select sum(precioestancia*(datediff(DAY,FechaInicio,FechaFin))) from estancias); 
end
go

go
Declare @CantidadEstancias int
exec @CantidadEstancias = dbo.sp_dni_suma '11111111A'
select @CantidadEstancias as CantidadEstancias
go

-- SET IDENTITY_INSERT estancias OFF;
SET IDENTITY_INSERT estancias on;

go
create trigger tr_capacidad_habitacion_insert on estancias instead of insert
as
	if ((select capacidad from habitaciones where numHabitacion=(select numHabitacion from inserted) and habitaciones.codHotel =(select codHotel from inserted)) < (select ocupantes from inserted))
    begin
		RAISERROR ('El número de ocupantes no puede superar la capacidad de la habitación.' ,10,1)
		rollback transaction
    end
go

go
create trigger tr_capacidad_habitacion_update on estancias instead of update
as
	if ((select capacidad from habitaciones where numHabitacion=(select numHabitacion from inserted) and habitaciones.codHotel =(select codHotel from inserted)) < (select ocupantes from inserted))
    begin
		RAISERROR ('El número de ocupantes no puede superar la capacidad de la habitación.' ,10,1)
		rollback transaction
    end
go


go
create trigger tr_precioEstancia on estancias for insert
as
	declare @preciodiahabitacion int, @numerodedias int, @precioregimen int, @precioestancia int;

	set @preciodiahabitacion = (select preciodia from habitaciones where numHabitacion=(select numHabitacion from inserted));

    set @numerodedias = datediff(day,(select FechaInicio from inserted),(select FechaFin from inserted));

    set @precioregimen = (select precio from regimenes where codRegimen=(select codRegimen from inserted));

	set @precioestancia = (@numerodedias*(@preciodiahabitacion+(select ocupantes from inserted)*@precioregimen));

    update estancias set precioestancia = @precioestancia
	where codEstancia = (select codEstancia from inserted)
go

insert into estancias values ('11111111A',111111,1,'2021-06-22','2021-07-22',5,2,60,0);
select precioestancia from estancias where codEstancia = 6;

select * from estancias

go
create trigger tr_no_eliminar_hotel  on hoteles instead of delete
as
	raiserror ('No se puede eliminar ningún hotel' ,10,1)
	rollback transaction
go