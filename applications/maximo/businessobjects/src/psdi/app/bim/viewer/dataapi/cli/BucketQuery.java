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

public class BucketQuery
{

	private DataRESTAPI _service;
	public BucketQuery()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result queryBucket(
		String bucketKey
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.bucketQueryDetails( bucketKey );
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
		BucketQuery query = new BucketQuery();

		if( args.length > 2 || args.length < 1 )
		{
			System.out.println( "Usage: BucketQuery bucketKey [appendkey]" );
			return;
		}
		String bucket = args[0];
		if( args.length > 1 )
		{
			if( args[1].equalsIgnoreCase( "appendkey" ))
			{
				bucket = bucket + query.getService().lookupKey().toLowerCase();
			}
			else
			{
				System.out.println( "Usage: BucketQuery bucketKey [appendkey]" );
				return;
			}
		}
		
		Result result = query.queryBucket( bucket );
		
		if( result.isError() )
		{
			if( result.getHttpStatus() == 404 )
			{
				System.out.println( "Bucket: " + bucket + " Not found" );
			}
			else
			{
				System.out.println( result.getRawError() );
			}
		}
		else
		{
			System.out.println( result.getRawData() );
		}
		System.out.println( result.toString() );
	}
}
