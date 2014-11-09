package brainslug;

import brainslug.flow.context.BrainslugContextBuilder;
import brainslug.flow.context.DefaultBrainslugContext;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractExecutionTest {
  protected DefaultBrainslugContext context = createContext();
  protected TestService testServiceMock;

  DefaultBrainslugContext createContext() {
    DefaultBrainslugContext context = new BrainslugContextBuilder().build();
    setup(context);
    return context;
  }

  private void setup(DefaultBrainslugContext context) {
    testServiceMock = mock(TestService.class);
    when(testServiceMock.getString()).thenReturn("a String");
    context.getRegistry().registerService(TestService.class, testServiceMock);
  }

  protected Answer<Object> answerWithFirstArgument() {
    return new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArguments()[0];
      }
    };
  }

  public static interface TestService {
    public String getString();
    public String echo(String echo);
  }
}
