package hello.jdbc_cozil.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;


/**
 * <언체크 예외 처리 활용>
 * 리포지토리에서 체크 예외인 "SQLException"이 발생할 경우 RuntimeException을 상속한 RuntimeSQLException"으로 전환해서 예외를 던진다.
 * - 기존 예외를 함께 확인 하기 위해 생성자에서 Throwable을 매개변수로 받는다.
 *
 * 런타임 예외 - 복구 불가능한 예외
 * 개발자는 런타임 예외를 복구할 필요가 없다.(가능성 거의 없음)
 * 런타임 예외는 해당 객체가 처리할 수 없는 예외는 무시, 즉 강제로 의존하지 않아도 된다.
 * 런타임 예외를 던지면 컨트롤러와 서비스에서 해당 예외에 대한 의존 관계가 발생하지 않는다.(중간 단계에서 코드 변경 x)
 */
@Slf4j
public class UncheckedAppTest {

    @Test
    void unchecked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(()->controller.request())
                .isInstanceOf(Exception.class);
    }

    @Test
    void printEx(){
        Controller controller = new Controller();
        try{
            controller.request();
        }catch (Exception e){
            log.info("ex",e);
        }
    }


    static class Controller {
        Service service = new Service();

        public void request()  {
            service.logic();
        }
    }
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }

    }
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }
    static class Repository {
        public void call()  {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }
        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }
    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message){
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException{
        public RuntimeSQLException(Throwable cause) {
                super(cause);
        }
    }
}
