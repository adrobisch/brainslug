package brainslug.flow.execution.node.task;

import brainslug.TestService;
import brainslug.flow.context.BrainslugExecutionContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.context.HashMapRegistry;
import brainslug.flow.context.Trigger;
import brainslug.flow.execution.property.IntProperty;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.node.task.TaskScript;
import org.junit.Test;

import javax.script.ScriptEngineManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ScriptExecutorTest {

    HashMapRegistry registry = new HashMapRegistry();
    TestService testService = mock(TestService.class);
    FlowInstance instance = mock(FlowInstance.class);

    @Test
    public void shouldExecuteScript() {
        // given:
        TaskScript taskJavaScript = new TaskScript("JavaScript", testScript());
        ExecutionContext executionContext = testContext();
        // when:
        new ScriptExecutor(new ScriptEngineManager()).execute(taskJavaScript, executionContext);
        // then:
        verify(testService).echo("js");

        assertThat(executionContext.getProperties().get("bar"))
                .isEqualTo(new IntProperty("bar", 2));
    }

    private String testScript() {
        return "brainslug.service('test').echo('js');" +
                "brainslug.setProperty('bar', 2);";
    }

    ExecutionContext testContext() {
        registry.registerService("test", testService);
        Trigger trigger = new Trigger();
        return new BrainslugExecutionContext(instance, trigger, registry);
    }

}