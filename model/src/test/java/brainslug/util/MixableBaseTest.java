package brainslug.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class MixableBaseTest {

  class MixedClass extends MixableBase<MixedClass> {
  }

  class TestMixin {
    String getValue() {
      return "value";
    }
  }

  @Test
  public void delegatesToMixinInstance() {
    MixedClass cl = new MixedClass().with(TestMixin.class, new TestMixin());
    Assertions.assertThat(cl.as(TestMixin.class).getValue()).isEqualTo("value");
  }

  @Test
  public void canCheckForMixin() {
    MixedClass cl = new MixedClass().with(TestMixin.class, new TestMixin());
    Assertions.assertThat(cl.is(TestMixin.class)).isEqualTo(true);
  }

  @Test(expected = IllegalStateException.class)
  public void onlyOneMixinInstancePerTypeAllowed() {
    MixedClass mixedClass = new MixedClass();
    mixedClass.with(TestMixin.class, new TestMixin());
    mixedClass.with(TestMixin.class, new TestMixin());
  }

}
