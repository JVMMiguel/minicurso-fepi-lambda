package send.email.function.utils

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import send.email.function.exceptions.BadRequestException
import javax.validation.Validation
import javax.validation.Validator

class RequestValidator(
    private val mapper: ObjectMapper
) {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    fun <T : Any> validateRequest(input: Map<String, Any>?, clazz: Class<T>, logger: LoggerUtils): T {
        val body = input?.get("body")?.toString()
            ?: throw BadRequestException("Request body is missing", "REQUEST_BODY_MISSING")

        try {
            val request = mapper.readValue(body, clazz)
            val violations = validator.validate(request)
            if (violations.isNotEmpty()) {
                val errorDetails = violations.joinToString("; ") { violation ->
                    "${violation.propertyPath}: ${violation.message}"
                }
                throw BadRequestException("Validation failed: $errorDetails", "VALIDATION_ERROR")
            }
            return request
        } catch (e: JsonMappingException) {
            val errorDetails = e.path.joinToString(", ") { it.fieldName ?: "Unknown field" }
            throw BadRequestException("Invalid or missing fields: $errorDetails", "MAPPING_ERROR")
        } catch (e: BadRequestException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to process JSON body", e)
            throw BadRequestException("Invalid JSON body", "INVALID_JSON")
        }
    }
}
