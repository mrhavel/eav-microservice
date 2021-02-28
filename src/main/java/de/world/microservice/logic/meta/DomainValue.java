package de.world.microservice.logic.meta;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

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
    @JsonIgnore         // Keine Kreisreferenzen im Parsing
    private DomainObject parent;

    // Bitte nicht mehr eintragen. Wir speichern hier nur die Werte ein
    // und geben den Werten mit Referenzen einen Kontext
}
