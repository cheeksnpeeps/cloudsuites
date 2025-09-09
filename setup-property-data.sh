#!/bin/bash

# CloudSuites Property Setup Script
# This script creates a complete property management structure:
# 1. Creates a management company
# 2. Creates a building under that company
# 3. Creates a floor with multiple units in that building

# Note: Removed 'set -e' to provide better error handling

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost:8080/api/v1"
TEMP_DIR="/tmp/cloudsuites-setup"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if jq is installed
check_dependencies() {
    if ! command -v jq &> /dev/null; then
        print_error "jq is required but not installed. Please install jq first:"
        print_error "  macOS: brew install jq"
        print_error "  Ubuntu: sudo apt-get install jq"
        exit 1
    fi
}

# Function to validate token (optional)
validate_token() {
    local token=$1
    if [[ -z "$token" ]]; then
        print_warning "No token provided - running without authentication"
        print_status "This will work if the API endpoints are publicly accessible"
        return 0
    fi
    
    print_status "Validating token format..."
    if [[ ! "$token" =~ ^eyJ ]]; then
        print_warning "Token doesn't appear to be a valid JWT (should start with 'eyJ')"
    fi
}

# Function to test API connectivity
test_connectivity() {
    print_status "Testing API connectivity..."
    local response_code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/actuator/health")
    
    if [[ "$response_code" != "200" ]]; then
        print_error "Cannot connect to CloudSuites API at ${BASE_URL}"
        print_error "Please ensure the application is running on localhost:8080"
        exit 1
    fi
    
    print_success "API is accessible"
}

# Function to create management company
create_company() {
    local token=$1
    print_status "Creating management company..." >&2
    
    local company_payload='{
  "name": "Skyline Property Management",
  "website": "https://www.skylinepropertymanagement.com",
  "address": {
    "aptNumber": "Apt 101",
    "streetNumber": "123",
    "streetName": "Main St",
    "addressLine2": "Near Central Park",
    "city": "Toronto",
    "stateProvinceRegion": "Ontario",
    "postalCode": "M1M 1M1",
    "country": "Canada",
    "latitude": 43.6532,
    "longitude": -79.3832
  }
}'

    local response
    if [[ -n "$token" ]]; then
        response=$(curl -s -X POST \
            "${BASE_URL}/companies" \
            -H "accept: application/json" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${token}" \
            -d "$company_payload")
    else
        response=$(curl -s -X POST \
            "${BASE_URL}/companies" \
            -H "accept: application/json" \
            -H "Content-Type: application/json" \
            -d "$company_payload")
    fi
    
    # Check if request was successful
    if [[ -z "$response" ]] || echo "$response" | jq -e .error >/dev/null 2>&1; then
        print_error "Failed to create company" >&2
        echo "Response: $response" >&2
        exit 1
    fi
    
    # Extract company ID
    local company_id=$(echo "$response" | jq -r '.companyId')
    if [[ "$company_id" == "null" || -z "$company_id" ]]; then
        print_error "Could not extract company ID from response" >&2
        echo "Response: $response" >&2
        exit 1
    fi
    
    print_success "Created company: $(echo "$response" | jq -r '.name')" >&2
    print_status "Company ID: $company_id" >&2
    
    # Save company response for reference
    echo "$response" > "${TEMP_DIR}/company_response.json"
    echo "$company_id" > "${TEMP_DIR}/company_id.txt"
    
    echo "$company_id"
}

