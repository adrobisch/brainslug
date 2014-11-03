package brainslug.flow.execution.expression;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.DefaultExecutionContext;
import brainslug.flow.execution.ExecutionProperties;
import brainslug.flow.execution.PropertyStore;
import brainslug.flow.execution.TriggerContext;
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
    PropertyStore propertyStoreMock = mock(PropertyStore.class);

    final DefaultExecutionContext executionContext = new DefaultExecutionContext(new TriggerContext().property("test", "foo"),
      new BrainslugContext()
        .withPropertyStore(propertyStoreMock));

    PropertyPredicate predicateSpy = spy(new PropertyPredicate() {
      @Override
      public boolean isFulfilled(ExecutionProperties executionProperties) {
        Assertions.assertThat(executionProperties).isNotNull();
        return executionProperties.get("test", String.class).equals("foo");
      }
    });

    PredicateDefinition predicateDefinition = new PredicateDefinition<PropertyPredicate>(predicateSpy);
    // when:

    boolean result = evaluator.evaluate(predicateDefinition, executionContext);

    // then:
    verify(predicateSpy).isFulfilled(any(ExecutionProperties.class));
    Assertions.assertThat(result);
  }

}