package brainslug.flow.node.task;

public interface GoalPredicate<Context> {
  Boolean isFulfilled(Context context);
}
