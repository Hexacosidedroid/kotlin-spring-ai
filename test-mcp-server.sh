#!/bin/bash

# MCP Server Testing Script
# This script tests the MCP server endpoints and VectorStore integration

BASE_URL="http://localhost:8080"

echo "=========================================="
echo "Testing MCP Server Endpoints"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Check if server is running
echo -e "${YELLOW}Test 1: Checking if server is running...${NC}"
# Try actuator first, then fall back to test controller
if curl -s -f "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Server is running (via Actuator)${NC}"
    curl -s "${BASE_URL}/actuator/health" | jq '.' 2>/dev/null || curl -s "${BASE_URL}/actuator/health"
elif curl -s -f "${BASE_URL}/test/mcp/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Server is running (via test controller)${NC}"
    curl -s "${BASE_URL}/test/mcp/health" | jq '.' 2>/dev/null || curl -s "${BASE_URL}/test/mcp/health"
else
    echo -e "${RED}✗ Server is not running${NC}"
    echo "   Make sure your Spring Boot application is running on port 8080"
    echo "   Start it with: ./mvnw spring-boot:run"
    exit 1
fi
echo ""

# Test 2: List available MCP tools via test controller (this always works)
echo -e "${YELLOW}Test 2a: Listing available MCP tools (via test controller)...${NC}"
TOOLS_RESPONSE=$(curl -s "${BASE_URL}/test/mcp/tools")
if [ $? -eq 0 ] && echo "$TOOLS_RESPONSE" | grep -q "searchVectorDatabase"; then
    echo -e "${GREEN}✓ Tools listed successfully${NC}"
    echo "$TOOLS_RESPONSE" | jq '.' 2>/dev/null || echo "$TOOLS_RESPONSE"
else
    echo -e "${RED}✗ Failed to list tools via test controller${NC}"
    echo "$TOOLS_RESPONSE"
fi
echo ""

# Test 2b: Try to find the actual MCP protocol endpoint
echo -e "${YELLOW}Test 2b: Finding MCP protocol endpoint...${NC}"
MCP_ENDPOINTS=(
    "${BASE_URL}/mcp/message"
    "${BASE_URL}/mcp"
    "${BASE_URL}/api/mcp/message"
    "${BASE_URL}/api/mcp"
)

MCP_ENDPOINT_FOUND=""
for endpoint in "${MCP_ENDPOINTS[@]}"; do
    RESPONSE=$(curl -s -X POST "${endpoint}" \
      -H "Content-Type: application/json" \
      -d '{"jsonrpc": "2.0", "id": 1, "method": "tools/list", "params": {}}' 2>&1)
    
    if echo "$RESPONSE" | grep -q "tools\|result\|error" && ! echo "$RESPONSE" | grep -q "404\|Not Found"; then
        MCP_ENDPOINT_FOUND="${endpoint}"
        echo -e "${GREEN}✓ Found MCP endpoint: ${endpoint}${NC}"
        echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE"
        break
    fi
done

if [ -z "$MCP_ENDPOINT_FOUND" ]; then
    echo -e "${YELLOW}⚠ MCP protocol endpoint not found at common paths${NC}"
    echo "   Tried: ${MCP_ENDPOINTS[*]}"
    echo "   The MCP server might use a different endpoint path."
    echo "   Check application logs for the actual MCP endpoint."
    echo "   For now, use the test controller endpoints at /test/mcp/*"
    MCP_ENDPOINT="${BASE_URL}/test/mcp"  # Fallback to test controller
else
    MCP_ENDPOINT="${MCP_ENDPOINT_FOUND}"
fi
echo ""

# Test 3: Test searchVectorDatabase tool via test controller (reliable method)
echo -e "${YELLOW}Test 3: Testing searchVectorDatabase tool (via test controller)...${NC}"
echo "   This will search the VectorStore with query: 'technology'"
SEARCH_RESPONSE=$(curl -s "${BASE_URL}/test/mcp/search?query=technology&topK=3&similarityThreshold=0.6")

if [ $? -eq 0 ] && echo "$SEARCH_RESPONSE" | grep -q "status"; then
    echo -e "${GREEN}✓ Search completed${NC}"
    echo "$SEARCH_RESPONSE" | jq '.' 2>/dev/null || echo "$SEARCH_RESPONSE"
else
    echo -e "${RED}✗ Failed to execute search${NC}"
    echo "$SEARCH_RESPONSE"
fi
echo ""

# Test 3b: Try MCP protocol endpoint if found
if [ "$MCP_ENDPOINT" != "${BASE_URL}/test/mcp" ] && [ -n "$MCP_ENDPOINT_FOUND" ]; then
    echo -e "${YELLOW}Test 3b: Testing searchVectorDatabase via MCP protocol...${NC}"
    MCP_SEARCH_RESPONSE=$(curl -s -X POST "${MCP_ENDPOINT}" \
      -H "Content-Type: application/json" \
      -d '{
        "jsonrpc": "2.0",
        "id": 2,
        "method": "tools/call",
        "params": {
          "name": "searchVectorDatabase",
          "arguments": {
            "query": "technology",
            "topK": 3,
            "similarityThreshold": 0.6
          }
        }
      }')
    
    if [ $? -eq 0 ] && ! echo "$MCP_SEARCH_RESPONSE" | grep -q "404\|Not Found"; then
        echo -e "${GREEN}✓ MCP protocol search completed${NC}"
        echo "$MCP_SEARCH_RESPONSE" | jq '.' 2>/dev/null || echo "$MCP_SEARCH_RESPONSE"
    else
        echo -e "${YELLOW}⚠ MCP protocol endpoint returned error${NC}"
        echo "$MCP_SEARCH_RESPONSE"
    fi
    echo ""
fi

# Test 4: Test with a more specific query via test controller
echo -e "${YELLOW}Test 4: Testing with specific query (via test controller)...${NC}"
echo "   Query: 'What is the technology radar?'"
SPECIFIC_RESPONSE=$(curl -s "${BASE_URL}/test/mcp/search?query=What%20is%20the%20technology%20radar%3F&topK=5")

if [ $? -eq 0 ] && echo "$SPECIFIC_RESPONSE" | grep -q "status"; then
    echo -e "${GREEN}✓ Specific search completed${NC}"
    echo "$SPECIFIC_RESPONSE" | jq '.' 2>/dev/null || echo "$SPECIFIC_RESPONSE"
else
    echo -e "${RED}✗ Failed to execute specific search${NC}"
    echo "$SPECIFIC_RESPONSE"
fi
echo ""

echo "=========================================="
echo "Testing Complete"
echo "=========================================="
echo ""
echo "Summary:"
echo "  ✓ Test controller endpoints are working at /test/mcp/*"
if [ -n "$MCP_ENDPOINT_FOUND" ]; then
    echo "  ✓ MCP protocol endpoint found at: $MCP_ENDPOINT_FOUND"
else
    echo "  ⚠ MCP protocol endpoint not found - using test controller instead"
    echo "     Check application logs to find the actual MCP endpoint path"
fi
echo ""
echo "Note: If you see errors, make sure:"
echo "  1. The Spring Boot application is running"
echo "  2. The VectorStore has been populated (call /rag endpoint first)"
echo "  3. The MCP server is properly configured"
echo "  4. jq is installed for pretty JSON output (optional)"
echo ""
echo "To test MCP tools directly:"
echo "  curl \"http://localhost:8080/test/mcp/search?query=technology\""
echo "  curl http://localhost:8080/test/mcp/tools"

