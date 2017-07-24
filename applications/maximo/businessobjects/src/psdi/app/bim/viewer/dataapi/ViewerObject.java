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

import java.util.Iterator;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;


public class ViewerObject
{
	public static final String	KEY_BUCKETKEY    = "bucketKey";
	public static final String	KEY_LOCATION     = "location";    
	public static final String	KEY_SIZE         = "size";
	public static final String	KEY_KEY          = "key";
	public static final String	KEY_OBJECT_KEY   = "objectKey";
	public static final String	KEY_ID           = "id";
	public static final String	KEY_OBJECT_ID    = "objectId";
	public static final String	KEY_SHA_1        = "sha-1";
	public static final String	KEY_SHA1         = "sha1";
	public static final String	KEY_CONTENT_TYPE = "content-type";
	public static final String	KEY_CONTENTTYPE  = "contentType";
	public static final String	KEY_BLOCKSIZES   = "blockSizes";
	
	private String	     _bucketKey = "";    
	private String	     _location;    
	private long 	     _size = -1;
	private String	     _key;
	private String	     _id;
	private String	     _sha_1;
	private String	     _contentType;
	private long         _blockSizes[];


	ViewerObject(
	         	String	location,    
	        	long	size,
	        	String	key,
	        	String	id,
	        	String	sha_1,
	        	String	contentType
    ) {
		_location    = location;    
		_size        = size;
		_key         = key;
		_id          = id;
		_sha_1       = sha_1;
		_contentType = contentType;
	}

	/**
	 * 
	 * @author Doug
	 * {  
	 * 		"location" : "https://developer.api.autodesk.com/oss/v1/buckets/eawtv1rfx17gumg0j9erqzhd8ll7qcn6_test_test/objects/clinic",    
	 * 		"size" : 1592751,    
	 * 		"key" : "clinic",    
	 * 		"id" : "urn:adsk.objects:os.object:eawtv1rfx17gumg0j9erqzhd8ll7qcn6_test_test/clinic",    
	 * 		"sha-1" : "26643645c88bcea142603f24126a756367e11721",    
	 * 		"content-type" : "*"  
	 * 	} 
	 */
	ViewerObject(
	    JSONObject jObj 
    ) {
		if( jObj != null )
		{
			Object value = jObj.get( KEY_BUCKETKEY );
			if( value != null && value instanceof String )
			{
				_bucketKey = (String)value;
			}

			value = jObj.get( KEY_LOCATION );
			if( value != null && value instanceof String )
			{
				_location = (String)value;
			}


	        value = jObj.get( KEY_SIZE );
	        if( value != null && value instanceof Long )
	        {
	        	_size = (Long)value;
	        }
	        
			value = jObj.get( KEY_KEY );		// V1
			if( value != null && value instanceof String )
			{
				_key = (String)value;
			}
			else		// V2
			{
				value = jObj.get( KEY_OBJECT_KEY );
				if( value != null && value instanceof String )
				{
					_key = (String)value;
				}
				
			}

			value = jObj.get( KEY_ID );		// V1
			if( value != null && value instanceof String )
			{
				_id = (String)value;
			}
			else		// V2
			{
				value = jObj.get( KEY_OBJECT_ID );
				if( value != null && value instanceof String )
				{
					_id = (String)value;
				}
			}

			value = jObj.get( KEY_SHA_1 );		// V1
			if( value != null && value instanceof String )
			{
				_sha_1 = (String)value;
			}
			else
			{
				value = jObj.get( KEY_SHA1 );	// V2
				if( value != null && value instanceof String )
				{
					_sha_1 = (String)value;
				}
			}

			value = jObj.get( KEY_CONTENT_TYPE );	// V1
			if( value != null && value instanceof String )
			{
				_contentType = (String)value;
			}
			else		// V2
			{
				value = jObj.get( KEY_CONTENTTYPE );
				if( value != null && value instanceof String )
				{
					_contentType = (String)value;
				}
			}

			value = jObj.get( KEY_BLOCKSIZES );	// V2
			if( value != null && value instanceof JSONArray )
			{
	        	JSONArray jArray = (JSONArray)value;
	        	_blockSizes = new long[ jArray.size() ];

	        	@SuppressWarnings( "rawtypes" )
	            Iterator itr = jArray.listIterator();
	        	int i = 0;
	        	while( itr.hasNext() )
	        	{
	        		value = itr.next();
	        		if( value instanceof Long )
	        		{
	        			_blockSizes[i++] = (Long)value;
	        		}
	        	}
			}
		}
	}

	public String getBucketKey()
	{
		return _bucketKey;
	}

	public String getLocation()
	{
		return _location;
	}

	public long getSize()
	{
		return _size;
	}

	public String getKey()
	{
		return _key;
	}

	public String getId()
	{
		return _id;
	}

	public String getSha1()
	{
		return _sha_1;
	}

	public String getContentType()
	{
		return _contentType;
	}

	@Override
    public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append( KEY_KEY ).append( " = " ).append( _key );
		buf.append( ", \t" );
		if( _bucketKey != null && _bucketKey.length() > 0 )
		{
			buf.append( KEY_BUCKETKEY ).append( " = " ).append( _bucketKey );
			buf.append( ", " );
		}
		buf.append( KEY_LOCATION ).append( " = " ).append( _location );
		buf.append( ", " );
		buf.append( KEY_SIZE ).append( " = " ).append( _size );
		buf.append( ", " );
		buf.append( KEY_ID ).append( " = " ).append( _id );
		buf.append( ", " );
		buf.append( KEY_SHA_1 ).append( " = " ).append( _sha_1 );
		buf.append( ", " );
		if( _contentType != null && _contentType.length() > 0 )
		{
			buf.append( KEY_CONTENT_TYPE ).append( " = " ).append( _contentType );
			buf.append( ", " );
		}
		if( _blockSizes != null )
		{
			buf.append( ", " );
			buf.append( KEY_BLOCKSIZES ).append( " = " );
			for( int i = 0; i < _blockSizes.length; i++ )
			{
				buf.append( " " + _blockSizes[i] );
			}
		}
		
		return buf.toString();
	}
}