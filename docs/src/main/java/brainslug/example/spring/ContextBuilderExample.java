package brainslug.example.spring;

import brainslug.flow.execution.property.store.HashMapPropertyStore;
import brainslug.spring.SpringBrainslugContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// # tag::builder-example[]
@Configuration
@Import(brainslug.spring.SpringBrainslugConfiguration.class)
public class ContextBuilderExample {
  @Bean
  SpringBrainslugContextBuilder contextBuilder() {
    return new SpringBrainslugContextBuilder()
      .withPropertyStore(new HashMapPropertyStore());
      // ...
  }
}
//# end::builder-example[]
