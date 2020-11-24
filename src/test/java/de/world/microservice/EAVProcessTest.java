package de.world.microservice;

import de.world.microservice.logic.meta.DomainAttribute;
import de.world.microservice.logic.meta.DomainAttributeRepository;
import de.world.microservice.logic.meta.DomainObjectRepository;
import de.world.microservice.logic.meta.DomainValueRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EAVProcessTest {

    @Autowired
    private DomainAttributeRepository domainAttributeRepository;

    @Autowired
    private DomainObjectRepository domainObjectRepository;

    @Autowired
    private DomainValueRepository domainValueRepository;

    private DomainAttribute getTestAttribute() {
        DomainAttribute attribute = new DomainAttribute();
        attribute.setDescription("Just a Test Attribute");
        attribute.setHandlerClass("TestHandler");
        attribute.setName("TestAttribute");
        attribute.setType("BLA");

        return attribute;
    }

    @Test
    public void attributeTest() {
        DomainAttribute attribute = getTestAttribute();
        DomainAttribute savedAttribute =
                domainAttributeRepository.save(attribute);

        Assertions.assertTrue(savedAttribute.getType().equals(attribute.getType()));
        try {
            domainAttributeRepository.save(attribute);
            Assertions.assertTrue(false);
        } catch (Exception exp) {
            // Expected
        }

        domainAttributeRepository.delete(savedAttribute);
        Assertions.assertTrue(domainAttributeRepository.count() == 0);
    }

}
