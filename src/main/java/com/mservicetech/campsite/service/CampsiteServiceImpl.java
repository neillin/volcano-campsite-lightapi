package com.mservicetech.campsite.service;

import com.mservicetech.campsite.model.AvailableDates;
import com.mservicetech.campsite.model.Reservation;
import com.mservicetech.campsite.repository.CampsiteRepository;
import com.networknt.exception.ApiException;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.status.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class CampsiteServiceImpl implements CampsiteService{

    private  CampsiteRepository campsiteRepository = SingletonServiceFactory.getBean(CampsiteRepository.class);
    private static final Logger logger= LoggerFactory.getLogger(CampsiteServiceImpl.class);

    @Override
    public AvailableDates getAvailableDates(LocalDate startDate, LocalDate endDate) throws ApiException {
        AvailableDates availableDates = new AvailableDates();
        List<LocalDate> result = new ArrayList<>();
        long days = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay() ).toDays();
        LongStream.range(0, days).forEach(l-> result.add(startDate.plusDays(l)));
        try {
            List<LocalDate> reservedDates = campsiteRepository.findReserved();
            result.removeAll(reservedDates);
        } catch (SQLException e) {
            Status status = new Status("ERR30001");
            throw new ApiException(status);
        }
        availableDates.setDatelist(result);
        availableDates.setStartDate(startDate);
        availableDates.setEndDate(endDate);
        return availableDates;
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        String reservationId = campsiteRepository.createReservation(reservation);
        reservation.setId(reservationId);
        return reservation;
    }

    @Override
    public Reservation updateReservation(String reservationId, Reservation reservation) throws ApiException  {
        Reservation oldReservation;
        try {
            oldReservation = campsiteRepository.getReservation(reservationId);
        } catch (Exception e) {
            logger.error("Error on the get reservation:" + e);
            Status status = new Status("ERR30004");
            throw new ApiException(status);
        }
        if (oldReservation!=null && reservationMarch(reservation, oldReservation)) {
            reservation.setId(reservationId);
            campsiteRepository.updateReservation(oldReservation, reservation);
            return reservation;
        } else {
            logger.error("Cannot update the reservation, please check your request again");
            Status status = new Status("ERR30005");
            throw new ApiException(status);
        }

    }

    @Override
    public Reservation deleteReservation(String reservationId) throws ApiException {
        Reservation reservation;
        try {
            reservation = campsiteRepository.getReservation(reservationId);
        } catch (Exception e) {
            logger.error("Error on the get reservation:" + e);
            Status status = new Status("ERR30004");
            throw new ApiException(status);
        }
        int rec = campsiteRepository.deleteReservation(reservation);
        if (rec==0) logger.error("Error on the delete reservation, not record deleted");
        return reservation;
    }

    @Override
    public Reservation getReservation(String reservationId)  throws ApiException{
        try {
            return campsiteRepository.getReservation(reservationId);
        } catch (Exception e) {
            logger.error("Error on the  retrieve Reservation:" + e);
            Status status = new Status("ERR30002");
            throw new ApiException(status);
        }
    }

    private boolean reservationMarch(Reservation reservation, Reservation oldReservation) {
        return oldReservation.getClient().getEmail().equalsIgnoreCase(reservation.getClient().getEmail());
    }
}
