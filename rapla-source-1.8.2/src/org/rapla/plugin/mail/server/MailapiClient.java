package org.rapla.plugin.mail.server;


import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.rapla.RaplaMainContainer;
import org.rapla.entities.configuration.Preferences;
import org.rapla.entities.configuration.RaplaConfiguration;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.plugin.mail.MailException;
import org.rapla.plugin.mail.MailPlugin;

public class MailapiClient implements MailInterface
{
    String mailhost = "localhost";
    int port = 25;
    boolean ssl =false;
    String username;
    String password;
    RaplaContext context;
    Object lookup;
    public MailapiClient( RaplaContext context) throws  RaplaException {
    	this.context = context;
    	if (  context.has(RaplaMainContainer.ENV_RAPLAMAIL))
    	{
    		lookup =  context.lookup(RaplaMainContainer.ENV_RAPLAMAIL);
    	}
    }
    
    public MailapiClient()
    {
    }
    
    public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}


    public void sendMail( String senderMail, String recipient, String subject, String mailBody ) throws MailException
    {
        if ( lookup != null)
        {
            send(senderMail, recipient, subject, mailBody,  lookup);
            return;
        }
        else
        {
            sendMail(senderMail, recipient, subject, mailBody,  (Configuration)null);
        }

    }
 
    public void sendMail( String senderMail, String recipient, String subject, String mailBody, Configuration config ) throws MailException
    {
        Object session;
        if ( config == null && context != null && context.has( ClientFacade.class))
        {
            Preferences systemPreferences;
            try {
                systemPreferences = context.lookup(ClientFacade.class).getSystemPreferences();
            } catch (RaplaException e) {
                throw new MailException( e.getMessage(),e);
            }
            config = systemPreferences.getEntry(MailPlugin.MAILSERVER_CONFIG, new RaplaConfiguration());
        }
        if ( config != null)
        {
            // get the configuration entry text with the default-value "Welcome"
            int port = config.getChild("smtp-port").getValueAsInteger(25);
            String mailhost = config.getChild("smtp-host").getValue("localhost");
            String username= config.getChild("username").getValue("");
            String password= config.getChild("password").getValue("");
            boolean ssl = config.getChild("ssl").getValueAsBoolean(false);
            session = createSessionFromProperties(mailhost,port,ssl, username, password);
        } 
        else
        {
            session = createSessionFromProperties(mailhost,port,ssl, username, password);
        }
        send(senderMail, recipient, subject, mailBody,  session);
    }

    private Object createSessionFromProperties(String mailhost, int port, boolean ssl, String username, String password) throws MailException {
        Properties props = new Properties();
    	props.put("mail.smtp.host", mailhost);
        props.put("mail.smtp.port", new Integer(port));

		boolean usernameSet = username != null && username.trim().length() > 0;
        if ( usernameSet)
        {
    	    props.put("username", username);
    		props.put("mail.smtp.auth","true");
        }
        if ( password != null)
        {
            if ( usernameSet || password.length() > 0)
            {
                props.put("password", password);
            }
        }
        if (ssl)
        {
        	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");	
        	props.put("mail.smtp.socketFactory.port",  new Integer(port));
        }
        Object session;
		try {
	        Class<?> MailLibsC = Class.forName("org.rapla.plugin.mail.server.RaplaMailLibs");
	        session = MailLibsC.getMethod("getSession", Properties.class).invoke( null, props);
		} catch (Exception e) {
			Throwable cause = e;
			if ( e instanceof InvocationTargetException)
			{ 
				cause = e.getCause();
			}
			throw new MailException( cause.getMessage(), cause);
		}
        return session;
    }

    private void send(String senderMail, String recipient, String subject,
			String mailBody, Object uncastedSession) throws MailException {
		ClassLoader classLoader = uncastedSession.getClass().getClassLoader();
		try {
			sendWithReflection(senderMail, recipient, subject, mailBody,  uncastedSession, classLoader);
//			try 
//			{
//				Session castedSession = (Session) uncastedSession;
//				sendWithoutReflection(senderMail, recipient, subject, mailBody, castedSession);
//			} 
//			catch ( ClassCastException ex)
//			{
//		        sendWithReflection(senderMail, recipient, subject, mailBody,  uncastedSession, classLoader);
//				return;
//			}
        } catch (Exception ex) {
            String message = ex.getMessage();
            throw new MailException( message, ex);
        }
	}

