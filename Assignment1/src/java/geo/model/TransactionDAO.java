/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geo.model;

import java.sql.*;
import java.util.*;

/**
 *
 * @author kennethliong
 */
public class TransactionDAO {

    private Connection connection = null;

    private TransactionDAO() {
    }

    public void establishConnection() {
        if (connection != null) {
            return;
        }
        String url = "jdbc:postgresql://localhost:5432/";
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(url, "kennethliong", "");

            if (connection != null) {
                System.out.println("Connecting to database...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem when connecting to the database 1");
        }
    }
    
    public ResultSet getAllTransactionDataToGeocode(){
        ResultSet rs = null;
        Statement s = null;
        try {
            s = connection.createStatement();

            rs = s.executeQuery(
                    "select * from geospatial.realis;"
            );
        } catch (Exception e) {
            System.out.println("Problem in searching the database 1");
        }
        return rs;
    }
    
    public ResultSet getAllTransactionData() {
        ResultSet rs = null;
        Statement s = null;
        try {
            s = connection.createStatement();

            rs = s.executeQuery(
                    "select * from geospatial.realis r, geospatial.\"Postal Geocode\" p where r.\"Postal Code\" = p. \"Postal Code\";"
            );
        } catch (Exception e) {
            System.out.println("Problem in searching the database 1");
        }
        return rs;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println("Problem to close the connection to the database");
        }
    }

    public static ArrayList<Transaction> getTransactionListToGeocode() throws SQLException {
        TransactionDAO x = new TransactionDAO();
        ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
        ResultSet rs = null;
        String string = "";

        x.establishConnection();
        rs = x.getAllTransactionDataToGeocode();

        try {
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getString("Project Name"),
                        rs.getString("Address"),
                        rs.getInt("No. of Units"),
                        rs.getDouble("Area (sqm)"),
                        rs.getString("Type of Area"),
                        rs.getDouble("Transacted Price ($)"),
                        rs.getDouble("Unit Price ($ psm)"),
                        rs.getDouble("Unit Price ($ psf)"),
                        rs.getDate("Contract Date"),
                        rs.getString("Property Type"),
                        rs.getString("Tenure"),
                        rs.getString("Completion Date"),
                        rs.getString("Type of Sale"),
                        rs.getString("Purchaser Address Indicator"),
                        rs.getString("Postal District"),
                        rs.getString("Postal Sector"),
                        rs.getString("Postal Code"),
                        rs.getString("Planning Region"),
                        rs.getString("Planning Area")
                );
                transactionList.add(t);
            }

            System.out.println(string);

        } catch (Exception e) {
            System.out.println("Problem when printing the database.");
        }
        x.closeConnection();

        return transactionList;
    }
    
    public static ArrayList<Transaction> getTransactionList() throws SQLException {
        TransactionDAO x = new TransactionDAO();
        ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
        ResultSet rs = null;
        String string = "";

        x.establishConnection();
        rs = x.getAllTransactionData();

        try {
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getString("Project Name"),
                        rs.getString("Address"),
                        rs.getInt("No. of Units"),
                        rs.getDouble("Area (sqm)"),
                        rs.getString("Type of Area"),
                        rs.getDouble("Transacted Price ($)"),
                        rs.getDouble("Unit Price ($ psm)"),
                        rs.getDouble("Unit Price ($ psf)"),
                        rs.getDate("Contract Date"),
                        rs.getString("Property Type"),
                        rs.getString("Tenure"),
                        rs.getString("Completion Date"),
                        rs.getString("Type of Sale"),
                        rs.getString("Purchaser Address Indicator"),
                        rs.getString("Postal District"),
                        rs.getString("Postal Sector"),
                        rs.getString("Postal Code"),
                        rs.getString("Planning Region"),
                        rs.getString("Planning Area")
                );
                t.lat = rs.getDouble("latitude");
                t.lng = rs.getDouble("longitude");
                transactionList.add(t);
            }

            System.out.println(string);

        } catch (Exception e) {
            System.out.println("Problem when printing the database.");
        }
        x.closeConnection();

        return transactionList;
    }

    private boolean updatePostalGeocodeTable(Transaction t) {
        Statement s = null;

        if (t.lat == 0.0 && t.lng == 0.0) {
            return false;
        }

        try {
            s = connection.createStatement();

            s.executeUpdate(
                    "insert into geospatial.\"Postal Geocode\" (\"Postal Code\", latitude, longitude)"
                    + "select '" + t.postalCode + "', " + t.lat + ", " + t.lng + " "
                    + "where not exists ("
                    + "    select * from geospatial.\"Postal Geocode\" where \"Postal Code\" = '" + t.postalCode + "'"
                    + ");"
            );
        } catch (Exception e) {
            System.out.println("Problem in updating the database Postal Geocode");
            return false;
        }

        return true;

    }

    public static void addPostalGeocodes(ArrayList<Transaction> transactionList) {
        TransactionDAO x = new TransactionDAO();
        x.establishConnection();

        for (Transaction t : transactionList) {
            x.updatePostalGeocodeTable(t);
        }

        x.closeConnection();
    }


}
