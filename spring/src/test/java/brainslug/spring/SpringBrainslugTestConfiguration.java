package brainslug.spring;

import brainslug.flow.FlowBuilder;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.SimpleTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

@Configuration
@PropertySource("classpath:brainslug.properties")
public class SpringBrainslugTestConfiguration {

  @Bean
  public TestService testService() {
    return mock(TestService.class);
  }

  @Bean
  public FlowBuilder testFlow() {
    return new FlowBuilder() {

      @Override
      public void define() {
        flowId(id("springTestFlow"));

        start(event(id("start")), every(1, TimeUnit.SECONDS))
          .execute(task(id("callSpringBean"), new SimpleTask() {
            @Override
            public void execute(ExecutionContext context) {
              context.service(TestService.class).bar();
            }
          }));
      }
    };
  }

}