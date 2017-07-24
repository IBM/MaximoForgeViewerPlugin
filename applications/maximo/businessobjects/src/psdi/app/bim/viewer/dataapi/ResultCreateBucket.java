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

public class ResultCreateBucket
    extends Result
{
	private BucketDescription _bucket;

	public ResultCreateBucket()
	{
	}

	public ResultCreateBucket(
	    Result result )
	{
		super( result );
	}

	public ResultCreateBucket(
	    HttpURLConnection connection 
    )
	    throws IOException
	{
		super( connection );
	}

	/*
	 * Status Codes:
     * 200 - Success.
     * 400 - Bad request, the request could not be understood by the server due to malformed syntax or missing request headers. The client SHOULD NOT repeat the request without modifications.
     * 401 - The supplied Authorization header was not valid or the supplied token scope was not acceptable. Verify Authentication and try again.
     * 403 - The Authorization was successfully validated but permission is not granted. Don’t try again unless you solve permissions first.
     * 409 - Bucket does not exist.
     * 500 - Internal failure while processing the request, reason depends on error.
	 */
    @Override
    protected JSONArtifact parseError(
    	String data
	) 
		throws IOException 
	{
		JSONArtifact jArtifact = super.parseError( data );
		return jArtifact;
	}
    
    @Override
    protected JSONArtifact parseReturn(
    	String data
	) 
		throws IOException 
	{
    	JSONArtifact jArtifact = super.parseReturn( data );
        if( jArtifact == null ) return null;
    	JSONObject jObj;
    	if( !(jArtifact instanceof JSONObject) )
    	{
    		return null;
    	}
		jObj = (JSONObject)jArtifact;

		_bucket = new BucketDescription( jObj );

		return jObj;
	}
    
    @Override
    public String toString()
	{
		if( isError() )
		{
			return super.toString();
		}
		
        StringBuffer buf = new StringBuffer();
        if( _bucket != null  )
        {
        	buf.append( "\n " );
        	buf.append( _bucket );
        }
        
        if( buf.length() == 0 )
        {
        	buf.append( getRawData() );
        }

		return buf.toString();
	}
}