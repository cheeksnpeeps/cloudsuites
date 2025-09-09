--
-- PostgreSQL database dump
--

\restrict yqahpzedzkQMaXjb0EZOiNwMyDFtuG7i6L5yMJhw0o4XohzI2SFOeVd3wGvQTIG

-- Dumped from database version 17.6 (Debian 17.6-1.pgdg13+1)
-- Dumped by pg_dump version 17.6 (Debian 17.6-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: address; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.address (
    latitude double precision,
    longitude double precision,
    address_id character varying(255) NOT NULL,
    address_line_2 character varying(255),
    apt_number character varying(255),
    city character varying(255),
    country character varying(255),
    postal_code character varying(255),
    state_province_region character varying(255),
    street_name character varying(255),
    street_number character varying(255)
);


--
-- Name: admin; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.admin (
    admin_id character varying(255) NOT NULL,
    role character varying(255),
    status character varying(255),
    user_id character varying(255),
    CONSTRAINT admin_role_check CHECK (((role)::text = ANY ((ARRAY['SUPER_ADMIN'::character varying, 'BUILDINGS_ADMIN'::character varying, 'BUSINESS_ADMIN'::character varying, 'USER'::character varying, 'THIRD_PARTY_ADMIN'::character varying, 'DELETED'::character varying, 'ALL_ADMIN'::character varying])::text[]))),
    CONSTRAINT admin_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


--
-- Name: aerobics_room; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.aerobics_room (
    booking_fee numeric(38,2),
    has_ac boolean,
    has_equipment boolean,
    has_sound_system boolean,
    max_class_capacity integer,
    mirror_walls boolean,
    amenity_id character varying(255) NOT NULL,
    class_schedule character varying(255),
    floor_type character varying(255)
);


--
-- Name: amenity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.amenity (
    advance_booking_period integer,
    booking_duration_limit integer,
    capacity integer,
    is_active boolean NOT NULL,
    is_booking_required boolean NOT NULL,
    is_paid_service boolean NOT NULL,
    is_waiver_required boolean,
    is_waiver_signed boolean,
    max_booking_overlap integer,
    max_bookings_per_tenant integer,
    maximum_booking_duration integer,
    min_booking_duration integer,
    amenity_id character varying(255) NOT NULL,
    booking_limit_period character varying(255),
    description character varying(255),
    image_url character varying(255),
    location character varying(255),
    maintenance_status character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    rules character varying(255),
    type character varying(255) NOT NULL,
    video_url character varying(255),
    waiver text,
    CONSTRAINT amenity_booking_limit_period_check CHECK (((booking_limit_period)::text = ANY ((ARRAY['DAILY'::character varying, 'WEEKLY'::character varying, 'MONTHLY'::character varying])::text[]))),
    CONSTRAINT amenity_maintenance_status_check CHECK (((maintenance_status)::text = ANY ((ARRAY['OPERATIONAL'::character varying, 'UNDER_MAINTENANCE'::character varying, 'OUT_OF_SERVICE'::character varying, 'SCHEDULED_FOR_MAINTENANCE'::character varying, 'CLOSED'::character varying])::text[]))),
    CONSTRAINT amenity_type_check CHECK (((type)::text = ANY ((ARRAY['BARBECUE_AREA'::character varying, 'AEROBICS_ROOM'::character varying, 'SWIMMING_POOL'::character varying, 'TENNIS_COURT'::character varying, 'PARTY_ROOM'::character varying, 'GYM'::character varying, 'THEATER'::character varying, 'MASSAGE_ROOM'::character varying, 'WINE_TASTING_ROOM'::character varying, 'GUEST_SUITE'::character varying, 'BILLIARD_ROOM'::character varying, 'GAMES_ROOM'::character varying, 'GOLF_SIMULATOR'::character varying, 'BOWLING_ALLEY'::character varying, 'LIBRARY'::character varying, 'YOGA_STUDIO'::character varying, 'ELEVATOR'::character varying, 'OTHER'::character varying])::text[])))
);


--
-- Name: amenity_booking; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.amenity_booking (
    created_at timestamp(6) without time zone,
    end_time timestamp(6) without time zone NOT NULL,
    start_time timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone,
    amenity_id character varying(255) NOT NULL,
    booking_id character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    CONSTRAINT amenity_booking_status_check CHECK (((status)::text = ANY ((ARRAY['REQUESTED'::character varying, 'DECLINED'::character varying, 'CANCELLED'::character varying, 'APPROVED'::character varying, 'PENDING'::character varying])::text[])))
);


--
-- Name: amenity_building; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.amenity_building (
    id bigint NOT NULL,
    amenity_id character varying(255),
    building_id character varying(255)
);


--
-- Name: amenity_building_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.amenity_building ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.amenity_building_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: amenity_custom_rules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.amenity_custom_rules (
    amenity_id character varying(255) NOT NULL,
    rule character varying(255)
);


--
-- Name: amenity_image_gallery; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.amenity_image_gallery (
    amenity_id character varying(255) NOT NULL,
    image_url character varying(255)
);


--
-- Name: barbeque_area; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.barbeque_area (
    has_fire_pit boolean,
    has_lighting boolean,
    has_reservation_system boolean,
    has_seating boolean,
    has_water_source boolean,
    is_covered boolean,
    max_occupancy integer,
    number_of_grills integer,
    rental_fee numeric(38,2),
    amenity_id character varying(255) NOT NULL
);


--
-- Name: billiard_room; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.billiard_room (
    allows_food_and_drinks boolean,
    number_of_billiard_tables integer,
    provides_pool_cues boolean,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: bowling_alley; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bowling_alley (
    allows_food_and_drinks boolean,
    number_of_lanes integer,
    provides_shoes boolean,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: building; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.building (
    total_floors integer,
    year_built integer,
    created_at timestamp(6) without time zone NOT NULL,
    last_modified_at timestamp(6) without time zone,
    address_id character varying(255),
    building_id character varying(255) NOT NULL,
    created_by character varying(255),
    last_modified_by character varying(255),
    management_company_id character varying(255),
    name character varying(255)
);


--
-- Name: daily_availability; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.daily_availability (
    close_time time(6) without time zone NOT NULL,
    open_time time(6) without time zone NOT NULL,
    amenity_id character varying(255) NOT NULL,
    day_id character varying(255) NOT NULL,
    day_of_week character varying(255) NOT NULL,
    CONSTRAINT daily_availability_day_of_week_check CHECK (((day_of_week)::text = ANY ((ARRAY['MONDAY'::character varying, 'TUESDAY'::character varying, 'WEDNESDAY'::character varying, 'THURSDAY'::character varying, 'FRIDAY'::character varying, 'SATURDAY'::character varying, 'SUNDAY'::character varying])::text[])))
);


--
-- Name: elevator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.elevator (
    floors_serviced integer,
    is_wheelchair_accessible boolean,
    weight_capacity integer,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: floor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.floor (
    floor_number integer,
    building_id character varying(255),
    floor_id character varying(255) NOT NULL,
    floor_name character varying(255)
);


--
-- Name: games_room; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.games_room (
    has_board_games boolean,
    max_capacity integer,
    number_of_game_consoles integer,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: golf_simulator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.golf_simulator (
    max_players integer,
    provides_clubs boolean,
    amenity_id character varying(255) NOT NULL,
    simulator_model character varying(255)
);


--
-- Name: guest_suite; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.guest_suite (
    has_kitchen boolean,
    nightly_rental_fee numeric(38,2),
    number_of_bedrooms integer,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: gym; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gym (
    has_changing_rooms boolean,
    has_fitness_classes boolean,
    has_lockers boolean,
    has_parking boolean,
    has_personal_trainers boolean,
    has_showers boolean,
    has_towels boolean,
    has_water_fountains boolean,
    has_wifi boolean,
    number_of_machines integer,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: identity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.identity (
    gender smallint,
    created_at timestamp(6) without time zone NOT NULL,
    last_modified_at timestamp(6) without time zone,
    created_by character varying(255),
    email character varying(255) NOT NULL,
    first_name character varying(255),
    last_modified_by character varying(255),
    last_name character varying(255),
    phone_number character varying(255),
    user_id character varying(255) NOT NULL,
    CONSTRAINT identity_gender_check CHECK (((gender >= 0) AND (gender <= 2)))
);


--
-- Name: lease; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lease (
    end_date date,
    original_end_date date,
    original_start_date date,
    renewal_count integer,
    rental_amount double precision,
    start_date date,
    lease_id character varying(255) NOT NULL,
    owner_id character varying(255),
    status character varying(255),
    unit_id character varying(255),
    CONSTRAINT lease_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'EXPIRED'::character varying, 'TERMINATED'::character varying, 'PENDING'::character varying, 'RENEWED'::character varying])::text[])))
);


--
-- Name: library; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.library (
    has_computers boolean,
    number_of_books integer,
    seating_capacity integer,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: management_company; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.management_company (
    created_at timestamp(6) without time zone NOT NULL,
    last_modified_at timestamp(6) without time zone,
    address_id character varying(255),
    created_by character varying(255),
    last_modified_by character varying(255),
    management_company_id character varying(255) NOT NULL,
    name character varying(255),
    website character varying(255)
);


--
-- Name: massage_room; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.massage_room (
    has_licensed_therapist boolean,
    has_sauna boolean,
    max_capacity integer,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: other; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.other (
    amenity_id character varying(255) NOT NULL,
    special_instructions character varying(255)
);


--
-- Name: owner; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.owner (
    is_primary_tenant boolean,
    owner_id character varying(255) NOT NULL,
    role character varying(255),
    status character varying(255),
    user_id character varying(255),
    CONSTRAINT owner_role_check CHECK (((role)::text = ANY ((ARRAY['DELETED'::character varying, 'DEFAULT'::character varying])::text[]))),
    CONSTRAINT owner_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'PENDING'::character varying, 'DELETED'::character varying])::text[])))
);


