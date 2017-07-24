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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import psdi.app.bim.viewer.dataapi.DataRESTAPI;

public class APIImpl
    extends DataRESTAPI
{

	public final static String PROP_KEY          = "bim.viewer.LMV.key";
	public final static String PROP_SECRET       = "bim.viewer.LMV.secret";
	public final static String PROP_HOST         = "bim.viewer.LMV.host";
	public final static String PROP_API_VER      = "bim.viewer.LMV.api.version";
	public final static String PROP_API_VER_AUTH = "bim.viewer.LMV.api.version.auth";
	public final static String PROP_VIEWER_VER   = "bim.viewer.LMV.viewer.version";

	private String _host   = "developer.api.autodesk.com";
	private String _key    = "";
	private String _secret = "";


public APIImpl()
	{
		super();
		try
		{
			String current;
			current = new java.io.File( "." ).getCanonicalPath();
			System.out.println( "Current dir:" + current );
			String currentDir = System.getProperty( "user.dir" );
			System.out.println( "Current dir using System:" + currentDir );
		}
		catch( IOException e1 )
		{
			e1.printStackTrace();
		}

		FileInputStream fis = null;
		try
        {
	        fis = new FileInputStream( "lmv.properties" );
        }
        catch( FileNotFoundException e )
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		if( fis != null )
		{
			Properties properties = new Properties();
			try
            {
	            properties.load(  fis  );
            }
            catch( IOException e )
            {
	            e.printStackTrace();
	            return;
            }
			String prop = properties.getProperty( PROP_HOST );
			if( prop != null )
			{
				_host = prop.trim();
			}
			prop = properties.getProperty( PROP_KEY );
			if( prop != null )
			{
				_key = prop.trim();
			}
			prop = properties.getProperty( PROP_SECRET );
			if( prop != null )
			{
				_secret = prop.trim();
			}
		}
	}

	@Override
	public String lookupVersion(
		int api
	) {
		switch( api )
		{
		case DataRESTAPI.API_AUTH:
			return "v1";
		case DataRESTAPI.API_OSS:
			return "v1";
		}
		return "v1";
	}

	@Override
	public String lookupHostname()
	{
		return _host;
	}

	@Override
	public String lookupKey()
	{
		return _key;
	}

	@Override
	public String lookkupSecret()
	{
		return _secret;
	}

}
