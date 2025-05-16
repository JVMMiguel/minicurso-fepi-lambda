package send.email.function

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import send.email.function.domain.request.PurchaseDataRequest
import send.email.function.exceptions.BadRequestException
import send.email.function.exceptions.S3StorageException
import send.email.function.exceptions.SendEmailFunctionException
import send.email.function.exceptions.SesEmailException
import send.email.function.services.EmailService
import send.email.function.services.StorageService
import send.email.function.utils.LoggerUtils
import send.email.function.utils.RequestValidator

class Handler(
    private val emailService: EmailService = EmailService(),
    private val storageService: StorageService = StorageService(),
    private val mapper: ObjectMapper = jacksonObjectMapper().registerModules().apply {
        registerModule(JavaTimeModule())
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    },
    private val sourceEmail: String = System.getenv("SOURCE_EMAIL")
        ?: throw IllegalStateException("SOURCE_EMAIL environment variable is required")
) {
    companion object {
        private val HEADERS = mapOf("Content-Type" to "application/json")
    }

    fun handleRequest(input: Map<String, Any>?, context: Context): APIGatewayProxyResponseEvent {
        val logger = LoggerUtils(context)
        // comentário para teste do github actions
        return try {
            logger.info("Processing incoming request")

            val purchaseData = RequestValidator(mapper).validateRequest(input, PurchaseDataRequest::class.java, logger)

            emailService.sendPurchaseConfirmationEmail(purchaseData, sourceEmail)
            logger.info("Email sent successfully to ${purchaseData.customerEmail}")

            val logFile = purchaseData.toSendEmailLogFile()
            storageService.saveEmailLogFile(logFile, logger)
            logger.info("Log file successfully saved for order ${logFile.orderId}")

            createSuccessResponse(logFile.orderId, logFile.customerEmail)
        } catch (e: Exception) {
            handleException(e, logger)
        }
    }

    private fun createSuccessResponse(orderId: String, customerEmail: String): APIGatewayProxyResponseEvent =
        APIGatewayProxyResponseEvent()
            .withStatusCode(201)
            .withBody(
                mapper.writeValueAsString(
                    mapOf(
                        "message" to "Email sent successfully",
                        "orderId" to orderId,
                        "customerEmail" to customerEmail
                    )
                )
            )
            .withHeaders(HEADERS)

    private fun createErrorResponse(statusCode: Int, message: String?, errorCode: String? = null): APIGatewayProxyResponseEvent =
        APIGatewayProxyResponseEvent()
            .withStatusCode(statusCode)
            .withBody(
                mapper.writeValueAsString(
                    mapOf(
                        "message" to (message ?: "Unknown error"),
                        "statusCode" to statusCode,
                        "errorCode" to errorCode
                    ).filterValues { it != null }
                )
            )
            .withHeaders(HEADERS)

    private fun handleException(e: Exception, logger: LoggerUtils): APIGatewayProxyResponseEvent {
        logger.error("Error handling request", e)
        return when (e) {
            is BadRequestException -> createErrorResponse(400, e.message, e.errorCode)
            is SesEmailException -> createErrorResponse(502, e.message, e.errorCode)
            is S3StorageException -> createErrorResponse(500, e.message, e.errorCode)
            is SendEmailFunctionException -> createErrorResponse(e.statusCode, e.message, e.errorCode)
            else -> createErrorResponse(500, "Internal server error")
        }
    }
}
