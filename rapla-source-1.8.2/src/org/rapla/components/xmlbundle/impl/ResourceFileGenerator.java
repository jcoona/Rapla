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

package org.rapla.components.xmlbundle.impl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.rapla.framework.ConfigurationException;
import org.rapla.framework.logger.ConsoleLogger;
import org.rapla.framework.logger.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;




class ResourceFileGenerator {
    public static final String encoding = "UTF-8";

    static Logger log = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );
    boolean writeProperties = true;
    
    public void transform(RaplaDictionary dict
                          ,String packageName
                          ,String classPrefix
                          ,File destDir
                          )
                          throws IOException 
    {
        String[] languages = dict.getAvailableLanguages();
        for (String lang:languages) {
            String className = classPrefix;
            if (!lang.equals(dict.getDefaultLang()))
            {
            	className +=  "_" + lang;
            }
            String ending = writeProperties ?  ".properties" : ".java";
            File file = new File(destDir, className  + ending);
            
			PrintWriter w = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),encoding)));
            if ( writeProperties)
            {
            	
            	Properties properties = new Properties();
            	Iterator<DictionaryEntry> it = dict.getEntries().iterator();
        		while ( it.hasNext()) {
        			DictionaryEntry entry =  it.next();
        			String key = entry.getKey();
    				String value =  entry.get(lang);
					if ( value != null)
    				{
        				properties.put(key, value);
    				}
        		}
        		@SuppressWarnings("serial")
				Properties temp = new Properties()
        		{
					@SuppressWarnings({ "rawtypes", "unchecked" })
					public synchronized Enumeration keys() {
    				     Enumeration<Object> keysEnum = super.keys();
    				     Vector keyList = new Vector<Object>();
    				     while(keysEnum.hasMoreElements()){
    				       keyList.add(keysEnum.nextElement());
    				     }
    				     Collections.sort(keyList);
    				     return keyList.elements();
    				  }
        		};
        		temp.putAll( properties);
            	String comment =  packageName + "." + className + "_" + lang;
				temp.store(w, comment);
            }
            else
            {
            	generateJavaHeader(w,packageName);
            	generateJavaContent(w,dict, className, lang);
            }
        	w.flush();
            w.close();
            
            
        }
    }


