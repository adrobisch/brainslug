package brainslug.flow.context;

public class BrainslugContextBuilder extends AbstractBrainslugContextBuilder<BrainslugContextBuilder, DefaultBrainslugContext> {
  @Override
  protected DefaultBrainslugContext internalBuild() {
    return new DefaultBrainslugContext(asyncTriggerScheduler,
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
