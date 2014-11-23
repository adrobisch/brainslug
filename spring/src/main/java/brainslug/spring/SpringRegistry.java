package brainslug.spring;

import brainslug.flow.context.Registry;
import org.springframework.context.ApplicationContext;

public class SpringRegistry implements Registry {
  ApplicationContext applicationContext;

  public SpringRegistry(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public <T> T getService(Class<T> serviceClass) {
    return applicationContext.getAutowireCapableBeanFactory().getBean(serviceClass);
  }

  @Override
  public <T> void registerService(Class<T> serviceClass, T serviceInstance) {
    throw new UnsupportedOperationException("you can not register a service in a spring context, create a bean instead.");
  }
}
