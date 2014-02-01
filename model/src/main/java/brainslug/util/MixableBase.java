package brainslug.util;

import java.util.HashMap;

public class MixableBase<SelfType> implements Mixable<SelfType> {

  HashMap<Class<?>, Object> mixins = new HashMap<Class<?>, Object>();

  @Override
  public <MixinType> SelfType with(MixinType mixinInstance) {
    return with(mixinInstance.getClass(), mixinInstance);
  }

  @Override
  public <M> SelfType with(Class clazz, M mixinInstance) {
    if (mixins.get(clazz) != null) {
      throw new IllegalStateException("you can only have one mixin instance per type, cant mixin " + clazz);
    }
    mixins.put(clazz, mixinInstance);
    return (SelfType) this;
  }

  @Override
  public <M> M as(Class<M> clazz) {
    if (mixins.get(clazz) == null) {
      throw new IllegalStateException("no mixin of type " + clazz + " found");
    }

    return (M) mixins.get(clazz);
  }

  @Override
  public boolean hasMixin(Class mixinClass) {
    return mixins.get(mixinClass) != null;
  }
}