--
-- Name: party_room; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.party_room (
    amenity_id character varying(255) NOT NULL
);


--
-- Name: staff; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.staff (
    building_id character varying(255),
    company_id character varying(255),
    role character varying(255),
    staff_id character varying(255) NOT NULL,
    status character varying(255),
    user_id character varying(255),
    CONSTRAINT staff_role_check CHECK (((role)::text = ANY ((ARRAY['PROPERTY_MANAGER'::character varying, 'LEASING_AGENT'::character varying, 'MAINTENANCE_TECHNICIAN'::character varying, 'ACCOUNTING_FINANCE_MANAGER'::character varying, 'CUSTOMER_SERVICE_REPRESENTATIVE'::character varying, 'BUILDING_SUPERVISOR'::character varying, 'BUILDING_SECURITY'::character varying, 'ALL_STAFF'::character varying, 'DELETED'::character varying, 'DEFAULT'::character varying, 'OTHER'::character varying])::text[]))),
    CONSTRAINT staff_status_check CHECK (((status)::text = 'ACTIVE'::text))
);


--
-- Name: swimming_pool; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.swimming_pool (
    children_allowed boolean,
    chlorine_level double precision,
    depth double precision,
    has_diving_board boolean,
    has_heating boolean,
    has_jacuzzi boolean,
    has_lifeguard boolean,
    has_pool_bar boolean,
    has_slides boolean,
    is_indoor boolean,
    jacuzzi_temperature double precision,
    max_age_for_children integer,
    max_capacity integer,
    max_depth double precision,
    min_depth double precision,
    pool_rental_fee numeric(38,2),
    pool_size double precision,
    private_lessons_fee numeric(38,2),
    shower_required boolean,
    water_temperature double precision,
    accessibility_features character varying(255),
    adult_swim_time character varying(255),
    amenity_id character varying(255) NOT NULL,
    cleaning_schedule character varying(255),
    lap_swimming_hours character varying(255),
    recreational_swimming_hours character varying(255)
);


