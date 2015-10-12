package org.rapla.bootstrap;

import java.io.IOException;
/**
 * Class is used to start the Rapla application. 
 * The main method starts up org.rapla.bootstrap.RaplaJettyLoader
 * @author Jackson
 *
 */

public class RaplaStandaloneLoader
{
	 public static void main(String[] args) throws IOException
	 {
		 // This is for backwards compatibility
		 if ( args.length > 0 && args[0].equals( "client"))
		 {
			 RaplaJettyLoader.main(new String[] {"client"});
		 }
		 else
		 {
			 RaplaJettyLoader.main(new String[] {"standalone"});
		 }
	 }
}
