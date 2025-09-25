// 📊 Suite de Tests de Performance - Helpdesk Mobile App
// Ubicación: app/src/test/java/com/helpdesk/performance/PerformanceTestSuite.kt

package com.helpdesk.performance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

/**
 * 🎯 Performance Test Suite
 * 
 * Mide tiempos de respuesta de APIs y operaciones críticas
 * según los targets definidos en metricas-calidad-performance.md
 */
@RunWith(AndroidJUnit4::class)
class PerformanceTestSuite {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockWebServer: MockWebServer
    
    @Mock
    private lateinit var apiClient: HelpdeskApiClient
    
    @Mock  
    private lateinit var ticketRepository: TicketRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ================================
    // 🌐 API PERFORMANCE TESTS
    // ================================

    /**
     * 🎯 Target: POST /auth/login < 800ms (p95)
     * Max Acceptable: < 1.5s
     */
    @Test
    fun `login_api_performance_under_800ms`() = runTest {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"token":"jwt.token.here","user":{"id":1,"email":"test@example.com"}}""")
                .setBodyDelay(600, java.util.concurrent.TimeUnit.MILLISECONDS) // Simular latencia
        )

        val credentials = LoginRequest("test@example.com", "password123")

        // Act & Measure
        val executionTime = measureTimeMillis {
            val response = apiClient.login(credentials)
            assertTrue(response.isSuccessful, "Login should be successful")
        }

        // Assert
        assertTrue(
            executionTime < 800,
            "❌ Login API took ${executionTime}ms, exceeds target of 800ms"
        )
        
        println("✅ Login API performance: ${executionTime}ms (target: <800ms)")
    }

    /**
     * 🎯 Target: GET /tickets < 1.2s (p95)
     * Max Acceptable: < 2.0s
     */
    @Test
    fun `get_tickets_api_performance_under_1200ms`() = runTest {
        // Arrange
        val ticketsJson = generateTicketsJson(50) // Simular 50 tickets
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(ticketsJson)
                .setBodyDelay(900, java.util.concurrent.TimeUnit.MILLISECONDS)
        )

        // Act & Measure
        val executionTime = measureTimeMillis {
            val response = apiClient.getTickets(page = 1, limit = 50)
            assertTrue(response.isSuccessful)
            assertTrue(response.data.size == 50)
        }

        // Assert
        assertTrue(
            executionTime < 1200,
            "❌ Get Tickets API took ${executionTime}ms, exceeds target of 1200ms"
        )
        
        println("✅ Get Tickets API performance: ${executionTime}ms (target: <1200ms)")
    }

    /**
     * 🎯 Target: POST /tickets < 1.0s (p95)
     * Max Acceptable: < 1.8s
     */
    @Test
    fun `create_ticket_api_performance_under_1000ms`() = runTest {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody("""{"id":123,"title":"Test Ticket","status":"OPEN","createdAt":"2024-01-01T00:00:00Z"}""")
                .setBodyDelay(700, java.util.concurrent.TimeUnit.MILLISECONDS)
        )

        val newTicket = CreateTicketRequest(
            title = "Performance Test Ticket",
            description = "Testing API performance under load",
            categoryId = 1
        )

        // Act & Measure
        val executionTime = measureTimeMillis {
            val response = apiClient.createTicket(newTicket)
            assertTrue(response.isSuccessful)
            assertTrue(response.data.id > 0)
        }

        // Assert
        assertTrue(
            executionTime < 1000,
            "❌ Create Ticket API took ${executionTime}ms, exceeds target of 1000ms"
        )
        
        println("✅ Create Ticket API performance: ${executionTime}ms (target: <1000ms)")
    }

    /**
     * 🎯 Target: GET /tickets/{id} < 600ms (p95)
     * Max Acceptable: < 1.2s
     */
    @Test
    fun `get_ticket_by_id_performance_under_600ms`() = runTest {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(generateTicketDetailJson(123))
                .setBodyDelay(400, java.util.concurrent.TimeUnit.MILLISECONDS)
        )

        // Act & Measure
        val executionTime = measureTimeMillis {
            val response = apiClient.getTicketById(123)
            assertTrue(response.isSuccessful)
            assertTrue(response.data.id == 123)
        }

        // Assert
        assertTrue(
            executionTime < 600,
            "❌ Get Ticket by ID took ${executionTime}ms, exceeds target of 600ms"
        )
        
        println("✅ Get Ticket by ID performance: ${executionTime}ms (target: <600ms)")
    }

    /**
     * 🎯 Target: GET /categories < 400ms (p95)
     * Max Acceptable: < 800ms
     */
    @Test
    fun `get_categories_performance_under_400ms`() = runTest {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""[{"id":1,"name":"Bug"},{"id":2,"name":"Feature"},{"id":3,"name":"Support"}]""")
                .setBodyDelay(250, java.util.concurrent.TimeUnit.MILLISECONDS)
        )

        // Act & Measure
        val executionTime = measureTimeMillis {
            val response = apiClient.getCategories()
            assertTrue(response.isSuccessful)
            assertTrue(response.data.isNotEmpty())
        }

        // Assert
        assertTrue(
            executionTime < 400,
            "❌ Get Categories took ${executionTime}ms, exceeds target of 400ms"
        )
        
        println("✅ Get Categories performance: ${executionTime}ms (target: <400ms)")
    }

    // ================================
    // 💾 DATABASE PERFORMANCE TESTS
    // ================================

    /**
     * 🎯 Target: Local query < 100ms
     * Database operations should be fast
     */
    @Test
    fun `local_database_query_under_100ms`() = runTest {
        // Arrange
        val testTickets = generateTestTickets(100)
        ticketRepository.insertTickets(testTickets)

        // Act & Measure
        val executionTime = measureTimeMillis {
            val tickets = ticketRepository.getAllTickets()
            assertTrue(tickets.size == 100)
        }

        // Assert
        assertTrue(
            executionTime < 100,
            "❌ Local DB query took ${executionTime}ms, exceeds target of 100ms"
        )
        
        println("✅ Local DB query performance: ${executionTime}ms (target: <100ms)")
    }

    /**
     * 🎯 Target: Bulk insert < 50ms
     * Database bulk operations should be efficient
     */
    @Test
    fun `database_bulk_insert_under_50ms`() = runTest {
        // Arrange
        val testTickets = generateTestTickets(50)

        // Act & Measure
        val executionTime = measureTimeMillis {
            ticketRepository.insertTickets(testTickets)
        }

        // Assert
        assertTrue(
            executionTime < 50,
            "❌ Bulk insert took ${executionTime}ms, exceeds target of 50ms"
        )
        
        println("✅ Bulk insert performance: ${executionTime}ms (target: <50ms)")
    }

    // ================================
    // 🔄 SYNC PERFORMANCE TESTS
    // ================================

    /**
     * 🎯 Target: Sync 100 tickets < 5s
     * Max Acceptable: < 10s
     */
    @Test
    fun `sync_tickets_performance_under_5_seconds`() = runTest {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(generateTicketsJson(100))
                .setBodyDelay(3500, java.util.concurrent.TimeUnit.MILLISECONDS)
        )

        // Act & Measure
        val executionTime = measureTimeMillis {
            val syncResult = ticketRepository.syncTickets()
            assertTrue(syncResult.isSuccess)
            assertTrue(syncResult.syncedCount == 100)
        }

        // Assert
        assertTrue(
            executionTime < 5000,
            "❌ Sync 100 tickets took ${executionTime}ms, exceeds target of 5000ms"
        )
        
        println("✅ Sync tickets performance: ${executionTime}ms (target: <5000ms)")
    }

    // ================================
    // 📊 SEARCH PERFORMANCE TESTS
    // ================================

    /**
     * 🎯 Target: Search response < 500ms
     * Real-time search should be responsive
     */
    @Test
    fun `search_tickets_performance_under_500ms`() = runTest {
        // Arrange
        val searchQuery = "bug"
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(generateSearchResultsJson(searchQuery, 25))
                .setBodyDelay(300, java.util.concurrent.TimeUnit.MILLISECONDS)
        )

        // Act & Measure
        val executionTime = measureTimeMillis {
            val searchResults = apiClient.searchTickets(searchQuery)
            assertTrue(searchResults.isSuccessful)
            assertTrue(searchResults.data.isNotEmpty())
        }

        // Assert
        assertTrue(
            executionTime < 500,
            "❌ Search took ${executionTime}ms, exceeds target of 500ms"
        )
        
        println("✅ Search performance: ${executionTime}ms (target: <500ms)")
    }

    // ================================
    // 🚀 PERFORMANCE BAJO DIFERENTES CONDICIONES
    // ================================

    /**
     * Test performance con conexión lenta simulada (3G)
     */
    @Test
    fun `api_performance_on_slow_connection`() = runTest {
        // Arrange - Simular 3G (latencia alta)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"token":"jwt.token"}""")
                .setBodyDelay(2000, java.util.concurrent.TimeUnit.MILLISECONDS) // 2s latencia
        )

        // Act & Measure
        val executionTime = measureTimeMillis {
            val response = apiClient.login(LoginRequest("test@example.com", "password"))
            assertTrue(response.isSuccessful)
        }

        // Assert - Target más relajado para conexión lenta
        assertTrue(
            executionTime < 5000,
            "❌ Login on slow connection took ${executionTime}ms, exceeds 5s threshold"
        )
        
        println("🐌 Slow connection performance: ${executionTime}ms (3G target: <5000ms)")
    }

    /**
     * Test de carga - múltiples requests concurrentes
     */
    @Test
    fun `concurrent_api_requests_performance`() = runTest {
        // Arrange
        repeat(5) {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(generateTicketDetailJson(it))
            )
        }

        // Act & Measure
        val executionTime = measureTimeMillis {
            val jobs = (1..5).map { ticketId ->
                async {
                    apiClient.getTicketById(ticketId)
                }
            }
            val responses = jobs.awaitAll()
            assertTrue(responses.all { it.isSuccessful })
        }

        // Assert
        assertTrue(
            executionTime < 2000,
            "❌ Concurrent requests took ${executionTime}ms, exceeds 2s threshold"
        )
        
        println("🔄 Concurrent requests performance: ${executionTime}ms (target: <2000ms)")
    }

    // ================================
    // 🔧 HELPER METHODS
    // ================================

    private fun generateTicketsJson(count: Int): String {
        val tickets = (1..count).map { id ->
            """
            {
                "id": $id,
                "title": "Test Ticket $id",
                "description": "Description for ticket $id",
                "status": "OPEN",
                "categoryId": ${(id % 3) + 1},
                "createdAt": "2024-01-01T00:00:00Z"
            }
            """.trimIndent()
        }
        return "[${tickets.joinToString(",")}]"
    }

    private fun generateTicketDetailJson(id: Int): String {
        return """
        {
            "id": $id,
            "title": "Detailed Ticket $id",
            "description": "Full description for ticket $id with more details",
            "status": "OPEN",
            "categoryId": 1,
            "creatorId": 1,
            "assigneeId": null,
            "createdAt": "2024-01-01T00:00:00Z",
            "updatedAt": "2024-01-01T00:00:00Z",
            "comments": [],
            "attachments": []
        }
        """.trimIndent()
    }

    private fun generateSearchResultsJson(query: String, count: Int): String {
        val results = (1..count).map { id ->
            """
            {
                "id": $id,
                "title": "Bug in $query functionality $id",
                "description": "Found $query related issue",
                "status": "OPEN",
                "relevanceScore": 0.8
            }
            """.trimIndent()
        }
        return """{"results": [${results.joinToString(",")}], "totalCount": $count}"""
    }

    private fun generateTestTickets(count: Int): List<Ticket> {
        return (1..count).map { id ->
            Ticket(
                id = id,
                title = "Test Ticket $id",
                description = "Test description $id",
                status = TicketStatus.OPEN,
                categoryId = (id % 3) + 1,
                creatorId = 1,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}

// ================================
// 📊 PERFORMANCE TEST RUNNER
// ================================

/**
 * Runner para ejecutar todos los tests de performance
 * y generar reporte consolidado
 */
class PerformanceTestRunner {
    
    fun runAllPerformanceTests(): PerformanceReport {
        val startTime = System.currentTimeMillis()
        val results = mutableMapOf<String, TestResult>()
        
        // Aquí se ejecutarían todos los tests y se recopilarían resultados
        
        return PerformanceReport(
            executionTime = System.currentTimeMillis() - startTime,
            testResults = results,
            overallScore = calculatePerformanceScore(results)
        )
    }
    
    private fun calculatePerformanceScore(results: Map<String, TestResult>): Int {
        // Lógica para calcular score basado en resultados
        val passedTests = results.values.count { it.passed }
        val totalTests = results.size
        return ((passedTests.toDouble() / totalTests) * 100).toInt()
    }
}

data class PerformanceReport(
    val executionTime: Long,
    val testResults: Map<String, TestResult>, 
    val overallScore: Int
)

data class TestResult(
    val testName: String,
    val executionTime: Long,
    val targetTime: Long,
    val passed: Boolean,
    val message: String
)
