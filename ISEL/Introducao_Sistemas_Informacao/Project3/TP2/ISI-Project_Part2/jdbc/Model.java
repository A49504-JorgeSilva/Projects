package jdbc;

import java.util.Objects;
import java.util.Scanner;
import java.io.IOException;
import java.sql.*;

import static java.lang.Integer.parseInt;
import static java.lang.reflect.Array.getInt;

/*
*
* @author MP
* @version 1.0
* @since 2024-11-07
*/
public class Model {

    static String inputData(String str) throws IOException {
        // IMPLEMENTED
        /*
         * Gets input data from user
         *
         * @param str Description of required input values
         *
         * @return String containing comma-separated values
         */
        Scanner key = new Scanner(System.in); // Scanner closes System.in if you call close(). Don't do it
        System.out.println("Enter corresponding values, separated by commas of:");
        System.out.println(str);
        return key.nextLine();
    }

    static void addUser(User userData, Card cardData) {
        // PARCIALLY IMPLEMENTED
        /**
         * Adds a new user with associated card to the database
         *
         * @param userData User information
         * @param cardData Card information
         * @throws SQLException if database operation fails
         */
        final String INSERT_PERSON = "INSERT INTO person(email, taxnumber, name) VALUES (?,?,?) RETURNING id";
        final String INSERT_CARD = "INSERT INTO card(credit, typeof, client) VALUES (?,?,?)";
        final String INSERT_USER = "INSERT INTO client(person, dtregister) VALUES (?,?)";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmtPerson = conn.prepareStatement(INSERT_PERSON, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement pstmtCard = conn.prepareStatement(INSERT_CARD);
                PreparedStatement pstmtUser = conn.prepareStatement(INSERT_USER);) {
            conn.setAutoCommit(false);

            // Insert person
            pstmtPerson.setString(1, userData.getEmail());
            pstmtPerson.setInt(2, userData.getTaxNumber());
            pstmtPerson.setString(3, userData.getName());

            int affectedRows = pstmtPerson.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Creating person failed, no rows affected.");
            }
            int personId;
            try (ResultSet generatedKeys = pstmtPerson.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    personId = generatedKeys.getInt(1);
                } else {
                    throw new RuntimeException("Creating person failed, no ID obtained.");
                }
            }



            // CONTINUE




            conn.commit();
            if (pstmtUser != null)
                pstmtUser.close();
            if (pstmtCard != null)
                pstmtCard.close();
            if (pstmtPerson != null)
                pstmtPerson.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error on insert values");
            // e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * To implement from this point forward. Do not need to change the code above.
     * -------------------------------------------------------------------------------
     * IMPORTANT:
     * --- DO NOT MOVE IN THE CODE ABOVE. JUST HAVE TO IMPLEMENT THE METHODS BELOW
     * ---
     * -------------------------------------------------------------------------------
     **/

    static void listOrders(String[] orders) {
        /**
         * Lists orders based on specified criteria
         *
         * @param orders Criteria for listing orders
         * @throws SQLException if database operation fails
         */
        final String VALUE_CMD = " TO BE DONE";
        // try(
        // Connection conn =
        // DriverManager.getConnection(UI.getInstance().getConnectionString());
        // PreparedStatement pstmt1 = conn.prepareStatement(VALUE_CMD);
        // ){

        // }
    }


    public static void listReplacementOrders(int stationId, Timestamp startDate, Timestamp endDate) throws SQLException {
        /**
         * Lists replacement orders for a specific station in a given time period
         * @param stationId Station ID
         * @param startDate Start date for period
         * @param endDate End date for period
         * @throws SQLException if database operation fails
         */
        // TO BE DONE
        System.out.print("EMPTY");
    }

    public static void travel(String[] values) {
        /**
         * Processes a travel operation (start or stop)
         * @param values Array containing [operation, name, station, scooter]
         * @throws SQLException if database operation fails
         */
        if(Objects.equals(values[0], "start")){
            startTravel(getClientId(values[1]), parseInt(values[3]),parseInt(values[2]));

        }
        else{
            stopTravel(getClientId(values[1]), parseInt(values[3]),parseInt(values[2]));
        }
    }

