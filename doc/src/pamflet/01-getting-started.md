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
