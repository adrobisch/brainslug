package brainslug.flow.renderer;

public class ShapeInfo {
  String name;
  String location;

  public ShapeInfo(String name, String location) {
    this.name = name;
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public String getLocation() {
    return location;
  }
}
