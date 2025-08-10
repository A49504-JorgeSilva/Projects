
public class Update {

    String delete_veiculos(Integer proprietario) {
        return "DELETE FROM VEICULO WHERE (id NOT IN (select id FROM (select distinct id  from VEICULO  where(proprietario = " + proprietario +
                " ) order by id desc limit 20) as TOP20) and proprietario = " + proprietario +
                ")";
    }

    String delete_condutor(Integer proprietario) {
        return "delete from CONDUTOR where(idpessoa in (select distinct PROPRIETARIO.idpessoa from PROPRIETARIO) and idpessoa = " + proprietario +
                ")";
    }

    String transfer_values_veiculos(Integer id, String matricula, Integer tipo, String modelo, String marca, Integer ano,
                                    Integer proprietario, Integer viagens, Double totalkm){
        return "insert into VEICULO_OLD (id, matricula, tipo, modelo, marca, ano, proprietario, viagens, totalkm) \n"+
                "values ("+id+","+matricula+","+tipo+","+modelo+","+marca+","+ano+","+proprietario+","+viagens+","+totalkm+");\n" +
                "delete from VEICULO where (date_part('year', current_date) - ano >= 5 AND id = "+id+");";
    }


}