package brainslug.flow.listener;

import brainslug.flow.context.Trigger;
import brainslug.flow.context.TriggerContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TriggerTest {

  @Test
  public void shouldGetPropertyByKey() {
    //given
    TriggerContext trigger = new Trigger();
    //when
    trigger.setProperty("foo", "bar");
    //then
    assertThat(trigger.getProperty("foo", String.class)).isEqualTo("bar");
  }

}
