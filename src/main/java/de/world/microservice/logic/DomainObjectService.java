package de.world.microservice.logic;

import de.world.microservice.handler.Handler;
import de.world.microservice.logic.meta.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Soll die Schnittstelle zwischen dem Controller und dem EAV System sein
 * <p>
 * Wir lösen hier die Anforderungen des Zusammenbaus, der Persistenz
 */
@Service
@Scope("prototype") // do get it thread safe
public class DomainObjectService {

    @Autowired
    private DomainObjectRepository repository;

    @Autowired
    private DomainValueRepository valueRepository;

    @Autowired
    private DomainAttributeRepository attributeRepository;

    // Handler Loader
    @Autowired
    private ApplicationContext context;

    /**
     * Die Lademethode für die EAV Objekte.
     *
     * @param id
     * @param runProcessors - Nachverarbeitungen finden statt, oder auch nicht
     * @return
     */
    public DomainObject load(
            Optional<Long> id,
            boolean runProcessors
    ) {
        Optional<DomainObject> object = repository.findById(id.get());

        if (!object.isPresent())
            return null;

        if (runProcessors) {
            for (DomainValue val : object.get().getValues()) {
                if (val.getAttribute().getHandlerClass() != null) {
                    Handler handler = (Handler) context.getBean(val.getAttribute().getHandlerClass());
                    val.setValue((String) handler.postProcessor(val.getValue()));
                }
            }
        }

        return object.get();
    }

    /**
     * Wir erstellen eine Vorlage
     * @return
     */
    public DomainObject newObject() {
        DomainObject obj = new DomainObject();

        List<DomainAttribute> attributeList = attributeRepository.findAllByOptional(false);
        for (DomainAttribute attribute : attributeList) {
            DomainValue val = new DomainValue();
            val.setAttribute(attribute);
            obj.getValues().add(val);
        }
        
        return obj;
    }


    /**
     * @TODO Das Löschen sollte nicht unkontrolliert erfolgen. Irgendwas Analog Hibernate Envers
     * Man könnte den Microservice auch spiegeln, als eine Art Papierkorb und dort final Skripte
     * verwenden
     *
     * @param obj - zu löschendes Domänenobjekt
     */
    public void deleteObject(DomainObject obj) {
        repository.delete(obj);
    }

    /**
     * Das Objekt soll so, wie es ist gespeichert werden.
     * Die Attribute kommen immer aus der Datenbank.
     * Die Values müssen u.U. neu aufgebaut werden
     *
     * @param domainObject
     * @return gespeichertes Domänenobjekt
     */
    public DomainObject persist(DomainObject domainObject) {
        List<DomainValue> importantValues = new ArrayList<>();

        if (domainObject.getId() == null || domainObject.getId() == 0) {
            // Kleiner Tausch um eine Entität vom DomainObject aus der Datenbank zu bekommen
            List<DomainValue> tmpList = new LinkedList<>(domainObject.getValues());
            domainObject.getValues().clear();
            domainObject = repository.save(domainObject);
            domainObject.setValues(tmpList);
        }

        for (DomainValue val: domainObject.getValues()) {
            if (val.getId() != null && val.getId() > 0) {       // Update
                importantValues.add(valueRepository.save(val));
                continue;
            }
            importantValues.add(
                addValueToObject(
                        domainObject,
                        val.getAttribute(),
                        val.getValue()
                )
            );
        }

        domainObject.setUpdatedAt(LocalDateTime.now());

        // Refs aktualisieren
        domainObject.setValues(importantValues);
        return repository.save(domainObject);
    }

    /**
     * Die eigentliche Speichermethode für Werte zu einem
     * Domänenobjekt
     *
     * @param obj           - Meta Domänen Object
     * @param attribute     - Kontext
     * @param value         - Wert
     * @return
     */
    public DomainValue addValueToObject(
            DomainObject obj,
            DomainAttribute attribute,
            String value) {
        
        DomainValue domainValue = new DomainValue();
        domainValue.setValue(value);
        domainValue.setAttribute(attribute);

        if (obj.getId() != null && obj.getId() > 0) {
            domainValue.setParent(obj);
        }
        if (domainValue.getAttribute().getHandlerClass() != null) {
            Handler handler = (Handler) context.getBean(domainValue.getAttribute().getHandlerClass());
            domainValue.setValue((String) handler.preProcessor(domainValue.getValue()));
        }

        domainValue = valueRepository.save(domainValue);
        return domainValue;
    }

}
