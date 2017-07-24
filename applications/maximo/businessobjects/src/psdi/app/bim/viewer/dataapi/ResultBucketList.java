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
import java.util.Iterator;
import java.util.LinkedList;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

public class ResultBucketList
    extends Result
{
	public static final String	KEY_ITEMS = "items";  
	
	private LinkedList<BucketDescription> _buckets;
	
	public ResultBucketList()
	{
		super();
	}

	public ResultBucketList(
	    Result result )
	{
		super( result );
	}

	public ResultBucketList(
	    HttpURLConnection connection 
    ) {
		super( connection );
	}

	public ResultBucketList(
	    Exception e 
    ) {
		super( e );
	}
	
	public int size()
	{
		if( _buckets == null ) return 0;
		return _buckets.size();
	}
	
	public BucketDescription getBucket( int index )
	{
		if( _buckets == null ) return null;
		if( index < 0 || index >= _buckets.size() ) return null;
		return _buckets.get( index );
	}
	
	void append(
		ResultBucketList result
	) {
		if( result == null || result._buckets == null )
		{
			return;
		}
		if( _buckets == null )
		{
			_buckets = result._buckets;
			return;
		}
		_buckets.addAll( result._buckets );
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

		Object value = jObj.get( KEY_ITEMS );
	
		if (value != null  && value instanceof  JSONArray )
		{
			JSONArray jArray = (JSONArray)value;
			_buckets = new LinkedList<BucketDescription>();
		
			@SuppressWarnings( "rawtypes" )
		    Iterator itr = jArray.listIterator();
			while( itr.hasNext() )
			{
				value = itr.next();
				if( value instanceof JSONObject )
				{
					_buckets.add( new BucketDescription( (JSONObject)value ) );
				}
			}
		}

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
        if( _buckets != null && _buckets.size() > 0 )
        {
        	buf.append( "\n " );
        	Iterator<BucketDescription> itr = _buckets.iterator();
        	while( itr.hasNext() )
        	{
            	buf.append( '\t' ).append(  itr.next() ).append( "\n " );
        	}
        }
        
        if( buf.length() == 0 )
        {
        	buf.append( getRawData() );
        }

		return buf.toString();
	}
}