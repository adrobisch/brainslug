package brainslug.flow.node;

import brainslug.flow.path.AndDefinition;
import brainslug.flow.path.FlowPathDefinition;

import java.util.ArrayList;
import java.util.List;

public class ParallelDefinition extends FlowNodeDefinition<ParallelDefinition> {

  FlowPathDefinition path;
  List<AndDefinition> parallelPaths = new ArrayList<AndDefinition>();

  public ParallelDefinition(FlowPathDefinition path) {
    this.path = path;
  }

  public AndDefinition fork() {
    return addAndDefinition(new AndDefinition(path.getDefinition(), this));
  }

  public AndDefinition addAndDefinition(AndDefinition and) {
    if (parallelPaths.contains(and)) {
      throw new IllegalArgumentException("you can add an and definition only once");
    }
    parallelPaths.add(and);
    return and;
  }

  public List<AndDefinition> getParallelPaths() {
    return parallelPaths;
  }
}
