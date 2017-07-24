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
import java.util.Date;
import java.util.Iterator;



import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

/*
 * {
 * 	"type":"manifest",
 * 	"hasThumbnail":"true",
 * 	"status":"success",
 * 	"progress":"complete",
 * 	"region":"US",
 * 	"urn":"dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b24lMjBrZWVwJTIwMjAxNi5ydnQ",
 * 	"version":"1.0",
 * 	"derivatives":
 * 		[
 * 			{
 * 				"name":"barton%20keep%202016.rvt",
 * 				"hasThumbnail":"true",
 * 				"status":"success",
 * 				"progress":"complete",
 * 				"outputType":"svf",
 * 				"children":
 * 					[
 * 						{
 * 							"guid":"53c5d765-b5eb-af7f-51bc-84339935e054",
 * 							"name":"{3D}",
 * 							"hasThumbnail":"true",
 * 							"role":"3d",
 * 							"viewableID":"087d6d7f-e90b-44fc-95b3-3261546eeb21-000518ef",
 * 							"progress":"complete",
 * 							"status":"success",
 * 							"type":"geometry",
 * 							"children":
 * 								[
 * 									{
 * 										"guid":"087d6d7f-e90b-44fc-95b3-3261546eeb21-000518ef",
 * 										"name":"{3D}",
 * 										"role":"3d",
 * 										"camera":[-439.382965,547.938965,170.972549,7.391357,73.255463,-1.636921,0.175436,-0.186395,0.966685,3.28053,0,1,1],
 * 										"progress":"complete",
 * 										"status":"success",
 * 										"type":"view"
 * 									},
 * 									{
 * 										"guid":"fe99ce5f-4ccb-a843-0d91-92ffabf88d1e",
 * 										"role":"graphics",
 * 										"mime":"application/autodesk-svf",
 * 										"urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b24lMjBrZWVwJTIwMjAxNi5ydnQ/output/Resource/3D_View/_3D_ 334063/_3D_.svf",
 * 										"type":"resource"
 * 									},
 * 									{
 * 										"guid":"ef76a954-ca8a-81ca-9b20-b4f89d0a351c",
 * 										"role":"thumbnail",
 * 										"mime":"image/png","resolution":[100,100],
 * 										"urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b24lMjBrZWVwJTIwMjAxNi5ydnQ/output/Resource/3D_View/_3D_ 334063/_3D_1.png",
 * 										"status":"success",
 * 										"type":"resource"
 * 									},
 * 									{
 * 										"guid":"0beaf925-b5d8-e8e1-b42c-b0870e47fe27",
 * 										"role":"thumbnail","mime":"image/png",
 * 										"resolution":[200,200],
 * 										"urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b24lMjBrZWVwJTIwMjAxNi5ydnQ/output/Resource/3D_View/_3D_ 334063/_3D_2.png",
 * 										"status":"success",
 * 										"type":"resource"
 * 									},
 * 									{
 * 										"guid":"a6e36816-4206-e11d-65af-cdd14b8278b6",
 * 										"role":"thumbnail","mime":"image/png","resolution":[400,400],
 * 										"urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b24lMjBrZWVwJTIwMjAxNi5ydnQ/output/Resource/3D_View/_3D_ 334063/_3D_4.png",
 * 										"status":"success",
 * 										"type":"resource"
 * 									}
 * 								]
 * 						}
 * 					]
 * 				},
 * 				{
 * 					"status":"success",
 * 					"progress":"complete",
 * 					"outputType":"thumbnail",
 * 					"children":
 * 						[
 * 							{
 * 								"guid":"0576ce39-e54e-369e-da0e-7a306abf2144",
 * 								"role":"thumbnail",
 * 								"mime":"image/png",
 * 								"resolution":[100,100],
 * 								"urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b24lMjBrZWVwJTIwMjAxNi5ydnQ/output/preview1.png",
 * 								"status":"success",
 * 								"type":"resource"
 * 							},
 * 							{"guid":"b5608b0b-b2e4-d4d7-85eb-3817e1a4a69f","role":"thumbnail","mime":"image/png","resolution":[200,200],"urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b24lMjBrZWVwJTIwMjAxNi5ydnQ/output/preview2.png","status":"success","type":"resource"},{"guid":"72c1b16a-7ad7-5497-e0fd-3268ee880a0a","role":"thumbnail","mime":"image/png","resolution":[400,400],"urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b24lMjBrZWVwJTIwMjAxNi5ydnQ/output/preview4.png","status":"success","type":"resource"}]}]}
 */
