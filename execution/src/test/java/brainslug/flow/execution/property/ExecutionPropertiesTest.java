package brainslug.flow.execution.property;

import org.junit.Test;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ExecutionPropertiesTest {
  @Test
  public void shouldCreatePropertiesFromList() {
    Date testDate = new Date();
    TestClass testClass = new TestClass();

    List<? extends AbstractProperty<?>> propertyList =
      asList(
        new LongProperty("longProp", 43l),
        new BooleanProperty("boolProp", true),
        new IntProperty("intProp", 42),
        new StringProperty("stringProp", "aString"),
        new DateProperty("dateProp", testDate),
        new DoubleProperty("doubleProp", 1.2),
        new FloatProperty("floatProp", 1.3f),
        new ObjectProperty("objectProp", testClass)
      );

    ExecutionProperties propertiesFromList = new ExecutionProperties()
      .fromList(propertyList);

    assertThat(propertiesFromList.getProperty("boolProp", Boolean.class).getValue()).isEqualTo(true);
    assertThat(propertiesFromList.getProperty("intProp", Integer.class).getValue()).isEqualTo(42);
    assertThat(propertiesFromList.getProperty("stringProp", String.class).getValue()).isEqualTo("aString");
    assertThat(propertiesFromList.getProperty("dateProp", Date.class).getValue()).isEqualTo(testDate);
    assertThat(propertiesFromList.getProperty("doubleProp", Double.class).getValue()).isEqualTo(1.2);
    assertThat(propertiesFromList.getProperty("floatProp", Float.class).getValue()).isEqualTo(1.3f);
    assertThat(propertiesFromList.getProperty("longProp", Long.class).getValue()).isEqualTo(43l);
    assertThat(propertiesFromList.getProperty("objectProp", TestClass.class).getValue()).isEqualTo(testClass);
  }

  static class TestClass {}

}