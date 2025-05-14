package send.email.function.exceptions

class S3StorageException(
    message: String,
    errorCode: String? = null
) : SendEmailFunctionException(500, message, errorCode)
