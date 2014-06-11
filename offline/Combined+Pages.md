
# brainslug

brainslug is a control flow abstraction library. 
It enables you to model your business logic flow as a graph of typed nodes, 
which can be transformed to different representations or be executed within a customisable environment.

# Motivation

> "Do we really need another Workflow-BPM-whatever engines on the JVM? Smells like 'Not invented here'!"

Activiti and camunda BPM and jBPM might be the most popular on the list, but they all have some kind bigger legacy and target environment 
they have to deal with and its more difficult to just get to their core, e.g. to understand there internal execution model. 

For example, Activiti is very focused on BPMN 2.0 and its jBPM 4 roots, jBPM5 is coming from the business rule side, 
BPEL engines makes most sense in WS-* environments.

To some degree they share some strong assumptions:

* Relational persistence (with JDBC), execution semantics are often strongly coupled to the DB transaction management
* high initial learning effort required to learn the workflow description language like BPMN, BPEL, ... 
the BPMN 2.0 specification is a several hundred pages big
* Dynamic expression evaluation, at least to control the flow
* You are living in a container (Tomcat / Spring, Java Enterprise Edition)
* XML based descriptions / configurations

Those points make them look heavy-weight and inflexible. 
*brainslug* aims to provide a small workflow library in Java without these strings attached.

# Getting started

## Download

The current version 0.9 is available in the maven central repository:

```xml
  <dependencies>
  ...
    <dependency>
      <groupId>de.androbit</groupId>
      <artifactId>brainslug-model</artifactId>
      <version>0.9</version>
    </dependency>
    <dependency>
      <groupId>de.androbit</groupId>
      <artifactId>brainslug-execution</artifactId>
      <version>0.9</version>
    </dependency>
  ...
  </dependencies>
```

## Example

The main [model](model) class is `brainslug.flow.model.FlowBuilder`. A new Flow Definition is specified in its define method:

```java
  new FlowBuilder() {
    @Override
    public void define() {
      start(event(id("start")))
        .execute(task(id("task")).display("A Task"))
        .execute(task(id("task2")).display("Another Task"))
      .end(event(id("end")));
    }
  };
```

# Definitions

## Flow Definition

A flow definition is a directed graph of typed flow nodes. brainslug provides a set of predefined node types
which are interpreted by the execution module. Generally its up to the user to define the meaning of the graph,
but its common to see it as the definition of possible paths / sequence of actions for a desired outcome, e.g.
the steps to prepare a sandwich, or a ordering process.

## Usage

See the [FlowBuilderTest](https://github.com/adrobisch/brainslug/blob/master/model/src/test/java/brainslug/flow/builder/FlowBuilderTest.java) class
for examples on how to build flow definitions.

# Execution

Flow Definitions can be executed, read this section to learn about the concepts and 
semantics of a execution.

## Concepts

### Flow Instance

A flow instance is a single execution of a given flow definition. Depending on the brainslug configuration and flow definition
an instance might be a persistent and long running, or just be executed in-memory.

### Flow Token

A flow token is a pointer to a flow node in a flow instance. A flow node might have multiple tokens.
The token includes the information it came from. A flow node needs the correct amount of tokens to be ready
for execution and will produce a defined amount of new flow node after execution.

## Token Semantics

### Task Node

A Task Node will be executed for every incoming token and produces one token per outgoing edge.

### Event Node

An Event Node is triggered by every incoming token and produces one token per outgoing edge.

### Choice Node

A Choice Node will be executed for every incoming token. A token is produced for the first outgoing path
where the predicate is fullfilled.

### Merge Node

A Merge Node will be executed for every incoming token. A token is produced for every outgoing edge.

### Parallel Node

A Parallel Node will be executed for every incoming token. A token is produced for every outgoing edge.

### Join Node

A Parallel Node will only be executed if it has tokens from every incoming edge.
A token is produced for every outgoing edge.

## Execution Example

To execute a flow definition, you need to create a brainslug context.
This will create a default context with a service instance:

```java
    BrainslugContext context = new BrainslugContext();
    context.getRegistry().registerService(TestService.class, new TestService());
```

then define the flow and add it to the context:

```java
    FlowDefinition flow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
          .execute(task(id("task")).display("A Task"))
          .execute(task(id("task2")).display("Another Task"))
        .end(event(id("end")));
      }
    }.getDefinition();
   context.addFlowDefinition(flow);
```

and trigger a node event for the node you want to start with:

```java
context.trigger(new TriggerEvent().nodeId(id("start")).definitionId(flow.getId()))
```

## Integration with your code

In order to connect you code with a brainslug flow definition you may use method call definition:

```java
    new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START)))
        .execute(task(id(TASK))
          .call(service(TestService.class).method("getString")))
        .end(event(id(END)));
      }
    }
```

or a delegate class:

```java
    class Delegate {
      @Execute
      public void execute(ExecutionContext context) {
      ...
      }
    }
    ...
    context.getRegistry().registerService(Delegate.class, new Delegate());
    ...
    new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START)))
          .execute(task(id(TASK)).delegate(Delegate.class)))
        .end(event(id(END)));
      }
    }
```

# BPMN

brainslug is able to export flow definitions to BPMN 2.0 as Image or XML definitions file.

## Renderer Example

```java
    JGraphRenderer renderer = new JGraphRenderer(new GraphFactory());
    FileOutputStream outputStream = ...
    FlowBuilder flowBuilder = ...
    renderer.render(flowBuilder, outputStream, Format.PNG);
```

![task_flow](images/task_flow.png)

also check [JGraphRendererTest](https://github.com/adrobisch/brainslug/blob/master/renderer/src/test/java/brainslug/flow/renderer/JGraphRendererTest.java).

## XML Definitions export

```java
    FlowBuilder flowBuilder =  new FlowBuilder() {
                                 @Override
                                 public void define() {
                                   start(event(id("start")))
                                     .execute(task(id("task")).display("A Task"))
                                     .execute(task(id("task2")).display("Another Task"))
                                   .end(event(id("end")));
                                 }
                               };
    BpmnModelTransformer bpmnModelTransformer = new BpmnModelTransformer();
    String bpmnXml = bpmnModelTransformer.toBpmnXml(flowBuilder);
```

will produce

```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:activiti="http://activiti.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.activiti.org/test">
    <process id="471ff134-4050-497b-bf80-d1fa5164f311" name="471ff134-4050-497b-bf80-d1fa5164f311" isExecutable="true">
      <startEvent id="start" name="start"></startEvent>
      <serviceTask id="task" name="A Task"></serviceTask>
      <serviceTask id="task2" name="Another Task"></serviceTask>
      <endEvent id="end" name="end"></endEvent>
      <sequenceFlow sourceRef="start" targetRef="task"></sequenceFlow>
      <sequenceFlow sourceRef="task" targetRef="task2"></sequenceFlow>
      <sequenceFlow sourceRef="task2" targetRef="end"></sequenceFlow>
    </process>
    <bpmndi:BPMNDiagram id="BPMNDiagram_471ff134-4050-497b-bf80-d1fa5164f311">
      <bpmndi:BPMNPlane bpmnElement="471ff134-4050-497b-bf80-d1fa5164f311" id="BPMNPlane_471ff134-4050-497b-bf80-d1fa5164f311"></bpmndi:BPMNPlane>
    </bpmndi:BPMNDiagram>
  </definitions>
```

also check [BpmnModelTransformerTest](https://github.com/adrobisch/brainslug/blob/master/bpmn/src/test/java/brainslug/bpmn/BpmnModelTransformerTest.java).
