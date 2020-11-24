package de.world.microservice.controllers;

import de.world.microservice.logic.meta.DomainObject;
import de.world.microservice.logic.meta.DomainObjectRepository;
import de.world.microservice.logic.meta.DomainValueRepository;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 *
 */
@RestController
@RequestMapping("/obj")
public class DomainObjectController {

    @Autowired
    private DomainObjectRepository objectRepository;

    @Autowired
    private DomainValueRepository valueRepository;

    /**
     *
     * @param context - lil´ bumper for all the ids in the database.
     * @return
     */
    @GetMapping
    @ApiResponse(description = "Context ")
    public ResponseEntity getIds(@RequestParam Optional<String> context) {

        /**
         * @TODO Es fehlt hier noch eine Sortierungsmöglichkeitn
         * => Pageable und Sortable mal nutzen
         */

        if (context.isPresent()) {
            return new ResponseEntity(objectRepository.findIdsByContext(context.get()), HttpStatus.OK);
        }

        // @TODO Die ID muss geholt werden
        return new ResponseEntity(objectRepository.findIds(), HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiResponse(description = "Holt das Objekt, anhand der übergebenen ID")
    public ResponseEntity getById(@PathVariable("id") Long id) {
        return new ResponseEntity(
                objectRepository.findById(id),
                HttpStatus.OK);
    }


    @PostMapping
    @ApiResponse(description = "Erstellt ein Objekt.")
    public ResponseEntity create(@RequestBody DomainObject value) {
        DomainObject object = objectRepository.save(value);
        return new ResponseEntity(object.getId(), HttpStatus.CREATED);
    }



}
