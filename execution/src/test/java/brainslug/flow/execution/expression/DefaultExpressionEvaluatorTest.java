package brainslug.flow.execution.expression;

import brainslug.flow.context.*;
import brainslug.flow.context.BrainslugExecutionContext;
import brainslug.flow.execution.property.ExecutionProperties;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.expression.PredicateExpression;
import brainslug.flow.instance.FlowInstanceProperties;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DefaultExpressionEvaluatorTest {

  @Test
  public void shouldEvaluatePropertyPredicate() {
    // given:

    BrainslugExecutionContext executionContext = testContext();
    DefaultExpressionEvaluator evaluator = new DefaultExpressionEvaluator();

    PropertyPredicate predicateSpy = spy(new PropertyPredicate() {
      @Override
      public boolean isFulfilled(FlowInstanceProperties executionProperties) {
        Assertions.assertThat(executionProperties).isNotNull();
        return false;
      }
    });

    PredicateExpression predicateDefintion = new PredicateExpression<PropertyPredicate>(predicateSpy);
    // when:

    boolean result = evaluator.evaluate(predicateDefintion, executionContext, Boolean.class);

    // then:
    verify(predicateSpy).isFulfilled(any(ExecutionProperties.class));
    Assertions.assertThat(result).isFalse();
  }


  @Test
  public void shouldEvaluateContextPredicate() {
    // given:

    DefaultExpressionEvaluator evaluator = new DefaultExpressionEvaluator();
    BrainslugExecutionContext executionContext = testContext();

    ContextPredicate predicateSpy = spy(new ContextPredicate() {
      @Override
      public boolean isFulfilled(ExecutionContext executionContext) {
        Assertions.assertThat(executionContext).isNotNull();
        return true;
      }
    });

    PredicateExpression predicateDefintion = new PredicateExpression<ContextPredicate>(predicateSpy);
    // when:

    boolean result = evaluator.evaluate(predicateDefintion, executionContext, Boolean.class);

    // then:
    verify(predicateSpy).isFulfilled(any(ExecutionContext.class));
    Assertions.assertThat(result).isTrue();
  }

  private BrainslugExecutionContext testContext() {
    PropertyStore propertyStoreMock = mock(PropertyStore.class);

    return new BrainslugExecutionContext(new Trigger().property("test", "foo"),
        new BrainslugContextBuilder()
          .withPropertyStore(propertyStoreMock).build().getRegistry());
  }

}