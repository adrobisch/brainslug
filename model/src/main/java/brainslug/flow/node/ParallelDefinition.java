package brainslug.flow.node;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.path.AndDefinition;

import java.util.ArrayList;
import java.util.List;

public class ParallelDefinition extends FlowNodeDefinition<ParallelDefinition> {

  private final FlowDefinition definition;
  List<AndDefinition> parallelPaths = new ArrayList<AndDefinition>();

  public ParallelDefinition(FlowDefinition definition) {
    this.definition = definition;
  }

  public AndDefinition fork() {
    return addAndDefinition(new AndDefinition(definition, this));
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
