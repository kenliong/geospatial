/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geo.controller;

import java.io.*;
import java.net.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;
import geo.model.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kennethliong
 */
public class GeoCodeServlet extends HttpServlet {

    private String accessKey = "xkg8VRu6Ol+gMH+SUamkRIEB7fKzhwMvfMo/2U8UJcFhdvR4yN1GutmUIA3A6r3LDhot215OVVkZvNRzjl28TNUZgYFSswOi";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try{
            String token = getTokenFromAPI();
            ArrayList<Transaction> transactionList = getAllTransactions();
            ArrayList<MRTStation> mrtList = getAllMRTStation();
            String[] coordinate = null;
            String result = null;
            /*
            for(Transaction t:transactionList){
                result = geoCodeTransactions(token,t);
                if (result.equals("")){
                    result = geoCodeTransactions2(t);
                }
                
                if(result.equals("")){
                    out.println(t.address);
                    out.println(t.postalCode);
                    out.println(result);
                    continue;
                }
                
                coordinate = result.split(",");
                try{
                    t.lng = Double.parseDouble(coordinate[0]);
                    t.lat = Double.parseDouble(coordinate[1]);
                } catch (NumberFormatException e){
                    out.println(t.address);
                    out.println(t.postalCode);
                    out.println(result);
                }
            }

            TransactionDAO.addPostalGeocodes(transactionList);
            */
            
            for(MRTStation m:mrtList){
                result = geoCodeMRT(token,m);
                
                if(result.equals("")){
                    out.println(m.code);
                    out.println(m.stationName);
                    out.println(result);
                    continue;
                }
                
                coordinate = result.split(",");
                try{
                    m.lng = Double.parseDouble(coordinate[0]);
                    m.lat = Double.parseDouble(coordinate[1]);
                } catch (NumberFormatException e){
                    out.println(m.code);
                    out.println(m.stationName);
                    out.println(result);
                }
            }

            MRTStationDAO.addStationGeocodes(mrtList);
            
            
            out.println("Success");
            
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<MRTStation> getAllMRTStation(){
        try {
            return MRTStationDAO.getMRTList();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private ArrayList<Transaction> getAllTransactions() {
        try {
            return TransactionDAO.getTransactionListToGeocode();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String convertCoordinates(double x, double y) {
        try {
            URL oneMapCoordConvert = new URL(
                    "http://www.onemap.sg/arcgis/rest/services/Utilities/Geometry/GeometryServer/project?"
                    + "f=json"
                    + "&outSR=4326"
                    + "&inSR=3414"
                    + "&geometries={\"geometryType\":\"esriGeometryPoint\",\"geometries\":[{\"x\":\"" + x + "\",\"y\":\"" + y + "\",\"spatialReference\":{\"wkid\":\"3414\"}}]}"
            );

            URLConnection yc = oneMapCoordConvert.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String json = reader.readLine();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray)jsonObject.get("geometries");
            jsonObject = (JSONObject)jsonArray.get(0);
            
            return jsonObject.get("x").toString()+","+jsonObject.get("y");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private String geoCodeTransactions2(Transaction t){
        String[] address = t.address.split("#");
        try {
            String url = "http://maps.googleapis.com/maps/api/geocode/json?address="+ URLEncoder.encode(("Singapore, "+address[0].trim()),"UTF-8") +"&sensor=true";        
            URL googleMapGeoCode = new URL(url);

            URLConnection yc = googleMapGeoCode.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            StringBuilder sb = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                sb.append(line).append("\n");
            }
            
            JSONParser parser = new JSONParser();           
            Object obj = parser.parse(sb.toString());
            JSONObject jsonObject = (JSONObject) obj;
            
            if (jsonObject.get("status").equals("OK")){
                JSONArray jsonArray = (JSONArray)jsonObject.get("results");
                jsonObject = (JSONObject)jsonArray.get(0);
                jsonObject = (JSONObject)jsonObject.get("geometry");
                jsonObject = (JSONObject)jsonObject.get("location");
                
                return jsonObject.get("lng").toString()+","+jsonObject.get("lat").toString();
                
                
            }
            
            
            //converts SVY21 (OneMap) to WSG84 (Google Map)
            //return convertCoordinates(xCoordinate,yCoordinate);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
        
        
    }
    
    private String geoCodeTransactions(String token, Transaction t) {

        try {
            URL oneMapGeoCode = new URL(
                    "http://www.onemap.sg/API/services.svc/basicSearch?token=" + token
                    + "&returnGeom=1&searchVal=" + t.postalCode
            );

            URLConnection yc = oneMapGeoCode.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String json = reader.readLine();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jsonObject.get("SearchResults");
            jsonObject = (JSONObject) jsonArray.get(1);
            double xCoordinate = Double.parseDouble(jsonObject.get("X").toString());
            double yCoordinate = Double.parseDouble(jsonObject.get("Y").toString());
            
            //converts SVY21 (OneMap) to WSG84 (Google Map)
            return convertCoordinates(xCoordinate,yCoordinate);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    /**
     *
     * Invoked Java URL Connection to retrieve Token from AccessKey provided by
     * OneMap API.
     *
     * A JSON object will be returned from the request call. We will Parse the
     * JSON using the simple JSON library and filter out the token required to
     * invoke OneMaps' geocoding service.
     *
     * @return token from OneMapAPI
     * @throws ServletException
     * @throws IOException
     */
    private String getTokenFromAPI() throws ServletException, IOException {

        String token = null;

        URL oneMapToken = new URL(
                "http://www.onemap.sg/API/services.svc/getToken?accessKEY=" + accessKey
        );
        URLConnection yc = oneMapToken.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String json = reader.readLine();

        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jsonObject.get("GetToken");
            jsonObject = (JSONObject) jsonArray.get(0);

            token = jsonObject.get("NewToken").toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return token;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private String geoCodeMRT(String token, MRTStation m) {
        String json = "";
        
        try {
            URL oneMapGeoCode = new URL(
                    "http://www.onemap.sg/API/services.svc/basicSearch?token=" + token
                    + "&returnGeom=1&searchVal=" +URLEncoder.encode((m.stationName + " MRT").trim(),"UTF-8")
            );

            URLConnection yc = oneMapGeoCode.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            json = reader.readLine();

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jsonObject.get("SearchResults");
            jsonObject = (JSONObject) jsonArray.get(1);
            double xCoordinate = Double.parseDouble(jsonObject.get("X").toString());
            double yCoordinate = Double.parseDouble(jsonObject.get("Y").toString());
            
            //converts SVY21 (OneMap) to WSG84 (Google Map)
            return convertCoordinates(xCoordinate,yCoordinate);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        System.out.println(json);

        return "";
    }
}
