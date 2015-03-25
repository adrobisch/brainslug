package brainslug.example.spring;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.node.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static brainslug.flow.execution.property.ExecutionProperties.newProperties;
import static java.lang.String.format;

// # tag::spring-example[]
@Configuration
@Import(brainslug.spring.SpringBrainslugConfiguration.class)
public class ConfigurationExample {

  @Component
  public static class SpringExampleTask implements Task {
    Environment environment;

    @Autowired
    public SpringExampleTask(Environment environment) {
      this.environment = environment;
    }

    @Override
    public void execute(ExecutionContext context) {
      printHello(context.property("name", String.class));

      context.service(SpringExampleTask.class).printHello("again");
    }

    public void printHello(String name) {
      System.out.println(
        format("Hello %s!", name)
      );
    }
  }

  @Bean
  FlowBuilder flowBuilder() {
    return new FlowBuilder() {
      @Override
      public void define() {
        flowId(id("spring-flow"));

        start(task(id("spring-task")).delegate(SpringExampleTask.class));
      }
    };
  }

  public static void main(String[] args) {
    AnnotationConfigApplicationContext applicationContext =
      new AnnotationConfigApplicationContext(ConfigurationExample.class);

    BrainslugContext brainslugContext = applicationContext.getBean(BrainslugContext.class);

    brainslugContext.startFlow(FlowBuilder.id("spring-flow"),
      newProperties().with("name", "World"));
  }

}
//# end::spring-example[]
