package hello.jdbc_cozil.repository;

import hello.jdbc_cozil.domain.Member;

import java.sql.SQLException;

/**
 * interface 런타임 예외 처리
 */
public interface MemberRepository {
    Member save(Member member);
    Member findById(String memberId) ;
    void update(String memberId,int money);
    void delete(String memberId);
}

