package com.cloudsuites.framework.webapp.rest;
import com.cloudsuites.framework.modules.property.BuildingServiceImpl;
import com.cloudsuites.framework.modules.property.FloorServiceImpl;
import com.cloudsuites.framework.modules.property.PropertyManagementCompanyServiceImpl;
import com.cloudsuites.framework.modules.property.UnitServiceImpl;
import com.cloudsuites.framework.modules.user.UserServiceImpl;
import com.cloudsuites.framework.services.common.entities.property.*;
import com.cloudsuites.framework.services.common.entities.user.ContactInfo;
import com.cloudsuites.framework.services.common.entities.user.User;
import com.cloudsuites.framework.services.property.BuildingService;
import com.cloudsuites.framework.services.property.FloorService;
import com.cloudsuites.framework.services.property.PropertyManagementCompanyService;
import com.cloudsuites.framework.services.property.UnitService;
import com.cloudsuites.framework.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/api/")
public class TestRestController {

	private final PropertyManagementCompanyService companyService;
	private final BuildingService buildingService;
	private final FloorService floorService;
	private final UnitService unitService;
	private final UserService userService;

	@Autowired
	public TestRestController(
			PropertyManagementCompanyService companyService,
			BuildingService buildingService,
			FloorService floorService,
			UnitService unitService,
			UserService userService) {

		this.companyService = companyService;
		this.buildingService = buildingService;
		this.floorService = floorService;
		this.unitService = unitService;
		this.userService = userService;
	}

	@GetMapping("/createTestData")
	public void createPropertyManagementCompanyWithAssociations() {
		// Create a new PropertyManagementCompany instance
		PropertyManagementCompany company = new PropertyManagementCompany();
		company.setName("Your Company Name");
		company.setWebsite("www.example.com");

		// Create an Address
		Address address = new Address();
		address.setStreetNumber("123");
		address.setStreetName("Main St");
		address.setCity("Your City");
		// Set other address properties as needed
		address.setCreatedBy(createDefaultUser());
		address.setLastModifiedBy(createDefaultUser());

		// Set the Address for the PropertyManagementCompany
		company.setAddress(address);

		// Create a ContactInfo
		ContactInfo contactInfo = new ContactInfo();
		contactInfo.setPhoneNumber("123-456-7890");
		contactInfo.setEmail("info@example.com");

		// Set the ContactInfo for the PropertyManagementCompany
		company.setContactInfo(contactInfo);

		// Create a Building
		Building building = new Building();
		building.setName("Your Building Name");
		building.setTotalFloors(5);
		building.setYearBuilt(2022);
		// Set other building properties as needed
		building.setCreatedBy(createDefaultUser());
		building.setLastModifiedBy(createDefaultUser());

		// Set the Building for the PropertyManagementCompany
		ArrayList<Building> buildings = new ArrayList<>();
		buildings.add(building);
		company.setBuildings(buildings);

		// Create a Floor
		Floor floor = new Floor();
		floor.setBuilding(building);
		floor.setFloorNumber(1);
		// Set other floor properties as needed

		// Add the Floor to the Building's list of floors
		List<Floor> floors = new ArrayList<>();
		floors.add(floor);
		building.setFloors(floors);

		// Create a Unit
		Unit unit = new Unit();
		unit.setFloor(floor);
		unit.setUnitNumber("101");
		unit.setSquareFootage(1000.0);
		// Set other unit properties as needed

		// Add the Unit to the Floor's list of units
		List<Unit> units = new ArrayList<>();
		units.add(unit);
		floor.setUnits(units);

		// Set audit fields
		company.setCreatedBy(createDefaultUser());
		company.setLastModifiedBy(createDefaultUser());
		company.setCreatedAt(LocalDateTime.now());
		company.setLastModifiedAt(LocalDateTime.now());

		// Save the PropertyManagementCompany
		companyService.saveCompany(company);
	}

	@GetMapping("/getCompany")
	public PropertyManagementCompany getPropertyManagementCompanyWithAssociations() {
		// Retrieve a PropertyManagementCompany entry with associated sub-entities
		Long companyId = 1L; // Replace with the actual ID of the company you want to retrieve
		return companyService.getCompanyWithAssociations(companyId);
	}

	private User createDefaultUser() {
		// Create and return a default user as needed
		User user = new User();
		// Set user properties
		user.setUsername("default_user");
		// Set other user properties as needed
		return user;
	}
}