--
-- Name: tenant; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tenant (
    is_owner boolean,
    is_primary_tenant boolean,
    building_id character varying(255),
    lease_id character varying(255),
    role character varying(255),
    status character varying(255),
    tenant_id character varying(255) NOT NULL,
    unit_id character varying(255),
    user_id character varying(255),
    CONSTRAINT tenant_role_check CHECK (((role)::text = ANY ((ARRAY['DELETED'::character varying, 'DEFAULT'::character varying])::text[]))),
    CONSTRAINT tenant_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'PENDING'::character varying, 'DELETED'::character varying])::text[])))
);


--
-- Name: tennis_court; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tennis_court (
    booking_fee numeric(38,2),
    court_size double precision,
    has_benches boolean,
    has_spectator_seats boolean,
    is_bookable boolean,
    is_indoor boolean,
    lighting boolean,
    net_height double precision,
    amenity_id character varying(255) NOT NULL,
    court_surface character varying(255)
);


--
-- Name: theater; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.theater (
    has_3d_projection boolean,
    has_surround_sound boolean,
    number_of_seats integer,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: unit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.unit (
    number_of_bedrooms integer,
    square_footage double precision,
    unit_number integer,
    building_id character varying(255),
    floor_id character varying(255),
    owner_id character varying(255),
    unit_id character varying(255) NOT NULL
);


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_roles (
    user_role_id bigint NOT NULL,
    identity_id character varying(255) NOT NULL,
    persona_id character varying(255) NOT NULL,
    role character varying(255) NOT NULL,
    user_type character varying(255) NOT NULL,
    CONSTRAINT user_roles_user_type_check CHECK (((user_type)::text = ANY ((ARRAY['OWNER'::character varying, 'TENANT'::character varying, 'STAFF'::character varying, 'ADMIN'::character varying])::text[])))
);


--
-- Name: user_roles_user_role_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.user_roles ALTER COLUMN user_role_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.user_roles_user_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: wine_tasting_room; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.wine_tasting_room (
    allows_private_events boolean,
    number_of_wines_available integer,
    seating_capacity integer,
    amenity_id character varying(255) NOT NULL
);


--
-- Name: yoga_studio; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.yoga_studio (
    max_participants integer,
    provides_yoga_mats boolean,
    amenity_id character varying(255) NOT NULL,
    available_classes character varying(255)
);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);


