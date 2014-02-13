/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package geo.model;

import org.json.simple.JSONObject;

/**
 *
 * @author kennethliong
 */
public class MRTStation {
    public String code;
    public String stationName;
    public String workingName;
    public double lat;
    public double lng;
    public String lineCode;
    public int stationNumber;

    public MRTStation(String code, String stationName, String workingName, double lat, double lng, String lineCode, int stationNumber) {
        this.code = code;
        this.stationName = stationName;
        this.workingName = workingName;
        this.lat = lat;
        this.lng = lng;
        this.lineCode = lineCode;
        this.stationNumber = stationNumber;
    }
    
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("code",code);
            obj.put("stationName",stationName);
            obj.put("workingName",workingName);
            obj.put("lineCode",lineCode);
            obj.put("stationNumber",stationNumber);
            obj.put("lat",lat);
            obj.put("lng",lng);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    
}
