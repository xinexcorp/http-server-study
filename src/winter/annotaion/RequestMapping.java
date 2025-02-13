package winter.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  // 실행 시간에도 유지됨
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})  // 메서드에 적용 가능
public @interface RequestMapping {
    String method();
}
