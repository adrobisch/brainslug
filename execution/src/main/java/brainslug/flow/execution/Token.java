package brainslug.flow.execution;

import brainslug.flow.model.Identifier;

public class Token {
  Identifier sourceNode;

  public Token(Identifier sourceNode) {
    this.sourceNode = sourceNode;
  }

  public Identifier getSourceNode() {
    return sourceNode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Token token = (Token) o;

    if (sourceNode != null ? !sourceNode.equals(token.sourceNode) : token.sourceNode != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return sourceNode != null ? sourceNode.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Token{" +
      "sourceNode=" + sourceNode +
      '}';
  }
}