//	private void sendWithoutReflection(String senderMail, String recipient,
//		String subject, String mailBody, Session session) throws Exception {
//    	Message message = new MimeMessage(session);
//		if ( senderMail != null && senderMail.trim().length() > 0)
//		{
//			message.setFrom(new InternetAddress(senderMail));
//		}
//		RecipientType type = Message.RecipientType.TO;
//		Address[] parse = InternetAddress.parse(recipient);
//		message.setRecipients(type,	parse);
//		message.setSubject(subject);
//		message.setText(mailBody);
//		Transport.send(message);
//	}

	private void sendWithReflection(String senderMail, String recipient,
			String subject, String mailBody, Object session,
			ClassLoader classLoader) throws Exception {
		Thread currentThread = Thread.currentThread();
		ClassLoader original = currentThread.getContextClassLoader();
		boolean changedClass =false;
		try {
			try
			{
				currentThread.setContextClassLoader( classLoader);
				changedClass = true;
			}
			catch (Throwable ex)
			{
				
			}
			Class<?> SessionC = classLoader.loadClass("javax.mail.Session");
			Class<?> MimeMessageC = classLoader.loadClass("javax.mail.internet.MimeMessage");
			Class<?> MessageC = classLoader.loadClass("javax.mail.Message");
			Class<?> AddressC = classLoader.loadClass("javax.mail.Address");
			Class<?> RecipientTypeC = classLoader.loadClass("javax.mail.Message$RecipientType");
			Class<?> InternetAddressC = classLoader.loadClass("javax.mail.internet.InternetAddress");
			Class<?> TransportC = classLoader.loadClass("javax.mail.Transport");
			//Message message = new MimeMessage(session);
			Object message = MimeMessageC.getConstructor( SessionC).newInstance( session);
			if ( senderMail != null && senderMail.trim().length() > 0)
			{
				//message.setFrom(new InternetAddress(senderMail));
				Object senderMailAddress = InternetAddressC.getConstructor( String.class).newInstance( senderMail);
				MimeMessageC.getMethod("setFrom", AddressC).invoke( message, senderMailAddress);
			}
			//RecipientType type = Message.RecipientType.TO;
			//Address[] parse = InternetAddress.parse(recipient);
			//message.setRecipients(type,	parse);
			Object type = RecipientTypeC.getField("TO").get(null);
			Object[] parsedRecipientDummy = (Object[]) Array.newInstance(AddressC, 0);
			Object parsedRecipient = InternetAddressC.getMethod("parse", String.class).invoke(null, recipient);
			Method method = MessageC.getMethod("setRecipients", RecipientTypeC, parsedRecipientDummy.getClass());
			method.invoke( message, type, parsedRecipient);
			
			//message.setSubject(subject);
			MimeMessageC.getMethod("setSubject", String.class).invoke( message, subject);
			//message.setText(mailBody);
			//MimeMessageC.getMethod("setText", String.class).invoke( message, mailBody);
			MimeMessageC.getMethod("setContent", Object.class, String.class).invoke( message, mailBody, "text/plain; charset=UTF-8");

			
			//Transport.send(message);
			TransportC.getMethod("send", MessageC).invoke( null, message);
		
		} catch (Exception ex) {
			Throwable e = ex;
			if ( ex instanceof InvocationTargetException){
				e = ex.getCause();
			}
			String message = e.getMessage();
		    throw new RaplaException( message, e);
		}
		finally
		{
			if ( changedClass)
			{
				currentThread.setContextClassLoader( original);
			}
		}
	}



    public String getSmtpHost()
    {
        return mailhost;
    }


    public void setSmtpHost( String mailhost )
    {
        this.mailhost = mailhost;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPassword( String password )
    {
        this.password = password;
    }


    public int getPort()
    {
        return port;
    }


    public void setPort( int port )
    {
        this.port = port;
    }


    public String getUsername()
    {
        return username;
    }


    public void setUsername( String username )
    {
        this.username = username;
    }

}
