import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class Model {

    RT rt = new RT();
    static Scanner in = new Scanner(System.in);


    public final void CheckRestrictions() {
        rt.rt1();
        rt.rt2();
        rt.rt4();
        System.out.println("DATABASE CHECK UP DONE");
        System.out.println("STATUS CODE: 200 OK");
        System.out.println("STARTING PROGRAM");
    }
    public static void newCondutor() {

        //query que irá recber a informação vinda do utizador que irá fazer a inserção do condutor
        final String INSERT_CONDUTOR_CMD = "insert into condutor (idpessoa,ncconducao,dtnascimento) values (?,?,?)";

        //seleciona o idpessoa,ncconducao e dtnascimento para ir fazer verificações
        String SELECT_CONDUTOR_CMD = "select idpessoa,ncconducao,dtnascimento from CONDUTOR;";
        String SELECT_PESSOA_CMD = "select id from PESSOA where(atrdisc = 'CL');";//nao pode ser condutor nem proprietario

        //cria as listas para guardar os valores
        List<Integer> id_bd = new ArrayList<Integer>();
        List<Integer> idpessoa_bd = new ArrayList<Integer>();
        List<String> ncconducao_bd = new ArrayList<String>();
        List<Date> dtnascimento_bd = new ArrayList<Date>();


        try {
            //faz conexão à base de dados
            Connection con = connectDB();

            //Cria um statement
            Statement sta = con.createStatement();

            //executa a query para termos a lista de dos condutores que existem
            ResultSet rs = sta.executeQuery(SELECT_CONDUTOR_CMD);

            //adiciona os resultados da query às suas respetivas listas
            while(rs.next()){
                idpessoa_bd.add(rs.getInt("idpessoa"));
                ncconducao_bd.add(rs.getString("ncconducao"));
                dtnascimento_bd.add(rs.getDate("dtnascimento"));
            }

            //adiciona a lista os ids validos para ser possivel validar o id introduzido
            rs = sta.executeQuery(SELECT_PESSOA_CMD);
            System.out.println("Register New Driver");
            System.out.println("Possible ids:");
            while(rs.next()){
                id_bd.add(rs.getInt("id"));
                System.out.println("->"+rs.getInt("id"));
            }
            sta.close();
            //desliga a conexão com o objetivo de não ter a conexão muito tempo ligada
            con.close();

            //pergunta ao utilizador os dados para criar um novo condutor
            System.out.println("New driver ID");
            System.out.print(">");
            Integer id = in.nextInt();
            //se id recebido estiver na lista de condutores ou nao estiver na lista de ids de pessoas
            // fica num loop até o utilizador inserir um id valido
            while(!id_bd.contains(id) || idpessoa_bd.contains(id)){
                System.out.println("id not valid");
                System.out.println("New driver ID");
                System.out.print(">");
                id = in.nextInt();
            }

            System.out.println("Driver's license number ([A-Z][A-Z]-[1-9][1-9][1-9][1-9][1-9][1-9][1-9])");
            System.out.print(">");
            String license = in.nextLine();
            while(Objects.equals(license, "")) license = in.nextLine();

            //se a ccondução recebida estiver na lista fica num loop até o utilizador inserir um ccondução valida
            while(ncconducao_bd.contains(license)){
                System.out.println("there's a driver with that license number already");
                System.out.println("New driver's license");
                System.out.print(">");
                license = in.nextLine();
            }

            System.out.println("Date of birth (yyyy-mm-dd)");
            System.out.print(">");
            Date date = java.sql.Date.valueOf(in.next());

            //faz conexão à base de dados
            con = connectDB();

            //prepara a query para ser executada, espera que todos os ? sejam substituidos para depois executar
            PreparedStatement pstmt = con.prepareStatement(INSERT_CONDUTOR_CMD);

            //pega nas informações recebidas e preenche na query onde tem ?
            pstmt.setInt(1,id);
            pstmt.setString(2,license);
            pstmt.setDate(3,date);

            //executa a query já com os ? substitudos, adicionando um novo condutor à bd
            pstmt.executeQuery();
            pstmt.close();
            con.close();

        } catch (SQLException err) {
            //Nothing to do. The option was not a valid one. Read another.
        }
    }
    public static void DesactivateVehicle() {

        //seleciona as matriculas dos veiculos ativos para ir fazer verificações
        String SELECT_ALL_VEHICLES_CMD = "select matricula from VEICULO;";

        //cria as lista para guardar os valores das matriculas
        List<String> matricula_bd = new ArrayList<String>();

        try{
            //faz conexão à base de dados
            Connection con = connectDB();

            //cria um statmente
            Statement sta = con.createStatement();

            //executa a query para nos dar uma lista com as matrículas de veiculos ativos
            ResultSet rs = sta.executeQuery(SELECT_ALL_VEHICLES_CMD);

            //adiciona os resultados da query às suas respetivas listas
            while(rs.next()){
                matricula_bd.add(rs.getString("matricula"));
            }

            sta.close();
            //desliga a conexão com o objetivo de não ter a conexão muito tempo ligada
            con.close();

            //pergunta ao utilizador qual a matricula do carro a desativar
            System.out.println("Insert matricula");
            System.out.print(">");
            String m = in.nextLine();

            //se a matricula recebida nao estiver na lista entra num loop ate ser introduzida uma matricula valida
            while(!matricula_bd.contains(m)){
                System.out.println("Matricula not valid");
                System.out.println("Insert matricula");
                System.out.print(">");
                m = in.nextLine();
            }

            //seleciona os dados do veiculo com a matricula introduzida
            String SELECT_VEHICLE_CMD = "select distinct id, matricula, tipo, modelo, marca, ano, proprietario, " +
                    "count(idsistema) as viagens, latinicio, latfim, longinicio, longfim " +
                    "from VEICULO join " +
                    "VIAGEM on (veiculo = id) " +
                    "where ('"+m+"' = matricula) " +
                    "group by id,matricula,tipo,modelo,marca,ano,proprietario,latinicio,latfim,longinicio,longfim;";

            //faz conexão à base de dados
            con = connectDB();

            //cria um statment
            Statement sta2 = con.createStatement();

            //executa a query para nos dar os dados do veiculo
            ResultSet rs2 = sta2.executeQuery(SELECT_VEHICLE_CMD);


            //adiciona os resultados da query a diferentes variaveis para serem depois introduzidos na nova tabela
            rs2.next();
            int id = rs2.getInt("id");
            int tipo = rs2.getInt("tipo");
            String modelo = rs2.getString("modelo");
            String marca = rs2.getString("marca");
            int ano = rs2.getInt("ano");
            int proprietario = rs2.getInt("proprietario");
            int n_viagens = 0;
            double totalDistance = 0.0;
            while (rs2.next()) {
                totalDistance += DistanceCalculator.distance(rs2.getDouble("latinicio"),
                        rs2.getDouble("longinicio"), rs2.getDouble("latfim"),
                        rs2.getDouble("longfim"), "K");
                n_viagens++;
            }

            //query que irá recber a informação vinda do utizador que irá fazer a inserção do veiculo na nova tabela
            final String DESACTIVATE_VEHICLE_CMD = "insert into VEICULO_OLD (id, matricula, tipo, modelo, marca, ano," +
                    "proprietario,viagens,totalkm) values (?,?,?,?,?,?,?,?,?);\n delete from VEICULO where(id="+id+");";

            //prepara a query para ser executada, espera que todos os ? sejam substituidos para depois executar
            PreparedStatement pstmt = con.prepareStatement(DESACTIVATE_VEHICLE_CMD);

            //pega nas informações recebidas e preenche na query onde tem ?
            pstmt.setInt(1,id);
            pstmt.setString(2,m);
            pstmt.setInt(3,tipo);
            pstmt.setString(4,modelo);
            pstmt.setString(5,marca);
            pstmt.setInt(6,ano);
            pstmt.setInt(7,proprietario);
            pstmt.setInt(8,n_viagens);
            pstmt.setDouble(9,totalDistance);

            //executa a query já com os ? substitudos, adicionando o veiculo a tabela veiculo_old
            pstmt.executeQuery();
            sta2.close();
            pstmt.close();
            con.close();

        }catch(SQLException err){
            //Nothing to do. The option was not a valid one. Read another.
        }
    }
    public static void VEHICLE_STATS(){

        //seleciona o id o modelo e a marca dos veiculos ativos para ir fazer verificações
        String SELECT_VEHICLES_CMD = "select id,modelo,marca from VEICULO;";

        //cria as listas para guardar os valores
        List<Integer> id_bd = new ArrayList<Integer>();
        List<String> modelo_bd = new ArrayList<String>();
        List<String> marca_bd = new ArrayList<String>();

        try{
            //faz conexão à base de dados
            Connection con = connectDB();

            //cria um statment
            Statement sta = con.createStatement();

            //executa a query para nos dar uma lista com os id's de activos ativos
            ResultSet rs = sta.executeQuery(SELECT_VEHICLES_CMD);

            //adiciona os resultados da query às suas respetivas listas
            System.out.println("List of Vehicles:");
            while(rs.next()){
                id_bd.add(rs.getInt("id"));
                modelo_bd.add(rs.getString("modelo"));
                marca_bd.add(rs.getString("marca"));
                System.out.println("->"+rs.getInt("id")+":"+rs.getString("modelo")+" "+
                        rs.getString("marca"));
            }

            sta.close();
            //desliga a conexão com o objetivo de não ter a conexão muito tempo ligada
            con.close();

            //pergunta ao utilizador qual o veiculo que será selecionado
            System.out.println("Choose a Vehicle");
            System.out.print(">");
            Integer id = in.nextInt();

            // se id recebido nao estiver na lista de veiculos fica num loop até o utilizador inserir um id valido
            while(!id_bd.contains(id)){
                System.out.println("Vehicle not valid");
                System.out.println("Choose a Vehicle");
                System.out.print(">");
                id = in.nextInt();
            }

            //seleciona os dados do veiculo selecionado
            final String SELECT_VEHICLE_STATS = "select hinicio,hfim,latinicio,longinicio,latfim,longfim," +
                    "valfinal from VEICULO join VIAGEM on (veiculo = id) where (id = "+ id +")";

            //faz conexão à base de dados
            con = connectDB();

            //cria um statment
            sta = con.createStatement();

            //executa a query para nos dar uma lista com os id's de activos ativos
            rs = sta.executeQuery(SELECT_VEHICLE_STATS);

            //adiciona os resultados da query a diferentes variaveis para serem depois apresentados ao utilizador
            double valTotal = 0.0;
            double totalDistance = 0.0;
            ArrayList<String> startTimeList = new ArrayList<String>();
            ArrayList<String> endTimeList = new ArrayList<String>();
            while(rs.next()){
                startTimeList.add(""+rs.getTime("hinicio"));
                endTimeList.add(""+rs.getTime("hfim"));
                valTotal += rs.getDouble("valfinal");
                totalDistance += DistanceCalculator.distance(rs.getDouble("latinicio"),
                        rs.getDouble("longinicio"), rs.getDouble("latfim"),
                        rs.getDouble("longfim"), "K");
            }

            //Apresentação dos resultados ao utilizador
            System.out.println("Stats of Vehicle " + id + ":");
            totalTime(startTimeList,endTimeList);
            System.out.println("Total KMs travelled: " + totalDistance);
            System.out.println("Total cost: " + valTotal);
            sta.close();
            con.close();

        }catch(SQLException err){
            //Nothing to do. The option was not a valid one. Read another.
        }

    }
    /**
     * Função auxiliar que calcula o tempo total das viagens de cada veiculo
     * sendo o resultado depois apresentado ao utilizador com a ajuda da função format
     * */
    public static void totalTime(ArrayList<String> a,ArrayList<String> b){
        long tm = 0;
        for (String tmp : b){
            String[] arr = tmp.split(":");
            tm += Integer.parseInt(arr[2]);
            tm += 60 * Integer.parseInt(arr[1]);
            tm += 3600 * Integer.parseInt(arr[0]);
        }
        for (String tmp : a){
            String[] arr = tmp.split(":");
            tm -= Integer.parseInt(arr[2]);
            tm -= 60 * Integer.parseInt(arr[1]);
            tm -= 3600 * Integer.parseInt(arr[0]);
        }
        long hh = tm / 3600;
        tm %= 3600;
        long mm = tm / 60;
        tm %= 60;
        long ss = tm;
        System.out.println("Total hours travelled: "+format(hh)+":"+format(mm)+":"+format(ss));
    }
    private static String format(long s){
        if (s < 10) return "0" + s;
        else return "" + s;
    }
    public static void MOST_TRAVELS(){

        //seleciona os anos em que houveram viagens para ir fazer verificações
        String SELECT_TRAVELS_CMD = "select distinct DATE_PART('year',dtviagem) as years from VIAGEM;";

        //cria as listas para guardar os valores
        List<Integer> years_bd = new ArrayList<Integer>();
        try{
            //faz conexão à base de dados
            Connection con = connectDB();

            //cria um statment
            Statement sta = con.createStatement();

            //executa a query para nos dar uma lista com os anos em que houve viagens
            ResultSet rs = sta.executeQuery(SELECT_TRAVELS_CMD);

            //adiciona os resultados da query às suas respetivas listas
            System.out.println("List of valid years:");
            while(rs.next()){
                years_bd.add(rs.getInt("years"));
                System.out.println("->"+rs.getInt("years"));
            }

            sta.close();
            con.close();

            //pergunta ao utilizador qual o ano que será selecionado
            System.out.println("Choose a Year");
            System.out.print(">");
            int year = in.nextInt();

            //se o ano recebido nao estiver na lista de anos validos fica num loop até o ser inserirido um ano valido
            while(!years_bd.contains(year)){
                System.out.println("Please choose a valid year");
                System.out.println("Choose a Year");
                System.out.print(">");
                year = in.nextInt();
            }

            //seleciona os resultados finais para serem depois apresentados aos utilizadores
            final String SELECT_MOST_TRAVELS_CMD = "select id, nif, nproprio, apelido \n" +
                    "from PESSOA\n" +
                    "join (select idpessoa, count(idpessoa) total \n" +
                    "from CLIENTEVIAGEM\n" +
                    "join VIAGEM on (viagem = idsistema)\n" +
                    "where(DATE_PART('year', dtviagem)='"+ year +"')\n" +
                    "group by idpessoa) as totalviagens on (totalviagens.idpessoa = id)\n" +
                    "where (select max(total) \n" +
                    "from (\n" +
                    "select idpessoa, count(idpessoa) total \n" +
                    "from CLIENTEVIAGEM\n" +
                    "join VIAGEM on (viagem = idsistema)\n" +
                    "where(DATE_PART('year', dtviagem)='"+ year +"')\n" +
                    "group by idpessoa) as maxtotalviagens) = totalviagens.total;";

            //faz conexão à base de dados
            con = connectDB();

            //cria um statement
            sta = con.createStatement();

            //executa a query para nos dar os dados do condutor com mais viagens nesse ano
            rs = sta.executeQuery(SELECT_MOST_TRAVELS_CMD);

            //Apresenta os resultados ao utilizador
            System.out.println("Most travels in "+year+":");
            while(rs.next()){
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("nproprio")+" "+rs.getString("apelido"));
                System.out.println("NIF: " + rs.getString("nif"));
                System.out.println();
            }

            sta.close();
            con.close();

        }catch(SQLException err){
            //Nothing to do. The option was not a valid one. Read another.
        }
    }
    public static void NEVER_TRAVELED(){
        try{
            //faz conexão à base de dados
            Connection con = connectDB();

            //cria um statment
            Statement sta = con.createStatement();

            //Seleciona o id, nif e nome dos condutores que nunca efetuaram nenhuma viagem
            final String SELECT_NEVER_TRAVELED_CMD= "select distinct id, nif, nproprio, apelido from VIAGEM \n" +
                    "join PESSOA on (atrdisc = 'C') \n" +
                    "where id not in (select condutor from VIAGEM);";

            //executa a query para nos dar uma tabela com os dados dos condutores que nunca efetuaram viagens
            ResultSet rs = sta.executeQuery(SELECT_NEVER_TRAVELED_CMD);
            System.out.println("Never Traveled:");
            while(rs.next()){
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("nproprio")+" "+rs.getString("apelido"));
                System.out.println("NIF: " + rs.getString("nif"));
                System.out.println();
            }

            sta.close();
            con.close();

        }catch(SQLException err){
            //Nothing to do. The option was not a valid one. Read another.
        }
    }
    public static void NUM_TRAVELS_PER_YEAR(){

        //seleciona o nif de todos os proprietarios
        String SELECT_PROP_CMD = "select nif from PESSOA where(atrdisc='P');";


        //cria as listas para guardar os valores
        List<Integer> nifs_bd = new ArrayList<Integer>();
        List<Integer> years_bd = new ArrayList<Integer>();
        try {
            //faz conexão à base de dados
            Connection con = connectDB();

            //cria um statmente
            Statement sta = con.createStatement();

            //executa a query para nos dar uma tabela com os nifs dos proprietarios
            ResultSet rs = sta.executeQuery(SELECT_PROP_CMD);

            //adiciona os nifs validos a uma lista
            System.out.println("List of valid NIFs:");
            while(rs.next()){
                nifs_bd.add(rs.getInt("nif"));
                System.out.println("->"+rs.getInt("nif"));
            }

            sta.close();
            con.close();

            //pergunta ao utilizador qual o nif que será selecionado
            System.out.println("Choose a NIF");
            System.out.print(">");
            int nif = in.nextInt();

            // se nif recebido nao estiver na lista de veiculos fica num loop até o utilizador inserir um nif valido
            while(!nifs_bd.contains(nif)){
                System.out.println("Please choose a valid NIF");
                System.out.println("Choose a NIF");
                System.out.print(">");
                nif = in.nextInt();
            }

            //seleciona os anos em que os veiculos do proprietario efetuaram viagens
            String SELECT_YEAR_CMD = "select distinct DATE_PART('year',dtviagem) as years from VIAGEM \n" +
                    "join VEICULO on(VEICULO.id = veiculo)\n" +
                    "join PESSOA on(proprietario = PESSOA.id)\n" +
                    "where(nif = '"+nif+"')";

            //faz conexão à base de dados
            con = connectDB();

            //cria um statment
            sta = con.createStatement();

            //executa a query para nos dar uma tabela com os anos em que os veiculos do proprietario efetuaram viagens
            rs = sta.executeQuery(SELECT_YEAR_CMD);

            //adiciona os anos em que os veiculos do proprietario efetuaram viagens a uma lista
            System.out.println("List of valid years:");
            while(rs.next()){
                years_bd.add(rs.getInt("years"));
                System.out.println("->"+rs.getInt("years"));
            }

            sta.close();
            con.close();

            //pergunta ao utilizador qual o ano que será selecionado
            System.out.println("Choose a Year");
            System.out.print(">");
            int year = in.nextInt();

            //se o ano recebido nao estiver na lista de anos validos fica num loop até o ser inserirido um ano valido
            while(!years_bd.contains(year)){
                System.out.println("Please choose a valid year");
                System.out.println("Choose a Year");
                System.out.print(">");
                year = in.nextInt();
            }

            //seleciona os resultados finais para serem depois apresentados aos utilizadores
            final String SELECT_TRAVELS_BY_YEAR_CMD = "select count(idsistema) as num_viagens from VIAGEM \n" +
                    "join VEICULO on (veiculo = VEICULO.id)" +
                    "join PESSOA on (proprietario = PESSOA.id)" +
                    "where(nif = '"+nif+"' and DATE_PART('year', dtviagem)='"+year+"')";

            //faz conexão à base de dados
            con = connectDB();

            //cria um statmente
            sta = con.createStatement();

            //executa a query para nos dar os resultados finais
            rs = sta.executeQuery(SELECT_TRAVELS_BY_YEAR_CMD);

            rs.next();

            //Apresenta os resultados ao utilizador
            System.out.println("No ano "+year+" foram feitas "+rs.getInt("num_viagens")+" viagens pelos " +
                    "veículos do proprietario com NIF: "+nif);

            sta.close();
            con.close();

        } catch(SQLException err){
            //Nothing to do. The option was not a valid one. Read another.
        }
    }
    public static void BEST_PAID_DRIVER() {

        //seleciona os ano em que houve viagens para serem usados depois na validação dos dados introduzidos
        String SELECT_YEAR_CMD = "select distinct DATE_PART('year',dtviagem) as years from VIAGEM;";

        //cria as listas para guardar os valores
        List<Integer> years_bd = new ArrayList<Integer>();
        try {
            //faz conexão à base de dados
            Connection con = connectDB();

            //cria um statmente
            Statement sta = con.createStatement();

            //executa a query para nos dar uma lista com os anos em que foram efetuadas viagens
            ResultSet rs = sta.executeQuery(SELECT_YEAR_CMD);
            System.out.println("List of valid years:");
            while (rs.next()) {
                years_bd.add(rs.getInt("years"));
                System.out.println("->" + rs.getInt("years"));
            }

            sta.close();
            con.close();

            //pergunta ao utilizador qual o ano que será selecionado
            System.out.println("Choose a Year");
            System.out.print(">");
            int year = in.nextInt();

            //se o ano recebido nao estiver na lista de anos validos fica num loop até o ser inserirido um ano valido
            while (!years_bd.contains(year)) {
                System.out.println("Please choose a valid year");
                System.out.println("Choose a Year");
                System.out.print(">");
                year = in.nextInt();
            }

            //seleciona os resultados finais para serem depois apresentados aos utilizadores
            final String SELECT_BEST_PAID_DRIVER_CMD = "select nproprio, apelido, noident, morada \n" +
                    "from PESSOA\n" +
                    "join (select idpessoa, sum(valfinal) total \n" +
                    "from VIAGEM \n" +
                    "join CONDUTOR on (condutor = idpessoa)\n" +
                    "where(DATE_PART('year', dtviagem)='"+year+"')\n" +
                    "group by idpessoa) as ganhos on ganhos.idpessoa = id\n" +
                    "where (select max(total) \n" +
                    "from (select idpessoa, sum(valfinal) total \n" +
                    "from VIAGEM \n" +
                    "join CONDUTOR on (condutor = idpessoa)\n" +
                    "where (DATE_PART('year', dtviagem)='"+year+"')\n" +
                    "group by idpessoa) as maxganhos) = ganhos.total;";

            //faz conexão à base de dados
            con = connectDB();

            //cria um statmente
            sta = con.createStatement();

            //executa a query para nos dar os dados do condutor que obteve o melhor pagamento no ano introduzido pelo utilizador
            rs = sta.executeQuery(SELECT_BEST_PAID_DRIVER_CMD);

            rs.next();

            //Apresenta os resultados ao utilizador
            System.out.println("O Condutor com melhor pagamento no ano "+year+" foi:");
            System.out.println("Nome: "+rs.getString("nproprio")+" "+rs.getString("apelido"));
            System.out.println("Número de Identificação: " + rs.getString("noident"));
            System.out.println("Morada: " + rs.getString("morada"));

            sta.close();
            con.close();

        }catch(SQLException err){
            //Nothing to do. The option was not a valid one. Read another.
        }
    }
    /**
     * metodo que faz a conexão à base de dados, sendo desnessário repetiçao de
     * código
     **/
    private static Connection connectDB() {
        String jdbcURL = "jdbc:postgresql://10.62.73.73:5432/";
        String username = "mp25";
        String password = "mp25";

        try {
            Connection con = DriverManager.getConnection(jdbcURL, username, password);
            return con;
        } catch (SQLException err) {
        }
        return null;
    }
}
