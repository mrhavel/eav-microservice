package de.world.microservice.logic.meta;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Meta Informationen für jedes Value, was damit
 * dann auch Kontextbehaftet sein sollte
 */
@Entity
@Data
public class DomainAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Ist als technischer Name gedacht
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String type;

    // Pre und Post Processing der Values
    private String handlerClass;

    @Lob
    private String description;

    private boolean optional = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
