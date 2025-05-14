package send.email.function.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import send.email.function.domain.entity.SendEmailLogFile
import send.email.function.exceptions.S3StorageException
import send.email.function.utils.LoggerUtils
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

class StorageService(
    private val s3Client: S3Client = S3Client.builder().build(),
    private val bucketName: String = System.getenv("BUCKET_NAME"),
    private val mapper: ObjectMapper = jacksonObjectMapper().registerModules().apply {
        registerModule(JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
) {
    fun saveEmailLogFile(logFile: SendEmailLogFile, logger: LoggerUtils) {
        try {
            val fileName = "${logFile.orderId}.json"
            val logFileJson = mapper.writeValueAsString(logFile)

            val request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("application/json")
                .build()

            s3Client.putObject(request, RequestBody.fromString(logFileJson))
        } catch (e: SdkException) {
            logger.error("Failed to save log file to S3", e)
            throw S3StorageException("Error saving log to S3", "S3_STORAGE_ERROR")
        } catch (e: Exception) {
            logger.error("Unexpected error saving log file", e)
            throw S3StorageException("Unexpected error saving log to S3", "UNEXPECTED_STORAGE_ERROR")
        }
    }
}
