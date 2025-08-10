--3a
	--2a

	select noident, idsistema, dtviagem, hinicio, hfim, valfinal from PESSOA 
	join CLIENTEVIAGEM on (idpessoa = id) 
	join VIAGEM on (viagem = idsistema)
	where(atrdisc = 'CL' and nproprio = 'António' and apelido = 'Silva');

	--2b

	select nproprio, apelido, noident from CONDUTOR 
	join CONDHABILITADO on (condutor = idpessoa) 
	join VEICULO on (veiculo = VEICULO.id) 
	join PROPRIETARIO on (proprietario = PROPRIETARIO.idpessoa) 
	join PESSOA on (PROPRIETARIO.idpessoa = PESSOA.id and nproprio = 'Bruno' and apelido = 'Ferreira' and apelido != 'Brito' and nproprio != 'Carlos') 
	where (atrdisc = 'C');

	--2c

	select id, nif, nproprio, apelido 
	from PESSOA
	join (select idpessoa, count(idpessoa) total 
		from CLIENTEVIAGEM
		join VIAGEM on (viagem = idsistema)
		where(DATE_PART('year', dtviagem)='2021')
		group by idpessoa) as totalviagens on (totalviagens.idpessoa = id)
	where (select max(total) 
		from (
			select idpessoa, count(idpessoa) total 
			from CLIENTEVIAGEM
			join VIAGEM on (viagem = idsistema)
			where(DATE_PART('year', dtviagem)='2021')
			group by idpessoa) as maxtotalviagens) = totalviagens.total;

	--2d

	select distinct id, nif, nproprio, apelido from VIAGEM 
	join PESSOA on (atrdisc = 'C') 
	where id not in (select condutor from VIAGEM);

	--2e

	select nproprio, apelido, count(viagem) as num_viagens from Condutor 
	join PESSOA on (CONDUTOR.idpessoa = id) 
	join CLIENTEVIAGEM on (CONDUTOR.idpessoa = CLIENTEVIAGEM.idpessoa) 
	group by nproprio, apelido order by num_viagens;

--3b

select count(viagem) as num_viagens from CLIENTEVIAGEM 
join VIAGEM on (viagem = idsistema)
join VEICULO on (VEICULO.ano = 2012 and VEICULO.id = veiculo) 
join PESSOA on (proprietario = PESSOA.id and nif = '987654321');

--3c

select nproprio, apelido, noident, morada 
from PESSOA
join (select idpessoa, sum(valfinal) total 
	from VIAGEM 
	join CONDUTOR on (condutor = idpessoa)
	group by idpessoa) as ganhos on ganhos.idpessoa = id
where (select max(total) 
from (select idpessoa, sum(valfinal) total 
	from VIAGEM 
	join CONDUTOR on (condutor = idpessoa)
	group by idpessoa) as maxganhos) = ganhos.total;

--3d

select nproprio, apelido, count(idsistema) as total_viagens from VIAGEM 
join PESSOA on (VIAGEM.condutor = id and atrdisc = 'C')
group by nproprio, apelido, noident, morada
having count(idsistema) < 3;

--3e

create view CONDUTORVIAGENSPORANO as
select CONDUTOR.idpessoa as condutor, DATE_PART('year', VIAGEM.dtviagem) as ano, count(idsistema) as num_viagens, sum(valfinal) as ganhos
from CONDUTOR join VIAGEM on (CONDUTOR.idpessoa = VIAGEM.condutor)
group by CONDUTOR.idpessoa, ano;

--3f

create table CLIENTEVIAGENSANUAIS as
select CLIENTEVIAGEM.idpessoa as cliente, DATE_PART('year', VIAGEM.dtviagem) as ano, count(viagem) as num_viagens
from CLIENTEVIAGEM join VIAGEM on (CLIENTEVIAGEM.viagem = VIAGEM.idsistema)
group by CLIENTEVIAGEM.idpessoa, ano;

--4
--	A

insert into TIPOVEICULO(tipo, designação, nlugares) values (4,'Premium',4);

--	B

update VEICULO set tipo = 4 where modelo = 'RangeRover';

