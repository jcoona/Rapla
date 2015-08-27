/*--------------------------------------------------------------------------*
 | Copyright (C) 2014 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.jndi.server;

import org.rapla.entities.User;
import org.rapla.entities.configuration.Preferences;
import org.rapla.entities.configuration.internal.PreferencesImpl;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.DefaultConfiguration;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.framework.logger.Logger;
import org.rapla.plugin.jndi.JNDIPlugin;
import org.rapla.plugin.jndi.internal.JNDIConfig;
import org.rapla.server.RemoteMethodFactory;
import org.rapla.server.RemoteSession;
import org.rapla.storage.RaplaSecurityException;

public class RaplaJNDITestOnLocalhost extends RaplaComponent implements RemoteMethodFactory<JNDIConfig>
{
        public RaplaJNDITestOnLocalhost( RaplaContext context)  {
            super( context );
        }
        

		public JNDIConfig createService(final RemoteSession remoteSession) {
			return new JNDIConfig() {
                
                @Override

                public void test(DefaultConfiguration config,String username,String password ) throws RaplaException 
                {
                    User user = remoteSession.getUser();
                    if ( !user.isAdmin())
                    {
                        throw new RaplaSecurityException("Access only for admin users");
                    }

                    Logger logger = getLogger();
                    JNDIAuthenticationStore testStore = new JNDIAuthenticationStore(getContext(), logger);
                    testStore.initWithConfig( config);
                    logger.info("Test of JNDI Plugin started");
                    boolean authenticate;
                    if ( password == null || password.equals(""))
                    {
                        throw new RaplaException("LDAP Plugin doesnt accept empty passwords.");
                    }

                    try {
                        authenticate = testStore.authenticate(username, password);
                    } catch (Exception e) {
                        throw new RaplaException(e);
                    } finally {
                        testStore.dispose();
                    }
                    if (!authenticate)
                    {
                        throw new RaplaSecurityException("Can establish connection but can't authenticate test user " + username);
                    }
                    logger.info("Test of JNDI Plugin successfull");
                }
                
                @SuppressWarnings("deprecation")
                @Override
                public DefaultConfiguration getConfig() throws RaplaException {
                    
                    User user = remoteSession.getUser();
                    if ( !user.isAdmin())
                    {
                        throw new RaplaSecurityException("Access only for admin users");
                    }
                    Preferences preferences = getQuery().getSystemPreferences();
                    DefaultConfiguration config = preferences.getEntry( JNDIPlugin.JNDISERVER_CONFIG);
                    if ( config == null)
                    {
                        config = (DefaultConfiguration) ((PreferencesImpl)preferences).getOldPluginConfig(JNDIPlugin.class.getName());
                    }
                    return config;
                }
            };
		}


		
		

        
}

