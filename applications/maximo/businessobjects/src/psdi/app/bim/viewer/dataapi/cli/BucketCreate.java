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

public class BucketCreate
{

	private DataRESTAPI _service;
	public BucketCreate()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result createBucket(
		String bucketKey,
		String policy,
		String region
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.bucketCreate( bucketKey, policy, region );
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
	
	public static void printUsage()
	{
		System.out.println( "Usage: BucketCreate bucketKey " +
				DataRESTAPI.BUCKET_POLICY_PERSISTENT + "|" +
				DataRESTAPI.BUCKET_POLICY_TEMPORARY + "|" +
				DataRESTAPI.BUCKET_POLICY_TRANSIENT + 
                "[appendkey] " +
				"[region:US|EMEA] ");
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
		BucketCreate create = new BucketCreate();
		String region = "";

		if( args.length > 4 || args.length < 2 )
		{
			BucketCreate.printUsage();
			return;
		}
		String bucket = args[0];
		String policy = args[1];
		
		if(    !policy.equalsIgnoreCase( DataRESTAPI.BUCKET_POLICY_PERSISTENT )
			&& !policy.equalsIgnoreCase( DataRESTAPI.BUCKET_POLICY_TEMPORARY )
			&& !policy.equalsIgnoreCase( DataRESTAPI.BUCKET_POLICY_TRANSIENT ))
		{
			BucketCreate.printUsage();
			return;
		}
		
		if( args.length > 2 )
		{
			if( args[2].equalsIgnoreCase(  "appendkey" ))
			{
				bucket = bucket + create.getService().lookupKey().toLowerCase();
			}
			else
			{
				region = create.parseRegionArg( args[2] );
				
				if( region.length() == 0 )
				{
					BucketCreate.printUsage();
					return;
				}
			}
		}
		
		if( args.length > 3 )
		{
			region = create.parseRegionArg( args[3] );
			
			if( region.length() == 0 )
			{
				BucketCreate.printUsage();
				return;
			}
		}

		Result result = create.createBucket( bucket, policy, region );
		
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
			System.out.println( result.toString() );
		}
	}
}
