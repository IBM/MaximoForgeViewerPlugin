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

import com.ibm.json.java.JSONObject;

public class Permission
{
	public static final String	KEY_SERVICE_ID	= "serviceId";
	public static final String	KEY_AUTH_ID     = "authId";
	public static final String	KEY_ACCESS	    = "access";

	private String	           _serviceId;
	private String	           _access;

	Permission(
	    String serviceId,
	    String access )
	{
		serviceId = _serviceId;
		access = _access;
	}

	Permission(
	    JSONObject jObj 
    ) {
		if( jObj != null )
		{
			Object value = jObj.get( KEY_SERVICE_ID );		// V1 API
			if( value != null && value instanceof String )
			{
				_serviceId = (String)value;
			}

			value = jObj.get( KEY_AUTH_ID );				// V2 API
			if( value != null && value instanceof String )
			{
				_serviceId = (String)value;
			}

			value = jObj.get( KEY_ACCESS );
			if( value != null )
			{
				if( value != null && value instanceof String )
				{
					_access = (String)value;
				}
			}
		}
	}

	public String getServiceId()
	{
		return _serviceId;
	}

	public String getAccess()
	{
		return _access;
	}
	
	@Override
    public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append( KEY_SERVICE_ID ).append( " = " ).append( _serviceId );
		buf.append( ", " );
		buf.append( KEY_ACCESS ).append( " = " ).append( _access );
		
		return buf.toString();
	}
}
