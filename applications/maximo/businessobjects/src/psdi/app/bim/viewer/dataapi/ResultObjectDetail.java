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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;


import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;


/*
 * V2
 * {  
 * 		"bucketKey" : "junk_traneawtv1rfx17gumg0j9erqzhd8ll7qcn6",  
 * 		"objectId" : "urn:adsk.objects:os.object:junk_traneawtv1rfx17gumg0j9erqzhd8ll7qcn6/office_a.rvt",  
 * 		"objectKey" : "office_a.rvt",  
 * 		"sha1" : "65a80dd6d3892e5aa8c1e9edbab37cd909e58480",  
 * 		"size" : 7696384,  
 * 		"contentType" : "application/stream",  
 * 		"location" : "https://developer.api.autodesk.com/oss/v2/buckets/junk_traneawtv1rfx17gumg0j9erqzhd8ll7qcn6/objects/office_a.rvt",  
 * 		"blockSizes" : [ 2048 ],  
 * 		"deltas" : [ ]
 * }
 * V1
 * {  
 * 		"bucket-key" : "eawtv1rfx17gumg0j9erqzhd8ll7qcn6_test_test",  
 * 		"objects" : 
 * 			[ 
 * 				{    
 * 					"location" : "https://developer.api.autodesk.com/oss/v1/buckets/eawtv1rfx17gumg0j9erqzhd8ll7qcn6_test_test/objects/clinic",    
 * 					"size" : 1592751,    
 * 					"key" : "clinic",    
 * 					"id" : "urn:adsk.objects:os.object:eawtv1rfx17gumg0j9erqzhd8ll7qcn6_test_test/clinic",    
 * 					"sha-1" : "26643645c88bcea142603f24126a756367e11721",    
 * 					"content-type" : "*"  
 * 				} 
 * 			]
 * 	}
 * 
 */
public class ResultObjectDetail
    extends Result
{
	public static final String	KEY_BUCKET_KEY = "bucket-key";  
	public static final String	KEY_BUCKETKEY  = "bucketKey";  
	public static final String	KEY_OBJECTS    = "objects"; 
	
	private static final String KEY_FAULT       = "fault";
	private static final String KEY_FAULTSTRING = "faultstring";
	private static final String KEY_DETAIL      = "detail";
	private static final String KEY_CODE        = "code";

	private String       _bucketKey;  
	private ViewerObject _objects[];
	
	public ResultObjectDetail(
	    Result result 
    ) {
		super( result );
	}

	public ResultObjectDetail(
	    HttpURLConnection connection 
    )
	    throws IOException
	{
		super( connection );
	}
	
	public ResultObjectDetail(
	    Exception e 
    ) {
		super( e );
		if( e instanceof FileNotFoundException )
		{
			
		}
	}

    public String getBucketKey()
	{
		return _bucketKey;
	}

	public ViewerObject[] getObjects()
	{
		return _objects;
	}

    @Override
    protected JSONArtifact parseError(
    	String data
	) 
		throws IOException 
	{
    	JSONArtifact jArtifact = super.parseError( data ); 
    	
    	// 504: {"fault":{"faultstring":"Gateway timeout","detail":{"code":"GATEWAY_TIMEOUT"}}}
    	if( jArtifact != null && jArtifact instanceof JSONObject )
    	{
        	JSONObject jObj;
    		jObj = (JSONObject)jArtifact;

            Object value = jObj.get( KEY_FAULT );
    		if( value instanceof JSONObject )
    		{
    			jObj = (JSONObject)value;
    			value = jObj.get( KEY_FAULTSTRING );
    			if( value != null &&  value instanceof String )
    			{
    				setErrorMessage( (String)value );
    			}
                value = jObj.get( KEY_DETAIL );
        		if( value instanceof JSONObject )
        		{
        			jObj = (JSONObject)value;
        			value = jObj.get( KEY_CODE );
        			if( value != null &&  value instanceof String )
        			{
        				setErrorCode( (String)value );
        			}
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
    	JSONArtifact jArtifact = super.parseReturn( data );
        if( jArtifact == null ) return null;
    	JSONObject jObj;
    	if( !(jArtifact instanceof JSONObject) )
    	{
    		return null;
    	}
		jObj = (JSONObject)jArtifact;
		
		// API Version 1;
		Object value = jObj.get( KEY_BUCKET_KEY );
		if( value != null &&  value instanceof String )
		{
			_bucketKey = (String)value;
	        value = jObj.get( KEY_OBJECTS );
	        if (value != null  && value instanceof  JSONArray )
	        {
	        	JSONArray jArray = (JSONArray)value;
	        	_objects = new ViewerObject[ jArray.size() ];

	        	@SuppressWarnings( "rawtypes" )
	            Iterator itr = jArray.listIterator();
	        	int i = 0;
	        	while( itr.hasNext() )
	        	{
	        		value = itr.next();
	        		if( value instanceof JSONObject )
	        		{
	        			_objects[i++] = new ViewerObject( (JSONObject)value );
	        		}
	        	}
	        }
		}

		// API Version 2
		value = jObj.get( KEY_BUCKETKEY );	
		if( value != null &&  value instanceof String )
		{
			_bucketKey = (String)value;
			ViewerObject object = new ViewerObject( jObj );
        	_objects = new ViewerObject[ 1 ];
        	_objects[0] = object;
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
        if( _bucketKey != null && _bucketKey.length() > 0)
        {
            buf.append( KEY_BUCKET_KEY).append( ": " ).append( _bucketKey ).append(  '\n' );
        }
        if( _objects != null && _objects.length > 0 )
        {
        	buf.append( KEY_OBJECTS ).append( "\n " );
        	for( int i = 0; i < _objects.length; i++ )
        	{
            	buf.append( '\t' ).append(  _objects[i] ).append( "\n " );
        	}
        }
        
        if( buf.length() == 0 )
        {
        	buf.append( getRawData() );
        }

		return buf.toString();
	}
}