package brainslug.juel;

import brainslug.flow.context.BrainslugExecutionContext;
import brainslug.flow.context.Registry;
import brainslug.flow.context.Trigger;
import brainslug.flow.context.TriggerContext;
import brainslug.flow.execution.expression.ExpressionEvaluator;
import brainslug.flow.instance.FlowInstance;
import org.junit.Test;

import java.util.ServiceLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class JuelExpressionEvaluatorTest {

    JuelExpressionEvaluator evaluator = new JuelExpressionEvaluator();

    @Test
    public void shouldBeRegisteredAsService() {
        ServiceLoader<ExpressionEvaluator> load = ServiceLoader.load(ExpressionEvaluator.class);
        assertThat(load.iterator().next()).isExactlyInstanceOf(JuelExpressionEvaluator.class);
    }

    @Test
    public void shouldEvaluateEqualsExpression() {
        JuelExpression fooEqualsFour = new JuelExpression("foo == 4");

        BrainslugExecutionContext context = testContext();
        context.setProperty("foo", 4);

        Boolean result = evaluator.evaluate(fooEqualsFour, context, Boolean.class);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldEvaluateContainsExpression() {
        JuelExpression fooEqualsFour = new JuelExpression("bar.contains('aTe')");

        BrainslugExecutionContext context = testContext();
        context.setProperty("bar", "aText");

        Boolean result = evaluator.evaluate(fooEqualsFour, context, Boolean.class);

        assertThat(result).isTrue();
    }

    private BrainslugExecutionContext testContext() {
        FlowInstance instance = mock(FlowInstance.class);
        TriggerContext trigger = new Trigger();
        Registry registry = mock(Registry.class);

        return new BrainslugExecutionContext(instance, trigger, registry);
    }
}