# Function to create building
create_building() {
    local token=$1
    local company_id=$2
    print_status "Creating building for company: $company_id" >&2
    
    local building_payload='{
  "name": "Skyline Tower",
  "address": {
    "aptNumber": "Apt 101",
    "streetNumber": "123",
    "streetName": "Main St",
    "addressLine2": "Near Central Park",
    "city": "Toronto",
    "stateProvinceRegion": "Ontario",
    "postalCode": "M1M 1M1",
    "country": "Canada",
    "latitude": 43.6532,
    "longitude": -79.3832
  },
  "totalFloors": 10,
  "yearBuilt": 1990
}'

    local response
    if [[ -n "$token" ]]; then
        response=$(curl -s -X POST \
            "${BASE_URL}/companies/${company_id}/buildings" \
            -H "accept: application/json" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${token}" \
            -d "$building_payload")
    else
        response=$(curl -s -X POST \
            "${BASE_URL}/companies/${company_id}/buildings" \
            -H "accept: application/json" \
            -H "Content-Type: application/json" \
            -d "$building_payload")
    fi
    
    # Check if request was successful
    if [[ -z "$response" ]] || echo "$response" | jq -e .error >/dev/null 2>&1; then
        print_error "Failed to create building" >&2
        echo "Response: $response" >&2
        exit 1
    fi
    
    # Extract building ID
    local building_id=$(echo "$response" | jq -r '.buildingId')
    if [[ "$building_id" == "null" || -z "$building_id" ]]; then
        print_error "Could not extract building ID from response" >&2
        echo "Response: $response" >&2
        exit 1
    fi
    
    print_success "Created building: $(echo "$response" | jq -r '.name')" >&2
    print_status "Building ID: $building_id" >&2
    
    # Save building response for reference
    echo "$response" > "${TEMP_DIR}/building_response.json"
    echo "$building_id" > "${TEMP_DIR}/building_id.txt"
    
    echo "$building_id"
}

# Function to create floor with units
create_floor() {
    local token=$1
    local building_id=$2
    print_status "Creating floor with units for building: $building_id" >&2
    
    local floor_payload='{
  "floorName": "Ground Floor",
  "floorNumber": 1,
  "units": [
    {
      "unitNumber": 101,
      "numberOfBedrooms": 2
    },
    {
      "unitNumber": 102,
      "numberOfBedrooms": 2
    },
    {
      "unitNumber": 103,
      "numberOfBedrooms": 2
    },
    {
      "unitNumber": 104,
      "numberOfBedrooms": 2
    }
  ]
}'

    local response
    if [[ -n "$token" ]]; then
        response=$(curl -s -X POST \
            "${BASE_URL}/buildings/${building_id}/floors" \
            -H "accept: application/json" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${token}" \
            -d "$floor_payload")
    else
        response=$(curl -s -X POST \
            "${BASE_URL}/buildings/${building_id}/floors" \
            -H "accept: application/json" \
            -H "Content-Type: application/json" \
            -d "$floor_payload")
    fi
    
    # Check if request was successful
    if [[ -z "$response" ]] || echo "$response" | jq -e .error >/dev/null 2>&1; then
        print_error "Failed to create floor" >&2
        echo "Response: $response" >&2
        exit 1
    fi
    
    # Extract floor ID
    local floor_id=$(echo "$response" | jq -r '.floorId')
    if [[ "$floor_id" == "null" || -z "$floor_id" ]]; then
        print_error "Could not extract floor ID from response" >&2
        echo "Response: $response" >&2
        exit 1
    fi
    
    local units_count=$(echo "$response" | jq -r '.units | length')
    
    print_success "Created floor: $(echo "$response" | jq -r '.floorName')" >&2
    print_status "Floor ID: $floor_id" >&2
    print_status "Units created: $units_count" >&2
    
    # Save floor response for reference
    echo "$response" > "${TEMP_DIR}/floor_response.json"
    echo "$floor_id" > "${TEMP_DIR}/floor_id.txt"
    
    # Extract unit IDs
    echo "$response" | jq -r '.units[].unitId' > "${TEMP_DIR}/unit_ids.txt"
    
    echo "$floor_id"
}

# Function to create summary report
create_summary() {
    print_status "Creating summary report..."
    
    local company_id=$(cat "${TEMP_DIR}/company_id.txt")
    local building_id=$(cat "${TEMP_DIR}/building_id.txt")
    local floor_id=$(cat "${TEMP_DIR}/floor_id.txt")
    
    cat > "${TEMP_DIR}/setup_summary.json" << EOF
{
  "summary": "CloudSuites Property Setup Complete",
  "timestamp": "$(date -u +"%Y-%m-%dT%H:%M:%SZ")",
  "company": $(cat "${TEMP_DIR}/company_response.json"),
  "building": $(cat "${TEMP_DIR}/building_response.json"),
  "floor": $(cat "${TEMP_DIR}/floor_response.json"),
  "extracted_ids": {
    "companyId": "$company_id",
    "buildingId": "$building_id",
    "floorId": "$floor_id",
    "unitIds": $(cat "${TEMP_DIR}/unit_ids.txt" | jq -R . | jq -s .)
  }
}
EOF

    print_success "Summary saved to: ${TEMP_DIR}/setup_summary.json"
}

