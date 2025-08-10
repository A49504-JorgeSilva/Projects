create table PERSON(
	id serial not null primary key,
	email varchar(40) unique not null check(email like '%@%'),
	taxnumber integer unique not null,
	name varchar(50) not null
);

create table CLIENT(
	person integer not null primary key,
	dtregister timestamp not null,
	foreign key(person) references PERSON(id) on delete cascade on update cascade
);

create table EMPLOYEE(
	number serial unique not null,
	person integer not null primary key,
	foreign key(person) references PERSON(id) on delete cascade on update cascade
);

create table STATION(
	id serial not null primary key,
	latitude numeric (6,4) not null,
	longitude numeric(6,4) not null
);

create table TYPEOF(
	reference char(10) not null primary key check(reference in ('resident','tourist')),
	nodays integer not null check(nodays > 0),
	price numeric(4,2) not null check(price > 0)
);

create table CARD(
	id serial not null primary key,
	credit numeric(4,2) not null check(credit > 0),
	typeof char(10) not null,
	client integer not null,
	foreign key(typeof) references TYPEOF(reference) on delete cascade on update cascade,
	foreign key(client) references CLIENT(person) on delete cascade on update cascade
);

create table TOPUP(
	dttopup timestamp not null,
	card integer not null,
	value numeric(4,2) not null check(value > 0),
	primary key(dttopup,card),
	foreign key(card) references CARD(id) on delete cascade on update cascade
);

create table SCOOTERMODEL(
	number serial not null primary key,
	designation varchar(30) not null,	
	autonomy integer not null check(autonomy >0)
);

create table SCOOTER(
	id serial not null primary key,
	weight numeric(4,2) not null check(weight > 0),
	maxvelocity numeric(4,2) not null check(maxvelocity > 0),
	battery integer not null check(battery > 0),
	model integer not null,
	foreign key(model) references SCOOTERMODEL(number) on delete cascade on update cascade
);

create table DOCK(
	number serial not null,
	station integer not null,
	state varchar(30) not null check(state in('free','occupy','under maintenance')),
	scooter integer,
	primary key(number,station),
	foreign key(station) references STATION(id) on delete cascade on update cascade,
	foreign key(scooter) references SCOOTER(id) on delete cascade on update cascade,
	check(not(state = 'occupy' and scooter is null))
);

create table REPLACEMENTORDER(
	dtorder timestamp not null,
	dtreplacement timestamp check(dtreplacement > dtorder),
	roccupation integer not null check(roccupation between 0 and 100),	
	station integer not null,
	primary key(dtorder,station),
	foreign key(station) references STATION(id) on delete cascade on update cascade
);

create table REPLACEMENT(-- pode ser tudo null caso ainda n tenha havido um replacement?
	number serial not null,
	dtreporder timestamp not null,
	dtreplacement timestamp check(dtreplacement > dtreporder),
	action char(8) not null check(action in('inplace','remove')),
	repstation integer not null,
	employee integer not null,
	primary key(number,dtreporder,repstation),
	foreign key(dtreporder,repstation) references REPLACEMENTORDER(dtorder,station) on delete cascade on update cascade,
	foreign key(employee) references EMPLOYEE(person) on delete cascade on update cascade
);

create table TRAVEL(
	dtinitial timestamp not null,
	comment varchar(100),
	evaluation integer not null check(evaluation between '1' and 5),
	dtfinal timestamp check (dtfinal > dtinitial),
	client integer not null,
	scooter integer unique not null,
	stinitial integer not null,
	stfinal integer not null,
	primary key(dtinitial, client),
	foreign key(client) references CLIENT(person) on delete cascade on update cascade,
	foreign key(scooter) references SCOOTER(id) on delete cascade on update cascade,
	foreign key(stinitial) references STATION(id) on delete cascade on update cascade,
	foreign key(stfinal) references STATION(id) on delete cascade on update cascade,
	check(evaluation is not null or comment is null)
);

create table SERVICECOST(
	unlock numeric(3,2) not null check(unlock = 1),
	usable numeric(3,2) not null check(usable = 0.15)
)