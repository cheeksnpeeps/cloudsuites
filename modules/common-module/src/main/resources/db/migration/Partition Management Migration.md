# Partition Management Migration

## Overview

This repository contains SQL migration scripts for managing table partitions in a PostgreSQL database. The primary goal is to handle partitioning changes, such as modifying the partitioning interval (e.g., from quarterly to monthly), ensuring that no data is lost and the partitioning scheme remains efficient.

## Script Summary

### `V1__create_or_manage_partitions.sql`

This script manages the partitioning of a table based on a specified interval. It includes:

1. **`check_future_partition_data(partition_prefix TEXT, start_date DATE)`**:
   - Checks if any data exists in future partitions that would be dropped.
   - Returns a boolean indicating whether future data exists.

2. **`drop_future_partitions(partition_prefix TEXT, start_year INTEGER, start_date DATE)`**:
   - Drops future partitions based on the old interval if no data exists in those partitions.
   - Logs the dropped partitions and their ranges into the `partition_audit_log` table.

3. **`create_or_update_partitions(current_year INTEGER, frequency INTEGER, interval_months INTEGER)`**:
   - Creates or updates partitions based on the new interval.
   - Handles interval changes by dropping old partitions and creating new ones as needed.
   - Logs partition creation or update actions into the `partition_audit_log` table.

4. **`manage_partitions()`**:
   - Manages partition creation and updates for both the current year and the next year.
   - Acquires and releases advisory locks to prevent concurrency issues.
   - Ensures partition management is executed during a maintenance window.

5. **`partition_audit_log` Table**:
   - Automatically created and tracks partition management actions, including creation, updates, and drops.

## Setup

1. **Run the Migration Script:**

   Execute the `V1__create_or_manage_partitions.sql` script to apply partition management logic to your database.

   ```sql
   \i V1__create_or_manage_partitions.sql
