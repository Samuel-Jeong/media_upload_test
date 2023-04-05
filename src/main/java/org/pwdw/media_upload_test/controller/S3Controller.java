package org.pwdw.media_upload_test.controller;

import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.pwdw.media_upload_test.service.S3UploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.pwdw.media_upload_test.util.HttpUtil.getResponseEntity;

@Slf4j
@Controller
@RequestMapping("/api")
public class S3Controller {

    private final S3UploadService s3Service;

    public S3Controller(S3UploadService s3Service) {
        this.s3Service = s3Service;
    }

    @ResponseBody
    @PostMapping(value="/v1/save/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity saveImage(@RequestParam(value="image") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String storedFileName = s3Service.upload(imageFile,"images");
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
                String storedFileName = s3Service.upload(videoFile,"videos");
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

    @ResponseBody
    @GetMapping(value = "/v1/get/image")
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "imageName") String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            return null;
        }

        ResponseEntity<byte[]> result = null;
        try {
            InputStream inputStream = getFileStream(imageName);

            HttpHeaders header = new HttpHeaders();
            //header.add(HttpHeaders.CONTENT_TYPE, URLConnection.guessContentTypeFromStream(inputStream));
            header.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
            result = new ResponseEntity<>(
                    IOUtils.toByteArray(inputStream),
                    header, HttpStatus.OK
            );
        } catch(IOException e) {
            log.warn("Fail to getImage. (imageName={}) ({}) ({})", imageName, e.getCause(), e.getMessage());
        }

        return result;
    }

    @ResponseBody
    @GetMapping(value = "/v1/get/video")
    public ResponseEntity<byte[]> getVideo(@RequestParam(value = "videoName") String videoName) {
        if (videoName == null || videoName.isEmpty()) {
            return null;
        }

        ResponseEntity<byte[]> result = null;
        try {
            InputStream inputStream = getFileStream(videoName);

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            result = new ResponseEntity<>(
                    IOUtils.toByteArray(inputStream),
                    header, HttpStatus.OK
            );
        } catch(IOException e) {
            log.warn("Fail to getVideo. (videoName={}) ({}) ({})", videoName, e.getCause(), e.getMessage());
        }

        return result;
    }

    private InputStream getFileStream(String fileName) throws IOException {
        URL url = new URL(s3Service.getFileURL(fileName));
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        return urlConn.getInputStream();
    }

}
