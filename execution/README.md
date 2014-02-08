Execution
=========

This module allows to execute brainslug flow definitions.

Concepts
========

Flow Instance
-------------

A flow instance is a single execution of a given flow definition. Depending on the brainslug configuration and flow definition
an instance might be a persistent and long running, or just be executed in-memory.

Flow Token
----------

A flow token is a pointer to a flow node in a flow instance. A flow node might have multiple tokens.
The token includes the information it came from. A flow node needs the correct amount of tokens to be ready
for execution and will produce a defined amount of new flow node after execution.

Token Semantics
===============

Task Node
---------
A Task Node will be executed for every incoming token and produces one token per outgoing edge.

Event Node
----------
An Event Node is triggered by every incoming token and produces one token per outgoing edge.

Choice Node
-----------
A Choice Node will be executed for every incoming token. A token is produced for the first outgoing path
where the predicate is fullfilled.

Merge Node
----------
A Merge Node will be executed for every incoming token. A token is produced for every outgoing edge.

Parallel Node
----------
A Parallel Node will be executed for every incoming token. A token is produced for every outgoing edge.

Join Node
----------
A Parallel Node will only be executed if it has tokens from every incoming edge.
A token is produced for every outgoing edge.

Execution Example
=================

To execute a flow definition, you need to create a brainslug context.
This will create a default context with a service instance:

```java
    BrainslugContext context = new BrainslugContext();
    context.getRegistry().registerService(TestService.class, new TestService());
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

Integration with your code
==========================

In order to connect you code with a brainslug flow definition you may use method call definition:

```java
    new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START))).execute(task(id(TASK)).call(service(TestService.class).method("getString"))).end(event(id(END)));
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
        start(event(id(START))).execute(task(id(TASK)).delegate(Delegate.class))).end(event(id(END)));
      }
    }
```




