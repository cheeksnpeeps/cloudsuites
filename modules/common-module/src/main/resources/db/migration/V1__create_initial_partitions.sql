-- V1__create_initial_partitions.sql

DO
$$
    DECLARE
        year                 INTEGER := EXTRACT(YEAR FROM CURRENT_DATE);
        partition_name       TEXT;
        partition_start_date DATE;
        partition_end_date   DATE;
        partition_prefix     TEXT    := '${partitioning.prefix}';
        frequency            INTEGER := ${partitioning.frequency}::INTEGER;
        archive_path         TEXT    := '${partitioning.archive_storage_path}';
        start_month          INTEGER;
        end_month            INTEGER;
        next_year            INTEGER;
    BEGIN
        -- Loop through quarters of the current year
        FOR i IN 1..frequency
            LOOP
                start_month := (i - 1) * 3 + 1;
                end_month := i * 3 + 1;

                -- Handle month overflow
                next_year := year;
                IF end_month > 12 THEN
                    end_month := 1;
                    next_year := year + 1;
                END IF;

                partition_start_date :=
                        TO_DATE(CONCAT(year, '-', LPAD(start_month::TEXT, 2, '0'), '-01'), 'YYYY-MM-DD');
                partition_end_date :=
                        TO_DATE(CONCAT(next_year, '-', LPAD(end_month::TEXT, 2, '0'), '-01'), 'YYYY-MM-DD');

                partition_name := CONCAT(partition_prefix, year, '_q', i);

                BEGIN
                    EXECUTE format(
                            'CREATE TABLE IF NOT EXISTS %I PARTITION OF amenity_booking ' ||
                            'FOR VALUES FROM (%L) TO (%L)',
                            partition_name,
                            partition_start_date,
                            partition_end_date
                            );
                    RAISE NOTICE 'Created partition %', partition_name;
                EXCEPTION
                    WHEN others THEN
                        RAISE WARNING 'Failed to create partition %: %', partition_name, SQLERRM;
                END;
            END LOOP;

        -- Create partition for the next year to cover the last quarter
        partition_start_date := TO_DATE(CONCAT(year + 1, '-10-01'), 'YYYY-MM-DD');
        partition_end_date := TO_DATE(CONCAT(year + 2, '-01-01'), 'YYYY-MM-DD');

        BEGIN
            EXECUTE format(
                    'CREATE TABLE IF NOT EXISTS %I PARTITION OF amenity_booking ' ||
                    'FOR VALUES FROM (%L) TO (%L)',
                    CONCAT(partition_prefix, year + 1, '_q', frequency),
                    partition_start_date,
                    partition_end_date
                    );
            RAISE NOTICE 'Created partition %', CONCAT(partition_prefix, year + 1, '_q', frequency);
        EXCEPTION
            WHEN others THEN
                RAISE WARNING 'Failed to create partition %: %', CONCAT(partition_prefix, year + 1, '_q', frequency), SQLERRM;
        END;
    END
$$;
