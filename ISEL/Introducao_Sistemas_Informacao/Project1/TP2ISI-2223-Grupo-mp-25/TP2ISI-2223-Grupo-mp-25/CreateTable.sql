
create table PESSOA(
	id integer not null unique,
	noident char(12) not null,
	nif char(12) not null,
	primary key(id,noident,nif),
	nproprio varchar(10) not null,
	apelido varchar(10) not null,
	morada varchar(150) not null,
	codpostal integer not null check(codpostal BETWEEN 999999 AND 10000000),
	localidade varchar(150) not null,
	atrdisc char(2) not null check (atrdisc in ('P', 'C', 'CL'))
);

create table PROPRIETARIO(
	idpessoa integer not null primary key,
	dtnascimento date not null check (DATE_PART ('year', current_date) - DATE_PART ('year', dtnascimento) >= 18),
	foreign key (idpessoa) references PESSOA(id) on delete cascade on update cascade
);


create table CONTACTO(
	idpessoa integer not null,
	ntelefone varchar(15) not null,
	primary key(idpessoa,ntelefone),
	foreign key (idpessoa) references PESSOA(id) on delete cascade on update cascade
);

create table CONDUTOR(
	idpessoa integer not null primary key,
	ncconducao varchar(20) not null unique 
	check (ncconducao similar to '[A-Za-z][A-Za-z]-[0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
	dtnascimento date not null check (DATE_PART('year', current_date) - DATE_PART('year', dtnascimento) >= 18),
	foreign key (idpessoa) references PESSOA(id) on delete cascade on update cascade
);

create table TIPOVEICULO(
	tipo integer not null primary key,
	nlugares integer not null check(nlugares between 0 and 8),
	multiplicador numeric(1),
	designação char(10) not null
);

create table VEICULO(
	id integer not null unique,
	matricula varchar(10) not null 
	check ( matricula similar to '[A-Za-z][A-Za-z][0-9][0-9][A-Za-z][A-Za-z]' or
	matricula similar to '[0-9][0-9][A-Za-z][A-Za-z][0-9][0-9]'),
	tipo integer not null,
	modelo varchar(10) not null,
	marca varchar(10) not null,
	ano integer not null check (DATE_PART ('year', current_date) - ano >= 5),
	proprietario integer not null,
	foreign key (proprietario) references PROPRIETARIO(idpessoa) on delete cascade on update cascade,
	foreign key (tipo) references TIPOVEICULO(tipo) on delete cascade on update cascade
);

create table CONDHABILITADO(
	condutor integer not null,
	veiculo integer not null,
	primary key(condutor,veiculo),
	foreign key (condutor) references CONDUTOR(idpessoa) on delete cascade on update cascade,
	foreign key (veiculo) references VEICULO(id) on delete cascade on update cascade
);

create table CORVEICULO(
	veiculo integer not null,
	cor varchar(10) not null,
	primary key(veiculo,cor),
	foreign key (veiculo) references VEICULO(id) on delete cascade on update cascade
);

create table PERIODOACTIVO(
	veiculo integer not null unique,
	condutor integer not null unique,
	dtinicio timestamp not null unique,
	primary key(veiculo,condutor,dtinicio),
	dtfim timestamp,
	lat decimal(6,2) not null,
	long decimal(6,2) not null,
	foreign key (veiculo) references VEICULO(id) on delete cascade on update cascade,
	foreign key (condutor) references CONDUTOR(idpessoa) on delete cascade on update cascade
);

create table VIAGEM(
	idsistema integer not null primary key,
	hinicio time not null ,
	hfim time,
	dtviagem date not null,
	valestimado decimal(5,2) not null,
	valfinal decimal(5,2) ,
	latinicio decimal(6,2) not null,
	longinicio decimal(6,2) not null,
	latfim decimal(6,2),
	longfim decimal(6,2),
	classificação integer not null check(classificação between 0 and 6 or NULL),
	veiculo integer not null,
	condutor integer not null,
	dtinicio timestamp not null,
	foreign key (veiculo) references PERIODOACTIVO(veiculo) on delete cascade on update cascade,
	foreign key (condutor) references PERIODOACTIVO(condutor) on delete cascade on update cascade,
	foreign key (dtinicio) references PERIODOACTIVO(dtinicio) on delete cascade on update cascade
);

create table CLIENTEVIAGEM(
	idpessoa integer not null,
	viagem integer not null,
	primary key(idpessoa, viagem),
	foreign key (idpessoa) references PESSOA(id) on delete cascade on update cascade,
	foreign key (viagem) references VIAGEM(idsistema) on delete cascade on update cascade
);