public class   ResultViewerService
	   extends Result
{
	 public static final String KEY_GUID         = "guid";
	 public static final String KEY_TYPE         = "type";
	 public static final String KEY_HAS_TUMBNAIL = "hasThumbnail";
	 public static final String KEY_NAME         = "name";
	 public static final String KEY_MESSAGES     = "messages";
	 public static final String KEY_OUTPUTTYPE   = "outputType";
	 public static final String KEY_PROGRESS     = "progress";
	 public static final String KEY_PROPERTIES   = "properties";
	 public static final String KEY_START_AT     = "startedAt";
	 public static final String KEY_STATUS       = "status";
	 public static final String KEY_SUCCESS      = "success";
	 public static final String KEY_URN          = "urn";
	 public static final String KEY_CHILDREN     = "children";;
	 public static final String KEY_DERIVATIVES  = "derivatives";
	 public static final String KEY_ROLE         = "role";
	 public static final String KEY_REGION       = "region";
	 
	 private boolean _showDetails = false;

	 private String  _guid;
	 private String  _type;
	 private boolean _hasThumbnail = false;
	 private String  _progress;
	 private String  _region;
	 private String  _startedAt;
	 private String  _status;
	 private String  _success;
	 private String  _urn;
	 private Node    _children[];

	public ResultViewerService(
	    Result result 
    ) {
		super( result );
	}

	public ResultViewerService(
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

	public String getProgress()
	{
		return _progress;
	}

	public String getRegion()
	{
		return _region;
	}

	public String getStartedAt()
	{
		return _startedAt;
	}

	public String getStatus()
	{
		return _status;
	}

	public String getSuccess()
	{
		return _success;
	}

	public String getURN()
	{
		return _urn;
	}

	public boolean isHasThumbnail()
	{
		return _hasThumbnail;
	}
	
	public void setShowDetails(
		boolean flag
	) {
		_showDetails = flag;
	}

	@SuppressWarnings( "deprecation" )
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

		Object value = jObj.get( KEY_GUID );
		if( value != null &&  value instanceof String )
		{
			_guid = (String)value;
		}
    	
		value = jObj.get( KEY_TYPE );
		if( value != null &&  value instanceof String )
		{
			_type = (String)value;
		}
    	
		value = jObj.get( KEY_REGION );
		if( value != null &&  value instanceof String )
		{
			_region = (String)value;
		}
    	
		value = jObj.get( KEY_HAS_TUMBNAIL );
		if( value != null &&  value instanceof String )
		{
			_hasThumbnail = Boolean.parseBoolean( (String)value );
		}
		else if( value != null )
		{
        	System.out.println( value.getClass().getSimpleName() );
		}
    	
//        value = jObj.get( KEY_HAS_TUMBNAIL );
//        if (value != null)
//        {
//            ValueType type = value.getValueType();
//            if (type == ValueType.STRING )
//            {
//            	String str = jObj.getString( KEY_HAS_TUMBNAIL );
//            	_hasThumbnail = Boolean.parseBoolean( str );
//            }
//        }

		value = jObj.get( KEY_PROGRESS );
		if( value != null &&  value instanceof String )
		{
			_progress = (String)value;
		}
    	
		value = jObj.get( KEY_START_AT );
		if( value != null &&  value instanceof String )
		{
			_startedAt = (String)value;
		}
        if( value != null && value instanceof Long )
        {
        	_startedAt = new Date( (Long)value ).toGMTString();
        }
   	
		value = jObj.get( KEY_STATUS );
		if( value != null &&  value instanceof String )
		{
			_status = (String)value;
		}
   	
		value = jObj.get( KEY_SUCCESS );
		if( value != null &&  value instanceof String )
		{
			_success = (String)value;
		}

		value = jObj.get( KEY_URN );
		if( value != null &&  value instanceof String )
		{
			_urn = (String)value;
		}

        value = jObj.get( KEY_CHILDREN );			// v1
        if( value == null )
        {
            value = jObj.get( KEY_DERIVATIVES );	// v2
        }
        if (value != null  && value instanceof  JSONArray )
        {
        	JSONArray jArray = (JSONArray)value;
        	_children = new Node[ jArray.size() ];

        	@SuppressWarnings( "rawtypes" )
            Iterator itr = jArray.listIterator();
        	int i = 0;
        	while( itr.hasNext() )
        	{
        		value = itr.next();
        		if( value instanceof JSONObject )
        		{
        			_children[i++] = new Node( (JSONObject)value, 1 );
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
        buf.append( KEY_HAS_TUMBNAIL).append( ": " ).append( _hasThumbnail ).append(  '\n' );
        if( _startedAt != null )
        {
            buf.append( KEY_START_AT).append( ": " ).append( _startedAt ).append(  '\n' );
        }
        if( _progress != null && _progress.length() > 0 )
        {
            buf.append( KEY_PROGRESS).append( ": " ).append( _progress ).append(  '\n' );
        }
        if( _status != null && _status.length() > 0)
        {
            buf.append( KEY_STATUS).append( ": " ).append( _status ).append(  '\n' );
        }
        if( _success != null && _success.length() > 0)
        {
            buf.append( KEY_SUCCESS).append( ": " ).append( _success ).append(  '\n' );
        }
        if( _region != null && _region.length() > 0)
        {
            buf.append( KEY_REGION).append( ": " ).append( _region ).append(  '\n' );
        }
        if( _urn != null && _urn.length() > 0)
        {
            buf.append( KEY_URN ).append( ": " ).append( _urn ).append(  '\n' );
        }
        
        if( _children != null && _children.length > 0 )
        {
        	buf.append( KEY_CHILDREN ).append( "\n " );
        	for( int i = 0; i < _children.length; i++ )
        	{
            	buf.append(  _children[i] ).append( "\n " );
        	}
        }

		return buf.toString();
	}
	
	public class Node
	{
		 private String  _guid;
		 private String  _name;
		 private String  _type;
		 private boolean _hasThumbnail = false;
		 private Message _messages[];
		 private String  _outputType;
		 private String  _progress;
		 private String  _properties[];
		 private String  _role;
		 private String  _status;
		 private String  _success;
		 private String  _urn;
		 private Node    _children[];
		 
		 private int _level;
		 
		private Node(
			JSONObject jObj,
		    int        level
	    ) {
	        if( jObj == null ) return;
	        
	        _level = level;

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
	    	
			value = jObj.get( KEY_TYPE );
			if( value != null &&  value instanceof String )
			{
				_type = (String)value;
			}
	    	
			value = jObj.get( KEY_HAS_TUMBNAIL );
			if( value != null &&  value instanceof String )
			{
				_hasThumbnail = Boolean.parseBoolean( (String)value );
			}
	    	
			value = jObj.get( KEY_ROLE );
			if( value != null &&  value instanceof String )
			{
				_role = (String)value;
			}
	    	
			value = jObj.get( KEY_STATUS );
			if( value != null &&  value instanceof String )
			{
				_status = (String)value;
			}
	    	
			value = jObj.get( KEY_SUCCESS );
			if( value != null &&  value instanceof String )
			{
				_success = (String)value;
			}
	    	
			value = jObj.get( KEY_PROGRESS );
			if( value != null &&  value instanceof String )
			{
				_progress = (String)value;
			}
	    	
			value = jObj.get( KEY_OUTPUTTYPE );
			if( value != null &&  value instanceof String )
			{
				_outputType = (String)value;
			}
	    	
			// TODO Add message class and parse message
	        value = jObj.get( KEY_MESSAGES );
	        if (value != null  && value instanceof  JSONArray )
	        {
	        	JSONArray jArray = (JSONArray)value;
	        	_messages = new Message[ jArray.size() ];

	        	@SuppressWarnings( "rawtypes" )
	            Iterator itr = jArray.listIterator();
	        	int i = 0;
	        	while( itr.hasNext() )
	        	{
	        		value = itr.next();
	        		if( value instanceof JSONObject )
	        		{
	        			_messages[i++] = new Message( (JSONObject)value );
	        		}
	        	}
	        }

			value = jObj.get( KEY_URN );
			if( value != null &&  value instanceof String )
			{
				_urn = (String)value;
			}
	    	
	        value = jObj.get( KEY_PROPERTIES );
	        if (value != null  && value instanceof  JSONArray )
	        {
	        	JSONArray jArray = (JSONArray)value;
	        	_properties = new String[ jArray.size() ];

	        	@SuppressWarnings( "rawtypes" )
	            Iterator itr = jArray.listIterator();
	        	int i = 0;
	        	while( itr.hasNext() )
	        	{
	        		value = itr.next();
	        		if( value instanceof JSONObject )
	        		{
	        			_properties[i++] = new String( (String)value );
	        		}
	        	}
	        }

	        value = jObj.get( KEY_CHILDREN );
	        if (value != null  && value instanceof  JSONArray )
	        {
	        	JSONArray jArray = (JSONArray)value;
	        	_children = new Node[ jArray.size() ];

	        	@SuppressWarnings( "rawtypes" )
	            Iterator itr = jArray.listIterator();
	        	int i = 0;
	        	while( itr.hasNext() )
	        	{
	        		value = itr.next();
	        		if( value instanceof JSONObject )
	        		{
	        			_children[i++] = new Node( (JSONObject)value, 1 );
	        		}
	        	}
	        }
		}
		
		public String getGuid()
		{
			return _guid;
		}

		public String getType()
		{
			return _type;
		}

		public boolean hasThumbnail()
		{
			return _hasThumbnail;
		}

		public String getOutputType()
		{
			return _outputType;
		}

		public String getRole()
		{
			return _role;
		}

		public String getStatus()
		{
			return _status;
		}

		@Override
        public String toString()
		{
			StringBuffer buf = new StringBuffer();
			if( _name != null && _name.length() > 0 )
			{
				tabs( buf ).append( KEY_NAME ).append( ": " ).append( _name ).append( '\n' );
			}
			if( _guid != null && _guid.length() > 0 )
			{
				tabs( buf ).append( KEY_GUID ).append( ": " ).append( _guid ).append( '\n' );
			}
			if( _type != null && _type.length() > 0 )
			{
				tabs( buf ).append( KEY_TYPE ).append( ": " ).append( _type ).append( '\n' );
			}
			tabs( buf ).append( KEY_HAS_TUMBNAIL ).append( ": " ).append( _hasThumbnail ).append( '\n' );
			if( _role != null && _role.length() > 0 )
			{
				tabs( buf ).append( KEY_ROLE ).append( ": " ).append( _role ).append( '\n' );
			}
			if( _status != null && _status.length() > 0 )
			{
				tabs( buf ).append( KEY_STATUS ).append( ": " ).append( _status ).append( '\n' );
			}
	        if( _progress != null && _progress.length() > 0 )
	        {
	        	tabs( buf ).append( KEY_PROGRESS).append( ": " ).append( _progress ).append(  '\n' );
	        }
	        if( _success != null && _success.length() > 0)
	        {
	        	tabs( buf ).append( KEY_SUCCESS).append( ": " ).append( _success ).append(  '\n' );
	        }
	        if( _outputType != null && _outputType.length() > 0)
	        {
	        	tabs( buf ).append( KEY_OUTPUTTYPE).append( ": " ).append( _outputType ).append(  '\n' );
	        }
	        if( _urn != null && _urn.length() > 0)
	        {
	        	tabs( buf ).append( KEY_URN ).append( ": " ).append( _urn ).append(  '\n' );
	        }
	        
			if( _messages != null && _messages.length > 0 )
			{
				tabs( buf ).append( KEY_MESSAGES ).append( ": " ).append( "\t" );
				for( int i = 0; i < _messages.length; i++ )
				{
					tabs( buf ).append( "\t" ).append( _messages[i] ).append( '\n' );
				}
			}
			if( _properties != null && _properties.length > 0 )
			{
				tabs( buf ).append( KEY_PROPERTIES ).append( ": " ).append( "\t" );
				for( int i = 0; i < _messages.length; i++ )
				{
					tabs( buf ).append( "\t" ).append( _properties[i] ).append( '\n' );
				}
			}

	        if( _children != null && _children.length > 0 && _showDetails )
	        {
	        	tabs( buf ).append( KEY_CHILDREN ).append( "\n " );
	        	for( int i = 0; i < _children.length; i++ )
	        	{
	            	buf.append(  _children[i] ).append( "\n " );
	        	}
	        }

	        if( buf.length() == 0 )
	        {
	        	buf.append( getRawData() );
	        }

	        return buf.toString();
		}
		
		private StringBuffer tabs(
			StringBuffer buf
		) {
			for( int i = 0;  i < _level; i++ )
			{
				buf.append(  '\t' );
			}
			return buf;
		}
	}
}