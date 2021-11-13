package com.mservicetech.campsite.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mservicetech.campsite.model.Error;
import com.mservicetech.campsite.service.CampsiteService;
import com.networknt.body.BodyHandler;
import com.networknt.config.Config;
import com.networknt.exception.ApiException;
import com.networknt.handler.LightHttpHandler;
import com.networknt.http.HttpStatus;
import com.networknt.service.SingletonServiceFactory;
import io.undertow.server.HttpServerExchange;
import com.mservicetech.campsite.model.Reservation;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
For more information on how to write business handlers, please check the link below.
https://doc.networknt.com/development/business-handler/rest/
*/
public class CampsiteOrderIdPutHandler implements LightHttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CampsiteGetHandler.class);
    private static CampsiteService service = SingletonServiceFactory.getBean(CampsiteService.class);
    private static final ObjectMapper objectMapper = Config.getInstance().getMapper();

    
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Map<String, Object> bodyMap = (Map<String, Object>)exchange.getAttachment(BodyHandler.REQUEST_BODY);
        Reservation requestBody = Config.getInstance().getMapper().convertValue(bodyMap, Reservation.class);
        String orderId = exchange.getPathParameters().get("orderId").getFirst();
        try {
            Reservation reservation = service.updateReservation(orderId, requestBody);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
            exchange.setStatusCode(HttpStatus.OK.value());
            exchange.getResponseSender().send(objectMapper.writeValueAsString(reservation));
        } catch (ApiException e) {
            logger.error("Error Occurred: " + e.getMessage());
            setExchangeStatus(exchange, e.getStatus());
            exchange.getResponseSender().send(e.getMessage());
        } catch (Exception e) {
            logger.error("Error Occurred: " + e.getMessage());
            Error error = new Error();
            error.setCode("E30001");
            error.setMessage("Database process error");
            exchange.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            exchange.getResponseSender().send(objectMapper.writeValueAsString(error));
        }
    }
}
