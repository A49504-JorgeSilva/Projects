insert into PESSOA(id, noident, nif, nproprio, apelido, morada, codpostal, localidade,
atrdisc) values
(1,'123456789012','123456789','Rodrigo','Rodrigues','morada1','1234123','localidade1','C'),
(2,'210987654321','987654321','Agosto','Agostinho','morada2','1234123','localidade1','C'),
(3,'566771230982','999999999','Antonio','Antunes','morada3','1234123','localidade3','CL'),
(4,'987462910470','173890128','Diogo','Diego','morada4','1234123','localidade4','P'),
(5,'678781423451','508128749','Peter','Parker','morada5','1234123','localidade5','CL');

insert into CONTACTO(idpessoa, ntelefone) values
(1,'351213456789'),
(2,'351960322212'),
(3,'351356762831'),
(4,'351912437687'),
(5,'351524576543');

insert into PROPRIETARIO(idpessoa, dtnascimento) values
(1,'2002-02-23'),
(2,'2002-03-12'),
(3,'1996-10-01'),
(4,'1964-10-25'),
(5,'2002-11-28');
                                                        
insert into TIPOVEICULO(tipo, nlugares, multiplicador, designação) values
(1,2,1,'Normal'),
(2,2,1,'Luxo'),
(3,5,1,'Normal');

insert into CONDUTOR(idpessoa, ncconducao, dtnascimento) values
(1,'aa-1234567','2002-11-26'),
(2,'aa-0743736','2002-02-23');

insert into VEICULO(id, matricula, tipo, modelo, marca, ano, proprietario) values
(1,'AA00AA',1,'RangeRover','Fiat',2015,1),
(2,'GF67BF',2,'polo','Volkswagen',2012,2),
(3,'50ZO53',3,'a3','Audi',2013,3);

insert into CORVEICULO(veiculo,cor) values
(1,'AMARELO'),
(2,'VERDE'),
(3,'AZUL');

insert into CONDHABILITADO(condutor, veiculo) values
(1,1),
(2,2),
(1,3);

insert into PERIODOACTIVO(veiculo,condutor, dtinicio, dtfim, lat, long) values
(2,2,'2021-11-29 13:13:13','2021-11-29 18:14:44',72.99,8.62),
(1,1,'2021-11-29 11:11:11','2021-11-29 15:12:15',234.12,126.1);

insert into VIAGEM(idsistema, hinicio, hfim, dtviagem, valestimado, valfinal, latinicio,
longinicio, latfim, longfim, classificação, veiculo, condutor, dtinicio) values
(1,'11:11:11','12:12:12','2021-11-29',124.12 , NULL,123.12 ,56.12, NULL,NULL,1,1,1,'2021-11-29 11:11:11'),
(2,'12:12:12','13:13:13','2021-12-29',253.12 ,253.12 ,35.12,54.12,456.12,156.12,2,2,2,'2021-11-29 13:13:13'),
(3,'13:13:13','14:14:14','2021-11-29',743.12 ,743.12 ,560.12,755.12,93.12,256.12,2,1,1,'2021-11-29 13:13:13'),
(4,'14:14:14','15:15:15','2021-11-29',359.12 ,156.12 ,76.12,45.12,12.12,264.12,1,2,2,'2021-11-29 11:11:11'),
(5,'15:15:15','16:16:16','2020-11-29',16.12 ,111.12 ,99.12,199.12,83.12,56.12,1,2,2,'2021-11-29 11:11:11');

insert into CLIENTEVIAGEM(idpessoa, viagem) values 
(2,1),
(2,2),
(3,3),
(4,4),
(3,5);