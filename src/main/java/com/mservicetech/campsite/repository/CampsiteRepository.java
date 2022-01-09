package com.mservicetech.campsite.repository;
import com.mservicetech.campsite.ApplicationConfig;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.networknt.config.Config;
import org.apache.ibatis.session.SqlSession;

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

    List<LocalDate> findReserved() throws SQLException;


    int reserveDates(SqlSession session, List<LocalDate> dateList) throws  SQLException;

    int deleteDates(SqlSession session, List<LocalDate> dateList) throws  SQLException;

    List<LocalDate> verifyDates(SqlSession session, List<LocalDate> dateList);

    Client checkClientExisting(SqlSession session, Client client)  throws SQLException;

    long insertClient  (SqlSession session, Client client) throws SQLException;

    String createReservation(Reservation reservation) ;

    Reservation getReservation(String reservationId) throws  SQLException;

    int deleteReservation(Reservation reservation) ;

    int updateReservation(Reservation oldReservation, Reservation reservation) ;

}
