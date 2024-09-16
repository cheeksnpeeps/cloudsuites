### CONCEPT - LATE Partition Management Migration

This diagram illustrates a multi-phase data migration or partitioning process for the `Amenity Bookings` table, the assumption in this scenario is that the `Amenity Bookings` table was never partitioned and now hold a large amount of data (billions of rows).

Summary of the Process:
Phase I: Initial partitioning of the database to store historical data.
Phase II: Decommissioning the old database, making it inactive.
Phase III: Auto-partitioning as new data is added each fiscal quarter.
Phase IV: Archiving old data into Parquet files and removing it from the main system.

<img width="994" alt="image" src="https://github.com/user-attachments/assets/5b780740-c458-43da-a903-016f7f95b506">


### **Phase 1 Approach: Migrating Q2-FY25 Data with CDC for Real-Time Updates**

This Phase 1 approach focuses on migrating the **Q2-FY25 data** (e.g: ~216.67 million rows) from the legacy database to the new partitioned base, ensuring that the system remains operational during and after the transition. We will use a combination of **snapshotting** for the initial migration and **Change Data Capture (CDC)** to track real-time updates for ongoing changes.

---

### **Objective:**
- Migrate **Q2-FY25 data** (~216.67 million rows, ~216.67 GB) to the new partitioned system within a **3-hour maintenance window**.
- Ensure no data is lost during the migration by leveraging **CDC** to capture any ongoing changes or new records.
- Validate the integrity of the migrated data to confirm a successful transition of the active quarter.

---

### **Phase 1: Migration of Q2-FY25 Data**

#### **1. Initial Maintenance Window – Q2-FY25 Snapshot + CDC Activation**
   - **Objective**: Perform the initial migration of the Q2-FY25 data using a snapshot and enable CDC to capture ongoing changes.
   - **Action Plan**:
     - We will take a **snapshot of Q2-FY25 data** (~216.67 million rows) during the first maintenance window. This will copy all records for the current quarter (e.g., from **April 1, 2024**, to **June 30, 2024**) to the new partitioned base.
     - **Activate CDC**: Immediately after the snapshot completes, we will enable CDC to track any changes or new records made to Q2-FY25 (or earlier periods) as the system continues operating.
   
   - **Parallelization**: To ensure the snapshot completes within the **3-hour window**, we will **partition the data by weeks** or months and **process each in parallel**. By running multiple parallel tasks, we can migrate each partition of the quarter’s data efficiently.
     - Example partitioning: Weeks 1-6 of Q2-FY25 or split by April, May, and June.
     - **Throughput Target**: Our goal is to achieve a migration throughput of **72.22 GB/hour** or higher, given the estimated total data size of 216.67 GB.
   
   - **Post-Snapshot CDC**: Once the snapshot is complete, CDC will begin tracking updates made to the Q2-FY25 data, ensuring that all changes are replicated in real-time to the new partitioned system. This ensures the system stays in sync with any inserts, updates, or deletes.

   - **Data Validation**: After the snapshot, we will conduct a quick **row count** and **checksum validation** to ensure that all rows for Q2-FY25 have been successfully migrated. If the initial migration is valid, CDC can take over real-time change tracking.

#### **2. Monitoring CDC for Real-Time Updates**
   - **Objective**: Ensure that CDC remains functional and keeps the new partition in sync with ongoing data changes.
   - **Action Plan**:
     - We will **monitor CDC performance** to ensure that it can handle the data change rates during the current quarter. It is important to watch for any CDC lag and ensure it remains within acceptable thresholds.
     - We will set up **alerts** to notify the team in case of CDC errors or performance degradation.
   
   - **Ongoing Validation**: We will regularly validate the data in the new partition by comparing the source and target databases using **row counts**, **checksums**, or similar validation techniques to ensure CDC is functioning as expected.

#### **3. Final Maintenance Window – Full Validation & Reconciliation**
   - **Objective**: After the initial snapshot and continuous CDC updates, we will perform a final reconciliation to ensure that the Q2-FY25 migration is fully consistent.
   - **Action Plan**:
     - We will perform a **comprehensive validation** of the Q2-FY25 data after a full week or period of CDC updates to ensure that no records have been missed or duplicated.
     - Using **checksums**, **row counts**, and other validation methods, we will compare the old and new partitions to verify consistency.
     - Any discrepancies will be addressed by triggering manual syncs for any out-of-sync data, if needed.

   - **Sign-Off**: Once validated, we will sign off on the successful migration of Q2-FY25 and confirm that the new partition is fully operational.

---

### **Considerations:**

#### **1. CDC Activation Timing**:
   - To avoid potential **race conditions** between the snapshot and CDC, we should ensure the system’s data is quiesced (i.e., in a stable state) just before taking the snapshot. This reduces the risk of conflicts between the snapshot and CDC capturing changes at the same time.
   - We could briefly **quiesce writes** to the database during the snapshot window to ensure consistency, if feasible.

#### **2. Handling High-Change Data Periods**:
   - We will monitor high-change periods in Q2-FY25 (e.g., month-end or quarter-end when data activity might spike) to ensure that CDC can keep up with the volume of changes.
   - If needed, we will adjust system resources allocated to CDC to handle peak loads during these periods.

#### **3. Incremental Snapshot Validation**:
   - After completing each parallel snapshot (if partitioned by weeks or months), we will perform an **incremental validation** to ensure data integrity at each step. This reduces the risk of discovering large discrepancies only at the end of the migration.
   - We will use validation methods such as **checksums** and **row count comparisons** to confirm the accuracy of each partition migrated.

#### **4. Backup Plan for Failures**:
   - We will have a contingency plan in place in case the snapshot or CDC process fails during the migration. This might involve re-running the snapshot for specific partitions or enabling additional CDC runs to catch up on any missed changes.
   - The **old system will remain accessible** during the transition in case a rollback is needed.

---

### **Phase 1 Summary:**

1. **Initial Snapshot of Q2-FY25**: We will migrate all records for the current quarter (~216.67 million rows) using a snapshot within the 3-hour maintenance window.
   - **Parallel processing** by weeks or months will help ensure the migration completes within the window.
2. **CDC for Real-Time Sync**: We will enable CDC immediately after the snapshot to track ongoing changes and ensure the new system stays in sync with any inserts, updates, or deletes.
3. **Validation**: We will perform incremental validation during the snapshot process and ongoing validation while CDC is active to ensure data consistency.
4. **Final Reconciliation**: After the migration is complete, we will conduct a full data reconciliation to validate the success of the migration before signing off on the new system's operation.
