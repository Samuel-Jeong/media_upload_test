package org.pwdw.media_upload_test.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpUtil {

    public static ResponseEntity<?> getResponseEntity(HttpStatus httpStatus) {
        return new ResponseEntity<>(httpStatus);
    }

}
