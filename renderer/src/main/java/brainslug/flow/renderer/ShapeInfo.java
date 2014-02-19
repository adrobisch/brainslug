package brainslug.flow.renderer;

import com.mxgraph.util.mxConstants;

import java.util.Hashtable;

public class ShapeInfo {
  String name;
  String location;
  Hashtable<String, Object> style;

  public ShapeInfo(String name, String location, Hashtable<String, Object> style) {
    this.name = name;
    this.location = location;
    this.style = style;

    style.put(mxConstants.STYLE_SHAPE, name);
  }

  public String getName() {
    return name;
  }

  public String getLocation() {
    return location;
  }

  public Hashtable<String, Object> getStyle() {
    return style;
  }
}
