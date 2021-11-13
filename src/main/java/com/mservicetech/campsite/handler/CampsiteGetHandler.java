package com.mservicetech.campsite.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mservicetech.campsite.model.AvailableDates;
import com.mservicetech.campsite.model.Error;
import com.mservicetech.campsite.service.CampsiteService;
import com.networknt.config.Config;
import com.networknt.exception.ApiException;
import com.networknt.handler.LightHttpHandler;
import com.networknt.http.HttpStatus;
import com.networknt.service.SingletonServiceFactory;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;


/**
For more information on how to write business handlers, please check the link below.
https://doc.networknt.com/development/business-handler/rest/
*/
public class CampsiteGetHandler implements LightHttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CampsiteGetHandler.class);
    private static CampsiteService service = SingletonServiceFactory.getBean(CampsiteService.class);
    private static final ObjectMapper objectMapper = Config.getInstance().getMapper();
    private final  String INVALID_DATE_INPUT = "ERR20002";

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        LocalDate startDate, endDate;
       if (exchange.getQueryParameters().get("startDate")!=null) {
           startDate = LocalDate.parse( exchange.getQueryParameters().get("startDate").getFirst());
       } else {
           startDate = LocalDate.now().plusDays(1);
       }
        if (exchange.getQueryParameters().get("endDate")!=null) {
            endDate = LocalDate.parse( exchange.getQueryParameters().get("endDate").getFirst());
        } else {
            endDate = LocalDate.now().plusDays(30);
        }
        Error error = validateSearchCriteria(startDate,  endDate);
        if (error!=null) {
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
            exchange.setStatusCode(HttpStatus.BAD_REQUEST.value());
            exchange.getResponseSender().send(objectMapper.writeValueAsString(error));
        } else {
            try {
                AvailableDates availableDates = service.getAvailableDates(startDate, endDate);
                exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
                exchange.setStatusCode(HttpStatus.OK.value());
                exchange.getResponseSender().send(objectMapper.writeValueAsString(availableDates));
            } catch (ApiException e) {
                logger.error("Error Occurred: " + e.getMessage());
                setExchangeStatus(exchange, e.getStatus());
                exchange.getResponseSender().send(e.getMessage());
            }
        }
    }

    private Error validateSearchCriteria(LocalDate startDate, LocalDate endDate)  {
        Error error = null;
        if (startDate.isBefore(LocalDate.now().plusDays(1)) || endDate.isAfter(LocalDate.now().plusDays(31)) || endDate.isBefore(startDate)) {
            error = new Error();
            error.setCode(INVALID_DATE_INPUT);
            error.setMessage( "Invalid date input, please check again.");
        }
        return error;
    }
}
