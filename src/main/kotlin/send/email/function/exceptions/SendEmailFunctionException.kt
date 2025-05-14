package send.email.function.exceptions

sealed class SendEmailFunctionException(
    val statusCode: Int,
    override val message: String,
    val errorCode: String? = null
) : RuntimeException(message)