    public static int getClientId(String name) {
        final String QUERY = "SELECT id FROM client WHERE name = ?";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(QUERY)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
        }
        catch (SQLException e){
            System.out.println("Error on insert values");
            throw new RuntimeException(e.getMessage());
        }
        return 0;
    }

    public static void startTravel(int clientId, int scooterId, int stationId) {
        final String INSERT_TRAVEL = "INSERT INTO TRAVEL(client, scooter, stinitial, dtinitial, dtfinal, evaluation, comment) " +
                "VALUES (?, ?, ?, ?, NULL, NULL, NULL)";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(INSERT_TRAVEL);
        ) {
            pstmt.setInt(1, clientId);
            pstmt.setInt(2, scooterId);
            pstmt.setInt(3, stationId);
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error on insert values");
            throw new RuntimeException(e.getMessage());
        }
    }


    public static void stopTravel(int clientId, int scooterId, int stationId) {
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        final String UPDATE_TRAVEL = "UPDATE TRAVEL SET stfinal = ?, dtfinal = ? WHERE client = ? AND scooter = ? AND dtfinal IS NULL";
        final String GET_DATE = "SELECT dtinitial FROM TRAVEL WHERE client = ? AND scooter = ? AND dtfinal = ?";
        final String GET_CARD = "SELECT credit FROM CARD WHERE client = ?";
        final  String UPDATE_CARD = "UPDATE CARD SET card = ?, WHERE client = ?";
        final String GET_COSTS = "SELECT * FROM SERVICECOST";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmtUpdate = conn.prepareStatement(UPDATE_TRAVEL);
                PreparedStatement pstmtGetTimes = conn.prepareStatement(GET_DATE);
                PreparedStatement pstmtGetCard = conn.prepareStatement(GET_CARD);
                PreparedStatement pstmtUpdateCard = conn.prepareStatement(UPDATE_CARD);
                PreparedStatement pstmtGetCosts = conn.prepareStatement(GET_COSTS);
        ){
            pstmtUpdate.setInt(1, stationId);
            pstmtUpdate.setTimestamp(2, currTime);
            pstmtUpdate.setInt(3, clientId);
            pstmtUpdate.setInt(4, scooterId);

            pstmtGetCard.setInt(1, clientId);
            pstmtUpdateCard.setInt(2, clientId);

            pstmtUpdate.executeUpdate();

            pstmtGetTimes.setInt(1, clientId);
            pstmtGetTimes.setInt(2, scooterId);
            pstmtGetTimes.setTimestamp(3, currTime);

            try (ResultSet rsTravel = pstmtGetTimes.executeQuery()){
                int tripDuration = (int)(currTime.getTime() - rsTravel.getTimestamp("dtinitial").getTime())/60000;
                ResultSet rsCosts = pstmtGetCosts.executeQuery();
                int cost = rsCosts.getInt("unlock") + rsCosts.getInt("usable")*tripDuration;
                try (ResultSet rsCard = pstmtGetCard.executeQuery()){
                    int newCredit = rsCard.getInt("credit") - cost;

                    ///verificar caso de cartão não ter suficiente
                    if(newCredit < 0){
                        System.out.println("credito insuficiente contacte um operador para retifcar a sitiação");
                        pstmtUpdateCard.setInt(1, 0);
                    }
                    else{
                        pstmtUpdateCard.setInt(1, newCredit);
                    }
                    pstmtUpdateCard.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error on insert values");
            // e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        userSatisfaction(clientId);
    }

    ///é utilizado polimorfismo para o caso de remover uma scooter
    /// pois o parametro scooter fica a null e não faz sentido passar o argumento para a função
    /// duvida a inserir nulls em sql
    public static void updateDocks(int stationId, String state, int scooter, int dockNumber) {
        final String DOCK_UPDATE = "UPDATE DOCK SET state = ?, scooter = ?, WHERE number = ? AND station = ?";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(DOCK_UPDATE);
        ){
            pstmt.setString(1, state);
            pstmt.setInt(2, scooter);
            pstmt.setInt(3, dockNumber);
            pstmt.setInt(4, stationId);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error on insert values");
            // e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        System.out.println("updateDocks()");
    }

    public static void updateDocks(int stationId, String state, int dockNumber) {
        final String DOCK_UPDATE = "UPDATE DOCK SET state = ?, scooter = NULL, WHERE number = ? AND station = ?";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(DOCK_UPDATE);
        ){
            pstmt.setString(1, state);
            pstmt.setInt(2, dockNumber);
            pstmt.setInt(3, stationId);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error on insert values");
            // e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        System.out.println("updateDocks()");
    }

    public static void userSatisfaction(int clientId) {

    }

    public static void occupationStation(/*FILL WITH PARAMETERS */) {
        // TODO
        System.out.println("occupationStation()");
    }
}