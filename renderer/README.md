Renderer
========
This module contains the renderer based on the BPMN symbols using JGraph

Example
=======

```java
    JGraphRenderer renderer = new JGraphRenderer(new GraphFactory());
    FileOutputStream outputStream = ...
    FlowBuilder flowBuilder = ...
    renderer.render(flowBuilder, outputStream, Format.PNG);
```

also check [JGraphRendererTest](src/test/java/brainslug/flow/renderer/JGraphRendererTest.java).
