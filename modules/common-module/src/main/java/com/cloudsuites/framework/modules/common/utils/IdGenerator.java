package com.cloudsuites.framework.modules.common.utils;

import de.huxhorn.sulky.ulid.ULID;

public class IdGenerator {

    private static final ULID ulid = new ULID();

    IdGenerator() {
    }

    public static String generateULID(String prefix) {
        return prefix + ulid.nextULID(); // Add prefix to ULID
    }
}
