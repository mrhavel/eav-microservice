package de.world.microservice.logic.meta;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class DomainValue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String value;

    @OneToOne
    private DomainAttribute attribute;

    @OneToOne
    private DomainObject parent;
}
