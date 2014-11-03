package brainslug.flow.execution.expression;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.Registry;
import brainslug.flow.execution.*;
import brainslug.flow.expression.PredicateDefinition;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class DefaultPredicateEvaluatorTest {

  @Test
  public void shouldEvaluatePropertyPredicate() {
    // given:

    DefaultPredicateEvaluator evaluator = new DefaultPredicateEvaluator();
    DefaultExecutionContext executionContext = testContext();

    PropertyPredicate predicateSpy = spy(new PropertyPredicate() {
      @Override
      public boolean isFulfilled(ExecutionProperties executionProperties) {
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
    DefaultExecutionContext executionContext = testContext();

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

  @Test
  public void shouldEvaluateServicePredicate() {
    // given:

    DefaultPredicateEvaluator evaluator = new DefaultPredicateEvaluator();
    DefaultExecutionContext executionContext = testContext();

    ServicePredicate predicateSpy = spy(new ServicePredicate() {
      @Override
      public boolean isFulfilled(Registry executionContext) {
        Assertions.assertThat(executionContext).isNotNull();
        return true;
      }
    });

    PredicateDefinition predicateDefintion = new PredicateDefinition<ServicePredicate>(predicateSpy);
    // when:

    boolean result = evaluator.evaluate(predicateDefintion, executionContext);

    // then:
    verify(predicateSpy).isFulfilled(any(Registry.class));
    Assertions.assertThat(result).isTrue();
  }

  private DefaultExecutionContext testContext() {
    PropertyStore propertyStoreMock = mock(PropertyStore.class);

    return new DefaultExecutionContext(new TriggerContext().property("test", "foo"),
        new BrainslugContext()
          .withPropertyStore(propertyStoreMock));
  }

}