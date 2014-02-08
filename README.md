![brainslug](doc/brainslug_big.png)

brainslug
=========

brainslug is a control flow abstraction library. It enables you to model your application flow as a graph of typed nodes,
which can be transformed to different representations or be executed within a customisable environment.

![Build Status](https://travis-ci.org/adrobisch/brainslug.png?branch=master)

Features
========

* Builder DSL for flow definitions
* [BPMN 2.0 XML](http://www.omg.org/spec/BPMN/2.0/) export using the [Activiti](https://github.com/Activiti/Activiti) Model
* Flow Renderer based on the BPMN symbols

Download
========

The current snapshot version is available in the cloudbees repository:

```xml
  <dependencies>
  ...
    <dependency>
      <groupId>de.androbit</groupId>
      <artifactId>brainslug-model</artifactId>
      <version>0.6-SNAPSHOT</version>    
    </dependency>
  ...
  </dependencies>
  
  <repositories>
  ...
    <repository>
      <id>brainslug-snapshots</id>
      <url>http://repository-drobisch.forge.cloudbees.com/snapshot/</url>
    </repository>
  ...
  </repositories>
```

Example
=======

The main [model](model) class is the *FlowBuilder*. A new Flow Definition is specified in its *define* method:

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

The [renderer](renderer) will produce the following flow image:

![task_flow](doc/task_flow.png)

The definition might be transformed using [the BPMN module](bpmn) into:

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

Documentation
=============

Check the documentation of the modules:

 * [Model](model)
 * [Execution](execution)
 * [Renderer](renderer)
 * [BPMN transformer](bpmn)

A detailed documentation on the concepts and the current execution options will soon be available in the github wiki pages.

Roadmap
=======

These features might or might not be implemented:

* Static and dynamic flow definitions
* persistent and non-persistent instances
* Spring Data TokenStore
* Alternative Event Dispatcher
* intent-based REST API orchestration
* Token Migration
* GraphML export and import
* BPMN import

License
=======

brainslug is published under the terms of the LGPL 3.0 License, which is [Apache 2.0 compatible](http://www.apache.org/licenses/GPL-compatibility.html).
See the [LICENSE](LICENSE) file.

![lgplv3](doc/lgplv3.png)

