# MCP Server Testing Guide

This guide explains how to verify that your MCP server is working and correctly searching the VectorStore.

## Prerequisites

1. **Start your Spring Boot application**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Load data into VectorStore** (if not already done)
   ```bash
   curl http://localhost:8080/rag
   ```

## Testing Methods

### Method 1: Using the Test Controller (Easiest)

The `McpTestController` provides convenient REST endpoints for testing:

#### 1. Check MCP Server Health
```bash
curl http://localhost:8080/test/mcp/health
```

#### 2. List Available MCP Tools
```bash
curl http://localhost:8080/test/mcp/tools
```

#### 3. Test VectorStore Search Directly
```bash
# Basic search
curl "http://localhost:8080/test/mcp/search?query=technology"

# Advanced search with parameters
curl "http://localhost:8080/test/mcp/search?query=What%20is%20the%20technology%20radar?&topK=5&similarityThreshold=0.6"
```

### Method 2: Using the Test Script

Run the automated test script:

```bash
./test-mcp-server.sh
```

This script will:
- Check if the server is running
- List available MCP tools
- Test the `searchVectorDatabase` tool with sample queries
- Display results in JSON format

**Note:** Install `jq` for pretty JSON output:
```bash
# macOS
brew install jq

# Linux
sudo apt-get install jq
```

### Method 3: Direct MCP Protocol Testing (JSON-RPC)

The MCP server uses JSON-RPC 2.0 protocol. Test it directly:

#### List Available Tools
```bash
curl -X POST http://localhost:8080/mcp/message \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "tools/list",
    "params": {}
  }'
```

#### Call searchVectorDatabase Tool
```bash
curl -X POST http://localhost:8080/mcp/message \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/call",
    "params": {
      "name": "searchVectorDatabase",
      "arguments": {
        "query": "technology",
        "topK": 5,
        "similarityThreshold": 0.6
      }
    }
  }'
```

#### Call loadToVectorDatabase Tool
```bash
curl -X POST http://localhost:8080/mcp/message \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
      "name": "loadToVectorDatabase",
      "arguments": {
        "pdfPath": "classpath:tr_technology_radar_vol_32_en.pdf"
      }
    }
  }'
```

## Expected Results

### Successful Response from searchVectorDatabase

A successful response should look like:
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "result": {
    "content": [
      {
        "type": "text",
        "text": "Answer based on vector database search results..."
      }
    ]
  }
}
```

### Successful Response from Test Controller

```json
{
  "status": "success",
  "query": "technology",
  "result": "Answer based on vector database search...",
  "message": "VectorStore search completed successfully"
}
```

## Troubleshooting

### Issue: "Server is not running"
- **Solution:** Make sure your Spring Boot application is running on port 8080
- Check: `curl http://localhost:8080/actuator/health`

### Issue: "No results returned" or empty response
- **Solution:** Make sure you've loaded data into the VectorStore
- Run: `curl http://localhost:8080/rag` to load the PDF

### Issue: "Tool not found" or 404 error
- **Solution:** Check that:
  1. The `@McpTool` annotations are correctly placed on RagService methods
  2. The application has compiled successfully
  3. The MCP server is properly configured in `application.properties`

### Issue: "Connection refused" on /mcp/message
- **Solution:** The MCP endpoint path might be different. Check:
  1. Application logs for MCP server startup messages
  2. Try alternative paths like `/mcp`, `/api/mcp`, or check actuator endpoints
  3. Verify `spring.ai.mcp.server.protocol=STREAMABLE` in application.properties

## Verifying VectorStore Integration

To verify that the search is actually querying the VectorStore:

1. **Check Application Logs**
   - Look for debug messages showing vector search operations
   - You should see logs from `RagService.searchVectorDatabase` method
   - The `println(context)` in the code will show retrieved documents

2. **Monitor Database Queries**
   - If using PostgreSQL with pgvector, check the database logs
   - Vector similarity search queries should appear

3. **Test with Known Data**
   - Load a specific PDF with known content
   - Search for terms you know exist in that PDF
   - Verify the response contains relevant information

## Using MCP Clients

You can also test using official MCP clients:

1. **MCP Inspector** - A tool for testing MCP servers
2. **Claude Desktop** - Can connect to MCP servers
3. **Custom MCP Client** - Build your own using the MCP SDK

## Additional Resources

- Spring AI MCP Documentation: https://docs.spring.io/spring-ai/reference/1.1/api/mcp/
- MCP Protocol Specification: https://modelcontextprotocol.io/

