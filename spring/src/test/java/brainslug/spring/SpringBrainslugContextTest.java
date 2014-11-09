package brainslug.spring;

import brainslug.flow.FlowBuilder;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.SimpleTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static brainslug.util.IdUtil.id;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringBrainslugConfiguration.class, SpringBrainslugTestConfiguration.class})
public class SpringBrainslugContextTest {

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  TestService testService;

  @Test
  public void shouldFindSpringBeansInRegistry() {
    BrainslugContext context = new SpringBrainslugContextBuilder()
      .withApplicationContext(applicationContext)
      .build();

    context.addFlowDefinition(new FlowBuilder() {

      @Override
      public void define() {
        flowId(id("springTestFlow"));

        start(event(id("start")))
          .execute(task(id("callSpringBean"), new SimpleTask() {
            @Override
            public void execute(ExecutionContext context) {
              context.service(TestService.class).foo();
            }
          }));
      }
    }.getDefinition());

    context.startFlow(id("springTestFlow"), id("start"));

    verify(testService).foo();
  }

}