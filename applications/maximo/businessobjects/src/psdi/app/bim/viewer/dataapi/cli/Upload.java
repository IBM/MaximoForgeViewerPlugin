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
import java.security.GeneralSecurityException;

import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.UploadProgress;

public class Upload implements UploadProgress
{

	private DataRESTAPI _service;
	public Upload()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result upload(
		String bucketKey,
		String fileName
	) 	
		throws IOException, 
			   URISyntaxException, 
			   GeneralSecurityException 
	{
		fileName = fileName.replace( "\\", "/" );
		int idx = fileName.lastIndexOf( '/' );
		String objectKey = fileName.substring( idx + 1 );
		return _service.objectUploadChunked( bucketKey, objectKey, fileName, this );
	}
	
	public void progress( 
		Result result,
		long   processed, 
		long   total 
	) {
		if( result != null )
		{
			if( result.isError() )
			{
				System.err.println( result );
			}
//			else
//			{
//				System.out.println( result.toString() );
//			}
		}
		System.out.println( "" + processed + " of " + total );
		System.out.print( '.' );
	}



	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public static void main(
	    String[] args
    )
		throws IOException, 
		       URISyntaxException, 
		       GeneralSecurityException 
	{
		if( args.length < 2 | args.length > 3 )
		{
			System.out.println( "Usage: Upload bucketkey filename [appendkey]" );
			return;
		}
		
		Upload upload = new Upload();

		String bucketKey = args[0];
		if( args.length > 2 )
		{
			if( args[2].equalsIgnoreCase( "appendkey" ))
			{
				bucketKey = bucketKey + upload.getService().lookupKey().toLowerCase();
			}
			else
			{
				System.out.println( "Usage: Upload bucketkey filename [appendkey]" );
				return;
			}
		}
		
		Result result = upload.upload( bucketKey, args[1] );
		System.out.println( result );
	}

}
