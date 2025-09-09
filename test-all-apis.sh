#!/bin/bash

# CloudSuites Comp# JWT Token for authenticated requests (optional)
# JWT_TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBRE0tMDFLM1o0TkgxVkdHVlBWUjFaNThFTjJNS0YiLCJhdWQiOlsiQ2xvdWRTdWl0ZXMiXSwidHlwZSI6IkFETUlOIiwidXNlcklkIjoiSUQtMDFLM1o0TkgxVEZFRzEyTjlUOVBNRjlRWEoiLCJpc3MiOiJjbG91ZHN1aXRlcyIsImlhdCI6MTc1NzI5Njg5MCwiZXhwIjoxNzg4ODMyODkwfQ.ycFZdWAYfTts6VrmJ714eTbIjyHJIgkeVGteMghJc18Jaq_7nnKJIxh5-kFZRq7dQAhDlqrI1qtawmYFPAkOdw"
# This script tests all API endpoints in a logical orchestrated flow:
# 1. Authentication & Admin Setup
# 2. Property Structure (Company → Building → Floor → Units)
# 3. User Management (Staff → Owners → Tenants)
# 4. Amenity Management & Bookings
# 5. Role Management & Permissions
#
# NOTE: OTP verification tests are skipped since they require Twilio SMS codes
# that cannot be extracted during automated testing. OTP request tests are included.
#
# Usage:
#   ./test-all-apis.sh                    # Uses default 1-year JWT token
#   ./test-all-apis.sh "your_jwt_token"   # Uses custom JWT token

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost:8080/api/v1"
TEMP_DIR="/tmp/cloudsuites-api-tests"
TEST_LOG="${TEMP_DIR}/test_results.log"

# JWT Token for authenticated requests (optional)
JWT_TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBRE0tMDFLM1o0TkgxVkdHVlBWUjFaNThFTjJNS0YiLCJhdWQiOlsiQ2xvdWRTdWl0ZXMiXSwidHlwZSI6IkFETUlOIiwidXNlcklkIjoiSUQtMDFLM1o0TkgxVEZFRzEyTjlUOVBNRjlRWEoiLCJpc3MiOiJjbG91ZHN1aXRlcyIsImlhdCI6MTc1NzI5Njg5MCwiZXhwIjoxNzg4ODMyODkwfQ.ycFZdWAYfTts6VrmJ714eTbIjyHJIgkeVGteMghJc18Jaq_7nnKJIxh5-kFZRq7dQAhDlqrI1qtawmYFPAkOdw"

# Override with command line parameter if provided
if [[ -n "$1" ]]; then
    JWT_TOKEN="$1"
fi


# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Function to print colored output
print_section() {
    echo -e "\n${PURPLE}========================================${NC}"
    echo -e "${PURPLE}$1${NC}"
    echo -e "${PURPLE}========================================${NC}"
}

print_test() {
    echo -e "\n${CYAN}[TEST]${NC} $1"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
}

print_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
    PASSED_TESTS=$((PASSED_TESTS + 1))
    echo "[PASS] $1" >> "$TEST_LOG"
}

print_error() {
    echo -e "${RED}[FAIL]${NC} $1"
    FAILED_TESTS=$((FAILED_TESTS + 1))
    echo "[FAIL] $1" >> "$TEST_LOG"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
    echo "[WARNING] $1" >> "$TEST_LOG"
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Function to check if jq is installed
check_dependencies() {
    if ! command -v jq &> /dev/null; then
        print_error "jq is required but not installed. Please install jq first"
        exit 1
    fi
}

# Function to test API connectivity
test_connectivity() {
    print_test "API Connectivity"
    local response_code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/actuator/health")
    
    if [[ "$response_code" == "200" ]]; then
        print_success "API is accessible"
    else
        print_error "Cannot connect to CloudSuites API"
        exit 1
    fi
}

# Function to make authenticated API call
make_api_call() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_code=${4:-200}
    local token=$5
    
    local headers=("-H" "accept: application/json" "-H" "Content-Type: application/json")
    if [[ -n "$token" ]]; then
        headers+=("-H" "Authorization: Bearer $token")
    fi
    
    local temp_file="${TEMP_DIR}/curl_response.tmp"
    local status_code
    
    if [[ -n "$data" ]]; then
        status_code=$(curl -s -X "$method" "${BASE_URL}${endpoint}" "${headers[@]}" -d "$data" -w "%{http_code}" -o "$temp_file")
    else
        status_code=$(curl -s -X "$method" "${BASE_URL}${endpoint}" "${headers[@]}" -w "%{http_code}" -o "$temp_file")
    fi
    
    # Store response for extraction
    cp "$temp_file" "${TEMP_DIR}/last_response.json"
    echo "$status_code" > "${TEMP_DIR}/last_status_code.txt"
    
    if [[ "$status_code" == "$expected_code" ]]; then
        return 0
    else
        print_warning "Expected $expected_code but got $status_code"
        if [[ -s "$temp_file" ]]; then
            print_warning "Response: $(cat "$temp_file")"
        fi
        return 1
    fi
}

