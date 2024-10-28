package com.demo.msgvalidationservice.controller;

import com.demo.msgvalidationservice.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MsgValidationController {

    private static final Logger logger = LoggerFactory.getLogger(MsgValidationController.class);

    @PostMapping("/my-rest-service/warning")
    public ResponseEntity<ResultDTO> handleWarning(@RequestBody String data) {
        logger.info("Received RestAPI request body: {}: ", data);

        ResultDTO response;
        if(data!= null){
            response = new ResultDTO(true, data, "Got it. Thanks");
        } else {
            response = new ResultDTO(false, data, "Got it. Thanks");
        }
        // Return a response
        return ResponseEntity.ok().body(response);
    }
}
