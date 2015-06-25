package brainslug.spring;

import brainslug.flow.context.DefaultBrainslugContext;
import brainslug.flow.context.Registry;
import brainslug.flow.execution.instance.InstanceStore;
import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.definition.DefinitionStore;
import brainslug.flow.execution.FlowExecutor;
import brainslug.flow.execution.async.*;
import brainslug.flow.execution.expression.ExpressionEvaluator;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.listener.ListenerManager;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringBrainslugContext extends DefaultBrainslugContext {
  ApplicationContext applicationContext;

  public SpringBrainslugContext(ApplicationContext applicationContext,
                                AsyncTriggerScheduler asyncTriggerScheduler,
                                AsyncTriggerStore asyncTriggerStore,
                                AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions,
                                AsyncFlowStartScheduler asyncFlowStartScheduler,
                                SchedulerOptions asyncFlowStartSchedulerOptions,
                                DefinitionStore definitionStore,
                                ListenerManager listenerManager,
                                CallDefinitionExecutor callDefinitionExecutor,
                                ExpressionEvaluator expressionEvaluator,
                                Registry registry,
                                FlowExecutor flowExecutor,
                                TokenStore tokenStore,
                                InstanceStore instanceStore) {
    super(asyncTriggerScheduler,
            asyncTriggerStore,
            asyncTriggerSchedulerOptions,
            asyncFlowStartScheduler,
            asyncFlowStartSchedulerOptions,
            definitionStore,
            listenerManager,
            callDefinitionExecutor,
            expressionEvaluator,
            registry,
            flowExecutor,
            tokenStore,
            instanceStore);

    this.applicationContext = applicationContext;
  }
}
