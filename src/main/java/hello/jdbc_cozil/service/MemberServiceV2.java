package hello.jdbc_cozil.service;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜젝션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepositoryV2;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false);   //autoCommit x -> 트랜젝션 시작
            //비즈니스 로직
            bizLogic(con, fromId, toId, money);

            con.commit(); //성공 시, commit;

        }catch (Exception e){
            con.rollback(); //실패 시, rollback;
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }

    }
    //트랜젝션 로직과 비즈니스 로직 구분을 위해서 분리
    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV2.findById(con, fromId);
        Member toMember = memberRepositoryV2.findById(con, toId);

        memberRepositoryV2.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV2.update(con, toId, toMember.getMoney() + money);
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
