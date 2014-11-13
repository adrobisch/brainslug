# Flow Definition

The flow definition is the central concept of brainslug. 
A definition defines possible paths / sequence of actions for a desired outcome, e.g. 
the steps for an ordering process, call to external systems etc. ...

A flow definition is constructed using the `FlowBuilder` DSL and is internally represented as a directed graph of typed flow nodes.

## Example

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
```

brainslug provides a set of predefined node types to define the [control flow](control-flow).
These types might be extended in additional modules like the [BPMN](https://github.com/adrobisch/brainslug/blob/master/bpmn)
support.

In addition to the examples here in the documentation,
the [FlowBuilderTest](https://github.com/adrobisch/brainslug/blob/master/model/src/test/java/brainslug/flow/builder/FlowBuilderTest.java)
is a good source for examples on how to build flow definitions.

# Execution

To execute a flow definition you need to create `BrainslugContext` which defines all aspects of the execution.
The `DefaultBrainslugContext` will use `HashMap` and `List`-based implementations for all aspects related to storing flow instance information.
It is possible to add durable persistence by using the [JPA](jpa-support)-module or by writing custom stores.

## Flow Instance

A flow instance is a single execution of a flow definition. A flow instance may have properties
which are stored in the `PropertyStore` to share data between flow nodes while the execution is not completed.

## Flow Token

A flow token is a pointer to a flow node in a flow instance. A flow node might have multiple tokens.

Tokens are consumed / deleted when the execution of a single node was successfully, which in turn creates
new tokens in the nodes which are considered as the next tokens in the flow or path of execution.

Which succeeding nodes get tokens is decided by the corresponding `FlowNodeExecutor` of the node.
The token includes the information which node execution lead to the creation of token (*"where it came from"*).

## Example

```java
//  create brainslug context with defaults
BrainslugContext context = new BrainslugContextBuilder().build();
context.addFlowDefinition(helloWorldFlow);

context.startFlow(helloWorldFlow.getId(), IdUtil.id("start"));
```

will execute the `helloFlow` and thus print `Hello World!` on the console.
