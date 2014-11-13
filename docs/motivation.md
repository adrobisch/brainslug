# Motivation

> "Do we really need another Workflow-BPM-whatever engines on the JVM? Smells like *'Not invented here'*!"

[Activiti](http://activiti.org), camunda BPM and jBPM might be the most popular on the list, but they all have some kind of bigger legacy and target environment 
they have to deal with and its more difficult to just get to their core. 

For example, Activiti is very focused on BPMN 2.0 and its jBPM 4 roots, jBPM5 is coming from the business rule side, 
BPEL engines make most sense in WS-* environments.

To some degree they share some strong assumptions:

* Relational persistence (with JDBC), execution semantics are often coupled to the DB transaction management
* High initial learning effort required to learn the workflow description language like BPMN, BPEL
* Dynamic expression evaluation, at least to control the flow
* You are living in a container (Tomcat / Spring, Java Enterprise Edition / JBoss)
* XML based descriptions / configurations

*brainslug* aims to provide a **small** workflow library for the JVM without these strings attached.

# Design Goals

A developer using brainslug should be able to:

* no external dependencies in the core parts (model and execution), except for logging  
* understand the execution model by reading the code
* customize all aspects the library (including persistence and transaction management)
* not want to buy commercial (sometimes called *enterprise*) support
