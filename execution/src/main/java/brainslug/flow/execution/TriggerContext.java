package brainslug.flow.execution;

import brainslug.flow.EnumIdentifier;
import brainslug.flow.Identifier;
import brainslug.flow.StringIdentifier;

public class TriggerContext<T extends TriggerContext> {

  protected Identifier definitionId;
  protected Identifier instanceId;
  protected Identifier nodeId;
  protected Boolean async = false;

  protected ExecutionProperties properties;

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

  public T definitionId(Enum id) {
    return definitionId(new EnumIdentifier(id));
  }

  public T definitionId(String id) {
    return definitionId(new StringIdentifier(id));
  }

  public T definitionId(Identifier definitionId) {
    this.definitionId = definitionId;
    return self();
  }

  public T instanceId(Identifier instanceId) {
    this.instanceId = instanceId;
    return self();
  }

  public T nodeId(Identifier nodeId) {
    this.nodeId = nodeId;
    return self();
  }

  public T async(Boolean async) {
    this.async = async;
    return self();
  }

  public T properties(ExecutionProperties properties) {
    this.properties = properties;
    return self();
  }

  public T property(Object value) {
    setProperty(value.getClass().getName(), value);
    return self();
  }

  public T property(Object key, Object value) {
    setProperty(key, value);
    return self();
  }

  public void setProperty(Object key, Object value) {
    getProperties().put(key, value);
  }

  public <P> P getProperty(Object key, Class<P> type) {
    return (P) getProperties().get(key);
  }

  public <P> P getProperty(Class<P> type) {
    P result = null;
    int typeCount = 0;
    for (Object object : getProperties().values()) {

      if(object.getClass().isAssignableFrom(type)) {
        result = (P) object;
        typeCount++;
      }
    }
    if (typeCount == 0) {
      throw new IllegalArgumentException(String.format("no property of type %s exists", type));
    }else if(typeCount > 1) {
      throw new IllegalArgumentException(String.format("multiple properties of type %s exist", type));
    }
    return result;
  }

  public ExecutionProperties getProperties() {
    if (properties == null) {
      properties = new ExecutionProperties();
    }
    return properties;
  }

  public Boolean isAsync() {
    return async;
  }

  /**
   * TODO: should we include the properties into equals?
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TriggerContext that = (TriggerContext) o;

    if (definitionId != null ? !definitionId.equals(that.definitionId) : that.definitionId != null) return false;
    if (instanceId != null ? !instanceId.equals(that.instanceId) : that.instanceId != null) return false;
    if (nodeId != null ? !nodeId.equals(that.nodeId) : that.nodeId != null) return false;
    if (async != null ? !async.equals(that.async) : that.async != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = definitionId != null ? definitionId.hashCode() : 0;
    result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
    result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
    result = 31 * result + (async != null ? async.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TriggerContext{" +
      "definitionId=" + definitionId +
      ", instanceId=" + instanceId +
      ", nodeId=" + nodeId +
      ", async=" + async +
      '}';
  }
}
