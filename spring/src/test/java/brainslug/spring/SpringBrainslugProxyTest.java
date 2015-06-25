package brainslug.spring;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.expression.Property;
import brainslug.flow.node.task.CallDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static brainslug.flow.execution.property.ExecutionProperties.newProperties;
import static brainslug.util.IdUtil.id;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringBrainslugConfiguration.class, SpringBrainslugTestConfiguration.class})
public class SpringBrainslugProxyTest {

  @Autowired
  BrainslugContext brainslugContext;

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  TestServiceClass testServiceClass;

  @Test
  public void shouldInvokeServiceMethodForClass() {
    BrainslugContext context = new SpringBrainslugContextBuilder()
      .withApplicationContext(applicationContext)
      .build();

    final Property param = FlowBuilder.property(id("param"));

    context.addFlowDefinition(new FlowBuilder() {

      @Override
      public void define() {
        flowId(id("inlineFlow"));

        CallDefinition beanMethod = method(service(TestServiceClass.class).classFoo(value(param, String.class)));

        start(event(id("start")))
          .execute(task(id("callSpringBean")).call(beanMethod));

      }
    }.getDefinition());

    context.startFlow(id("inlineFlow"), id("start"), newProperties().with(param, "foo"));

    verify(testServiceClass).classFoo("foo");
  }

}