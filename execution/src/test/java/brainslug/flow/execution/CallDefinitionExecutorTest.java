package brainslug.flow.execution;

import brainslug.AbstractExecutionTest;
import brainslug.flow.builder.FlowBuilderSupport;
import brainslug.flow.context.BrainslugExecutionContext;
import brainslug.flow.context.Trigger;
import brainslug.flow.execution.instance.DefaultFlowInstance;
import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.node.task.CallDefinition;
import brainslug.util.IdUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static brainslug.util.IdUtil.id;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CallDefinitionExecutorTest extends AbstractExecutionTest {
  @Test
  public void shouldEvaluateCallDefinition() {
    FlowBuilderSupport dsl = new FlowBuilderSupport();
    TestService service = dsl.service(TestService.class);

    CallDefinition callDefinition = dsl.method(service.getString());

    Object callResult = new CallDefinitionExecutor().execute(callDefinition, new BrainslugExecutionContext(instanceMock(),new Trigger(), registryWithServiceMock()));

    Assertions.assertThat(callResult).isEqualTo("a String");
  }

  private FlowInstance instanceMock() {
    return new DefaultFlowInstance(id("instance"), propertyStore, tokenStore);
  }

  @Test
  public void shouldEvaluateNestedCallDefinitionArgument() {
    FlowBuilderSupport dsl = new FlowBuilderSupport();
    TestService service = dsl.service(TestService.class);

    CallDefinition callDefinition = dsl.method(service.echo(service.getString()));

    when(testServiceMock.echo(anyString())).then(answerWithFirstArgument());

    Object callResult = new CallDefinitionExecutor().execute(callDefinition, new BrainslugExecutionContext(instanceMock(),new Trigger(), registryWithServiceMock()));

    Assertions.assertThat(callResult).isEqualTo("a String");
  }

  @Test
  public void shouldEvaluatePropertyArguments() {
    FlowBuilderSupport dsl = new FlowBuilderSupport();
    TestService service = dsl.service(TestService.class);

    String property = dsl.value(IdUtil.id("property"), String.class);
    String property2 = dsl.value(IdUtil.id("property2"), String.class);

    CallDefinition callDefinition = dsl.method(service.multiEcho(property, property2));

    when(testServiceMock.multiEcho(anyString(), anyString())).then(answerWithFirstArgumentAndSecondArgument());

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(),new Trigger()
      .property("property", "prop1").property("property2", "prop2"), registryWithServiceMock());

    Object callResult = new CallDefinitionExecutor().execute(callDefinition, execution);

    Assertions.assertThat(callResult).isEqualTo("prop1prop2");
  }

  @Test
  public void shouldEvaluateConstantExpressionArgument() {
    FlowBuilderSupport dsl = new FlowBuilderSupport();
    TestService service = dsl.service(TestService.class);

    CallDefinition callDefinition = dsl.method(service.echo(dsl.value(dsl.constant("a constant"))));

    when(testServiceMock.echo(anyString())).then(answerWithFirstArgument());

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(),new Trigger().property("property", "the property value"), registryWithServiceMock());

    Object callResult = new CallDefinitionExecutor().execute(callDefinition, execution);

    Assertions.assertThat(callResult).isEqualTo("a constant");
  }

}