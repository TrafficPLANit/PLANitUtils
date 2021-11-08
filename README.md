# PLANitUtils

Utilities for PLANit and interfaces that allow one to provide custom implementations of network entities other than the ones available in the PLANit (core) project.

For more information on PLANit such as the user the manual, licensing, installation, getting started, reference documentation, and more, please visit [www.goPLANit.org](http://www.goplanit.org)

## Development

### Maven build

PLANit utils has the following PLANit specific dependencies (See pom.xml):

* planit-parentpom

Dependencies will be automatically downloaded from the PLANit website, (www.repository.goplanit.org)[http://repository.goplanit.org], or alternatively can be checked-out locally for local development. The shared PLANit Maven configuration can be found in planit-parent-pom which is defined as the parent pom of each PLANit repository.

> When developing on multiple PLANit projects locally, including the parent-pom; make sure you install the PLANitParentPom pom.xml before conducting a Maven build (in for example Eclipse), otherwise it resorts to the online repository rather then the local one.

### Maven deploy

Distribution management is setup via the parent pom such that Maven deploys this project to the PLANit online repository (also specified in the parent pom). To enable deployment ensure that you setup your credentials correctly in your settings.xml as otherwise the deployment will fail.

### Git Branching model

We adopt GitFlow as per https://nvie.com/posts/a-successful-git-branching-model/
