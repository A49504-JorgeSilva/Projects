--2a
SELECT nome, morada, telefone
FROM PESSOA;

--2b
SELECT nome, morada, telefone
FROM PESSOA
	WHERE atrdisc = 'G';

--2c
SELECT nome, morada, telefone
FROM PESSOA
EXCEPT
	SELECT cliente
	FROM CLIENTERESERVA;

--2d
SELECT marca, modelo, estado
FROM BICICLETA
EXCEPT
(
  SELECT bicicleta
  FROM RESERVA
  JOIN BICICLETA
  ON RESERVA.bicicleta = BICICLETA.id
  JOIN ELETRICA
  ON BICICLETA.id = ELETRICA.id
);

--2e
SELECT bicicleta.noserie, bicicleta.latitude, bicicleta.longitude
FROM BICICLETA
JOIN DISPOSITIVO
ON bicicleta.id = dispositivo.bicicleta
	WHERE bicicleta.estado = 'em manutenção';
	
--2f
SELECT PESSOA.nome, PESSOA.morada, PESSOA.telefone, COUNT(RESERVA.noreserva) AS num_reservas
FROM PESSOA
JOIN CLIENTERESERVA
ON PESSOA.atrdisc = CLIENTERESERVA.cliente
JOIN RESERVA
ON CLIENTERESERVA.reserva = RESERVA.noreserva
JOIN ELETRICA
ON RESERVA.bicicleta = ELETRICA.id
	WHERE ELETRICA.velocidade IS NOT NULL
	GROUP BY PESSOA.nome, PESSOA.morada, PESSOA.telefone;

--2g
SELECT cliente, PESSOA.nome, PESSOA.morada, PESSOA.telefone
FROM CLIENTERESERVA
JOIN RESERVA
ON CLIENTERESERVA.reserva = RESERVA.noreserva
JOIN PESSOA
ON CLIENTERESERVA.cliente = PESSOA.atrdisc
	WHERE RESERVA.valor > 100;

--2h
SELECT LOJA.email, LOJA.endereco, LOJA.localidade, LOJA.numero
FROM LOJA
JOIN TELEFONELOJA
ON LOJA.id = TELEFONELOJA.loja;

--2i
SELECT
    noreserva,
    loja,
    dtinicio,
    dtfim,
    valor
FROM
    (
        SELECT
            *
        FROM
            PESSOA
        INNER JOIN
            CLIENTERESERVA
        ON
            PESSOA.id = CLIENTERESERVA.cliente
        INNER JOIN
            RESERVA
        ON
            CLIENTERESERVA.reserva = RESERVA.noreserva
        WHERE
            PESSOA.nome = 'José Manuel'
    ) AS RESERVAS;

--2j
SELECT
    PESSOA.nome,
    PESSOA.morada,
    PESSOA.telefone,
    PESSOA.nacionalidade,
    COUNT(RESERVA.noreserva) AS NUM_RESERVAS
FROM
    (
        SELECT
            *
        FROM
            PESSOA
        INNER JOIN
            CLIENTERESERVA
        ON
            PESSOA.id = CLIENTERESERVA.cliente
        INNER JOIN
            RESERVA
        ON
            CLIENTERESERVA.reserva = RESERVA.noreserva
    ) AS RESERVAS
WHERE
    EXTRACT(year FROM RESERVAS.dtinicio) = 2023
GROUP BY
    PESSOA.nome,
    PESSOA.morada,
    PESSOA.telefone,
    PESSOA.nacionalidade;
   
--2k
SELECT
    nacionalidade,
    COUNT(PESSOA.id) AS NUM_CLIENTES
FROM
    PESSOA
GROUP BY
    PESSOA.nacionalidade
    
    

    
--3b
SELECT
  p.nome
FROM
  PESSOA p
JOIN CLIENTERESERVA cr ON p.id = cr.cliente
JOIN RESERVA r ON cr.reserva = r.noreserva
JOIN LOJA l ON r.loja = l.codigo
WHERE
  l.localidade = 'Lisboa';

--3c
SELECT
  d.noserie,
  d.bateria
FROM
  DISPOSITIVO d
WHERE
  d.bateria > 50
ORDER BY
  d.bateria;

--3d
SELECT
  b.marca,
  b.modelo
FROM
  BICICLETA b
WHERE
  b.autonomia = (
    SELECT
      max(autonomia)
    FROM
      BICICLETA
  );

--3e
SELECT
  l.codigo,
  COUNT(*) AS num_reservas
FROM
  RESERVA r
JOIN LOJA l ON r.loja = l.codigo
GROUP BY
  l.codigo;

--3f
SELECT
  l.codigo,
  l.email,
  COUNT(*) AS num_reservas
FROM
  RESERVA r
JOIN LOJA l ON r.loja = l.codigo
WHERE
  CURRENT_DATE >= r.dtinicio
GROUP BY
  l.codigo
HAVING
  COUNT(*) > 5
ORDER BY
  num_reservas DESC;

--3g
SELECT
  p.nome
FROM
  CLIENTERESERVA cr
JOIN RESERVA r ON cr.reserva = r.noreserva
JOIN BICICLETA b ON r.bicicleta = b.id
JOIN PESSOA p ON cr.cliente = p.id
WHERE
  b.estado = 'em manutenção'
  AND r.dtinicio > '2022-01-01'
  AND r.dtinicio < '2023-01-01';
 
--3h
SELECT
  p.nome,
  p.morada,
  p.telefone
FROM
  PESSOA p
JOIN CLIENTERESERVA cr ON p.id = cr.cliente
JOIN RESERVA r ON cr.reserva = r.noreserva
JOIN LOJA l ON r.loja = l.codigo
WHERE
  p.atrdisc = 'G';
 
--3i
SELECT
  p.nome
FROM
  PESSOA p
JOIN CLIENTERESERVA cr ON p.id = cr.cliente
JOIN RESERVA r ON cr.reserva = r.noreserva
JOIN LOJA l ON r.loja = l.codigo
JOIN PESSOA g ON l.gestor = g.id
WHERE
  g.nome = 'João';
 
--3j
CREATE VIEW LISTAJOAOFILIPE AS
SELECT
  p.nome AS cliente,
  r.bicicleta AS bicicleta,
  r.dtinicio AS data_inicio,
  r.dtfim AS data_fim
FROM
  PESSOA p
JOIN CLIENTERESERVA cr ON p.id = cr.cliente
JOIN RESERVA r ON cr.reserva = r.noreserva
JOIN LOJA l ON r.loja = l.codigo
WHERE
  l.gestor IN (
    SELECT
      id
    FROM
      PESSOA
    WHERE
      nome = 'João Filipe'
  );
 
--3k
CREATE VIEW BICICLETASEMNUMEROS AS
SELECT
  b.tipo AS tipo,
  b.estado AS estado,
  COUNT(*) AS n_total
FROM
  BICICLETA b
GROUP BY
  b.tipo,
  b.estado;
    

