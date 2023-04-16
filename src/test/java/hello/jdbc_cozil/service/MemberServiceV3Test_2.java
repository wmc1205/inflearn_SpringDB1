package hello.jdbc_cozil.service;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc_cozil.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜젝션 매니저 사용
 */

@Slf4j
class MemberServiceV3Test_2 {

    public static final String member_A = "memberA";
    public static final String member_B = "memberB";
    public static final String member_ex = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_2 memberService;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_2(transactionManager,memberRepository);
    }
    //매번 h2 콘솔에 가서 데이터 delete 하기 귀찮다.. 그래서!
    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(member_A);
        memberRepository.delete(member_B);
        memberRepository.delete(member_ex);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(member_A,10000);
        Member memberB = new Member(member_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when(connection 재사용)
        log.info("start----------");
        memberService.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);
        log.info("end----------");
        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(member_A,10000);
        Member memberEx = new Member(member_ex, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);
        //when
        assertThatThrownBy(()->  memberService.accountTransfer(memberA.getMemberId(),memberEx.getMemberId(),2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}