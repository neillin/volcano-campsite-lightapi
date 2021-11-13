package com.mservicetech.campsite.service;

import com.mservicetech.campsite.H2DatasourceStartupHook;
import com.mservicetech.campsite.model.AvailableDates;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.mservicetech.campsite.repository.CampsiteRepository;
import com.networknt.exception.ApiException;
import com.networknt.service.SingletonServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampsiteServiceImplTest {

    private static CampsiteService campsiteService = SingletonServiceFactory.getBean(CampsiteService.class);

    private static Client client;
    private static Reservation reservation;
    private static H2DatasourceStartupHook h2DatasourceStartupHook;

    @BeforeAll
    public static void setUp() {
        client = new Client();
        client.setName("test");
        client.setEmail("test.admin@gmail.com");
        reservation = new Reservation();
        reservation.setClient(client);
        reservation.setArrival(LocalDate.now());
        reservation.setDeparture(LocalDate.now().plusDays(3));
        h2DatasourceStartupHook= new H2DatasourceStartupHook();
        h2DatasourceStartupHook.onStartup();
    }

    @Test
    public void testCreateReservation() throws ApiException {
        Reservation result = campsiteService.createReservation(reservation);
        assertNotNull(result.getId());
        campsiteService.deleteReservation(result.getId());
    }

    @Test
    public void testDeleteReservation()  throws  ApiException{

        Reservation result = campsiteService.createReservation(reservation);

        Reservation result2 = campsiteService.deleteReservation(result.getId());
        assertNotNull(result2.getId());
    }

    @Test
    public void testGetAvailableDates()  throws  ApiException{
        AvailableDates availableDates = campsiteService.getAvailableDates(LocalDate.now(), LocalDate.now().plusDays(5));
        assertEquals(availableDates.getDatelist().size(), 5);
    }

    @Test
    public void testUpdateReservation()  throws  ApiException{

        Reservation result = campsiteService.createReservation(reservation);
        result.getDeparture().plusDays(2);
        Reservation result2 = campsiteService.updateReservation(result.getId(), result);
        assertNotNull(result2.getId());
        campsiteService.deleteReservation(result2.getId());

    }

    @Test
    public void testUpdateReservationException()  {
        Exception exception = assertThrows(ApiException.class, () -> {
            campsiteService.updateReservation("111111-11111", reservation);
        });
        String expectedMessage = "{\"statusCode\":0,\"code\":\"ERR30005\",\"message\":\"null\",\"description\":\"null\",\"severity\":\"null\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
