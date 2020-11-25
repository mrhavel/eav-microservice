package de.world.microservice.controllers;

import de.world.microservice.logic.meta.DomainObject;
import de.world.microservice.logic.meta.DomainObjectRepository;
import de.world.microservice.logic.meta.DomainValue;
import de.world.microservice.logic.meta.DomainValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Dieser Microservice ist ein reiner Datenlieferant.
 *
 */
@RestController
@RequestMapping("/val")
public class DomainValueController {

    @Autowired
    private DomainObjectRepository objectRepository;

    @Autowired
    private DomainValueRepository valueRepository;

    /**
     * Holt die Werte aus der Datenbank.
     *
     * @param domainObjectId
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity values(Long domainObjectId) {

        Optional<DomainObject> obj = objectRepository.findById(domainObjectId);
        if (obj.isPresent())
            return new ResponseEntity(obj.get().getValues(), HttpStatus.OK);


        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param domainObjectId
     * @param value
     * @return
     */
    @PostMapping("/{id}")
    @PutMapping("/{id}")
    public ResponseEntity put(@PathVariable("id") Long domainObjectId,
                              @RequestBody DomainValue value) {

        Optional<DomainObject> obj = objectRepository.findById(domainObjectId);
        if (!obj.isPresent())
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        boolean foundEntity = false;
        for (DomainValue val : obj.get().getValues()) {
            if (val.getAttribute().getId().equals(value.getAttribute().getId())) {
                value.setId(val.getId());
                foundEntity = true;
                break;
            }
         }

        value = valueRepository.save(value);

        if (foundEntity) {
            return new ResponseEntity(value, HttpStatus.OK);
        } else {
            return new ResponseEntity(value, HttpStatus.CREATED);
        }

    }

    /**
     *
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity delete(
            @PathVariable("id") Long domainObjectId,
            @RequestBody DomainValue value) {

        Optional<DomainObject> obj = objectRepository.findById(domainObjectId);
        if (!obj.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        for (DomainValue v : obj.get().getValues()) {
            if (v.getAttribute().getId().equals(value.getAttribute().getId())) {
                if (!v.getAttribute().isOptional()) {
                    valueRepository.delete(v);
                    break;
                } else {
                    return new ResponseEntity(HttpStatus.NOT_MODIFIED);
                }
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
