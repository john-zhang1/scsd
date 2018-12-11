/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author john
 */
public class CSV {
    /** The headings of the CSV file */
    protected List<String> headings;

    /** An array list of CSV lines */
    public List<CSVLine> lines;

    /** A counter of how many CSV lines this object holds */
    protected int counter;

    /** The value separator (defaults to double pipe '||') */
    protected String valueSeparator;

    /** The value separator in an escaped form for using in regexes */
    protected String escapedValueSeparator;

    /** The field separator (defaults to comma) */
    protected String fieldSeparator;

    /** The field separator in an escaped form for using in regexes */
    protected String escapedFieldSeparator;
    
    /** A list of metadata elements to ignore */
    protected Map<String, String> ignore;
    
    /** Remember the position of primary key */
    private int keyPos;

    /**
     * Create a new instance of a CSV line holder
     */
    public CSV()
    {
        init();
    }

    /**
     * Create a new instance, reading the lines in from file
     *
     * @param f The file to read from
     * @param c The Context
     *
     * @throws Exception thrown if there is an error reading or processing the file
     */
    public CSV(File f) throws Exception
    {
        init();
        readFile(f);
    }


    /**
     * Initialise this class with values from dspace.cfg
     */
    protected void init()
    {
        // Set the value separator
        setValueSeparator();

        // Set the field separator
        setFieldSeparator();

        // Create the headings
        headings = new ArrayList<>();

        // Create the blank list of items
        lines = new ArrayList<>();

        // Initialise the counter
        counter = 0;

        // Set the metadata fields to ignore
        ignore = new HashMap<>();

        // Specify default values
        String[] defaultValues = new String[]{"dc.date.accessioned, dc.date.available, " +
                                              "dc.date.updated, dc.description.provenance"};
        for (String toIgnoreString : defaultValues)
        {
            String[] ignoredStrings = toIgnoreString.split("\\,");
            for(String ignoredString : ignoredStrings)
            {
                if (!"".equals(ignoredString.trim()))
                {
                    ignore.put(ignoredString.trim(), ignoredString.trim());
                }
            }
        }
    }

    /** Processing csv file */
    private void readFile(File f) throws FileNotFoundException, UnsupportedEncodingException, IOException, Exception{
       // Open the CSV file
       BufferedReader input = null;
       try{
           input = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
           // Read the heading line
           String head = input.readLine();
           String[] headingElements = head.split(escapedFieldSeparator);

           String keyColumn = null;
           String fileName = f.getName();
           if(fileName.equals("collection.csv")){
               keyColumn = "dwc.npdg.sampleid";
           }else if(fileName.equals("csd.csv")){
               keyColumn = "dwc.npdg.sampleid";
           }else if(fileName.equals("states.csv")){
               keyColumn = "short";
           }else if(fileName.equals("zipcode.csv")){
               keyColumn = "zip";
           }

           int i = 0;
           keyPos = 0;
            for (String element : headingElements)
            {
                // Remove surrounding quotes if there are any
                if ((element.startsWith("\"")) && (element.endsWith("\"")))
                {
                    element = element.substring(1, element.length() - 1);
                }

                // Store the heading
                headings.add(element);

                if(fileName.equals("csd.csv"))
                    element = convertMetadata(element);
                if(element.contains(keyColumn)){
                    keyPos = i;
                }
                headings.set(i, element);
                i++;
            }

            // Read each subsequent line
            StringBuilder lineBuilder = new StringBuilder();
            String lineRead;

            while ((lineRead = input.readLine()) != null)
            {
                if (lineBuilder.length() > 0) {
                    // Already have a previously read value - add this line
                    lineBuilder.append("\n").append(lineRead);

                    // Count the number of quotes in the buffer
                    int quoteCount = 0;
                    for (int pos = 0; pos < lineBuilder.length(); pos++) {
                        if (lineBuilder.charAt(pos) == '"') {
                            quoteCount++;
                        }
                    }

                    if (quoteCount % 2 == 0) {
                        // Number of quotes is a multiple of 2, add the item
                        addItem(lineBuilder.toString());
                        lineBuilder = new StringBuilder();
                    }
                } else if (lineRead.indexOf('"') > -1) {
                    // Get the number of quotes in the line
                    int quoteCount = 0;
                    for (int pos = 0; pos < lineRead.length(); pos++) {
                        if (lineRead.charAt(pos) == '"') {
                            quoteCount++;
                        }
                    }

                    if (quoteCount % 2 == 0) {
                        // Number of quotes is a multiple of 2, add the item
                        addItem(lineRead);
                    } else {
                        // Uneven quotes - add to the buffer and leave for later
                        lineBuilder.append(lineRead);
                    }
                } else {
                    // No previously read line, and no quotes in the line - add item
                    addItem(lineRead);
                }
            }
       }
       finally
       {
            if (input != null)
            {
                input.close();
            }
       }        
    }

    private void setFieldSeparator()
    {
        // Get the value separator
        fieldSeparator = ",";
        if ((fieldSeparator != null) && (!"".equals(fieldSeparator.trim())))
        {
            fieldSeparator = fieldSeparator.trim();
            if ("tab".equals(fieldSeparator))
            {
                fieldSeparator = "\t";
            }
            else if ("semicolon".equals(fieldSeparator))
            {
                fieldSeparator = ";";
            }
            else if ("hash".equals(fieldSeparator))
            {
                fieldSeparator = "#";
            }
            else
            {
                fieldSeparator = fieldSeparator.trim();
            }
        }
        else
        {
            fieldSeparator = ",";
        }

        // Now store the escaped version
        Pattern spchars = Pattern.compile("([\\\\*+\\[\\](){}\\$.?\\^|])");
        Matcher match = spchars.matcher(fieldSeparator);
        escapedFieldSeparator = match.replaceAll("\\\\$1");
    }

