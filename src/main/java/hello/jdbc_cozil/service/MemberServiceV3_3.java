package hello.jdbc_cozil.service;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜젝션 - @Transactional AOP
 * -트랜젝션 로직 제거
 * -@Transactional 사용으로 비즈니스로직에 트랜젝션 설정
 */
@Slf4j
public class MemberServiceV3_3 {



    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3( MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    //트랜젝션 설정
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
                bizLogic(fromId, toId, money);
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


}