--
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (admin_id);


--
-- Name: aerobics_room aerobics_room_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aerobics_room
    ADD CONSTRAINT aerobics_room_pkey PRIMARY KEY (amenity_id);


--
-- Name: amenity_booking amenity_booking_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amenity_booking
    ADD CONSTRAINT amenity_booking_pkey PRIMARY KEY (booking_id);


--
-- Name: amenity_building amenity_building_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amenity_building
    ADD CONSTRAINT amenity_building_pkey PRIMARY KEY (id);


--
-- Name: amenity amenity_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amenity
    ADD CONSTRAINT amenity_name_key UNIQUE (name);


--
-- Name: amenity amenity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amenity
    ADD CONSTRAINT amenity_pkey PRIMARY KEY (amenity_id);


--
-- Name: barbeque_area barbeque_area_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barbeque_area
    ADD CONSTRAINT barbeque_area_pkey PRIMARY KEY (amenity_id);


--
-- Name: billiard_room billiard_room_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.billiard_room
    ADD CONSTRAINT billiard_room_pkey PRIMARY KEY (amenity_id);


--
-- Name: bowling_alley bowling_alley_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bowling_alley
    ADD CONSTRAINT bowling_alley_pkey PRIMARY KEY (amenity_id);


--
-- Name: building building_address_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.building
    ADD CONSTRAINT building_address_id_key UNIQUE (address_id);


--
-- Name: building building_created_by_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.building
    ADD CONSTRAINT building_created_by_key UNIQUE (created_by);


--
-- Name: building building_last_modified_by_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.building
    ADD CONSTRAINT building_last_modified_by_key UNIQUE (last_modified_by);


--
-- Name: building building_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.building
    ADD CONSTRAINT building_pkey PRIMARY KEY (building_id);


--
-- Name: daily_availability daily_availability_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daily_availability
    ADD CONSTRAINT daily_availability_pkey PRIMARY KEY (day_id);


--
-- Name: elevator elevator_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.elevator
    ADD CONSTRAINT elevator_pkey PRIMARY KEY (amenity_id);


--
-- Name: floor floor_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.floor
    ADD CONSTRAINT floor_pkey PRIMARY KEY (floor_id);


--
-- Name: games_room games_room_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.games_room
    ADD CONSTRAINT games_room_pkey PRIMARY KEY (amenity_id);


--
-- Name: golf_simulator golf_simulator_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.golf_simulator
    ADD CONSTRAINT golf_simulator_pkey PRIMARY KEY (amenity_id);


--
-- Name: guest_suite guest_suite_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.guest_suite
    ADD CONSTRAINT guest_suite_pkey PRIMARY KEY (amenity_id);


--
-- Name: gym gym_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gym
    ADD CONSTRAINT gym_pkey PRIMARY KEY (amenity_id);


--
-- Name: identity identity_created_by_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity
    ADD CONSTRAINT identity_created_by_key UNIQUE (created_by);


