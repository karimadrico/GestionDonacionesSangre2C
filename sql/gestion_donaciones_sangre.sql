
drop table hospital cascade constraints;
drop table tipo_sangre cascade constraints;
drop table reserva_hospital cascade constraints;
drop table donante cascade constraints;
drop table donacion cascade constraints;
drop table traspaso cascade constraints;

drop sequence seq_hospital;
drop sequence seq_tipo_sangre;
drop sequence seq_donacion;
drop sequence seq_traspaso;


-- hospital que recibe litros de sangre
create sequence seq_hospital;
create table hospital (
	id_hospital	integer,
	nombre	varchar(100) not null,
	localidad varchar(100) not null,
    constraint hospital_pk primary key (id_hospital)
);

-- tipos de sangre del centro, en el campo reserva está la sangre de la que disponibe el centro ante la petición de hospitales, nunca menor que 0
create sequence seq_tipo_sangre;
create table tipo_sangre (
	id_tipo_sangre	integer,
	descripcion	varchar(10) not null,
    constraint tipo_sangre_pk primary key (id_tipo_sangre)
);


create table reserva_hospital (
    id_tipo_sangre integer,
    id_hospital integer,
    cantidad float not null,
    constraint reserva_sangre_pk primary key (id_tipo_sangre, id_hospital),
    constraint reserva_tipo_sangre_fk foreign key (id_tipo_sangre) references tipo_sangre(id_tipo_sangre),
    constraint reserva_hospital_fk foreign key (id_hospital) references hospital(id_hospital),
    constraint reserva_cantidad_reserva_check  check (cantidad >= 0)
);


-- persona que dona y tiene un tipo de sangre
create table donante (
	NIF	varchar(9) ,
	nombre	varchar(20) not null,
	ape1	varchar(20) not null,
	ape2	varchar(20) not null,
	fecha_nacimiento date not null,	
	id_tipo_sangre integer not null,
    constraint donante_pk primary key (NIF),
    constraint donante_tipo_sangre_fk foreign key (id_tipo_sangre) references tipo_sangre(id_tipo_sangre)
);


-- en esta tabla se guarda cada donación
create sequence seq_donacion;
create table donacion (
	id_donacion	integer,
	nif_donante varchar(20) not null,
	cantidad float not null,
	fecha_donacion date not null,
    constraint donacion_pk primary key (id_donacion),
    constraint donacion_nif_donante_fk foreign key (nif_donante) references donante(NIF),
    constraint donacion_cantidad_min_check check (cantidad >= 0), 
    constraint donacion_cantidad_max_check check(cantidad <= 0.45)
);


-- en esta tabla se guarda cada traspaso de sangre del centro a un hospital
create sequence seq_traspaso;
create table traspaso (
	id_traspaso	integer,
	id_tipo_sangre integer not null,
	id_hospital_origen integer not null,
	id_hospital_destino integer not null,
	cantidad float not null,
	fecha_traspaso date not null,
    constraint traspaso_pk primary key (id_traspaso),
    constraint traspaso_reserva_origen_fk foreign key (id_tipo_sangre,id_hospital_origen) references reserva_hospital(id_tipo_sangre,id_hospital),
    constraint traspaso_reserva_destino_fk foreign key (id_tipo_sangre,id_hospital_destino) references reserva_hospital (id_tipo_sangre,id_hospital),
    constraint traspaso_cantidad_check check (cantidad >= 0)
);



-------------------------------------------------------------------------------------


create or replace procedure reset_seq( p_seq_name varchar ) is
--From https://stackoverflow.com/questions/51470/how-do-i-reset-a-sequence-in-oracle
    l_val number;
begin
    --Averiguo cual es el siguiente valor y lo guardo en l_val
    execute immediate
    'select ' || p_seq_name || '.nextval from dual' INTO l_val;

    --Utilizo ese valor en negativo para poner la secuencia cero, pimero cambiando el incremento de la secuencia
    execute immediate
    'alter sequence ' || p_seq_name || ' increment by -' || l_val || 
                                                          ' minvalue 0';
   --segundo pidiendo el siguiente valor
    execute immediate
    'select ' || p_seq_name || '.nextval from dual' INTO l_val;

    --restauro el incremento de la secuencia a 1
    execute immediate
    'alter sequence ' || p_seq_name || ' increment by 1 minvalue 0';

end;
/


create or replace procedure inicializa_test is
    sangre_a tipo_sangre.id_tipo_sangre%type;
    sangre_b tipo_sangre.id_tipo_sangre%type;
    sangre_ab tipo_sangre.id_tipo_sangre%type;
    sangre_o tipo_sangre.id_tipo_sangre%type;
