package brainslug;

import brainslug.flow.context.BrainslugContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractExecutionTest {
  protected BrainslugContext context = createContext();
  protected TestService testService;

  BrainslugContext createContext() {
    BrainslugContext context = new BrainslugContext();
    testService = mock(TestService.class);
    when(testService.getString()).thenReturn("a String");
    context.getRegistry().registerService(TestService.class, testService);
    return context;
  }

  public static interface TestService {
    public String getString();
  }
}
