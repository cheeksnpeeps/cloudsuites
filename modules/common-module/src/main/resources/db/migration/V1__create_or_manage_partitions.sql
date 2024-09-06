-- Create the table if it does not already exist
DO
$$
    BEGIN
        IF EXISTS (SELECT 1 FROM pg_class WHERE relname = 'amenity_booking') THEN
            EXECUTE 'ALTER TABLE amenity_booking RENAME TO amenity_booking_old';

            EXECUTE '
        CREATE TABLE amenity_booking (
            booking_id TEXT,
            amenity_id TEXT NOT NULL,
            user_id TEXT NOT NULL,
            start_time TIMESTAMP NOT NULL,
            end_time TIMESTAMP NOT NULL,
            status TEXT NOT NULL,
            created_at TIMESTAMP,
            updated_at TIMESTAMP,
            PRIMARY KEY (booking_id, amenity_id, user_id, start_time),
            FOREIGN KEY (amenity_id) REFERENCES amenity(amenity_id)
        ) PARTITION BY RANGE (start_time)';

            EXECUTE 'DROP TABLE IF EXISTS amenity_booking_old';
        ELSE
            EXECUTE '
        CREATE TABLE amenity_booking (
            booking_id TEXT,
            amenity_id TEXT NOT NULL,
            user_id TEXT NOT NULL,
            start_time TIMESTAMP NOT NULL,
            end_time TIMESTAMP NOT NULL,
            status TEXT NOT NULL,
            created_at TIMESTAMP,
            updated_at TIMESTAMP,
            PRIMARY KEY (booking_id, amenity_id, user_id, start_time),
            FOREIGN KEY (amenity_id) REFERENCES amenity(amenity_id)
        ) PARTITION BY RANGE (start_time)';
        END IF;
    END
$$;

-- Function to check if a partition has data
CREATE OR REPLACE FUNCTION partition_has_data(partition_name TEXT, start_date DATE)
    RETURNS BOOLEAN AS
$$
DECLARE
    data_exists BOOLEAN := FALSE;
BEGIN
    EXECUTE format(
            'SELECT EXISTS (
                SELECT 1
                FROM %I
                WHERE start_time >= $1
            )',
            partition_name
            ) INTO data_exists USING start_date;

    RETURN data_exists;
END
$$ LANGUAGE plpgsql;

-- Function to drop empty future partitions
CREATE OR REPLACE FUNCTION drop_empty_future_partitions(partition_prefix TEXT)
    RETURNS VOID AS
$$
DECLARE
    partition_name       TEXT;
    partition_start_date DATE;
    current_date         DATE := CURRENT_DATE; -- Use current_date in the function
BEGIN
    -- Loop through future partitions based on their naming pattern
    FOR partition_name IN
        SELECT relname
        FROM pg_class
        WHERE relname LIKE partition_prefix || '%'
          AND relname > partition_prefix || to_char(current_date, 'YYYY_MM')
        LOOP
            -- Extract the start date from the partition name
            BEGIN
                partition_start_date := to_date(split_part(partition_name, '_', 2), 'YYYY_MM');
            EXCEPTION
                WHEN others THEN
                    RAISE WARNING 'Skipping invalid partition name %', partition_name;
                    CONTINUE;
            END;

            -- Check if the partition is empty
            IF NOT partition_has_data(partition_name, partition_start_date) THEN
                -- Drop the empty partition
                EXECUTE format('DROP TABLE IF EXISTS %I CASCADE', partition_name);
                RAISE NOTICE 'Dropped empty partition %', partition_name;
            ELSE
                RAISE NOTICE 'Partition % has data and will not be dropped', partition_name;
            END IF;
        END LOOP;
END
$$ LANGUAGE plpgsql;

-- Function to create partitions based on a new interval
CREATE OR REPLACE FUNCTION create_partitions_for_period(
    current_year INTEGER,
    frequency INTEGER,
    interval_months INTEGER,
    partition_prefix TEXT
)
    RETURNS VOID AS