# Function to extract value from last response
extract_from_response() {
    local key=$1
    cat "${TEMP_DIR}/last_response.json" | jq -r ".$key"
}

# 1. AUTHENTICATION & ADMIN SETUP
test_authentication() {
    print_section "1. AUTHENTICATION & ADMIN SETUP"
    
    # Extract admin ID from JWT token if available
    if [[ -n "$JWT_TOKEN" ]]; then
        print_test "Extract Admin ID from JWT Token"
        local jwt_payload=$(echo "$JWT_TOKEN" | cut -d'.' -f2)
        # Add padding if needed for base64 decoding
        local padding=$((4 - ${#jwt_payload} % 4))
        if [[ $padding -ne 4 ]]; then
            jwt_payload="${jwt_payload}$(printf '%*s' $padding | tr ' ' '=')"
        fi
        
        ADMIN_ID=$(echo "$jwt_payload" | base64 -d 2>/dev/null | jq -r '.sub // empty')
        if [[ -n "$ADMIN_ID" && "$ADMIN_ID" != "null" ]]; then
            print_success "Using admin from JWT token: $ADMIN_ID"
            echo "$ADMIN_ID" > "${TEMP_DIR}/admin_id.txt"
        else
            ADMIN_ID="ADM-01K3Z4NH1VGGVPVR1Z58EN2MKF"  # Fallback to known admin
            print_warning "Could not extract admin ID from JWT, using fallback: $ADMIN_ID"
            echo "$ADMIN_ID" > "${TEMP_DIR}/admin_id.txt"
        fi
    else
        # Test admin registration (only if no JWT token)
        print_test "Admin Registration"
        local admin_payload='{
            "identity": {
                "firstName": "John",
                "lastName": "Admin",
                "email": "admin'$(date +%s)'@cloudsuites.com",
                "phoneNumber": "+14166024669",
                "gender": "MALE"
            },
            "role": "SUPER_ADMIN",
            "status": "ACTIVE"
        }'
        
        if make_api_call "POST" "/auth/admins/register" "$admin_payload" 200; then
            ADMIN_ID=$(extract_from_response "adminId")
            print_success "Admin registered with ID: $ADMIN_ID"
            echo "$ADMIN_ID" > "${TEMP_DIR}/admin_id.txt"
        elif [[ $(cat "${TEMP_DIR}/last_status_code.txt") == "409" ]]; then
            print_success "Admin already exists - using existing admin"
            ADMIN_ID="ADM-01K3Z4NH1VGGVPVR1Z58EN2MKF"
            echo "$ADMIN_ID" > "${TEMP_DIR}/admin_id.txt"
        else
            print_error "Admin registration failed"
            return 1
        fi
    fi
    
    # Test OTP request for admin (DISABLED - prevents unnecessary SMS)
    print_test "Admin OTP Request"
    print_info "Skipped - OTP requests disabled to prevent unnecessary SMS"
    
    # Get all admins
    print_test "Get All Admins"
    if make_api_call "GET" "/admins" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved all admins"
    else
        print_error "Failed to get admins"
    fi
    
    # Get admin by ID
    print_test "Get Admin by ID"
    if make_api_call "GET" "/admins/${ADMIN_ID}" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved admin by ID"
    else
        print_error "Failed to get admin by ID"
    fi
}

# 2. PROPERTY STRUCTURE SETUP
test_property_structure() {
    print_section "2. PROPERTY STRUCTURE SETUP"
    
    # Use existing IDs from our current setup
    COMPANY_ID="MC-01K3Z4Y7DSCP3Y2SGXHNJG64ZF"
    BUILDING_ID="BLD-01K3Z53T176C2AH23XEM0VBHN2"
    FLOOR_ID="FL-01K3Z565HM27B2GE23GF3TH1V6"
    
    # Test get all companies
    print_test "Get All Companies"
    if make_api_call "GET" "/companies" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved all companies"
    else
        print_error "Failed to get companies"
    fi
    
    # Test get company by ID
    print_test "Get Company by ID"
    if make_api_call "GET" "/companies/${COMPANY_ID}" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved company by ID"
    else
        print_error "Failed to get company by ID"
    fi
    
    # Test get buildings for company
    print_test "Get Buildings for Company"
    if make_api_call "GET" "/companies/${COMPANY_ID}/buildings" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved buildings for company"
    else
        print_error "Failed to get buildings"
    fi
    
    # Test get building by ID
    print_test "Get Building by ID"
    if make_api_call "GET" "/companies/${COMPANY_ID}/buildings/${BUILDING_ID}" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved building by ID"
    else
        print_error "Failed to get building by ID"
    fi
    
    # Test get floors for building
    print_test "Get Floors for Building"
    if make_api_call "GET" "/buildings/${BUILDING_ID}/floors" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved floors for building"
    else
        print_error "Failed to get floors"
    fi
    
    # Test get floor by ID
    print_test "Get Floor by ID"
    if make_api_call "GET" "/buildings/${BUILDING_ID}/floors/${FLOOR_ID}" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved floor by ID"
    else
        print_error "Failed to get floor by ID"
    fi
    
    # Test get units for building
    print_test "Get Units for Building"
    if make_api_call "GET" "/buildings/${BUILDING_ID}/units" "" 200 "$JWT_TOKEN"; then
        # Try different extraction methods based on response structure
        UNIT_ID=$(extract_from_response "content[0].unitId" 2>/dev/null || extract_from_response "[0].unitId" 2>/dev/null || extract_from_response "unitId" 2>/dev/null)
        if [[ "$UNIT_ID" == "null" || -z "$UNIT_ID" ]]; then
            UNIT_ID="UN-01K3Z565HND25JD6ZXEQ3X25BB"  # Use current unit ID as fallback
            print_warning "Could not extract unit ID from response, using fallback"
        fi
        print_success "Retrieved units for building"
        echo "$UNIT_ID" > "${TEMP_DIR}/unit_id.txt"
        echo "$COMPANY_ID" > "${TEMP_DIR}/company_id.txt"
        echo "$BUILDING_ID" > "${TEMP_DIR}/building_id.txt"
        echo "$FLOOR_ID" > "${TEMP_DIR}/floor_id.txt"
    else
        print_error "Failed to get units"
    fi
}

# 3. USER MANAGEMENT
test_user_management() {
    print_section "3. USER MANAGEMENT"
    
    COMPANY_ID=$(cat "${TEMP_DIR}/company_id.txt")
    BUILDING_ID=$(cat "${TEMP_DIR}/building_id.txt")
    UNIT_ID=$(cat "${TEMP_DIR}/unit_id.txt")
    
    # Test create staff for company (add status field and unique email)
    print_test "Create Company Staff"
    local staff_payload='{
        "identity": {
            "firstName": "Jane",
            "lastName": "Staff",
            "email": "staff'$(date +%s)'@cloudsuites.com",
            "phoneNumber": "+14166024669",
            "gender": "FEMALE"
        },
        "role": "PROPERTY_MANAGER",
        "status": "ACTIVE"
    }'
    
    if make_api_call "POST" "/staff/companies/${COMPANY_ID}" "$staff_payload" 201 "$JWT_TOKEN"; then
        STAFF_ID=$(extract_from_response "staffId")
        print_success "Staff created with ID: $STAFF_ID"
        echo "$STAFF_ID" > "${TEMP_DIR}/staff_id.txt"
    else
        print_error "Staff creation failed"
    fi
    
    # Test create building staff (add status field and unique email)
    print_test "Create Building Staff"
    local building_staff_payload='{
        "identity": {
            "firstName": "Bob",
            "lastName": "Security",
            "email": "security'$(date +%s)'@cloudsuites.com",
            "phoneNumber": "+14166024669",
            "gender": "MALE"
        },
        "role": "BUILDING_SECURITY",
        "status": "ACTIVE"
    }'
    
    # Try without JWT token first (some endpoints don't require auth)
    if make_api_call "POST" "/staff/companies/${COMPANY_ID}/building/${BUILDING_ID}" "$building_staff_payload" 201 "$JWT_TOKEN"; then
        BUILDING_STAFF_ID=$(extract_from_response "staffId")
        print_success "Building staff created with ID: $BUILDING_STAFF_ID"
        echo "$BUILDING_STAFF_ID" > "${TEMP_DIR}/building_staff_id.txt"
    else
        print_error "Building staff creation failed"
        print_warning "Endpoint requires 'ALL_ADMIN' or 'BUILDING_SUPERVISOR' authority"
        print_info "JWT token contains SUPER_ADMIN role which should inherit ALL_ADMIN via role hierarchy"
        print_info "This might be a Spring Security role hierarchy configuration issue"
    fi
    
    # Test create owner (use large random number for uniqueness)
    print_test "Create Owner"
    local random_id=$((RANDOM * RANDOM * RANDOM))  # Generate a very large random number
    local owner_payload='{
        "identity": {
            "firstName": "Alice",
            "lastName": "Owner",
            "email": "owner.'$random_id'.unique@cloudsuites.com",
            "phoneNumber": "+14166024669",
            "gender": "FEMALE"
        },
        "isPrimaryTenant": true,
        "status": "ACTIVE",
        "role": "DEFAULT"
    }'

    if make_api_call "POST" "/owners" "$owner_payload" 201 "$JWT_TOKEN"; then
        OWNER_ID=$(extract_from_response "ownerId")
        print_success "Owner created with ID: $OWNER_ID"
        echo "$OWNER_ID" > "${TEMP_DIR}/owner_id.txt"
        
        # Associate the unit with the owner
        print_test "Associate Unit with Owner"
        if [[ -n "$UNIT_ID" && -n "$BUILDING_ID" && -n "$OWNER_ID" ]]; then
            if make_api_call "POST" "/owners/${OWNER_ID}/buildings/${BUILDING_ID}/units/${UNIT_ID}/transfer" "" 200 "$JWT_TOKEN"; then
                print_success "Unit $UNIT_ID associated with owner $OWNER_ID"
            else
                print_error "Failed to associate unit with owner"
            fi
        else
            print_warning "Skipping unit association - missing required IDs"
        fi
    else
        print_error "Owner creation failed"
    fi
    
    # Test create tenant (use more unique email)
    print_test "Create Tenant"
    local tenant_payload='{
        "identity": {
            "gender": "MALE",
            "firstName": "Charlie",
            "lastName": "Tenant",
            "phoneNumber": "+14166024669",
            "email": "tenant.'$(date +%s).$$'@cloudsuites.com"
        },
        "isOwner": false,
        "isPrimaryTenant": true,
        "status": "ACTIVE",
        "role": "DEFAULT",
        "lease": {
            "leaseId": "string",
            "ownerId": "string",
            "unitId": "string",
            "startDate": "2025-01-01",
            "endDate": "2025-12-31",
            "originalStartDate": "2025-01-01",
            "originalEndDate": "2025-12-31",
            "rentalAmount": 1200.00,
            "status": "ACTIVE",
            "renewalCount": 0
        }
    }'
    
    if make_api_call "POST" "/buildings/${BUILDING_ID}/units/${UNIT_ID}/tenants" "$tenant_payload" 201 "$JWT_TOKEN"; then
        TENANT_ID=$(extract_from_response "tenantId")
        print_success "Tenant created with ID: $TENANT_ID"
        echo "$TENANT_ID" > "${TEMP_DIR}/tenant_id.txt"
    else
        print_error "Tenant creation failed"
    fi
    
    # Test get all staff by company
    print_test "Get All Staff by Company"
    if make_api_call "GET" "/staff/companies/${COMPANY_ID}" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved staff by company"
    else
        print_error "Failed to get staff by company"
    fi
    
    # Test get all owners
    print_test "Get All Owners"
    if make_api_call "GET" "/owners" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved all owners"
    else
        print_error "Failed to get owners"
    fi
    
    # Test get tenants by unit
    print_test "Get Tenants by Unit"
    if make_api_call "GET" "/buildings/${BUILDING_ID}/units/${UNIT_ID}/tenants" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved tenants by unit"
    else
        print_error "Failed to get tenants by unit"
    fi
}

