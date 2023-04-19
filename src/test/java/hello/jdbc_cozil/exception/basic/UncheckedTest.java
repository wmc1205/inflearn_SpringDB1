package hello.jdbc_cozil.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }
    @Test
    void unchecked_throw(){
        Service service = new Service();

        Assertions.assertThatThrownBy(()->service.callThrow())
                .isInstanceOf(MyUncheckedException.class);
    }
    /**
     * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message) {
            super(message);
        }
    }
    static class Service{
        Repository repository = new Repository();

        /**
         * 필요한 경우, 예외를 잡아서 처리하면 된다.
         */
        public void callCatch(){
            try {
                repository.call();
            }catch (MyUncheckedException e){
                log.info("예외처리, MyUncheckedException={}",e.getMessage(),e);
            }

        }
        public void callThrow(){

               repository.call();


        }
    }


    /**
     * UncheckedException 은 throws 를 생략할 수 있다.
     * 즉, 예외를 잡거나 던지지 않아도 컴파일러가 자동으로 예외를 밖으로 던진다.
     */
    static class Repository {
        public void call(){
            throw new MyUncheckedException("ex");
        }
    }
}
