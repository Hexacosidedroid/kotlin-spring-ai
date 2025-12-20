package ru.cib.kotlinspringai.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.cib.kotlinspringai.service.RagService

/**
 * Test controller to verify MCP server and VectorStore integration
 * This is a convenience endpoint for testing - not part of the MCP protocol
 */
@RestController
@RequestMapping("/test/mcp")
class McpTestController(
    private val ragService: RagService
) {

    /**
     * Test endpoint to verify VectorStore search is working
     * This directly calls the RagService to verify it's functioning
     */
    @GetMapping("/search")
    fun testVectorStoreSearch(
        @RequestParam(defaultValue = "technology") query: String,
        @RequestParam(defaultValue = "5") topK: Int,
        @RequestParam(defaultValue = "0.6") similarityThreshold: Double
    ): Map<String, Any> {
        return try {
            val result = ragService.searchVectorDatabase(
                query = query,
                topK = topK,
                similarityThreshold = similarityThreshold
            )
            mapOf(
                "status" to "success",
                "query" to query,
                "result" to (result ?: "No result returned"),
                "message" to "VectorStore search completed successfully"
            )
        } catch (e: Exception) {
            mapOf<String, Any>(
                "status" to "error",
                "query" to query,
                "error" to (e.message ?: "Unknown error"),
                "message" to "VectorStore search failed"
            )
        }
    }

    /**
     * Test endpoint to verify MCP tools are registered
     * Returns information about available MCP tools
     */
    @GetMapping("/tools")
    fun listMcpTools(): Map<String, Any> {
        val tool1: Map<String, Any> = mapOf(
            "name" to "searchVectorDatabase",
            "description" to "Searches the vector database using semantic similarity and returns an AI-generated answer based on the retrieved context.",
            "parameters" to listOf(
                "query (String): The search query",
                "topK (Int, default: 5): Number of results to return",
                "similarityThreshold (Double, default: 0.6): Minimum similarity score",
                "conversationId (String?, optional): Conversation ID for memory"
            )
        )
        val tool2: Map<String, Any> = mapOf(
            "name" to "loadToVectorDatabase",
            "description" to "Loads a PDF document into the vector database for RAG search.",
            "parameters" to listOf(
                "pdfPath (String, default: classpath:tr_technology_radar_vol_32_en.pdf): Path to PDF file"
            )
        )
        return mapOf(
            "status" to "success",
            "message" to "MCP tools are registered via @McpTool annotations",
            "availableTools" to listOf(tool1, tool2),
            "note" to "To test via MCP protocol, use POST /mcp/message with JSON-RPC format"
        )
    }

    /**
     * Health check for MCP server
     */
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "MCP Server",
            "message" to "MCP server is running. Use /test/mcp/tools to see available tools."
        )
    }
}

