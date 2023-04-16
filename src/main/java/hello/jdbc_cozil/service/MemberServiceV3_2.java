package hello.jdbc_cozil.service;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜젝션 - 트랜젝션 템플릿 / 중복 코드 제거
 */
@Slf4j
public class MemberServiceV3_2 {


    //private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager,MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //txTemplate -> 트랜젝션 시작, 커밋, 롤백 코드가 모두 제거(생성자에서 자동 주입)
        txTemplate.executeWithoutResult((status) -> {
            //비즈니스 로직
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

    }
    //트랜젝션 로직과 비즈니스 로직 구분을 위해서 분리
    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
    private static void release(Connection con) {
        if(con != null){
            try{
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            }catch(Exception e){
                log.info("error",e);
            }
        }
    }


}
