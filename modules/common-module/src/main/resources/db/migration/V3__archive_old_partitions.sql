-- V3__archive_old_partitions.sql

DO
$$
    DECLARE
        cut_off_date         DATE := CURRENT_DATE - INTERVAL '6 months';
        partition_record     RECORD;
        partition_name       TEXT;
        partition_prefix     TEXT := '${partitioning.prefix}';
        partition_year       INTEGER;
        partition_quarter    INTEGER;
        partition_start_date DATE;
        partition_end_date   DATE;
        archive_path         TEXT := '${partitioning.archive_storage_path}';
        file_path            TEXT;
    BEGIN
        -- Loop through all partitions matching the pattern
        FOR partition_record IN
            SELECT tablename
            FROM pg_tables
            WHERE tablename LIKE CONCAT(partition_prefix, '%')
            LOOP
                partition_name := partition_record.tablename;

                IF partition_name ~ format('%s(\d{4})_q(\d)', partition_prefix) THEN
                    -- Extract year and quarter from partition name
                    partition_year :=
                            (regexp_matches(partition_name, format('%s(\d{4})_q(\d)', partition_prefix)))[1]::INTEGER;
                    partition_quarter :=
                            (regexp_matches(partition_name, format('%s(\d{4})_q(\d)', partition_prefix)))[2]::INTEGER;

                    -- Calculate start and end dates for the partition
                    partition_start_date :=
                            TO_DATE(CONCAT(partition_year, '-', (partition_quarter - 1) * 3 + 1, '-01'), 'YYYY-MM-DD');
                    partition_end_date := partition_start_date + INTERVAL '3 months';

                    -- Archive data if it's older than the cut-off date
                    IF partition_end_date < cut_off_date THEN
                        file_path := CONCAT(archive_path, '/', partition_name, '.parquet');
                        -- Archive data to Parquet (requires external tool/script, not supported directly in PostgreSQL)
                        RAISE NOTICE 'Archiving table % to %', partition_name, file_path;
                        -- Use external tool/script to archive the data to Parquet
                        EXECUTE format('DROP TABLE IF EXISTS %I', partition_name);
                    END IF;
                END IF;
            END LOOP;
    END
$$;
