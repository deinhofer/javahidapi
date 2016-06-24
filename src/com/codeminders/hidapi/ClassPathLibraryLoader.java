package com.codeminders.hidapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class ClassPathLibraryLoader {

    public static String[] HID_LIB_NAMES = {
	        "/native/linux/x86_64/libhidapi-jni-64.so",
	        "/native/linux/x86/libhidapi-jni-32.so",
	        "/native/linux/arm/libhidapi-jni-32.so",
	        "/native/macosx/x86_64/libhidapi-jni-64.jnilib",
	        "/native/macosx/x86/libhidapi-jni-32.jnilib",
	        "/native/windows/x86_64/hidapi-jni-64.dll",
	        "/native/windows/x86/hidapi-jni-32.dll"
	};
	      
	public static boolean loadNativeHIDLibrary()
        {
		  boolean isHIDLibLoaded = false;
		  String osName=getOsName();
    	  for(String path : HID_LIB_NAMES)
          {
    		  if(!path.matches(".*/"+osName+"/.*")) {
    			  //System.out.println("Skipping path "+path+", for OS: "+osName);
    			  continue;
    		  }
		        try {
		                // have to use a stream
		                InputStream in = ClassPathLibraryLoader.class.getResourceAsStream(path);
		                if (in != null) {
		                	try {
				                // always write to different location
				                String tempName = path.substring(path.lastIndexOf('/') + 1);
				                File fileOut = File.createTempFile(tempName.substring(0, tempName.lastIndexOf('.')), tempName.substring(tempName.lastIndexOf('.'), tempName.length()));
				                fileOut.deleteOnExit();
				                
				                OutputStream out = new FileOutputStream(fileOut);
				                byte[] buf = new byte[1024];
				                int len;
				                while ((len = in.read(buf)) > 0){            
				                	out.write(buf, 0, len);
				                }
				                
				                out.close();
				                Runtime.getRuntime().load(fileOut.toString());
				                isHIDLibLoaded = true;
		                	} finally {
		                		in.close();
		                	}
		                }	                
		        } catch (Exception e) {
		        	  // ignore
		        } catch (UnsatisfiedLinkError e) {
		        	  // ignore
		        }
		        
		        if (isHIDLibLoaded) {
		        	break;
		        }
        }
    	  
    	return isHIDLibLoaded;  
    }
    
	private static String OS = System.getProperty("os.name").toLowerCase();
	 
	/**
	 * Running on windows? 
	 * @return true: yes
	 */
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
 
	/**
	 * Running on Mac OS X?
	 * @return true: yes
	 */
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}
 
	/**
	 * Running on Linux or Unix?
	 * @return true: yes
	 */
	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
	
	/**
	 * Returns the operating system name according to my convention :-)
	 * @return
	 */
	public static String getOsName() {
		if(isWindows()) {
			return "windows";
		} else if(isMac()) {
			return "macosx";
		} else if(isUnix()) {
			return "linux";
		}
		return "unknown";
	}
    

}
