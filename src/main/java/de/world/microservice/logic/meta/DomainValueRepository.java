package de.world.microservice.logic.meta;


import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface DomainValueRepository extends
        CrudRepository<DomainValue, Serializable> {
}
