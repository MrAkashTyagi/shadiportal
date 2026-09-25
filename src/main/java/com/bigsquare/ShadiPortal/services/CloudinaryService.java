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
            MultipartFile file,
            String folder
    ) {

        try {

            String resourceType = "auto";

            if ("application/pdf".equalsIgnoreCase(
                    file.getContentType()
            )) {
                resourceType = "raw";
            }

            return cloudinary
                    .uploader()
                    .upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type",
                                    resourceType,
                                    "folder",
                                    folder
                            )
                    );

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Cloudinary upload failed",
                    exception
            );
        }
    }

//    public Map uploadFile(
//            MultipartFile file,
//            String folder
//    ) {
//
//        try {
//            return cloudinary
//                    .uploader()
//                    .upload(
//                            file.getBytes(),
//                            ObjectUtils.asMap(
//                                    "resource_type",
//                                    "auto",
//                                    "folder",
//                                    folder
//                            )
//                    );
//
//        } catch (IOException exception) {
//
//            throw new RuntimeException(
//                    "Cloudinary upload failed",
//                    exception
//            );
//        }
//    }

    public void deleteFile(
            String publicId,
            String resourceType
    ) {

        try {

            cloudinary
                    .uploader()
                    .destroy(
                            publicId,
                            ObjectUtils.asMap(
                                    "resource_type",
                                    resourceType
                            )
                    );

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Cloudinary delete failed",
                    exception
            );
        }
    }
}
