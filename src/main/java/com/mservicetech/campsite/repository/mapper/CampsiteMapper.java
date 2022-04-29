package com.mservicetech.campsite.repository.mapper;

import java.sql.Date;
import java.util.List;

import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.One;

public interface CampsiteMapper {

    @Select("SELECT id, full_name, email FROM client WHERE email= #{email} ")
    @Results({
        @Result(property = "id", column = "ID"),
        @Result(property = "email", column = "EMAIL"),
        @Result(property = "name", column = "FULL_NAME")
    })
    Client selectClientByEmail(String email);

    @Select("SELECT id, full_name, email FROM client WHERE id= #{id} ")
    @Results({
        @Result(property = "id", column = "ID"),
        @Result(property = "email", column = "EMAIL"),
        @Result(property = "name", column = "FULL_NAME")
    })
    Client selectClientById(Long id);

    @Select("SELECT reserved_date FROM reserved")
    List<Date> getReservedDates();

    @Select(
       "<script>SELECT reserved_date  FROM reserved WHERE reserved_date IN "+
       "<foreach collection=\"items\" item=\"item\" separator=\",\" open=\"(\" close=\")\">'${item}'</foreach></script>"
    )
    List<Date> verifyReserveDates(@Param("items") List<Date> dates);


    @Insert("INSERT INTO client(full_name, email ) VALUES( #{name}, #{email} ) ")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertClient(Client client);
    
    @Insert("INSERT INTO reserved(reserved_date ) VALUES( #{date} )")
    void insertReservedDate(Date date);

    @Delete(
       "<script>DELETE FROM reserved WHERE reserved_date IN "+
       "<foreach collection=\"items\" item=\"item\" separator=\",\" open=\"(\" close=\")\">'${item}'</foreach></script>"
    )
    int deleteReservedDates(@Param("items") List<Date> dates);

    @Insert("INSERT INTO reservation(id, client_Id, arrival_date, departure_date ) VALUES( #{id}, #{client_id}, #{arrivalDate}, #{departureDate} )")
    void insertReservation(@Param("id") String id, @Param("client_id") Long client_id, @Param("arrivalDate") Date arrivalDate, @Param("departureDate") Date departureDate);

    @Select("SELECT id, client_id, arrival_date, departure_date FROM reservation WHERE id = #{id} AND status = 'Active' ")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "client", column = "client_id", one = @One(select = "selectClientById")),
        @Result(property = "arrival", column = "arrival_date"),
        @Result(property = "departure", column = "departure_date")
    })
    Reservation selectReservation(@Param("id") String reservationId);

    @Delete("UPDATE reservation SET status = 'Inactive' WHERE id = #{reservationId} ")
    int deleteReservation(String reservationId);


    @Update("UPDATE reservation SET arrival_date= #{arrival} , departure_date= #{departure} WHERE id = #{id}")
    int updateReservation(Reservation data);

}
