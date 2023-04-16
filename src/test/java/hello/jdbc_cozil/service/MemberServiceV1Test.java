package hello.jdbc_cozil.service;

import hello.jdbc_cozil.connection.ConnectionConst;
import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.MemberRepositoryV1;


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

class MemberServiceV1Test {

    public static final String member_A = "memberA";
    public static final String member_B = "memberB";
    public static final String member_ex = "ex";

    private MemberRepositoryV1 memberRepositoryV1;
    private MemberServiceV1 memberServiceV1;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV1 = new MemberRepositoryV1(dataSource);
        memberServiceV1 = new MemberServiceV1(memberRepositoryV1);
    }
    //매번 h2 콘솔에 가서 데이터 delete 하기 귀찮다.. 그래서!
    @AfterEach
    void after() throws SQLException {
        memberRepositoryV1.delete(member_A);
        memberRepositoryV1.delete(member_B);
        memberRepositoryV1.delete(member_ex);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(member_A,10000);
        Member memberB = new Member(member_B, 10000);
        memberRepositoryV1.save(memberA);
        memberRepositoryV1.save(memberB);
        //when
        memberServiceV1.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);
        //then
        Member findMemberA = memberRepositoryV1.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV1.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(member_A,10000);
        Member memberEx = new Member(member_ex, 10000);
        memberRepositoryV1.save(memberA);
        memberRepositoryV1.save(memberEx);
        //when
        assertThatThrownBy(()->  memberServiceV1.accountTransfer(memberA.getMemberId(),memberEx.getMemberId(),2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepositoryV1.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV1.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}