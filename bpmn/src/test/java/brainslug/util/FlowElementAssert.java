package brainslug.util;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.assertj.core.api.IterableAssert;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class FlowElementAssert extends IterableAssert<FlowElement> {

  List<FlowElement> flowElements = null;
  Map<Class<?>, List<FlowElement>> flowElementsByType = new HashMap<Class<?>, List<FlowElement>>();

  protected FlowElementAssert(Collection<FlowElement> actual) {
    super(actual);
    flowElements = new ArrayList<FlowElement>(actual);
    initFlowElementsMap();
  }

  private void initFlowElementsMap() {
    for (FlowElement flowElement : flowElements) {
      if (flowElementsByType.get(flowElement.getClass()) == null) {
        flowElementsByType.put(flowElement.getClass(), new ArrayList<FlowElement>());
      }

      flowElementsByType.get(flowElement.getClass()).add(flowElement);
    }
  }

  public static FlowElementAssert assertThat(Collection<FlowElement> actual) {
    return new FlowElementAssert(actual);
  }

  public FlowElementAssert hasFlowElementsWithType(Class clazz, int expectedCount) {
    int clazzCount = 0;
    for (FlowElement element : flowElements) {
      if (clazz.isInstance(element)) {
        clazzCount++;
      }
    }
    assertEquals(expectedCount, clazzCount);
    return this;
  }

  public FlowElementAssert hasFlowElement(Class<? extends FlowElement> elementClass, String expectedId, String expectedName) {
    for (FlowElement flowElement : getFlowElements(elementClass)) {
      if ( (flowElement.getId() == expectedId || flowElement.getId().equals(expectedId)) &&
           (flowElement.getName() == expectedName || flowElement.getName().equals(expectedName)) ) {
        return this;
      }
    }
    throw new AssertionError("flowElement with id: " + expectedId + " and name: " + expectedName + " not found");
  }

  <T> List<T> getFlowElements(Class<T> clazz) {
    if (flowElementsByType.get(clazz) == null) {
      throw new IllegalArgumentException("no elements of class " + clazz + " in flow element list");
    }
    return (List<T>) flowElementsByType.get(clazz);
  }

  public FlowElementAssert hasSequenceFlow(String expectedSourceRef, String expectedTargetRef) {
    return hasSequenceFlow(expectedSourceRef, expectedTargetRef, null);
  }

  public FlowElementAssert hasSequenceFlowWithExpression(String expectedSourceRef, String expectedTargetRef, String conditionExpression) {
    return hasSequenceFlow(expectedSourceRef, expectedTargetRef, conditionExpression);
  }

  private FlowElementAssert hasSequenceFlow(String expectedSourceRef, String expectedTargetRef, String conditionExpression) {
    for (SequenceFlow flow : getFlowElements(SequenceFlow.class)) {
      if(flow.getSourceRef().equals(expectedSourceRef) &&
          flow.getTargetRef().equals(expectedTargetRef) &&
          ( (flow.getConditionExpression() == null && conditionExpression == null) ||
          flow.getConditionExpression().equals(conditionExpression))) {
        return this;
      }
    }
    throw new AssertionError("sequence flow with source: " + expectedSourceRef + " and target: " + expectedTargetRef + " not found");
  }

  public FlowElementAssert hasConditionExpression(int index, String expectedConditionExpression) {
    assertEquals(expectedConditionExpression, ((SequenceFlow)flowElements.get(index)).getConditionExpression());
    return this;
  }

}
