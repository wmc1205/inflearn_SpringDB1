package hello.jdbc_cozil.repository;

import hello.jdbc_cozil.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

@Slf4j
public class MemberRepositoryV6 implements MemberRepository{
    //JDBC 연결 코드 반복 문제 해결
    private final JdbcTemplate template;


    public MemberRepositoryV6(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?,?)";
        template.update(sql,member.getMemberId(),member.getMoney());
        return member;
    }

    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        //RowMapper 인터페이스 덕분에 Member 객체 자체를 반환할 수 있다.
        return template.queryForObject(sql, memberRowMapper(),memberId);
    }
    private RowMapper<Member> memberRowMapper(){
        return (rs,rowNum)->{
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }


    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money= ? where member_id= ?";
        // sql의 파라미터에 맞게 변수를 담는다.
        template.update(sql,money,memberId);
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id = ?";
        template.update(sql,memberId);
    }
}
