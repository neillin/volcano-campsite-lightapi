package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.exception.DataProcessException;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CampsiteRepositoryJdbcImpl implements CampsiteRepository{

    private static final Logger logger= LoggerFactory.getLogger(CampsiteRepository.class);


    SqlSessionFactory sqlSessionFactory;

    public CampsiteRepositoryJdbcImpl(ApiSqlSessionFactoryBuilder apiSqlSessionFactoryBuilder) throws IOException {
        this.sqlSessionFactory = apiSqlSessionFactoryBuilder.create();
    }

    @Override
    public List<LocalDate> findReserved() throws SQLException {
        List<LocalDate> reservedDates = new ArrayList<>();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            reservedDates = session.selectList("com.mservicetech.campsite.findReserved");
        }
        return reservedDates;
    }


    @Override
    public int reserveDates( SqlSession session,  List<LocalDate> dateList) throws  SQLException {
        int res = 0;
        for (LocalDate date: dateList) {
            res += session.insert("com.mservicetech.campsite.insertReserved", date);
        }
        session.flushStatements();
        return res;
    }

    @Override
    public int deleteDates(SqlSession session, List<LocalDate> dateList) throws  SQLException {
        int res = 0;
        for (LocalDate date: dateList) {
            res += session.delete("com.mservicetech.campsite.deleteReservedDates", date);
        }
        session.flushStatements();
        return res;

    }

    @Override
    public List<LocalDate> verifyDates(SqlSession session, List<LocalDate> dateList)  {

        List<LocalDate> reservedDates;
        reservedDates = session.selectList("com.mservicetech.campsite.verifyReservedDates", dateList);

        return reservedDates;
    }

    @Override
    public Client checkClientExisting(SqlSession session, Client client)  throws SQLException{

        Client existingClient=null;
        existingClient = session.selectOne("com.mservicetech.campsite.getClientByEmail", client);
        return existingClient;
    }

    @Override
    public long insertClient(SqlSession session, Client client) throws SQLException{
        session.insert("com.mservicetech.campsite.insertClient", client);
        return client.getId();
    }

    @Override
    public String createReservation(Reservation reservation)  {

        try (SqlSession session = sqlSessionFactory.openSession()) {
            List<LocalDate> dateList = new ArrayList<>();
            long days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay() ).toDays();
            LongStream.range(0, days).forEach(l-> dateList.add(reservation.getArrival().plusDays(l)));
            List<LocalDate> bookedList =  verifyDates(session, dateList);
            if ( bookedList.size()==0) {
                Client client = checkClientExisting(session, reservation.getClient());
                if (client==null) {
                    long clientId = insertClient(session, reservation.getClient());
                    reservation.getClient().setId(clientId);
                    logger.debug("new client inserted, client id:" + clientId);
                }else {
                    //TODO if client existing but name is different as database, do we need update client??
                    reservation.getClient().setId(client.getId());
                }
                int records = reserveDates(session, dateList);
                if (logger.isDebugEnabled()) logger.debug("Total days:" + records + " reserved for client" + reservation.getClient().getName());
                String reservationId = UUID.randomUUID().toString();
                reservation.setId(reservationId);
                session.insert("com.mservicetech.campsite.insertReservation", reservation);
                session.commit();
                return reservationId;
            } else {
                logger.error("Error on the reservation, campsite is no available for the input period.");
                throw new DataProcessException("Error on the reservation, campsite is no available for the input period. Follow dates have been booked" + reservedDates(bookedList));
            }

        } catch (Exception e) {
            logger.error("createReservation error:" + e);
            throw new DataProcessException("createReservation Db process error", e);
        }

    }

    @Override
    public Reservation getReservation(String reservationId) throws  SQLException{
        Reservation reservation ;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            reservation = session.selectOne("com.mservicetech.campsite.getReservation", reservationId);
        }
        return reservation;
    }

    @Override
    public int deleteReservation(Reservation reservation) {
        int rec=0;

        try (SqlSession session = sqlSessionFactory.openSession()) {
            List<LocalDate> dateList = new ArrayList<>();
            long days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay() ).toDays();
            LongStream.range(0, days).forEach(l-> dateList.add(reservation.getArrival().plusDays(l)));
            deleteDates(session, dateList);
            rec = session.update("com.mservicetech.campsite.deleteReservation", reservation.getId());
            session.commit();
        }  catch (Exception e) {
            logger.error("createReservation error:" + e);
            throw new DataProcessException("createReservation Db process error", e);
        }

        return rec;
    }

    @Override
    public int updateReservation(Reservation oldReservation, Reservation reservation) {
        int rec=0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            List<LocalDate> dateList = new ArrayList<>();
            long days = Duration.between(oldReservation.getArrival().atStartOfDay(), oldReservation.getDeparture().atStartOfDay() ).toDays();
            LongStream.range(0, days).forEach(l-> dateList.add(oldReservation.getArrival().plusDays(l)));
            deleteDates(session, dateList);
            dateList.clear();
            days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay() ).toDays();
            LongStream.range(0, days).forEach(l-> dateList.add(reservation.getArrival().plusDays(l)));

            List<LocalDate> bookedList =  verifyDates(session, dateList);
            if (bookedList.size()==0) {
                rec = session.update("com.mservicetech.campsite.updateReservation", reservation);
                reserveDates(session, dateList);
            } else {
                logger.error("Error on the reservation, campsite is no available for the input period.");
                throw new DataProcessException("Error on the reservation, campsite is no available for the input period. Follow dates have been booked" + reservedDates(bookedList));
            }
            session.commit();
        } catch (Exception e) {
            logger.error("createReservation error:" + e);
            throw new DataProcessException("updateReservation Db process error", e);
        }

        return rec;
    }

    private String reservedDates(List<LocalDate> dateList) {
        return dateList.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(" ; ", "{", "}"));
    }
}