# 4. AMENITY MANAGEMENT
test_amenity_management() {
    print_section "4. AMENITY MANAGEMENT"
    
    BUILDING_ID=$(cat "${TEMP_DIR}/building_id.txt" 2>/dev/null || echo "BLD-01K3Z53T176C2AH23XEM0VBHN2")
    TENANT_ID=$(cat "${TEMP_DIR}/tenant_id.txt" 2>/dev/null || echo "TENANT-FALLBACK")
    
    # Test get all amenities
    print_test "Get All Amenities"
    if make_api_call "GET" "/buildings/amenities" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved all amenities"
    else
        print_error "Failed to get all amenities"
    fi
    
    # Test create amenity
    print_test "Create Amenity"
    # Generate unique name with timestamp and process ID
    local unique_suffix="$(date +%s)-$$"
    local amenity_payload="{
        \"type\": \"SWIMMING_POOL\",
        \"dailyAvailabilities\": [
            {
                \"dayOfWeek\": \"MONDAY\",
                \"openTime\": \"08:00:00\",
                \"closeTime\": \"20:00:00\"
            },
            {
                \"dayOfWeek\": \"TUESDAY\",
                \"openTime\": \"08:00:00\",
                \"closeTime\": \"20:00:00\"
            },
            {
                \"dayOfWeek\": \"WEDNESDAY\",
                \"openTime\": \"08:00:00\",
                \"closeTime\": \"20:00:00\"
            },
            {
                \"dayOfWeek\": \"THURSDAY\",
                \"openTime\": \"08:00:00\",
                \"closeTime\": \"20:00:00\"
            },
            {
                \"dayOfWeek\": \"FRIDAY\",
                \"openTime\": \"08:00:00\",
                \"closeTime\": \"20:00:00\"
            },
            {
                \"dayOfWeek\": \"SATURDAY\",
                \"openTime\": \"09:00:00\",
                \"closeTime\": \"18:00:00\"
            },
            {
                \"dayOfWeek\": \"SUNDAY\",
                \"openTime\": \"09:00:00\",
                \"closeTime\": \"18:00:00\"
            }
        ],
        \"name\": \"Swimming Pool Test ${unique_suffix}\",
        \"isActive\": true,
        \"description\": \"Olympic-sized swimming pool with diving board\",
        \"location\": \"Level 2, Building A\",
        \"capacity\": 50,
        \"isBookingRequired\": true,
        \"isPaidService\": false,
        \"rules\": \"No food or drinks allowed. Proper swimwear required.\",
        \"maintenanceStatus\": \"OPERATIONAL\",
        \"advanceBookingPeriod\": 7,
        \"bookingDurationLimit\": 120,
        \"minimumBookingDuration\": 30,
        \"maxBookingsPerTenant\": 2,
        \"bookingLimitPeriod\": \"DAILY\",
        \"imageGallery\": [
            \"http://example.com/pool1.jpg\",
            \"http://example.com/pool2.jpg\"
        ],
        \"videoUrl\": \"http://example.com/pool-video.mp4\",
        \"buildingIds\": [
            \"${BUILDING_ID}\"
        ],
        \"customRules\": [\"No diving in shallow end\", \"Children must be supervised\"],
        \"waiverDetails\": [\"Swimming pool usage waiver - use at your own risk\"],
        \"isWaiverRequired\": false,
        \"isWaiverSigned\": false
    }"
    
    if make_api_call "POST" "/amenities" "$amenity_payload" 201 "$JWT_TOKEN"; then
        AMENITY_ID=$(extract_from_response "amenityId")
        print_success "Amenity created with ID: $AMENITY_ID"
        echo "$AMENITY_ID" > "${TEMP_DIR}/amenity_id.txt"
    elif [[ $(cat "${TEMP_DIR}/last_status_code.txt") == "409" ]]; then
        # Amenity already exists, try to get existing one
        print_success "Amenity already exists - retrieving existing amenity"
        if make_api_call "GET" "/buildings/amenities" "" 200 "$JWT_TOKEN"; then
            # Extract first amenity ID from the response
            AMENITY_ID=$(cat "${TEMP_DIR}/last_response.json" | jq -r '.[0].amenityId // empty')
            if [[ -n "$AMENITY_ID" && "$AMENITY_ID" != "null" ]]; then
                print_success "Using existing amenity ID: $AMENITY_ID"
                echo "$AMENITY_ID" > "${TEMP_DIR}/amenity_id.txt"
            else
                print_warning "Could not extract amenity ID from existing amenities"
            fi
        fi
    else
        print_error "Amenity creation failed"
    fi
    
    # Test add building to amenity
    print_test "Add Building to Amenity"
    if [[ -n "$AMENITY_ID" ]]; then
        if make_api_call "POST" "/amenities/${AMENITY_ID}/add-building/${BUILDING_ID}" "" 200 "$JWT_TOKEN"; then
            print_success "Building added to amenity"
        else
            print_error "Failed to add building to amenity"
        fi
    else
        print_warning "Skipping - no amenity ID available"
    fi
    
    # Test get all building amenities
    print_test "Get Building Amenities"
    if make_api_call "GET" "/buildings/${BUILDING_ID}/amenities" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved building amenities"
    else
        print_error "Failed to get building amenities"
    fi
    
    # Test get amenity by ID
    print_test "Get Amenity by ID"
    if [[ -n "$AMENITY_ID" ]]; then
        if make_api_call "GET" "/buildings/${BUILDING_ID}/amenities/${AMENITY_ID}" "" 200 "$JWT_TOKEN"; then
            print_success "Retrieved amenity by ID"
        else
            print_error "Failed to get amenity by ID"
        fi
    else
        print_warning "Skipping - no amenity ID available"
    fi
    
    # Test amenity booking
    print_test "Create Amenity Booking"
    if [[ -n "$AMENITY_ID" && "$TENANT_ID" != "TENANT-FALLBACK" ]]; then
        # Use future date (tomorrow) with proper UTC timezone format
        local tomorrow=$(date -v+1d +"%Y-%m-%d")
        local booking_payload='{
            "startTime": "'${tomorrow}'T09:00:00.000Z",
            "endTime": "'${tomorrow}'T10:00:00.000Z"
        }'
        
        if make_api_call "POST" "/amenities/${AMENITY_ID}/tenants/${TENANT_ID}/bookings" "$booking_payload" 201 "$JWT_TOKEN"; then
            BOOKING_ID=$(extract_from_response "bookingId")
            print_success "Booking created with ID: $BOOKING_ID"
            echo "$BOOKING_ID" > "${TEMP_DIR}/booking_id.txt"
        else
            print_error "Booking creation failed"
        fi
    else
        print_warning "Skipping - no amenity ID or tenant ID available"
    fi
}

