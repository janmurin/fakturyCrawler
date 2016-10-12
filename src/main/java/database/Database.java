/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.util.List;
import java.util.logging.Logger;
import org.hsqldb.jdbc.JDBCDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author jan.murin
 */
public class Database {

    private final Logger logger = Logger.getLogger(Database.class.getName());
    private final JdbcTemplate jdbcTemplate;
    public static final String DB_NAME = "fakturydb";
    public static final int PORT = 3321;
    public static final String DB_URL = "jdbc:hsqldb:hsql://localhost:3320/gltvdb";
    public static final String DB_USERNAME = "sa";
    public static final String DB_PASSWORD = "";
    private final JDBCDataSource dataSource;

    public Database() {
        dataSource = new JDBCDataSource();
        dataSource.setUrl(DB_URL);
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

}
