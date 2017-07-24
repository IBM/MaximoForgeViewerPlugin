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

import psdi.app.bim.viewer.dataapi.Constants;
import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.app.bim.viewer.dataapi.Result;

public class BucketList
{

	private DataRESTAPI _service;
	public BucketList()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result listBucket(
		String region
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.bucketList( region );
	}

	public String parseRegionArg(
		String arg
	) {
		arg = arg.toUpperCase();
		if( arg.startsWith( "REGION:" ) )
		{
			arg = arg.substring( 7 );
			if( arg.equals( Constants.REGION_US ) || arg.equals( Constants.REGION_EMEA ) )
			{
				return arg;
			}
		}
		return "";
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
		BucketList list = new BucketList();

		if( args.length > 1  )
		{
			System.out.println( "Usage: BucketList [region:US|EMEA]" );
			return;
		}
		
		String region = "";
		if( args.length == 1  )
		{
			region = list.parseRegionArg( args[0] );
			if( region.length() == 0 )
			{
				System.out.println( "Usage: BucketList [region:US|EMEA]" );
				return;
			}
		}
		
		Result result = list.listBucket( region );
		
		System.out.println( result.toString() );
	}
}
