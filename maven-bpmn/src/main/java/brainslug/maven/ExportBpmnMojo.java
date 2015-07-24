package brainslug.maven;

import brainslug.bpmn.BpmnModelExporter;
import brainslug.flow.builder.FlowBuilder;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mojo(name = "export-bpmn", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ExportBpmnMojo extends AbstractMojo {
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
  private List<String> classpath;

  @Parameter(required = true)
  private String flows;

  @Parameter(required = false)
  private String outputDir = "flows";

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    for (String flowName : flows.split(",")) {
      FlowBuilder flowBuilder = createFlowInstance(flowName.trim(), setupClassPath());
      exportBpmn(flowBuilder, getFileOutputStream(getOutputFile(flows + "." + "bpmn")));
    }
  }

  private void exportBpmn(FlowBuilder flowBuilder, FileOutputStream fileOutputStream) {
    BpmnModelExporter bpmnModelExporter = new BpmnModelExporter();
    BpmnModel bpmnModel = bpmnModelExporter.toBpmnModel(flowBuilder);
    new BpmnAutoLayout(bpmnModel).execute();
    String bpmnXml = bpmnModelExporter.toBpmnXml(bpmnModel);
    writeToFile(fileOutputStream, bpmnXml);
  }

  private void writeToFile(FileOutputStream fileOutputStream, String bpmnXml) {
    try {
      fileOutputStream.write(bpmnXml.getBytes("UTF-8"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        fileOutputStream.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private FlowBuilder createFlowInstance(String flowName, ClassLoader classLoader) {
    try {
      Class<FlowBuilder> flowClass = (Class<FlowBuilder>) Class.forName(flowName, true, classLoader);
      try {
        return flowClass.newInstance();
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("unable to find flow with name: " +  flowName, e);
    }
  }

  private File getOutputFile(String fileName) {
    return new File(getOutputDirectory().getAbsolutePath() + File.separatorChar + fileName);
  }

  private File getOutputDirectory() {
    File outputDirectory = new File(project.getBuild().getDirectory() + File.separatorChar + outputDir);
    outputDirectory.mkdirs();
    return outputDirectory;
  }

  private FileOutputStream getFileOutputStream(File outputFile) {
    try {
      return new FileOutputStream(outputFile);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException(outputFile + " is a invalid path.");
    }
  }

  ClassLoader setupClassPath() {
    try {
      Set<URL> urls = new HashSet<URL>();

      for (String element : classpath) {
        urls.add(new File(element).toURI().toURL());
      }

      ClassLoader contextClassLoader = URLClassLoader.newInstance(
        urls.toArray(new URL[0]),
        Thread.currentThread().getContextClassLoader());

      Thread.currentThread().setContextClassLoader(contextClassLoader);

      return contextClassLoader;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
