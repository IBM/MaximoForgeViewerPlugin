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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

/**
 * {
 *   "guid":"dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b25rZWVwLnppcA",
 *   "success":"100%",
 *   "hasThumbnail":"true",
 *   "progress":"complete",
 *   "urn":"dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b25rZWVwLnppcA",
 *   "status":"success",
 *   "startedAt":"Wed May 10 15:21:04 UTC 2017",
 *   "region":"US",
 *   "owner":"dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b25rZWVwLnppcA",
 *   "type":"design",
 *   "children":
 *   [
 *     {
 *       "guid":"aa85aad6-c480-4a35-9cbf-4cf5994a25ba",
 *       "name":"Barton Keep 2016.rvt",
 *       "success":"100%",
 *       "hasThumbnail":"true",
 *       "role":"viewable",
 *       "version":"2.0",
 *       "properties":
 *       {
 *         "Document Information":
 *         {
 *           "Project Name":"Barton Keep B&B",
 *           "Project Number":
 *           "Project Number",
 *           "Author":"",
 *           "Project Address":"1002 East Swedesford Road King of Prussia, PA 19406 United States",
 *           "Project Issue Date":"2003-05-25",
 *           "Project Status":"Complete",
 *           "Building Name":"Barton Keep B&B",
 *           "Client Name":"Eagale Noth America",
 *           "Organization Name":"North Hills Home",
 *           "Organization Description":""
 *         }
 *       },
 *       "progress":"complete",
 *       "urn":"dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b25rZWVwLnppcA",
 *       "status":"success",
 *       "type":"folder",
 *       "children":
 *       [
 *         {
 *           "guid":"8f55cfeb-b04c-4364-c9ab-4c88788f0966",
 *           "role":"Autodesk.CloudPlatform.DesignDescription",
 *           "mime":"application/json",
 *           "urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b25rZWVwLnppcA/output/designDescription.json",
 *           "status":"success",
 *           "type":"resource"
 *         },
 *         {
 *           "guid":"6fac95cb-af5d-3e4f-b943-8a7f55847ff1",
 *           "size":11796480,
 *           "role":"Autodesk.CloudPlatform.PropertyDatabase",
 *           "mime":"application/autodesk-db",
 *           "urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b25rZWVwLnppcA/output/Resource/model.sdb",
 *           "status":"success","type":"resource"
 *         },
 *         {
 *           "guid":"b3d264ca-e819-9185-b1a4-9d4d0502c1e9",
 *           "name":"Views",
 *           "success":"100%",
 *           "hasThumbnail":"true",
 *           "progress":"complete",
 *           "status":"success",
 *           "type":"folder",
 *           "children":
 *             [
 *               {
 *                 "guid":"b92bfc63-c1a3-3235-49f4-69782d4aa0fe",
 *                 "name":"3D View",
 *                 "success":"100%",
 *                 "hasThumbnail":"true",
 *                 "progress":"complete",
 *                 "status":"success",
 *                 "type":"folder",
 *                 "children":
 *                   [
 *                     {
 *                       "guid":"53c5d765-b5eb-af7f-51bc-84339935e054",
 *                       "name":"{3D}",
 *                       "success":"100%",
 *                       "size":38073607,
 *                       "hasThumbnail":"true",
 *                       "role":"3d",
 *                       "viewableID":"087d6d7f-e90b-44fc-95b3-3261546eeb21-000518ef",
 *                       "progress":"complete",
 *                       "status":"success",
 *                       "type":"geometry",
 *                       "children":
 *                         [
 *                           {
 *                             "guid":"087d6d7f-e90b-44fc-95b3-3261546eeb21-000518ef",
 *                             "name":"{3D}",
 *                             "role":"3d",
 *                             "camera":[-439.034668,547.917297,170.972549,7.739655,73.233826,-1.636921,0.175436,-0.186395,0.966685,3.28053,0,1,1],
 *                             "progress":"complete",
 *                             "status":"success",
 *                             "type":"view"
 *                           },
 *                           {
 *                             "guid":"fe99ce5f-4ccb-a843-0d91-92ffabf88d1e",
 *                             "size":38010719,
 *                             "role":"graphics",
 *                             "mime":"application/autodesk-svf",
 *                             "urn":"urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dGVzdDQ3YXBma293dG9sc3hlcmdjYWJxanZnM29iZ2d1bmhkYS9iYXJ0b25rZWVwLnppcA/output/Resource/3D_View/_3D_ 334063/_3D_.svf",
 *                             "type":"resource"
 *                           }
 *                         ]
 *                       }
 *                     ]
 *                   }
 *                 ]
 *               }
 *             ]
 *           }
 *         ]
 *       }
 *
 */
