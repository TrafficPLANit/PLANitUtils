# Release Log

PLANitUtils release log.

## 0.2.0

* Rename everything related to routes to path in the memory model for consistency reasons (PlanitUtils/#1)
* Support for a over arching Graph interface/implementation as a basic building block for networks (Planit/#31)
* add LICENSE.TXT to each repository so it is clearly licensed (planit/#33)
* Add support for predefined modes and physical and usability features (planitxmlgenerator/#2)
* add maximum speed to mode so we have an upperbound regardless of any link(segment(types)) (planit/#39)
* move geometry from link to edge to be consistent with vertex and make it available at a lower level (planitutils/#2)
* internal id is long, xml id is string, and external id is also string now, with their own base implementation class (planitxmlgenerator/#6)

## 0.1.0

* Move repository to new location (www.github.com/trafficplanit/PLANitUtils)
* added functionality for group and token based id generation (#24)
* added functionality for stack walking
* added functionality for logging prefixes for various PLANit components