//    public void transformSingleLanguage(RaplaDictionary dict
//                                        ,String packageName
//                                        ,String classPrefix
//                                        ,String lang
//                                        ) {
//        String className = classPrefix + "_" + lang;
//        w =  new PrintWriter(System.out);  
//        generateHeader(packageName);
//        generateContent(dict,className, lang);
//        w.flush();
//    }

    public static String toPackageName(String pathName) {
		StringBuffer buf = new StringBuffer();
		char[] c =pathName.toCharArray();
		for (int i=0;i<c.length;i++) {
		    if (c[i] == File.separatorChar ) {
			if (i>0 && i<c.length-1)
			    buf.append('.');
		    } else {
			buf.append(c[i]);
		    }
		}
		return buf.toString();
    }

    private void generateJavaHeader(PrintWriter w, String packageName) {
        w.println("/*******************************************");
        w.println(" * Autogenerated file. Please do not edit. *");
        w.println(" * Edit the *Resources.xml file.           *");
        w.println(" *******************************************/");
        w.println();
        w.println("package " + packageName + ";");
    }

    private void generateJavaContent(PrintWriter w, RaplaDictionary dict
                                 ,String className
                                 ,String lang
                                 ) 
    {
        w.println("import java.util.ListResourceBundle;");
        w.println("import java.util.ResourceBundle;");
        w.println();
        w.println("public class " + className + " extends ListResourceBundle {");
        w.println("  public Object[][] getContents() { return contents; }");
	// We make the setParent method public, so that we can use it in I18nImpl
        w.println("  public void setParent(ResourceBundle parent) { super.setParent(parent); }");
        w.println("  static final Object[][] contents = { {\"\",\"\"} ");

        Iterator<DictionaryEntry> it = dict.getEntries().iterator();
        while ( it.hasNext()) {
            DictionaryEntry entry =  it.next();
		    String value =  entry.get(lang);
		    if (value != null) {
		    	String content = convertToJava(value);
		    	w.println("   , { \"" + entry.getKey() + "\",\"" + content + "\"}");
		    }
        }

        w.println("  };");
        w.println("}");
    }

	static public String byteToHex(byte b) {
        // Returns hex String representation of byte b
        char hexDigit[] = {  '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'        };
        char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
        return new String(array);
    }
    
    static public String charToHex(char c) {
        // Returns hex String representation of char c
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }

    private String convertToJava(String text) {
        StringBuffer result = new StringBuffer();
        for ( int i = 0;i< text.length();i++) {
            char c = text.charAt(i);
            
            switch ( c) {
            case '\n': // LineBreaks
                result.append("\" \n      +  \"");               
                break;
            case '\\': // \
                result.append("\\\\");               
                break;
            case '\"': // "
                result.append("\\\"");               
                break;
            default:
                if ( c > 127) {
                    result.append("\\u" + charToHex(c));                                    
                } else {
                    result.append(c);
                } // end of else
                break;
            } // end of switch ()
        } // end of for ()
        return result.toString();
    }
    
    public static final String USAGE = new String( "Usage : \n"
            + "PATH_TO_SOURCES [DESTINATION_PATH]\n"
            + "Example usage under windows:\n"
            + "java -classpath build\\classes "
            + "org.rapla.components.xmlbundle.ResourceFileGenerator "
            + "src \n" );

    public static void processDir( String srcDir, String destDir ) throws IOException, SAXException,
            ConfigurationException
    {
        TranslationParser parser = new TranslationParser();
        ResourceFileGenerator generator = new ResourceFileGenerator();
        Set<String> languages = new HashSet<String>();
        Stack<File> stack = new Stack<File>();
        File topDir = new File( srcDir );
        stack.push( topDir );
        while ( !stack.empty() )
        {
            File file =  stack.pop();
            if ( file.isDirectory() )
            {
                //              System.out.println("Checking Dir: " + file.getName());
                File[] files = file.listFiles();
                for ( int i = 0; i < files.length; i++ )
                    stack.push( files[i] );
            }
            else
            {
                //              System.out.println("Checking File: " + file.getName());
                if ( file.getName().endsWith( "Resources.xml" ) )
                {
                    String absolut = file.getAbsolutePath();
                    System.out.println( "Transforming source:" + file );
                    String relativePath = absolut.substring( topDir.getAbsolutePath().length() );
                    String prefix = file.getName().substring( 0, file.getName().length() - "Resources.xml".length() );
                    String pathName = relativePath.substring( 0, relativePath.indexOf( file.getName() ) );
                    RaplaDictionary dict = parser.parse( file.toURI().toURL().toExternalForm() );

                    File dir = new File( destDir, pathName );
                    System.out.println( "destination:" + dir );
                    dir.mkdirs();

                    String packageName = ResourceFileGenerator.toPackageName( pathName );
                    generator.transform( dict, packageName, prefix + "Resources", dir );
                    String[] langs = dict.getAvailableLanguages();
                    for ( int i = 0; i < langs.length; i++ )
                        languages.add( langs[i] );
                }
            }
        }
    }

    public static void main( String[] args )
    {
        try
        {
            if ( args.length < 1 )
            {
                System.out.println( USAGE );
                return;
            } // end of if ()

            String sourceDir = args[0];
            String destDir = ( args.length > 1 ) ? args[1] : sourceDir;
            processDir( sourceDir, destDir );

        }
        catch ( SAXParseException ex )
        {
            log.error( "Line:" + ex.getLineNumber() + " Column:" + ex.getColumnNumber() + " " + ex.getMessage(), ex );
            System.exit( 1 );
        }
        catch ( Throwable e )
        {
            log.error( e.getMessage(), e );
            System.exit( 1 );
        }
    } // end of main ()

}   
 

