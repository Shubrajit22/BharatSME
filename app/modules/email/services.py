import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def send_kyc_confirmation_email(recipient_email: str, user_name: str, application_id: str):
    """
    Sends an automated, beautifully styled HTML confirmation email using Mailpit.
    """
    
    # 1. Configuration (Prioritizing Pipeline Environment Variables)
    # Inside Docker: MAIL_SERVER="mailpit", MAIL_PORT=1025
    # Locally: Defaults to localhost:1025
    smtp_server = os.getenv("MAIL_SERVER", "localhost")
    smtp_port = int(os.getenv("MAIL_PORT", 1025))
    
    # Credentials are NOT required for Mailpit
    sender_email = os.getenv("SMTP_USERNAME", "no-reply@bharatsme.in")
    sender_password = os.getenv("SMTP_PASSWORD")

    # 2. Format the HTML Email Content
    subject = "BharatSME: KYC Application Received"
    
    html_content = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <style>
            body {{ font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 0; }}
            .container {{ max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }}
            .header {{ background-color: #0A2540; color: #ffffff; padding: 30px; text-align: center; }}
            .content {{ padding: 40px 30px; color: #333333; line-height: 1.6; }}
            .app-id-box {{ background-color: #f8f9fa; border-left: 4px solid #00D289; padding: 15px; margin: 25px 0; font-family: monospace; font-size: 16px; }}
            .footer {{ background-color: #f8f9fa; padding: 20px; text-align: center; color: #888888; font-size: 12px; border-top: 1px solid #eeeeee; }}
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header"><h1>BharatSME</h1></div>
            <div class="content">
                <h2>Hello {user_name},</h2>
                <p>We have successfully received your Digital KYC application.</p>
                <div class="app-id-box"><strong>Application ID:</strong> {application_id}</div>
                <p>We will notify you via email as soon as the verification is complete.</p>
                <p>Best Regards,<br><strong>The BharatSME Team</strong></p>
            </div>
            <div class="footer"><p>&copy; 2026 BharatSME. All rights reserved.</p></div>
        </div>
    </body>
    </html>
    """

    msg = MIMEMultipart('alternative')
    msg['From'] = f"BharatSME KYC <{sender_email}>"
    msg['To'] = recipient_email
    msg['Subject'] = subject
    msg.attach(MIMEText(html_content, 'html'))

    # 3. Connect and Send
    try:
        # Connect to Mailpit
        server = smtplib.SMTP(smtp_server, smtp_port)
        
        # We only use TLS and Login if we are NOT talking to Mailpit
        # This makes the code compatible with both Dev and Production
        if "mailpit" not in smtp_server and "localhost" not in smtp_server:
            server.starttls()
            if sender_password:
                server.login(sender_email, sender_password)
        
        server.send_message(msg)
        server.quit()
        print(f"[Email Module] ✅ SUCCESS: Captured by Mailpit for {recipient_email}")
        
    except Exception as e:
        print(f"[Email Module] ❌ ERROR: Connection to {smtp_server}:{smtp_port} failed: {e}")