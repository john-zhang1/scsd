/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.csv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author john
 */
public class CSVLine {
    private final String id;
    private final Map<String, ArrayList> items;
    
    /** ensuring that the order-sensible columns of the csv are processed in the correct order */
    private transient final Comparator<? super String> headerComparator = new Comparator<String>() {
        @Override
        public int compare(String md1, String md2) {
            int compare;
            if (md1 == null && md2 != null) {
                compare = -1;
            }
            else if (md1 != null && md2 == null) {
                compare = 1;
            } else {
                // the order of the rest does not matter
                compare = md1.compareTo(md2);
            }
            return compare;
        }
    };

    public CSVLine(String id) {
        // Store the ID + separator, and initialise the hashtable
        this.id = id;
        items = new TreeMap<>(headerComparator);
    }

    /**
     * Create a new CSV line for a new item
     */
    public CSVLine()
    {
        // Set the ID to be null, and initialise the hashtable
        this.id = null;
        this.items = new TreeMap<>(headerComparator);
    }

    public String getId()
    {
        // Return the string key
        return id;
    }

    /**
     * Add a new metadata value to this line
     *
     * @param key The metadata key (e.g. dc.contributor.author)
     * @param value The metadata value
     */
    public void add(String key, String value)
    {
        // Create the array list if we need to
        if (items.get(key) == null)
        {
            items.put(key, new ArrayList<>());
        }

        // Store the item if it is not null
        if (value != null)
        {
            items.get(key).add(value);
        }
    }

    public void addAll(String key, List<String> value)
    {
        // Create the array list if we need to
        if (items.get(key) == null)
        {
            items.put(key, new ArrayList<>());
        }

        // Store the item if it is not null
        if (value != null)
        {
            items.get(key).addAll(value);
        }
    }

    /** clear all elements under a key
     * @param key */
    public void clear(String key)
    {
        // Create the array list if we need to
        if (items.get(key) != null)
        {
            items.get(key).clear();
        }
    }

    /** remove an entry by key
     * @param key */
    public void remove(String key)
    {
        if (items.get(key) != null)
        {
            items.remove(key);
        }
    }
    /**
     * Get all the values that match the given metadata key. Will be null if none exist.
     *
     * @param key The metadata key
     * @return All the elements that match
     */
    public List<String> get(String key)
    {
        // Return any relevant values
        return items.get(key);
    }

    /**
     * Get all the metadata keys that are represented in this line
     *
     * @return An enumeration of all the keys
     */
    public Set<String> keys()
    {
        return items.keySet();
    }

    public Map<String, ArrayList> getItems()
    {
        return items;
    }

    /**
     * Write this line out as a CSV formatted string, in the order given by the headings provided
     *
     * @param headings The headings which define the order the elements must be presented in
     * @param fieldSeparator separator between metadata fields
     * @param valueSeparator separator between metadata values (within a field)
     * @return The CSV formatted String
     */
    protected String toCSV(List<String> headings, String fieldSeparator, String valueSeparator)
    {
        StringBuilder bits = new StringBuilder();

        // Add the id
        if(id == null)
            bits.append("\"").append(id).append("\"").append(fieldSeparator);

        bits.append(valueToCSV(items.get("collection"),valueSeparator));

        // Add the rest of the elements
        for (String heading : headings)
        {
            bits.append(fieldSeparator);
            List<String> values = items.get(heading);
            if (values != null && !"collection".equals(heading))
            {
                bits.append(valueToCSV(values, valueSeparator));
            }
        }

        return bits.toString();
    }

    /**
     * Internal method to create a CSV formatted String joining a given set of elements
     *
     * @param values The values to create the string from
     * @param valueSeparator value separator
     * @return The line as a CSV formatted String
     */
    protected String valueToCSV(List<String> values, String valueSeparator)
    {
        // Check there is some content
        if (values == null)
        {
            return "";
        }

        // Get on with the work
        String s;
        if (values.size() == 1)
        {
            s = values.get(0);
        }
        else
        {
            // Concatenate any fields together
            StringBuilder str = new StringBuilder();

            for (String value : values)
            {
                if (str.length() > 0)
                {
                    str.append(valueSeparator);
                }

                str.append(value);
            }

            s = str.toString();
        }

        // Replace internal quotes with two sets of quotes
        return "\"" + s.replaceAll("\"", "\"\"") + "\"";
    }    
}
