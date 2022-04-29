package com.mservicetech.campsite.service;

import com.mservicetech.campsite.H2DatasourceStartupHook;
import com.mservicetech.campsite.model.AvailableDates;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.networknt.exception.ApiException;
import com.networknt.service.SingletonServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(OrderAnnotation.class)
public class CampsiteServiceImplTest {

    private static CampsiteService campsiteService = SingletonServiceFactory.getBean(CampsiteService.class);

    private static Client client;
    private static Reservation reservation;
    private static H2DatasourceStartupHook h2DatasourceStartupHook;

    @BeforeAll
    public static void setUp() throws SQLException {
        client = new Client();
        client.setName("test");
        client.setEmail("test.admin@gmail.com");
        reservation = new Reservation();
        reservation.setClient(client);
        reservation.setArrival(LocalDate.now());
        reservation.setDeparture(LocalDate.now().plusDays(3));
        h2DatasourceStartupHook= new H2DatasourceStartupHook();
        h2DatasourceStartupHook.onStartup();
        cleanupDB();
    }

    private static void cleanupDB() throws SQLException {
        Connection conn = H2DatasourceStartupHook.dataSource.getConnection();
        conn.createStatement().execute("DELETE FROM reserved");
        conn.createStatement().execute("DELETE FROM reservation");
        conn.createStatement().execute("DELETE FROM client");
        conn.createStatement().execute("INSERT INTO reserved(reserved_date ) VALUES('2025-11-05')");
        conn.createStatement().execute("INSERT INTO client(full_name, email ) VALUES('Admin', 'volcano.admin@gmail.com')");
        conn.close();
    }

    @Test
    @Order(1)
    public void testCreateReservation() throws ApiException {
        Reservation result = campsiteService.createReservation(reservation);
        assertNotNull(result.getId());
        campsiteService.deleteReservation(result.getId());
    }

    @Test
    @Order(2)
    public void testDeleteReservation()  throws  ApiException{

        Reservation result = campsiteService.createReservation(reservation);

        Reservation result2 = campsiteService.deleteReservation(result.getId());
        assertNotNull(result2.getId());
    }

    @Test
    @Order(3)
    public void testGetAvailableDates()  throws  ApiException{
        AvailableDates availableDates = campsiteService.getAvailableDates(LocalDate.now(), LocalDate.now().plusDays(5));
        assertEquals(availableDates.getDatelist().size(), 5);
    }

    @Test
    @Order(4)
    public void testUpdateReservation()  throws  ApiException{

        Reservation result = campsiteService.createReservation(reservation);
        result.getDeparture().plusDays(2);
        Reservation result2 = campsiteService.updateReservation(result.getId(), result);
        assertNotNull(result2.getId());
        campsiteService.deleteReservation(result2.getId());

    }

    @Test
    @Order(5)
    public void testUpdateReservationException()  {
        Exception exception = assertThrows(ApiException.class, () -> {
            campsiteService.updateReservation("111111-11111", reservation);
        });
        String expectedMessage = "{\"statusCode\":0,\"code\":\"ERR30005\",\"message\":\"null\",\"description\":\"null\",\"severity\":\"null\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
