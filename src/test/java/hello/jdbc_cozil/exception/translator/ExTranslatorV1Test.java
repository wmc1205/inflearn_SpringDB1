package hello.jdbc_cozil.exception.translator;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.ex.MyDBException;
import hello.jdbc_cozil.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc_cozil.connection.ConnectionConst.*;

public class ExTranslatorV1Test {

    Repository repository;
    Service service;
    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME, PASSWORD);
       repository = new Repository(dataSource);
       service = new Service(repository);
    }
    @Test
    void duplicateKeySave() {
        service.create("myId");
        service.create("myId");//같은 ID 저장 시도
    }
    @Slf4j
    @RequiredArgsConstructor
    static class Service{
        private final Repository repository;

        public void create(String memberId){
            try {
                repository.save(new Member(memberId,0));
                log.info("memberId={}",memberId);
            }catch (MyDuplicateKeyException e){
                log.info("키 중복, 복구 시도");
                String retryId = generateNewId(memberId);
                log.info("retryId={}",retryId);
                repository.save(new Member(retryId,0));

            }catch (MyDBException e){
                throw e;
            }
        }
        private String generateNewId(String memberId){
            return memberId + new Random().nextInt(10000);
        }
    }

    @RequiredArgsConstructor
    static class Repository{
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id,money) values (?,?)";
            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                //h2 DB 경우
                //키 중복 예외 : 23505
                if (e.getErrorCode() == 23505) {
                    throw new MyDuplicateKeyException(e);
                }

            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }

            return member;
        }
    }
}
