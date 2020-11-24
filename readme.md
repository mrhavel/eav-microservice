# Generischer Microservice

Das Domänenobjekt besteht aus drei Entitäten. 


# Forschung und Entwicklung
Dieses Projekt ist Hauptprojekt für die Entwicklung genrischer 
Microservices

# DomänenKlassen 

##  DomainAttribute
Diese Entität dient der Definition des Domänenobjektes, mit dem der 
Microservice arbeitet. Dort können Handler definiert werden

## DomainValue
Beinhaltet den konkreten Wert eines Domänenobjektes. Der Kontext des
Wertes wird durch die Referenz auf das Domänenattribut gesetzt und 
durch das Objekt DomainObject als ein Objekt zusammengefasst.

## DomainObject
Die Klammer für die o.g. Klassen.