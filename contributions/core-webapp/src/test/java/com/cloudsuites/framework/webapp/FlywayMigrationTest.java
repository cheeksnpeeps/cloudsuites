package com.cloudsuites.framework.webapp;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@SpringBootTest(classes = CloudsuitesCoreApplication.class)
@ActiveProfiles("local")
public class FlywayMigrationTest {

    @Autowired
    private DataSource dataSource;

    private Flyway flyway;

    @BeforeEach
    public void setUp() {
        // Initialize Flyway and apply migrations
        flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testV1CreateInitialPartitions() throws SQLException {
        // Expected partitions for the current year and next year
        String[] expectedPartitions = {
                "amenity_booking_2024_q1",
                "amenity_booking_2024_q2",
                "amenity_booking_2024_q3",
                "amenity_booking_2024_q4",
                "amenity_booking_2025_q1"
        };

        try (Connection conn = dataSource.getConnection()) {
            for (String partition : expectedPartitions) {
                ResultSet rs = conn.getMetaData().getTables(null, null, partition, null);
                Assertions.assertTrue(rs.next(), "Partition " + partition + " should exist.");
            }
        }
    }

    @Test
    public void testV2ManageActiveData() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Insert sample data into a specific partition
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS amenity_booking_2024_q1 (booking_id TEXT PRIMARY KEY, amenity_id TEXT, user_id TEXT, start_time TIMESTAMP, end_time TIMESTAMP)");
            conn.createStatement().execute("INSERT INTO amenity_booking_2024_q1 (booking_id, amenity_id, user_id, start_time, end_time) VALUES ('BK-001', 'amenity1', 'user1', '2024-01-10 10:00:00', '2024-01-10 12:00:00')");

            // Run the V2 script if needed

            // Verify that old partitions are dropped (if applicable)
            ResultSet rs = conn.getMetaData().getTables(null, null, "amenity_booking_2023_q4", null);
            Assertions.assertFalse(rs.next(), "Old partition amenity_booking_2023_q4 should be dropped.");
        }
    }

    @Test
    public void testV3ArchiveOldPartitions() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Insert sample data into a specific partition
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS amenity_booking_2023_q4 (booking_id TEXT PRIMARY KEY, amenity_id TEXT, user_id TEXT, start_time TIMESTAMP, end_time TIMESTAMP)");
            conn.createStatement().execute("INSERT INTO amenity_booking_2023_q4 (booking_id, amenity_id, user_id, start_time, end_time) VALUES ('BK-002', 'amenity2', 'user2', '2023-10-10 10:00:00', '2023-10-10 12:00:00')");

            // Run the V3 script if needed

            // Verify that the partition is archived and dropped
            ResultSet rs = conn.getMetaData().getTables(null, null, "amenity_booking_2023_q4", null);
            Assertions.assertFalse(rs.next(), "Old partition amenity_booking_2023_q4 should be archived and dropped.");
        }
    }
}
