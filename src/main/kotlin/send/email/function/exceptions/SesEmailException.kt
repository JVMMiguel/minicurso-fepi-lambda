package send.email.function.exceptions

class SesEmailException(
    message: String,
    errorCode: String? = null
) : SendEmailFunctionException(502, message, errorCode)