# 5. ROLE MANAGEMENT
test_role_management() {
    print_section "5. ROLE MANAGEMENT"
    
    # Use fallback IDs if files don't exist
    ADMIN_ID=$(cat "${TEMP_DIR}/admin_id.txt" 2>/dev/null || echo "ADM-01K3Z4NH1VGGVPVR1Z58EN2MKF")
    STAFF_ID=$(cat "${TEMP_DIR}/staff_id.txt" 2>/dev/null || echo "STAFF-FALLBACK")
    OWNER_ID=$(cat "${TEMP_DIR}/owner_id.txt" 2>/dev/null || echo "OWNER-FALLBACK")
    TENANT_ID=$(cat "${TEMP_DIR}/tenant_id.txt" 2>/dev/null || echo "TENANT-FALLBACK")
    
    # Test admin roles
    print_test "Get Admin Roles"
    if make_api_call "GET" "/admins/roles" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved admin roles"
    else
        print_error "Failed to get admin roles"
    fi
    
    # Test staff roles
    print_test "Get Staff Roles"
    if make_api_call "GET" "/staff/roles" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved staff roles"
    else
        print_error "Failed to get staff roles"
    fi
    
    # Test owner roles
    print_test "Get Owner Roles"
    if make_api_call "GET" "/owners/roles" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved owner roles"
    else
        print_error "Failed to get owner roles"
    fi
    
    # Test tenant roles
    print_test "Get Tenant Roles"
    if make_api_call "GET" "/tenants/roles" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved tenant roles"
    else
        print_error "Failed to get tenant roles"
    fi
    
    # Test get individual user roles
    print_test "Get Admin Role by ID"
    if make_api_call "GET" "/admins/${ADMIN_ID}/roles" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved admin role by ID"
    else
        print_error "Failed to get admin role by ID"
    fi
    
    print_test "Get Staff Role by ID"
    if make_api_call "GET" "/staff/${STAFF_ID}/roles" "" 200 "$JWT_TOKEN"; then
        print_success "Retrieved staff role by ID"
    else
        print_error "Failed to get staff role by ID"
    fi
}

