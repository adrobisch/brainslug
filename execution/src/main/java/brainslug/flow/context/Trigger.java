package brainslug.flow.context;

import brainslug.flow.definition.EnumIdentifier;
import brainslug.flow.definition.Identifier;
import brainslug.flow.definition.StringIdentifier;
import brainslug.flow.execution.property.ExecutionProperties;
import brainslug.flow.execution.instance.FlowInstanceProperties;
import brainslug.flow.execution.instance.FlowInstanceProperty;

public class Trigger<T extends Trigger> implements TriggerContext {

  protected Identifier definitionId;
  protected Identifier instanceId;
  protected Identifier nodeId;
  protected Boolean async = false;
  protected Boolean signaling = false;

  protected FlowInstanceProperties<?, FlowInstanceProperty<?>> properties;

  @Override
  public Identifier getDefinitionId() {
    return definitionId;
  }

  @Override
  public Identifier getInstanceId() {
    return instanceId;
  }

  @Override
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

  public T signaling(Boolean signaling) {
    this.signaling = signaling;
    return self();
  }

  public T properties(FlowInstanceProperties properties) {
    this.properties = properties;
    return self();
  }

  public void setProperties(FlowInstanceProperties properties) {
    this.properties = properties;
  }

  public T property(Object value) {
    setProperty(value.getClass().getName(), value);
    return self();
  }

  public T property(String key, Object value) {
    setProperty(key, value);
    return self();
  }

  public T property(String key, Object value, boolean isTransient) {
    setProperty(key, value, isTransient);
    return self();
  }

  public T property(Identifier<?> key, Object value) {
    setProperty(key.stringValue(), value);
    return self();
  }

  public T property(Identifier<?> key, Object value, boolean isTransient) {
    setProperty(key.stringValue(), value, isTransient);
    return self();
  }

  @Override
  public void setProperty(String key, Object value) {
    getProperties().with(key, value);
  }

  @Override
  public void setProperty(String key, Object value, boolean isTransient) {
    getProperties().with(key, value, isTransient);
  }

  @Override
  public <P> P getProperty(String key, Class<P> type) {
    return getProperties().value(key, type);
  }

  @Override
  public FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties() {
    if (properties == null) {
      properties = new ExecutionProperties();
    }
    return properties;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean isAsync() {
    return async;
  }

  @Override
  public Boolean isSignaling() {
    return signaling;
  }

  /**
   * TODO: should we include the setProperties into equals?
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Trigger that = (Trigger) o;

    if (definitionId != null ? !definitionId.equals(that.definitionId) : that.definitionId != null) return false;
    if (instanceId != null ? !instanceId.equals(that.instanceId) : that.instanceId != null) return false;
    if (nodeId != null ? !nodeId.equals(that.nodeId) : that.nodeId != null) return false;
    if (async != null ? !async.equals(that.async) : that.async != null) return false;
    if (signaling != null ? !signaling.equals(that.signaling) : that.signaling != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = definitionId != null ? definitionId.hashCode() : 0;
    result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
    result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
    result = 31 * result + (async != null ? async.hashCode() : 0);
    result = 31 * result + (signaling != null ? signaling.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TriggerContext{" +
      "definitionId=" + definitionId +
      ", instanceId=" + instanceId +
      ", nodeId=" + nodeId +
      ", async=" + async +
      ", signaling=" + signaling +
      '}';
  }
}
