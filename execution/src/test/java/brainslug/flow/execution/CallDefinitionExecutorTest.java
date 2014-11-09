package brainslug.flow.execution;

import brainslug.AbstractExecutionTest;
import brainslug.flow.builder.FlowBuilderSupport;
import brainslug.flow.context.Trigger;
import brainslug.flow.node.task.CallDefinition;
import brainslug.util.IdUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

public class CallDefinitionExecutorTest extends AbstractExecutionTest {
  @Test
  public void shouldEvaluateCallDefinition() {
    FlowBuilderSupport dsl = new FlowBuilderSupport();
    TestService service = dsl.service(TestService.class);

    CallDefinition callDefinition = dsl.method(service.getString());

    Object callResult = new CallDefinitionExecutor().execute(callDefinition, new DefaultExecutionContext(new Trigger(), context.getRegistry()));

    Assertions.assertThat(callResult).isEqualTo("a String");
  }

  @Test
  public void shouldEvaluateNestedCallDefinitionArgument() {
    FlowBuilderSupport dsl = new FlowBuilderSupport();
    TestService service = dsl.service(TestService.class);

    CallDefinition callDefinition = dsl.method(service.echo(service.getString()));

    when(testServiceMock.echo(anyString())).then(answerWithFirstArgument());

    Object callResult = new CallDefinitionExecutor().execute(callDefinition, new DefaultExecutionContext(new Trigger(), context.getRegistry()));

    Assertions.assertThat(callResult).isEqualTo("a String");
  }

  @Test
  public void shouldEvaluatePropertyArgument() {
    FlowBuilderSupport dsl = new FlowBuilderSupport();
    TestService service = dsl.service(TestService.class);

    String property = dsl.property(IdUtil.id("property"), String.class);

    CallDefinition callDefinition = dsl.method(service.echo(property));

    when(testServiceMock.echo(anyString())).then(answerWithFirstArgument());

    DefaultExecutionContext execution = new DefaultExecutionContext(new Trigger().property("property", "the property value"), context.getRegistry());

    Object callResult = new CallDefinitionExecutor().execute(callDefinition, execution);

    Assertions.assertThat(callResult).isEqualTo("the property value");
  }

  @Test
  public void shouldEvaluateConstantExpressionArgument() {
    FlowBuilderSupport dsl = new FlowBuilderSupport();
    TestService service = dsl.service(TestService.class);

    CallDefinition callDefinition = dsl.method(service.echo(dsl.val(dsl.constant("a constant"))));

    when(testServiceMock.echo(anyString())).then(answerWithFirstArgument());

    DefaultExecutionContext execution = new DefaultExecutionContext(new Trigger().property("property", "the property value"), context.getRegistry());

    Object callResult = new CallDefinitionExecutor().execute(callDefinition, execution);

    Assertions.assertThat(callResult).isEqualTo("a constant");
  }

}