package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.WallMediaDto;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.entities.WallMedia;
import com.bigsquare.ShadiPortal.entities.WallMediaType;
import com.bigsquare.ShadiPortal.repositories.WallMediaRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.WallMediaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bigsquare.ShadiPortal.services.CloudinaryService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class WallMediaServiceImpl
        implements WallMediaService {

    private static final long MAX_FILE_SIZE =
            100L * 1024L * 1024L;

    @Autowired
    private WallMediaRepo wallMediaRepo;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public WallMediaDto uploadMedia(
            MultipartFile file,
            String caption
    ) {

        validateFile(file);

        User currentUser =
                currentUserService
                        .getCurrentUser();

        User ownerUser =
                currentUserService
                        .getCurrentOwnerUser();

        String folder =
                "users/"
                        + ownerUser.getId()
                        + "/media";

        String contentType =
                file.getContentType();

        String originalFileName =
                file.getOriginalFilename() != null
                        ? file.getOriginalFilename()
                        : "media";

        WallMediaType mediaType =
                resolveMediaType(
                        contentType
                );

        Map<String, Object> uploadResult =
                cloudinaryService
                        .uploadFile(file, folder);

        String cloudinaryUrl =
                uploadResult
                        .get("secure_url")
                        .toString();

        String publicId =
                uploadResult
                        .get("public_id")
                        .toString();

        WallMedia wallMedia =
                new WallMedia();

        wallMedia.setOriginalFileName(
                originalFileName
        );

        wallMedia.setStoredFileName(
                publicId
        );

        wallMedia.setStoragePath(
                cloudinaryUrl
        );

        wallMedia.setPublicId(
                publicId
        );
        wallMedia.setContentType(
                contentType
        );

        wallMedia.setMediaType(
                mediaType
        );

        wallMedia.setFileSize(
                file.getSize()
        );

        wallMedia.setCaption(
                caption == null
                        ? ""
                        : caption.trim()
        );

        wallMedia.setUploadedAt(
                LocalDateTime.now()
        );

        wallMedia.setOwnerUser(
                ownerUser
        );

        wallMedia.setUploadedBy(
                currentUser
        );

        WallMedia savedMedia =
                wallMediaRepo.save(
                        wallMedia
                );

        return toDto(
                savedMedia
        );
    }

    @Override
    public List<WallMediaDto> getAllMedia() {

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        return wallMediaRepo
                .findAllByOwnerUserIdOrderByUploadedAtDesc(
                        ownerUserId
                )
                .stream()
                .map(
                        this::toDto
                )
                .toList();
    }

    @Override
    public WallMedia getMediaForCurrentUser(
            Integer mediaId
    ) {

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        return wallMediaRepo
                .findByIdAndOwnerUserId(
                        mediaId,
                        ownerUserId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Wall media not found"
                        )
                );
    }

    @Override
    public void deleteMedia(
            Integer mediaId
    ) {

        User currentUser =
                currentUserService
                        .getCurrentUser();

        if (
                "ROLE_GUEST".equals(
                        currentUser.getRole()
                )
        ) {

            throw new RuntimeException(
                    "Guest users cannot delete media"
            );
        }

        WallMedia wallMedia =
                getMediaForCurrentUser(
                        mediaId
                );

        String resourceType =
                wallMedia.getMediaType()
                        == WallMediaType.VIDEO
                        ? "video"
                        : "image";

        if (
                wallMedia.getPublicId() != null &&
                        !wallMedia.getPublicId().isBlank()
        ) {

            cloudinaryService.deleteFile(
                    wallMedia.getPublicId(),
                    resourceType
            );
        }

        wallMediaRepo.delete(
                wallMedia
        );
    }

    private void validateFile(
            MultipartFile file
    ) {

        if (
                file == null ||
                        file.isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "Please select a media file"
            );
        }

        if (
                file.getSize() >
                        MAX_FILE_SIZE
        ) {
            throw new IllegalArgumentException(
                    "Media file cannot be greater than 100 MB"
            );
        }

        resolveMediaType(
                file.getContentType()
        );
    }

    private WallMediaType resolveMediaType(
            String contentType
    ) {

        if (contentType == null) {
            throw new IllegalArgumentException(
                    "Unable to identify media type"
            );
        }

        if (
                contentType.startsWith(
                        "image/"
                )
        ) {
            return WallMediaType.IMAGE;
        }

        if (
                contentType.startsWith(
                        "video/"
                )
        ) {
            return WallMediaType.VIDEO;
        }

        throw new IllegalArgumentException(
                "Only image and video files are allowed"
        );
    }

    private WallMediaDto toDto(
            WallMedia wallMedia
    ) {

        return new WallMediaDto(
                wallMedia.getId(),
                wallMedia.getOriginalFileName(),
                wallMedia.getStoragePath(),
                wallMedia.getContentType(),
                wallMedia.getMediaType()
                        .name(),
                wallMedia.getFileSize(),
                wallMedia.getCaption(),
                wallMedia.getUploadedAt(),
                wallMedia.getUploadedBy() == null
                        ? "Unknown"
                        : wallMedia.getUploadedBy()
                        .getName()
        );
    }

    @Override
    public ByteArrayResource
    downloadAllMedia() {

        Integer ownerUserId =
                currentUserService
                        .getCurrentOwnerUserId();

        List<WallMedia> mediaList =
                wallMediaRepo
                        .findAllByOwnerUserIdOrderByUploadedAtDesc(
                                ownerUserId
                        );

        try (

                ByteArrayOutputStream baos =
                        new ByteArrayOutputStream();

                ZipOutputStream zos =
                        new ZipOutputStream(
                                baos
                        )

        ) {

            for (
                    WallMedia media
                    : mediaList
            ) {

                try (

                        InputStream inputStream =
                                new URL(
                                        media.getStoragePath()
                                ).openStream()

                ) {

                    ZipEntry zipEntry =
                            new ZipEntry(
                                    media.getOriginalFileName()
                            );

                    zos.putNextEntry(
                            zipEntry
                    );

                    inputStream.transferTo(
                            zos
                    );

                    zos.closeEntry();

                } catch (Exception exception) {

                    System.out.println(
                            "Skipping file: "
                                    + media.getOriginalFileName()
                    );
                }


            }

            zos.finish();

            return new ByteArrayResource(
                    baos.toByteArray()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to create ZIP",
                    e
            );
        }
    }

}
