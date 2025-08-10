
public class Queries {

        String t1 = "select distinct count(idpessoa) as n_veiculos, PROPRIETARIO.idpessoa from VEICULO \n" +
                        "join PROPRIETARIO on VEICULO.proprietario = PROPRIETARIO.idpessoa GROUP BY idpessoa";

        String t2 = "select distinct idpessoa from PROPRIETARIO";

        String t3 = "select distinct id, matricula, tipo, modelo, marca, ano, proprietario, count(idsistema) as viagens,latinicio,longinicio,latfim,longfim\n" +
                "from VEICULO join\n" +
                "VIAGEM on (veiculo = id)\n" +
                "where (date_part('year', current_date) - ano >= 5)\n" +
                "group by id, matricula, tipo, modelo, marca, ano, proprietario;";

        String check_veiculo_old ="SELECT EXISTS (SELECT FROM pg_catalog.pg_tables WHERE( schemaname  = 'public'\n"+
                " AND tablename  = 'veiculo_old'));";

        String create_veiculo_old = "create table VEICULO_OLD(\n" +
                "id integer not null unique,\n" +
                "matricula varchar(10) not null \n" +
                "check ( matricula similar to '[A-Za-z][A-Za-z][0-9][0-9][A-Za-z][A-Za-z]' or\n" +
                "matricula similar to '[0-9][0-9][A-Za-z][A-Za-z][0-9][0-9]'),\n" +
                "tipo integer not null,\n" +
                "modelo varchar(10) not null,\n" +
                "marca varchar(10) not null,\n" +
                "ano integer not null,\n" +
                "proprietario integer not null,\n" +
                "viagens integer not null,\n" +
                "totalkm integer not null,\n" +
                "foreign key (proprietario) references PROPRIETARIO(idpessoa) on delete cascade on update cascade,\n" +
                "foreign key (tipo) references TIPOVEICULO(tipo) on delete cascade on update cascade\n" +
                ");";
}