package brainslug.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SpringBrainslugTestConfiguration {

  @Bean
  public TestService testService() {
    return mock(TestService.class);
  }

}