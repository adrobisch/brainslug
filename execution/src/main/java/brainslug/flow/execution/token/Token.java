package brainslug.flow.execution.token;

import brainslug.flow.definition.Identifier;
import brainslug.util.IdUtil;
import brainslug.util.Option;

public class Token {
  Identifier id;
  Identifier nodeId;
  Option<Identifier> sourceNode;
  Option<Identifier> instanceId;
  boolean isDead;

  public Token(String id, String nodeId, String sourceNodeId, String instanceId, int isDead) {
    this(IdUtil.id(id),
      IdUtil.id(nodeId),
      Option.of(IdUtil.id(sourceNodeId)),
      Option.of(IdUtil.id(instanceId)),
      isDead == 1);
  }

  public Token(Identifier id, Identifier nodeId, Option<Identifier> sourceNode, Option<Identifier> instanceId, boolean isDead) {
    this.id = id;
    this.nodeId = nodeId;
    this.sourceNode = sourceNode;
    this.instanceId = instanceId;
    this.isDead = isDead;
  }

  public Identifier getId() {
    return id;
  }

  public Identifier getNodeId() {
    return nodeId;
  }

  public Option<Identifier> getSourceNode() {
    return sourceNode;
  }

  public Option<Identifier> getInstanceId() {
    return instanceId;
  }

  public boolean isRootToken() {
    return !sourceNode.isPresent();
  }

  public boolean isDead() {
    return isDead;
  }

  public Token setDead(boolean isDead) {
    this.isDead = isDead;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Token token = (Token) o;

    if (isDead != token.isDead) return false;
    if (id != null ? !id.equals(token.id) : token.id != null) return false;
    if (instanceId != null ? !instanceId.equals(token.instanceId) : token.instanceId != null) return false;
    if (nodeId != null ? !nodeId.equals(token.nodeId) : token.nodeId != null) return false;
    if (sourceNode != null ? !sourceNode.equals(token.sourceNode) : token.sourceNode != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
    result = 31 * result + (sourceNode != null ? sourceNode.hashCode() : 0);
    result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
    result = 31 * result + (isDead ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Token{" +
      "id=" + id +
      ", nodeId=" + nodeId +
      ", sourceNode=" + sourceNode +
      ", instanceId=" + instanceId +
      ", isDead=" + isDead +
      '}';
  }
}
