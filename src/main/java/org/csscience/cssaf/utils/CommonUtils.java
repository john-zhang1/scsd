/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.csscience.cssaf.app.saf.SAF;

/**
 *
 * @author john
 */
public class CommonUtils {
    public static void createDirectory(String directory){
        File dir = new File(directory);
        dir.mkdirs();
    }

    public static void copyFile(Path src, Path dest) {
        try {
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Convert zip code as Decimal Format "00000"*/
    public static String convertZip(int zipcode) {
        DecimalFormat format = new DecimalFormat("00000");
        return format.format(zipcode);
    }

    public static List<String> getMetadataElements(String key){
        List<String> elements = new ArrayList<>(Arrays.asList(key.split("\\.")));
        return elements;
    }

    public static void writeFile(String name, String content)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(name);
            OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
            try (PrintWriter out = new PrintWriter(osr)) {
                out.print(content);
            }
        } catch (IOException e)
        {
            Logger.getLogger(SAF.class.getName()).log(Level.SEVERE, null, e);
            System.exit(0);
        }
    }
    
    public static boolean ifDirectoryExists(File f){
        return Files.exists(f.toPath());
    }
}
