/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;
import org.csscience.cssaf.content.SAFObject;

public interface BatchService {

    /**
     * Unzip a file to a destination
     * @param zipfile file
     * @param destDir destination directory
     * @return unzip location
     * @throws IOException if error
     *
     * supports Zip64 extensions and individual entries larger than 4 GB
     */
    public String unzip(File zipFile, File destDir) throws IOException, ArchiveException;

    /**
     * Unzip a file in a specific source directory
     * @param zipFileDir source directory
     * @param zipFileName file name
     * @param unzipDir unzip location
     * @return unzip location
     * @throws IOException if error
     */
    public String unzip(String zipFileDir, String zipFileName, String unzipDir) throws IOException;

    /**
     * Cleanup
     */
    public void cleanupZipTemp();


    /**
     * used for export download
     */
    public static final String COMPRESSED_EXPORT_MIME_TYPE = "application/zip";

    /**
     * Method to perform an export and save it as a zip file.
     *
     * @param context The DSpace Context
     * @param items The items to export
     * @param destDirName The directory to save the export in
     * @param zipFileName The name to save the zip file as
     * @param seqStart The first number in the sequence
     * @param migrate Whether to use the migrate option or not
     * @param excludeBitstreams Whether to exclude bitstreams or not
     * @throws Exception if error
     */
//    public void exportAsZip(Iterator<SAFItem> items, String destDirName, String zipFileName) throws Exception;

    /**
     * Convenience method to create export a single Community, Collection, or
     * Item
     * @param dso - the dspace object to export
     * @param context - the dspace context
     * @param migrate Whether to use the migrate option or not
     * @throws Exception if error
     */
    public void createDownloadableExport(SAFObject safo) throws Exception;

    /**
     * Convenience method to export a List of dspace objects (Community,
     * Collection or Item)
     *
     * @param dsObjects
     *            - List containing dspace objects
     * @param context
     *            - the dspace context
     * @param migrate Whether to use the migrate option or not
     * @throws Exception if error
     */
    public void createDownloadableExport(List<SAFObject> safObjects) throws Exception;

    /**
     * Returns config file entry for org.dspace.app.itemexport.work.dir
     *
     * @return String representing config file entry for
     *         org.dspace.app.itemexport.work.dir
     * @throws Exception if error
     */
    public String getExportWorkDirectory() throws Exception;

    /**
     * Used to read the export archived. Inteded for download.
     *
     * @param fileName
     *            the name of the file to download
     * @param eperson
     *            the eperson requesting the download
     * @return an input stream of the file to be downloaded
     * @throws Exception if error
     */
    public InputStream getExportDownloadInputStream(String fileName) throws Exception;

    /**
     * Get the file size of the export archive represented by the file name.
     *
     * @param fileName
     *            name of the file to get the size.
     * @throws Exception if error
     * @return size as long
     */
    public long getExportFileSize(String fileName) throws Exception;

    /**
     * A clean up method that is ran before a new export archive is created. It
     * uses the config file entry 'org.dspace.app.itemexport.life.span.hours' to
     * determine if the current exports are too old and need purgeing
     * Removes all old exports, not just those for the person doing the export.
     *
     * @throws Exception if error
     */
    public void deleteOldExportArchives() throws Exception;

    /**
     * Zip source to target
     * @param strSource source file
     * @param target target file
     * @throws Exception if error
     */
    public void zip(String strSource, String target) throws Exception;

    /**
     * List all entries in the photo derectory 
     * @param sourceZipFile source file
     * @throws Exception if error
     */
    public List<String> listTempWorkDirFiles(String sourceZipFile) throws Exception;
    
}
