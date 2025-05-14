package send.email.function.domain.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import send.email.function.domain.entity.SendEmailLogFile
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PurchaseDataRequest(
    @field:NotBlank(message = "Order number cannot be null or blank")
    val orderId: String,

    @field:NotBlank(message = "Product name cannot be null or blank")
    val productName: String,

    @field:NotBlank(message = "Amount paid cannot be null or blank")
    val amountPaid: String,

    @field:NotBlank(message = "Delivery time cannot be null or blank")
    val deliveryTime: String,

    @field:NotBlank(message = "Customer email cannot be null or blank")
    val customerEmail: String,

    @field:NotBlank(message = "Customer name cannot be null or blank")
    val customerName: String
) {
    fun toSendEmailLogFile(): SendEmailLogFile = SendEmailLogFile(
        orderId = orderId,
        customerEmail = customerEmail,
        emailSentAt = LocalDateTime.now()
    )
}
