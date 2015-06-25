package brainslug.spring;

import org.springframework.transaction.annotation.Transactional;

public class TestServiceClass {
  public String classFoo(String param) {
    return param;
  }
  public String classBar() {
    return "bar";
  }
}
