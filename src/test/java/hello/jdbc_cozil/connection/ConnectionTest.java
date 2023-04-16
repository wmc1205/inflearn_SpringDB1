package hello.jdbc_cozil.connection;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.DriverDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc_cozil.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {
    @Test
    void driveManager() throws SQLException {
        Connection connection1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection connection2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={},class={}",connection1,connection1.getClass());
        log.info("connection={},class={}",connection2,connection2.getClass());
    }

    @Test
    void dateSourceConnectionPool() throws SQLException, InterruptedException {
        //커넥션 풀링(Hikari CP) 사용
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("myPool");

        useDataSource(dataSource);
        Thread.sleep(1000);
    }


    @Test
    void dataSourceDriverManager() throws SQLException {
        //DriverManagerDataSource - 항상 새로운 커넥션을 획득
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={},class={}",con1,con1.getClass());
        log.info("connection={},class={}",con2,con2.getClass());
    }
}