--
-- Name: identity identity_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity
    ADD CONSTRAINT identity_email_key UNIQUE (email);


--
-- Name: identity identity_last_modified_by_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity
    ADD CONSTRAINT identity_last_modified_by_key UNIQUE (last_modified_by);


--
-- Name: identity identity_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity
    ADD CONSTRAINT identity_pkey PRIMARY KEY (user_id);


--
-- Name: lease lease_owner_id_unit_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lease
    ADD CONSTRAINT lease_owner_id_unit_id_key UNIQUE (owner_id, unit_id);


--
-- Name: lease lease_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lease
    ADD CONSTRAINT lease_pkey PRIMARY KEY (lease_id);


--
-- Name: library library_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.library
    ADD CONSTRAINT library_pkey PRIMARY KEY (amenity_id);


--
-- Name: management_company management_company_address_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.management_company
    ADD CONSTRAINT management_company_address_id_key UNIQUE (address_id);


--
-- Name: management_company management_company_created_by_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.management_company
    ADD CONSTRAINT management_company_created_by_key UNIQUE (created_by);


--
-- Name: management_company management_company_last_modified_by_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.management_company
    ADD CONSTRAINT management_company_last_modified_by_key UNIQUE (last_modified_by);


--
-- Name: management_company management_company_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.management_company
    ADD CONSTRAINT management_company_pkey PRIMARY KEY (management_company_id);


--
-- Name: massage_room massage_room_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.massage_room
    ADD CONSTRAINT massage_room_pkey PRIMARY KEY (amenity_id);


--
-- Name: other other_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.other
    ADD CONSTRAINT other_pkey PRIMARY KEY (amenity_id);


--
-- Name: owner owner_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.owner
    ADD CONSTRAINT owner_pkey PRIMARY KEY (owner_id);


--
-- Name: party_room party_room_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.party_room
    ADD CONSTRAINT party_room_pkey PRIMARY KEY (amenity_id);


--
-- Name: staff staff_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_pkey PRIMARY KEY (staff_id);


--
-- Name: swimming_pool swimming_pool_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.swimming_pool
    ADD CONSTRAINT swimming_pool_pkey PRIMARY KEY (amenity_id);


--
-- Name: tenant tenant_lease_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant
    ADD CONSTRAINT tenant_lease_id_key UNIQUE (lease_id);


--
-- Name: tenant tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant
    ADD CONSTRAINT tenant_pkey PRIMARY KEY (tenant_id);


--
-- Name: tennis_court tennis_court_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tennis_court
    ADD CONSTRAINT tennis_court_pkey PRIMARY KEY (amenity_id);


--
-- Name: theater theater_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.theater
    ADD CONSTRAINT theater_pkey PRIMARY KEY (amenity_id);


--
-- Name: unit unit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unit_id);


--
-- Name: user_roles user_roles_identity_id_persona_id_user_type_role_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_identity_id_persona_id_user_type_role_key UNIQUE (identity_id, persona_id, user_type, role);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_role_id);


--
-- Name: wine_tasting_room wine_tasting_room_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.wine_tasting_room
    ADD CONSTRAINT wine_tasting_room_pkey PRIMARY KEY (amenity_id);


--
-- Name: yoga_studio yoga_studio_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yoga_studio
    ADD CONSTRAINT yoga_studio_pkey PRIMARY KEY (amenity_id);


--
-- Name: tenant fk1gyatfnex9ubyk2sdhw1sweoh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant
    ADD CONSTRAINT fk1gyatfnex9ubyk2sdhw1sweoh FOREIGN KEY (unit_id) REFERENCES public.unit(unit_id);


--
-- Name: theater fk2vseinre3cpiicbg3bv3pmfyk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.theater
    ADD CONSTRAINT fk2vseinre3cpiicbg3bv3pmfyk FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: amenity_image_gallery fk2xsomlonho2y9hjmg8pae0k6x; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amenity_image_gallery
    ADD CONSTRAINT fk2xsomlonho2y9hjmg8pae0k6x FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: library fk379ns5nj6o86r0om5cr5vqrfh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.library
    ADD CONSTRAINT fk379ns5nj6o86r0om5cr5vqrfh FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: golf_simulator fk3f234ejsd6ywg9hh8tqaa2iau; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.golf_simulator
    ADD CONSTRAINT fk3f234ejsd6ywg9hh8tqaa2iau FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: wine_tasting_room fk4g2dwhx37ff3nrsnsmmu67624; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.wine_tasting_room
    ADD CONSTRAINT fk4g2dwhx37ff3nrsnsmmu67624 FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: building fk5grk1u490m3dy6xy9d5xy8hfo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.building
    ADD CONSTRAINT fk5grk1u490m3dy6xy9d5xy8hfo FOREIGN KEY (management_company_id) REFERENCES public.management_company(management_company_id);


