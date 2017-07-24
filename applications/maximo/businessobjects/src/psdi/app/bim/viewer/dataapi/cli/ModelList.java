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

public class ModelList
{

	private DataRESTAPI _service;
	public ModelList()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result listModel(
		String bucketKey,
		String keyBeginsWith
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.objectList( bucketKey, keyBeginsWith );
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
		boolean appendKey = false;
		String  keyPrefix = "";
		if( args.length < 1 || args.length > 3 )
		{
			System.out.println( "Usage: querymodel bucketKey objectkey  [appendkey] [keyPrefix]" );
			return;
		}
		ModelList list = new ModelList();
		
		String bucketKey = args[0];
		if( args.length > 1 )
		{
			if( args[1].equalsIgnoreCase( "appendkey" ))
			{
				bucketKey = bucketKey + list.getService().lookupKey().toLowerCase();
				appendKey = true;
			}
			else
			{
				keyPrefix = args[1];
			}
		}
		if( args.length == 3 )
		{
		    if( appendKey )
			{
				keyPrefix = args[2];
			}
			else
			{
				System.out.println( "Usage: ListModel bucketKey [appendkey]" );
				return;
			}
		}
		
		Result result = list.listModel( bucketKey, keyPrefix );
		System.out.println( result.toString() );
	}
}
