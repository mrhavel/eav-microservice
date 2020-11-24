package de.world.microservice.logic.meta;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;

public interface DomainObjectRepository extends
        CrudRepository<DomainObject, Serializable> {

    @Query(value = "select ID from DomainObject where context = :context", nativeQuery = true)
    List<Long> findIdsByContext(@Param("context") String context);

    @Query(value = "select ID from DomainObject", nativeQuery = true)
    List<Long> findIds();

    List<DomainObject> findAllByContext(String context);
}
