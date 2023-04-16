package hello.jdbc_cozil.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc_cozil.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc_cozil.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {
    MemberRepositoryV1 repository;
    //@BeforeEach -> 각 테스트가 호출되기 직전 한번만 호출
    @BeforeEach
    void beforeEach(){
        //기본 DriveManager를 통해 항상 새로운 Connection을 생성
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);

        //connection Pool 사용 -> conn0 을 재사용
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException, InterruptedException {
        log.info("Start");
        Member member= new Member("memberV1", 10000);
        repository.save(member);
        //findById
        Member memberById = repository.findById(member.getMemberId());
        assertThat(memberById).isNotNull();
        //update: money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);
        //delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException .class);
    }

}