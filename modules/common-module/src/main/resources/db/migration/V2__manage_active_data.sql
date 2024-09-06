-- V2__manage_active_data.sql
SELECT create_partitions();

DO
$$
    DECLARE
        cut_off_date         DATE := CURRENT_DATE - INTERVAL '${partitioning.retention_period_months} MONTHS'; -- Placeholder
        partition_record     RECORD;
        partition_name       TEXT;
        partition_year       INTEGER;
        partition_quarter    INTEGER;
        partition_start_date DATE;
        partition_end_date   DATE;
        partition_prefix     TEXT := '${partitioning.prefix}'; -- Placeholder
    BEGIN
        -- Loop through all partitions matching the pattern
        FOR partition_record IN
            SELECT tablename
            FROM pg_tables
            WHERE tablename LIKE CONCAT(partition_prefix, '%')
            LOOP
                partition_name := partition_record.tablename;

                IF partition_name ~ CONCAT(partition_prefix, '(\d{4})_q(\d)') THEN
                    -- Extract year and quarter from partition name
                    partition_year :=
                            (regexp_matches(partition_name, CONCAT(partition_prefix, '(\d{4})_q(\d)')))[1]::INTEGER;
                    partition_quarter :=
                            (regexp_matches(partition_name, CONCAT(partition_prefix, '(\d{4})_q(\d)')))[2]::INTEGER;

                    -- Calculate start and end dates for the partition
                    partition_start_date :=
                            TO_DATE(CONCAT(partition_year, '-', (partition_quarter - 1) * 3 + 1, '-01'), 'YYYY-MM-DD');
                    partition_end_date := partition_start_date + INTERVAL '3 months';

                    -- Drop partitions older than the cut-off date
                    IF partition_end_date < cut_off_date THEN
                        BEGIN
                            EXECUTE format('DROP TABLE IF EXISTS %I', partition_name);
                            RAISE NOTICE 'Dropped partition %', partition_name;
                        EXCEPTION
                            WHEN others THEN
                                RAISE WARNING 'Failed to drop partition %: %', partition_name, SQLERRM;
                        END;
                    END IF;
                END IF;
            END LOOP;

        -- Perform maintenance operations
        BEGIN
            ANALYZE amenity_booking;
            VACUUM FULL amenity_booking;
        EXCEPTION
            WHEN others THEN
                RAISE WARNING 'Failed to perform maintenance on amenity_booking: %', SQLERRM;
        END;
    END
$$;
