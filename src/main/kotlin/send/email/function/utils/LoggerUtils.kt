package send.email.function.utils

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger

class LoggerUtils(context: Context) {
    private val logger: LambdaLogger = context.logger

    fun info(message: String) {
        logger.log("[INFO] $message")
    }

    fun error(message: String, e: Exception) {
        logger.log("[ERROR] " + message + ": " + e.message)
    }
}
