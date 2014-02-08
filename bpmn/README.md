BPMN
====
This module contains the transformation from flow definitions to BPMN 2.0 XML

Example
=======

```java
    FlowBuilder flowBuilder = ...
    BpmnModelTransformer bpmnModelTransformer = new BpmnModelTransformer();
    String bpmnXml = bpmnModelTransformer.toBpmnXml(flowBuilder);
```

also check [BpmnModelTransformerTest](src/test/java/brainslug/bpmn/BpmnModelTransformerTest.java).
