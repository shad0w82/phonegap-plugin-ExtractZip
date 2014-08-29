package org.apache.cordova.plugin.ExtractZip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.*;

import android.content.Context;

/**
 * @author Evgeniy Lukovsky
 *
 */
public class ExtractZipPlugin extends CordovaPlugin {
	public enum Action{
		extract, getTempDir
	}

	/**
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[65536];
		int len;

		while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
	}


	/* (non-Javadoc)
	 * @see org.apache.cordova.CordovaPlugin#execute(java.lang.String, org.json.JSONArray, org.apache.cordova.CallbackContext)
	 */
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		System.out.println("ZIP plugin has been started");
		boolean result = false;

		switch(Action.valueOf(action)){
		case extract:
			result = true;
			// run "extractAll" threaded to support big files (e.g. > 100MByte)
			final JSONArray _args = args;
			final CallbackContext _callbackContext = callbackContext;
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					extractAll(_args, _callbackContext);
	            }
	        });
		break;
                case extractArray:
			result = true;
			final JSONArray _args = args;
			final CallbackContext _callbackContext = callbackContext;
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					extractBytearray(_args, _callbackContext);
	            }
	        });
		break;
		case getTempDir:
			result = true;
			getTempDir(args, callbackContext);
		}
        		
		
		return result;
	}

	/**
	 * @param args
	 * @param callbackContext
	 * @return
	 */
	private boolean extractAll(JSONArray args, CallbackContext callbackContext) {
		try {
			String filename = args.getString(0);
			String destDir =  args.getString(1);
			ZipFile zipFile = new ZipFile(filename);
			ZipEntry entry;
			InputStream is = null;
			BufferedOutputStream os = null;
			try {
				Enumeration<? extends ZipEntry> e = zipFile.entries();
				while (e.hasMoreElements()) 
				{
					entry = (ZipEntry) e.nextElement();
					String fileName = destDir.toString() + entry.getName();
					File outFile = new File(fileName);
					if (entry.isDirectory()) 
					{
						outFile.mkdirs();
						continue;
					} 
					try{ 
						outFile.getAbsoluteFile().getParentFile().mkdirs();
						is = zipFile.getInputStream(entry);
						os =new BufferedOutputStream(new FileOutputStream(outFile.getAbsolutePath()));
						copyInputStream(is, os);
					}
					catch(IOException e2){
						System.out.println("Can't write file.");
						System.out.println(e2.getMessage());
						callbackContext.error("Can not write a file");
						return false;
					}finally{
						if(is!=null){
							is.close();
						}
						if(os!=null){
							os.flush();
							os.close();
						}
					}
				}
			} catch (ZipException e1) {
				System.out.println("ZIP exception");
				System.out.println(e1.getMessage());
				callbackContext.error("ZIP Exception.");
				return false;
			} catch (IOException e1) {
				System.out.println("IO exception");
				System.out.println(e1.getMessage());
				callbackContext.error("IO Exception");
				return false;
			}

		} catch (JSONException e) {
			System.out.println("JSON exception");
			callbackContext.error("JSON exception");
			System.out.println(e.getMessage());
			return false;
		} catch (IOException e3) {
			System.out.println("IO/ZIP exception");
			System.out.println(e3.getMessage());
			callbackContext.error("IO/ZIP Exception");
			return false;
		}
		System.out.println("All went fine.");
		callbackContext.success("Succesfully extracted.");
		return true;
	}

        /**
	 * @param args
	 * @param callbackContext
	 * @return
	 */
	private boolean extractBytearray(JSONArray args, CallbackContext callbackContext) {
		try {
			byte[] bArray = args.getString(0);
			String destDir =  args.getString(1);
			ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(bArray));
			ZipEntry entry;
			BufferedOutputStream os = null;
			try {

                            while ((entry = zipStream.getNextEntry()) != null) {
                                System.out.println("Unzipping: " + entry.getName());

                                int size;
                                byte[] buffer = new byte[2048];

                                String fileName = destDir.toString() + entry.getName();
                                File outFile = new File(fileName);
                                if (entry.isDirectory()) 
                                {
                                        outFile.mkdirs();
                                        continue;
                                } 
                                try{ 
                                        outFile.getAbsoluteFile().getParentFile().mkdirs();
                                        os =new BufferedOutputStream(new FileOutputStream(outFile.getAbsolutePath()));
                                        while ((size = zipStream.read(buffer, 0, buffer.length)) != -1) {
                                            os.write(buffer, 0, size);
                                        }
                                }
                                catch(IOException e2){
                                        System.out.println("Can't write file.");
                                        System.out.println(e2.getMessage());
                                        callbackContext.error("Can not write a file");
                                        return false;
                                }finally{

                                        if(os!=null){
                                                os.flush();
                                                os.close();
                                        }
                                }
                            }

			} catch (ZipException e1) {
				System.out.println("ZIP exception");
				System.out.println(e1.getMessage());
				callbackContext.error("ZIP Exception.");
				return false;
			} catch (IOException e1) {
				System.out.println("IO exception");
				System.out.println(e1.getMessage());
				callbackContext.error("IO Exception");
				return false;
			}

		} catch (JSONException e) {
			System.out.println("JSON exception");
			callbackContext.error("JSON exception");
			System.out.println(e.getMessage());
			return false;
		} catch (IOException e3) {
			System.out.println("IO/ZIP exception");
			System.out.println(e3.getMessage());
			callbackContext.error("IO/ZIP Exception");
			return false;
		}
		System.out.println("All went fine.");
		callbackContext.success("Succesfully extracted.");
		return true;
	}

	private boolean getTempDir(JSONArray args,CallbackContext callbackContext){
		String dirName;
		try {
			dirName = args.getString(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			callbackContext.error(e.getMessage());
			return false;
		}
		Context appContext = cordova.getActivity().getApplicationContext();
		String absolutePath = appContext.getDir(dirName, Context.MODE_PRIVATE).getAbsolutePath();
		callbackContext.success(absolutePath);
		return true;
	}

}
