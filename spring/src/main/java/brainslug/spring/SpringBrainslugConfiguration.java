package brainslug.spring;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.flow.execution.instance.InstanceStore;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.execution.token.TokenStore;
import brainslug.util.IdGenerator;
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

  @Autowired(required = false)
  InstanceStore instanceStore;

  @Autowired(required = false)
  TokenStore tokenStore;

  @Autowired(required = false)
  PropertyStore propertyStore;

  @Autowired(required = false)
  AsyncTriggerStore asyncTriggerStore;

  @Autowired(required = false)
  IdGenerator idGenerator;

  @Bean
  public BrainslugContext brainslugContext() {
    SpringBrainslugContext context = getBrainslugContextBuilder()
      .withIdGenerator(idGenerator)
      .withAsyncTriggerExecutor(springAsyncTriggerExecutor())
      .withAsyncTriggerStore(asyncTriggerStore)
      .withInstanceStore(instanceStore)
      .withTokenStore(tokenStore)
      .withPropertyStore(propertyStore)
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
