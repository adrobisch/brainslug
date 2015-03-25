package brainslug.flow.definition;

public interface Identifier<T> {
  T getId();
  String stringValue();
}
