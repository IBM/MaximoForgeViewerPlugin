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

import java.util.Date;
import java.util.Iterator;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class BucketDescription
{
	public static final String KEY_BUCKET_KEY  = "bucketKey";
	public static final String KEY_KEY         = "key";
	public static final String KEY_OWNER       = "owner";
	public static final String KEY_CREATE_DATE = "createdDate";
	public static final String KEY_PERMISSIONS = "permissions";
	public static final String KEY_POLICY_KEY  = "policyKey";

	private String     _bucketKey;
	private String     _owner;
	private Date       _createDate;
	private String     _policyKey;
	private Permission _permissions[];

	public BucketDescription(
	    JSONObject jObj 
	) {
		Object value = jObj.get( KEY_BUCKET_KEY );
		if( value != null &&  value instanceof String )
		{
			_bucketKey = (String)value;
		}
		else
		{
			value = jObj.get( KEY_KEY );
			if( value != null &&  value instanceof String )
			{
				_bucketKey = (String)value;
			}
		}
    	
		value = jObj.get( KEY_OWNER );
		if( value != null &&  value instanceof String )
		{
			_owner = (String)value;
		}
    	
        value = jObj.get( KEY_CREATE_DATE );
        if( value != null && value instanceof Long )
        {
    		_createDate = new Date( (Long)value );
        }

		value = jObj.get( KEY_POLICY_KEY );
		if( value != null &&  value instanceof String )
		{
			_policyKey = (String)value;
		}
    	
		value = jObj.get( KEY_PERMISSIONS );
		if( value != null &&  value instanceof String )
		{
			_policyKey = (String)value;
		}
    	
        value = jObj.get( KEY_PERMISSIONS );
        if (value != null  && value instanceof  JSONArray )
        {
        	JSONArray jArray = (JSONArray)value;
        	_permissions = new Permission[ jArray.size() ];

        	@SuppressWarnings( "rawtypes" )
            Iterator itr = jArray.listIterator();
        	int i = 0;
        	while( itr.hasNext() )
        	{
        		value = itr.next();
        		if( value instanceof JSONObject )
        		{
            		_permissions[i++] = new Permission( (JSONObject)value );
        		}
        	}
        }
	}

	public String getBucketKey()
	{
		return _bucketKey;
	}

	public String getOwner()
	{
		return _owner;
	}

	public Date getCreateDate()
	{
		return _createDate;
	}

	public String getPolicyKey()
	{
		return _policyKey;
	}

	public Permission[] getPermissions()
	{
		return _permissions;
	}
	
	@Override
    public String toString()
	{
        StringBuffer buf = new StringBuffer();
        if( _bucketKey != null && _bucketKey.length() > 0)
        {
            buf.append( KEY_BUCKET_KEY).append( ": " ).append( _bucketKey ).append(  '\n' );
        }
        if( _owner != null && _owner.length() > 0)
        {
            buf.append( KEY_OWNER).append( ": " ).append( _owner ).append(  '\n' );
        }
        if( _createDate != null )
        {
            buf.append( KEY_CREATE_DATE).append( ": " ).append( _createDate ).append(  '\n' );
        }
        if( _policyKey != null && _policyKey.length() > 0)
        {
            buf.append( KEY_POLICY_KEY).append( ": " ).append( _policyKey ).append(  '\n' );
        }
        if( _permissions != null && _permissions.length > 0 )
        {
        	buf.append( KEY_PERMISSIONS).append( "\n " );
        	for( int i = 0; i < _permissions.length; i++ )
        	{
            	buf.append( '\t' ).append(  _permissions[i] ).append( "\n " );
        	}
        }

		return buf.toString();
	}
}