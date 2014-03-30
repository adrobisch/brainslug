package brainslug.flow.model;

import java.util.ArrayList;
import java.util.List;

public class DefinitionStore {
  List<FlowDefinition> flowDefinitions = new ArrayList<FlowDefinition>();

  public void addDefinition(FlowDefinition flowDefinition) {
    flowDefinitions.add(flowDefinition);
  }

  public FlowDefinition findById(Identifier id) {
    for (FlowDefinition definition : flowDefinitions) {
      if (definition.getId().equals(id)) {
        return definition;
      }
    }
    throw new IllegalArgumentException(String.format("no flow with id %s found", id));
  }

}
