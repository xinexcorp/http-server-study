package winter.http;

import java.util.Map;

public class HttpRequest {
    String method;
    String uri;
    Map<String, String> headers;
    String body;
}