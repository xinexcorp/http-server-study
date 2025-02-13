package winter.controller;

import winter.annotaion.GetMapping;
import winter.annotaion.PostMapping;
import winter.annotaion.RequestMapping;
import winter.http.HttpRequest;
import winter.http.HttpResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class Controller {

    public void handle(HttpRequest request, HttpResponse response) {
        System.out.println("Request: " + request.uri + " " + request.method);

        Class<?> clazz = this.getClass(); // 현재 컨트롤러 클래스

        for (Method method : clazz.getDeclaredMethods()) {
            RequestMapping requestMapping = getRequestMapping(method);
            if (requestMapping == null) continue;

            // GetMapping 또는 PostMapping에서 `value()`를 가져오기
            String uri = getMappingValue(method);
            if (uri == null || !uri.equals(request.uri)) continue;

            if (!requestMapping.method().equalsIgnoreCase(request.method)) continue;

            try {
                method.invoke(this, request, response);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.body = "Unknown URI";
    }

    // 📌 메서드에서 `@RequestMapping`을 찾는 함수
    private RequestMapping getRequestMapping(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(RequestMapping.class)) {
                return annotation.annotationType().getAnnotation(RequestMapping.class);
            }
        }
        return null;
    }

    // 📌 `value()` 값을 가져오는 함수 (GetMapping 또는 PostMapping에서)
    private String getMappingValue(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            try {
                return (String) annotation.annotationType().getMethod("value").invoke(annotation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    @GetMapping(value = "/hi")
    private static void hi(HttpRequest request, HttpResponse response) {
        response.body = "hi, aespa winter!";
    }

    @GetMapping(value = "/hello")
    private static void hello(HttpRequest request, HttpResponse response) {
        response.body = "Hello, aespa winter!";
    }

    @GetMapping(value = "/bye")
    private static void bye(HttpRequest request, HttpResponse response) {
        // 아주 예쁜 흐트믈 응답을 내려주세요! TODO

        response.body = "Bye, aespa winter!";
    }

    @PostMapping(value = "/bye")
    private static void byePost(HttpRequest request, HttpResponse response) {
        // 아주 예쁜 흐트믈 응답을 내려주세요! TODO

        response.body = "POST!!! Bye, aespa winter!";
    }
}
