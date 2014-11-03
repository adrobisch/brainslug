# Flow Definition

The flow definition is the central concept of brainslug. 
A definition defines possible paths / sequence of actions for a desired outcome, e.g. 
the steps to prepare a sandwich, or a ordering process... 

It is constructed using the `FlowBuilder` DSL and is internally represented as a directed graph of typed flow nodes.

brainslug provides a set of predefined node types define the [control flow](control-flow). 
These types might be extended in additional modules like the [BPMN](https://github.com/adrobisch/brainslug/blob/master/bpmn)
support.

In addition to the examples here in the documentation, 
the [FlowBuilderTest](https://github.com/adrobisch/brainslug/blob/master/model/src/test/java/brainslug/flow/builder/FlowBuilderTest.java) 
is a good source for examples on how to build flow definitions.

## Example 

```java
  FlowDefinition helloWorldFlow = new FlowBuilder() {
    @Override
    public void define() {
      start(event(id("start"))).execute(task(id("helloTask"), new SimpleTask() {
        @Override
        public void execute(ExecutionContext context) {
          System.out.println("Hello World!");
        }
      }));
    }
  }.getDefinition();
```

# Execution

To execute a flow definition you need a `BrainslugContext` from the execution module 
which defines all aspects of the execution. Per default it will use `HashMap`-based implementations
for saving execution details like properties and use a `Token`-model to execute a flow.

```java
  //  create brainslug context with defaults
  BrainslugContext context = new BrainslugContext();
  context.addFlowDefinition(helloWorldFlow);

  context.startFlow(helloWorldFlow.getId(), IdUtil.id("start"));
```

## Flow Instance

A flow instance is a single execution of a flow definition. A flow instance may have properties
which are stored in the `PropertyStore` to share data between flow nodes while the execution is not completed.

## Flow Token

A flow token is a pointer to a flow node in a flow instance. A flow node might have multiple tokens. 

Tokens are consumed / deleted when the execution of a single node was successfully, which in turn creates 
new tokens in the nodes which are considered as the next tokens in the flow or path of execution.

Which succeeding nodes get tokens is decided by the corresponding `FlowNodeExecutor` of the node.
The token includes the information which node execution lead to the creation of token (*"where it came from"*).
