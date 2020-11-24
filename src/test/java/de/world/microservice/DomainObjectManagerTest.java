package de.world.microservice;

import de.world.microservice.logic.DomainObjectService;
import de.world.microservice.logic.meta.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wildfly.common.Assert;

@SpringBootTest
public class DomainObjectManagerTest {

    @Autowired
    private DomainObjectService service;

    @Autowired
    private DomainObjectRepository repository;

    @Autowired
    private DomainValueRepository valueRepository;

    @Autowired
    private DomainAttributeRepository attributeRepository;

    /**
     * Minimales Objekt erstellen
     */
    @Test
    public void aaa_testeDomainObjectCreation() {
        DomainObject obj = service.newObject();
        obj.setName("TestObjekt");
        obj.setContext("TEST");
        obj = service.persist(obj);
        Assertions.assertTrue(obj != null);
        Assertions.assertTrue(obj.getId() != null && obj.getId() > 0);
        Assertions.assertTrue(obj.getValues() != null && obj.getValues().size() > 0);
    }


    /**
     * Test des kaskadierten Löschens
     */
    @Test
    public void zzz_leereTabelle() {
        for (DomainObject obj : repository.findAll())
            service.deleteObject(obj);

        Assertions.assertTrue(repository.count() == 0);
        Assertions.assertTrue(attributeRepository.count() > 0);
        Assertions.assertTrue(valueRepository.count() == 0);

    }
}
