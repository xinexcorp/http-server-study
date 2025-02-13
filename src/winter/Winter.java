package winter;

import winter.controller.Controller;
import winter.http.Http;
import winter.http.HttpRequest;
import winter.http.HttpResponse;

import java.io.EOFException;
import java.io.IOException;

/**
 * 궁극의 HTTP 서버 그 모냐 구현체 최상단 클래스입니다.
 * 이거 인스턴스 만들고 무한루프에서 <code>loop()</code> 호출 하십쇼.
 */
public class Winter {
    final Http http; // 매우 stateful

    public Winter() {
        http = new Http();
        http.listen(8080);
    }

    /**
     * HTTP 서버의 메인 루프입니다.
     * 최대한 빠른 속도로 적은 간격으로 공백 없이 호출해 제@끼십쇼;;
     * <p>
     * 아, 작성자가 임베디드 하던 놈이라서 아두이노 스타일입니다. ㅈㅅ;;
     */
    public void loop() {
        try {
            // 요청 땡겨와용
            HttpRequest request = http.waitForRequest();
            HttpResponse response = new HttpResponse();

            // TODO 지금 여기서는 요청이 어떻게 생겼든 그냥 지정된 동일한 컨트롤러의 한 메소드만을 호출하지만, 이제 annotation을 기반으로 요청을 잘 routing 해주세요 준영씨.
            new Controller().handle(request, response);

            http.sendResponse(response);
            // 응답을 던져줘요
        } catch (EOFException e) {
            System.out.println("클라이언트가 간만 보다가 나감ㅋㅎ 얼탱이 ㅋㅎ");
        } catch (IOException e) {
            System.out.println("요청과 응답을 처리하는 도중 어딘가에서 문제가 생겼습니다:");
            e.printStackTrace();
        }
    }
}
