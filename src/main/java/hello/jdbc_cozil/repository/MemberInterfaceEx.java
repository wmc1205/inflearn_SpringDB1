package hello.jdbc_cozil.repository;

import hello.jdbc_cozil.domain.Member;

import java.sql.SQLException;

/**
 * interface 구현
 * DI 적용을 하기 위해서는 interface가 설계되어야 하는데, 구현 메서드의 예외처리가 인터페이스에도 그대로 반영되어야 한다.
 * 그렇지 않으면, 컴파일 오류를 던진다.
 */

public interface MemberInterfaceEx {
    Member save(Member member) throws SQLException;
    Member findById(String memberId) throws SQLException;
    void update(String memberId,int money) throws SQLException;
    void delete(String memberId);
}
