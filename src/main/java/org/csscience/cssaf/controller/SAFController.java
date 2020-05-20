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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.io.FileUtils;
import org.csscience.cssaf.content.Zip;
import org.csscience.cssaf.service.BatchService;
import org.csscience.cssaf.service.PointJsonService;
import org.csscience.cssaf.service.SAFService;
import org.csscience.cssaf.service.TaxonomyService;
import org.csscience.cssaf.service.ValidateService;
import org.csscience.cssaf.service.ZipcodeService;
import org.csscience.cssaf.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SAFController {

    private final String csvTaxFileName = "links.csv";
    private final String unzipPhotoDirectory = "photos";
    private String unzipSourceDir = null;
    private Map<String, String> nextSteps = new HashMap<>();
    private Map<String, String> taxNextSteps = new HashMap<>();
    private Map<String, ArrayList> collectedErrors = new HashMap<>();
    private Map<String, ArrayList> taxErrors = new HashMap<>();
    private Set<String> photoHiddenErrors = new HashSet<String>();
    private boolean isEdit = false;
    private String sessionID;

    @Autowired
    private ServletContext context;

    @Autowired
    private ValidateService validateService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private PointJsonService pointJsonService;

    @Autowired
    private SAFService sAFService;

    @Autowired
    private TaxonomyService taxonomyService;

    @Autowired
    private ZipcodeService zipService;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String homePage() {
        return "index";
    }

    @RequestMapping(value = {"/csProcess"}, method = RequestMethod.GET)
    public String csProcessPage() {
        nextSteps = new HashMap<>();
        clearErrors("csd");
        clearErrors("photos");
        ClearHiddenErrors();
        return "cs/home";
    }

    @RequestMapping(value = {"/csdReupload"}, method = RequestMethod.GET)
    public String csdReuploadPage(final ModelMap model) {
        nextSteps.remove("csd");
        clearErrors("csd");
        model.addAttribute("errors", collectedErrors);
        model.addAttribute("next", nextSteps);

        return "cs/home";
    }

    @RequestMapping(value = {"/linkReupload"}, method = RequestMethod.GET)
    public String linkReuploadPage(final ModelMap model) {
        taxNextSteps.remove("link");
        clearTaxErrors();
        model.addAttribute("taxNext", taxNextSteps);
        return "cs/taxonomy";
    }

    @RequestMapping(value = { "/addZipCode" }, method = RequestMethod.POST)
    public String addZipCodePage(@Valid @ModelAttribute("zip") Zip zip, BindingResult result, ModelMap model){
        Map<String, String> alert = new HashMap<>();

        String zipString = zip.getZip().trim();

        zipString = zipString.split("-")[0];

            if(!CommonUtils.validateZipcode(zipString)) {
                FieldError zipError =new FieldError("zip", "zip", messageSource.getMessage("Invalid.zip.zip", new String[]{zip.getZip()}, Locale.getDefault()));
                result.addError(zipError);
            }
            if("".equals(zip.getCity().trim())) {
                FieldError cityError =new FieldError("zip", "city", messageSource.getMessage("NotNull.zip.city", new String[]{zip.getCity()}, Locale.getDefault()));
                result.addError(cityError);
            }
            if("".equals(zip.getShortState().trim()) || zip.getShortState().trim().length() != 2) {
                FieldError stateError =new FieldError("zip", "shortState", messageSource.getMessage("Invalid.zip.shortState", new String[]{zip.getShortState()}, Locale.getDefault()));
                result.addError(stateError);
            }
            if(!CommonUtils.validateLatitude(String.valueOf(zip.getLatitude()))) {
                FieldError latError =new FieldError("zip", "latitude", messageSource.getMessage("Invalid.zip.latitude", new String[]{String.valueOf(zip.getLatitude())}, Locale.getDefault()));
                result.addError(latError);
            }
            if(!CommonUtils.validateLongitude(String.valueOf(zip.getLongitude()))) {
                FieldError lonError =new FieldError("zip", "longitude", messageSource.getMessage("Invalid.zip.longitude", new String[]{String.valueOf(zip.getLongitude())}, Locale.getDefault()));
                result.addError(lonError);
            }
        
        if(zip.getId() == null) {
            if(!"".equals(zipString) && !zipService.isZipcodeUnique(zipString)) {
                FieldError zipError =new FieldError("zip", "zip", messageSource.getMessage("non.unique.zip", new String[]{zip.getZip()}, Locale.getDefault()));
                result.addError(zipError);
            }

            if(result.hasErrors()){
                alert.put("warning", "Error binding");
                model.addAttribute("alert", alert);
                model.addAttribute("errors", collectedErrors);
                model.addAttribute("next", nextSteps);
                return "cs/home";
            }

            zip.setZip(Integer.valueOf(zipString).toString());
            zipService.saveZip(zip);
            alert.put("success", "zip code " + adjustZipcode(zip.getZip()) + " has been added sucessfully");

        } else {
            zipService.updateZip(zip);
            alert.put("success", "zip code " + adjustZipcode(zip.getZip()) + " has been updated sucessfully");
        }

        model.addAttribute("zip", adjustZip(zip));
        model.addAttribute("alert", alert);
        model.addAttribute("errors", collectedErrors);
        model.addAttribute("next", nextSteps);

        return "cs/home";
    }


    @RequestMapping(value = {"/newZipCode"}, method = RequestMethod.GET)
    public String newZipCodePage(final ModelMap model) {
        model.addAttribute("zip", new Zip());
        model.addAttribute("errors", collectedErrors);
        model.addAttribute("next", nextSteps);

        return "cs/home";
    }
    
    @RequestMapping(value = {"/zipReupload"}, method = RequestMethod.GET)
    public String zipReuploadPage(final ModelMap model) {
        clearErrors("photos");
        nextSteps.remove("photos");

        if(collectedErrors.get("csvErrorFlag") != null && collectedErrors.get("csvErrorFlag").size() > 0) {
            // Add zipcode form
            Zip zip = new Zip();
            model.addAttribute("zip", zip);
        }

        model.addAttribute("errors", collectedErrors);
        model.addAttribute("next", nextSteps);

        return "cs/home";
    }

    @RequestMapping(value = {"/saf", "/csvReturn"}, method = RequestMethod.GET)
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

    @RequestMapping(value = "/csTaxonomy", method = RequestMethod.GET)
    public String taxonomyPage(final ModelMap model) {
        taxNextSteps = new HashMap<>();
        clearTaxErrors();
        return "cs/taxonomy";
    }

    @RequestMapping(value = "/csvFileUpload", method = RequestMethod.POST)
    public String csvFileUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") final MultipartFile file, @RequestParam("uploadFileName") final String fileName, @RequestParam("sessionid") final String sessionid, final ModelMap model) throws IOException, Exception {
        sessionID = request.getSession().getId();
        Map<String, ArrayList> csdErrors = null;
        if(file.getBytes().length > 0) {
            String fileType = file.getContentType();

            String uploadPath = context.getRealPath("") + "/data/" + sessionid + File.separator;
            File sessionDir = new File(uploadPath);

            if(!sessionDir.isDirectory()) {
               sessionDir.mkdirs(); 
            }

            String csvFile = uploadPath + fileName;
            FileCopyUtils.copy(file.getBytes(), new File(csvFile));

            if (fileType.equals("text/csv")) {
                File f = new File(file.getOriginalFilename());
                file.transferTo(f);
                if (fileName.startsWith("csd")) {
                    nextSteps.remove("csd");
                    clearErrors("csd");
                    csdErrors = validateService.getRowsWithErrors(new File(csvFile));
                    if (csdErrors.isEmpty()) {
                        nextSteps.put("csd", "csd");
                    } else {
                        collectedErrors.putAll(csdErrors);
                    }
                }
                if (fileName.startsWith("collection")) {
                    nextSteps.put("collection", "collection");
                }
            }
        }
        if(collectedErrors.get("csvErrorFlag") != null && collectedErrors.get("csvErrorFlag").size() > 0) {
            // Add zipcode form
            Zip zip = new Zip();
            model.addAttribute("zip", zip);
        }
        model.addAttribute("file", file);
        model.addAttribute("errors", collectedErrors);
        model.addAttribute("next", nextSteps);

        return "cs/home";
    }

    @RequestMapping(value = "/taxCsvFileUpload", method = RequestMethod.POST)
    public String taxCsvFileUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") final MultipartFile file, @RequestParam("uploadFileName") final String fileName, @RequestParam("sessionid") final String sessionid, final ModelMap model) throws IOException, Exception {
        sessionID = request.getSession().getId();
        if(file.getBytes().length > 0) {
            String fileType = file.getContentType();

            String uploadPath = context.getRealPath("") + "/data/" + sessionid + File.separator;
            File sessionDir = new File(uploadPath);

            if(!sessionDir.isDirectory()) {
               sessionDir.mkdirs(); 
            }

            String csvFile = uploadPath + fileName;
            FileCopyUtils.copy(file.getBytes(), new File(csvFile));

            if (fileType.equals("text/csv")) {
                File f = new File(file.getOriginalFilename());
                file.transferTo(f);
                taxNextSteps.remove("link");

                if (fileName.startsWith("link")) {
                    Map<String, ArrayList> csdErrors = null;
                    clearTaxErrors();
                    csdErrors = validateService.getRowsWithTaxErrors(sessionID);
                    if (csdErrors.size() == 0) {
                        taxNextSteps.put("link", "link");
                    } else {
                        taxErrors.putAll(csdErrors);
                    }
                }
                if (fileName.startsWith("taxCollection")) {
                    taxNextSteps.put("collection", "collection");
                }
            }
        }

        model.addAttribute("file", file);
        model.addAttribute("taxErrors", taxErrors);
        model.addAttribute("taxNext", taxNextSteps);

        return "cs/taxonomy";
    }

    @RequestMapping(value = "/zipFileUpload", method = RequestMethod.POST)
    public String zipFileUpload(@RequestParam("file") final MultipartFile file, @RequestParam("uploadFileName") final String fileName, @RequestParam("sessionid") final String sessionid, final ModelMap model) throws IOException, Exception {
        if(file.getBytes().length > 0) {
            String fileType = file.getContentType();

            String uploadPath = context.getRealPath("") + File.separator + "data/" + sessionid + File.separator;
            File sessionDir = new File(uploadPath);

            if (!sessionDir.isDirectory()) {
                sessionDir.mkdirs();
            }

            String newZipFile = uploadPath + fileName;
            FileCopyUtils.copy(file.getBytes(), new File(newZipFile));
            Map<String, String> photoErrors = new HashMap<>();
            nextSteps.remove("photos");
            clearErrors("photos");

            if (fileType.equals("application/zip")) {
                unzipSourceDir = batchService.unzip(uploadPath, fileName, uploadPath);
                List<String> photoNames = batchService.listTempWorkDirFiles(newZipFile);
                Map<String, ArrayList> formatErrors = validateService.validatePhotoNameFormat(unzipSourceDir);

                File unzipDir = new File(unzipSourceDir);
                File unzipPhotoDir = new File(uploadPath + unzipPhotoDirectory);

                if(!unzipPhotoDir.isDirectory()) {
                    unzipDir.renameTo(unzipPhotoDir);
                } else {
                    for(String photoName : photoNames){
                        String photo = uploadPath + photoName;
                        File f = new File(photo);
                        FileUtils.copyFileToDirectory(f, unzipPhotoDir, true);
                    }
                }

                if (formatErrors.get("invalidNames").isEmpty() && formatErrors.get("invalidPairs").isEmpty()) {
                    nextSteps.put("photos", "photos");
                } else {
                    List<String> invalidNames = formatErrors.get("invalidNames");
                    List<String> invalidPairs = formatErrors.get("invalidPairs");
                    for (String invalidName : invalidNames) {
                        String key = invalidName.replaceAll("\\s", "");
                        photoErrors.put(key, invalidName);
                    }
                    for (String invalidPair : invalidPairs) {
                        String key = invalidPair.replaceAll("\\s", "");
                        photoErrors.put(key, invalidPair);
                    }
                    List<String> invalidPhotoNames = new ArrayList<>();
                    for (String key : photoErrors.keySet()) {
                        invalidPhotoNames.add(photoErrors.get(key));
                    }
                    collectedErrors.put("photos", (ArrayList) invalidPhotoNames);
                }

//                if(!photoHiddenErrors.isEmpty()) {
//                    mergeHiddenErrors();
//                }

                File zipFile = new File(newZipFile);
                zipFile.delete();
                FileUtils.deleteDirectory(unzipDir);
            }
        }
        if(collectedErrors.get("csvErrorFlag") != null && collectedErrors.get("csvErrorFlag").size() > 0) {
            // Add zipcode form
            Zip zip = new Zip();
            model.addAttribute("zip", zip);
        }

        model.addAttribute("file", file);
        model.addAttribute("errors", collectedErrors);
        model.addAttribute("next", nextSteps);

        return "cs/home";
    }

    @RequestMapping(value = "/createSAF", method = RequestMethod.GET)
    public String createSAF(final ModelMap model) throws Exception {

        String downloadDir = context.getRealPath("") + File.separator + "data/" + sessionID + "/downloads";    // /usr/share/tomcat/webapps/scsd/data/sessionID/downloads
        String newDataSAFDir = downloadDir + File.separator + "saf-new";
        String exitDataSAFDir = downloadDir + File.separator + "saf-existing";

        if (CommonUtils.detectOrRenameSAF(exitDataSAFDir)) {
            sAFService.createExitingDataSAF(sessionID);
        }

        if (CommonUtils.detectOrRenameSAF(newDataSAFDir)) {
            sAFService.createNewDataSAF(sessionID);
        }

        String zipDir = downloadDir + File.separator + "zip";   // /usr/share/tomcat/webapps/scsd/sessionID/downloads/zip
        String newSourceDir = downloadDir + File.separator + "saf-new";   // /usr/share/tomcat/webapps/scsd/sessionID/downloads/saf-new
        String existSourceDir = downloadDir + File.separator + "saf-existing";   // /usr/share/tomcat/webapps/scsd/sessionID/downloads/saf-existing
        String target_new = zipDir + File.separator + "saf-new.zip";
        String target_exist = zipDir + File.separator + "saf-existing.zip";

        File zipPath = new File(zipDir);

        if(!zipPath.isDirectory()) {
           zipPath.mkdirs(); 
        }

        File nsd = new File(newSourceDir);
        File esd = new File(existSourceDir);
        if (nsd.list().length > 0) {
            batchService.zip(newSourceDir, target_new);
            File newZipFile = new File(target_new);
            model.addAttribute("file_new", newZipFile);
        }
        if (esd.list().length > 0) {
            batchService.zip(existSourceDir, target_exist);
            File existZipFile = new File(target_exist);
            model.addAttribute("file_exist", existZipFile);
        }

        return "cs/download";
    }

    @RequestMapping(value = "/downloads/{fileName}", method = RequestMethod.GET)
    public String downloadSAF(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("fileName") String fileName, final ModelMap model) throws Exception {
        String downloadDir = context.getRealPath("") + File.separator + "data/" + sessionID + "/downloads";

        if (fileName.equalsIgnoreCase("points")) {
            String jsonFile = downloadDir + File.separator + fileName + ".json";
            File file = new File(jsonFile);
            if (file.exists()) {
                response.setContentType("application/json");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
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
        } else {
            String zipDir = downloadDir + File.separator + "zip";
            String zipFileName = fileName + ".zip";
            String zipFile = zipDir + File.separator + zipFileName;

            File file = new File(zipFile);
            Path path = Paths.get(zipDir, zipFileName);
            if (Files.exists(path)) {
                response.setContentType("application/zip");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
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

        return "cs/download";
    }

    @RequestMapping(value = "/checkSaf", method = RequestMethod.GET)
    public String checkExistings(final ModelMap model) throws Exception {

        Map<String, ArrayList> errors = null;
        boolean next = false;
        String uploadPath = context.getRealPath("") + File.separator + "data" + File.separator;
        String csdFileSource = uploadPath + "csd.csv";
        String photoDirectorySource = uploadPath + "photos" + File.separator;

        File csdSource = new File(csdFileSource);
        File photoSource = new File(photoDirectorySource);

        if (!(csdSource.exists() && photoSource.exists())) {
            return "csvUpload";
        }
        errors = validateService.validateCSV(new File(csdFileSource));
        Map<String, ArrayList> formatErrors = validateService.validatePhotoNameFormat(photoDirectorySource);

        if (formatErrors.get("invalidNames").isEmpty() && formatErrors.get("invalidPairs").isEmpty()) {
            if (errors.get("invalidHeadings").isEmpty() && errors.get("invalidZipFormat").isEmpty() && errors.get("zipAddressNotMached").isEmpty()) {
                next = true;
            }
        }
        model.addAttribute("errors", errors);
        model.addAttribute("formatErrors", formatErrors);
        model.addAttribute("next", next);

        return "checkSaf";
    }

    @RequestMapping(value = "/generatePoints", method = RequestMethod.POST)
    public String generatePoints(final ModelMap model) {
        boolean next = false;
        next = pointJsonService.writeJsonFile();

        model.addAttribute("next", next);

        return "points";
    }

    @RequestMapping(value = "/uploadTax", method = RequestMethod.POST)
    public String uploadTaxonomy(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException, Exception {
        Map<String, ArrayList> errors = null;
        boolean next = false;

        String uploadPath = context.getRealPath("") + File.separator + "data" + File.separator;
        String csvFile = uploadPath + csvTaxFileName;
        FileCopyUtils.copy(file.getBytes(), new File(csvFile));

        errors = validateService.validateTaxonomy();
        if (errors.get("invalidInternalCode").isEmpty()) {
            next = true;
        }
        model.addAttribute("file", file);
        model.addAttribute("errors", errors);
        model.addAttribute("next", next);
        model.addAttribute("upload", true);

        return "taxonomies";
    }

    @RequestMapping(value = "/generateTax", method = RequestMethod.GET)
    public String generateTaxonomy(final ModelMap model) throws Exception {

        boolean generated = taxonomyService.createTaxonomySAF();

        String downloadDir = context.getRealPath("") + File.separator + "downloads";
        String zipDir = downloadDir + File.separator + "zip";
        String sourceDir = downloadDir + File.separator + "saf-taxonomy";
        String target = zipDir + File.separator + "saf-taxonomy.zip";

        File nsd = new File(sourceDir);
        if (nsd.list().length > 0) {
            batchService.zip(sourceDir, target);
            File newZipFile = new File(target);
            model.addAttribute("file", newZipFile);
        }
        model.addAttribute("generated", generated);

        return "taxonomies";
    }
    
    @RequestMapping(value = "/createTaxSAF", method = RequestMethod.GET)
    public String createTaxSAF(final ModelMap model) throws Exception {

        taxonomyService.createTaxonomySAF(sessionID);

        String downloadDir = context.getRealPath("") + File.separator + "data/" + sessionID + "/downloads";
        String zipDir = downloadDir + File.separator + "zip";

        File sessionDir = new File(zipDir);
        if(!sessionDir.isDirectory()) {
           sessionDir.mkdirs(); 
        }

        String sourceDir = downloadDir + File.separator + "saf-taxonomy";
        String target = zipDir + File.separator + "saf-taxonomy.zip";

        File nsd = new File(sourceDir);
        if (nsd.list().length > 0) {
            batchService.zip(sourceDir, target);
            File newZipFile = new File(target);
            model.addAttribute("file", newZipFile);
        }

        return "cs/taxDownload";
    }
    

    private void clearErrors(String clearType) {
        Iterator<Map.Entry<String, ArrayList>> iterator = collectedErrors.entrySet().iterator();
        if (clearType.equals("csd")) {
            while (iterator.hasNext()) {
                Map.Entry<String, ArrayList> entry = iterator.next();
                if (!entry.getKey().equals("photos")) {
                    iterator.remove();
                }
            }
        }
        if (clearType.equals("photos")) {
            if(collectedErrors.get("photos")!= null) {
                for(Object photoName : collectedErrors.get("photos")) {
                    photoHiddenErrors.add((String) photoName);
                }
            }

            while (iterator.hasNext()) {
                Map.Entry<String, ArrayList> entry = iterator.next();
                if (entry.getKey().equals("photos")) {
                    iterator.remove();
                }
            }
        }
    }

    private void clearTaxErrors() {
        Iterator<Map.Entry<String, ArrayList>> iterator = taxErrors.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    private void ClearHiddenErrors() {
        photoHiddenErrors = new HashSet<>(); 
    }

    private void mergeHiddenErrors() {
        clearErrors("photos");
        collectedErrors.put("photos", new ArrayList<>(photoHiddenErrors));
    }

    private Zip adjustZip(Zip zip) {
        if(zip != null) {
            String zipcode = zip.getZip();
            if(zipcode.length() == 1) {
                zip.setZip("0000" + zipcode);
            }
            if(zipcode.length() == 2) {
                zip.setZip("000" + zipcode);
            }
            if(zipcode.length() == 3) {
                zip.setZip("00" + zipcode);
            }
            if(zipcode.length() == 4) {
                zip.setZip("0" + zipcode);
            }
        }
        return zip;
    }

    private String adjustZipcode(String zipcode) {
        if(!"".equals(zipcode) && zipcode != null) {
            if(zipcode.length() == 1) {
                zipcode = "0000" + zipcode;
            }
            if(zipcode.length() == 2) {
                zipcode = "000" + zipcode;
            }
            if(zipcode.length() == 3) {
                zipcode = "00" + zipcode;
            }
            if(zipcode.length() == 4) {
                zipcode = "0" + zipcode;
            }
        }
        return zipcode;
    }

}
