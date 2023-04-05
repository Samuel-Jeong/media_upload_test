package org.pwdw.media_upload_test.controller;

import lombok.extern.slf4j.Slf4j;
import org.pwdw.media_upload_test.service.S3UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.pwdw.media_upload_test.util.HttpUtil.getResponseEntity;

@Slf4j
@Controller
@RequestMapping("/api")
public class S3Controller {

    private final S3UploadService s3UploadService;

    public S3Controller(S3UploadService s3UploadService) {
        this.s3UploadService = s3UploadService;
    }

    @ResponseBody
    @PostMapping(value="/v1/save/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity saveImage(@RequestParam(value="image") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String storedFileName = s3UploadService.upload(imageFile,"images");
                log.info("Success to upload the image file. (name={})", storedFileName);
            } catch (IOException e) {
                log.warn("Fail to upload the image file. (name={}) ({}) ({})", imageFile.getName(), e.getCause(), e.getMessage());
                return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Fail to upload the image file. (name={})", imageFile.getName());
            return getResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return getResponseEntity(HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value="/v1/save/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity saveVideo(@RequestParam(value="video") MultipartFile videoFile) {
        if (!videoFile.isEmpty()) {
            try {
                String storedFileName = s3UploadService.upload(videoFile,"videos");
                log.info("Success to upload the video file. (name={})", storedFileName);
            } catch (IOException e) {
                log.warn("Fail to upload the video file. (name={}) ({}) ({})", videoFile.getName(), e.getCause(), e.getMessage());
                return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Fail to upload the video file. (name={})", videoFile.getName());
            return getResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return getResponseEntity(HttpStatus.OK);
    }

}
