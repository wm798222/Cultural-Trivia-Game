/*
 * Copyright (c) 1996, 1996, 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 * Modified 10/11/17 by swong to include message display strategies
 */

package provided.rmiUtils.classServer;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;



/**
 * ClassServer is an abstract class that provides the
 * basic functionality of a mini-webserver, specialized
 * to load class files only. A ClassServer must be extended
 * and the concrete subclass should define the <b>getBytes</b>
 * method which is responsible for retrieving the bytecodes
 * for a class.<p>
 *
 * The ClassServer creates a thread that listens on a socket
 * and accepts  HTTP GET requests. The HTTP response contains the
 * bytecodes for the class that requested in the GET header. <p>
 *
 * For loading remote classes, an RMI application can use a concrete
 * subclass of this server in place of an HTTP server. 
 * 
 * swong 4/7/19: Modified to handle arbitrary file types, not just class files
 *
 * @see ClassFileServer
 */
public abstract class ClassServer implements Runnable {
	
	/**
	 * Utility class to hold both a pathname and the suffix of a filename
	 * @author swong
	 *
	 */
	protected static class PathSuffix {
		/**
		 * The pathname of the file
		 */
		String path;
		/**
		 * The suffix of the filename
		 */
		String suffix;
		
		/**
		 * Instantiate the class 
		 * @param path The pathname of the file
		 * @param suffix The suffix of the filename
		 */
		PathSuffix(String path, String suffix) {
			this.path = path;
			this.suffix = suffix;
		}
	}

	/**
	 * The socket object the server uses
	 */
	private ServerSocket server = null;

	/**
	 * Flag to used to stop the server
	 */
	private volatile boolean isStop = false;
	
	/**
	 * Strategy to display an informational message.  Defaults to
	 * System.out.println()
	 */
	private Consumer<String> infoMsgStrategy = System.out::println;
	
	/**
	 * Strategy to display an error message.  Defaults to
	 * System.err.println()
	 */	
	private Consumer<String> errMsgStrategy = System.err::println;	
	
	/**
	 * Constructs a ClassServer that listens on <b>port</b> and
	 * obtains a class's bytecodes using the method <b>getBytes</b>.
	 * This constructor uses the default System.out.println() and 
	 * System.err.println() methods to display info and error messages
	 * respectively.
	 *
	 * @param port the port number
	 * @throws IOException if the ClassServer could not listen
	 *            on <b>port</b>.
	 */
	protected ClassServer(int port) throws IOException
	{
		this(port, System.out::println, System.err::println);
	}

	/**
	 * Constructs a ClassServer that listens on <b>port</b> and
	 * obtains a class's bytecodes using the method <b>getBytes</b>.
	 * This constructor explicitly defines the strategies to use for 
	 * displaying info and error messages.  
	 * 
	 * @param port the port number
	 * @param infoMsgStrategy Strategy to display an info message.  
	 * Recommended: For increased system robustness, this strategy 
	 * should first write to System.out.println()
	 * @param errMsgStrategy Strategy to display an error message.  
	 * Recommended: For increased system robustness, this strategy 
	 * should first write to System.err.println()
	 * @throws IOException if the ClassServer could not listen on <b>port</b>.
	 */
	protected ClassServer(int port, Consumer<String> infoMsgStrategy, Consumer<String> errMsgStrategy) throws IOException
	{
		this.infoMsgStrategy = infoMsgStrategy;
		this.errMsgStrategy = (c) -> {};
		server = new ServerSocket(port);
		showInfoMsg("[ClassServer] new server on port "+port);
		newListener();		
	}	
	
	/**
	 * Returns an array of bytes containing the bytecodes for
	 * the class represented by the argument <b>path</b>.
	 * The <b>path</b> is a dot separated class name with
	 * the ".class" extension removed.
	 *
	 * @param pathSuffix The pathname and suffic of the desired class
	 * @return the byte codes for the class
	 * @throws ClassNotFoundException if the class corresponding
	 * to <b>path</b> could not be loaded.
	 * @throws IOException if error occurs reading the class
	 */
	public abstract byte[] getBytes(PathSuffix pathSuffix) throws IOException, ClassNotFoundException;
//	public abstract byte[] getBytes(String path) throws IOException, ClassNotFoundException;
	
	
	/**
	 * Stops the server
	 */
	public void stop(){
		isStop = true;
		try {
			server.close();
			showInfoMsg("[ClassServer.stop()] ClassServer has stopped.");
		}
		catch(Exception e){
			showErrorMsg("[ClassServer.stop()]  Error closing server socket "+server+":\n"+e);
		}
	}

