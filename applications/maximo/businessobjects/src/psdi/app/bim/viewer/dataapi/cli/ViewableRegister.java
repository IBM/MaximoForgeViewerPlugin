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


public class ViewableRegister
{

	private DataRESTAPI _service;
	public ViewableRegister()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result viewableRegister(
		String  viewableURN,
		String  region,
		boolean compressed,
		String  rootFileName
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.viewableRegister( viewableURN, region, compressed, rootFileName, false, true );
	}


	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(
	    String[] arg
    )
		throws IOException, 
		   URISyntaxException 
	{
		String  urn;
		boolean compressed   = false;
		String  rootFileName = null;
		String  region       = "us";
		if( arg.length < 1 )
		{
			System.out.println( "Usage: ViewableRegister urn [conpressed rootFileName] [ region us/emea");
			return;
		}
		urn = arg[0];

		for( int i = 1; i < arg.length; i++ )
		{
			if( arg[i].equalsIgnoreCase( "region" ))
			{
				if( arg.length > i + 1 )
				{
					region = arg[i+1];
				}
				else
				{
					System.out.println( "Usage: ViewableRegister urn [conpressed rootFileName] [ region us/emea");
					return;
				}
			}
			if( arg[i].equalsIgnoreCase( "compressed" ))
			{
				if( arg.length > i + 1 )
				{
					compressed = true;
					rootFileName = arg[i+1];
				}
				else
				{
					System.out.println( "Usage: ViewableRegister urn [conpressed rootFileName] [ region us/emea");
					return;
				}
			}
		}
		
		ViewableRegister query = new ViewableRegister();
		Result result = query.viewableRegister( urn, region, compressed, rootFileName );
		System.out.println( result.toString() );
	}
}