$$
DECLARE
    start_date           DATE;
    end_date             DATE;
    partition_name       TEXT;
    partition_start_date DATE;
    partition_end_date   DATE;
BEGIN
    start_date := DATE_TRUNC('year', (current_year || '-01-01')::DATE);
    end_date := start_date + INTERVAL '1 year' - INTERVAL '1 day';

    -- Example logic to create partitions (e.g., quarterly partitions)
    FOR i IN 0..((12 / interval_months) - 1)
        LOOP
            partition_start_date := start_date + INTERVAL '1 month' * (i * interval_months);
            partition_end_date := partition_start_date + INTERVAL '1 month' * interval_months;

            partition_name := format('%s_%s', partition_prefix, to_char(partition_start_date, 'YYYY_MM'));

            EXECUTE format(
                    'CREATE TABLE IF NOT EXISTS %I PARTITION OF amenity_booking FOR VALUES FROM (%L) TO (%L)',
                    partition_name,
                    partition_start_date::TEXT,
                    partition_end_date::TEXT
                    );
            RAISE NOTICE 'Created partition %', partition_name;
        END LOOP;
END
$$ LANGUAGE plpgsql;

-- Function to handle interval change and recreate partitions
CREATE OR REPLACE FUNCTION create_or_update_partitions(
    current_year INTEGER,
    frequency INTEGER,
    interval_months INTEGER,
    partition_prefix TEXT
)
    RETURNS VOID AS
$$
DECLARE
    start_date              DATE;
    current_interval_months INTEGER;
BEGIN
    start_date := DATE_TRUNC('year', (current_year || '-01-01')::DATE);

    -- Get the current interval to detect any changes in the partitioning logic
    current_interval_months := CASE
                                   WHEN EXISTS (SELECT 1
                                                FROM pg_class
                                                WHERE relname LIKE format('%s%%', partition_prefix)
                                                  AND relname LIKE '%_01') THEN 1
                                   ELSE 12 -- Default to yearly if no partitions exist
        END;

    -- Drop empty future partitions if the interval has changed
    IF current_interval_months <> interval_months THEN
        RAISE NOTICE 'Interval has changed from % months to % months. Dropping empty future partitions.', current_interval_months, interval_months;
        PERFORM drop_empty_future_partitions(partition_prefix);
    END IF;

    -- Proceed with partition creation based on the new interval
    PERFORM create_partitions_for_period(current_year, frequency, interval_months, partition_prefix);
END
$$ LANGUAGE plpgsql;

-- Function to manage partition creation, with the interval change handling logic
CREATE OR REPLACE FUNCTION manage_partitions(
    partition_prefix TEXT,
    frequency INTEGER,
    interval_months INTEGER
)
    RETURNS VOID AS
$$
DECLARE
    current_year INTEGER := EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER;
    next_year    INTEGER := current_year + 1;
BEGIN
    -- Enforce this partitioning update to be done during a maintenance window
    RAISE NOTICE 'Ensure that partition management is executed during a maintenance window to avoid peak times.';

    -- Acquire an advisory lock to prevent concurrency issues
    PERFORM pg_advisory_lock(hashtext(partition_prefix));

    -- Create or update partitions for the current year
    PERFORM create_or_update_partitions(current_year, frequency, interval_months, partition_prefix);

    -- Create or update partitions for the next year
    PERFORM create_or_update_partitions(next_year, frequency, interval_months, partition_prefix);

    -- Release the advisory lock
    PERFORM pg_advisory_unlock(hashtext(partition_prefix));
END
$$ LANGUAGE plpgsql;

-- Automatically trigger partition management when this migration is run
SELECT manage_partitions('amenity_booking_', 4, 3);

-- Create the partition_audit_log table if it does not already exist
CREATE TABLE IF NOT EXISTS partition_audit_log
(
    action         TEXT,
    partition_name TEXT,
    start_date     DATE,
    end_date       DATE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
