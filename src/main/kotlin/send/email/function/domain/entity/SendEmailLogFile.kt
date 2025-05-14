package send.email.function.domain.entity

import java.time.LocalDateTime

data class SendEmailLogFile(
    val orderId: String,
    val customerEmail: String,
    val emailSentAt: LocalDateTime
)
