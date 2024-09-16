Here’s the updated documentation reflecting the revised script:

---

# Partition Management Migration

## Overview

This repository contains SQL migration scripts for managing table partitions in a PostgreSQL database. The primary goal is to handle partitioning changes, such as modifying the partitioning interval (e.g., from quarterly to monthly), ensuring that no data is lost and the partitioning scheme remains efficient.

## Partition Management Migration

Optimize data management by partitioning the `Amenity Bookings` table for improved system performance. Key features include:
1. **Time-Based Partitioning**: Organize large booking datasets by time intervals, boosting query performance.
2. **Automated Partition Creation**: Seamless scalability with automatic partition creation.
3. **Historical Data Archiving**: Archive or drop outdated booking data to optimize storage.
4. **Retention Policies**: Retain only necessary data for active operations, reducing storage strain.

<img width="658" alt="image" src="https://github.com/user-attachments/assets/3431e29d-53be-4da5-8741-7a4d8b2bafdb">

## Requirements Summary

- **Create Parent Table for Partitioning:**
    - A parent table `amenity_booking` is created with `PARTITION BY RANGE (start_time)` to enable time-based
      partitioning.
    - Includes columns: `booking_id`, `amenity_id`, `user_id`, `start_time`, `end_time`, `status`, `created_at`,
      and `updated_at`.

- **Logging Partition Creation:**
    - A `partition_audit_log` table tracks partition names and their creation times, ensuring visibility into the
      partitions created.

- **Check for Data in Future Partitions Before Dropping:**
    - The function `partition_has_data(partition_name TEXT, start_date DATE)` verifies if data exists in partitions to
      be dropped. It prevents accidental data loss by returning `TRUE` if data is found.

- **Dropping Future Partitions:**
    - The function `drop_empty_future_partitions(partition_prefix TEXT)` safely drops future partitions after checking
      for data. Partitions are dropped using `CASCADE`, and `partition_audit_log` is updated.

- **Handle Interval Changes and Recreate Partitions:**
    - The
      function `create_or_update_partitions(current_year INTEGER, frequency INTEGER, interval_months INTEGER, partition_prefix TEXT)`
      detects changes in partitioning intervals (monthly, quarterly, yearly). It drops future partitions (if no data is
      found) and recreates them based on the new interval.

- **Partition Management Logic:**
    - The `manage_partitions(partition_prefix TEXT, frequency INTEGER, interval_months INTEGER)` function manages
      partition creation for the current and next year, handles interval changes, and uses `pg_advisory_lock` to prevent
      concurrency issues.

- **Execution Notice:**
    - The script raises a notice to perform partition management during maintenance windows to avoid performance impacts
      during peak times.

- **Version Control and Auditing:**
    - Emphasizes the need for version control in the migration system and tracking changes across environments (DEV,
      STAGING, PROD). Includes logging and alerting mechanisms for auditing partition management.

## Script Summary

### `V1__create_or_manage_partitions.sql`

This script manages the partitioning of a table based on a specified interval. It includes:

1. **`partition_has_data(partition_name TEXT, start_date DATE)`**:
    - Checks if any data exists in the specified partition.
    - Returns a boolean indicating whether data exists.

2. **`drop_empty_future_partitions(partition_prefix TEXT)`**:
    - Drops future partitions based on the partition prefix if no data exists in those partitions.
    - Uses `CASCADE` to drop partitions and updates the `partition_audit_log` table with dropped partitions' details.

3. *
   *`create_partitions_for_period(current_year INTEGER, frequency INTEGER, interval_months INTEGER, partition_prefix TEXT)`
   **:
    - Creates partitions for a given period based on the frequency of partition creation (e.g., quarterly).
    - Partitions are created for each interval defined by the `interval_months` parameter.
    - Named using the `partition_prefix` followed by the start month and year (e.g., `amenity_booking_2024_01` for
      January 2024).
    - Ensures that partitions do not overlap by adjusting the end date of each partition to the start of the next
      partition minus one second.

4. *
   *`create_or_update_partitions(current_year INTEGER, frequency INTEGER, interval_months INTEGER, partition_prefix TEXT)`
   **:
    - Creates or updates partitions based on the new interval.
    - Handles interval changes by dropping old partitions and creating new ones as needed.
    - Logs partition creation or update actions into the `partition_audit_log` table.

5. **`manage_partitions(partition_prefix TEXT, frequency INTEGER, interval_months INTEGER)`**:
    - Manages partition creation and updates for both the current year and the next year.
    - Acquires and releases advisory locks to prevent concurrency issues.
    - Ensures partition management is executed during a maintenance window.

6. **`partition_audit_log` Table**:
    - Automatically created and tracks partition management actions, including creation, updates, and drops.

## Setup

1. **Run the Migration Script:**

   Execute the `V1__create_or_manage_partitions.sql` script to apply partition management logic to your database.

   ```sql
   \i V1__create_or_manage_partitions.sql
   ```

---
