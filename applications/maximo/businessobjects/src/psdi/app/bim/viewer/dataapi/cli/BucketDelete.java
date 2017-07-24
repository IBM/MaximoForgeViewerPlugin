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

public class BucketDelete
{

	private DataRESTAPI _service;
	public BucketDelete()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result delete(
		String bucketKey
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.bucketDelete( bucketKey );
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
		if( args.length < 1 || args.length > 2 )
		{
			System.out.println( "Usage: BucketDelete bucketKey [appendkey]" );
			return;
		}
		
		BucketDelete delete = new BucketDelete();
		
		String bucketKey = args[0];
		if( args.length > 1 )
		{
			if( args[1].equalsIgnoreCase( "appendkey" ))
			{
				bucketKey = bucketKey + delete.getService().lookupKey().toLowerCase();
			}
			else
			{
				System.out.println( "Usage: BucketDelete bucketKey [appendkey]" );
				return;
			}
		}
		
		Result result = delete.delete( bucketKey );
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
