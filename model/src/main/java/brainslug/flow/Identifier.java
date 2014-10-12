package brainslug.flow;

public interface Identifier<T> {
  T getId();
  String stringValue();
}
