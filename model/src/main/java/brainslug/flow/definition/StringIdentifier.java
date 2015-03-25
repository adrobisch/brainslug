package brainslug.flow.definition;

public class StringIdentifier implements Identifier<String> {

  private String id;

  public StringIdentifier(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
      return this.id;
  }

  @Override
  public String stringValue() {
    return id;
  }

  @Override
  public boolean equals(Object o) {

    if (o instanceof EnumIdentifier) {
      return ((EnumIdentifier) o).getId().name().equals(id);
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StringIdentifier that = (StringIdentifier) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return id;
  }
}