begin
    reset_seq( 'seq_tipo_sangre' );
    reset_seq( 'seq_hospital' );
    reset_seq( 'seq_traspaso' );
    reset_seq(' seq_donacion');
    
    
	delete from traspaso;
    delete from reserva_hospital;
	delete from donacion;
	delete from donante;
	delete from tipo_sangre;
	delete from hospital;
    

	insert into tipo_sangre values (seq_tipo_sangre.nextval, 'Tipo A.');
    sangre_a:= seq_tipo_sangre.currval;
	insert into tipo_sangre values (seq_tipo_sangre.nextval, 'Tipo B.');
    sangre_b:= seq_tipo_sangre.currval;
	insert into tipo_sangre values (seq_tipo_sangre.nextval, 'Tipo AB.');
    sangre_ab:= seq_tipo_sangre.currval;
	insert into tipo_sangre values (seq_tipo_sangre.nextval, 'Tipo O.');
    sangre_o:= seq_tipo_sangre.currval;
    
	
	insert into hospital values (seq_hospital.nextval, 'Complejo Asistencial de Avila', 'Avila');
    insert into reserva_hospital values (sangre_a, seq_hospital.currval, 3.45);
    insert into reserva_hospital values (sangre_b, seq_hospital.currval, 2.5);
    insert into reserva_hospital values (sangre_ab, seq_hospital.currval, 5.82);
    insert into reserva_hospital values (sangre_o, seq_hospital.currval, 2.6);
    
	insert into hospital values (seq_hospital.nextval, 'Hospital Santos Reyes de Aranda de Duero', 'Aranda Duero');
    insert into reserva_hospital values (sangre_a, seq_hospital.currval, 2.45);
    insert into reserva_hospital values (sangre_b, seq_hospital.currval, 5.5);
    insert into reserva_hospital values (sangre_ab, seq_hospital.currval, 8.82);
    
	insert into hospital values (seq_hospital.nextval, 'Complejo Asistencial Univesitario de Leon', 'Leon');
    insert into reserva_hospital values (sangre_a, seq_hospital.currval, 6.52);
    insert into reserva_hospital values (sangre_b, seq_hospital.currval, 5.7);
    insert into reserva_hospital values (sangre_ab, seq_hospital.currval, 10.26);
    insert into reserva_hospital values (sangre_o, seq_hospital.currval, 8.64);
    
	insert into hospital values (seq_hospital.nextval, 'Complejo Asistencial Universitario de Palencia', 'Palencia');
    insert into reserva_hospital values (sangre_ab, seq_hospital.currval, 3.61);
    insert into reserva_hospital values (sangre_o, seq_hospital.currval, 12.91);
    
    
    insert into donante values ('12345678A', 'Juan', 'Garcia', 'Porras', to_date('24/03/1983', 'DD/MM/YYYY'), sangre_a);
	insert into donante values ('77777777B', 'Lucia', 'Rodriguez', 'Martin', to_date('12/04/1963', 'DD/MM/YYYY'), sangre_a);
	insert into donante values ('98989898C', 'Maria', 'Fernandez', 'Dominguez', to_date('01/12/1977', 'DD/MM/YYYY'), sangre_o);
	insert into donante values ('98765432Y', 'Alba', 'Serrano', 'Garcia', to_date('09/06/1997', 'DD/MM/YYYY'), sangre_ab);
    
    
	insert into donacion values (seq_donacion.nextval, '12345678A', 0.25, to_date('10/01/2025', 'DD/MM/YYYY') );
	insert into donacion values (seq_donacion.nextval, '12345678A', 0.40, to_date('15/01/2025', 'DD/MM/YYYY') );
	insert into donacion values (seq_donacion.nextval, '77777777B', 0.35, to_date('15/01/2025', 'DD/MM/YYYY') );
	insert into donacion values (seq_donacion.nextval, '98989898C', 0.25, to_date('25/01/2025', 'DD/MM/YYYY') );
	insert into donacion values (seq_donacion.nextval, '98765432Y', 0.35, to_date('25/01/2025', 'DD/MM/YYYY') );
	
    
	insert into traspaso values (seq_traspaso.nextval, 1, 1, 2, 2, to_date('11/01/2025', 'DD/MM/YYYY') );
	insert into traspaso values (seq_traspaso.nextval, 2, 1, 2, 3, to_date('11/01/2025', 'DD/MM/YYYY') );
	insert into traspaso values (seq_traspaso.nextval, 3, 1, 2, 2.1, to_date('11/01/2025', 'DD/MM/YYYY') );
	insert into traspaso values (seq_traspaso.nextval, 1, 2, 3, 1.2, to_date('16/01/2025', 'DD/MM/YYYY') );
	insert into traspaso values (seq_traspaso.nextval, 2, 3, 2, 10, to_date('16/01/2025', 'DD/MM/YYYY') );
	
    commit;
end;
/

exit;