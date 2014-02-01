package brainslug.util;

public interface Mixable<SelfType> {
  public <MixinType> SelfType with(MixinType mixinInstance);
  public <MixinType> SelfType with(Class clazz, MixinType mixinInstance);
  public <MixinType> MixinType as(Class<MixinType> clazz);
  public boolean hasMixin(Class<?> mixinClass);
}
