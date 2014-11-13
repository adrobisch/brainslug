![brainslug](docs/images/brainslug_big.png)

brainslug
=========

brainslug is a control flow abstraction library. It enables you to model your application flow as a graph of typed nodes,
which can be transformed to different representations or be executed within a customisable environment.

<a href="https://travis-ci.org/adrobisch/brainslug"><img src="https://travis-ci.org/adrobisch/brainslug.png?branch=master" /></a>

Download
========

The current version is available in the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cbrainslug)

Hello World
===========

```java
FlowDefinition helloWorldFlow = new FlowBuilder() {
  @Override
  public void define() {
    flowId(id("helloFlow"));

    start(id("start")).execute(task(id("helloTask"), new SimpleTask() {
      @Override
      public void execute(ExecutionContext context) {
        System.out.println("Hello World!");
      }
    }));
  }
}.getDefinition();

//  create brainslug context with defaults
BrainslugContext context = new BrainslugContextBuilder().build();
context.addFlowDefinition(helloWorldFlow);

context.startFlow(helloWorldFlow.getId(), IdUtil.id("start"));
```

Rendered as:

![hello_flow](docs/images/hello_flow.png)

Documentation
=============

Check [Read The Docs](http://brainslug.readthedocs.org) for examples and documentation.

Versioning
==========

Starting with version 1.0.0, brainslug will follow [semantic versioning](http://semver.org). During the 0.x releases, the minor (.x) releases may include breaking changes.

License
=======

brainslug is published under the terms of the Apache 2.0 License.
See the [LICENSE](LICENSE) file.
