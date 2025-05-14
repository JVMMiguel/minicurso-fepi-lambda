package send.email.function.utils

import send.email.function.domain.request.PurchaseDataRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EmailUtils {
    companion object {
        fun createPurchaseConfirmationEmail(purchaseData: PurchaseDataRequest): String {
            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Purchase Confirmation</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333333;
                        max-width: 600px;
                        margin: 0 auto;
                    }
                    .header {
                        background-color: #4CAF50;
                        color: white;
                        padding: 20px;
                        text-align: center;
                    }
                    .content {
                        padding: 20px;
                    }
                    .order-details {
                        background-color: #f9f9f9;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .footer {
                        font-size: 12px;
                        color: #777;
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 10px;
                        border-top: 1px solid #eee;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Thank You for Your Purchase!</h1>
                </div>
                
                <div class="content">
                    <p>Hello ${purchaseData.customerName},</p>
                    
                    <p>We're pleased to confirm that your order has been successfully processed.</p>
                    
                    <div class="order-details">
                        <h2>Order Details</h2>
                        <p><strong>Order Number:</strong> ${purchaseData.orderId}</p>
                        <p><strong>Product:</strong> ${purchaseData.productName}</p>
                        <p><strong>Amount Paid:</strong> ${purchaseData.amountPaid}</p>
                        <p><strong>Estimated Delivery:</strong> ${purchaseData.deliveryTime}</p>
                    </div>
                    
                    <p>If you have any questions about your order, please contact our customer support team.</p>
                    
                    <p>Best regards,<br>
                    João Vittor's Company</p>
                </div>
                
                <div class="footer">
                    <p>This is an automated message, please do not reply directly to this email.</p>
                    <p>Email sent on: $currentDateTime</p>
                </div>
            </body>
            </html>
            """.trimIndent()
        }
    }
}
