package winter.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    String method;
    String uri;
    Map<String, String> headers = new HashMap<String, String>();
    String body;
}