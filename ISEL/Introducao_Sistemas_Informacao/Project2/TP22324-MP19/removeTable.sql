begin try
	begin transaction
		print 'Drop Tables'
		--Drop tables if exist
		drop table if exists DISPOSITIVO;
		drop table if exists BICICLETA;
		drop table if exists CLASSICA;
		drop table if exists ELETRICA;
		drop table if exists PESSOA;
		drop table if exists LOJA;
		drop table if exists RESERVA;
		drop table if exists CLIENTERESERVA;
		drop table if exists TELEFONELOJA;
	
	commit transaction
end try

begin catch
	select 'Entrei no catch'
	rollback
	-- Generates an error message and initiates error
	-- Processing for the session 
	raiserror('Erro',16,1)
end catch
		