# Function to display results
display_results() {
    echo
    echo "======================================================"
    echo -e "${GREEN}CloudSuites Property Setup Complete!${NC}"
    echo "======================================================"
    echo
    
    local company_name=$(cat "${TEMP_DIR}/company_response.json" | jq -r '.name')
    local building_name=$(cat "${TEMP_DIR}/building_response.json" | jq -r '.name')
    local floor_name=$(cat "${TEMP_DIR}/floor_response.json" | jq -r '.floorName')
    local units_count=$(cat "${TEMP_DIR}/unit_ids.txt" | wc -l | xargs)
    
    echo -e "${BLUE}Created Structure:${NC}"
    echo "  📢 Company: $company_name"
    echo "  🏢 Building: $building_name"
    echo "  🏠 Floor: $floor_name"
    echo "  🚪 Units: $units_count units"
    echo
    
    echo -e "${BLUE}Generated IDs:${NC}"
    echo "  Company ID:  $(cat "${TEMP_DIR}/company_id.txt")"
    echo "  Building ID: $(cat "${TEMP_DIR}/building_id.txt")"
    echo "  Floor ID:    $(cat "${TEMP_DIR}/floor_id.txt")"
    echo
    
    echo -e "${BLUE}Unit IDs:${NC}"
    while read -r unit_id; do
        echo "  • $unit_id"
    done < "${TEMP_DIR}/unit_ids.txt"
    
    echo
    echo -e "${YELLOW}Files created:${NC}"
    echo "  📄 Complete summary: ${TEMP_DIR}/setup_summary.json"
    echo "  📄 Individual responses: ${TEMP_DIR}/*_response.json"
    echo "  📄 Extracted IDs: ${TEMP_DIR}/*_id.txt"
    echo
}

# Main execution
main() {
    local token=$1
    
    echo
    echo "======================================================"
    echo -e "${BLUE}CloudSuites Property Setup Script${NC}"
    echo "======================================================"
    echo
    
    # Validate inputs and dependencies
    check_dependencies
    validate_token "$token"
    test_connectivity
    
    # Create temp directory
    mkdir -p "$TEMP_DIR"
    
    # Execute setup steps
    echo
    print_status "Starting property setup process..."
    echo
    
    # Step 1: Create company
    print_status "Step 1: Creating company..."
    local company_id
    company_id=$(create_company "$token" 2>/dev/null)
    if [[ -z "$company_id" ]]; then
        print_error "Failed to get company ID"
        exit 1
    fi
    print_status "Company created with ID: $company_id"
    echo
    
    # Step 2: Create building
    print_status "Step 2: Creating building..."
    local building_id
    building_id=$(create_building "$token" "$company_id" 2>/dev/null)
    if [[ -z "$building_id" ]]; then
        print_error "Failed to get building ID"
        exit 1
    fi
    print_status "Building created with ID: $building_id"
    echo
    
    # Step 3: Create floor with units
    print_status "Step 3: Creating floor with units..."
    local floor_id
    floor_id=$(create_floor "$token" "$building_id" 2>/dev/null)
    if [[ -z "$floor_id" ]]; then
        print_error "Failed to get floor ID"
        exit 1
    fi
    print_status "Floor created with ID: $floor_id"
    echo
    
    # Step 4: Create summary
    print_status "Step 4: Creating summary..."
    create_summary
    
    # Step 5: Display results
    display_results
}

# Check if token is provided
if [[ $# -eq 0 ]]; then
    print_warning "No JWT token provided - running without authentication"
    echo
    echo "Usage: $0 [JWT_TOKEN]"
    echo
    echo "Examples:"
    echo "  $0                                           # Run without authentication"
    echo "  $0 eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBRE0t...  # Run with authentication"
    echo
    echo "Note: Token is optional if API endpoints are publicly accessible"
    echo
    # Run with empty token
    main ""
else
    # Run main function with provided token
    main "$1"
fi
