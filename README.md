![brainslug](doc/brainslug_big.png)

brainslug
=========

brainslug is a control flow abstraction library. It enables you to model your application flow as a graph of typed nodes,
which can be transformed to different representations or be executed within a customisable environment.

<a href="https://travis-ci.org/adrobisch/brainslug"><img src="https://travis-ci.org/adrobisch/brainslug.png?branch=master" /></a>

Features
========

* Builder DSL for flow definitions
* [BPMN 2.0 XML](http://www.omg.org/spec/BPMN/2.0/) export using the [Activiti](https://github.com/Activiti/Activiti) Model
* Flow Renderer based on the BPMN symbols

Download
========

The current version `0.7` is available in the maven central repository:

```xml
  <dependencies>
  ...
    <dependency>
      <groupId>de.androbit</groupId>
      <artifactId>brainslug-model</artifactId>
      <version>0.7</version>
    </dependency>
    <dependency>
      <groupId>de.androbit</groupId>
      <artifactId>brainslug-execution</artifactId>
      <version>0.7</version>
    </dependency>
  ...
  </dependencies>
```

Example
=======

The main [model](model) class is `brainslug.flow.model.FlowBuilder`. A new Flow Definition is specified in its `define` method:

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

