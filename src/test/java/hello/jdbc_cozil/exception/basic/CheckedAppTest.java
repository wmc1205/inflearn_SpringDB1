package hello.jdbc_cozil.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;


/**
 * <체크 예외 처리 활용>
 * 기본적으로 "언체크 예외"를 기본적으로 처리해야한다.
 * 체크 예외를 처리하려고 한다면?
 * - DB connection 문제, 네트워크 오류 문제 등 애플리케이션 단에서 처리할 수 없는 예외들이 발생했을 경우,
 * 체크 예외를 처리하는 방식으로는 service, controller에서 예외를 처리할 수 없다. 반복적으로 체크를 밖으로 던지는 행위가 발생하게 된다.
 * - JDBC -> JPA로 기술을 변경하게 될 경우, SQLException으로 처리했던 모든 것을 JPAException으로 모두 변경하게 된다
 * -> OCP 를 위반하는 행위 (구현에 의존하게 됨)
 *
 * Q.Exception을 던져버리면?
 * - Exception은 모든 예외의 최상위. Exception을 던지면 체크 예외의 역할이 무효화된다.
 */
public class CheckedAppTest {

    @Test
    void checked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(()->controller.request())
                .isInstanceOf(Exception.class);
    }



    static class Controller {
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }

    }
    static class NetworkClient {
        public void call() throws ConnectException{
            throw new ConnectException("failed Connection");
        }
    }
    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }
}
