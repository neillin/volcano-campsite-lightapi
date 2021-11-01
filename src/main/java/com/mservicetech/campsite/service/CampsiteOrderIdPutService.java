package com.mservicetech.campsite.service;

import com.networknt.http.HttpStatus;
import com.networknt.http.MediaType;
import com.networknt.http.RequestEntity;
import com.networknt.http.ResponseEntity;
import com.networknt.http.HttpService;
import com.mservicetech.campsite.model.Reservation;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CampsiteOrderIdPutService implements HttpService<Reservation, String> {
    private static final Logger logger = LoggerFactory.getLogger(CampsiteOrderIdPutService.class);

    @Override
    public ResponseEntity invoke(RequestEntity<Reservation> requestEntity) {
        Reservation requestBody = requestEntity.getBody();
        logger.debug(requestBody.toString());
        HeaderMap responseHeaders = new HeaderMap();
        responseHeaders.add(Headers.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String body = "";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
        return responseEntity;
    }
}
