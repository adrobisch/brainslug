package brainslug.flow.execution.property;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.instance.DefaultFlowInstance;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.execution.instance.FlowInstanceProperties;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static brainslug.util.IdUtil.id;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ExecutionPropertiesTest {
  @Test
  public void shouldCreatePropertiesFromList() {
    Date testDate = new Date();
    TestClass testClass = new TestClass();

    List<? extends AbstractProperty<?>> propertyList =
      asList(
        new LongProperty("longProp", 43L),
        new BooleanProperty("boolProp", true),
        new IntProperty("intProp", 42),
        new StringProperty("stringProp", "aString"),
        new DateProperty("dateProp", testDate),
        new DoubleProperty("doubleProp", 1.2),
        new FloatProperty("floatProp", 1.3f),
        new ObjectProperty("objectProp", testClass)
      );

    ExecutionProperties propertiesFromList = new ExecutionProperties()
      .from(propertyList);

    assertThat(propertiesFromList.get("boolProp", Boolean.class).getValue()).isEqualTo(true);
    assertThat(propertiesFromList.get("intProp", Integer.class).getValue()).isEqualTo(42);
    assertThat(propertiesFromList.get("stringProp", String.class).getValue()).isEqualTo("aString");
    assertThat(propertiesFromList.get("dateProp", Date.class).getValue()).isEqualTo(testDate);
    assertThat(propertiesFromList.get("doubleProp", Double.class).getValue()).isEqualTo(1.2);
    assertThat(propertiesFromList.get("floatProp", Float.class).getValue()).isEqualTo(1.3f);
    assertThat(propertiesFromList.get("longProp", Long.class).getValue()).isEqualTo(43l);
    assertThat(propertiesFromList.get("objectProp", TestClass.class).getValue()).isEqualTo(testClass);
  }

  @Test
  public void shouldReturnTypedPropertyValue() {
    PropertyStore propertyStore = mock(PropertyStore.class);

    Identifier instanceId = id("instance");

    DefaultFlowInstance instance = new DefaultFlowInstance(instanceId,
        id("definition"),
        propertyStore,
        mock(TokenStore.class));

    FlowInstanceProperties executionProperties = new ExecutionProperties().with("foo", "bar");

    given(propertyStore.getProperties(instanceId))
        .willReturn(executionProperties);

    String foo = instance.getProperties().value(id("foo"), String.class);
    assertThat(foo).isEqualTo("bar");
  }

  static class TestClass {}

}