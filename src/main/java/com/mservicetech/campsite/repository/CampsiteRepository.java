package com.mservicetech.campsite.repository;
import com.mservicetech.campsite.ApplicationConfig;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.networknt.config.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Define data repository  interface methods
 * <p>
 * Supports volcano campsite reservation information to persist to backend repository
 *
 * @author Gavin Chen
 */
public interface CampsiteRepository {

    ApplicationConfig appConfig = (ApplicationConfig) Config.getInstance().getJsonObjectConfig(ApplicationConfig.CONFIG_NAME, ApplicationConfig.class);
    default String getQueryString (String queryName) {
        return appConfig.getQueryMap().get(queryName);
    }

    List<LocalDate> findReserved() throws SQLException;


    int reserveDates(Connection connection, List<LocalDate> dateList) throws  SQLException;

    int deleteDates(Connection connection, List<LocalDate> dateList) throws  SQLException;

    List<LocalDate> verifyDates(Connection connection, List<LocalDate> dateList);

    Client checkClientExisting(Connection connection, Client client)  throws SQLException;

    long insertClient(Connection connection,  Client client) throws SQLException;

    String createReservation(Reservation reservation) ;

    Reservation getReservation(String reservationId) throws  SQLException;

    int deleteReservation(Reservation reservation) ;

    int updateReservation(Reservation oldReservation, Reservation reservation) ;

}
