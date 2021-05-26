# PLANitUtils

Utilities for PLANit and interfaces that allow one to provide custom implementations of network entities other than the ones available in the PLANit (core) project.

For more information on PLANit such as the user the manual, licensing, installation, getting started, reference documentation, and more, please visit [https://trafficplanit.github.io/PLANitManual/](https://trafficplanit.github.io/PLANitManual/)

## Maven parent

Projects need to be built from Maven before they can be run. The common maven configuration can be found in the PLANitParentPom project which acts as the parent for this project's pom.xml.

> Make sure you install the PLANitParentPom pom.xml before conducting a maven build (in Eclipse) on this project, otherwise it cannot find the references dependencies, plugins, and other resources.

## Git Branching model

We adopt GitFlow as per https://nvie.com/posts/a-successful-git-branching-model/