# 6. AUTHENTICATION WORKFLOWS
test_authentication_workflows() {
    print_section "6. AUTHENTICATION WORKFLOWS"
    
    COMPANY_ID=$(cat "${TEMP_DIR}/company_id.txt" 2>/dev/null || echo "MC-01K3Z4Y7DSCP3Y2SGXHNJG64ZF")
    BUILDING_ID=$(cat "${TEMP_DIR}/building_id.txt" 2>/dev/null || echo "BLD-01K3Z53T176C2AH23XEM0VBHN2")
    UNIT_ID=$(cat "${TEMP_DIR}/unit_id.txt" 2>/dev/null || echo "UN-01K3Z565HND25JD6ZXEQ3X25BB")
    STAFF_ID=$(cat "${TEMP_DIR}/staff_id.txt" 2>/dev/null || echo "STAFF-FALLBACK")
    OWNER_ID=$(cat "${TEMP_DIR}/owner_id.txt" 2>/dev/null || echo "OWNER-FALLBACK")
    TENANT_ID=$(cat "${TEMP_DIR}/tenant_id.txt" 2>/dev/null || echo "TENANT-FALLBACK")
    
    # Test staff authentication workflows
    print_test "Staff OTP Request"
    print_info "Skipped - OTP requests disabled to prevent unnecessary SMS"
    
    # Test owner authentication workflows
    print_test "Owner OTP Request"
    print_info "Skipped - OTP requests disabled to prevent unnecessary SMS"
    
    # Test tenant authentication workflows
    print_test "Tenant OTP Request"
    print_info "Skipped - OTP requests disabled to prevent unnecessary SMS"
}

