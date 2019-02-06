/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.csscience.cssaf.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FileDownloadController {

    @Autowired
    private BatchService batchService;

    
    @RequestMapping(value = "/downloads/zip/{fileName}", method = RequestMethod.GET)
    public void downloadSAF( HttpServletRequest request, HttpServletResponse response, 
            @PathVariable("fileName") String fileName) throws Exception
    {
        String dataDirectory = request.getServletContext().getRealPath("/downloads/");
        String zipDir = dataDirectory + File.separator + "zip";
        String sourceDir = dataDirectory + File.separator + "images";
        String target = zipDir + File.separator + fileName;
        batchService.zip(sourceDir, target);

        File zipFile = new File(target);
        Path file = Paths.get(zipDir, fileName);
        if (Files.exists(file))
        {
            response.setContentType("application/zip");
            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
            response.setContentLength((int) zipFile.length());

            try {
                FileInputStream fileInputStream = new FileInputStream(zipFile);
                OutputStream responseOutputStream = response.getOutputStream();
                int bytes;
                while ((bytes = fileInputStream.read()) != -1) {
                    responseOutputStream.write(bytes);
                }
            } catch (IOException e) {
            }            
        }
    }
}
