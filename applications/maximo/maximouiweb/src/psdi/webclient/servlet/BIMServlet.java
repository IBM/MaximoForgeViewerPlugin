/**
* Copyright IBM Corporation 2009-2017
*
* Licensed under the Eclipse Public License - v 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.eclipse.org/legal/epl-v10.html
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
* @Author Doug Wood
**/
package psdi.webclient.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Hashtable;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import psdi.app.bim.BIMService;
import psdi.server.MXServer;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

/**
 * Servers building models to the building model controls.  
 * <p>
 * Models are expected to be located at the directory specified by the Maximo system property
 * <p>
 * bim.model.dir
 */
public class BIMServlet extends HttpServlet 
{
	private static final long serialVersionUID = 5182623260077384490L;
	private final Hashtable<String, AuthToken> _authTokens = new Hashtable<String, AuthToken>();

	
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(
    	HttpServletRequest request, 
    	HttpServletResponse response
	)
    	throws ServletException, 
    	       java.io.IOException 
    {
    	String methodName = isRESTCall( request ); 
        if( methodName != null )
        {
        	doRESTGet( request, response, methodName );
            return;
        }

        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(
    	HttpServletRequest request, 
    	HttpServletResponse response
	)
    	throws ServletException, 
    	       java.io.IOException   
    {
    	String methodName = isRESTCall( request ); 
        if( methodName != null )
        {
        	doRESTPost( request, response, methodName );
            return;
        }

        processRequest(request, response);
    }
    
    private String isRESTCall(
    	HttpServletRequest request
	) {
        String uri     = request.getRequestURI();
        String context = request.getContextPath();
        String urlPath = context +"/models/Sys/";
        if( uri.startsWith( urlPath))
        {
        	return uri.substring( urlPath.length() );
        }
        return null;
    }

    protected void doRESTGet(
    	HttpServletRequest  request, 
    	HttpServletResponse response,
    	String              methodName
	)
    	throws ServletException,
		       java.io.IOException 
    {
    	if( methodName.equals( "AD/Auth" ))
    	{
//    		restAutodeskViewerAuth( request, response );
    	}
    }

    protected void doRESTPost(
    	HttpServletRequest  request, 
    	HttpServletResponse response,
    	String              methodName
	)
    	throws ServletException,
		       java.io.IOException 
    {
    	if( methodName.equals( "AD/Auth" ))
    	{
    		restAutodeskViewerAuth( request, response );
    	}
    }
    
    public final static String LMV_KEY    = "bim.viewer.LMV.key"; 
    public final static String LMV_SECRET = "bim.viewer.LMV.secret";
    public final static String LMV_HOST   = "bim.viewer.LMV.host";
    
    private final static String AUTH_FRAGMENT    = "/authentication/v1/authenticate";

    private final String _protocol  = "https";
    private final int    _port      = -1;

	private void restAutodeskViewerAuth(
    	HttpServletRequest  request, 
    	HttpServletResponse response
	)
    	throws ServletException,
		       java.io.IOException 
    {
        response.setContentType( "application/json; charset=utf-8" );

        MXServer server = MXServer.getMXServer();
    	String key       = server.getProperty( LMV_KEY );
    	AuthToken authToken = _authTokens.get( key );
    	if( authToken != null )
    	{
    		if( !authToken.isExpired() )
    		{
    			OutputStream os = null;
    			try
    			{
        	        os = response.getOutputStream();
        	        os.write( authToken.getRawData().getBytes(Charset.forName("UTF-8")) );
        			return;
    			}
            	finally
            	{
            		if( os != null )
            		{
            			os.close();
            		}
            	}
    		}
    		else
    		{
    			_authTokens.remove( key );
    		}
    	}

    	String secret    = server.getProperty( LMV_SECRET );
    	String host      = server.getProperty( LMV_HOST );
		
		String body    = "client_id=" + key + "&client_secret=" + secret + "&grant_type=client_credentials";

		URI uri;
        try
        {
	        uri = new URI( _protocol, null, host, _port, AUTH_FRAGMENT, null, null );
        }
        catch( URISyntaxException e )
        {
        	throw new IOException( e );
        }

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		if( connection instanceof HttpsURLConnection )
		{
//			HttpsURLConnection httpsConn = (HttpsURLConnection)connection;
//
//
// 	       TrustManager[] trustAllCerts = new TrustManager[] 
//		    		{
// 	    	       new X509TrustManager() {
// 	     	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
// 	     	            return null;
// 	     	          }
//
// 	     	          public void checkClientTrusted(
//     	        		  X509Certificate[] certs, 
//     	        		  String            authType
// 	        		  ) {  
// 	     	        	  System.out.println( authType );
// 	     	        	  System.out.println( certs );
// 	     	          }
//
// 	     	          public void checkServerTrusted(
//     	        		  X509Certificate[] certs, 
//     	        		  String authType
// 	        		  ) {  
// 	     	        	  System.out.println( authType );
// 	     	        	  System.out.println( certs );
// 	     	          }
//
// 	     	       }
//		    	    };

// 	       	KeyManagerFactory factory;
// 	       	KeyManager km[] = null;
//			try 
//			{
//				factory = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
//	 	       	km = factory.getKeyManagers();
//			} 
//			catch( Exception e ) 
//			{
//				e.printStackTrace();
//            	throw new IOException( e );
//			}
//    	    SSLContext sc;
//    	    
//            try
//            {
//	            sc = SSLContext.getInstance("SSL");
//            }
//            catch( NoSuchAlgorithmException e )
//            {
//            	throw new IOException( e );
//            }
//    	    try
//            {
//	            sc.init( km, trustAllCerts, new java.security.SecureRandom());
//            }
//            catch( KeyManagementException e )
//            {
//            	throw new IOException( e );
//            }
//			
//		    // Create all-trusting host name verifier
//		    HostnameVerifier allHostsValid = new HostnameVerifier() 
//		    {
//				@Override
//                public boolean verify(
//                    String     hostname,
//                    SSLSession session 
//                ) {
//	                return true;
//                }
//		    };
//		    
//		    httpsConn.setHostnameVerifier( allHostsValid );
//		    httpsConn.setSSLSocketFactory( sc.getSocketFactory() );
		}

		try
		{
			connection.setRequestMethod( "POST" );
			connection.setConnectTimeout( 0 );
	        connection.setRequestProperty( "Accept", "application/json; charset=utf-8" );
			connection.setRequestProperty( "Content-Length", "" + body.length() );
			connection.setRequestProperty( "Content-Type", 
					                       "application/x-www-form-urlencoded; charset=UTF-8");

			connection.setDoOutput( true );
			
			OutputStream os = null;
        	InputStream is = null;
        	try
        	{
    	        try
    	        {
    	            os = connection.getOutputStream();
    	            os.write(body.getBytes(Charset.forName("UTF-8")));
    	        }
    	        catch( IOException e )
    	        {
    	        	e.printStackTrace();
    	        	throw e;
    	        }
    	        
    	        int status = connection.getResponseCode();
    	        if( status > 299 )
    	        {
    	        	String errStr = stream2string( connection.getErrorStream() );
    	        	response.sendError( status, errStr );
    	        	return;
    	        }
            	is = connection.getInputStream();
            	String dataStr = stream2string( is );
            	
            	AuthToken token = new AuthToken( dataStr );
    			_authTokens.put( key, token );

    	        response.setContentLength( dataStr.length() );
    	        os = response.getOutputStream();
    	        os.write( dataStr.getBytes(Charset.forName("UTF-8")) );
        	}
        	finally
        	{
        		if( os != null )
        		{
        			os.close();
        		}
        		if( is != null )
        		{
        			is.close();
        		}
        	}
		}
		finally
		{
			connection.disconnect();
			connection = null;
			url        = null;
		}
    }
	
    private String stream2string(
   	 InputStream is
	 ) 
		 throws IOException 
	 {
        if (is == null)
        {
            return "";
        }
            
        BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
        String output;
        StringBuffer buf = new StringBuffer();
            
        while( (output = br.readLine()) != null )
        {
            buf.append( output );
        }
        return buf.toString();
    }

    
    
	/** 
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(
    	HttpServletRequest  request, 
    	HttpServletResponse response
	)
    	throws ServletException,
		       java.io.IOException 

    {
    	// authorization
    	Object objSession = request.getSession().getAttribute( "MXSession" );
        if (objSession == null)
        {
            response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
            return;
        }    	
        
        String uri     = request.getRequestURI();
        String context = request.getContextPath();
        String urlPath = context +"/models/";
        
        if( !uri.startsWith( urlPath))
        {
            response.sendError( HttpServletResponse.SC_BAD_REQUEST );
            return;
        }
        
    	String rootDir = MXServer.getMXServer().getProperty( BIMService.PROP_NAME_BIM_MODEL_DIR );
        if( rootDir.length() == 0 )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
        	return;
        }
        
    	String fileName = uri.substring( urlPath.length() );
        String fullPath = rootDir + File.separatorChar + fileName;
        
        String rangeHdr = request.getHeader( "Range" );
        if( rangeHdr != null && rangeHdr.length() > 0 )
        {
        	processRangeRequest( request, response, fullPath );
        	return;
        }
        
        File modelFile = new File( fullPath );
        if( !modelFile.exists() )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        FileInputStream istream;
        try
        {
	        istream = new FileInputStream( modelFile );
        }
        catch( FileNotFoundException e1 )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }
        
        response.setStatus( HttpServletResponse.SC_OK );
        response.setContentType( "application/octet-stream" );
        response.addHeader( "Content-Length", "" + modelFile.length() );
        OutputStream out = response.getOutputStream();

        try
        {
            byte[] b = new byte[8 * 1024];
            
            // Read the file and send back in HTTP response
           while (true)
           {
               int bytesRead = istream.read(b);
               if (bytesRead < 0)
                   break;
               out.write(b, 0, bytesRead);
           }
           out.flush();
        }
        catch( Exception x )
        {
            x.printStackTrace();
        }
        try
        {
        	istream.close();
        }
        catch( IOException e ) { /* Ignore */ }
		try
		{
			out.close();
		}
		catch( IOException e ) { /* Ignore */ }
    }
    
    protected void processRangeRequest(
    	HttpServletRequest  request, 
    	HttpServletResponse response,
    	String              fullPath
	)
    	throws ServletException, 
		       java.io.IOException 
    {
        String rangeHdr = request.getHeader( "Range" );

        String split[] = rangeHdr.split( "=" );
        if( split.length != 2 )
        {
            response.sendError( HttpServletResponse.SC_BAD_REQUEST );
            return;
        }
        String units = split[0].trim();
        String range = split[1].trim();
        if( !units.equalsIgnoreCase( "bytes"))
        {
            response.sendError( HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE );
            return;
        }
        split = range.split( "," );
        if( split.length != 1 )			// Not handling disjoint ranges
        {
            response.sendError( HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE );
            return;
        }
        int idx = range.indexOf( '-' );
        String start    = null;
        long   startLoc = -1;
        String end      = null;
        long   endLoc   = -1;
        if( idx > 0 )
        {
        	start = range.substring( 0, idx );
        	startLoc = Long.parseLong( start );
        }
        if( idx != range.length() )
        {
        	end = range.substring( idx+ 1 );
        	endLoc = Long.parseLong( end );
        }

        File modelFile = new File( fullPath );
        if( !modelFile.exists() )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        FileInputStream fis;
        try
        {
	        fis = new FileInputStream( modelFile );
        }
        catch( FileNotFoundException e1 )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }
        if( start == null )
        {
        	startLoc =  modelFile.length() - endLoc;
        	if( startLoc < 0 ) startLoc = 0;
        }
        if( end == null )
        {
        	endLoc = modelFile.length();
        }
        if( endLoc > modelFile.length() )
        {
        	endLoc = modelFile.length();
        }
        long size = endLoc - startLoc + 1;

        response.setStatus( HttpServletResponse.SC_PARTIAL_CONTENT );
        response.setContentType( "application/octet-stream" );

        if( startLoc == 0 )
        {
            response.addHeader( "Content-Length", "" + modelFile.length() );
        }

        OutputStream out = response.getOutputStream();
        
        try
        {
            int bufSize = 8 * 1024;
        	byte[] buffer = new byte[ bufSize ];
            fis.skip( startLoc );
            
            // Read the file and send back in HTTP response
           while( size > 0 )
           {
        	   int amountToRead;
               if( bufSize > size )
               {
            	   amountToRead = (int)size;
               }
               else
               {
            	   amountToRead = bufSize;
               }
               int bytesRead = fis.read( buffer, 0, amountToRead );
               size -= bytesRead;
               if( bytesRead < 0 )
               {
            	   break;
               }
               out.write( buffer, 0, bytesRead );
           }
           out.flush();
        }
        catch( Exception x )
        {
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            System.out.println("Exception: "+x);
            x.printStackTrace();
        }
        try
        {
        	fis.close();
        }
        catch( IOException e ) { /* Ignore */ }
        try
        {
            out.close();
        }
        catch( IOException e ) { /* Ignore */ }
    }
    
    private class AuthToken
    {
    	private static final String KEY_TOKEN_TYPE   = "token_type";
    	private static final String KEY_EXPIRES_IN   = "expires_in";
    	private static final String KEY_ACCESS_TOKEN = "access_token";
    	
    	private final String _rawData;
    	private String       _token_type;
    	private long         _expires_in;
    	private String       _access_token;
    	private final long   _timeStamp;
    	
        protected  AuthToken(
        	String data
    	) 
    		throws IOException 
    	{
    		_timeStamp = System.currentTimeMillis();
    		_expires_in = -1;
    		_rawData = data;
    		
        	JSONArtifact jArtifact = JSON.parse(  data );
        	JSONObject jObj;
        	if( jArtifact instanceof JSONObject )
        	{
        		jObj = (JSONObject)jArtifact;
        		
        		Object value = jObj.get( KEY_TOKEN_TYPE );
        		if( value instanceof String )
        		{
        			_token_type = (String)value;
        		}
        	
        		value = jObj.get( KEY_EXPIRES_IN );
        		if( value instanceof Long )
        		{
        			_expires_in = (Long)value;
        		}
            	
        		value = jObj.get( KEY_ACCESS_TOKEN );
        		if( value instanceof String )
        		{
        			_access_token = (String)value;
        		}
        	}
    		
        }
    	
        public String getRawData()
        {
            return _rawData;
        }

        public boolean isExpired()
    	{
    		return _timeStamp + _expires_in - 5 < System.currentTimeMillis();
    	}
    }
}