	/**
	 * The "listen" thread that accepts a connection to the
	 * server, parses the header to obtain the class file name
	 * and sends back the bytecodes for the class (or error
	 * if the class is not found or the response was malformed).
	 */
	public void run()
	{
		Socket socket;

		// accept a connection
		try {
			socket = server.accept();

		} catch (IOException e) {
			if(isStop) return;
			showErrorMsg("[ClassServer.run()] Class server died: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// create a new thread to accept the next connection
		newListener();

		try {
			DataOutputStream out =
				new DataOutputStream(socket.getOutputStream());
			try {
				// get path to class file from header
				//DataInputStream in =
				//  new DataInputStream(socket.getInputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				String path = getPath(in);
				PathSuffix pathSuffix = getPath(in);
				showInfoMsg("[ClassServer.run()] path = "+pathSuffix.path+", suffix = "+pathSuffix.suffix);
				// retrieve bytecodes
				byte[] bytecodes = getBytes(pathSuffix);
				// send bytecodes in response (assumes HTTP/1.0 or later)
				try {
					out.writeBytes("HTTP/1.0 200 OK\r\n");
					out.writeBytes("Content-Length: " + bytecodes.length +
					"\r\n");
					if(""!=pathSuffix.suffix) {
						String mime = IClassServer_Defs.SUFFIX_MIME_MAP.get(pathSuffix.suffix);
						if(null!=mime ) {
							System.out.println("[ClassServer.run()] For suffix = \""+pathSuffix.suffix+"\", MIME type = "+mime);
							out.writeBytes("Content-Type: "+mime+"\r\n\r\n");
//							out.writeBytes("Content-Type: application/java\r\n\r\n");

						}
						else {
							System.err.println("[ClassServer.run()] No associated MIME type for suffix = "+pathSuffix.suffix);
						}
					}
					else {
						System.err.println("[ClassServer.run()] No filename suffix for request on path = "+pathSuffix.path );
					}
					out.write(bytecodes);
					out.flush();
				} catch (IOException ie) {
					return;
				}

			} catch (Exception e) {
				System.err.println("[ClassServer.run()] Exception while trying to write out byte stream: "+e);
				e.printStackTrace();
				// write out error response
				out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
				out.writeBytes("Content-Type: text/html\r\n\r\n");
				out.flush();
			}

		} catch (IOException ex) {
			// eat exception (could log error to log file, but
			// write out to error output for now).
			showErrorMsg("[ClassServer.run()] error writing response: " + ex.getMessage());
			ex.printStackTrace();

		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Create a new thread to listen.
	 */
	private void newListener() 
	{
		(new Thread(this)).start();
	}

	/**
	 * Returns the path to the class file obtained from
	 * parsing the HTML header.
	 * @param in stream from which the header is read.
	 * @return the path to the desired file 
	 * @throws IOException if the stream cannot be correctly read
	 */
	private static PathSuffix getPath(BufferedReader in) throws IOException
//	private static String getPath(BufferedReader in) throws IOException

	{
		String line = in.readLine();
		String path = "";
		String suffix = "";

		// extract class from GET line
		if (line.startsWith("GET /")) {
			// Remove the "GET /" from the beginning of the line
			line = line.substring(5, line.length()-1).trim();
			
			line = line.substring(0, line.indexOf(' '));
			System.out.println("line = "+line);
			int index = line.lastIndexOf(".");		
			index = line.lastIndexOf("/") < index ? index: -1; // In case there is a directory name with a "." while the filename does not.
//			int index = line.indexOf(".class ");
			if (index != -1) {
				suffix = line.substring(index);
				path = line.substring(0, index).replace('/', '.');
			}
		}

		// eat the rest of header
		do {
			line = in.readLine();
		} while ((line.length() != 0) &&
				(line.charAt(0) != '\r') && (line.charAt(0) != '\n'));

		if (path.length() != 0) {
			System.out.println("path = "+path+", suffix = "+suffix);
			//return path;
			return new PathSuffix(path, suffix);
		} else {
			throw new IOException("Malformed Header");
		}
	}
	
	/**
	 * Encapsulated behavior to show an informational message
	 * @param msg  Message to show.
	 */
	protected void showInfoMsg(String msg) {
		this.infoMsgStrategy.accept(msg);
	}

	/**
	 * Encapsulated behavior to show an error message
	 * @param msg  Message to show.
	 */
	protected void showErrorMsg(String msg) {
		this.errMsgStrategy.accept(msg);
	}
}