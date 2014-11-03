# Flow Node Types

## Task Node

A Task Node will be executed for every incoming token and produces one token per outgoing edge.

## Event Node

An Event Node is triggered by every incoming token and produces one token per outgoing edge.

## Choice Node

A Choice Node will be executed for every incoming token. A token is produced for the first outgoing path
where the predicate is fullfilled.

## Merge Node

A Merge Node will be executed for every incoming token. A token is produced for every outgoing edge.

## Parallel Node

A Parallel Node will be executed for every incoming token. A token is produced for every outgoing edge.

## Join Node

A Parallel Node will only be executed if it has tokens from every incoming edge.
A token is produced for every outgoing edge.