    private void setValueSeparator()
    {
        // Get the value separator
        valueSeparator = "||";
        if ((valueSeparator != null) && (!"".equals(valueSeparator.trim())))
        {
            valueSeparator = valueSeparator.trim();
        }
        else
        {
            valueSeparator = "||";
        }

        // Now store the escaped version
        Pattern spchars = Pattern.compile("([\\\\*+\\[\\](){}\\$.?\\^|])");
        Matcher match = spchars.matcher(valueSeparator);
        escapedValueSeparator = match.replaceAll("\\\\$1");
    }

    public final void addItem(String line) throws Exception
    {
        // Check if the line is empty
        if(line.trim().split(",").length > 0){
            // Check to see if the last character is a field separator, which hides the last empty column
            boolean last = false;
            if (line.endsWith(fieldSeparator))
            {
                // Add a space to the end, then remove it later
                last = true;
                line += " ";
            }

            // Split up on field separator
            String[] parts = line.split(escapedFieldSeparator);
            ArrayList<String> bits = new ArrayList<>();
            bits.addAll(Arrays.asList(parts));

            // Merge parts with embedded separators
            boolean alldone = false;
            while (!alldone)
            {
                boolean found = false;
                int i = 0;
                for (String part : bits)
                {
                    int bitcounter = part.length() - part.replaceAll("\"", "").length();
                    if ((part.startsWith("\"")) && ((!part.endsWith("\"")) || ((bitcounter & 1) == 1)))
                    {
                        found = true;
                        String add = bits.get(i) + fieldSeparator + bits.get(i + 1);
                        bits.remove(i);
                        bits.add(i, add);
                        bits.remove(i + 1);
                        break;
                    }
                    i++;
                }
                alldone = !found;
            }

            // Deal with quotes around the elements
            int i = 0;
            for (String part : bits)
            {
                if ((part.startsWith("\"")) && (part.endsWith("\"")))
                {
                    part = part.substring(1, part.length() - 1);
                    bits.set(i, part);
                }
                i++;
            }

            // Remove embedded quotes
            i = 0;
            for (String part : bits)
            {
                if (part.contains("\"\""))
                {
                    part = part.replaceAll("\"\"", "\"");
                    bits.set(i, part);
                }
                i++;
            }

            // Add elements to a CSVLine
            String id = null;

            id = bits.get(keyPos).replaceAll("\"", "");

            CSVLine csvLine;

            // Is this an existing item, or a new item (where id = '+')
            if ("+".equals(id))
            {
                csvLine = new CSVLine();
            }
            else
            {
                try
                {
                    csvLine = new CSVLine(id);
                }
                catch (NumberFormatException nfe)
                {
                    System.err.println("Invalid item identifier: " + id);
                    System.err.println("Please check your CSV file for information. " +
                                       "Item id must be numeric, or a '+' to add a new item");
                    throw(nfe);
                }
            }

            // Add the rest of the parts
            i = 0;
            for (String part : bits)
            {
                // Is this a last empty item?
                if ((last) && (i == headings.size()))
                {
                    part = "";
                }

                // Make sure we register that this column was there
                if (headings.size() < i) {
                    throw new Exception("error with out of range");
                }
                csvLine.add(headings.get(i), null);
                String[] elements = part.split(escapedValueSeparator);
                for (String element : elements)
                {
                    if ((element != null) && (!"".equals(element)))
                    {
                        csvLine.add(headings.get(i), element);
                    }
                }
                i++;
            }

            lines.add(csvLine);
            counter++;
        }
    }

    protected final String convertMetadata(String head){
        String metadataString = null;
        head = head.trim();
        if((head != null) && !"".equals(head))
        {
            if(StringUtils.contains(head, "Internal ID"))
                metadataString = "dwc.npdg.internalcode";
            else if(StringUtils.contains(head, "Sample ID"))
                metadataString = "dwc.npdg.sampleid";
            else if(StringUtils.contains(head, "Date Collected"))
                metadataString = "dwc.npdg.datecollected";
            else if(StringUtils.contains(head, "City"))
                metadataString = "dwc.npdg.homecity";
            else if(StringUtils.contains(head, "State"))
                metadataString = "dwc.npdg.homestate";
            else if(StringUtils.contains(head, "Zip"))
                metadataString = "dwc.npdg.homezip";
            else if(StringUtils.contains(head, "Screen Status"))
                metadataString = "screenstatus";
            else if(StringUtils.contains(head, "# of isolates from RBM"))
                metadataString = "dwc.npdg.isolatesRBM";
            else if(StringUtils.contains(head, "# of isolates from TV8"))
                metadataString = "dwc.npdg.isolatesTV8";
            else if(StringUtils.contains(head, "Collection Detail"))
                metadataString = "dwc.npdg.detail";
            else if(StringUtils.contains(head, "LIB"))
                metadataString = "lib";
            else if(StringUtils.contains(head, "Photo"))
                metadataString = "dwc.npdg.imagestatus";
            else
                metadataString = head;
        }
        
        return metadataString;
    }    
}
