package hello.jdbc_cozil.repository;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.ex.MyDBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC 반복 문제 해결 -> JDBCTemplate 사용
 * connection, preparedStatment, resultSet 등 모든 것을 다 JdbcTemplate 내부에서 처리된다.
 * connection 동기화까지 처리된다.
 */

@Slf4j
public class MemberRepositoryV5 implements MemberRepository{
    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
       this.template = new JdbcTemplate(dataSource);
    }


    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?, ?)";
        template.update(sql,member.getMemberId(),member.getMoney());
        return member;

    }
    public Member findById(String memberId)  {
        String sql = "select * from member where member_id = ?";
       //query 단건 조회
        return template.queryForObject(sql, memberRowMapper(), memberId);

    }

    private RowMapper<Member> memberRowMapper(){
        return(rs,rowNum)->{
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }

    public Member findById(Connection con,String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" +
                        memberId); }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // * connection은 여기서 닫지 않는다
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
//            JdbcUtils.closeConnection(con);
        }
    }
    public void update(String memberId, int money)  {
        String sql = "update member set money=? where member_id=?";
        //파라미터가 들어가는 변수를 잘 확인해서 순서대로 넣어야 한다.
        template.update(sql,money,memberId);
    }



    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;
        try {

            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // * connection은 여기서 닫지 않는다.
            JdbcUtils.closeStatement(pstmt);
//            JdbcUtils.closeConnection(con);
        }
    }
    public void delete(String memberId)  {
        String sql = "delete from member where member_id=?";
       template.update(sql,memberId);
    }

}

