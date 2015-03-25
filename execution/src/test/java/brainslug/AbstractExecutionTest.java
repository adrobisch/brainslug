package brainslug;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.BrainslugContextBuilder;
import brainslug.flow.context.Registry;
import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.definition.DefinitionStore;
import brainslug.flow.execution.property.store.HashMapPropertyStore;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.execution.async.AsyncTriggerScheduler;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.flow.execution.expression.DefaultPredicateEvaluator;
import brainslug.flow.execution.expression.PredicateEvaluator;
import brainslug.flow.execution.token.HashMapTokenStore;
import brainslug.flow.execution.token.TokenFlowExecutor;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.listener.ListenerManager;
import brainslug.util.UuidGenerator;
import org.junit.Before;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class AbstractExecutionTest {
  protected BrainslugContext context = createContext();

  protected TestService testServiceMock =  mock(TestService.class);
  protected DefinitionStore definitionStore = mock(DefinitionStore.class);
  protected PredicateEvaluator predicateEvaluator = new DefaultPredicateEvaluator();
  protected AsyncTriggerStore asyncTriggerStore = mock(AsyncTriggerStore.class);
  protected AsyncTriggerScheduler asyncTriggerScheduler = mock(AsyncTriggerScheduler.class);
  protected CallDefinitionExecutor callExecutor = mock(CallDefinitionExecutor.class);
  protected TokenStore tokenStore = new HashMapTokenStore(new UuidGenerator());
  protected PropertyStore propertyStore = new HashMapPropertyStore();
  protected ListenerManager listenerManager = mock(ListenerManager.class);

  BrainslugContext createContext() {
    return new BrainslugContextBuilder()
      .build();
  }

  protected TokenFlowExecutor tokenFlowExecutorWithMocks() {
    return spy(new TokenFlowExecutor(tokenStore,
      definitionStore,
      propertyStore,
      listenerManager,
      registryWithServiceMock(),
      predicateEvaluator,
      asyncTriggerStore,
      asyncTriggerScheduler,
      callExecutor));
  }

  protected Registry registryWithServiceMock() {
    Registry registry = mock(Registry.class);
    when(registry.getService(TestService.class)).thenReturn(testServiceMock);
    return registry;
  }

  @Before
  public void setup() {
    when(testServiceMock.getString()).thenReturn("a String");
  }

  protected Answer<Object> answerWithFirstArgument() {
    return new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArguments()[0];
      }
    };
  }

  public static
  // #tag::test-service[]
  interface TestService {
    public String getString();
    public String echo(String echo);
  }
  // #end::test-service[]
}
