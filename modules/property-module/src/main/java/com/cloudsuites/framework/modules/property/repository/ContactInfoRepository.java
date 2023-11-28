package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.common.entities.user.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {
    // Add custom query methods if needed
}
