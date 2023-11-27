package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.services.common.entities.property.PropertyManagementCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyManagementCompanyRepository extends JpaRepository<PropertyManagementCompany, Long> {
    // Add custom query methods if needed
}

