package com.mservicetech.campsite.repository.mapper;

import com.mservicetech.campsite.model.Client;
import org.apache.ibatis.annotations.Select;

public interface CampsiteMapper {
    @Select("SELECT id, full_name, email FROM client WHERE email = #{email}")
    Client selectClientByEmail(String email);

}
