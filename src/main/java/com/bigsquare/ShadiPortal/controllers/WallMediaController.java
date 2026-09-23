package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.WallMediaDto;
import com.bigsquare.ShadiPortal.services.WallMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/wall")
public class WallMediaController {

    @Autowired
    private WallMediaService wallMediaService;

    @PostMapping(
            value = "/upload",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<WallMediaDto>
    uploadMedia(

            @RequestPart("file")
            MultipartFile file,

            @RequestParam(
                    value = "caption",
                    required = false,
                    defaultValue = ""
            )
            String caption

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        wallMediaService
                                .uploadMedia(
                                        file,
                                        caption
                                )
                );
    }

    @GetMapping
    public ResponseEntity<List<WallMediaDto>>
    getAllMedia() {

        return ResponseEntity.ok(
                wallMediaService
                        .getAllMedia()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteMedia(
            @PathVariable Integer id
    ) {

        wallMediaService.deleteMedia(
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(
            "/download-all"
    )
    public ResponseEntity<ByteArrayResource>
    downloadAllMedia() {

        ByteArrayResource resource =
                wallMediaService
                        .downloadAllMedia();

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=WeddingWall.zip"
                )

                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )

                .body(
                        resource
                );
    }
}
