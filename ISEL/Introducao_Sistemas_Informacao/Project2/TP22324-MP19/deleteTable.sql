begin try
	begin transaction
	print 'Delete tables';
	
	delete from DISPOSITIVO,
	delete from BICICLETA,
	delete from CLASSICA,
	delete from ELETRICA,
	delete from PESSOA,
	delete from LOJA,
	delete from RESERVA,
	delete from CLIENTERESERVA,
	delete from LOJATELEFONE;
	
	commit transaction; 
end try  
begin catch 
	select 'Entrei no catch'  
	rollback
	-- Generates an error message and initiates error 
	-- processing for the session.
	raiserror('Erro',16,1)
end catch