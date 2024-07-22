package com.cloudsuites.framework.services.property.personas.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.user.entities.Identity;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
@Entity
@Table(name = "owner")
public class Owner {

    private static final Logger logger = LoggerFactory.getLogger(Owner.class);

    @Id
    @Column(name = "owner_id", unique = true, nullable = false)
    private String ownerId;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unit> units;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Identity identity;

    @PrePersist
    protected void onCreate() {
        this.ownerId = IdGenerator.generateULID("OW-");
        logger.debug("Generated ownerId: {}", this.ownerId);
    }
}
