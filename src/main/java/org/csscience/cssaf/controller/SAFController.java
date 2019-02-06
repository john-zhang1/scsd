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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.csscience.cssaf.service.BatchService;
import org.csscience.cssaf.service.SAFService;
import org.csscience.cssaf.service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.csscience.cssaf.service.ZipcodeService;

@Controller
public class SAFController {
    
    private final String csvFileName = "csd.csv";
    private String unzipSourceDir = null;

    @Autowired
    private ServletContext context;

    @Autowired
    private ValidateService validateService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ZipcodeService zipService;

    @Autowired
    private SAFService  sAFService;

    @RequestMapping(value = "/saf", method = RequestMethod.GET)
    public String csvUploadPage() {
        return "csvUpload";
    }

    @RequestMapping(value = "/zipUpload", method = RequestMethod.GET)
    public String zipUploadPage() {
        return "zipUpload";
    }

    @RequestMapping(value="/csvFileUpload", method = RequestMethod.POST)
    public String csvFileUpload(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException, Exception {
        String fileType = file.getContentType();
        Map<String, ArrayList> errors = null;
        boolean next = false;

        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator;
        File path = new File(uploadPath);
        String ncsvFile = uploadPath + csvFileName;
        FileCopyUtils.copy(file.getBytes(), new File(ncsvFile));

        if(fileType.equals("text/csv")){
            File f = new File( file.getOriginalFilename());
            file.transferTo(f);
            
            errors = validateService.validateCSV(f);
        }
        if(errors.get("invalidHeadings").size()==0 && errors.get("invalidZipFormat").size()==0){
            next = true;
        }
        model.addAttribute("file", file);
        model.addAttribute("errors", errors);
        model.addAttribute("next", next);

        return "csvValidate";
    }

    @RequestMapping(value="/zipFileUpload", method = RequestMethod.POST)
    public String zipFileUpload(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException, Exception {
        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator;
        File path = new File(uploadPath);
        String zipFileName = UUID.randomUUID().toString() + ".zip";
        String newZipFile = uploadPath + zipFileName;
        FileCopyUtils.copy(file.getBytes(), new File(newZipFile));
        boolean next = false;

        unzipSourceDir = batchService.unzip(uploadPath, zipFileName, uploadPath);
        List<String> photoNames = batchService.listTempWorkDirFiles(newZipFile);
        Map<String, ArrayList> formatErrors = validateService.validatePhotoNameFormat(unzipSourceDir);        

        if(formatErrors.get("invalidNames").size()==0 && formatErrors.get("invalidPairs").size()==0){
            next = true;
        }
        
        model.addAttribute("file", file);
        model.addAttribute("photoNames", photoNames);
        model.addAttribute("formatErrors", formatErrors);
        model.addAttribute("next", next);

        return "zipValidate";
    }

    @RequestMapping(value="/createSAF", method = RequestMethod.GET)
    public String zipFileUpload(final ModelMap model) throws Exception {
        
        sAFService.createNewDataSAF();
        sAFService.createExitingDataSAF();

        String downloadDir = context.getRealPath("") + File.separator + "downloads";    // /usr/share/tomcat/webapps/scsd/downloads
        String zipDir = downloadDir + File.separator + "zip";   // /usr/share/tomcat/webapps/scsd/downloads/zip
        String newSourceDir = downloadDir + File.separator + "saf-new";   // /usr/share/tomcat/webapps/scsd/downloads/saf-new
        String existSourceDir = downloadDir + File.separator + "saf-existing";   // /usr/share/tomcat/webapps/scsd/downloads/saf-existing
        String target_new = zipDir + File.separator + "saf-new.zip";
        String target_exist = zipDir + File.separator + "saf-existing.zip";
        batchService.zip(newSourceDir, target_new);
        batchService.zip(existSourceDir, target_exist);

        File newZipFile = new File(target_new);
        File existZipFile = new File(target_exist);

        model.addAttribute("file_new", newZipFile);
        model.addAttribute("file_exist", existZipFile);

        return "downloadPage";
    }

    @RequestMapping(value = "/downloads/{fileName}", method = RequestMethod.GET)
    public String downloadSAF( HttpServletRequest request, HttpServletResponse response, 
            @PathVariable("fileName") String fileName, final ModelMap model) throws Exception
    {
        String downloadDir = context.getRealPath("") + File.separator + "downloads";
        String zipDir = downloadDir + File.separator + "zip";
        String zipFileName = fileName + ".zip";
        String zipFile = zipDir + File.separator + zipFileName;

        Path file = Paths.get(zipDir, zipFileName);
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
//        model.addAttribute("file", zipFile);
        return "downloadPage";
    }
}
