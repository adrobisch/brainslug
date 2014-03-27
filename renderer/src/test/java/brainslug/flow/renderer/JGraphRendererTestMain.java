package brainslug.flow.renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

public class JGraphRendererTestMain {

  public static void main(String[] args) throws FileNotFoundException {
    Format format = Format.JPG;
    JGraphRenderer renderer = new JGraphRenderer(new DefaultSkin());
    FileOutputStream output = new FileOutputStream(new File("" + UUID.randomUUID() + "." + format.name().toLowerCase()));
    renderer.render(JGraphRendererTest.afterFlow(), output, format);
  }

}
