Model
=====

The model contains all classes related to the core concepts of brainslug and their code-wise representation.
This includes the DSL builder.

Concepts
========

Flow Definition
---------------

A flow definition is a directed graph of typed flow nodes. brainslug provides a set of predefined node types
which are interpreted by the execution module. Generally its up to the user to define the meaning of the graph,
but its common to see it as the definition of possible paths / sequence of actions for a desired outcome, e.g.
the steps to prepare a sandwich, or your companies ordering process.

Usage
=====

See the [FlowBuilderTest](src/test/java/brainslug/flow/builder/FlowBuilderTest.java) class
for examples on how to build flow definitions.
