import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def send_kyc_confirmation_email(recipient_email: str, user_name: str, application_id: str):
    """
    Sends an automated, beautifully styled HTML confirmation email in the background.
    """
    
    smtp_server = os.getenv("SMTP_SERVER", "smtp.gmail.com")
    smtp_port = int(os.getenv("SMTP_PORT", 587))
    sender_email = os.getenv("SMTP_USERNAME")
    sender_password = os.getenv("SMTP_PASSWORD")

    if not sender_email or not sender_password:
        print("[Email Module] ⚠️ SMTP Credentials missing in .env! Skipping email dispatch.")
        return

    # 1. Format the HTML Email Content for BharatSME
    subject = "BharatSME: KYC Application Received"
    
    html_content = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <style>
            body {{
                font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                background-color: #f4f7f6;
                margin: 0;
                padding: 0;
            }}
            .email-wrapper {{
                width: 100%;
                background-color: #f4f7f6;
                padding: 40px 0;
            }}
            .container {{
                max-width: 600px;
                margin: 0 auto;
                background-color: #ffffff;
                border-radius: 8px;
                overflow: hidden;
                box-shadow: 0 4px 15px rgba(0,0,0,0.05);
            }}
            .header {{
                background-color: #0A2540;
                color: #ffffff;
                padding: 30px 20px;
                text-align: center;
            }}
            .header h1 {{
                margin: 0;
                font-size: 26px;
                letter-spacing: 1px;
            }}
            .content {{
                padding: 40px 30px;
                color: #333333;
                line-height: 1.6;
            }}
            .content h2 {{
                color: #0A2540;
                font-size: 20px;
                margin-top: 0;
            }}
            .app-id-box {{
                background-color: #f8f9fa;
                border-left: 4px solid #00D289;
                padding: 15px 20px;
                margin: 25px 0;
                border-radius: 4px;
                font-family: monospace;
                font-size: 16px;
                color: #0A2540;
            }}
            .footer {{
                background-color: #f8f9fa;
                padding: 20px;
                text-align: center;
                color: #888888;
                font-size: 12px;
                border-top: 1px solid #eeeeee;
            }}
        </style>
    </head>
    <body>
        <div class="email-wrapper">
            <div class="container">
                <div class="header">
                    <h1>BharatSME</h1>
                </div>
                <div class="content">
                    <h2>Hello {user_name},</h2>
                    <p>Thank you for choosing <strong>BharatSME</strong>. We have successfully received your Digital KYC application.</p>
                    <p>Your application is currently under review by our automated AI verification system and compliance team. Please save your Application ID for future reference:</p>
                    
                    <div class="app-id-box">
                        <strong>Application ID:</strong> {application_id}
                    </div>
                    
                    <p>We will notify you via email as soon as the verification process is complete. This usually takes just a few moments.</p>
                    <p>Best Regards,<br><strong>The BharatSME Team</strong></p>
                </div>
                <div class="footer">
                    <p>This is an automated message. Please do not reply directly to this email.</p>
                    <p>&copy; 2026 BharatSME. All rights reserved.</p>
                </div>
            </div>
        </div>
    </body>
    </html>
    """

    # We use MIMEMultipart('alternative') to support HTML
    msg = MIMEMultipart('alternative')
    msg['From'] = f"BharatSME KYC <{sender_email}>"
    msg['To'] = recipient_email
    msg['Subject'] = subject
    
    # Attach the HTML content and specify the type as 'html'
    msg.attach(MIMEText(html_content, 'html'))

    # 2. Connect and Send
    try:
        server = smtplib.SMTP(smtp_server, smtp_port)
        server.starttls() # Encrypt the connection
        server.login(sender_email, sender_password)
        server.send_message(msg)
        server.quit()
        print(f"[Email Module] ✅ SUCCESS: HTML Confirmation sent to {recipient_email}")
    except Exception as e:
        print(f"[Email Module] ❌ ERROR: Failed to send email: {e}")