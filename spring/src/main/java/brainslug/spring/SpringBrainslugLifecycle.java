package brainslug.spring;

import brainslug.flow.context.BrainslugContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;

import java.util.concurrent.atomic.AtomicBoolean;

public class SpringBrainslugLifecycle implements SmartLifecycle {

  AtomicBoolean started = new AtomicBoolean(false);

  @Autowired
  BrainslugContext brainslugContext;

  @Autowired
  Environment environment;

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable callback) {
    stop();
    callback.run();
  }

  @Override
  public void start() {
    Boolean schedulersEnabled = environment.getProperty("brainslug.schedulers.enabled", Boolean.class, false);

    if(schedulersEnabled && !started.get()) {
      brainslugContext.start();
      started.set(true);
    }
  }

  @Override
  public void stop() {
    if (isRunning()) {
      brainslugContext.stop();
      started.set(false);
    }
  }

  @Override
  public boolean isRunning() {
    return started.get();
  }

  @Override
  public int getPhase() {
    return 0;
  }
}
