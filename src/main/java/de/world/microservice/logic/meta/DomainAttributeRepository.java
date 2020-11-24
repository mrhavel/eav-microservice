package de.world.microservice.logic.meta;

import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;

public interface DomainAttributeRepository
    extends CrudRepository<DomainAttribute, Serializable> {


    List<DomainAttribute> findAllByOptional(Boolean optional);
}
