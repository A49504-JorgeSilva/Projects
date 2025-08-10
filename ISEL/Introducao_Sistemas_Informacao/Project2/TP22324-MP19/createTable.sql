begin try
	
	begin transaction
	..print 'CreateTables'

	create table DISPOSITIVO(
		noserie integer primary key,
		latitude numeric(6,4) check (latitude between -90 and 90),
		longitude numeric(6,4) check (longitude between -180 and 180),
		bateria integer check (bateria between 0 and 100)
	);
	
	create table BICICLETA(
		id serial integer primary key not null,
		peso numeric(4,2) check (peso > 0) not null,
		raio roda integer check (raio between 13 and 23) not null,
		modelo varchar(20) not null,
		marca varchar(30) not null,
		mudanca integer check (mudanca in (1,6,18,24)),--pode ser null se CLASSICA n tiver mudanças
		estado varchar(30) check (estado in ('livre','ocupado','em manutenção'))
		atrdisc char(2) check (estado in ('C','E')) not null,
		dispositivo integer not null,
		foreign key (dispositivo) references DISPOSITIVO(noserie) on delete cascade on update cascade
	);
	
	create table CLASSICA(
		bicicleta integer primary key not null,
		nomudanca integer check (nomudanca between 0 and 5) not null,
		foreign key (bicicleta references BICICLETA(id) on delete cascade on update cascade
	);
	
	create table ELETRICA(
		bicicleta integer primary key not null,
		autonomia integer check (autonomia > 0) not null,
		velocidade integer check (velocidade >= 0) not null,
		foreign key (bicicleta references BICICLETA(id) on delete cascade on update cascade
	);
	
	create table PESSOA(
		id serial integer primary key not null,
		nome varchar(40) not null,
		morada varchar(150) not null,
		email varchar(40) not null,
		telefone varchar(30) not null,
		noident char(12) not null,
		nacionalidade varchar(20) not null,
		atrdisc char(2) check(atrdisc in ('G','C')) not null,
		constraint email_valido check (email ~ '@')
	);
	
	create table LOJA(
		codigo integer primary key not null,
		email varchar(40) not null,
		endereco varchar(100) not null,
		localidade varchar(30),
		gestor integer not null,
		foreign key (gestor references PESSOA(id) on delete cascade on update cascade,
		constraint email_valido check (email ~ '@')
	);
	
	create table RESERVA(
		noreserva serial integer not null,
		loja integer not null,
		dtinicio timestamp check(dtinicio similar to 'aaaa-mm-dd hh:mm:ss') not null,
		dtfim timestamp check(dtfim similar to 'aaaa-mm-dd hh:mm:ss' and dtfim > dtinicio),
		valor numeric(4,2) not null, --valor em euros
		bicicleta integer not null,
		primary key (noreserva, loja),
		foreign key (loja references LOJA(codigo) on delete cascade on update cascade, 
		foreign key (bicicleta references BICICLETA(id) on delete cascade on update cascade
	);
	
	create table CLIENTERESERVA(
		cliente integer not null,
		reserva integer not null,
		loja integer not null,
		primary key (cliente,reserva,loja),
		foreign key (cliente) references PESSOA(id) on delete cascade on update cascade,
		foreign key (reserva,loja) references RESERVA(noreserva,loja) on delete cascade on update cascade
	);

	create table TELEFONELOJA(
		loja integer not null,
		numero varchar(10) not null,
		primary key (loja,numero),
		foreign key (loja) references LOJA(codigo) on delete cascade on update cascade,
	);

	commit transaction
end try

begin catch
	select 'Entrei no catch'
	rollback
	-- Generates an error message and initiates error
	-- Processing for the session 
	raiserror('Erro',16,1)
end catch
	
