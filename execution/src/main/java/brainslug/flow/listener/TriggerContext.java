package brainslug.flow.listener;

import brainslug.flow.model.EnumIdentifier;
import brainslug.flow.model.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TriggerContext<T extends TriggerContext> {

  protected Identifier id;
  protected Identifier definitionId;
  protected Identifier instanceId;
  protected Identifier nodeId;
  protected Identifier<?> sourceNodeId;

  protected Map<Object, Object> properties;

  public Identifier<?> getSourceNodeId() {
    return sourceNodeId;
  }

  public Identifier getId() {
    return id;
  }

  public Identifier getDefinitionId() {
    return definitionId;
  }

  public Identifier getInstanceId() {
    return instanceId;
  }

  public Identifier getNodeId() {
    return nodeId;
  }

  T self() {
    return (T) this;
  }

  public T id(Identifier id) {
    this.id = id;
    return self();
  }

  public T definitionId(Enum id) {
    return definitionId(new EnumIdentifier(id));
  }

  public T definitionId(Identifier definitionId) {
    this.definitionId = definitionId;
    return self();
  }

  public T instanceId(Identifier definitionId) {
    this.instanceId = definitionId;
    return self();
  }

  public T nodeId(Identifier nodeId) {
    this.nodeId = nodeId;
    return self();
  }

  public T sourceNodeId(Identifier sourceNodeId) {
    this.sourceNodeId = sourceNodeId;
    return self();
  }

  public void setProperty(Object key, Object value) {
    if (properties == null) {
      properties = new HashMap<Object, Object>();
    }
    properties.put(key, value);
  }

  public <T> T getProperty(Object key, Class<T> type) {
    return (T) properties.get(key);
  }

  public <T> T getProperty(Class<T> type) {
    T result = null;
    int typeCount = 0;
    for (Object object : properties.values()) {

      if(object.getClass().isAssignableFrom(type)) {
        result = (T) object;
        typeCount ++;
      }
    }
    if (typeCount == 0) {
      throw new IllegalArgumentException(String.format("no property of type %s exists", type));
    }else if(typeCount > 1) {
      throw new IllegalArgumentException(String.format("multiple properties of type %s exist", type));
    }
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TriggerContext triggerContext = (TriggerContext) o;

    if (definitionId != null ? !definitionId.equals(triggerContext.definitionId) : triggerContext.definitionId != null)
      return false;
    if (id != null ? !id.equals(triggerContext.id) : triggerContext.id != null) return false;
    if (instanceId != null ? !instanceId.equals(triggerContext.instanceId) : triggerContext.instanceId != null) return false;
    if (nodeId != null ? !nodeId.equals(triggerContext.nodeId) : triggerContext.nodeId != null) return false;
    if (sourceNodeId != null ? !sourceNodeId.equals(triggerContext.sourceNodeId) : triggerContext.sourceNodeId != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (definitionId != null ? definitionId.hashCode() : 0);
    result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
    result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
    result = 31 * result + (sourceNodeId != null ? sourceNodeId.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TriggerContext{" +
      "id=" + id +
      ", definitionId=" + definitionId +
      ", instanceId=" + instanceId +
      ", nodeId=" + nodeId +
      ", sourceNodeId=" + sourceNodeId +
      '}';
  }
}
