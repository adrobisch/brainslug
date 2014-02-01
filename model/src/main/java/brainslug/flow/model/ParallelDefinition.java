package brainslug.flow.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParallelDefinition extends FlowNodeDefinition<ParallelDefinition> {

  FlowPathDefinition path;
  List<AndDefinition> parallelPaths = new ArrayList<AndDefinition>();

  public ParallelDefinition(FlowPathDefinition path) {
    this.path = path;
  }

  public AndDefinition fork() {
    return addAndDefinition(new AndDefinition(path.definition, this));
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
