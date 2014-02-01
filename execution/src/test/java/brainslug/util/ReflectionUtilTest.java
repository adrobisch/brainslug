package brainslug.util;

import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.fest.assertions.api.Assertions.assertThat;

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

  @Test(expected = NoSuchMethodError.class)
  public void shouldThrowWhenNoMethodWithAnnotationFound() {
    ReflectionUtil.getFirstMethodAnnotatedWith(ClassWithOutAnnotation.class, MyAnnotation.class);
  }

  @Test
  public void shouldFindMethodWithAnnotation() {
    Method method = ReflectionUtil.getFirstMethodAnnotatedWith(ClassWithAnnotation.class, MyAnnotation.class);
    assertThat(method.getName()).isEqualTo("methodWithAnnotation");
  }
}
