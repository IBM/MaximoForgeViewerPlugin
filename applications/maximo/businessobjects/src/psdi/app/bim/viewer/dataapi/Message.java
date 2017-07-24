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


public class Message
{
	public static final String	KEY_CODE         = "code";    
	public static final String	KEY_TYPE         = "type";
	public static final String	KEY_MESSAGE      = "message";
	public static final String	KEY_ID           = "id";
	public static final String	KEY_SHA_1        = "sha-1";
	public static final String	KEY_CONTENT_TYPE = "content-type";
	
	private String	_code;    
	private String	_type;    
	private String  _messages[];


	Message(
    	String	     code,    
    	String	     type    
    ) {
		_code = code;
		_type = type;
	}

	/**
	 * 
	 * @author Doug
	 * {
	 * 	"code":"Revit-MissingLink",
	 * 	"type":"warning",
	 * 	"message":
	 * 		[
	 * 			"<message>Missing link files: <ul>{0}<\/ul><\/message>",
	 * 			"NE Corner Building 6.dwg"
	 * 		]
	 * }
	 */
	Message(
	    JSONObject jObj 
    ) {
		if( jObj != null )
		{
			Object value = jObj.get( KEY_CODE );
			if( value != null && value instanceof String )
			{
				_code = (String)value;
			}

			value = jObj.get( KEY_TYPE );
			if( value != null && value instanceof String )
			{
				_type = (String)value;
			}

			value = jObj.get( KEY_MESSAGE );
	        if (value != null  && value instanceof  JSONArray )
	        {
	        	JSONArray jArray = (JSONArray)value;
	        	_messages = new String[ jArray.size() ];

	        	@SuppressWarnings( "rawtypes" )
	            Iterator itr = jArray.listIterator();
	        	int i = 0;
	        	while( itr.hasNext() )
	        	{
	        		value = itr.next();
	        		if( value instanceof String )
	        		{
	        			_messages[i++] = (String)value;
	        		}
	        	}
	        }
		}
	}

	public String getCode()
	{
		return _code;
	}

	public String getType()
	{
		return _type;
	}

	@Override
    public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append( KEY_CODE ).append( " = " ).append( _code );
		buf.append( ", " );
		buf.append( KEY_TYPE ).append( " = " ).append( _type );
		
		if( _messages != null && _messages.length > 0 )
		{
			String baseMsg = _messages[0];
			for( int i = 1; i < _messages.length; i++ )
			{
				String patt = "{" + (i - 1) + "}";
				baseMsg = baseMsg.replace( patt, _messages[i] );
			}
			buf.append( "\t" ).append( KEY_MESSAGE ).append( ": " );
			buf.append( "\t" ).append( baseMsg ).append( '\n' );
		}

		return buf.toString();
	}
}
