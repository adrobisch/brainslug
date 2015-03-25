package brainslug.flow.execution.expression;

import brainslug.flow.context.*;
import brainslug.flow.context.BrainslugExecutionContext;
import brainslug.flow.execution.property.ExecutionProperties;
import brainslug.flow.execution.property.PropertyStore;
import brainslug.flow.expression.PredicateDefinition;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DefaultPredicateEvaluatorTest {

  @Test
  public void shouldEvaluatePropertyPredicate() {
    // given:

    BrainslugExecutionContext executionContext = testContext();
    DefaultPredicateEvaluator evaluator = new DefaultPredicateEvaluator();

    PropertyPredicate predicateSpy = spy(new PropertyPredicate() {
      @Override
      public boolean isFulfilled(FlowProperties executionProperties) {
        Assertions.assertThat(executionProperties).isNotNull();
        return false;
      }
    });

    PredicateDefinition predicateDefintion = new PredicateDefinition<PropertyPredicate>(predicateSpy);
    // when:

    boolean result = evaluator.evaluate(predicateDefintion, executionContext);

    // then:
    verify(predicateSpy).isFulfilled(any(ExecutionProperties.class));
    Assertions.assertThat(result).isFalse();
  }


  @Test
  public void shouldEvaluateContextPredicate() {
    // given:

    DefaultPredicateEvaluator evaluator = new DefaultPredicateEvaluator();
    BrainslugExecutionContext executionContext = testContext();

    ContextPredicate predicateSpy = spy(new ContextPredicate() {
      @Override
      public boolean isFulfilled(ExecutionContext executionContext) {
        Assertions.assertThat(executionContext).isNotNull();
        return true;
      }
    });

    PredicateDefinition predicateDefintion = new PredicateDefinition<ContextPredicate>(predicateSpy);
    // when:

    boolean result = evaluator.evaluate(predicateDefintion, executionContext);

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