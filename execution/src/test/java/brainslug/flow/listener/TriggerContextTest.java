package brainslug.flow.listener;

import brainslug.flow.execution.TriggerContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TriggerContextTest {

  @Test
  public void shouldGetSinglePropertyByClass() {
    //given
    TriggerContext<?> triggerContext = new TriggerContext();
    //when
    triggerContext.setProperty("foo", "bar");
    //then
    assertThat(triggerContext.getProperty(String.class)).isEqualTo("bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfSinglePropertyIsAmbiguous() {
    //given
    TriggerContext<?> triggerContext = new TriggerContext();
    //when
    triggerContext.setProperty("foo", "bar");
    triggerContext.setProperty("many", "bar");
    //then
    assertThat(triggerContext.getProperty(String.class)).isEqualTo("bar");
  }

  @Test
  public void shouldGetPropertyByKey() {
    //given
    TriggerContext triggerContext = new TriggerContext();
    //when
    triggerContext.setProperty("foo", "bar");
    //then
    assertThat(triggerContext.getProperty("foo", String.class)).isEqualTo("bar");
  }
}
