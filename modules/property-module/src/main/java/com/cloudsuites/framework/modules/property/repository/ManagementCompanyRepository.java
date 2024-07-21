package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.property.entities.ManagementCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementCompanyRepository extends JpaRepository<ManagementCompany, String> {

}

