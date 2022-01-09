package com.mservicetech.campsite.repository;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiSqlSessionFactoryBuilder {

    private String configFile = "mybatis/mybatis-config.xml";
    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    private String environment = "development";

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void setSqlSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
        this.sqlSessionFactoryBuilder = sqlSessionFactoryBuilder;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * Factory method to create session factory based on config,
     * environment from config and properties file to populate parameters in config ${param}
     */
    public SqlSessionFactory create() throws IOException {
        InputStream inputStream ;
        Properties properties;
        inputStream = Resources.getResourceAsStream(configFile);
        properties = Resources.getResourceAsProperties("mybatis/mybatis.properties");
        return sqlSessionFactoryBuilder.build(inputStream, environment, properties);
    }
}
