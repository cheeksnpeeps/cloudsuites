package com.cloudsuites.framework.modules.property.features.repository;

import com.cloudsuites.framework.services.user.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}

