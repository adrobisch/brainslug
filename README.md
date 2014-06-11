![brainslug](doc/src/site/images/brainslug_big.png)

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
* Quartz Scheduler for Async Tasks

Download
========

The current version is available in the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cbrainslug):

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

Example
=======

```java
    new brainslug.flow.model.FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
          .execute(task(id("task")).display("A Task"))
          .execute(task(id("task2")).display("Another Task"))
        .end(event(id("end")));
      }
    };
```

Documentation
=============

Check the [github pages](http://adrobisch.github.io/brainslug/) for the brainslug guide and documentation.

License
=======

brainslug is published under the terms of the Apache 2.0 License.
See the [LICENSE](LICENSE) file.
