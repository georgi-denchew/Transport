/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.FTP;

import com.griffinslogistics.exceptions.DeliveryFTPDeleteException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Georgi
 */
public class DeliveryFTP {

    private static final String FTP_SERVER = "serenity.sbnd.net";
    private static final String USERNAME = "griffins";
    private static final String PASSWORD = "wOx0hw";

    private static final String ROOT_FOLDER = "TransportApp/Deliveries/";

    private final FTPClient ftpClient;

    public DeliveryFTP() {
        this.ftpClient = new FTPClient();
    }

    /**
     * Uploads the attachment to FTP Server.
     *
     * @param deliveryUuid
     * @param stream
     * @param fileName
     * @return Returns the full path to the file.
     */
    public String uploadFile(String deliveryUuid, InputStream stream, String fileName) {
        String resultPath = "";
        boolean isUploadSuccessful;
        try {

            this.connect();
            this.changeDirectory(deliveryUuid);

            String currentDirectory = ftpClient.printWorkingDirectory();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            isUploadSuccessful = ftpClient.storeFile(fileName, stream);

            if (isUploadSuccessful) {
                resultPath = this.ftpClient.printWorkingDirectory() + "/" + fileName;

                this.logout();
                stream.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(DeliveryFTP.class.getName()).log(Level.SEVERE, null, ex);
        }

        return resultPath;
    }

    public int deleteDirectory(String deliveryUuid) throws DeliveryFTPDeleteException {
        int deletedCount = 0;

        try {
            this.connect();
            this.changeDirectory(deliveryUuid);

            String currentDirectory = ftpClient.printWorkingDirectory();
            String[] allFilesNames = ftpClient.listNames(currentDirectory);

            for (String fileName : allFilesNames) {
                int length = fileName.trim().length();
                if (length <= 3) {
                    continue;
                }

                boolean successfulDelete = ftpClient.deleteFile(currentDirectory + "/" + fileName);

                if (successfulDelete) {
                    deletedCount++;
                } else {
                    throw new DeliveryFTPDeleteException("Unable to delete file");
                }
            }
            
            boolean successfulFolderDelete = ftpClient.deleteFile(currentDirectory);

        } catch (IOException ex) {
            Logger.getLogger(DeliveryFTP.class.getName()).log(Level.SEVERE, null, ex);
        }

        return deletedCount;
    }

    public OutputStream downloadFile(String filePath) {
        OutputStream outputStream = new ByteArrayOutputStream();

        boolean isConnected = this.connect();

        if (isConnected) {
            try {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.retrieveFile(filePath, outputStream);

                this.logout();
            } catch (IOException ex) {
                Logger.getLogger(DeliveryFTP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return outputStream;
    }

    private void logout() {
        try {
            this.ftpClient.logout();
        } catch (IOException ex) {
            Logger.getLogger(DeliveryFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean changeDirectory(String deliveryUuid) {
        boolean directoryChanged = false;
        try {
            directoryChanged = ftpClient.changeWorkingDirectory(DeliveryFTP.ROOT_FOLDER + deliveryUuid);

            if (!directoryChanged) {
                directoryChanged = ftpClient.makeDirectory(DeliveryFTP.ROOT_FOLDER + deliveryUuid);
                directoryChanged = ftpClient.changeWorkingDirectory(DeliveryFTP.ROOT_FOLDER + deliveryUuid);
            }
        } catch (IOException ex) {
            Logger.getLogger(DeliveryFTP.class.getName()).log(Level.SEVERE, null, ex);
        }

        return directoryChanged;
    }

    private boolean connect() {
        boolean isConnected = false;
        try {
            ftpClient.connect(DeliveryFTP.FTP_SERVER);
            isConnected = ftpClient.isConnected();

            if (isConnected) {
                isConnected = ftpClient.login(DeliveryFTP.USERNAME, DeliveryFTP.PASSWORD);
            }

        } catch (IOException ex) {
            Logger.getLogger(DeliveryFTP.class.getName()).log(Level.SEVERE, null, ex);
        }

        return isConnected;
    }
}
