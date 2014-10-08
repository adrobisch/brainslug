![brainslug](doc/src/site/images/brainslug_big.png)

brainslug
=========

brainslug is a control flow abstraction library. It enables you to model your application flow as a graph of typed nodes,
which can be transformed to different representations or be executed within a customisable environment.

<a href="https://travis-ci.org/adrobisch/brainslug"><img src="https://travis-ci.org/adrobisch/brainslug.png?branch=master" /></a>

Features
========

* Builder DSL for flow definitions
* [BPMN 2.0 XML](http://www.omg.org/spec/BPMN/2.0/) export using the [Activiti](https://github.com/Activiti/Activiti) Model
* Flow Renderer based on the BPMN symbols
* Quartz Scheduler for Async Tasks

Download
========

The current version is available in the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cbrainslug)

Documentation
=============

Check the [github pages](http://adrobisch.github.io/brainslug/) for examples and documentation.

Versioning
==========

Starting with version 1.0.0, brainslug will follow [semantic versioning](http://semver.org). During the 0.x releases, the minor (.x) releases may include breaking changes.

License
=======

brainslug is published under the terms of the Apache 2.0 License.
See the [LICENSE](LICENSE) file.
