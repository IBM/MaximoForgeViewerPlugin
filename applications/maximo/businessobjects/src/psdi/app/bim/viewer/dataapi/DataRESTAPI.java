/*
 *
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * 5724-U18
 *
 * (C) COPYRIGHT IBM CORP. 2006,2016
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 *
 */
package psdi.app.bim.viewer.dataapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.codec.binary.Base64;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public abstract class DataRESTAPI 
{
	public final static int    API_AUTH			= 1;
	public final static int    API_OSS          = 2;
	public final static int    API_VIEWING      = 3;
	public final static int    API_REF          = 4;
    public final static String SERVICE_NAME = "BIMLMV";
    
    
     
    /**
     * View your profile info The application will be able to read the end user’s profile data. 
     */
    public final static String SCOPE_PROFIEL_READ = "user-profile:read";
    
    /**
     * View your data The application will be able to read the end user’s data within the 
     * Autodesk ecosystem. 
     */
    public final static String SCOPE_DATA_READ = "data:read"; 
    
    /**
     * Manage your data The application will be able to create, update, and delete data on
     *  behalf of the end user within the Autodesk ecosystem. 
     */
    public final static String SCOPE_DATA_WRITE = "data:write";
     
    /**
     * Write data The application will be able to create data on behalf of the end user within t
     * he Autodesk ecosystem. 
     */
    public final static String SCOPE_DATA_CREATE = "data:create"; 
     
    /**
     * Search across your data The application will be able to search the end user’s data 
     * within the Autodesk ecosystem. 
     */
    public final static String SCOPE_DATA_SEARCH = "data:search"; 
     
    /**
     * Create new buckets The application will be able to create an OSS bucket it will own. 
     */
    public final static String SCOPE_BUCKET_CREATE = "bucket:create"; 
    
    /**
     * View your buckets The application will be able to read the metadata and list contents
     * for OSS buckets that it has access to. 
     */
    public final static String SCOPE_BUCKET_READ = "bucket:read"; 
    
    /**
     * Update your buckets The application will be able to set permissions and entitlements for 
     * OSS buckets that it has permission to modify. 
     */
    public final static String SCOPE_BUCKET_UPDATE = "bucket:update"; 
     
    /**
     * Delete your buckets The application will be able to delete a bucket that it has permission 
     * to delete. 
     */
    public final static String SCOPE_BUCKET_DELETE = "bucket:delete"; 
    
    /**
     * Author or execute your codes The application will be able to author and execute code on behalf 
     * of the end user (e.g., scripts processed by the Design Automation API).
     */
    public final static String SCOPE_CODE_ALL = "code:all"; 
    
    /**
     * View your product and service accounts For Product APIs, the application will be able to read 
     * the account data the end user has entitlements to. 
     */
    public final static String SCOPE_ACCOUNT_READ = "account:read"; 
    
    /**
     * Manage your product and service accounts For Product APIs, the application will be able to update 
     * the account data the end user has entitlements to. 
     */
    public final static String SCOPE_ACCOUNT_WRITE = "account:write"; 

    
    public final static String BUCKET_POLICY_TRANSIENT  = "transient";
	public final static String BUCKET_POLICY_TEMPORARY  = "temporary";
	public final static String BUCKET_POLICY_PERSISTENT = "persistent";
	
	public final static String FORGE_SERVICE_URN_PREFIX = "urn:adsk.objects:os.object:";
	
	protected final static String PATT_AUTH                    = "/authentication/v1/authenticate";
	protected final static String PATT_BUCKET                  = "/oss/v2/buckets";
	protected final static String PATT_BUCKET_DELETE           = "/oss/v2/buckets/%1";
	protected final static String PATT_BUCKET_REVOKE2          = "/oss/V2/buckets/%1/revoke";
	protected final static String PATT_BUCKET_GRANT2           = "/oss/v2/buckets/%1/grant";
	protected final static String PATT_BUCKET_QUERY            = "/oss/v2/buckets/%1/details";
	protected final static String PATT_BUCKET_LIST             = "/oss/v2/buckets";
	protected final static String PATT_OBJECT_DELETE           = "/oss/v2/buckets/%1/objects/%2";
	protected final static String PATT_OBJECT_LIST             = "/oss/v2/buckets/%1/objects";
	protected final static String PATT_OBJECT_UPLOAD           = "/oss/v2/buckets/%1/objects/%2";
	protected final static String PATT_OBJECT_UPLOAD_RESUMABLE = "/oss/v2/buckets/%1/objects/%2/resumable";
	protected final static String PATT_OBJECT_QUERY            = "/oss/v2/buckets/%1/objects/%2/details";
	protected final static String PATT_LINK                    = "/references/v1/setreference";
	protected final static String PATT_VIEW_DEREGISTER         = "/modelderivative/v2/designdata/%1/manifest";
	protected final static String PATT_VIEW_REGISTER           = "/modelderivative/v2/designdata/job";
	protected final static String PATT_VIEW_QUERY              = "/derivativeservice/v2/manifest/%1";
	protected final static String PATT_VIEW_SUPPORTED          = "/modelderivative/v2/designdata/formats";
	protected final static String PATT_VIEW_METADATA           = "/modelderivative/v2/designdata/%1/metadata";
	protected final static String PATT_VIEW_DOWNLOAD           = "/derivativeservice/v2/derivatives/%1";

	/**
	 * Schema for bucket create
	 */
	protected final static String KEY_SCHEMA         = "$schema";
	/**
	 * A unique name you assign to a bucket. It must be globally unique across all applications and	
	 * regions, otherwise the call will fail. Possible values: -_.a-z0-9 (between 3-128 characters in
	 * length). Note that you cannot change a bucket key.
	 */
	protected final static String KEY_BUCKETKEY      = "bucketKey";
	/**
	 * Data retention policy
	 * Acceptable values: transient, temporary, persistent 
	 */
	protected final static String KEY_POLICY_KEY     = "policyKey";
	protected final static String KEY_POLICY         = "policy";
	/**
	 * array Objects representing applications to which the owner wants to grant access at bucket 
	 * creation time
	 */
	protected final static String KEY_ALLOW          = "allow";
	/**
	 *  string The application key to grant access to 
	 */
	protected final static String KEY_AUTH_ID        = "authId";
	/**
	 *  enum Acceptable values: full, read 
	 */
	protected final static String KEY_ACCESS         = "access";

	protected final static String KEY_LIMIT          = "limit";
	protected final static String KEY_URN            = "urn";
	protected final static String KEY_BEGINS_WITH    = "beginsWith";
	
	protected final static String KEY_MASTER         = "master";
	protected final static String KEY_DEPENDENCIES   = "dependencies";
	protected final static String KEY_FILE           = "file";
	protected final static String KEY_METADATA       = "metadata"; 
	protected final static String KEY_CHILD_PATH     = "childPath";
	protected final static String KEY_PARENT_PATH    = "parentPath";
	/**
	 * /modelderivative/v2/designdata/job
	 * Set this to true if the source file is compressed. If set to true, you need to define the rootFilename.
	 */
	protected final static String KEY_COMPRESSED_URN = "compressedUrn";
	/**
	 * /modelderivative/v2/designdata/job
	 * The root filename of the compressed file. Mandatory if the compressedUrn is set to true.
	 */
	protected final static String KEY_ROOT_FILENAME  = "rootFilename";
	
	protected final static String KEY_INPUT          = "input";
	protected final static String KEY_OUTPUT         = "output";
	protected final static String KEY_DESTINATION    = "destination";
	protected final static String KEY_REGION         = "region";
	protected final static String KEY_FORMATS        = "formats";
	protected final static String KEY_TYPE           = "type";
	protected final static String KEY_VIEWS          = "views";

	private final String _protocol  = "https";
	
	private final int    _port      = -1; 
	private  int         _uploadChunkSize     = 0x200000;
	private  int         _upLoadRetryCount    = 10;
	private  int         _uploadRetryDelay    = 5000;		// Milliseconds
	private  int         _bucketListChunkSize = 100;
	private  int         _objectListChunkSize = 100;
	
	private Hashtable<String, ResultAuthentication>  _authTokens = new Hashtable<String, ResultAuthentication>();
	

    public abstract String lookkupSecret();

    public abstract String lookupHostname();
	
	public abstract String lookupKey();
	
    public abstract String lookupVersion( int api );
    
    

    public int getUploadChunkSize()
	{
		return _uploadChunkSize;
	}

	public void setUploadChunkSize(
	    int _uploadChunkSize )
	{
		this._uploadChunkSize = _uploadChunkSize;
	}

	public int getUpLoadRetryCount()
	{
		return _upLoadRetryCount;
	}

	public void setUpLoadRetryCount(
	    int _upLoadRetryCount )
	{
		this._upLoadRetryCount = _upLoadRetryCount;
	}

	public int getUploadRetryDelay()
	{
		return _uploadRetryDelay;
	}

	public void setUploadRetryDelay(
	    int _uploadRetryDelay )
	{
		this._uploadRetryDelay = _uploadRetryDelay;
	}

	public int getBucketListChunkSize()
	{
		return _bucketListChunkSize;
	}

	public void setBucketListChunkSize(
	    int bucketListChunkSize 
    ) {
		_bucketListChunkSize = bucketListChunkSize;
	}

	public int getObjectListChunkSize()
	{
		return _objectListChunkSize;
	}

	public void setObjectListChunkSize(
	    int objectListChunkSize 
    ) {
		_objectListChunkSize = objectListChunkSize;
	}
	
	/**
	 * Clears all auth tokens for the authorization token cache
	 */
	public void clearAuthCache()
	{
		synchronized (_authTokens) 
		{
			_authTokens = new Hashtable<String, ResultAuthentication>();
		}
	}

	public ResultAuthentication authenticate(
    	String[] scope
	)
    	    throws IOException, 
    	           URISyntaxException
    	{
        	String key       = lookupKey();
        	String secret    = lookkupSecret();
        	
        	String hashKey = key;
        	for( int i = 0; scope != null && i < scope.length; i++ )
        	{
        		hashKey = hashKey + scope[i].toLowerCase();
        	}
        	ResultAuthentication authToken = _authTokens.get( hashKey );

    		synchronized (_authTokens) 
    		{
            	if(    authToken != null 
        			&& !authToken.isError() 
        			&& !authToken.isExpired() )
        		{
        			return authToken;
        		}
    		}
    		
    		String body    = "client_id=" + key + "&client_secret=" + secret + "&grant_type=client_credentials";
    		if( scope != null && scope.length > 0 )
    		{
        		body = body + "&scope=";
        		for( int i = 0; i < scope.length; i++ )
        		{
        			body = body + scope[i] + ' ';
        		}
    		}

    		String frag = makeURN( API_AUTH, PATT_AUTH, null );
    		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

    		URL url = new URL( uri.toASCIIString() );
    		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

    		connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Accept", "Application/json" );
    		connection.setRequestProperty( "Content-Length", "" + body.length() );
    		connection.setRequestProperty( "Content-Type", 
    				                       "application/x-www-form-urlencoded; charset=UTF-8");

    		connection.setDoOutput( true );
            OutputStream os = null;
    		try
    		{
    	        os = connection.getOutputStream();
    	        os.write(body.getBytes(Charset.forName("UTF-8")));
    		}
    		catch( UnknownHostException uhe )
    		{
    			return new ResultAuthentication( uhe );
    		}
    		catch( SSLHandshakeException sslhse )
    		{
    			return new ResultAuthentication( sslhse );
    		}
    		catch( IOException ioe )
    		{
    			ioe.printStackTrace();
    			throw ioe;
    		}
    		finally
    		{
    			connection.disconnect();
    			if( os != null )
    			{
    				os.close();
    			}
    		}
    		
    		ResultAuthentication result = new ResultAuthentication( connection );
    		synchronized (_authTokens) 
    		{
    			_authTokens.put( hashKey, result );
    		}
    		connection.disconnect();
    		
    		return result;
    	}
    	
	/**
	 * Use this API to create a bucket. Buckets are arbitrary spaces created and owned by services. Bucket 
	 * keys are unique within the data center or region in which they were created. The service creating 
	 * the bucket is the owner of the bucket, and the owning service will always have full access to a bucket. 
	 * A bucket key cannot be changed once it is created.
	 * <p>
	 * Buckets must have a retention policy attached to them at create time. Policy options are: 
	 * Transient,
	 * Temporary,
	 * Persistent
	 * @param bucketKey		A unique name you assign to a bucket. It must be globally unique across 
	 *                      all applications and regions, otherwise the call will fail. Possible 
	 *                      values: -_.a-z0-9 (between 3-128 characters in length). Note that you cannot 
	 *                      change a bucket key.
	 * @param policy
	 * @param region		The region where the bucket resides Acceptable values: US, EMEA Default: US
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
    public ResultCreateBucket bucketCreate(
		String bucketKey,
		String policy,
		String region
	) 
	    throws IOException, 
	           URISyntaxException
	{
    	String scope[] = { SCOPE_BUCKET_CREATE };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new ResultCreateBucket( authResult );
		}
		
		JSONObject j_bucket = new JSONObject();
		j_bucket.put( KEY_BUCKETKEY, bucketKey.toLowerCase() );
		j_bucket.put( KEY_POLICY_KEY, policy );
		String jStr = j_bucket.toString();
		
		String frag = makeURN( API_OSS, PATT_BUCKET, null );

		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag , null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		if( region != null && region.length() > 0 )
		{
			connection.setRequestProperty( "x-ads-region", region );
		}
		connection.setRequestMethod( "POST" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );
		connection.setRequestProperty( "Content-Length", "" + jStr.length() );
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		connection.setDoOutput( true );
        OutputStream os = null;
        try
        {
            os = connection.getOutputStream();
            os.write(jStr.getBytes(Charset.forName("UTF-8")));
        }
        finally
        {
        	if( os != null ) os.close();
        }
		
        ResultCreateBucket result = new ResultCreateBucket( connection );
		return result;
	}
	
    public Result bucketGrantRightsV2(
		String bucketKey,
		String serviceId,
		String access
	) 
	    throws IOException, 
	           URISyntaxException
	{
    	String scope[] = { SCOPE_BUCKET_UPDATE };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return authResult;
		}
		
		/*
		{
		  "allow":[
		       {"authId":"D6C9x7Bk0vo2HA1qC7l0VHM4MtYqZsN4","access":"full"}
		  ]
		}
		*/
		
		JSONObject j_rights = new JSONObject();
		j_rights.put( "authId", serviceId );
		j_rights.put( "access", access );
		JSONArray j_serviceList = new JSONArray();
		j_serviceList.add( j_rights );
		JSONObject j_grantRequest = new JSONObject();
		j_grantRequest.put( "allow", j_serviceList );
		
		String jStr = j_grantRequest.toString();
		
		String params[] = { bucketKey.toLowerCase() };
		String frag = makeURN( API_OSS, PATT_BUCKET_GRANT2, params );
		
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag , null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "POST" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );
		connection.setRequestProperty( "Content-Length", "" + jStr.length() );
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		connection.setDoOutput( true );
        OutputStream os = null;
        try
        {
            os = connection.getOutputStream();
            os.write(jStr.getBytes(Charset.forName("UTF-8")));
        }
        finally
        {
        	if( os != null ) os.close();
        }
		
		Result result = new Result( connection );
		return result;
	}
    
    public Result bucketDelete(
    	String bucketKey
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	String scope[] = { SCOPE_BUCKET_DELETE };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return authResult;
		}
		
		String params[] = { bucketKey };
		String frag = makeURN( API_OSS, PATT_BUCKET_DELETE, params );
		
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag , null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "DELETE" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		Result result = new ResultBucketList( connection );
		return result;
    	
    }

    public ResultBucketList bucketList(
    	String region
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	String scope[] = { SCOPE_BUCKET_READ };
    	
		String params[] = {};
		String frag = makeURN( API_OSS, PATT_BUCKET_LIST, params );

		ResultBucketList result  = null;
		String           startAt = null;
		int returnCount          = 0;
		
    	do
    	{
			ResultAuthentication authResult = authenticate( scope );
			if( authResult.isError() )
			{
				return new ResultBucketList( authResult );
			}
			
			String query = "limit=" + _bucketListChunkSize;
			if( startAt != null )
			{
				query = query + "&startAt=" + startAt; 
			}
			if( region != null && region.length() > 0 )
			{
				query = query + "&region=" + region; 
				
			}
			
			URI uri = new URI( _protocol, null, lookupHostname(), _port, frag , query, null );
	
			URL url = new URL( uri.toASCIIString() );
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	
			connection.setRequestMethod( "GET" );
			authResult.setAuthHeader( connection );
	        connection.setRequestProperty( "Accept", "Application/json" );
			connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );
			
			ResultBucketList resultNew = new ResultBucketList( connection );
			returnCount = resultNew.size();
			BucketDescription bucket = resultNew.getBucket( returnCount - 1 );
			if( bucket != null )
			{
				startAt = bucket.getBucketKey();
			}

			if( result == null )
			{
				result = resultNew;
			}
			else
			{
				result.append( resultNew );
			}
    	}
    	while( returnCount == _bucketListChunkSize );
		return result;
    	
    }
	
    /**
	 * This API will return bucket details in json format, if the caller is the owner or the calling 
	 * service or application has access rights on the bucket. Any other request will result in 403
	 *  Forbidden.
	 * @param bucketKey
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
    public ResultBucketDetail bucketQueryDetails(
		String bucketKey
	) 
	    throws IOException, 
	           URISyntaxException
	{
    	String scope[] = { SCOPE_BUCKET_READ };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new ResultBucketDetail( authResult );
		}
		
		String params[] = { bucketKey.toLowerCase() };
		String frag = makeURN( API_OSS, PATT_BUCKET_QUERY, params );

		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "GET" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		return new ResultBucketDetail( connection );
	}

	public Result bucketRevokeRightsV2(
		String bucketKey,
		String serviceId
	) 
	    throws IOException, 
	           URISyntaxException
	{
    	String scope[] = { SCOPE_BUCKET_UPDATE };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return authResult;
		}
		
		/*
		{
		  "allow":[
		       {"authId":"D6C9x7Bk0vo2HA1qC7l0VHM4MtYqZsN4","access":"full"}
		  ]
		}
		*/
		
		JSONObject j_rights = new JSONObject();
		j_rights.put( "authId", serviceId );
		JSONArray j_serviceList = new JSONArray();
		j_serviceList.add( j_rights );
		JSONObject j_grantRequest = new JSONObject();
		j_grantRequest.put( "allow", j_serviceList );

		String jStr = j_grantRequest.toString();
		
		String params[] = { bucketKey.toLowerCase() };
		String frag = makeURN( API_OSS, PATT_BUCKET_REVOKE2, params );
		
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "POST" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );
		connection.setRequestProperty( "Content-Length", "" + jStr.length() );
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		connection.setDoOutput( true );
        OutputStream os = null;
        try
        {
            os = connection.getOutputStream();
            os.write(jStr.getBytes(Charset.forName("UTF-8")));
        }
        finally
        {
        	if( os != null ) os.close();
        }

		Result result = new Result( connection );
		return result;
	}

    /**
	 * Calculate Sha-1 checksum for a file
	 * @param file Name of the file
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
    public String createSha1(
		File file
	) 
		throws GeneralSecurityException,
		       IOException  
	{
	    MessageDigest digest = MessageDigest.getInstance("SHA-1");
	    InputStream fis = null;
	    try
	    {
		    fis = new FileInputStream(file);
		    int n = 0;
		    byte[] buffer = new byte[8192];
		    while (n != -1) {
		        n = fis.read(buffer);
		        if (n > 0) {
		            digest.update(buffer, 0, n);
		        }
		    }
	    }
	    finally
	    {
	    	if( fis != null )
	    	{
	    		fis.close();
	    	}
	    }
	    
	    byte bSha1[] = digest.digest();
	    
	    StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < bSha1.length; i++) {
	    	sb.append(Integer.toString((bSha1[i] & 0xff) + 0x100, 16).substring(1));
	    }

	    return sb.toString();
	}

    /**
	 * The reference service enables you to establish the relationships between a master design file and 
	 * its dependencies. Call this API after you have uploaded your files using the upload 
	 * API ( OSS Upload API v1.0 ), and before you register them with the Translation Service to create a 
	 * viewable ( Post Data ). This is a simple json API where you specify the master file name and a list 
	 * of reference file names. You must also specify the parent/child relationships between the files 
	 * using the childPath and parentPath elements.
	 * {
     * 		"master" : "urn:adsk.objects:os.object:alexbicalhobucket2/A1.iam",
     *			"dependencies" : [
     * 				{ 
     * 					"file" : "urn:adsk.objects:os.object:alexbicalhobucket2/A1A1.iam",
     *					"metadata" : 
     *					{
     *       				"childPath" : "A1A1.iam",
     *       				"parentPath" : "A1.iam"
     *   				}
     * 				},
     *				{ 
     *					"file" : "urn:adsk.objects:os.object:alexbicalhobucket2/A1P1.ipt",
     *   				"metadata" : 
     *   				{
     *       				"childPath" : "A1P1.ipt",
     *       				"parentPath" : "A1.iam"
     *   				}
     * 				},
     * 				{ 
     * 					"file" : "urn:adsk.objects:os.object:alexbicalhobucket2/A1P2.ipt",
     *   				"metadata" : 
     *   				{
     *       				"childPath" : "A1P2.ipt",
     *       				"parentPath" : "A1.iam"
     *   				}
     *				}
     * 			]
	 *	}
	 * @param unrMaster
	 * @param urnChildren
	 * @return
	 */
    public Result linkFileSet(
		FileReference master,
		FileReference children[]
	) 
		    throws IOException, 
	           URISyntaxException
	{
    	String scope[] = {};
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new ResultObjectDetail( authResult );
		}
		
		JSONObject j_reference = new JSONObject();
		j_reference.put( KEY_MASTER, master.getUrn() );
		JSONArray j_childern = new JSONArray();
		j_reference.put( KEY_DEPENDENCIES, j_childern );
	
		for( int i = 0; children != null && i < children.length; i++ )
		{
			JSONObject j_child = new JSONObject();
			j_child.put( KEY_FILE, children[i].getUrn() );
			
			JSONObject j_metadata = new JSONObject();
			j_metadata.put( KEY_CHILD_PATH, children[i].getKey() );
			j_metadata.put( KEY_PARENT_PATH, master.getKey() );
			j_child.put( KEY_METADATA, j_metadata );
			            
			j_childern.add( j_child );
		}

		String jStr = j_reference.toString();
		String frag = makeURN( API_REF, PATT_LINK, null );
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag , null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "POST" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "application/json" );
		connection.setRequestProperty( "Content-Length", "" + jStr.length() );
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		connection.setDoOutput( true );
        OutputStream os = null;
        try
        {
            os = connection.getOutputStream();
            os.write(jStr.getBytes(Charset.forName("UTF-8")));
        }
        finally
        {
        	if( os != null ) os.close();
        }
		
		Result result = new Result( connection );
		return result;
	}
	
	private String makeURN(
		int    api,
		String pattern,
		String params[]
	) {
		String URN = pattern.replace( "%VER", lookupVersion( api ) );
		for( int i = 0; params != null && i < params.length; i++ )
		{
			URN = URN.replace( "%" + (i + 1), params[i] );
		}
		return URN;
	}

	public Result objectDelete(
		String bucketKey,
		String objectKey
	) 
	    throws IOException, 
	           URISyntaxException
	{
		String scope[] = { SCOPE_DATA_WRITE };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new Result( authResult );
		}
		
		String params[] = { bucketKey.toLowerCase(), objectKey.toLowerCase() };
		String frag = makeURN( API_OSS, PATT_OBJECT_DELETE, params );

		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "DELETE" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		return new Result( connection );
	}
	
	public ResultObjectDetail objectQueryDetails(
		String bucketKey,
		String objectKey
	) 
	    throws IOException, 
	           URISyntaxException
	{
		String scope[] = { SCOPE_DATA_READ };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new ResultObjectDetail( authResult );
		}
		
		String params[] = { bucketKey.toLowerCase(), objectKey.toLowerCase() };
		String frag = makeURN( API_OSS, PATT_OBJECT_QUERY, params );

		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "GET" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		return new ResultObjectDetail( connection );
	}
	
	public ResultObjectList objectList(
		String bucketKey,
		String keyBeginsWith
	) 
	    throws IOException, 
	           URISyntaxException
	{
		ResultObjectList result  = null;
		String           startAt = null;
		int returnCount          = 0;

		String params[] = { bucketKey.toLowerCase() };
		String frag = makeURN( API_OSS, PATT_OBJECT_LIST, params );

		String scope[] = { SCOPE_DATA_READ };
		
		do
		{
			ResultAuthentication authResult = authenticate( scope );
			if( authResult.isError() )
			{
				return new ResultObjectList( authResult );
			}
			
			String query = "limit=" + _objectListChunkSize;
			if( startAt != null )
			{
				query = query + "&startAt=" + startAt; 
			}
			if( keyBeginsWith != null && keyBeginsWith.length() > 0 )
			{
				query = query + "&" + KEY_BEGINS_WITH + "=" + keyBeginsWith; 
			}
	
			URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, query, null );
	
			URL url = new URL( uri.toASCIIString() );
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	
			connection.setRequestMethod( "GET" );
			authResult.setAuthHeader( connection );
	        connection.setRequestProperty( "Accept", "Application/json" );
			connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

			ResultObjectList resultNew = new ResultObjectList( connection );
			returnCount = resultNew.size();
			ViewerObject object = resultNew.getObject( returnCount - 1 );
			if( object != null )
			{
				startAt = object.getKey();
			}
	
			if( result == null )
			{
				result = resultNew;
			}
			else
			{
				result.append( resultNew );
			}
		}
		while( returnCount == _objectListChunkSize );
		return result;
	}
    
	public ResultObjectDetail objectUpload(
		String bucketKey,
		String objectKey,
		String fileName
	) 
	    throws IOException, 
	           URISyntaxException, 
	           GeneralSecurityException
	{
		String scope[] = { SCOPE_DATA_CREATE, SCOPE_DATA_WRITE };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new ResultObjectDetail( authResult );
		}

		FileInputStream fis = null; 
		try
		{
			File file = new File( fileName );
			file.length();
			
			String fileSha1;
			try
			{
				fileSha1 = createSha1( file );
			}
			catch( FileNotFoundException fnf )
			{
				return new ResultObjectDetail( fnf );
			}
			
			fis = new FileInputStream( file ); 

			String params[] = { bucketKey.toLowerCase(), objectKey.toLowerCase() };
			String frag = makeURN( API_OSS, PATT_OBJECT_UPLOAD, params );

			URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

			URL url = new URL( uri.toASCIIString() );
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			String contentType;
			int index = fileName.lastIndexOf( '.' );
			if( index > 0 )
			{
				String extension = fileName.substring( index );
				contentType = "image/vnd" + extension;
			}
			else
			{
				contentType = "application/octet-stream";
			}
			
			connection.setRequestMethod( "PUT" );
			authResult.setAuthHeader( connection );
			connection.setRequestProperty( "Content-Length", "" + file.length() );
	        connection.setRequestProperty( "Accept", "Application/json" );
			connection.setRequestProperty( "Content-Type",contentType );

			connection.setDoOutput( true );
	        OutputStream os = null;
	        try
	        {
	            os = connection.getOutputStream();
		        byte[] buffer = new byte[1024];
		        int len = fis.read(buffer);
				while( len != -1 )
				{
					os.write( buffer, 0, len );
					len = fis.read( buffer );
				}
	        }
	        finally
	        {
	        	if( os != null ) os.close();
	        }
			ResultObjectDetail result = new ResultObjectDetail( connection );
			ViewerObject objects[] = result.getObjects();
			if( objects == null || objects.length == 0 )
			{
				result.setError( Result.API_ERR_NO_OBJECT );
	        	return result;
			}
			String uploadSha1 = objects[0].getSha1();
			if( !fileSha1.equalsIgnoreCase( uploadSha1 ))
			{
				result.setError( Result.API_ERR_BAD_CHECKSUM );
	        	return result;
			}
			return result;
		}
		finally
		{
			if( fis != null ) fis.close();
		}
	}

	/**
	 * 
	 * @return
	 * @throws GeneralSecurityException 
	 */
	public ResultObjectDetail objectUploadChunked(
		String         bucketKey,
		String         objectKey,
		String         fileName,
		UploadProgress tracker
	) 
	    throws URISyntaxException, 
	           GeneralSecurityException, 
	           IOException
    {
		String scope[] = { SCOPE_DATA_CREATE, SCOPE_DATA_WRITE };
		String params[] = new String[2];
		params[0] = bucketKey.toLowerCase();
		
		params[1] = objectKey.toLowerCase();
		
		String frag = makeURN( API_OSS, PATT_OBJECT_UPLOAD_RESUMABLE, params );
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

		URL url = new URL( uri.toASCIIString() );
		
		long sessionId = System.currentTimeMillis();
		
		ResultObjectDetail result = null;
		FileInputStream  fis = null;
		byte buf[] = new byte[ _uploadChunkSize ];
		try
		{
			File srcFile     = new File( fileName );
			long srcFileSize = srcFile.length();
			String fileSha1;
			try
			{
				fileSha1 = createSha1( srcFile );
		        fis = new FileInputStream( srcFile );
			}
			catch( FileNotFoundException fnf )
			{
				return new ResultObjectDetail( fnf );
			}

	        long bytesRead = 0;
			long offset = 0;

			if( tracker != null )
			{
				tracker.progress( null, offset, srcFileSize );
			}
			
			while( bytesRead >= 0 && offset + 1 < srcFileSize )
			{
				result = null;
				bytesRead = fis.read( buf );
				if( bytesRead > 0 )
				{
					int retry = 0;
					for(;;)
					{
						ResultAuthentication authResult = authenticate( scope );
						if( authResult.isError() )
						{
							if( fis != null )
							{
								try
								{
									fis.close();
								}
								catch( Exception e )
								{ /* ignore */ }
							}
							return new ResultObjectDetail( authResult );
						}
						
						HttpURLConnection connection = (HttpURLConnection)url.openConnection();
						connection.setRequestMethod( "PUT" );
						authResult.setAuthHeader( connection );
						connection.setRequestProperty( "Content-Length", "" + srcFileSize );
						connection.setRequestProperty( "Content-Type", "application/stream" );
						String range = "bytes " + offset +"-" + ( offset + bytesRead - 1) + "/" + srcFileSize;
						connection.setRequestProperty( "Content-Range", range);
				        connection.setRequestProperty( "Accept", "Application/json" );
				        connection.setRequestProperty( "Session-Id", "" + sessionId );
				        connection.setDoOutput( true );
						
				        OutputStream os = null;
				        try
				        {
					        os = connection.getOutputStream();
							os.write( buf, 0, (int)bytesRead );
				        }
				        finally
				        {
							if( os != null )
							{
								try
								{
									os.close();
								}
								catch( Exception e )
								{ /* ignore */ }
							}
				        }

				        // Capture the first error retries may generate a different error message such as
				        // overlapping range.
				        if( result == null )
				        {
					        result = new ResultObjectDetail( connection );
				        }
				        retry++;
				        if( !result.isError() || retry >= _upLoadRetryCount )
				        {
				        	break;
				        }
				        try
                        {
	                        Thread.sleep( _uploadRetryDelay );
                        }
                        catch( InterruptedException e )
                        {
                        	break;
                        }
					}

					offset += bytesRead;

			        if( tracker != null )
					{
						tracker.progress( result, offset, srcFileSize );
					}
			        if( result.isError() )
			        {
			        	return result;
			        }
				}
			}
			ViewerObject objects[] = result.getObjects();
			if( objects == null || objects.length == 0 )
			{
				result.setError( Result.API_ERR_NO_OBJECT );
	        	return result;
			}
			String uploadSha1 = objects[0].getSha1();
			if( uploadSha1 == null )
			{
				ResultObjectDetail rod = objectQueryDetails( bucketKey, objectKey );
				ViewerObject objs[] = rod.getObjects();
				if( objs != null && objs.length > 0 )
				{
					objects = objs;
					uploadSha1 = objects[0].getSha1();
				}
			}
			if( uploadSha1 != null && !fileSha1.equalsIgnoreCase( uploadSha1 ))
			{
				result.setError( Result.API_ERR_BAD_CHECKSUM );
	        	return result;
			}
		}
		finally
		{
			if( fis != null )
			{
				try
				{
					fis.close();
				}
				catch( Exception e )
				{ /* ignore */ }
			}
		}
		return result;
	}

	public Result viewableDeregister(
		String viewableURN
	) 
	    throws IOException, 
	           URISyntaxException
	{
		String scope[] = { SCOPE_DATA_READ, SCOPE_DATA_WRITE };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return authResult;
		}

		viewableURN = viewableURN.toLowerCase();
		viewableURN = new String( Base64.encodeBase64( viewableURN.getBytes() ) );
		String params[] = { viewableURN };
		String frag = makeURN( API_VIEWING, PATT_VIEW_DEREGISTER, params );
		
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "DELETE" );
		authResult.setAuthHeader( connection );
