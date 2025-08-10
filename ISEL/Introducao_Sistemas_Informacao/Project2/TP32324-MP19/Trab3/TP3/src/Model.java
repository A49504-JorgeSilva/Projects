/*
 * @Author: Matilde Pato (mpato)
 * @Date: 2023-11-27 09:30:00
 * @Last Modified time: 2023-12-06 18:30:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2023-2024
 * 
 * IMPORTANT:
 * This class should implement all the options listed in the statement. 
 * You may need to import more classes.
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Model {

    static String inputData(String str){
        // IMPLEMENTED
        Scanner key = new Scanner(System.in);
        System.out.print("Enter corresponding values, separated by commas of: \n" + str);
        String values = key.nextLine();
        return values;
    }

    static void addGPSDevice(GPSDevice device){
        //IMPLEMENTED
        final String INSERT_CMD = "INSERT INTO dispositivo VALUES (?,?,?,?)";
        try(
            Connection conn = DriverManager.getConnection(App.getInstance().getConnectionString());
            PreparedStatement pstmt = conn.prepareStatement(INSERT_CMD);
        ){
            conn.setAutoCommit(false);
            pstmt.setInt(1,device.getSerialNumber());
            pstmt.setDouble(2,device.getLatitude());
            pstmt.setDouble(3,device.getLongitude());
            pstmt.setDouble(4,device.getBattery());
            pstmt.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);

            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e){
            e.getMessage();
            System.out.println("Error on insert values");
            }    
        }

    /**
    To implement from this point forward. Do not need to change the code above.
    -------------------------------------------------------------------------------     
        IMPORTANT:
    --- DO NOT MOVE IN THE CODE ABOVE. JUST HAVE TO IMPLEMENT THE METHODS BELOW ---
    -------------------------------------------------------------------------------
    */


    /**
     * Método estático para adicionar uma bicicleta clássica à base de dados.
     * Utiliza uma instrução SQL preparada para evitar injeção de SQL.
     * Os parâmetros da bicicleta são usados para preencher os valores na instrução SQL.
     * Realiza a conexão com o banco de dados, executa a inserção e imprime uma mensagem de sucesso ou erro.
     *
     * @param bike A instância de ClassicBike contendo os detalhes da bicicleta clássica a ser inserida.
     */
    static void addClassicBike(ClassicBike bike) {
        // Instrução SQL para a inserção de uma bicicleta clássica
        final String INSERT_CMD = "INSERT INTO classic_bikes (id, peso, raio, modelo, marca, mudanca, estado, atrdisc, dispositivo, nomudanca) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                // Conexão com o banco de dados
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                // Preparação da instrução SQL
                PreparedStatement pstmt = con.prepareStatement(INSERT_CMD);
        ) {
            // Preencher os parâmetros na instrução SQL com os valores da bicicleta
            pstmt.setInt(1, bike.getId());
            pstmt.setDouble(2, bike.getPeso());
            pstmt.setInt(3, bike.getRaio());
            pstmt.setString(4, bike.getModelo());
            pstmt.setString(5, bike.getMarca());
            pstmt.setInt(6, bike.getMudanca());
            pstmt.setString(7, bike.getEstado());
            pstmt.setString(8, String.valueOf(bike.getAtrdisc()));
            pstmt.setInt(9, bike.getDispositivo());
            pstmt.setInt(10, bike.getNoMudanca());

            // Executar a inserção
            pstmt.executeUpdate();
            System.out.println("Classic bike registered successfully!");
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error on insert values. Classic bike registration failed.");
        }
    }


    /**
     * Método estático para adicionar uma bicicleta elétrica à base de dados.
     * Utiliza uma instrução SQL preparada para evitar injeção de SQL.
     * Os parâmetros da bicicleta são usados para preencher os valores na instrução SQL.
     * Realiza a conexão com o banco de dados, executa a inserção e imprime uma mensagem de sucesso ou erro.
     *
     * @param bike A instância de ElectricBike contendo os detalhes da bicicleta elétrica a ser inserida.
     */
    static void addElectricBike(ElectricBike bike) {
        // Instrução SQL para a inserção de uma bicicleta elétrica
        final String INSERT_CMD = "INSERT INTO electric_bikes (id, peso, raio, modelo, marca, mudanca, estado, atrdisc, dispositivo, autonomia, velocidade_maxima) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                // Conexão com o banco de dados
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                // Preparação da instrução SQL
                PreparedStatement pstmt = con.prepareStatement(INSERT_CMD);
        ) {
            // Preencher os parâmetros na instrução SQL com os valores da bicicleta elétrica
            pstmt.setInt(1, bike.getId());
            pstmt.setDouble(2, bike.getPeso());
            pstmt.setInt(3, bike.getRaio());
            pstmt.setString(4, bike.getModelo());
            pstmt.setString(5, bike.getMarca());
            pstmt.setInt(6, bike.getMudanca());
            pstmt.setString(7, bike.getEstado());
            pstmt.setString(8, String.valueOf(bike.getAtrdisc()));
            pstmt.setInt(9, bike.getDispositivo());
            pstmt.setDouble(10, bike.getAutonomia());
            pstmt.setDouble(11, bike.getVelocidadeMaxima());

            // Executar a inserção
            pstmt.executeUpdate();
            System.out.println("Electric bike registered successfully!");
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error on insert values. Electric bike registration failed.");
        }
    }


    /**
     * Método estático para atualizar o estado de uma bicicleta com base no ID.
     * Utiliza uma instrução SQL preparada para evitar injeção de SQL.
     * O ID da bicicleta é usado para identificar a bicicleta a ser atualizada.
     * Realiza a conexão com o banco de dados, executa a atualização e imprime uma mensagem de sucesso ou erro.
     *
     * @param bikeId O ID da bicicleta a ser atualizada.
     */
    static void updateBikeState(int bikeId) {
        // Query para atualizar o estado de uma bicicleta com base no ID
        final String UPDATE_CMD = "UPDATE bikes SET estado = 'em manutenção' WHERE id = ?";

        try (
                // Conexão com o banco de dados
                Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
                // Preparação da instrução SQL
                PreparedStatement pstmt = con.prepareStatement(UPDATE_CMD);
        ) {
            // Preencher o parâmetro na instrução SQL com o ID da bicicleta
            pstmt.setInt(1, bikeId);

            // Executar a atualização e obter o número de linhas afetadas
            int rowsAffected = pstmt.executeUpdate();

            // Verificar se alguma linha foi afetada
            if (rowsAffected > 0) {
                System.out.println("Bike state updated to 'em manutenção' successfully!");
            } else {
                System.out.println("No bike found with the specified ID.");
            }
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error updating bike state. Update operation failed.");
        }
    }

    /**
     * Método estático para calcular a média de autonomia, velocidade máxima e bateria das bicicletas elétricas de uma determinada marca.
     * Utiliza uma instrução SQL preparada para evitar injeção de SQL.
     * A marca é passada como parâmetro para a query.
     * Realiza a conexão com o banco de dados, executa a query e imprime as métricas médias.
     *
     * @param selectedBrand A marca das bicicletas elétricas para as quais calcular as médias.
     */
    static void calculateAverageMetricforElectricBikes(String selectedBrand)
    {
        // Query para calcular a média de autonomia e velocidade máxima das bicicletas elétricas
        final String AVG_CMD = "SELECT AVG(autonomia), AVG(velocidade_maxima), AVG(bateria) FROM electric_bikes WHERE marca = ?";
        try (
            // Conexão com o banco de dados
            Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
            // Preparação da instrução SQL
            PreparedStatement pstmt = con.prepareStatement(AVG_CMD);
        ) {
            // Preencher o parâmetro na instrução SQL com a marca selecionada
            pstmt.setString(1, selectedBrand);

            // Executar a query e obter o conjunto de resultados
            ResultSet rs = pstmt.executeQuery();

            // Iterar sobre os resultados e imprimir as métricas médias
            while (rs.next()) {
                System.out.println("\nAverage metrics for electric bikes of brand '" + selectedBrand + "':");
                System.out.println("Average autonomy: " + rs.getDouble(1));
                System.out.println("Average maximum speed: " + rs.getDouble(2));
                System.out.println("Average Bateria: " + rs.getDouble(3));
            }
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error calculating average metrics");
        }
    }

    /**
     * Método estático para obter os clientes com mais reservas no ano ou espaço temporal especificado.
     * Utiliza uma instrução SQL preparada para evitar injeção de SQL.
     * Recebe o ano ou intervalo temporal como parâmetro, executa a consulta no banco de dados e imprime os resultados.
     *
     * @param inputYear O ano ou espaço temporal para o qual obter os clientes com mais reservas.
     */
    static void getClientsWithReservations(String inputYear) {
        // Query para obter os clientes com mais reservas no ano ou espaço temporal especificado
        final String QUERY = "SELECT C.nome, C.morada, C.telefone, C.nacionalidade " +
                "FROM cliente C, reserva R " +
                "WHERE C.id = R.cliente AND YEAR(R.datareserva) IN (" + inputYear + ") " +
                "GROUP BY C.nome " +
                "ORDER BY COUNT(R.id) DESC " +
                "LIMIT 1";

        // Conexão com o banco de dados
        try (Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
             // Preparação da instrução SQL
             PreparedStatement pstmt = con.prepareStatement(QUERY);
             // Executar a consulta
             ResultSet rs = pstmt.executeQuery()) {

            // Exibir os clientes com mais reservas
            System.out.println("Client(s) with the most reservations in " + inputYear + ":");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("nome") +
                        ", Address: " + rs.getString("morada") +
                        ", Phone: " + rs.getString("telefone") +
                        ", Nationality: " + rs.getString("nacionalidade"));
            }

        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error executing query: ");
        }
    }


    /**
     * Método estático para obter os dispositivos GPS de bicicletas em manutenção.
     * Utiliza uma instrução SQL preparada para evitar injeção de SQL.
     * Executa a consulta no banco de dados e imprime os resultados.
     */
    static void getGPSDevice() {
        // Query para obter os dispositivos GPS de bicicletas em manutenção
        String QUERY = "SELECT D.noserie, D.latitude, D.longitude " +
                "FROM dispositivo D, bike B " +
                "WHERE B.estado = 'em manutenção' AND B.dispositivo = D.noserie";

        // Conexão com o banco de dados
        try (Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
             // Preparação da instrução SQL
             PreparedStatement pstmt = con.prepareStatement(QUERY);
             // Executar a consulta
             ResultSet rs = pstmt.executeQuery()) {

            // Exibir os dispositivos GPS
            System.out.println("List of GPS devices for bikes in maintenance:");
            while (rs.next()) {
                System.out.println("Serial Number: " + rs.getInt("noserie") +
                        ", Latitude: " + rs.getDouble("latitude") +
                        ", Longitude: " + rs.getDouble("longitude"));
            }

        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error executing query: ");
        }
    }


    /**
     * Método estático para obter as lojas com mais de um número mínimo de reservas.
     * Utiliza uma instrução SQL preparada para evitar injeção de SQL.
     * Executa a consulta no banco de dados e imprime os resultados.
     *
     * @param minReservations Número mínimo de reservas para filtrar as lojas.
     */
    static void getStores(int minReservations) {
        // Query para obter as lojas com mais de minReservations reservas
        String QUERY = "SELECT L.codigo, L.email, COUNT(*) as num_reservations " +
                "FROM loja L, reserva R " +
                "WHERE L.codigo = R.loja " +
                "GROUP BY L.codigo, L.email " +
                "HAVING num_reservations > ? " +
                "ORDER BY num_reservations DESC";

        // Conexão com o banco de dados
        try (Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
             // Preparação da instrução SQL
             PreparedStatement pstmt = con.prepareStatement(QUERY)) {

            // Definir o número mínimo de reservas como parâmetro
            pstmt.setInt(1, minReservations);

            // Executar a consulta
            ResultSet rs = pstmt.executeQuery();

            // Exibir as lojas com mais de minReservations reservas
            System.out.println("List of stores with more than " + minReservations + " reservations:");
            while (rs.next()) {
                System.out.println("Store Code: " + rs.getInt("codigo") +
                        ", Email: " + rs.getString("email") +
                        ", Number of Reservations: " + rs.getInt("num_reservations"));
            }

        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error executing query: ");
        }
    }


    /**
     * Método estático para obter informações dos gerentes que efetuaram reservas.
     * Utiliza uma instrução SQL preparada para evitar injeção de SQL.
     * Executa a consulta no banco de dados e imprime os resultados.
     */
    static void getManagers() {
        // Query para obter informações dos gerentes com reservas
        String QUERY = "SELECT DISTINCT P.nome, P.morada, P.telefone " +
                "FROM pessoa P " +
                "JOIN loja L ON P.nif = L.gerente " +
                "JOIN reserva R ON L.cod_loja = R.loja";

        // Conexão com o banco de dados
        try (Connection con = DriverManager.getConnection(App.getInstance().getConnectionString());
             // Preparação da query
             PreparedStatement pstmt = con.prepareStatement(QUERY);
             // Executar a consulta
             ResultSet rs = pstmt.executeQuery()) {

            // Exibir informações dos gerentes com reservas
            System.out.println("List of managers with reservations:");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("nome") +
                        ", Address: " + rs.getString("morada") +
                        ", Phone: " + rs.getString("telefone"));
            }

        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Error executing query: ");
        }
    }
}