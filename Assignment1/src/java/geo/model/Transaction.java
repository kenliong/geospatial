/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package geo.model;

import java.util.*;
import org.json.simple.*;

/**
 *
 * @author kennethliong
 */
public class Transaction {
    public String projectName;
    public String address;
    public int noOfUnits;
    public double areaSQM;
    public String typeOfArea;
    public double trasactedPrice;
    public double unitPricePSM;
    public double unitPricePSF;
    public Date contractDate;
    public String propertyType;
    public String tenure;
    public String completionDate;
    public String typeOfSale;
    public String purchaserAddressIndicator;
    public String postalDistrict;
    public String postalSector;
    public String postalCode;
    public String planningRegion;
    public String planningArea;
    public double lat;
    public double lng;

    public Transaction(
            String projectName, 
            String address, 
            int noOfUnits, 
            double areaSQM, 
            String typeOfArea,
            double trasactedPrice, 
            double unitPricePSM, 
            double unitPricePSF, 
            Date contractDate, 
            String propertyType, 
            String tenure, 
            String completionDate, 
            String typeOfSale, 
            String purchaserAddressIndicator, 
            String postalDistrict, 
            String postalSector, 
            String postalCode, 
            String planningRegion, 
            String planningArea
    ) {
        this.projectName = projectName;
        this.address = address;
        this.noOfUnits = noOfUnits;
        this.areaSQM = areaSQM;
        this.typeOfArea = typeOfArea;
        this.trasactedPrice = trasactedPrice;
        this.unitPricePSM = unitPricePSM;
        this.unitPricePSF = unitPricePSF;
        this.contractDate = contractDate;
        this.propertyType = propertyType;
        this.tenure = tenure;
        this.completionDate = completionDate;
        this.typeOfSale = typeOfSale;
        this.purchaserAddressIndicator = purchaserAddressIndicator;
        this.postalDistrict = postalDistrict;
        this.postalSector = postalSector;
        this.postalCode = postalCode;
        this.planningRegion = planningRegion;
        this.planningArea = planningArea;
    }
    
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("projectName",projectName);
            obj.put("address",address);
            obj.put("noOfUnits",noOfUnits);
            obj.put("areaSQM",areaSQM);
            obj.put("typeOfArea",typeOfArea);
            obj.put("trasactedPrice",trasactedPrice);
            obj.put("unitPricePSM",unitPricePSM);
            obj.put("unitPricePSF",unitPricePSF);
            obj.put("contractDate",contractDate.toString());
            obj.put("propertyType",propertyType);
            obj.put("tenure",tenure);
            obj.put("completionDate",completionDate);
            obj.put("typeOfSale",typeOfSale);
            obj.put("purchaserAddressIndicator",purchaserAddressIndicator);
            obj.put("postalDistrict",postalDistrict);
            obj.put("postalSector",postalSector);
            obj.put("postalCode",postalCode);
            obj.put("planningRegion",planningRegion);
            obj.put("planningArea",planningArea);
            obj.put("lat",lat);
            obj.put("lng",lng);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
