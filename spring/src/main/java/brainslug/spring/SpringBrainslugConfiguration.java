package brainslug.spring;

import brainslug.flow.FlowBuilder;
import brainslug.flow.context.BrainslugContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
public class SpringBrainslugConfiguration {
  @Autowired
  ApplicationContext applicationContext;

  @Autowired(required = false)
  SpringBrainslugContextBuilder brainslugContextBuilder;

  @Autowired(required = false)
  Collection<FlowBuilder> flowBuilders;

  @Bean
  public BrainslugContext brainslugContext() {
    SpringBrainslugContext context = getBrainslugContextBuilder()
      .withAsyncTriggerExecutor(springAsyncTriggerExecutor())
      .withApplicationContext(applicationContext)
      .build();

    addFlowDefinitions(context);

    return context;
  }

  @Bean
  public SpringAsyncTriggerExecutor springAsyncTriggerExecutor() {
    return new SpringAsyncTriggerExecutor();
  }

  private SpringBrainslugContextBuilder getBrainslugContextBuilder() {
    if (brainslugContextBuilder != null) {
      return brainslugContextBuilder;
    }
    return new SpringBrainslugContextBuilder();
  }

  private void addFlowDefinitions(SpringBrainslugContext context) {
    if (flowBuilders != null) {
      for (FlowBuilder flowBuilder: flowBuilders) {
        context.addFlowDefinition(flowBuilder.getDefinition());
      }
    }
  }

  @Bean
  public SpringBrainslugLifecycle brainslugLifecycle() {
    return new SpringBrainslugLifecycle();
  }

}
