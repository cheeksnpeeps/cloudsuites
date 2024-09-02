package com.cloudsuites.framework.services.amenity.entities.booking;

public class AmenityAlreadyExistsException extends RuntimeException {
    public AmenityAlreadyExistsException(String message) {
        super(message);
    }
}
