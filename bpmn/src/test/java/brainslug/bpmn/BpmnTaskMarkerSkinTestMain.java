package brainslug.bpmn;

import brainslug.flow.renderer.Format;
import brainslug.flow.renderer.JGraphRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

public class BpmnTaskMarkerSkinTestMain {

  public static void main(String[] args) throws FileNotFoundException {
    Format format = Format.PNG;
    JGraphRenderer renderer = new JGraphRenderer(new BpmnTaskMarkerSkin());
    FileOutputStream output = new FileOutputStream(new File("" + UUID.randomUUID() + "." + format.name().toLowerCase()));
    renderer.render(new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(event(id("start"))).execute(serviceTask(id("task1")).display("Hallo")).execute(userTask(id("usertask"))).end(event(id("end")));
      }
    }, output, format);
  }

}
