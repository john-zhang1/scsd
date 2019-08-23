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
import org.apache.commons.io.FileUtils;
import org.csscience.cssaf.service.BatchService;
import org.csscience.cssaf.service.PointJsonService;
import org.csscience.cssaf.service.SAFService;
import org.csscience.cssaf.service.TaxonomyService;
import org.csscience.cssaf.service.ValidateService;
import org.csscience.cssaf.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SAFController {

    private final String csvFileName = "csd.csv";
    private final String csvTaxFileName = "links.csv";
    private final String unzipPhotoDirectory = "photos";
    private String unzipSourceDir = null;

    @Autowired
    private ServletContext context;

    @Autowired
    private ValidateService validateService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private PointJsonService pointJsonService;

    @Autowired
    private SAFService  sAFService;

    @Autowired
    private TaxonomyService taxonomyService;

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String homePage() {
        return "index";
    }

    @RequestMapping(value = { "/saf", "/csvReturn" }, method = RequestMethod.GET)
    public String csvUploadPage() {
        return "csvUpload";
    }

    @RequestMapping(value = {"/zipUpload", "zipReturn"}, method = RequestMethod.GET)
    public String zipUploadPage() {
        return "zipUpload";
    }

    @RequestMapping(value = "/points", method = RequestMethod.GET)
    public String pointsPage(final ModelMap model) {
        boolean next = false;
        model.addAttribute("next", next);
        return "points";
    }

    @RequestMapping(value = "/taxonomies", method = RequestMethod.GET)
    public String taxonomies(final ModelMap model) {
        boolean next = false;
        model.addAttribute("next", next);
        return "taxonomies";
    }

    @RequestMapping(value="/csvFileUpload", method = RequestMethod.POST)
    public String csvFileUpload(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException, Exception {
        String fileType = file.getContentType();
        Map<String, ArrayList> errors = null;
        boolean next = false;

        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator;
        String csvFile = uploadPath + csvFileName;
        FileCopyUtils.copy(file.getBytes(), new File(csvFile));

        if(fileType.equals("text/csv")){
            File f = new File( file.getOriginalFilename());
            file.transferTo(f);
            
            errors = validateService.validateCSV(new File(csvFile));
        }
        if(errors.get("invalidHeadings").isEmpty() && errors.get("invalidZipFormat").isEmpty() && errors.get("zipAddressNotMached").isEmpty()){
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

        if(formatErrors.get("invalidNames").isEmpty() && formatErrors.get("invalidPairs").isEmpty()){
            File unzipDir = new File(unzipSourceDir);
            File unzipPhotoDir = new File(uploadPath + unzipPhotoDirectory);
            if(unzipPhotoDir.exists() && unzipPhotoDir.isDirectory()){
                for(String photoName : photoNames){
                    String photo = uploadPath + photoName;
                    File f = new File(photo);
                    FileUtils.copyFileToDirectory(f, unzipPhotoDir, true);
                }
            }else{
                unzipDir.renameTo(unzipPhotoDir);
            }
            
            next = true;
        }else{
            // Remoce uploaded zip file
            File zipFile = new File(newZipFile);
            zipFile.delete();
        }
        
        model.addAttribute("file", file);
        model.addAttribute("photoNames", photoNames);
        model.addAttribute("formatErrors", formatErrors);
        model.addAttribute("next", next);

        return "zipValidate";
    }

    @RequestMapping(value="/createSAF", method = RequestMethod.GET)
    public String zipFileUpload(final ModelMap model) throws Exception {

        String downloadsDir = "/usr/share/tomcat/webapps/scsd/downloads";
        String newDataSAFDir = downloadsDir + File.separator + "saf-new";
        String exitDataSAFDir = downloadsDir + File.separator + "saf-existing";

        if(CommonUtils.detectOrRenameSAF(exitDataSAFDir)){
            sAFService.createExitingDataSAF();
        }

        if(CommonUtils.detectOrRenameSAF(newDataSAFDir)){
            sAFService.createNewDataSAF();
        }

        String downloadDir = context.getRealPath("") + File.separator + "downloads";    // /usr/share/tomcat/webapps/scsd/downloads
        String zipDir = downloadDir + File.separator + "zip";   // /usr/share/tomcat/webapps/scsd/downloads/zip
        String newSourceDir = downloadDir + File.separator + "saf-new";   // /usr/share/tomcat/webapps/scsd/downloads/saf-new
        String existSourceDir = downloadDir + File.separator + "saf-existing";   // /usr/share/tomcat/webapps/scsd/downloads/saf-existing
        String target_new = zipDir + File.separator + "saf-new.zip";
        String target_exist = zipDir + File.separator + "saf-existing.zip";
        
        File nsd = new File(newSourceDir);
        File esd = new File(existSourceDir);
        if(nsd.list().length > 0){
            batchService.zip(newSourceDir, target_new);
            File newZipFile = new File(target_new);
            model.addAttribute("file_new", newZipFile);
        }
        if(esd.list().length > 0){
            batchService.zip(existSourceDir, target_exist);
            File existZipFile = new File(target_exist);
            model.addAttribute("file_exist", existZipFile);
        }

        return "downloadPage";
    }

    @RequestMapping(value = "/downloads/{fileName}", method = RequestMethod.GET)
    public String downloadSAF( HttpServletRequest request, HttpServletResponse response, 
            @PathVariable("fileName") String fileName, final ModelMap model) throws Exception
    {
        String downloadDir = context.getRealPath("") + File.separator + "downloads";

        if(fileName.equalsIgnoreCase("points")){
            String jsonFile = downloadDir + File.separator + fileName + ".json";
            File file = new File(jsonFile);
            if(file.exists()){
                response.setContentType("application/json");
                response.addHeader("Content-Disposition", "attachment; filename="+fileName);
                response.setContentLength((int) file.length());
                try {
                    FileInputStream fileInputStream = new FileInputStream(jsonFile);
                    OutputStream responseOutputStream = response.getOutputStream();
                    int bytes;
                    while ((bytes = fileInputStream.read()) != -1) {
                        responseOutputStream.write(bytes);
                    }
                } catch (IOException e) {
                }
            }
        }else{
            String zipDir = downloadDir + File.separator + "zip";
            String zipFileName = fileName + ".zip";
            String zipFile = zipDir + File.separator + zipFileName;

            File file = new File(zipFile);
            Path path = Paths.get(zipDir, zipFileName);
            if (Files.exists(path))
            {
                response.setContentType("application/zip");
                response.addHeader("Content-Disposition", "attachment; filename="+fileName);
                response.setContentLength((int) file.length());

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

        return "downloadPage";
    }

    @RequestMapping(value="/checkSaf", method = RequestMethod.GET)
    public String checkExistings(final ModelMap model) throws Exception {

        Map<String, ArrayList> errors = null;
        boolean next = false;
        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator; 
        String csdFileSource = uploadPath + "csd.csv";
        String photoDirectorySource = uploadPath + "photos" + File.separator;
        
        File csdSource = new File(csdFileSource);
        File photoSource = new File(photoDirectorySource);

        if(!(csdSource.exists() && photoSource.exists())){
            return "csvUpload";
        }
        errors = validateService.validateCSV(new File(csdFileSource));
        Map<String, ArrayList> formatErrors = validateService.validatePhotoNameFormat(photoDirectorySource); 

        if(formatErrors.get("invalidNames").isEmpty() && formatErrors.get("invalidPairs").isEmpty()){
            if(errors.get("invalidHeadings").isEmpty() && errors.get("invalidZipFormat").isEmpty() && errors.get("zipAddressNotMached").isEmpty()){
                next = true;
            }
        }
        model.addAttribute("errors", errors);
        model.addAttribute("formatErrors", formatErrors);
        model.addAttribute("next", next);

        return "checkSaf";
    }

    @RequestMapping(value="/generatePoints", method = RequestMethod.POST)
    public String generatePoints(final ModelMap model) {
        boolean next = false;
        next = pointJsonService.writeJsonFile();

        model.addAttribute("next", next);

        return "points";
    }

    @RequestMapping(value="/uploadTax", method = RequestMethod.POST)
    public String uploadTaxonomy(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException, Exception {
        Map<String, ArrayList> errors = null;
        boolean next = false;

        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator;
        String csvFile = uploadPath + csvTaxFileName;
        FileCopyUtils.copy(file.getBytes(), new File(csvFile));

        errors = validateService.validateTaxonomy();
        if(errors.get("invalidInternalCode").isEmpty()){
            next = true;
        }
        model.addAttribute("file", file);
        model.addAttribute("errors", errors);
        model.addAttribute("next", next);
        model.addAttribute("upload", true);

        return "taxonomies";
    }

    @RequestMapping(value="/generateTax", method = RequestMethod.GET)
    public String generateTaxonomy(final ModelMap model) throws Exception {

        boolean generated = taxonomyService.createTaxonomySAF();

        String downloadDir = context.getRealPath("") + File.separator + "downloads";
        String zipDir = downloadDir + File.separator + "zip";
        String sourceDir = downloadDir + File.separator + "saf-taxonomy";
        String target = zipDir + File.separator + "saf-taxonomy.zip";
        
        File nsd = new File(sourceDir);
        if(nsd.list().length > 0){
            batchService.zip(sourceDir, target);
            File newZipFile = new File(target);
            model.addAttribute("file", newZipFile);
        }
        model.addAttribute("generated", generated);

        return "taxonomies";
    }
}
