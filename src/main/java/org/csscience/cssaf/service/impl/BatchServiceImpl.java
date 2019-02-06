/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.csscience.cssaf.content.SAFObject;
import org.csscience.cssaf.service.BatchService;
import org.springframework.stereotype.Service;

@Service
public class BatchServiceImpl implements BatchService {

    private static final Logger log = Logger.getLogger(StateServiceImpl.class);

    @Override
    public String unzip(String zipFileDir, String zipFileName, String unzipDir) throws IOException {
        
        String unzipLocation = null;
        String zipFilePath = zipFileDir + File.separator + zipFileName;

        ZipFile zipFile = new ZipFile(zipFilePath);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        boolean isRootDirectory = false;
        
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if(entry.isDirectory()){
                String destPath = unzipDir + File.separator + entry.getName();
                if(!isRootDirectory){
                    unzipLocation = destPath;
                    isRootDirectory = true;
                }
                File file = new File(destPath);
                file.mkdirs();
            } else {
                String destPath = unzipDir + File.separator + entry.getName();

                try(InputStream inputStream = zipFile.getInputStream(entry);
                    FileOutputStream outputStream = new FileOutputStream(destPath);
                ){
                    int data = inputStream.read();
                    while(data != -1){
                        outputStream.write(data);
                        data = inputStream.read();
                    }
                }
            }
        }
        
        return unzipLocation;
    }

    @Override
    public void cleanupZipTemp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createDownloadableExport(SAFObject safo) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createDownloadableExport(List<SAFObject> safObjects) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getExportWorkDirectory() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getExportDownloadInputStream(String fileName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getExportFileSize(String fileName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteOldExportArchives() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void zip(String strSource, String target) throws Exception {
        ZipOutputStream cpZipOutputStream = null;
        String tempFileName = target + "_tmp";
        try
        {
            File cpFile = new File(strSource);
            if (!cpFile.isFile() && !cpFile.isDirectory())
            {
                return;
            }
            File targetFile = new File(tempFileName);
            if (!targetFile.createNewFile())
            {
                log.warn("Target file already exists: " + targetFile.getName());
            }

            FileOutputStream fos = new FileOutputStream(tempFileName);
            cpZipOutputStream = new ZipOutputStream(fos);
            cpZipOutputStream.setLevel(9);
            zipFiles(cpFile, strSource, tempFileName, cpZipOutputStream);
            cpZipOutputStream.finish();
            cpZipOutputStream.close();
            cpZipOutputStream = null;

            // Fix issue on Windows with stale file handles open before trying to delete them
            System.gc();

//            deleteDirectory(cpFile);
            if (!targetFile.renameTo(new File(target)))
            {
                log.error("Unable to rename file");
            }
        }
        finally
        {
            if (cpZipOutputStream != null)
            {
                cpZipOutputStream.close();
            }
        }
    }

    @Override
    public List<String> listTempWorkDirFiles(String sourceZipFile) throws Exception {

        List<String> photoNames = new ArrayList<>();
        ZipFile zipFile = new ZipFile(sourceZipFile);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if(!entry.isDirectory()){
                photoNames.add(entry.getName());
            }
        }
        return photoNames;
    }

    @Override
    public String unzip(File zipFile, File destDir) throws IOException, ArchiveException {
       String destDirectory = destDir.getAbsolutePath();

       try (ArchiveInputStream i = new ZipArchiveInputStream(new 
          FileInputStream(zipFile), "UTF-8", false, true)) {
          ArchiveEntry entry = null;
          while ((entry = i.getNextEntry()) != null) {
             if (!i.canReadEntryData(entry)) {
                System.out.println("Can't read entry: " + entry);
                continue;
             }
             String name = destDirectory + File.separator + entry.getName();
             File f = new File(name);
             if (entry.isDirectory()) {
                if (!f.isDirectory() && !f.mkdirs()) {
                   throw new IOException("failed to create directory " + f);
                }
             } else {
                File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                   throw new IOException("failed to create directory " + parent);
                }
                try (OutputStream o = Files.newOutputStream(f.toPath())) {
                   IOUtils.copy(i, o);
                }
             }
          }
       }

       return destDirectory;
    }

    protected void zipFiles(File cpFile, String strSource, String strTarget, ZipOutputStream cpZipOutputStream) throws Exception
    {
        int byteCount;
        final int DATA_BLOCK_SIZE = 2048;
        FileInputStream cpFileInputStream = null;
        if (cpFile.isDirectory())
        {
            File[] fList = cpFile.listFiles();
            for (File aFList : fList) {
                zipFiles(aFList, strSource, strTarget, cpZipOutputStream);
            }
        }
        else
        {
            try
            {
                if (cpFile.getAbsolutePath().equalsIgnoreCase(strTarget))
                {
                    return;
                }
                String strAbsPath = cpFile.getPath();
                String strZipEntryName = strAbsPath.substring(strSource
                        .length() + 1, strAbsPath.length());

                // byte[] b = new byte[ (int)(cpFile.length()) ];

                cpFileInputStream = new FileInputStream(cpFile);

                ZipEntry cpZipEntry = new ZipEntry(strZipEntryName);
                cpZipOutputStream.putNextEntry(cpZipEntry);

                byte[] b = new byte[DATA_BLOCK_SIZE];
                while ((byteCount = cpFileInputStream.read(b, 0,
                        DATA_BLOCK_SIZE)) != -1)
                {
                    cpZipOutputStream.write(b, 0, byteCount);
                }

                // cpZipOutputStream.write(b, 0, (int)cpFile.length());
            }
            finally
            {
                if (cpFileInputStream != null)
                {
                    cpFileInputStream.close();
                }
                cpZipOutputStream.closeEntry();
            }
        }
    }

    /**
     * Delete a directory
     * @param path directory path
     * @return true if successful, false otherwise
     */
    protected boolean deleteDirectory(File path)
    {
        if (path.exists())
        {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        log.error("Unable to delete file: " + file.getName());
                    }
                }
            }
        }

        return (path.delete());
    }
}
