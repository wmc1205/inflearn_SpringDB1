package hello.jdbc_cozil.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc_cozil.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class SpringExceptionTest {
    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }
    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeQuery();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);
            int errorCode = e.getErrorCode();
            log.info("errorCode={}", errorCode);
            //org.h2.jdbc.JdbcSQLSyntaxErrorException
            log.info("error", e);
        } }
    /**
     * <스프링 예외 추상화>
     *
     *BadSQLGrammerException : 잘못된 쿼리로 매핑할 경우 던지는 예외
     * SQLErrorCodeSQLExceptionTraslator.translate(a,b,c) -> a : 읽을 수 있는 설명 b :실행 sql c : 발생한 예외 전달(e)
     * 추상화 예외 코드 확인 -> org.springframework.jdbc.support.sql-error-codes.xml 에서 확인 가능
     * 해당 스프링 예외 추상화 기술 덕분에 더 이상 특정 기술에 종속되어 있지 않는다. 스프링에서 특정 기술에 변화에 따라 데이터 접근 예외를 개별적으로 처리해준다.
     * 스프링 기술에 대해서는 종속적 -> 스프링 기술까지 예외 처리해도 되지만 권장하지는 않는다.
     */
   @Test
    void exceptionTranslator(){
       String sql ="select bad grammer";
       try{
           Connection connection = dataSource.getConnection();
           PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeQuery();
       }catch(SQLException e){
           assertThat(e.getErrorCode()).isEqualTo(42122);
           SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator();
           DataAccessException resultEx = exTranslator.translate("select",sql,e);
           log.info("resultEx",resultEx);
           assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
       }
    }
}
