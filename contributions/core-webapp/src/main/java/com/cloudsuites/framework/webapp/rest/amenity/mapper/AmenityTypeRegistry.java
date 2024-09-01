package com.cloudsuites.framework.webapp.rest.amenity.mapper;

import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.features.SwimmingPool;

import java.util.HashMap;
import java.util.Map;

public class AmenityTypeRegistry {

    private static final Map<AmenityType, Class<?>> TYPE_MAP = new HashMap<AmenityType, Class<?>>();

    static {
        // Register types with their corresponding classes
        TYPE_MAP.put(AmenityType.SWIMMING_POOL, SwimmingPool.class);
        // Add other types as needed
    }

    public static Class<?> getClassForType(AmenityType type) {
        return TYPE_MAP.get(type);
    }
}