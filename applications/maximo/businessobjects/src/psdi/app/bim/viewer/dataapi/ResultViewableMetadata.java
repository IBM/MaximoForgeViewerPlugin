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

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

/*
 * 	{
 * 		"data":
 * 			{
 * 				"type":"metadata",
 * 				"metadata":
 * 					[
 * 						{
 * 							"name":"{3D}",
 * 							"guid":"fa82451c-5a4f-f2cb-4293-857e6096fc46"
 * 						}
 * 					]
 * 			}
 * 	}
 */
public class   ResultViewableMetadata
	   extends Result
{
	 public static final String KEY_DATA         = "data";
	 public static final String KEY_GUID         = "guid";
	 public static final String KEY_NAME         = "name";
	 public static final String KEY_METADATA     = "metadata";
	 public static final String KEY_TYPE         = "type";
	 
	 private String  _guid;
	 private String  _type;
	 private Scene   _scenes[];


	public ResultViewableMetadata(
	    Result result 
    ) {
		super( result );
	}

	public ResultViewableMetadata(
	    HttpURLConnection connection 
    )
	    throws IOException
	{
		super( connection );
	}
	
    public String getGuid()
	{
		return _guid;
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

		JSONObject jObjectData = null;
		Object value = jObj.get( KEY_DATA );
		if( value != null &&  value instanceof JSONObject )
		{
			jObjectData = (JSONObject)value;
		}
		else
		{
			return jObj;
		}
    	
		value = jObjectData.get( KEY_TYPE );
		if( value != null &&  value instanceof String )
		{
			_type = (String)value;
		}
    	

        value = jObjectData.get( KEY_METADATA );
        if (value != null  && value instanceof  JSONArray )
        {
        	JSONArray jArray = (JSONArray)value;
        	_scenes = new Scene[ jArray.size() ];

        	@SuppressWarnings( "rawtypes" )
            Iterator itr = jArray.listIterator();
        	int i = 0;
        	while( itr.hasNext() )
        	{
        		value = itr.next();
        		if( value instanceof JSONObject )
        		{
        			_scenes[i++] = new Scene( (JSONObject)value );
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
        if( _guid != null && _guid.length() > 0)
        {
            buf.append( KEY_GUID).append( ": " ).append( _guid ).append(  '\n' );
        }
        if( _type != null && _type.length() > 0)
        {
            buf.append( KEY_TYPE).append( ": " ).append( _type ).append(  '\n' );
        }
        
        if( _scenes != null && _scenes.length > 0 )
        {
        	buf.append( KEY_METADATA ).append( "\n " );
        	for( int i = 0; i < _scenes.length; i++ )
        	{
            	buf.append(  _scenes[i] ).append( "\n " );
        	}
        }

		return buf.toString();
	}
	
	public class Scene
	{
		 private String  _guid;
		 private String  _name;
		 
		private Scene(
			JSONObject jObj
	    ) {
	        if( jObj == null ) return;
	        
			Object value = jObj.get( KEY_NAME );
			if( value != null &&  value instanceof String )
			{
				_name = (String)value;
			}
	    	
			value = jObj.get( KEY_GUID );
			if( value != null &&  value instanceof String )
			{
				_guid = (String)value;
			}
		}
		
		public String getGuid()
		{
			return _guid;
		}

		public String getName()
		{
			return _name;
		}

		@Override
        public String toString()
		{
			StringBuffer buf = new StringBuffer();
			if( _name != null && _name.length() > 0 )
			{
				buf.append( KEY_NAME ).append( ": " ).append( _name ).append( '\n' );
			}
			if( _guid != null && _guid.length() > 0 )
			{
				buf.append( KEY_GUID ).append( ": " ).append( _guid ).append( '\n' );
			}

	        if( buf.length() == 0 )
	        {
	        	buf.append( getRawData() );
	        }

	        return buf.toString();
		}
	}
}