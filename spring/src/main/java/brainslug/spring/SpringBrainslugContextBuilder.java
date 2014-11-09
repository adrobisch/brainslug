package brainslug.spring;

import brainslug.flow.context.AbstractBrainslugContextBuilder;
import org.springframework.context.ApplicationContext;

public class SpringBrainslugContextBuilder extends AbstractBrainslugContextBuilder<SpringBrainslugContextBuilder, SpringBrainslugContext> {

  ApplicationContext applicationContext;

  public SpringBrainslugContextBuilder withApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    this.registry = new SpringRegistry(applicationContext);
    return this;
  }

  @Override
  protected SpringBrainslugContext internalBuild() {
    return new SpringBrainslugContext(applicationContext,
      asyncTriggerScheduler,
      asyncTriggerStore,
      asyncTriggerSchedulerOptions,
      asyncFlowStartScheduler,
      asyncFlowStartSchedulerOptions,
      definitionStore,
      listenerManager,
      callDefinitionExecutor,
      predicateEvaluator,
      registry,
      flowExecutor,
      tokenStore);
  }
}
