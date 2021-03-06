package de.world.microservice.logic.meta;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Meta Informationen für jedes Value, was damit
 * dann auch Kontextbehaftet sein soll.
 *
 * Das System selbst wird keine Kontextwechsel unterstützen, weil
 * wir
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

    // Pre und Post Processing der Values über die Handler im entsprechenden Package
    private String handlerClass;

    @Lob
    private String description;

    private boolean optional = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
