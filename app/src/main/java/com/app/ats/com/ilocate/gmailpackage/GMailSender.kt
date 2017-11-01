package com.app.ats.com.ilocate.gmailpackage

import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.Security
import java.util.Properties

/**
 * Created by abdulla on 2/6/17.
 */

class GMailSender : javax.mail.Authenticator {
    private val mailhost = "smtp.gmail.com"
    private var user: String = ""
    private var password: String = ""
    private var session: Session? = null


    constructor() {
        val username = "username@gmail.com"
        val password = "password"

        val props = Properties()
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.host", "smtp.gmail.com")
        props.put("mail.smtp.port", "587")

        val session = Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("thanseehabdulla@gmail.com", "namoideen")
                    }
                })
        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress("thanseehabdulla@gmail.com"))
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("infinitmail006@gmail.com"))
            message.subject = "Testing Subject"
            message.setText("Dear Mail Crawler," + "\n\n No spam to my email, please!")

            //                        MimeBodyPart messageBodyPart = new MimeBodyPart();
            //
            //                        Multipart multipart = new MimeMultipart();
            //
            //                        messageBodyPart = new MimeBodyPart();
            //                        String file = "path of file to be attached";
            //                        String fileName = "attachmentName";
            //                        DataSource source = new FileDataSource(file);
            //                        messageBodyPart.setDataHandler(new DataHandler(source));
            //                        messageBodyPart.setFileName(fileName);
            //                        multipart.addBodyPart(messageBodyPart);
            //
            //                        message.setContent(multipart);

            Transport.send(message)

            //                        System.out.println("Done");

        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }

    }


    constructor(user: String, password: String) {
        this.user = user
        this.password = password

        val props = Properties()
        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.host", mailhost)
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465")
        props.put("mail.smtp.socketFactory.port", "465")
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory")
        props.put("mail.smtp.socketFactory.fallback", "false")
        props.setProperty("mail.smtp.quitwait", "false")

        session = Session.getDefaultInstance(props, this)
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, password)
    }

    @Synchronized
    @Throws(Exception::class)
    fun sendMail(subject: String, body: String, sender: String, recipients: String) {
        try {
            val message = MimeMessage(session)
            val handler = DataHandler(ByteArrayDataSource(body.toByteArray(), "text/plain"))
            message.sender = InternetAddress(sender)
            message.subject = subject
            message.dataHandler = handler
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients))
            else
                message.setRecipient(Message.RecipientType.TO, InternetAddress(recipients))
            Transport.send(message)
        } catch (e: Exception) {

        }

    }

    inner class ByteArrayDataSource : DataSource {
        private var data: ByteArray? = null
        private var type: String? = null

        constructor(data: ByteArray, type: String) : super() {
            this.data = data
            this.type = type
        }

        constructor(data: ByteArray) : super() {
            this.data = data
        }

        fun setType(type: String) {
            this.type = type
        }

        override fun getContentType(): String {
            return if (type == null)
                "application/octet-stream"
            else
                type!!
        }

        @Throws(IOException::class)
        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }

        override fun getName(): String {
            return "ByteArrayDataSource"
        }

        @Throws(IOException::class)
        override fun getOutputStream(): OutputStream {
            throw IOException("Not Supported")
        }
    }

    companion object {

        init {
            Security.addProvider(JSSEProvider())
        }
    }
}
