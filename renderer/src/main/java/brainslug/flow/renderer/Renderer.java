package brainslug.flow.renderer;

import brainslug.flow.builder.FlowBuilder;

import java.io.OutputStream;

public interface Renderer {
  public void render(FlowBuilder flowBuilder, OutputStream outputStream, Format format);
}
