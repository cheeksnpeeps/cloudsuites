package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.modules.property.features.repository.BuildingRepository;
import com.cloudsuites.framework.modules.property.features.repository.FloorRepository;
import com.cloudsuites.framework.modules.property.features.repository.UnitRepository;
import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Floor;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.user.entities.Address;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use the test profile to load application-test.yml
@Transactional // Rollback after each test
public class OwnerRestControllerTest {

    String validOwnerId1;
    String validOwnerId2;
    String invalidOwnerId = "invalidOwnerId";
    String invalidUnitId = "invalidUnitId";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private UnitRepository unitRepository;
    private String validUnitId1;
    private String validUnitId2;
    private String validBuildingId1;
    private String validBuildingId2;
    @Autowired
    private FloorRepository floorRepository;

    @BeforeEach
    void setUp() {
        clearDatabase();

        Identity identity1 = createIdentity("test1");
        Identity identity2 = createIdentity("test2");

        Owner owner1 = createOwner(identity1);
        Owner owner2 = createOwner(identity2);

        Building building1 = createBuilding("building1", "city1");
        Building building2 = createBuilding("building2", "city2");

        Unit unit1 = createUnit(building1, owner1);
        Unit unit2 = createUnit(building2, owner2);

        owner1.addUnit(unit1);
        owner2.addUnit(unit2);

        // Save owners, buildings, and units
        ownerRepository.save(owner1);
        ownerRepository.save(owner2);

        this.validBuildingId1 = building1.getBuildingId();
        this.validBuildingId2 = building2.getBuildingId();
        this.validUnitId1 = unit1.getUnitId();
        this.validUnitId2 = unit2.getUnitId();
        this.validOwnerId1 = owner1.getOwnerId();
        this.validOwnerId2 = owner2.getOwnerId();
    }


    private void clearDatabase() {
        ownerRepository.deleteAll();
        buildingRepository.deleteAll();
        unitRepository.deleteAll(); // Ensure units are also cleared
    }

    private Identity createIdentity(String username) {
        Identity identity = new Identity();
        identity.setUsername(username);
        return identity; // Return the created identity
    }

    private Owner createOwner(Identity identity) {
        Owner owner = new Owner();
        owner.setIdentity(identity);
        return ownerRepository.save(owner); // Save and return the owner
    }

    private Building createBuilding(String name, String city) {
        Building building = new Building();
        building.setName(name);
        Address address = new Address();
        address.setCity(city);
        building.setAddress(address);
        return buildingRepository.save(building); // Save and return the building
    }

    private Unit createUnit(Building building, Owner owner) {
        Unit unit = new Unit();
        Floor floor = new Floor();
        floor.setFloorName("floor");
        unit.setFloor(floorRepository.save(floor));
        unit.setBuilding(building);
        //unit.setOwner(owner);
        return unitRepository.save(unit); // Save and return the unit
    }
}