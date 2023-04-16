package hello.jdbc_cozil.service;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.MemberRepositoryV2;
import hello.jdbc_cozil.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜젝션 - 트랜젝션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    //private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepositoryV3;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //트랜젝션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());


        try {

            //비즈니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(status);//성공시 commit;
        }catch (Exception e){
            transactionManager.rollback(status);//실패 시, rollback;
            throw new IllegalStateException(e);
        }

    }
    //트랜젝션 로직과 비즈니스 로직 구분을 위해서 분리
    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV3.findById(fromId);
        Member toMember = memberRepositoryV3.findById(toId);

        memberRepositoryV3.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV3.update(toId, toMember.getMoney() + money);
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
