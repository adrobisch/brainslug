package brainslug.flow.definition;

public class EnumIdentifier implements Identifier<Enum> {

  private Enum id;

  public EnumIdentifier(Enum id) {
    this.id = id;
  }

  @Override
  public Enum getId() {
      return this.id;
  }

  @Override
  public String stringValue() {
    return id.name();
  }

  @Override
  public boolean equals(Object o) {

    if (o instanceof StringIdentifier) {
      return ((StringIdentifier) o).getId().equals(id.name());
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EnumIdentifier that = (EnumIdentifier) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id != null ? id.name().hashCode() : 0;
  }

  @Override
  public String toString() {
    return id.name();
  }
}