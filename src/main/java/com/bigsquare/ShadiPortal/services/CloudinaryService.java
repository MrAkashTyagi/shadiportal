package com.bigsquare.ShadiPortal.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public Map uploadFile(
            MultipartFile file
    ) {

        try {

            return cloudinary
                    .uploader()
                    .upload(
                            file.getBytes(),
                            ObjectUtils.emptyMap()
                    );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Cloudinary upload failed",
                    e
            );
        }
    }

    public void deleteFile(
            String publicId
    ) {

        try {

            cloudinary
                    .uploader()
                    .destroy(
                            publicId,
                            ObjectUtils.emptyMap()
                    );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Cloudinary delete failed",
                    e
            );
        }
    }
}
