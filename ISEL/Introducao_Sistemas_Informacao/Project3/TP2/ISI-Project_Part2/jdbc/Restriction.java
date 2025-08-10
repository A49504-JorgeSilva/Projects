package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Restriction {
    public static boolean hasNoActiveTravel(int userID){
        final String QUERY = "SELECT * FROM TRAVEL WHERE client = ? AND dtfinal IS NOT NULL";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(QUERY)) {
            pstmt.setInt(1, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                return !rs.next();
            }
        }
        catch (SQLException e){
            System.out.println("Error on insert values");
            throw new RuntimeException(e.getMessage());
        }
    }

    public static boolean scooterIsAvailableAtStation(int scooterID, int stationID){
        final String QUERY = "SELECT * FROM DOCK WHERE station = ? AND scooter = ?";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(QUERY)) {
            pstmt.setInt(1, stationID);
            pstmt.setInt(2, scooterID);
            try (ResultSet rs = pstmt.executeQuery()) {
                return !rs.next();
            }
        }
        catch (SQLException e){
            System.out.println("Error on insert values");
            throw new RuntimeException(e.getMessage());
        }
    }

    public static boolean isValidInt(String s){
        return s.matches("\\d+");
    }

    public static boolean hasEval(int userID, Timestamp dtinitial){
        final String QUERY = "SELECT evaluation FROM TRAVEL WHERE client = ? AND dtinitial = ?";
        try (
                Connection conn = DriverManager.getConnection(UI.getInstance().getConnectionString());
                PreparedStatement pstmt = conn.prepareStatement(QUERY)) {
            pstmt.setInt(1, userID);
            pstmt.setTimestamp(2, dtinitial);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getString("evaluation").isEmpty();
            }
        }
        catch (SQLException e){
            System.out.println("Error on insert values");
            throw new RuntimeException(e.getMessage());
        }
    }
}