--
-- Name: other fk6mcubx73j39t91s3fponlef0m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.other
    ADD CONSTRAINT fk6mcubx73j39t91s3fponlef0m FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: identity fk75nya8umwfmwdduutjchrdwk4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity
    ADD CONSTRAINT fk75nya8umwfmwdduutjchrdwk4 FOREIGN KEY (created_by) REFERENCES public.identity(user_id);


--
-- Name: amenity_custom_rules fk7iaase1t9ti61fhpjfj7mik9k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amenity_custom_rules
    ADD CONSTRAINT fk7iaase1t9ti61fhpjfj7mik9k FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: tenant fk7yrgb7scaprv6quaes5wfjvtq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant
    ADD CONSTRAINT fk7yrgb7scaprv6quaes5wfjvtq FOREIGN KEY (user_id) REFERENCES public.identity(user_id);


--
-- Name: massage_room fk8cif1tbs82dnof102faoo3fha; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.massage_room
    ADD CONSTRAINT fk8cif1tbs82dnof102faoo3fha FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: yoga_studio fk9f0fd2u3r392iwe65xlrmntsn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yoga_studio
    ADD CONSTRAINT fk9f0fd2u3r392iwe65xlrmntsn FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: staff fk9l9wxvmqij0d0y1nk1d78bdxf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT fk9l9wxvmqij0d0y1nk1d78bdxf FOREIGN KEY (company_id) REFERENCES public.management_company(management_company_id);


--
-- Name: tenant fkbhmvwg79q2saih1or6k1jl50i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant
    ADD CONSTRAINT fkbhmvwg79q2saih1or6k1jl50i FOREIGN KEY (building_id) REFERENCES public.building(building_id);


--
-- Name: billiard_room fkeb0nh6dsrbr8vyquattg68fg9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.billiard_room
    ADD CONSTRAINT fkeb0nh6dsrbr8vyquattg68fg9 FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: building fkf3ryyh4bd143l5b8stt65hwgr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.building
    ADD CONSTRAINT fkf3ryyh4bd143l5b8stt65hwgr FOREIGN KEY (address_id) REFERENCES public.address(address_id);


--
-- Name: floor fkfvb11l7lpgqc6qdrg3bm24kr3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.floor
    ADD CONSTRAINT fkfvb11l7lpgqc6qdrg3bm24kr3 FOREIGN KEY (building_id) REFERENCES public.building(building_id);


--
-- Name: swimming_pool fkgntjrpmqypfxbtb6se8qsuu1b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.swimming_pool
    ADD CONSTRAINT fkgntjrpmqypfxbtb6se8qsuu1b FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: daily_availability fkh007isgfhwfc5tc3pykblnrvr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daily_availability
    ADD CONSTRAINT fkh007isgfhwfc5tc3pykblnrvr FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: gym fkilw09a3m97bl1ar2oy3qtdin1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gym
    ADD CONSTRAINT fkilw09a3m97bl1ar2oy3qtdin1 FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: building fkipeuj8g9urk87bjomih6ga0oy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.building
    ADD CONSTRAINT fkipeuj8g9urk87bjomih6ga0oy FOREIGN KEY (last_modified_by) REFERENCES public.identity(user_id);


--
-- Name: tennis_court fkiyiege0kt6jb4daqtes9h8e4q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tennis_court
    ADD CONSTRAINT fkiyiege0kt6jb4daqtes9h8e4q FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: guest_suite fkj7sy4j6a9rlo1e7s1ogc0vore; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.guest_suite
    ADD CONSTRAINT fkj7sy4j6a9rlo1e7s1ogc0vore FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: bowling_alley fkjpu3e2pad20ygr133wjpydsn7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bowling_alley
    ADD CONSTRAINT fkjpu3e2pad20ygr133wjpydsn7 FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: management_company fkjwgoqrh7l6769kt1a2ucj0trj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.management_company
    ADD CONSTRAINT fkjwgoqrh7l6769kt1a2ucj0trj FOREIGN KEY (last_modified_by) REFERENCES public.identity(user_id);


