package brainslug.spring;

import brainslug.flow.context.BrainslugContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBrainslugConfiguration {
  @Autowired
  ApplicationContext applicationContext;

  @Bean
  public BrainslugContext brainslugContext() {
    return new SpringBrainslugContextBuilder().withApplicationContext(applicationContext).build();
  }

}
