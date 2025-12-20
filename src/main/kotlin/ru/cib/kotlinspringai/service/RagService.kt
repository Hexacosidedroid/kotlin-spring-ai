package ru.cib.kotlinspringai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.document.Document
import org.springaicommunity.mcp.annotation.McpTool
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

@Service
class RagService(
    private val resourceLoader: ResourceLoader,
    private val vectorStore: VectorStore,
    private val chatClient: ChatClient
) {

    fun loadPdf(pdfPath: String = "classpath:tr_technology_radar_vol_32_en.pdf"): List<Document> {
        val resource = resourceLoader.getResource(pdfPath)
        val pdfReader = PagePdfDocumentReader(resource)
        val documents = pdfReader.get()
        val tokenTextSplitter = TokenTextSplitter()
        return tokenTextSplitter.apply(documents)
    }

    @McpTool(
        name = "loadToVectorDatabase",
        description = "Loads a PDF document into the vector database for RAG (Retrieval Augmented Generation) search. The PDF is split into chunks and embedded for semantic search."
    )
    fun loadToVectorDatabase(pdfPath: String = "classpath:tr_technology_radar_vol_32_en.pdf") {
        val documents = loadPdf(pdfPath)
        val cleanedDocuments = documents.map { document ->
            val cleanedContent = document.text?.replace("\u0000", "")
            val cleanedMetadata = document.metadata.mapValues { (_, value) ->
                when (value) {
                    is String -> value.replace("\u0000", "")
                    else -> value
                }
            }
            Document(document.id, cleanedContent!!, cleanedMetadata)
        }
        vectorStore.add(cleanedDocuments)
    }

    @McpTool(
        name = "searchVectorDatabase",
        description = "Searches the vector database using semantic similarity and returns an AI-generated answer based on the retrieved context. This is the main RAG search functionality."
    )
    fun searchVectorDatabase(
        query: String,
        topK: Int = 5,
        similarityThreshold: Double = 0.6,
        conversationId: String? = null
    ): String? {
        val searchRequest = SearchRequest.builder()
            .query(query)
            .topK(topK)
            .similarityThreshold(similarityThreshold)
            .build()
        
        val documents = vectorStore.similaritySearch(searchRequest)
        
        val context = documents.joinToString("\n\n") { doc ->
            "Content: ${doc.text}\nMetadata: ${doc.metadata}"
        }

        println(context)
        
        val systemPrompt = """
            You are an AI assistant that answers questions based on the provided context from a vector database search.
            
            Use the search results as the primary source of truth. If the answer cannot be found in the provided context,
            you may use your general knowledge, but clearly indicate when you are extrapolating beyond the given data.
            
            Context from vector database:
            $context
        """.trimIndent()
        
        return chatClient
            .prompt()
            .system(systemPrompt)
            .user(query)
            .advisors {
                if (conversationId != null) {
                    it.param(ChatMemory.CONVERSATION_ID, conversationId)
                }
            }
            .call()
            .content()
    }
}