package hello.jdbc_cozil.repository;

import hello.jdbc_cozil.domain.Member;
import hello.jdbc_cozil.repository.ex.MyDBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 추가
 */

@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository{
    private DataSource dataSource;
    //SQLExceptionTranslator 인터페이스 선언
    private SQLExceptionTranslator translator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        //생성자 선언 => 어떤 DB를 쓰는지에 대한 정보를 알기 위해 dataSource 를 넣어준다
        this.translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }


    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            //스프링에서 제공하는 DataAccessException.translate 를 사용
            //자동으로 스프링에서 예외를 추상화해서 제공한다.
           throw translator.translate("save",sql,e);
        } finally {
            close(con, pstmt, null);
        }

    }
    public Member findById(String memberId)  {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
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
            throw new MyDBException(e);
        } finally {
            close(con, pstmt, rs);
        }
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
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            throw new MyDBException(e);
        } finally {
            close(con, pstmt, null);
        }
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
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new MyDBException(e);
        } finally {
            close(con, pstmt, null);
        }
    }
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //트랜젝션 동기화를 사용하기 위해서는 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con,dataSource);


    }


    private Connection getConnection() throws SQLException {
        //트랜젝션 동기화를 사용하기 위해서는 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}",con,con.getClass());
        return con;
    }
}

