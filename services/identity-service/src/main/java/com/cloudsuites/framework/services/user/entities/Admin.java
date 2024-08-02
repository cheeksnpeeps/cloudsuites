package com.cloudsuites.framework.services.user.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Entity
@Table(name = "admin")
public class Admin {

    private static final Logger logger = LoggerFactory.getLogger(Admin.class);

    @Id
    @Column(name = "admin_id", unique = true, nullable = false)
    private String adminId;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Identity identity;

    @Enumerated(EnumType.STRING)
    private AdminStatus status;

    @Enumerated(EnumType.STRING)
    private AdminRole role;

    @PrePersist
    public void onCreate() {
        this.adminId = IdGenerator.generateULID("ADM-");
        logger.debug("Generated adminId: {}", this.adminId);
        this.status = AdminStatus.ACTIVE;
        this.role = AdminRole.USER;
    }
}
