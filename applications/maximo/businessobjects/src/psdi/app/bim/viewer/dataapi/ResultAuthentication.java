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
package psdi.app.bim.viewer.dataapi;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

public class ResultAuthentication
    extends Result
    implements AuthToken
{

	private static final String KEY_TOKEN_TYPE   = "token_type";
	private static final String KEY_EXPIRES_IN   = "expires_in";
	private static final String KEY_ACCESS_TOKEN = "access_token";
	
	private static final String KEY_DEV_MSG    = "developerMessage";
	private static final String KEY_ERROR_CODE = "errorCode";
	private static final String KEY_USER_MSG   = "userMessage";
	private static final String KEY_MORE_INFO  = "more info";
	
	private String _token_type;
	private long   _expires_in;
	private String _access_token;
	private long   _timeStamp;

	
	ResultAuthentication(
	    Result result 
    ) {
		super( result );
	}

	ResultAuthentication(
	    HttpURLConnection connection 
    )
	    throws IOException
	{
		super( connection );
	}
	
	ResultAuthentication(
	    Exception e 
    ) {
		super( e );
	}
	
	/**
	 * Failed authenticate because the specified scope is not allowed
	 * @param scope
	 */
	ResultAuthentication(
	    String scope 
    ) {
		super();
		setError( Result.API_ERR_SCOPE_REJECTED );
		setErrorMessage( scope );
		setHttpStatus( 403 );
		_rawError = "{ \"ErrorCode\" : " + Result.API_ERR_SCOPE_REJECTED + ", \"ErrorType\" : \"access\", \"ErrorMessage\" : \"" + scope + "\" }";
	}
	
	/**
	 * {"token_type":"Bearer","expires_in":7200,"access_token":"Qdl8N6KPoyfAX3Lumtgia6leqacd"}
	 * @return 
	 */
	public String getAuthTokenJSOM()
	{
		JSONObject jobj = new JSONObject();
		jobj.put( KEY_TOKEN_TYPE, _token_type );
		if( isError() )
		{
			jobj.put( "ErrorMessage", getErrorMessage() );
			String errorType = "";
			switch( getErrorType() )
			{
			case NONE:
				errorType = "None";
				break;
			case HTTP:
				errorType = "HTTP";
				break;
			case REST:
				errorType = "REST";
				break;
			case API:
				errorType = "API";
				break;
			case EXCEPTION:
				errorType = "EXCEPTION";
				break;
			}
			jobj.put( "ErrorType",    errorType );
			jobj.put( "ErrorCode",    getErrorCode());
		}
		else
		{
			jobj.put( KEY_TOKEN_TYPE, _token_type );
			jobj.put( KEY_EXPIRES_IN, "" + _expires_in );
			jobj.put( KEY_ACCESS_TOKEN, _access_token );
		}

		return jobj.toString();
	}

   @Override
	public String getTokenType()
	{
		return _token_type;
	}

    @Override
	public long getExpiresIn()
	{
		return _expires_in;
	}

    @Override
	public String getAccessToken()
	{
		return _access_token;
	}

    @Override
	public long getTimeStamp()
	{
		return _timeStamp;
	}

	
	public boolean isExpired()
	{
		long sysTime = System.currentTimeMillis();
		return _timeStamp + (_expires_in - 2 ) * 1000  < sysTime;
	}
	
	public void setAuthHeader(
		HttpURLConnection connection
	) {
        connection.setRequestProperty( "Authorization", _token_type + " " + _access_token );
		connection.setRequestProperty( "cache-control", "no-cache" );
	}

   @Override
   protected JSONArtifact parseError(
    	String data
	) 
		throws IOException 
	{
	   	JSONArtifact jArtifact =  Result.string2JSON( data ); 
		
	   	if( jArtifact != null && jArtifact instanceof JSONObject )
	   	{
	   		String devMsg = "";
	   		String userMsg = "";
        	JSONObject jObj;
    		jObj = (JSONObject)jArtifact;
            Object value = jObj.get( KEY_DEV_MSG );
            if( value instanceof String )
            {
            	devMsg = (String)value;
            }

            value = jObj.get( KEY_USER_MSG );
            if( value != null )
            {
                if( value instanceof String )
                {
                	userMsg = (String)value;
                }
            }
            if( userMsg.length() > 0 )
            {
            	setErrorMessage( userMsg );
            }
            else
            {
            	setErrorMessage( devMsg );
            }

            value = jObj.get( KEY_MORE_INFO );
            if( value != null )
            {
                if( value instanceof String )
                {
                	setDetailMessage( (String)value );
                }
            }

            value = jObj.get( KEY_ERROR_CODE );
            if (value != null)
            {
                if( value instanceof String )
                {
                	setErrorCode( (String)value );
                }
            }
    	}
    	
        return jArtifact;
    }
   
    @Override
    protected JSONArtifact parseReturn(
    	String data
	) 
		throws IOException 
	{
		_timeStamp = System.currentTimeMillis();
		_expires_in = -1;
		
		JSONArtifact jArtifact = super.parseReturn( data );
		
        if( jArtifact == null ) return null;
    	JSONObject jObj;
    	if( !(jArtifact instanceof JSONObject) )
    	{
    		return null;
    	}
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
		
		return jArtifact;
    }
	
	@Override
    public String toString()
	{
		if( isError() )
		{
			return super.toString();
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append( KEY_TOKEN_TYPE );
		buf.append( " = " );
		buf.append( _token_type );
		buf.append( "\n" );
		buf.append( KEY_EXPIRES_IN );
		buf.append( " = " );
		buf.append( _expires_in );
		buf.append( "\n" );
		buf.append( KEY_ACCESS_TOKEN );
		buf.append( " = " );
		buf.append( _access_token );
		buf.append( "\n" );
		return buf.toString();
	}
}
