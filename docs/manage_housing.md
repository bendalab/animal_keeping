# Manage animal housing

![](./images/ak_housing_icon128.png)

## Housing information
Animals are kept in housing units which are modeled as a hierarchical
tree. That is, an aquarium is placed in a rack, which is placed in
room, which is part of the animal keeping facility... Each housing unit entry contains the following information:

* *name:* a textual identifier that must be given, unique
* *description:* textual description of the housing unit
* *dimensions:*  size of unit
* *parent:* to form the tree structure, a housing unit links to a parent unit
* *type:* link to a user-defined type of housing unit (animal keeping facility, room, rack, cage, ...)
