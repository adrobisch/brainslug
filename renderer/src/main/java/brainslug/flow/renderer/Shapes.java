package brainslug.flow.renderer;

import brainslug.flow.model.*;

public class Shapes {
  public static final String EndEvent = "End-Event";
  public static final String StartEvent = "Start-Event";
  public static final String IntermediateEvent = "Intermediate-Event";
  public static final String Rounded = "Rounded";
  public static final String GatewayExclusive = "Gateway-Exclusive";
  public static final String GatewayParallel = "Gateway-Parallel";

  public static String getShape(FlowNodeDefinition<?> node) {
    if (node instanceof AbstractTaskDefinition) {
      return Shapes.Rounded;
    }
    if (node instanceof ChoiceDefinition) {
      return Shapes.GatewayExclusive;
    }
    if (node instanceof MergeDefinition) {
      return Shapes.GatewayExclusive;
    }
    if (node instanceof JoinDefinition) {
      return Shapes.GatewayParallel;
    }
    if (node instanceof ParallelDefinition) {
      return Shapes.GatewayParallel;
    }
    if (node.hasMixin(brainslug.flow.model.marker.EndEvent.class)) {
      return Shapes.EndEvent;
    }
    if (node.hasMixin(brainslug.flow.model.marker.StartEvent.class)) {
      return Shapes.StartEvent;
    }
    if (node.hasMixin(brainslug.flow.model.marker.IntermediateEvent.class)) {
      return Shapes.IntermediateEvent;
    }

    throw new IllegalArgumentException("no style definition available for " + node);
  }
}