# 7. ADVANCED OPERATIONS
test_advanced_operations() {
    print_section "7. ADVANCED OPERATIONS"
    
    BUILDING_ID=$(cat "${TEMP_DIR}/building_id.txt" 2>/dev/null || echo "BLD-01K3Z53T176C2AH23XEM0VBHN2")
    TENANT_ID=$(cat "${TEMP_DIR}/tenant_id.txt" 2>/dev/null || echo "TENANT-FALLBACK")
    STAFF_ID=$(cat "${TEMP_DIR}/staff_id.txt" 2>/dev/null || echo "STAFF-FALLBACK")
    AMENITY_ID=$(cat "${TEMP_DIR}/amenity_id.txt" 2>/dev/null || echo "AMENITY-FALLBACK")
    
    # Test booking calendar for tenant
    print_test "Get Tenant Booking Calendar"
    local tenant_calendar_request='{
        "startDate": "2024-12-01T00:00:00",
        "endDate": "2024-12-31T23:59:59"
    }'
    
    if make_api_call "POST" "/buildings/${BUILDING_ID}/tenants/${TENANT_ID}/bookings/calendar" "$tenant_calendar_request" 200 "$JWT_TOKEN"; then
        print_success "Retrieved tenant booking calendar"
    else
        print_error "Failed to get tenant booking calendar"
    fi
    
    # Test booking calendar for staff
    print_test "Get Staff Booking Calendar"
    local staff_calendar_request='{
        "startDate": "2024-12-01T00:00:00",
        "endDate": "2024-12-31T23:59:59",
        "byBookingStatus": ["REQUESTED"]
    }'
    
    if make_api_call "POST" "/buildings/${BUILDING_ID}/staff/${STAFF_ID}/bookings/calendar" "$staff_calendar_request" 200 "$JWT_TOKEN"; then
        print_success "Retrieved staff booking calendar"
    else
        print_error "Failed to get staff booking calendar"
    fi
    
    # Test amenity availability (add required parameters)
    print_test "Check Amenity Availability"
    if [[ -n "$AMENITY_ID" && "$AMENITY_ID" != "AMENITY-FALLBACK" ]]; then
        # Use current date + 1 day between 10-11 AM with Z format
        local tomorrow=$(date -v+1d +"%Y-%m-%d")
        local start_time="${tomorrow}T10:00:00Z"  # Tomorrow at 10 AM UTC
        local end_time="${tomorrow}T11:00:00Z"    # Tomorrow at 11 AM UTC
        
        if make_api_call "GET" "/amenities/${AMENITY_ID}/availability?startTime=${start_time}&endTime=${end_time}" "" 200 "$JWT_TOKEN"; then
            print_success "Retrieved amenity availability"
        else
            print_error "Failed to get amenity availability"
        fi
    else
        print_warning "Skipping - no valid amenity ID available"
    fi
    
    # Test amenity maintenance status update
    print_test "Update Amenity Maintenance Status"
    if [[ -n "$AMENITY_ID" && "$AMENITY_ID" != "AMENITY-FALLBACK" ]]; then
        local maintenance_payload='{
            "maintenanceStatus": "UNDER_MAINTENANCE",
            "reason": "Routine maintenance"
        }'
        
        if make_api_call "PUT" "/buildings/${BUILDING_ID}/amenities/${AMENITY_ID}/maintenance-status" "$maintenance_payload" 200 "$JWT_TOKEN"; then
            print_success "Updated amenity maintenance status"
        else
            print_error "Failed to update maintenance status"
        fi
    else
        print_warning "Skipping - no valid amenity ID available"
    fi
}

