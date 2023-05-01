package hello.jdbc_cozil.service;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class MemberServiceV5 {
    private final MemberRepository memberRepository;

    public MemberServiceV5(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    //트랜젝션 설정
    @Transactional(propagation = Propagation.REQUIRED)
    public void accountTransfer(String fromId, String toId, int money){
        bizLogic(fromId, toId, money);

    }
    // 비즈니스 / 트랜젝션 로직 분리
    private void bizLogic(String fromId, String toId, int money){
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId,toMember.getMoney()+money);
    }
    //예외 검증
    private static void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

}
