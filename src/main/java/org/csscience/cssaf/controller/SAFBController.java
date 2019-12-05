package org.csscience.cssaf.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SAFBController {

    @RequestMapping(value = {"/safbuilder"}, method = RequestMethod.GET)
    public String safMetadataUploadPage() {
        return "safMetadataUpload";
    }

    @RequestMapping(value = "/safbuild", method = RequestMethod.GET)
    public String safBuild(final ModelMap model) {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "ls /vagrant/");
        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output);
                System.exit(0);
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String s;
        try {
            Runtime rt = Runtime.getRuntime();
//            Process pr1 = rt.exec("cd ~");
            Process pr = rt.exec("./safbuilder.sh -c src/sample_data/AAA_batch-metadata.csv -z");

//            Process pr = Runtime.getRuntime().exec("cd ~/; ./safbuilder.sh -c src/sample_data/AAA_batch-metadata.csv -z");
            BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            while ((s = br.readLine()) != null) {
                System.out.println("line: " + s);
            }
            pr.waitFor();
            System.out.println("exit: " + pr.exitValue());
            pr.destroy();
        } catch (Exception e) {
        }
        return "csvValidate";
    }

}