//        connection.setRequestProperty( "Accept", "Application/json" );
		
		return new Result( connection );
	}
	
	public ResultDownload viewableDownload(
		String derivitiveURN,
		String dirName,
		String fileName
	)
	    throws IOException, 
        URISyntaxException
    {
		if( fileName.startsWith( "/" ))
		{
			fileName = fileName.substring( 1 );
		}
		
		int idx = fileName.lastIndexOf( "/" );
		if( idx >= 0 )
		{
			String relPath = fileName.substring( 0, idx );
			File path = new File( dirName + "/" + relPath );
			path.mkdirs();
		}
		File file = new File( dirName + "/" + fileName );
		file.delete();
		file.createNewFile();
				
		FileOutputStream fos = new FileOutputStream( dirName + "/" + fileName );

		String urnPrefix = "urn:adsk.viewing:fs.file:";
		return viewableDownload( urnPrefix + derivitiveURN + "/" + fileName, fos );
	}

	
	public ResultDownload viewableDownload(
		String       derivitiveURN,
		OutputStream os
	) 
	    throws IOException, 
	           URISyntaxException
	{
		String scope[] = { SCOPE_DATA_READ };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new ResultDownload( authResult );
		}
		
		String params[] = { derivitiveURN };
		String frag = makeURN( API_VIEWING, PATT_VIEW_DOWNLOAD, params );
	
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );
		
		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	
		connection.setRequestMethod( "GET" );
		authResult.setAuthHeader( connection );
	    connection.setRequestProperty( "Content-Type", "application/octet-stream" );
	    
		return new ResultDownload( connection, os );
	}

		
	public ResultViewerService viewableQuery(
		String viewableURN
	) 
	    throws IOException, 
	           URISyntaxException
	{
		String scope[] = { SCOPE_DATA_READ };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new ResultViewerService( authResult );
		}
		
		viewableURN = viewableURN.toLowerCase();
		viewableURN = new String( Base64.encodeBase64( viewableURN.getBytes() ) );

		String params[] = { viewableURN };
		String frag = makeURN( API_VIEWING, PATT_VIEW_QUERY, params );

		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "GET" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );

		return new ResultViewerService( connection );
	}

	/**
	 * Returns a list of model view (metadata) IDs for a design model. The metadata ID enables end
	 * users to select an object tree and properties for a specific model view. 
	 *  
	 * <p>Although most design apps (e.g., Fusion and Inventor) only allow a single model view 
	 * (object tree and set of properties), some apps (e.g., Revit) allow users to design models 
	 * with multiple model views (e.g., HVAC, architecture, perspective). 
	 *  
	 * <p>Note that you can only retrieve metadata from  an input file that has been translated into 
	 * an SVF file.
	 *  
	 * @param viewableURN The Base64 (URL Safe) encoded design URN
	 */
	public ResultViewableMetadata viewableQueryMetadata(
		String viewableURN
	) 
	    throws IOException, 
	           URISyntaxException
	{
		String scope[] = { SCOPE_DATA_READ };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return new ResultViewableMetadata( authResult );
		}
		
		viewableURN = viewableURN.toLowerCase();
		viewableURN = new String( Base64.encodeBase64( viewableURN.getBytes() ) );

		String params[] = { viewableURN };
		String frag = makeURN( API_VIEWING, PATT_VIEW_METADATA, params );

		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag, null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "GET" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );

		return new ResultViewableMetadata( connection );
	}

	public Result viewableRegister(
		String viewableURN
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return viewableRegister( viewableURN, "us", false, null, false, false );
	}

	public Result viewableRegister(
		String viewableURN,
		String region,
		boolean compressedURN,
		String  rootFileName,
		boolean test,
		boolean force
	) 
	    throws IOException, 
	           URISyntaxException
	{
		String scope[] = { SCOPE_DATA_CREATE, SCOPE_DATA_READ, SCOPE_DATA_WRITE };
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return authResult;
		}

		viewableURN = new String( Base64.encodeBase64( viewableURN.getBytes() ) );
		JSONObject j_input = new JSONObject();
		j_input.put( KEY_URN, viewableURN );
		if( compressedURN )
		{
			j_input.put( KEY_COMPRESSED_URN, true );
			if( rootFileName != null && rootFileName.length() > 0 )
			{
				j_input.put( KEY_ROOT_FILENAME, rootFileName );
			}
		}

		JSONObject j_destination = new JSONObject();
		j_destination.put( KEY_REGION, region );
		
		JSONObject j_format = new JSONObject();
		j_format.put( KEY_TYPE, "svf" );
		JSONArray j_views = new JSONArray();
		j_views.add( "2d" );
		j_views.add( "3d" );
		j_format.put( KEY_VIEWS, j_views );
		JSONArray j_formats = new JSONArray();
		j_formats.add( j_format );
		
		JSONObject j_output = new JSONObject();
		j_output.put( KEY_DESTINATION,  j_destination );
		j_output.put( KEY_FORMATS,  j_formats );

		JSONObject j_obj = new JSONObject();
		j_obj.put( KEY_INPUT,  j_input );
		j_obj.put( KEY_OUTPUT, j_output );

		String jStr = j_obj.toString();
		
		String frag = makeURN( API_VIEWING, PATT_VIEW_REGISTER, null );
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag , null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "POST" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );
		connection.setRequestProperty( "Content-Length", "" + jStr.length() );
		if( force )
		{
			connection.setRequestProperty( "x-ads-force", "true" );
		}
		if( test )
		{
			connection.setRequestProperty( "x-ads-test", "true" );
		}
		connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );

		connection.setDoOutput( true );
        OutputStream os = null;
        try
        {
            os = connection.getOutputStream();
            os.write(jStr.getBytes(Charset.forName("UTF-8")));
        }
        finally
        {
        	if( os != null ) os.close();
        }
		
		return new Result( connection );
	}

	/**
	 * The supported API was added to the Viewing Service to provide the following information: 
	 * The allowed extensions 
	 * The extensions to channel mapping 
	 * The RegExp information
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Result viewableSupported() 
	    throws IOException, 
	           URISyntaxException
	{
		String scope[] = {};
		ResultAuthentication authResult = authenticate( scope );
		if( authResult.isError() )
		{
			return authResult;
		}

		String frag = makeURN( API_VIEWING, PATT_VIEW_SUPPORTED, null );
		URI uri = new URI( _protocol, null, lookupHostname(), _port, frag , null, null );

		URL url = new URL( uri.toASCIIString() );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "GET" );
		authResult.setAuthHeader( connection );
        connection.setRequestProperty( "Accept", "Application/json" );

		return new Result( connection );
	}
	
	
	/**
	 * Extracts the Bucket key from a Forge service Model URN.
	 * It is possible the viewable is stored somewhere other than the Forge service in
	 * which case there is no Bucket Key
	 * @return Bucket key or ""
	 */
	public static String bucketFromBase64URN(
		String base64URN
	) {
		String modelURN = "";
		if( base64URN.toLowerCase().startsWith( "urn:" ) )
		{
			base64URN = base64URN.substring( 4 ).trim();
		}
		modelURN = new String( Base64.decodeBase64( base64URN.getBytes() ) );
		return bucketFromModelURN( modelURN );
	}

	/**
	 * Extracts the Bucket key from a Forge service Model URN.
	 * It is possible the viewable is stored somewhere other than the Forge service in
	 * which case there is no Bucket Key
	 * @return Bucket key or ""
	 */
	public static String bucketFromModelURN(
		String modelURN
	) {
		String bucketKey = "";
		// urn:adsk.objects:os.object:test27apfkowtolsxergcabqjvg3obggunhda/office_a.rvt
		if( modelURN.startsWith( FORGE_SERVICE_URN_PREFIX ))
		{
			bucketKey = modelURN.substring( FORGE_SERVICE_URN_PREFIX.length() );
			int idx = bucketKey.indexOf( '/' );
			
			if( idx <= 0 ) return "";
			
			bucketKey = bucketKey.substring( 0, idx );
		}
		return bucketKey;
	}

	static String ioStream2String(
	    HttpURLConnection connection 
    ) {
		InputStream is = connection.getErrorStream();
		if( is == null ) return "";
		
		try
		{
			return ioStream2String( is );
		}
		catch( IOException e )
		{
			return "";
		}
	}
	static String ioStream2String(
	    InputStream is 
    )
	    throws IOException
	{
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );

		String output;
		StringBuffer buf = new StringBuffer();
		while( ( output = br.readLine() ) != null )
		{
			buf.append( output );
		}
		return buf.toString();
	}
	
	static void printHttpError(
	    HttpURLConnection connection 
    ) {
		InputStream is = connection.getErrorStream();
		if( is == null ) return;
		
		try
		{
			System.out.println( ioStream2String( is ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	static void printHttpHeaders(
	    HttpURLConnection connection 
    ) {
	    Map<String, List<String>> headers = connection.getHeaderFields();
	    Set<String> keys = headers.keySet();
	    Iterator<String> itr = keys.iterator();
	    while( itr.hasNext() )
	    {
	    	String key = itr.next();
	    	List<String> values = headers.get( key );
	    	StringBuffer sb = new StringBuffer();
	    	sb.append(  key ).append( ":" );
	    	Iterator<String> itrV = values.iterator();
	    	while( itrV.hasNext() ) sb.append( " " ).append( itrV.next() );
	    	System.out.println( sb );
	    }
	}
}