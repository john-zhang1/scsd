/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.csscience.cssaf.content.FormDataWithFile;
import org.csscience.cssaf.service.BatchService;
import org.csscience.cssaf.service.StateService;
import org.csscience.cssaf.service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

    private static Logger logger = Logger.getLogger(FileUploadController.class);
    
    @Autowired
    private ServletContext context;
   
    @Autowired
    private StateService stateService;
    
    @Autowired
    private ValidateService validateService;
    
    @Autowired
    private BatchService batchService;

   @RequestMapping(value = "/fileUploadForm", method = RequestMethod.GET)
   public String fileUploadPage() {
        return "fileUploadForm";
   }

    @RequestMapping(value="/fileUpload", method = RequestMethod.POST)
    public String fileUpload(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException {
        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator;
        FileCopyUtils.copy(file.getBytes(), new File(uploadPath+file.getOriginalFilename()));
        model.addAttribute("file", file);
        return "fileUploadSuccess";
    }
//
//    @RequestMapping(value="/csvFileUpload", method = RequestMethod.POST)
//    public String csvFileUpload(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException, Exception {
//        String fileType = file.getContentType();
//        Map<String, ArrayList> errors = null;
//        if(fileType.equals("text/csv")){
//            File f = new File( file.getOriginalFilename());
//            file.transferTo(f);
//            
//            errors = validateService.validateCSV(f);
//            System.err.println("Headings contain wrong columns: ");
//        }
//        model.addAttribute("file", file);
//        model.addAttribute("errors", errors);
//
//        return "fileUploadSuccess";
//    }
//
//    @RequestMapping(value="/zipFileUpload", method = RequestMethod.POST)
//    public String zipFileUpload(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException, Exception {
//        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator;
//        File path = new File(uploadPath);
//        String zipFileName = UUID.randomUUID().toString() + ".zip";
//        String newZipFile = uploadPath + zipFileName;
//        FileCopyUtils.copy(file.getBytes(), new File(newZipFile));
//
////        unzip
//        String unzipSourceDir = batchService.unzip(uploadPath, zipFileName, uploadPath);
//        List<String> photoNames = batchService.listTempWorkDirFiles(newZipFile);
//        Map<String, ArrayList> formatErrors = validateService.validatePhotoNameFormat(unzipSourceDir);        
//
//        model.addAttribute("file", file);
//        model.addAttribute("photoNames", photoNames);
//        model.addAttribute("formatErrors", formatErrors);
//
//        return "fileUploadSuccess";
//    }

    @RequestMapping(value = "/uploadMultiFile", method = RequestMethod.POST)
    public String submit(@RequestParam("files") MultipartFile[] files, ModelMap model) throws IOException {
        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator;
        for(MultipartFile f: files){
            FileCopyUtils.copy(f.getBytes(), new File(uploadPath+f.getOriginalFilename()));
        }
        model.addAttribute("files", files);
        return "fileUploadSuccess";
    }

    @RequestMapping(value = "/uploadFileWithAddtionalData", method = RequestMethod.POST)
    public String submit(@RequestParam final MultipartFile file, @RequestParam final String name, @RequestParam final String email, final ModelMap model) throws IOException {
        String uploadPath = context.getRealPath("") + File.separator + "temp" + File.separator;
        FileCopyUtils.copy(file.getBytes(), new File(uploadPath+file.getOriginalFilename()));
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("file", file);
        return "fileUploadSuccess";
    }

    @RequestMapping(value = "/uploadFileModelAttribute", method = RequestMethod.POST)
    public String submit(@ModelAttribute final FormDataWithFile formDataWithFile, final ModelMap model) {

        model.addAttribute("formDataWithFile", formDataWithFile);
        return "fileUploadSuccess";
    }
//
//    @RequestMapping(value = "/downloads/{fileName}", method = RequestMethod.GET)
//    public void downloadSAF( HttpServletRequest request, HttpServletResponse response, 
//            @PathVariable("fileName") String fileName)
//    {
//        String dataDirectory = request.getServletContext().getRealPath("/downloads/");
//        File zipFile = new File(dataDirectory+File.separator+fileName);
//        Path file = Paths.get(dataDirectory, fileName);
//        if (Files.exists(file))
//        {
//            response.setContentType("application/zip");
//            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
//            response.setContentLength((int) zipFile.length());
//
//            try {
//                FileInputStream fileInputStream = new FileInputStream(zipFile);
//                OutputStream responseOutputStream = response.getOutputStream();
//                int bytes;
//                while ((bytes = fileInputStream.read()) != -1) {
//                    responseOutputStream.write(bytes);
//                }
//            } catch (IOException e) {
//            }            
//        }
//    }
}