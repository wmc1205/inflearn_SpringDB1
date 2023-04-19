package hello.jdbc_cozil.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <Checked Exception>
 */
@Slf4j
public class CheckedTest {
    @Test
    void checked_Catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_Throws(){
        Service service = new Service();
        /**
         * test 단계에서 예외를 던질 때는 assertThatThrownBy(()->throw메서드).isInstaceOf(CheckedException)
         */
        Assertions.assertThatThrownBy(()->service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }
    /**
    * Exception을 상속받으면 기본적으로 Checked 예외가 된다.
     */
    static class MyCheckedException extends Exception{
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         * 1) try-catch (예외를 잡는다)
         */
        public void callCatch(){
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, myCheckedException={}",e.getMessage(),e);
            }
        }
        /**
        * 예외를 잡지 않고 던지려면 throws 메서드를 반드시 선언해야 한다.
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }
    static class Repository{
        /**
         * 예외를 던져서 처리하는 코드
        * 2) 예외를 throw (예외를 던진다)
         */
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
