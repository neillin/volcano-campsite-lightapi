package com.mservicetech.campsite.repository;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.mservicetech.campsite.MybatisSessionManager;
import com.mservicetech.campsite.exception.DataProcessException;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.mservicetech.campsite.repository.mapper.CampsiteMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CampsiteRepositoryMyBatisImpl implements CampsiteRepository {

    private static final Logger log = LoggerFactory.getLogger(CampsiteRepositoryMyBatisImpl.class);

    private MybatisSessionManager sessionManager = MybatisSessionManager.INSTANCE;

    public CampsiteRepositoryMyBatisImpl() {
    }
    
    @Override
    public List<LocalDate> findReserved() {
       return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            return mapper.getReservedDates().stream().map(v -> v.toLocalDate()).collect(Collectors.toList());
        });
    }

    @Override
    public int reserveDates(List<LocalDate> dateList) {
        return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            dateList.forEach(d -> mapper.insertReservedDate(Date.valueOf(d)));
            return dateList.size();
        });
    }

    @Override
    public int deleteDates(List<LocalDate> dateList) {
        return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            var dates = dateList.stream().map(d -> Date.valueOf(d)).collect(Collectors.toList());
            var v = mapper.deleteReservedDates(dates);
            return v;
        });
    }

    @Override
    public List<LocalDate> verifyDates(List<LocalDate> dateList) {
        return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            var dates = dateList.stream().map(d -> Date.valueOf(d)).collect(Collectors.toList());
            return mapper.verifyReserveDates(dates).stream().map(v -> v.toLocalDate()).collect(Collectors.toList());
        });
    }

    @Override
    public Client checkClientExisting(Client client) throws SQLException {
        return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            return mapper.selectClientByEmail(client.getEmail());
        });
    }

    @Override
    public long insertClient(Client client) throws SQLException {
        return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            mapper.insertClient(client);
            return client.getId();
        });
    }

    @Override
    public String createReservation(Reservation reservation) {
        try {
            return sessionManager.executeWithSession( session -> {
                var mapper = session.getMapper(CampsiteMapper.class);
                List<LocalDate> dateList = new ArrayList<>();
                long days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay() ).toDays();
                LongStream.range(0, days).forEach(l-> dateList.add(reservation.getArrival().plusDays(l)));
                List<LocalDate> bookedList =  verifyDates(dateList);
                if ( bookedList.size()==0) {
                    Client client = mapper.selectClientByEmail(reservation.getClient().getEmail());
                    if (client==null) {
                        mapper.insertClient(reservation.getClient());
                        log.debug("new client inserted, client id:" + reservation.getClient().getId());
                    }else {
                        //TODO if client existing but name is different as database, do we need update client??
                        reservation.setClient(client);
                    }
                    int records = reserveDates(dateList);
                    if (log.isDebugEnabled()) log.debug("Total days:" + records + " reserved for client" + reservation.getClient().getName());
                    String reservationId = UUID.randomUUID().toString();
                    mapper.insertReservation(reservationId, reservation.getClient().getId(),  Date.valueOf(reservation.getArrival()),  Date.valueOf(reservation.getDeparture()));
                    return reservationId;
                } else {
                    log.error("Error on the reservation, campsite is no available for the input period.");
                    throw new DataProcessException("Error on the reservation, campsite is no available for the input period. Follow dates have been booked" + formatReservedDates(bookedList));
                }
            });
        }  catch (Exception e) {
            log.error("createReservation error:" + e);
            throw new DataProcessException("createReservation Db process error", e);
        }
    }

    @Override
    public Reservation getReservation(String reservationId) throws SQLException {
        return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            return mapper.selectReservation(reservationId);
        });
    }

    @Override
    public int deleteReservation(Reservation reservation) {
        return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            List<LocalDate> dateList = new ArrayList<>();
            long days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay() ).toDays();
            LongStream.range(0, days).forEach(l-> dateList.add(reservation.getArrival().plusDays(l)));
            this.deleteDates(dateList);
            var v = mapper.deleteReservation(reservation.getId());
            return v;
        });
    }

    @Override
    public int updateReservation(Reservation oldReservation, Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(oldReservation.getId());
        }
        if (reservation.getId() == null || !reservation.getId().equals(oldReservation.getId())) {
            throw new IllegalArgumentException("Old reservation and new reservation has different id or NULL id");
        }
        return sessionManager.executeWithSession( session -> {
            var mapper = session.getMapper(CampsiteMapper.class);
            var v = mapper.updateReservation(reservation);
            return v;
        });
    }
    
    private String formatReservedDates(List<LocalDate> dateList) {
        return dateList.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(" ; ", "{", "}"));
    }
}
