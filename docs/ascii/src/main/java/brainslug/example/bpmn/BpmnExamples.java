package brainslug.example.bpmn;

import brainslug.bpmn.BpmnModelTransformer;
import brainslug.flow.renderer.DefaultSkin;
import brainslug.flow.renderer.Format;
import brainslug.flow.renderer.JGraphBpmnRenderer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static brainslug.SimpleExamples.*;

public class BpmnExamples {

  public static void main(String[] args) throws FileNotFoundException {
//# tag::renderer-example[]
    Format format = Format.JPG;
    JGraphBpmnRenderer renderer = new JGraphBpmnRenderer(new DefaultSkin());

    String fileName = simpleFlow.getName() + "." + format.name();
    FileOutputStream outputFile = new FileOutputStream(fileName);

    renderer.render(simpleFlow, outputFile, format);
//# end::renderer-example[]
  }

  public void transformExample() {
//# tag::transformer-example[]
    BpmnModelTransformer bpmnModelTransformer = new BpmnModelTransformer();
    String bpmnXml = bpmnModelTransformer.toBpmnXml(simpleFlow);
//# end::transformer-example[]
  }

}
