package brainslug.util;

import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionUtilTest {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface MyAnnotation {
  }

  public class ClassWithAnnotation {
    @MyAnnotation
    public void methodWithAnnotation() {
    }
  }

  public class ClassWithOutAnnotation {
    public void methodWithoutAnnotation() {
    }
  }

  @Test
  public void shouldReturnNotPresentIfNoMethodFound() {
    assertThat(ReflectionUtil.getFirstMethodAnnotatedWith(ClassWithOutAnnotation.class, MyAnnotation.class).isPresent()).isFalse();
  }

  @Test
  public void shouldFindMethodWithAnnotation() {
    Option<Method> method = ReflectionUtil.getFirstMethodAnnotatedWith(ClassWithAnnotation.class, MyAnnotation.class);
    assertThat(method.isPresent()).isTrue();
    assertThat(method.get().getName()).isEqualTo("methodWithAnnotation");
  }
}
