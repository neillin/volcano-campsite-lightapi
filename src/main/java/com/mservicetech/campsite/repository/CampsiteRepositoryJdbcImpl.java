package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.MybatisSessionManager;
import com.mservicetech.campsite.exception.DataProcessException;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CampsiteRepositoryJdbcImpl implements CampsiteRepository{

    private static final Logger logger= LoggerFactory.getLogger(CampsiteRepository.class);

    private static  final  String QUERY_GET_ALL_RESERVED = "getReservedDates";
    private static  final  String QUERY_INSERT_RESERVED_DATE = "insertReservedDates";
    private static  final  String QUERY_GET_RESERVATION = "getReservation";
    private static  final  String QUERY_VERIFY_RESERVED_DATE = "verifyReservedDates";
    private static  final  String QUERY_GET_CLIENT_BY_EMAIL = "getClientByEmail";
    private static  final  String QUERY_INSERT_CLIENT = "insertClient";
    private static  final  String QUERY_DELETE_RESERVED_DATE = "deleteReservedDates";
    private static  final  String QUERY_INSERT_RESERVATION = "insertReservation";
    private static  final  String QUERY_DELETE_RESERVATION = "deleteReservation";
    private static  final  String QUERY_UPDATE_RESERVATION = "updateReservation";

    private MybatisSessionManager sessionManager = MybatisSessionManager.INSTANCE;

    public CampsiteRepositoryJdbcImpl() {
    }

    @Override
    public List<LocalDate> findReserved() {
        return sessionManager.executeWithSession( session -> {
             List<LocalDate> reservedDates = new ArrayList<>();
            var connection = session.getConnection();
            try (final PreparedStatement statement = connection.prepareStatement(getQueryString(QUERY_GET_ALL_RESERVED))) {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    reservedDates.add(rs.getDate("reserved_date").toLocalDate());
                }
            } catch ( SQLException e) {
                throw new DataProcessException("findReserved", e);
            }
            return reservedDates;
        });
    }


    @Override
    public int reserveDates(List<LocalDate> dateList) throws  SQLException {
        return sessionManager.executeWithSession( session -> {
            Connection connection = session.getConnection() ; 
            try (final PreparedStatement ps = connection.prepareStatement(getQueryString(QUERY_INSERT_RESERVED_DATE))) {
                for (LocalDate date: dateList) {
                    ps.setDate(1, Date.valueOf(date));
                    ps.addBatch();
                }
                int[] rows = ps.executeBatch();
                return Arrays.stream(rows).sum();
            } catch ( SQLException e) {
                throw new DataProcessException("reserveDates", e);
            }
        });
    }

    @Override
    public int deleteDates(List<LocalDate> dateList) {
        return sessionManager.executeWithSession( session -> {
            Connection connection = session.getConnection() ; 
            try(final PreparedStatement ps = connection.prepareStatement(getQueryString(QUERY_DELETE_RESERVED_DATE))) {
                for (LocalDate date: dateList) {
                    ps.setDate(1, Date.valueOf(date));
                    ps.addBatch();
                }
                int[] rows = ps.executeBatch();
                return Arrays.stream(rows).sum();
            } catch ( SQLException e) {
                throw new DataProcessException("deleteDates", e);
            }
        });
    }

    @Override
    public List<LocalDate> verifyDates(List<LocalDate> dateList)  {
        String inSql = String.join(",", Collections.nCopies(dateList.size(), "?"));
        String query = String.format(getQueryString(QUERY_VERIFY_RESERVED_DATE), inSql);
        logger.debug("VerifyDates dynamic query:" + query);
        return sessionManager.executeWithSession( session -> {
            Connection connection = session.getConnection() ; 
            List<LocalDate> reservedDates = new ArrayList<>();
            try (final PreparedStatement statement = connection.prepareStatement(query)) {
                    int i=1;
                    for (LocalDate date:dateList) {
                        statement.setDate(i, Date.valueOf(date));
                        i++;
                    }
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        reservedDates.add(rs.getDate("reserved_date").toLocalDate());
                    }

            } catch (SQLException e) {
                logger.error("verifyDates error:" + e);
                throw new DataProcessException("verifyDates Db process error", e);
            }
            return reservedDates;
        });
    }

    @Override
    public Client checkClientExisting(Client client)  throws SQLException{
        if (logger.isDebugEnabled()) logger.debug("query for get CLIENT:" + getQueryString(QUERY_GET_CLIENT_BY_EMAIL));
        return sessionManager.executeWithSession( session -> {
            Connection connection = session.getConnection() ; 
            try (final PreparedStatement statement = connection.prepareStatement(getQueryString(QUERY_GET_CLIENT_BY_EMAIL))) {
                Client existingClient=null;
                statement.setString(1, client.getEmail());
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    existingClient = new Client();
                    existingClient.setName(rs.getString("full_name"));
                    existingClient.setEmail(rs.getString("email"));
                    existingClient.setId(rs.getLong("id"));
                }
                return existingClient;
            } catch (SQLException e) {
                logger.error("checkClientExisting error:" + e);
                throw new DataProcessException("checkClientExisting Db process error", e);
            }
        });
    }

    @Override
    public long insertClient(Client client) {
        return sessionManager.executeWithSession( session -> {
            Connection connection = session.getConnection() ; 
            try (final PreparedStatement ps = connection.prepareStatement(getQueryString(QUERY_INSERT_CLIENT), Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, client.getName());
                ps.setString(2,client.getEmail());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys != null) {
                    if (keys.next()) {
                    return keys.getLong(1);
                    }
                }
                return 0L;
            } catch (SQLException e) {
                logger.error("insertClient error:" + e);
                throw new DataProcessException("insertClient Db process error", e);
            }
        });
    }

    @Override
    public String createReservation(Reservation reservation)  {
        return sessionManager.executeWithSession( session -> {
            try {
                Connection connection = session.getConnection() ; 
                List<LocalDate> dateList = new ArrayList<>();
                long days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay() ).toDays();
                LongStream.range(0, days).forEach(l-> dateList.add(reservation.getArrival().plusDays(l)));
                List<LocalDate> bookedList =  verifyDates(dateList);
                if ( bookedList.size()==0) {
                    Client client = checkClientExisting(reservation.getClient());
                    if (client==null) {
                        long clientId = insertClient(reservation.getClient());
                        reservation.getClient().setId(clientId);
                        logger.debug("new client inserted, client id:" + clientId);
                    }else {
                        //TODO if client existing but name is different as database, do we need update client??
                        reservation.getClient().setId(client.getId());
                    }
                    int records = reserveDates(dateList);
                    if (logger.isDebugEnabled()) logger.debug("Total days:" + records + " reserved for client" + reservation.getClient().getName());
                    String reservationId = UUID.randomUUID().toString();
                    try (final PreparedStatement ps = connection.prepareStatement(getQueryString(QUERY_INSERT_RESERVATION))) {
                        ps.setString(1, reservationId);
                        ps.setLong(2, reservation.getClient().getId());
                        ps.setDate(3, Date.valueOf(reservation.getArrival()));
                        ps.setDate(4, Date.valueOf(reservation.getDeparture()));
                        ps.executeUpdate();
                    }
                    connection.commit();
                    return reservationId;
                } else {
                    logger.error("Error on the reservation, campsite is no available for the input period.");
                    throw new DataProcessException("Error on the reservation, campsite is no available for the input period. Follow dates have been booked" + reservedDates(bookedList));
                }
            } catch (DataProcessException e) {
                throw e;
            } catch (Exception e) {
                logger.error("createReservation error:" + e);
                throw new DataProcessException("createReservation Db process error", e);
            }
        });
    }

    @Override
    public Reservation getReservation(String reservationId) {
        if (logger.isDebugEnabled()) logger.debug("query for get CLIENT:" + getQueryString(QUERY_GET_RESERVATION));
        return sessionManager.executeWithSession( session -> {
            Connection connection = session.getConnection();
            try(final PreparedStatement statement = connection.prepareStatement(getQueryString(QUERY_GET_RESERVATION))) {
                Reservation reservation = null;

                statement.setString(1, reservationId);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    Client client = new Client();
                    client.setName(rs.getString("full_name"));
                    client.setEmail(rs.getString("email"));
                    client.setId(rs.getLong("client_id"));
                    reservation = new Reservation();
                    reservation.setId(rs.getString("id"));
                    reservation.setArrival(rs.getDate("arrival_date").toLocalDate());
                    reservation.setDeparture(rs.getDate("departure_date").toLocalDate());
                    reservation.setClient(client);
                }
                return reservation;
            } catch (SQLException e) {
                logger.error("getReservation error:" + e);
                throw new DataProcessException("getReservation Db process error", e);
            }
        });
    }

    @Override
    public int deleteReservation(Reservation reservation) {
        return sessionManager.executeWithSession( session -> {
            try {
                int rec = 0;
                Connection connection = session.getConnection();
                List<LocalDate> dateList = new ArrayList<>();
                long days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay() ).toDays();
                LongStream.range(0, days).forEach(l-> dateList.add(reservation.getArrival().plusDays(l)));
                deleteDates(dateList);
                try (final PreparedStatement ps = connection.prepareStatement(getQueryString(QUERY_DELETE_RESERVATION))) {
                    ps.setString(1, reservation.getId());
                    rec = ps.executeUpdate();
                }
                connection.commit();
                return rec;
            } catch (Exception e) {
                logger.error("deleteReservation error:" + e);
                throw new DataProcessException("deleteReservation Db process error", e);
            }
        });
    }

    @Override
    public int updateReservation(Reservation oldReservation, Reservation reservation) {
        return sessionManager.executeWithSession( session -> {
            Connection connection = session.getConnection();
            try {
                int rec=0;
                connection.setAutoCommit(false);
                List<LocalDate> dateList = new ArrayList<>();
                long days = Duration.between(oldReservation.getArrival().atStartOfDay(), oldReservation.getDeparture().atStartOfDay() ).toDays();
                LongStream.range(0, days).forEach(l-> dateList.add(oldReservation.getArrival().plusDays(l)));
                deleteDates(dateList);
                dateList.clear();
                days = Duration.between(reservation.getArrival().atStartOfDay(), reservation.getDeparture().atStartOfDay() ).toDays();
                LongStream.range(0, days).forEach(l-> dateList.add(reservation.getArrival().plusDays(l)));

                List<LocalDate> bookedList =  verifyDates(dateList);
                if (bookedList.size()==0) {
                    try (final PreparedStatement ps = connection.prepareStatement(getQueryString(QUERY_UPDATE_RESERVATION))) {
                        ps.setDate(1, Date.valueOf(reservation.getArrival()));
                        ps.setDate(2, Date.valueOf(reservation.getDeparture()));
                        ps.setString(3, reservation.getId());
                        rec = ps.executeUpdate();
                    }
                    reserveDates(dateList);
                } else {
                    logger.error("Error on the reservation, campsite is no available for the input period.");
                    throw new DataProcessException("Error on the reservation, campsite is no available for the input period. Follow dates have been booked" + reservedDates(bookedList));
                }
                connection.commit();
                return rec;
            } catch (DataProcessException e) {
                throw e;
            } catch (Exception e) {
                logger.error("updateReservation error:" + e);
                throw new DataProcessException("updateReservation Db process error", e);
            }
        });
    }

    private String reservedDates(List<LocalDate> dateList) {
        return dateList.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(" ; ", "{", "}"));
    }
}
