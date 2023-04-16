package hello.jdbc_cozil.connection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class DBConnectionUtilTest {
    @Test
    void connection(){
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();

    }
}
