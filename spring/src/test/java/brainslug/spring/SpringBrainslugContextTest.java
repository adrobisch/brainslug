package brainslug.spring;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.node.task.SimpleTask;
import brainslug.util.IdUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static brainslug.util.IdUtil.id;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringBrainslugConfiguration.class, SpringBrainslugTestConfiguration.class})
public class SpringBrainslugContextTest {

  @Autowired
  BrainslugContext brainslugContext;

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  TestService testService;

  @Test
  public void shouldAddFlowBuilderBeansToContext() {
    Collection<FlowDefinition> definitions = brainslugContext.getDefinitions();

    assertThat(definitions)
      .hasSize(1);

    assertThat(definitions.iterator().next().getId())
      .isEqualTo(IdUtil.id("springTestFlow"));
  }

  @Test
  public void shouldFindSpringBeansInRegistry() {
    BrainslugContext context = new SpringBrainslugContextBuilder()
      .withApplicationContext(applicationContext)
      .build();

    context.addFlowDefinition(new FlowBuilder() {

      @Override
      public void define() {
        flowId(id("inlineFlow"));

        start(event(id("start")))
          .execute(task(id("callSpringBean"), new SimpleTask() {
            @Override
            public void execute(ExecutionContext context) {
              context.service(TestService.class).foo();
            }
          }));
      }
    }.getDefinition());

    context.startFlow(id("inlineFlow"), id("start"));

    verify(testService).foo();
  }

  @Test
  public void shouldIntegrateWithLifecycle() {
    // see auto start flow in SpringBrainslugTestConfiguration
    verify(testService).bar();
  }

}