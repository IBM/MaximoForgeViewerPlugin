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
import psdi.app.bim.viewer.dataapi.FileReference;
import psdi.app.bim.viewer.dataapi.Result;

public class ModelLink
{

	private DataRESTAPI _service;
	public ModelLink()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result linkModel(
		FileReference master,
		FileReference children[]
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.linkFileSet( master, children );
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
		if( args.length < 4 || args.length % 2 != 0 )
		{
			System.out.println( "Usage: ModelLing  parentkey parenturn [childkey childurn]+" );
			return;
		}
		ModelLink link = new ModelLink();
		
		FileReference parentRef = new FileReference( args[1], args[0] );
		
		FileReference childRefs[] = new FileReference[ (args.length - 1)/2 ];
		
		int j = 2;
		for( int i = 0; i < childRefs.length; i++ )
		{
			childRefs[i] = new FileReference( args[j+1], args[j] );
			j+=2;
		}
		
		
		Result result = link.linkModel( parentRef, childRefs );
		System.out.println( result.toString() );
	}
}
