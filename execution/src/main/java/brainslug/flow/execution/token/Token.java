package brainslug.flow.execution.token;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.instance.FlowInstanceToken;
import brainslug.util.IdUtil;
import brainslug.util.Option;

public class Token implements FlowInstanceToken {
  Identifier id;
  Identifier nodeId;
  Option<Identifier> sourceNode;
  Identifier instanceId;
  boolean isDead;
  boolean isFinal;

  public Token(String id, String nodeId, String sourceNodeId, String instanceId, int isDead, int isFinal) {
    this(IdUtil.id(id),
      IdUtil.id(nodeId),
      Option.of(IdUtil.id(sourceNodeId)),
      IdUtil.id(instanceId),
      isDead == 1,
      isFinal == 1);
  }

  public Token(Identifier id, Identifier nodeId, Option<Identifier> sourceNode, Identifier instanceId, boolean isDead, boolean isFinal) {
    this.id = id;
    this.nodeId = nodeId;
    this.sourceNode = sourceNode;
    this.instanceId = instanceId;
    this.isDead = isDead;
    this.isFinal = isFinal;
  }

  @Override
  public Identifier getId() {
    return id;
  }

  @Override
  public Identifier getNodeId() {
    return nodeId;
  }

  @Override
  public Option<Identifier> getSourceNodeId() {
    return sourceNode;
  }

  @Override
  public Identifier getInstanceId() {
    return instanceId;
  }

  @Override
  public boolean isDead() {
    return isDead;
  }

  @Override
  public boolean isFinal() {
    return isFinal;
  }

  public void setDead(boolean isDead) {
    this.isDead = isDead;
  }

  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Token token = (Token) o;

    if (isDead != token.isDead) return false;
    if (isFinal != token.isFinal) return false;
    if (id != null ? !id.equals(token.id) : token.id != null) return false;
    if (nodeId != null ? !nodeId.equals(token.nodeId) : token.nodeId != null) return false;
    if (sourceNode != null ? !sourceNode.equals(token.sourceNode) : token.sourceNode != null) return false;
    return !(instanceId != null ? !instanceId.equals(token.instanceId) : token.instanceId != null);

  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
    result = 31 * result + (sourceNode != null ? sourceNode.hashCode() : 0);
    result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
    result = 31 * result + (isDead ? 1 : 0);
    result = 31 * result + (isFinal ? 1 : 0);
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
            ", isFinal=" + isFinal +
            '}';
  }
}
