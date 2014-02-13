/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geo.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author kennethliong
 */
public class MRTStationDAO {

    private Connection connection = null;

    private MRTStationDAO() {
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

    public ResultSet getAllMRTData() {
        ResultSet rs = null;
        Statement s = null;
        try {
            s = connection.createStatement();

            rs = s.executeQuery(
                    "select *, substring(code from 1 for 2) as lineCode, (substring(code from 3))::int as stationNumber from geospatial.mrt order by stationnumber;"
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

    public static ArrayList<MRTStation> getMRTList() throws SQLException {
        MRTStationDAO x = new MRTStationDAO();
        ArrayList<MRTStation> mrtList = new ArrayList<MRTStation>();
        ResultSet rs = null;
        String string = "";

        x.establishConnection();
        rs = x.getAllMRTData();

        try {
            while (rs.next()) {
                MRTStation m = new MRTStation(
                        rs.getString("code"),
                        rs.getString("Station Name"),
                        rs.getString("Working Name"),
                        rs.getDouble("lat"),
                        rs.getDouble("lng"),
                        rs.getString("lineCode"),
                        rs.getInt("stationNumber")
                );
                mrtList.add(m);
            }

            System.out.println(string);

        } catch (Exception e) {
            System.out.println("Problem when printing the database.");
        }
        x.closeConnection();

        return mrtList;
    }

    private boolean updateGeocode(MRTStation m) {
        Statement s = null;

        if (m.lat == 0.0 && m.lng == 0.0) {
            return false;
        }

        try {
            s = connection.createStatement();

            s.executeUpdate(
                    "UPDATE geospatial.mrt SET lat = " + m.lat + ",lng = " + m.lng + " WHERE code = '" + m.code + "';"
            );
        } catch (Exception e) {
            System.out.println("Problem in updating the database Postal Geocode");
            return false;
        }

        return true;

    }

    public static void addStationGeocodes(ArrayList<MRTStation> mrtList) {
        MRTStationDAO x = new MRTStationDAO();
        x.establishConnection();

        for (MRTStation m : mrtList) {
            x.updateGeocode(m);
        }

        x.closeConnection();
    }

}
