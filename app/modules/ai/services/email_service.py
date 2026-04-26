# services/email_service.py
import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def send_kyc_confirmation_email(recipient_email: str, user_name: str, application_id: str):
    """Sends an automated confirmation email in the background."""
    
    smtp_server = os.getenv("SMTP_SERVER")
    smtp_port = int(os.getenv("SMTP_PORT", 587))
    sender_email = os.getenv("SMTP_USERNAME")
    sender_password = os.getenv("SMTP_PASSWORD")

    # Failsafe if env vars are missing
    if not sender_email or not sender_password:
        print("[Email System] Credentials missing. Skipping email dispatch.")
        return

    # 1. Format the Email
    subject = "Digital KYC: Application Received"
    body = f"""
    Dear {user_name},

    Thank you for submitting your Digital KYC application. 
    
    Your application (ID: {application_id}) has been successfully received and is currently under review by our AI verification system.

    We will notify you once the verification process is complete.

    Regards,
    Digital KYC Team
    """

    msg = MIMEMultipart()
    msg['From'] = f"Digital KYC <{sender_email}>"
    msg['To'] = recipient_email
    msg['Subject'] = subject
    msg.attach(MIMEText(body, 'plain'))

    # 2. Send the Email
    try:
        server = smtplib.SMTP(smtp_server, smtp_port)
        server.starttls() # Encrypt the connection
        server.login(sender_email, sender_password)
        server.send_message(msg)
        server.quit()
        print(f"[Email System] -> SUCCESS: Confirmation sent to {recipient_email}")
    except Exception as e:
        print(f"[Email System] -> ERROR: Failed to send email: {e}")