package send.email.function.services

import send.email.function.domain.request.PurchaseDataRequest
import send.email.function.exceptions.SesEmailException
import send.email.function.utils.EmailUtils
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.Body
import software.amazon.awssdk.services.ses.model.Content
import software.amazon.awssdk.services.ses.model.Destination
import software.amazon.awssdk.services.ses.model.Message
import software.amazon.awssdk.services.ses.model.SendEmailRequest

class EmailService(
    private val sesClient: SesClient = SesClient.builder()
        .region(Region.of(System.getenv("AWS_REGION") ?: "us-east-2"))
        .build()
) {
    companion object {
        private const val CHARSET_UTF8 = "UTF-8"
    }

    private val emailSubjectTemplate = "Your Purchase Confirmation #ORDER_NUMBER"

    fun sendPurchaseConfirmationEmail(purchaseData: PurchaseDataRequest, sourceEmail: String) {
        val destination = Destination.builder().toAddresses(purchaseData.customerEmail).build()
        val emailContent = EmailUtils.createPurchaseConfirmationEmail(purchaseData)
        val subject = Content.builder().data(emailSubjectTemplate.replace("ORDER_NUMBER", purchaseData.orderId)).charset(CHARSET_UTF8).build()
        val emailBody = Content.builder().data(emailContent).charset(CHARSET_UTF8).build()
        val message = Message.builder().subject(subject).body(Body.builder().html(emailBody).build()).build()

        try {
            val request = SendEmailRequest.builder()
                .source(sourceEmail)
                .destination(destination)
                .message(message)
                .build()

            sesClient.sendEmail(request)
        } catch (e: SdkException) {
            throw SesEmailException("Failed to send email: ${e.message}", "EMAIL_SEND_FAILED")
        }
    }
}
