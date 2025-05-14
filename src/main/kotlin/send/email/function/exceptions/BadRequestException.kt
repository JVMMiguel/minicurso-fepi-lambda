package send.email.function.exceptions

class BadRequestException(
    message: String,
    errorCode: String? = null
) : SendEmailFunctionException(400, message, errorCode)
