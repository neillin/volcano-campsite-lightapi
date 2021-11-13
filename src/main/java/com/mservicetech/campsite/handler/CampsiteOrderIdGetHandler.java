package com.mservicetech.campsite.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mservicetech.campsite.model.Reservation;
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

/**
For more information on how to write business handlers, please check the link below.
https://doc.networknt.com/development/business-handler/rest/
*/
public class CampsiteOrderIdGetHandler implements LightHttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CampsiteGetHandler.class);
    private static CampsiteService service = SingletonServiceFactory.getBean(CampsiteService.class);
    private static final ObjectMapper objectMapper = Config.getInstance().getMapper();


    
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String orderId = exchange.getPathParameters().get("orderId").getFirst();
        try {
            Reservation reservation = service.getReservation(orderId);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
            exchange.setStatusCode(HttpStatus.OK.value());
            exchange.getResponseSender().send(objectMapper.writeValueAsString(reservation));
        } catch (ApiException e) {
            logger.error("Error Occurred: " + e.getMessage());
            setExchangeStatus(exchange, e.getStatus());
            exchange.getResponseSender().send(e.getMessage());
        }

    }
}