public class   ResultViewerService
	   extends Result
{
	 public static final String KEY_GUID         = "guid";
	 public static final String KEY_HAS_TUMBNAIL = "hasThumbnail";
	 public static final String KEY_NAME         = "name";
	 public static final String KEY_MIME         = "mime";
	 public static final String KEY_MESSAGES     = "messages";
	 public static final String KEY_OWNER        = "owner";
	 public static final String KEY_OUTPUTTYPE   = "outputType";
	 public static final String KEY_PROGRESS     = "progress";
	 public static final String KEY_PROPERTIES   = "properties";
	 public static final String KEY_SIZE         = "size";
	 public static final String KEY_START_AT     = "startedAt";
	 public static final String KEY_STATUS       = "status";
	 public static final String KEY_SUCCESS      = "success";
	 public static final String KEY_URN          = "urn";
	 public static final String KEY_CHILDREN     = "children";;
	 public static final String KEY_DERIVATIVES  = "derivatives";
	 public static final String KEY_ROLE         = "role";
	 public static final String KEY_REGION       = "region";
	 public static final String KEY_TYPE         = "type";
	 public static final String KEY_VIEWABLE_ID  = "viewableID";
	 
	 private boolean _showDetails = false;

	 private String  _guid;
	 private String  _type;
	 private boolean _hasThumbnail = false;
	 private String  _owner;
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
    
    public String getOwner()
    {
    	return _owner;
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

	public void listDerivativeFiles(
		List<String> files
	) {
        if( _children != null && _children.length > 0 )
        {
        	for( int i = 0; i < _children.length; i++ )
        	{
            	_children[i].listDerivativeFiles( files );
        	}
        }
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
    	
		value = jObj.get( KEY_OWNER );
		if( value != null &&  value instanceof String )
		{
			_owner = (String)value;
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
        			_children[i++] = new Node( (JSONObject)value, null, 1 );
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
        if( _owner != null && _owner.length() > 0 )
        {
            buf.append( KEY_OWNER ).append( ": " ).append( _owner ).append(  '\n' );
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
		 private boolean _hasThumbnail = false;
		 private String  _mime = "";
		 private Message _messages[];
		 private String  _name;
		 private String  _outputType;
		 private String  _progress;
		 private String  _properties[];
		 private String  _role = "";
		 private int     _size = -1;
		 private String  _status;
		 private String  _success;
		 private String  _type;
		 private String  _urn;
		 private String  _viewableID;
		 private Node    _children[];
		 
		 final private Node _parent;
		 final private int  _level;
		 
		private Node(
			JSONObject jObj,
			Node       parent,
		    int        level
	    ) {
	        _level  = level;
	        _parent = parent;

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
	    	
			value = jObj.get( KEY_SIZE );
			if( value != null &&  value instanceof String )
			{
				try
				{
					_size = Integer.parseInt( (String)value );
				}
				catch( Throwable t ) {}
			}
			else if( value  instanceof Integer )
			{
				_size = (int)value;
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
	    	
			value = jObj.get( KEY_MIME );
			if( value != null &&  value instanceof String )
			{
				_mime = (String)value;
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
	    	
			value = jObj.get( KEY_VIEWABLE_ID );
			if( value != null &&  value instanceof String )
			{
				_viewableID = (String)value;
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
	        			_children[i++] = new Node( (JSONObject)value, this, _level + 1 );
	        		}
	        	}
	        }
		}
		
		public String getFileName()
		{
			if( _urn == null || _urn == "" ) return "";
			int idx = _urn.lastIndexOf( "/" );
			if( idx < 0 ) return _urn;
			return _urn.substring( idx + 1 ); 
		}
		
		public String getFilePath()
		{
			String rootURN = ResultViewerService.this._urn;
			if( rootURN == null || rootURN.length() == 0 ) return "/";
			if( _urn == null || _urn.length() == 0 ) return "/";
			int idx = _urn.indexOf( rootURN );
			if( idx < 0 ) return "/";
			String path = _urn.substring( idx + rootURN.length() );
			idx = path.lastIndexOf( "/" );
			if( idx > 0 )
			{
				path = path.substring( 0, idx + 1 );
			}
			return path;
		}
		
		public String getGuid()
		{
			return _guid;
		}

		public String getType()
		{
			return _type;
		}

		public String getMime()
		{
			return _mime;
		}

		public String getOutputType()
		{
			return _outputType;
		}

		public String getRole()
		{
			return _role;
		}
		
		public int getSize()
		{
			return _size;
		}

		public String getStatus()
		{
			return _status;
		}
		
		public String getViewableID()
		{
			return _viewableID;
		}
		
		public boolean hasThumbnail()
		{
			return _hasThumbnail;
		}
		
		public void listDerivativeFiles(
			List<String> files
		) {
			
			String path = getFilePath();
			if( _mime == null || _mime.length() == 0 )
			{
				// Do nothing;
			}
			else if( _mime.equals( "application/autodesk-db" ) )
			{
				// Property DB files are fixed
				files.add ( path + "objects_attrs.json.gz") ;
				files.add ( path + "objects_vals.json.gz") ;
				files.add ( path + "objects_avs.json.gz") ;
				files.add ( path + "objects_offs.json.gz" );
				files.add ( path + "objects_ids.json.gz") ;
				files.add(  path + getFileName() ) ;
			} 
			else if( _mime.equals( "thumbnail" ) )
			{
				files.add(  path + getFileName() ) ;
			} 
			else if( _mime.equals( "application/autodesk-f2d" ) )
			{
				files.add(  path + "manifest.json.gz" );
			}
			else 
			{
				// All other files are assumed to be just the file listed in the bubble
				String fileName = getFileName();
				if( fileName.length() > 0 )
				{
					files.add( path + fileName ) ;
				}
			}		
			
	        if( _children != null && _children.length > 0 )
	        {
	        	for( int i = 0; i < _children.length; i++ )
	        	{
	            	_children[i].listDerivativeFiles( files );
	        	}
	        }
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
			if( _mime != null && _mime.length() > 0 )
			{
				tabs( buf ).append( KEY_MIME ).append( ": " ).append( _mime ).append( '\n' );
			}
			if( _size >= 0 )
			{
				tabs( buf ).append( KEY_SIZE ).append( ": " ).append( _size ).append( '\n' );
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
	        if( _viewableID != null && _viewableID.length() > 0)
	        {
	        	tabs( buf ).append( KEY_VIEWABLE_ID ).append( ": " ).append( _viewableID ).append(  '\n' );
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