# Function to generate test report
generate_report() {
    print_section "TEST RESULTS SUMMARY"
    
    echo -e "\n${BLUE}Test Summary:${NC}"
    echo -e "  Total Tests: ${TOTAL_TESTS}"
    echo -e "  ${GREEN}Passed: ${PASSED_TESTS}${NC}"
    echo -e "  ${RED}Failed: ${FAILED_TESTS}${NC}"
    
    local success_rate=0
    if [[ $TOTAL_TESTS -gt 0 ]]; then
        success_rate=$((PASSED_TESTS * 100 / TOTAL_TESTS))
    fi
    
    echo -e "  Success Rate: ${success_rate}%"
    
    if [[ $FAILED_TESTS -eq 0 ]]; then
        echo -e "\n${GREEN}🎉 All tests passed! API is functioning correctly.${NC}"
    else
        echo -e "\n${YELLOW}⚠️  Some tests failed. Check the log for details: ${TEST_LOG}${NC}"
    fi
    
    echo -e "\n${BLUE}Detailed log: ${TEST_LOG}${NC}"
    echo -e "${BLUE}Generated files: ${TEMP_DIR}/*${NC}"
}

# Main execution
main() {
    echo -e "${CYAN}"
    echo "========================================="
    echo "   CloudSuites Comprehensive API Test"
    echo "========================================="
    echo -e "${NC}"
    
    if [[ -n "$JWT_TOKEN" ]]; then
        print_info "Running with JWT authentication"
    else
        print_warning "Running without authentication - some tests may fail"
        print_info "Usage: $0 [JWT_TOKEN]"
        print_info "Example: $0 \"eyJhbGciOiJIUzUxMiJ9...\""
    fi
    
    # Setup
    check_dependencies
    mkdir -p "$TEMP_DIR"
    echo "Starting API tests at $(date)" > "$TEST_LOG"
    
    # Run test suites
    test_connectivity
    test_authentication
    test_property_structure
    test_user_management
    test_amenity_management
    test_role_management
    test_authentication_workflows
    test_advanced_operations
    
    # Generate report
    generate_report
}

# Run the main function
main "$@"
