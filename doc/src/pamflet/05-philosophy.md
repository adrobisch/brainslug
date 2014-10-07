# Philosophy

> "Do we really need another Workflow-BPM-whatever engines on the JVM? Smells like 'Not invented here'!"

Activiti, camunda BPM and jBPM might be the most popular on the list, but they all have some kind bigger legacy and target environment 
they have to deal with and its more difficult to just get to their core, e.g. to understand there internal execution model. 

For example, Activiti is very focused on BPMN 2.0 and its jBPM 4 roots, jBPM5 is coming from the business rule side, 
BPEL engines make most sense in WS-* environments.

To some degree they share some strong assumptions:

* Relational persistence (with JDBC), execution semantics are often strongly coupled to the DB transaction management
* high initial learning effort required to learn the workflow description language like BPMN, BPEL, ... 
the BPMN 2.0 specification is a several hundred pages big
* Dynamic expression evaluation, at least to control the flow
* You are living in a container (Tomcat / Spring, Java Enterprise Edition)
* XML based descriptions / configurations

Those points make them look heavy-weight and inflexible. 
*brainslug* aims to provide a small workflow library in Java without these strings attached.
