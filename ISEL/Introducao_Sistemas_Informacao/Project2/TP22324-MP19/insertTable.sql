begin try
	begin transaction
	print 'Fill Tables'
	
	
	insert into DISPOSITIVO(noserie, latitute, longitude, bateria) values
		(1, 43.61, -116.2, 12),
		(2, 1.160, -76.618, 5),
		(3, -4.055, -44.172, 4),
		(4, 31.49, 120.31, 44),
		(5, 36.85, 114.50, 20);
	
	insert into BICICLETA(id, peso, raio, modelo, marca, mudanca, estado, atrdisc, dispositivo) values
		(1, 8759.95, 20, 'Eclipse', 'Mitsubishi', '6', 'em manutencao', 'E', 1),
		(2, 7332.11, 21, 'Prius', 'Toyota', '18', 'em manutencao', 'C', 2),
		(3, 9807.09, 16, 'G-Series 1500', 'Chevrolet', 'NULL', 'em manutencao', 'C', 3),
		(4, 770.87, 17, 'E-Series', 'Ford', '24', 'ocupado', 'C', 4),
		(5, 5965.39, 18, 'Millenia', 'Mazda', '1', 'em manutencao', 'E', 5);
	
	insert into CLASSICA(bicicleta, nomudanca) values
		(1, 2),
		(2, 2),
		(3, 1),
		(4, 2),
		(5, 1);
	
	insert into ELETRICA(bicicleta, autonomia, velocidade) values
		(1, 13, 1),
		(2, 3, 0),
		(3, 30, 7),(4, 4, '2023-05-28 03:31:52', '2024-03-17 15:59:04', 9439.44, 4)
		(4, 16, 21),
		(5, 10, 41);
	
	insert into PESSOA(id, nome, morada, email, telefone, noident, nacionalidade, atrdisc) values
		(1, 'Dorisa Maddie', 'Suite 75', 'dmaddie0@bigcartel.com', '+351904797968', '783079765232', 'France', 'G'),
		(2, 'Keri Ceeley', 'Room 632', 'kceeley1@squarespace.com', '+351975173947', '785406844789', 'United States', 'C'),
		(3, 'Tarra Chenery', 'PO Box 17306', 'tchenery2@businesswire.com', '+351947742588', '903728926788', 'Portugal', 'C'),
		(4, 'Jacynth Shepcutt', 'PO Box 80001', 'jshepcutt3@msn.com', '+351951681980', '934156011928', 'Portugal', 'G'),
		(5, 'Roselle Prosek', 'Suite 3', 'rprosek4@indiegogo.com', '+351960165892', '240547613042', 'United States', 'G');
	
	insert into LOJA(codigo, email, endereco, localidade, gestor) values
		(1, 'tbinning0@dailymotion.com', 'Room 740', 'Tengah', 3),
		(2, 'hkick1@sitemeter.com', 'Room 1129', 'Puerto Galera', 2),
		(3, 'apittaway2@smh.com.au', 'Apt 1411', 'Franco da Rocha', 9),
		(4, 'jpizzie3@imgur.com', 'Room 475', 'Paripiranga', 6),
		(5, 'apreston4@thetimes.co.uk', '1st Floor', 'CalÃ§ada', 3);
	
	insert into RESERVA(noreserva, loja, dtinicio, dtfim, valor, bicicleta) values
		(1, 1, '2023-06-21 06:36:41', '2024-05-17 12:14:20', 6219.8, 1),
		(2, 2, '2023-01-10 18:28:23', '2024-04-21 17:22:53', 74.51, 2),
		(3, 3, '2023-11-05 19:09:36', '2024-02-28 05:06:03', 762.7, 3),
		(4, 4, '2023-05-28 03:31:52', '2024-03-17 15:59:04', 9439.44, 4),
		(5, 5, '2023-11-25 22:49:05', '2024-02-16 08:20:22', 3086.39, 5);
	
	insert into TELEFONELOJA(loja, numero) values
		(1, '+351956332789'),
		(2, '+351981422904'),
		(3, '+351969351192'),
		(4, '+351966339729'),
		(5, '+351965519999');
	
	commit transaction; 
end try  
begin catch 
	select 'Entrei no catch'  
	rollback
	-- Generates an error message and initiates error 
	-- processing for the session.
	raiserror('Erro',16,1)
end catch
	
	