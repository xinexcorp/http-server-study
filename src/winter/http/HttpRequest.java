package winter.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public String method;
    public String uri;
    public Map<String, String> headers = new HashMap<String, String>();
    public String body;
}