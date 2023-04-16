package hello.jdbc_cozil.service;

import hello.jdbc_cozil.connection.ConnectionConst;
import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.MemberRepositoryV1;


import hello.jdbc_cozil.repository.MemberRepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc_cozil.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 기본 동작, 트랜젝션이 없어서 문제가 발생
 */

@Slf4j
class MemberServiceV2Test {

    public static final String member_A = "memberA";
    public static final String member_B = "memberB";
    public static final String member_ex = "ex";

    private MemberRepositoryV2 memberRepositoryV2;
    private MemberServiceV2 memberServiceV2;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV2 = new MemberRepositoryV2(dataSource);
        memberServiceV2 = new MemberServiceV2(dataSource,memberRepositoryV2);
    }
    //매번 h2 콘솔에 가서 데이터 delete 하기 귀찮다.. 그래서!
    @AfterEach
    void after() throws SQLException {
        memberRepositoryV2.delete(member_A);
        memberRepositoryV2.delete(member_B);
        memberRepositoryV2.delete(member_ex);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(member_A,10000);
        Member memberB = new Member(member_B, 10000);
        memberRepositoryV2.save(memberA);
        memberRepositoryV2.save(memberB);
        //when(connection 재사용)
        log.info("start----------");
        memberServiceV2.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);
        log.info("end----------");
        //then
        Member findMemberA = memberRepositoryV2.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV2.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(member_A,10000);
        Member memberEx = new Member(member_ex, 10000);
        memberRepositoryV2.save(memberA);
        memberRepositoryV2.save(memberEx);
        //when
        assertThatThrownBy(()->  memberServiceV2.accountTransfer(memberA.getMemberId(),memberEx.getMemberId(),2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepositoryV2.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV2.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}