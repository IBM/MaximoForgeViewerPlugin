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
package psdi.app.bim.viewer.dataapi.cli;

import java.io.IOException;
import java.net.URISyntaxException;

import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.app.bim.viewer.dataapi.Result;

public class Grant
{

	private DataRESTAPI _service;
	public Grant()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result grant(
		String bucketKey,
		String serviceId,
		String access
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.bucketGrantRightsV2( bucketKey, serviceId, access );
	}


	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(
	    String[] args
    )
		throws IOException, 
		   URISyntaxException 
	{
		if( args.length < 3 || args.length > 4 )
		{
			System.out.println( "Usage: grant bucketKey serviceKey access [appendkey]" );
			return;
		}
		
		Grant grant = new Grant();
		
		String bucketKey = args[0];
		if( args.length > 3 )
		{
			if( args[3].equalsIgnoreCase( "appendkey" ))
			{
				bucketKey = bucketKey + grant.getService().lookupKey().toLowerCase();
			}
			else
			{
				System.out.println( "Usage: grant bucketKey serviceKey access [appendkey]" );
				return;
			}
		}
		
		Result result = grant.grant( bucketKey, args[1], args[2] );
		if( result.isError() )
		{
			System.err.println( result.getHttpStatus() );
			System.err.println( result.getErrorCode() );
			System.err.println( result.getErrorMessage() );
			return;
		}
		else
		{
			System.out.println( result.toString() );
		}
	}
}
