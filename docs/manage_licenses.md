# Licenses

![](./images/ak_license_icon128.png)

## License information

Animal keeping and animal use cannot happen without a legal base. In
the AnimalBase we can store this information in *License* entities.

* *name:* a textual identifier, must be given, must be unique
* *filing agency:* the agency that files the permit, free text entry
* *filing number:* the reference number given by the agency
* *responsible person:* link to a responsible person entity
* *deputy:* link to a [person entity](./manage_persons.md)  the deputy of the responsible person
* *start date:* start date of the licensed period
* *end date:* of the licensed period


## Quotas
Quota entries define how many animals may be used in the context of a license.

* *licence:* link to the respective license entry
* *species:* the species entry
* *count:* the number of animals that may be used according to the permit 
