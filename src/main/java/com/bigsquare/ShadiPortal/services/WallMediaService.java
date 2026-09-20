package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.WallMediaDto;
import com.bigsquare.ShadiPortal.entities.WallMedia;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WallMediaService {

    WallMediaDto uploadMedia(
            MultipartFile file,
            String caption
    );

    List<WallMediaDto> getAllMedia();

    WallMedia getMediaForCurrentUser(
            Integer mediaId
    );

    void deleteMedia(
            Integer mediaId
    );

    ByteArrayResource downloadAllMedia();
}
