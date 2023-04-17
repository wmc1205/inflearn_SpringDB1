package hello;

public class ExceptionDescription {
    /*

    자바 예외
    Error : 시스템 오류, 복구가 필요한 사항이기 때문에 개발자가 해결할 수 있는 문제가 대부분 아니다. Unchecked Exception에 속함
    Exception :
    - Checked Exception
    - 컴파일러가 체크하는 체크 예외
    - RuntimeException과 그 하위 예외는 Unchecked Exception에 포함한다.
     */

    /*
    예외 기본 규칙
    - 예외를 처리하지 못하면, 해당 시점부터 예외를 계속 던지게 된다
    - 예외는 처리하거나, 처리하지 않아야 한다.
    - 예외 처리 될 시, 해당 예외의 자식까지 처리가 된다.
    1) try-catch
    2) throws

    -예외가 처리되지 못할 경우?
    자바 main() 쓰레드의 경우에는 로그를 출력하면서 계속해서 예외를 던진다.
    애플리케이션의 경우에는 오류 페이지를 보여주면서, WAS 에서 예외를 처리하게 된다.

     */
}
