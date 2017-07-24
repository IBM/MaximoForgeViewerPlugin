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

public class ModelQuery
{

	private DataRESTAPI _service;
	public ModelQuery()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result queryModel(
		String bucketKey,
		String objectKey
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.objectQueryDetails( bucketKey, objectKey );
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
		if( args.length < 2 || args.length > 3 )
		{
			System.out.println( "Usage: modelQuery bucketKey objectkey  [appendkey]" );
			return;
		}
		ModelQuery query = new ModelQuery();
		
		String bucketKey = args[0];
		if( args.length > 2 )
		{
			if( args[2].equalsIgnoreCase( "appendkey" ))
			{
				bucketKey = bucketKey + query.getService().lookupKey().toLowerCase();
			}
			else
			{
				System.out.println( "Usage: querymodel bucketKey objectkey  [appendkey]" );
				return;
			}
		}
		
		Result result = query.queryModel( bucketKey, args[1] );
		System.out.println( result.toString() );
	}
}
