/*
 * @Author: Matilde Pato (mpato)
 * @Date: 2023-11-27 09:30:00
 * @Last Modified time: 2023-12-06 18:30:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2023-2024
 * 
 * NOTE: 
 * The code is split into classes; please don't add more classes or 
 * change the initial configuration.
 * 1) The Bike class is a class that contains the attributes of the 
 *  BICYCLE table. The same for ElectricBike, ClassicBike and GPSDevice.
 *  All are already implemented! Do not change it!
 * 2) The model class is where all the application's methods should be 
 *  implemented.
 * 3) The restriction class should contain the restrictions on the data 
 *  model. It is only executed when there is a new entry in the tables to 
 *  which it is affected.
 * 4) You must add the IP address, Port number, Database and PWD in line 237
 * 5) The values entered must be separated by a comma with no blank spaces
 * 6) The values entered must follow the order defined in the database to 
 * avoid further validation codes
 * 
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
interface DbWorker
{
    void doWork();
}
class App
{
    private enum Option
    {
        // DO NOT CHANGE ANYTHING!
        Unknown,
        Exit,
        novelBike,
        updateBikeState,
        calculateAverageMetricsForElectricBikes,
        getClientsWithReservations,
        getGPSDevice,
        getStores,
        getManagers,
    }
    private static App __instance = null;
    private String __connectionString;

    private HashMap<Option,DbWorker> __dbMethods;

    private App()
    {
        // DO NOT CHANGE ANYTHING!
        __dbMethods = new HashMap<Option,DbWorker>();
        __dbMethods.put(Option.novelBike, () -> App.this.novelBike());    
        __dbMethods.put(Option.updateBikeState, () -> App.this.updateBikeState()); 
        __dbMethods.put(Option.calculateAverageMetricsForElectricBikes, () -> App.this.calculateAverageMetricsForElectricBikes());  
        __dbMethods.put(Option.getClientsWithReservations, () -> App.this.getClientsWithReservations());
        __dbMethods.put(Option.getGPSDevice, new DbWorker() {public void doWork() {App.this.getGPSDevice();}});
        __dbMethods.put(Option.getStores, new DbWorker() {public void doWork() {App.this.getStores();}});
        __dbMethods.put(Option.getManagers, new DbWorker() {public void doWork() {App.this.getManagers();}});

    }

    public static App getInstance()
    {
        if(__instance == null)
        {
            __instance = new App();
        }
        return __instance;
    }

    private Option DisplayMenu()
    {
        Option option = Option.Unknown;
        try
        {
            // DO NOT CHANGE ANYTHING!
            System.out.println("Bicycle reservation");
            System.out.println();
            System.out.println("1. Exit");
            System.out.println("2. Novel bikes");
            System.out.println("3. Update bike states");
            System.out.println("4. Average metrics for electric bikes");
            System.out.println("5. List of clients with reservations");//2j
            System.out.println("6. List of devices");//2e
            System.out.println("7. List of stores");//3f
            System.out.println("8. List of managers and made reservations");//3h
            System.out.print(">");
            Scanner s = new Scanner(System.in);
            int result = s.nextInt();
            option = Option.values()[result];
        }
        catch(RuntimeException ex)
        {
            //nothing to do.
        }
        return option;

    }
    private static void clearConsole() throws Exception
    {
        for (int y = 0; y < 25; y++) //console is 80 columns and 25 lines
            System.out.println("\n");

    }
    private void Login() throws SQLException
    {
        Connection con = DriverManager.getConnection(getConnectionString());
        if(con != null)
            con.close();
    }

    public void Run() throws Exception
    {
        Login();
        Option userInput;
        do
        {
            clearConsole();
            userInput = DisplayMenu();
            clearConsole();
            try
            {
                __dbMethods.get(userInput).doWork();
                System.in.read();

            }
            catch(NullPointerException ex)
            {
                //Nothing to do. The option was not a valid one. Read another.
            }

        }while(userInput!=Option.Exit);
    }

    public String getConnectionString()
    {
        return __connectionString;
    }
    public void setConnectionString(String s)
    {
        __connectionString = s;
    }

    /**
    To implement from this point forward. Do not need to change the code above.
    -------------------------------------------------------------------------------     
        IMPORTANT:
    --- DO NOT MOVE IN THE CODE ABOVE. JUST HAVE TO IMPLEMENT THE METHODS BELOW ---
    -------------------------------------------------------------------------------
    
    */

    private static final int TAB_SIZE = 24;

    /**
     * Método responsável por imprimir os resultados de um ResultSet de forma formatada.
     *
     * @param dr ResultSet contendo os resultados de uma consulta SQL.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    static void printResults(ResultSet dr) throws SQLException {
        //Obtém os metadados do resultado da consulta, incluindo informações sobre o número de colunas e os nomes das colunas.
        ResultSetMetaData metaData = dr.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Imprimir cabeçalho
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(String.format("%-24s", metaData.getColumnName(i)));
        }
        System.out.println("\n-----------------------------------------------------");

        // Imprimir linhas de dados
        while (dr.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(String.format("%-24s", dr.getString(i)));
            }
            System.out.println();
        }
    }

    /**
     * Método para adicionar uma nova bicicleta ao sistema. Solicita informações ao usuário,
     * incluindo o tipo de bicicleta (elétrica ou clássica), os componentes da bicicleta e
     * os detalhes do dispositivo GPS associado.
     */
    private void novelBike() {
        // Exibir mensagem indicando que o método novelBike está sendo executado
        System.out.println("novelBike()");

        // Solicitar ao usuário o tipo de bicicleta (elétrica ou clássica)
        String bikeType = Model.inputData("Type of bike (1 for Electric, 2 for Classic):\n");

        // Solicitar ao usuário os componentes da bicicleta (marca, modelo, peso, etc.)
        String val_bikes = Model.inputData("The components of the bike (brand, model, weight, and others).\n");

        // Solicitar ao usuário os detalhes do dispositivo GPS (número de série, latitude, longitude, nível de bateria)
        String val_device = Model.inputData("The serial number, location (latitude and longitude), and battery level of the GPS device.\n");

        // Criar um objeto GPSDevice com base nos dados fornecidos pelo usuário
        GPSDevice device = new GPSDevice(val_device.split(","));
        // Obter o número de série do dispositivo GPS
        int nodevice = device.getSerialNumber();
        // Adicionar o dispositivo GPS ao sistema
        Model.addGPSDevice(device);

        // Verificar o tipo de bicicleta e adicionar a bicicleta correspondente ao sistema
        if ("1".equals(bikeType)) {
            // Adicionar uma bicicleta elétrica ao sistema
            Model.addElectricBike(new ElectricBike(val_bikes.split(","), nodevice));
        } else if ("2".equals(bikeType)) {
            // Adicionar uma bicicleta clássica ao sistema
            Model.addClassicBike(new ClassicBike(val_bikes.split(","), nodevice));
        } else {
            // Se o tipo de bicicleta inserido pelo usuário não for válido, exibir uma mensagem de erro
            System.out.println("Invalid bike type. Please enter 1 for Electric or 2 for Classic.");
        }

        // Recuperar e exibir os resultados para teste
        try (
                // Estabelecer conexão com o banco de dados
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                // Criar uma declaração SQL
                Statement stmt = con.createStatement();
                // Executar uma consulta para recuperar todas as bicicletas
                ResultSet rs = stmt.executeQuery("SELECT * FROM bikes");
        ) {
            // Chamar o método printResults para exibir os resultados obtidos
            printResults(rs);
        } catch (SQLException e) {
            // Se ocorrer uma exceção SQL, exibir uma mensagem de erro
            e.getMessage();
            System.out.println("Error retrieving and printing results.");
        }
    }


    /**
     * Método para atualizar o estado de uma bicicleta no sistema. Solicita ao usuário o ID
     * da bicicleta a ser atualizada e chama o método updateBikeState no modelo para realizar
     * a atualização. Em seguida, exibe os resultados para teste, mostrando o estado atualizado
     * da bicicleta.
     */
    private void updateBikeState() {
        // Exibir mensagem indicando que o método updateBikeState está sendo executado
        System.out.println("updateBikeState()");

        // Solicitar ao usuário o ID da bicicleta a ser atualizada
        int bikeId = Integer.parseInt(Model.inputData("Enter the ID of the bike to update:\n"));

        // Chamar o método updateBikeState no modelo para realizar a atualização do estado da bicicleta
        Model.updateBikeState(bikeId);

        // Recuperar e exibir os resultados para teste
        try (
                // Estabelecer conexão com o banco de dados
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                // Criar uma declaração SQL
                Statement stmt = con.createStatement();
                // Executar uma consulta para recuperar a bicicleta atualizada
                ResultSet rs = stmt.executeQuery("SELECT * FROM bikes WHERE id = " + bikeId);
        ) {
            // Chamar o método printResults para exibir os resultados obtidos
            printResults(rs);
        } catch (SQLException e) {
            // Se ocorrer uma exceção SQL, exibir uma mensagem de erro
            e.getMessage();
            System.out.println("Error retrieving and printing results.");
        }
    }


   /**
    * Método para calcular as métricas médias para bicicletas elétricas de uma marca específica.
    * Obtém a lista de marcas disponíveis, solicita ao usuário a escolha da marca e calcula as médias.
    * Exibe os resultados para teste.
    */
    private void calculateAverageMetricsForElectricBikes() {
        // Exibir mensagem indicando que o método calculateAverageMetricsForElectricBikes está sendo executado
        System.out.println("calculateAverageMetricsForElectricBikes()");

        // Obter a lista de marcas disponíveis do banco de dados
        List<String> availableBrands = getAvailableBrands();

        // Mostrar a lista de marcas disponíveis
        System.out.println("List of available brands:");
        for (String brand : availableBrands) {
            System.out.println(brand);
        }

        // Solicitar ao usuário a escolha da marca
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the brand to calculate average metrics for electric bikes:\n");
        String selectedBrand = scanner.nextLine();

        // Chamar o método calculateAverageMetricforElectricBikes no modelo para calcular as médias
        Model.calculateAverageMetricforElectricBikes(selectedBrand);

        // Obter os resultados usando o bloco try-with-resources
        try (
                // Estabelecer conexão com o banco de dados
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                // Criar uma declaração SQL
                Statement stmt = con.createStatement();
                // Executar uma consulta para calcular as médias para bicicletas elétricas da marca escolhida
                ResultSet rs = stmt.executeQuery(
                        "SELECT AVG(velocidade_maxima) AS velocidade_media, AVG(autonomia) AS autonomia_media " +
                                "FROM electric_bikes " +
                                "WHERE marca = '" + selectedBrand + "'");
        ) {
            // Exibir os resultados usando a função printResults
            printResults(rs);
        } catch (SQLException e) {
            // Se ocorrer uma exceção SQL, exibir uma mensagem de erro
            e.getMessage();
            System.out.println("Error retrieving and printing results.");
        }
    }

    /**
     * Método auxiliar para obter a lista de marcas disponíveis no banco de dados.
     * Realiza uma consulta SQL para recuperar as marcas distintas de bicicletas elétricas.
     * Exibe os resultados para teste e retorna a lista de marcas.
     *
     * @return Lista de marcas disponíveis
     */
    private static List<String> getAvailableBrands() {
        // Inicializar uma lista para armazenar as marcas disponíveis
        List<String> availableBrands = new ArrayList<>();

        // Query para obter a lista de marcas distintas do banco de dados
        String query = "SELECT DISTINCT marca FROM electric_bikes";

        try (
                // Estabelecer conexão com o banco de dados
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                // Criar uma declaração SQL
                Statement stmt = con.createStatement();
                // Executar a consulta SQL e obter o resultado
                ResultSet rs = stmt.executeQuery(query);
        ) {
            // Exibir os resultados usando a função printResults
            printResults(rs);

            // Adicionar as marcas à lista enquanto percorre os resultados
            while (rs.next()) {
                availableBrands.add(rs.getString("marca"));
            }
        } catch (SQLException e) {
            // Se ocorrer uma exceção SQL, exibir uma mensagem de erro
            e.getMessage();
            System.out.println("Error getting available brands");
        }

        // Retornar a lista de marcas disponíveis
        return availableBrands;
    }


    /**
     * Método para obter e exibir clientes com reservas com base no ano ou intervalo temporal fornecido pelo usuário.
     * Solicita ao usuário um ano ou intervalo temporal, valida o formato do ano e chama a função correspondente na classe Model.
     * Exibe os resultados usando a função printResults.
     */
    private void getClientsWithReservations() {
        System.out.println("getClientsWithReservations()");

        // Solicitar ao usuário o ano ou espaço temporal desejado
        Scanner scanner = new Scanner(System.in);
        String inputYear;

        // Pedir ao usuário até que um ano válido seja fornecido
        do {
            System.out.print("Enter the year or time frame for reservations (e.g., '2023' or '2022-2023'): ");
            inputYear = scanner.nextLine();

            if (!isValidYear(inputYear)) {
                System.out.println("Invalid year format. Please enter a valid year.");
            }
        } while (!isValidYear(inputYear));

        // Chamar a função na classe Model com o ano validado
        Model.getClientsWithReservations(inputYear);

        // Exibir os resultados usando a função printResults
        try (
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT * FROM clients_with_reservations WHERE year = '" + inputYear + "'");
        ) {
            printResults(rs);
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error printing results.");
        }
    }

    /**
     * Método auxiliar para validar o ano fornecido pelo usuário.
     *
     * @param inputYear Ano fornecido pelo usuário como String.
     * @return true se o formato do ano for válido e estiver dentro do intervalo razoável, false caso contrário.
     */
    private boolean isValidYear(String inputYear) {
        // Verificar se o ano está no formato correto e dentro de um intervalo razoável
        try {
            int year = Integer.parseInt(inputYear);
            return year >= 2000 && year <= 2030;
        } catch (NumberFormatException e) {
            return false; // Se não for um número válido
        }
    }


    /**
     * Método para obter e exibir informações sobre os dispositivos GPS.
     * Chama a função correspondente na classe Model e exibe os resultados usando a função printResults.
     */
    private void getGPSDevice() {
        System.out.println("getGPSDevices()");

        // Chamar a função na classe Model para obter informações sobre os dispositivos GPS
        Model.getGPSDevice();

        // Exibir os resultados usando a função printResults
        try (
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM gps_devices");
        ) {
            printResults(rs);
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error printing results.");
        }
    }


    /**
     * Método para obter e exibir informações sobre lojas que realizaram mais de um número mínimo de reservas.
     * Chama a função correspondente na classe Model, passando o número mínimo de reservas como parâmetro.
     * Exibe os resultados usando a função printResults.
     */
    private void getStores() {
        System.out.println("getStores()");
        int minReservations = 5; // Número mínimo de reservas

        // Chamar a função na classe Model para obter informações sobre lojas com mais de um número mínimo de reservas
        Model.getStores(minReservations);

        // Exibir os resultados usando a função printResults
        try (
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT L.codigo, L.email, COUNT(*) as num_reservations " +
                        "FROM loja L, reserva R " +
                        "WHERE L.codigo = R.loja " +
                        "GROUP BY L.codigo, L.email " +
                        "HAVING num_reservations > ? " +
                        "ORDER BY num_reservations DESC");
        ) {
            printResults(rs);
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error printing results.");
        }
    }


    /**
     * Método para obter e exibir informações sobre gerentes de lojas que realizaram reservas.
     * Chama a função correspondente na classe Model.
     * Exibe os resultados usando a função printResults.
     */
    private void getManagers() {
        System.out.println("getManagers()");

        // Chamar a função na classe Model para obter informações sobre gerentes de lojas que realizaram reservas
        Model.getManagers();

        // Exibir os resultados usando a função printResults
        try (
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT DISTINCT P.nome, P.morada, P.telefone " +
                        "FROM pessoa P " +
                        "JOIN loja L ON P.nif = L.gerente " +
                        "JOIN reserva R ON L.cod_loja = R.loja");
        ) {
            printResults(rs);
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error printing results.");
        }
    }
}

public class Ap{
    public static void main(String[] args) throws Exception{
        
        String url = "jdbc:postgresql://localhost:5433/?user=postgres&password=rumoao20&ssl=false";
        App.getInstance().setConnectionString(url);
        App.getInstance().Run();
    }
}