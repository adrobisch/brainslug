package brainslug.maven;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.renderer.DefaultSkin;
import brainslug.flow.renderer.Format;
import brainslug.flow.renderer.JGraphBpmnRenderer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mojo(name = "render-flows")
public class RenderFlowsMojo extends AbstractMojo {
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
  private List<String> classpath;

  @Parameter(required = true)
  private String flows;

  @Parameter(required = false)
  private String format = "PNG";

  @Parameter(required = false)
  private String outputDir = "flows";

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    for (String flowName : flows.split(",")) {
      FlowBuilder flowBuilder = createFlowInstance(flowName.trim(), setupClassPath());
      renderImage(flowBuilder);
    }
  }

  private void renderImage(FlowBuilder flowBuilder) {
    Format imageFormat = Format.valueOf(format.toUpperCase());

    FileOutputStream fileOutputStream = getFileOutputStream(getOutputFile(flows + "." + imageFormat.name().toLowerCase()));
    new JGraphBpmnRenderer(new DefaultSkin()).render(flowBuilder, fileOutputStream, imageFormat);
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
