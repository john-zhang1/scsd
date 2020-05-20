package org.csscience.cssaf.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.csscience.cssaf.safpackage.SAFPackage;
import org.csscience.cssaf.service.BatchService;
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
public class SAFBuilderController {

    private Map<String, String> safNextSteps = new HashMap<>();
    private Map<String, ArrayList> safErrors = new HashMap<>();
    private final String unziDataDirectory = "safdata";
    private String outputFilename = "SimpleArchiveFormat.zip";
    private String sessionID;
    private String metadataCsv;

    @Autowired
    private ServletContext context;

    @Autowired
    private BatchService batchService;

    @RequestMapping(value = {"/safProcess"}, method = RequestMethod.GET)
    public String safProcessPage() {
        safNextSteps = new HashMap<>();
        clearErrors();
        return "saf/home";
    }

    @RequestMapping(value = "/zipSafUpload", method = RequestMethod.POST)
    public String zipSafUploadPage(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") final MultipartFile file, @RequestParam("uploadFileName") final String fileName, @RequestParam("sessionid") final String sessionid, final ModelMap model) throws IOException, Exception {
        sessionID = request.getSession().getId();

        if(file.getContentType().equals("application/zip") && file.getBytes().length > 0) {

            String uploadPath = context.getRealPath("") + File.separator + "data/" + sessionid + File.separator;
            File sessionDir = new File(uploadPath);

            if (!sessionDir.isDirectory()) {
                sessionDir.mkdirs();
            }

            String newZipFile = uploadPath + fileName;
            FileCopyUtils.copy(file.getBytes(), new File(newZipFile));

            safNextSteps.remove("saf");
            clearErrors();

            String unzipSourceDir = batchService.unzip(uploadPath, fileName, uploadPath);
            File unzipDir = new File(unzipSourceDir);
            File zipFile = new File(newZipFile);
            zipFile.delete();
            File unzipDataDir = new File(uploadPath + unziDataDirectory);
            unzipDir.renameTo(unzipDataDir);
            safNextSteps.put("saf", "saf");
        }

        model.addAttribute("file", file);
        model.addAttribute("safNext", safNextSteps);

        return "saf/home";
    }            

    @RequestMapping(value = "/createSafPackage", method = RequestMethod.GET)
    public String createSafPackage(final ModelMap model) throws IOException, Exception {
        SAFPackage safPackageInstance = new SAFPackage();
        String safSourceDir = context.getRealPath("") + File.separator + "data/" + sessionID + "/safdata";
        String downloadDir = context.getRealPath("") + File.separator + "data/" + sessionID + "/downloads";
        String zipDir = downloadDir + File.separator + "zip";

        File zipPath = new File(zipDir);
        if(!zipPath.isDirectory()) {
           zipPath.mkdirs(); 
        }

        getMetadataCsv(safSourceDir);
        String csvPath = safSourceDir + File.separator + metadataCsv;

        try {
            if(metadataCsv != null) {
                safPackageInstance.processMetaPack(csvPath, zipDir, true);
            }
        } catch (IOException ex) {
            Logger.getLogger(SAFBuilderController.class.getName()).log(Level.SEVERE, null, ex);
        }

        String target = zipDir + File.separator + outputFilename;
        File file = new File(target);
        String filesize = CommonUtils.readableFileSize(file.length());
        model.addAttribute("file", file);
        model.addAttribute("filesize ", filesize);
        return "saf/safDownload";
    }                                                 

    @RequestMapping(value = "/safdownload/{fileName}", method = RequestMethod.GET)
    public String safDownloadPage(HttpServletRequest request, HttpServletResponse response,
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

        return "saf/safDownload";
    }



    private void clearErrors() {
        Iterator<Map.Entry<String, ArrayList>> iterator = safErrors.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    protected void getMetadataCsv(String parentDir){

        File directory = new File(parentDir);
        FilenameFilter filter;
        filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".csv");
            }
        };
        File[] listOfFiles = directory.listFiles(filter);

        if(listOfFiles.length > 0) {
            metadataCsv = listOfFiles[0].getName();
        }
    } 
}