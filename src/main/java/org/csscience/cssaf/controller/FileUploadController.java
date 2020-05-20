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

   @RequestMapping(value = "/fileUploadForm", method = RequestMethod.GET)
   public String fileUploadPage() {
        return "fileUploadForm";
   }

    @RequestMapping(value="/fileUpload", method = RequestMethod.POST)
    public String fileUpload(@RequestParam("file") final MultipartFile file, final ModelMap model) throws IOException {
        String uploadPath = context.getRealPath("") + File.separator + "data" + File.separator;
        FileCopyUtils.copy(file.getBytes(), new File(uploadPath+file.getOriginalFilename()));
        model.addAttribute("file", file);
        return "fileUploadSuccess";
    }

    @RequestMapping(value = "/uploadMultiFile", method = RequestMethod.POST)
    public String submit(@RequestParam("files") MultipartFile[] files, ModelMap model) throws IOException {
        String uploadPath = context.getRealPath("") + File.separator + "data" + File.separator;
        for(MultipartFile f: files){
            FileCopyUtils.copy(f.getBytes(), new File(uploadPath+f.getOriginalFilename()));
        }
        model.addAttribute("files", files);
        return "fileUploadSuccess";
    }

    @RequestMapping(value = "/uploadFileWithAddtionalData", method = RequestMethod.POST)
    public String submit(@RequestParam final MultipartFile file, @RequestParam final String name, @RequestParam final String email, final ModelMap model) throws IOException {
        String uploadPath = context.getRealPath("") + File.separator + "data" + File.separator;
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
}