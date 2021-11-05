-- Creación base de datos y uso de la misma

create database bdHoteles;

use bdHoteles;

-- Creación de Tablas

create table hoteles (
	codHotel char(6) primary key, 
    nomHotel varchar(60)
);

create table habitaciones (
	codHotel char(6),
	numHabitacion char(4),
    primary key (codHotel, numHabitacion),
    foreign key (codHotel) references hoteles(codHotel),
    capacidad tinyInt default 2, 
    preciodia int, 
    activa tinyint(1) default 0
); /*0 si no está en funcionamiento, 1 si está en funcionamiento*/

create table regimenes (
	codRegimen int auto_increment primary key, 
    codHotel char(6), foreign key (codHotel) references hoteles(codHotel),
    tipo char(2),
    precio int
);

create table clientes (
	coddnionie char(9) primary key,
    nombre varchar(50),
    nacionalidad varchar(40)
);

create table estancias (
	codEstancia int auto_increment primary key,
    coddnionie char(9), foreign key (coddnionie) references clientes(coddnionie),
    codHotel char(6),
    numHabitacion char(4), foreign key (codHotel,numHabitacion) references habitaciones(codHotel,numHabitacion),
    fechaInicio date,
    fechaFin date,
    codRegimen int, foreign key (codRegimen) references regimenes(codRegimen),
    ocupantes tinyint default 2,
    precioestancia int, 
    pagado tinyint(1) default 1
);

-- Insercciones, procedimientos y funciones respectivamente.

insert into hoteles values ("111111", "Barceló Canarias");
insert into hoteles values ("222222", "Iberostar Heritage");

insert into habitaciones values ("111111",1,2,30,default);
insert into habitaciones values ("222222",2,1,40,1);

Delimiter ??
create procedure proc_habitaciones_hotel (nomHotel varchar(60)) 
begin
	select numHabitacion, capacidad, preciodia, activa from habitaciones
    inner join hoteles on hoteles.codHotel = habitaciones.codHotel
    where hoteles.nomHotel = nomHotel;
end ??
Delimiter ;

call proc_habitaciones_hotel ("Barceló Canarias");

delimiter ??
create procedure proc_insert_habitacion (
	codHotel char(6), numHabitacion char(4), capacidad tinyint, preciodia int, activa tinyint(1),
    out hotel_exists tinyint(1), out validate_insert tinyint(1)
)
begin
    declare exit handler for SQLException
	begin
		select 0 = validate_insert;
	end;
    if (select count(*) from hoteles where hoteles.codHotel=codHotel)>0 
    then 
		select 1 into hotel_exists;
		insert into habitaciones values (codHotel, numHabitacion, capacidad, preciodia, activa);
        select 1 into validate_insert;
	else 
		select 0 into hotel_exists;
        select 0 into validate_insert;
    end if;
end ??
delimiter ;

call proc_insert_habitacion ("111111", 4, 4, 50, 1, @hotel_exists, @validate_insert);

select @hotel_exists, @validate_insert;

delimiter ??
create procedure proc_cantidad_habitaciones (nomHotel varchar(60), preciodia int, out cantidadTotal int, out cantidadTotal_preciodia int)
begin
    select (select count(*) from habitaciones
    inner join hoteles on hoteles.codHotel = habitaciones.codHotel
    where hoteles.nomHotel = nomHotel) 
    into cantidadTotal;
    
    select (select count(*) from habitaciones
    inner join hoteles on hoteles.codHotel = habitaciones.codHotel
    where hoteles.nomHotel = nomHotel and habitaciones.preciodia < preciodia and activa = 1)
    into cantidadTotal_preciodia;
end ??
delimiter ;

call proc_cantidad_habitaciones ("Iberostar Heritage", 35, @cantidadTotal, @cantidadTotal_preciodia);

select @cantidadTotal, @cantidadTotal_preciodia;

insert into clientes values ("11111111A", "Lolo", "Angolés");

insert into regimenes values (1,"111111","PC",60);

insert into estancias values (1,"11111111A","111111",1,"2021-08-22","2021-09-22", 1, default, 60, 1);
insert into estancias values (2,"11111111A","111111",1,"2021-07-22","2021-08-22", 1, default, 60, 1);

delimiter ??
create function sp_dni_suma (dnionie char(9)) returns int
begin
	return (
    select 
		sum(precioestancia*(datediff(FechaFin,FechaInicio))) 
	from estancias
	); 
end ??
delimiter ;

select sp_dni_suma("11111111A") as CantidadTotal;

-- Triggers

delimiter ??
create trigger tr_capacidad_habitacion_insert before insert on estancias for each row
begin
	if ((select capacidad from habitaciones where numHabitacion=new.numHabitacion and habitaciones.codHotel = new.codHotel)
    <
    (new.ocupantes))
    then
		signal sqlstate  '45000'
        set message_text="El número de ocupantes no puede superar la capacidad de la habitación";
    end if;
end ??
Delimiter ;

-- insert into estancias values (3,"11111111A",222222,2,"2021-06-22","2021-07-22",1,45,60,0);

delimiter ??
create trigger tr_capacidad_habitacion_update before update on estancias for each row
begin
	if ((select capacidad from habitaciones where numHabitacion=new.numHabitacion and habitaciones.codHotel = new.codHotel)
    <
    (new.ocupantes))
    then
		signal sqlstate  '45000'
        set message_text="El número de ocupantes no puede superar la capacidad de la habitación";
    end if;
end ??
Delimiter ;

delimiter ??
create trigger tr_precioEstancia before insert on estancias for each row 
begin
	set @preciodiahabitacion = (select preciodia from habitaciones where numHabitacion=new.numHabitacion);
    set @numerodedias = (datediff(new.FechaFin,new.FechaInicio));
    set @precioregimen = (select precio from regimenes where codRegimen=new.codRegimen);
    set new.precioestancia = (@numerodedias*(@preciodiahabitacion+new.ocupantes*@precioregimen));
end ??
Delimiter ;

insert into estancias values (3,"11111111A",111111,1,"2021-06-22","2021-07-22",1,2,60,0);

delimiter ??
create trigger tr_no_eliminar_hotel before delete on hoteles for each row
begin
	signal sqlstate '45000'
    set message_text = "No se puede eliminar ningún hotel";
end ??
delimiter ;

-- drop database bdHoteles_1;