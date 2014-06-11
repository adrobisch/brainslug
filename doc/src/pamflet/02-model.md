# Definitions

## Flow Definition

A flow definition is a directed graph of typed flow nodes. brainslug provides a set of predefined node types
which are interpreted by the execution module. Generally its up to the user to define the meaning of the graph,
but its common to see it as the definition of possible paths / sequence of actions for a desired outcome, e.g.
the steps to prepare a sandwich, or a ordering process.

## Usage

See the [FlowBuilderTest](https://github.com/adrobisch/brainslug/blob/master/model/src/test/java/brainslug/flow/builder/FlowBuilderTest.java) class
for examples on how to build flow definitions.
