/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import com.google.gson.Gson;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.csscience.cssaf.app.marker.PointJson;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSVService;
import org.csscience.cssaf.service.PointJsonService;
import org.csscience.cssaf.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointJsonServiceImpl implements PointJsonService {

    public final String jsonFile = "/usr/share/tomcat/webapps/scsd/downloads/points.json";

    @Autowired
    private CSVService cSVService;

    @Override
    public boolean writeJsonFile() {

        boolean success = false;
        List<CSVLine> collectionData = cSVService.getCollectionData();

        try
        {
            FileOutputStream fos = new FileOutputStream(jsonFile);
            OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
            PrintWriter out = new PrintWriter(osr);
            out.print("{\"points\": [");
            
            CSVLine firstLine = collectionData.get(0);
            CSVLine firstCsv = getPoint(firstLine);
            PointJson.Point firstPoint = new PointJson.Point(firstCsv.getItems());
            String firstPointString = toJson(firstPoint);
            out.print(firstPointString);
            collectionData.remove(0);
           
            for(CSVLine line : collectionData)
            {
                CSVLine csv = getPoint(line);
                Map<String, ArrayList> items = csv.getItems();
                PointJson.Point point = new PointJson.Point(items);
                String pointString = toJson(point);
                pointString = "," + pointString;
                out.print(pointString);
            }
            
            out.print("]}");
            out.close();
            success = true;
        } catch (IOException e)
        {
            Logger.getLogger(PointJson.class.getName()).log(Level.SEVERE, null, e);
            System.out.println(e);
            System.exit(1);
        }
        return success;
    }

    @Override
    public CSVLine getPoint(CSVLine line) {
        String spatial = "dwc.npdg.spatial";
        String internalcode = "dwc.npdg.internalcode";
        String uri = "dc.identifier.uri";
        String city = "dwc.npdg.homecity";
        String state = "dwc.npdg.homestate";
        String zip = "dwc.npdg.homezip";
        CSVLine point = null;
        CSVLine csv = CommonUtils.mergeColumns(line);
        Map<String, ArrayList> items = csv.getItems();
        
        point = new CSVLine(csv.getId());
        point.add(uri, csv.get(uri).get(0));
        point.add(zip, csv.get(zip).get(0));
        point.add(spatial, csv.get(spatial).get(0));
        
        String title = null;
        // If internal code doesn't exist, use sample id instead
        if(csv.get(internalcode).size() > 0){
            title = csv.get(internalcode).get(0);
        }else{
            title = csv.getId();
        }

        point.add(internalcode, title);
        String place = csv.get(city).get(0) + ", " + csv.get(state).get(0).split(" - ")[1];
        if(place !=null && title != null && uri != null && spatial != null)
            point.add("place", place);
        
        return point;
    }

    /** Get point data as string for print */
    public String toJson(PointJson.Point point) {
        Gson gson = new Gson();
        return gson.toJson(point);
    }

    public static final class Point {
        private String spatial;
        private String title;
        private String uri;
        private String place;
        private String zip;
        
        public Point(Map<String, ArrayList> items) {
            setSpatial((String)items.get("dwc.npdg.spatial").get(0));
            setTitle((String)items.get("dwc.npdg.internalcode").get(0));
            setUri((String)items.get("dc.identifier.uri").get(0));
            setPlace((String)items.get("place").get(0));
            setZip((String)items.get("dwc.npdg.homezip").get(0));
        }

        public String getSpatial() {
            return spatial;
        }

        public void setSpatial(String spatial) {
            this.spatial = spatial;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            if(uri != null || uri.equals("")){
                if(uri.contains("http:")){
                    String replace = uri.replace("http:", "https:");
                    uri = replace;
                }
            }
            this.uri = uri;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }
    }    
}
