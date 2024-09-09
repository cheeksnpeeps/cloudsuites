Amenity Management Service
==================
Overview
========
This repository provides the **Amenity Management Service** of the CloudSuites platform. The service offers a robust and
highly scalable REST API that enables management of amenities across multiple buildings in real estate applications.
It's designed to handle common business operations like creating, updating, and managing amenities as well as
associating them with buildings in property management systems.

---

### **Key Features**

#### 1. **Comprehensive Amenity Management**

- **Add, Edit, Delete Amenities**: Manage amenities for residential or commercial buildings efficiently.
- **View Amenities by Building**: Filter amenities based on specific building IDs, enabling property managers to have a
  building-centric view.
- **Update Amenity Details**: Modify details of existing amenities, including their name, description, and other
  metadata.

#### 2. **Building Association**

- **Link Amenities to Buildings**: Easily associate amenities with multiple buildings, providing flexibility in managing
  shared amenities across properties.
- **Remove Associations**: Manage building associations by unlinking specific buildings from amenities, allowing dynamic
  adjustments as per changing needs.

#### 3. **Amenity Maintenance Status**

- **Update Maintenance Status**: Update the status of an amenity (e.g., under maintenance, operational) to notify
  tenants and property managers.
- **Maintenance Workflows**: Integrate with maintenance workflows to ensure timely upkeep of amenities.

#### 4. **Security and Access Control**

- **Role-based Access**: Leverages Spring Security for controlling access based on user roles:
    - **Admin Role**: Full control over amenities, including creating, updating, and deleting.
    - **Staff Role**: Ability to manage and update amenities, excluding deletion.
    - **Tenant and Owner Roles**: Restricted access for tenants and owners, allowing them to view amenity details but
      not modify them.

---

### Polymorphic Nature of the Amenity Management Service

The Amenity Management Service leverages polymorphism to manage a variety of different amenities under a common
interface, making it flexible for real estate businesses. Whether you’re dealing with physical resources like swimming
pools or service-based offerings like concierge services, the system uses a standardized approach to handle these
different amenity types seamlessly.

---

### **Standardized Data Transfer**

The Data Transfer Object acts as a universal data container for all types of amenities. Whether an amenity is a physical
resource (like a gym) or a service-based offering (like cleaning), all amenity data is transferred in a standardized way
using this DTO.

- **Unified Representation**: All types of amenities share common fields like `name`, `description`, `status`,
  and `availability`, making it easier to handle them consistently.
- **Extensibility**: Even if new amenity types are added, they can be incorporated into the system without requiring
  major changes to the `AmenityDto`.

This ensures that regardless of the amenity type, it can be transferred through the system in a uniform manner, allowing
for easy integration into different business processes.

#### Example of fields handled in `AmenityDto`:

- `name`: The name of the amenity (e.g., "Swimming Pool" or "Gym")
- `description`: A brief overview of the amenity
- `status`: Whether the amenity is active or under maintenance
- `availability`: The time period or schedule for which the amenity is available

---

### **Seamless Translation of Different Amenity Types**

`AmenityMapping` is a core part of the system’s polymorphic nature, as it translates different types of amenities into a
common format for processing. This allows the system to interact with different types of amenities without needing to
customize the handling for each one.

- **Flexible Mapping**: Regardless of whether the amenity is physical (e.g., a parking space) or service-based, the
  mapping translates the specific attributes into the standard DTO format.
- **Centralized Management**: All amenity types are mapped in a centralized way, making it easier to update or modify
  how amenities are handled without disrupting the overall system.

This mapping ensures that businesses can scale their amenity offerings without needing to make deep changes to how
amenities are processed. New types can be easily integrated as the business grows, making it a highly adaptable system.

---

### **Unified API for Managing Amenities**

The `AmenityRestController` is the gateway through which all amenities are managed via a RESTful API. This controller
provides a unified set of endpoints to create, update, and manage different types of amenities, making it easy for
external applications to interact with the service.

- **Polymorphic Endpoints**: The same API endpoints can be used for all types of amenities, thanks to the standardized
  approach. Whether you're managing a gym, a concierge service, or a parking lot, you’ll interact with them using the
  same methods.
- **Flexible Operations**: CRUD (Create, Read, Update, Delete) operations are simplified because the system doesn’t need
  to differentiate between amenity types. The polymorphic design ensures that different attributes can be handled
  gracefully within the same endpoint structure.

#### Example Endpoints:

- `POST /amenities`: Create a new amenity (regardless of its type)
- `GET /amenities/{id}`: Retrieve information about a specific amenity
- `PUT /amenities/{id}`: Update an amenity’s details (name, availability, status)
- `DELETE /amenities/{id}`: Remove an amenity from the system

---

### **Business Value of the Polymorphic Design**

This polymorphic design brings several business advantages:

- **Scalability**: As businesses expand, adding new types of amenities is straightforward without needing to redesign
  the system.
- **Consistency**: A standardized approach ensures that all amenities, regardless of type, are managed consistently,
  leading to fewer errors and simpler integration with other systems.
- **Efficiency**: Developers can add new features or amenity types without reworking the underlying architecture, saving
  time and reducing costs.

The polymorphic nature of the Amenity Management Service ensures flexibility and scalability, making it a powerful tool
for real estate businesses to efficiently manage a wide range of amenities, from physical assets to service offerings.

### **Business Use Cases**

This service is ideal for property management platforms where managing facilities and amenities is a core functionality.
Some typical use cases include:

- **Property Management Companies**: Managing amenities like gyms, pools, conference rooms, and parking lots for
  multiple residential or commercial buildings.
- **Building Maintenance**: Ensure proper communication to tenants about amenity availability based on their maintenance
  status.
- **Tenant Engagement**: Allow tenants to view the amenities associated with their buildings, improving transparency and
  customer satisfaction.

---