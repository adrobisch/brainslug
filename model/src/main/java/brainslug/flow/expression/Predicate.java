package brainslug.flow.expression;

public interface Predicate<Context> {
  boolean isFulfilled(Context context);
}
