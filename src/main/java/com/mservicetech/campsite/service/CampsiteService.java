package com.mservicetech.campsite.service;


import com.mservicetech.campsite.model.AvailableDates;
import com.mservicetech.campsite.model.Reservation;
import com.networknt.exception.ApiException;

import java.time.LocalDate;

/**
 * Define business service interface methods
 * <p>
 * Supports volcano campsite reservation business process
 *
 * @author Gavin Chen
 */
public interface CampsiteService {

	/**
	 * Search from repository to get all available date for reservation.
	 * It use two parameters as searchCriteria
	 *
	 * @param startDate The start time for search.
	 * @param endDate The end time for search.
	 */
	AvailableDates getAvailableDates(LocalDate startDate, LocalDate endDate) throws ApiException;

	/**
	 * Create Reservation method
	 * It use input reservation information to reserve the campsite
	 *
	 * @param reservation reservation detail for the campsite reserve.
	 */
	Reservation createReservation(Reservation reservation) ;

	/**
	 * Change Reservation method
	 * It use input new reservation information to change reservation which get by the reservationId
	 *
	 * @param reservationId reservation detail for the campsite reserve.
	 * @param reservation reservation detail for the campsite reserve.
	 */
	Reservation updateReservation(String reservationId, Reservation reservation) throws ApiException ;

	/**
	 * Delete Reservation method
	 * Get the reservation base on the provide id and delete it from database.
	 *
	 * @param reservationId reservation id which need be canceled .
	 */
	Reservation deleteReservation(String reservationId) throws ApiException;

	/**
	 * get Reservation by reservationId
	 * Get the reservation base on the provide id  from database.
	 *
	 * @param reservationId reservation id which need be canceled .
	 */
	Reservation getReservation(String reservationId)  throws ApiException;
}
