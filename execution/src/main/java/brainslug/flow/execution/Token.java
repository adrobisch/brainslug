package brainslug.flow.execution;

import brainslug.flow.model.Identifier;
import brainslug.util.Option;

public class Token {
  Identifier id;
  Identifier nodeId;
  Option<Identifier> sourceNode;
  Option<Identifier> instanceId;

  public Token(Identifier id, Identifier nodeId, Option<Identifier> sourceNode, Option<Identifier> instanceId) {
    this.id = id;
    this.nodeId = nodeId;
    this.sourceNode = sourceNode;
    this.instanceId = instanceId;
  }

  public Identifier getId() {
    return id;
  }

  public Option<Identifier> getSourceNode() {
    return sourceNode;
  }

  public boolean isRootToken() {
    return !sourceNode.isPresent();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Token token = (Token) o;

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
    return result;
  }

  @Override
  public String toString() {
    return "Token{" +
      "id=" + id +
      ", nodeId=" + nodeId +
      ", sourceNode=" + sourceNode +
      ", instanceId=" + instanceId +
      '}';
  }
}
