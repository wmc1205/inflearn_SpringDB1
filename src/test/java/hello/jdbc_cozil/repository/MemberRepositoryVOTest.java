package hello.jdbc_cozil.repository;

import hello.jdbc_cozil.domain.Member;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryVOTest {
    MemberRepositoryVO repository = new MemberRepositoryVO();
    @Test
    void crud() throws SQLException {
        Member memberV0 = new Member("memberV0", 10000);
        repository.save(memberV0);
    }
}