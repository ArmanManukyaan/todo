package com.example.todorest.util;

import com.example.todorest.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageDownloader {
    @Value("${todo.upload.image.pat}")
    private String imageUploadPath;

    /**
     * Saves a profile picture for the user.
     *
     * @param multipartFile The profile picture file to be saved.
     * @param user          The user for whom the profile picture is saved.
     * @throws IOException If an I/O exception occurs during the file transfer.
     */
    public void saveProfilePicture(MultipartFile multipartFile, User user) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
            user.setPicName(fileName);
        }
    }


    /**
     * Deletes a profile picture by its filename.
     *
     * @param fileName The filename of the profile picture to be deleted.
     * @throws IOException If an I/O exception occurs during the file deletion.
     */
    public void deleteProfilePicture(String fileName) throws IOException {
        if (fileName != null && !fileName.isEmpty()) {
            Path paths = Paths.get(imageUploadPath + fileName);
            Files.delete(paths);
        }
    }
}