--
-- Name: unit fkklptfnnbkkgj8o0v8w8x88lcm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.unit
    ADD CONSTRAINT fkklptfnnbkkgj8o0v8w8x88lcm FOREIGN KEY (owner_id) REFERENCES public.owner(owner_id);


--
-- Name: identity fklolik0ymuhdpub7pi1q5gf1em; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity
    ADD CONSTRAINT fklolik0ymuhdpub7pi1q5gf1em FOREIGN KEY (last_modified_by) REFERENCES public.identity(user_id);


--
-- Name: building fkm0qfikoj3cc88j8fnov9wraxj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.building
    ADD CONSTRAINT fkm0qfikoj3cc88j8fnov9wraxj FOREIGN KEY (created_by) REFERENCES public.identity(user_id);


--
-- Name: owner fkmtlvr9lrrf3eack6f6wd0uii1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.owner
    ADD CONSTRAINT fkmtlvr9lrrf3eack6f6wd0uii1 FOREIGN KEY (user_id) REFERENCES public.identity(user_id);


--
-- Name: staff fkn5i2niom635rio4hte8xv0udo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT fkn5i2niom635rio4hte8xv0udo FOREIGN KEY (user_id) REFERENCES public.identity(user_id);


--
-- Name: aerobics_room fkodbkiu4wheydl9eg90mwrsne0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aerobics_room
    ADD CONSTRAINT fkodbkiu4wheydl9eg90mwrsne0 FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: elevator fkoeipjcyics7po5gslutfdc4h; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.elevator
    ADD CONSTRAINT fkoeipjcyics7po5gslutfdc4h FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: management_company fkofd1m22fwkpf1km4ynjpg20ws; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.management_company
    ADD CONSTRAINT fkofd1m22fwkpf1km4ynjpg20ws FOREIGN KEY (address_id) REFERENCES public.address(address_id);


--
-- Name: management_company fkogsy3khbaobr4qltr93yggcdr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.management_company
    ADD CONSTRAINT fkogsy3khbaobr4qltr93yggcdr FOREIGN KEY (created_by) REFERENCES public.identity(user_id);


--
-- Name: unit fkox6glpbqx7txpntux9rgkqul2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.unit
    ADD CONSTRAINT fkox6glpbqx7txpntux9rgkqul2 FOREIGN KEY (floor_id) REFERENCES public.floor(floor_id);


--
-- Name: unit fkp0hq1evgtn9mkl6epaipd3g3e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.unit
    ADD CONSTRAINT fkp0hq1evgtn9mkl6epaipd3g3e FOREIGN KEY (building_id) REFERENCES public.building(building_id);


--
-- Name: admin fkpvgdoni47xvivdkmr3njshdmf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT fkpvgdoni47xvivdkmr3njshdmf FOREIGN KEY (user_id) REFERENCES public.identity(user_id);


--
-- Name: party_room fkrvp40yfv8otomhtcee2ba64rd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.party_room
    ADD CONSTRAINT fkrvp40yfv8otomhtcee2ba64rd FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: staff fkshc8pylar2c2sf58qylbfglw4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT fkshc8pylar2c2sf58qylbfglw4 FOREIGN KEY (building_id) REFERENCES public.building(building_id);


--
-- Name: games_room fkswe4ief0ikjujrwhlfvrtllkc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.games_room
    ADD CONSTRAINT fkswe4ief0ikjujrwhlfvrtllkc FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: barbeque_area fktc3nxmydmdia52mhcvp5dlpc3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barbeque_area
    ADD CONSTRAINT fktc3nxmydmdia52mhcvp5dlpc3 FOREIGN KEY (amenity_id) REFERENCES public.amenity(amenity_id);


--
-- Name: tenant fktf0qtmv8498ags7tqg5pf0rdj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant
    ADD CONSTRAINT fktf0qtmv8498ags7tqg5pf0rdj FOREIGN KEY (lease_id) REFERENCES public.lease(lease_id);


--
-- PostgreSQL database dump complete
--

\unrestrict yqahpzedzkQMaXjb0EZOiNwMyDFtuG7i6L5yMJhw0o4XohzI2SFOeVd3wGvQTIG

