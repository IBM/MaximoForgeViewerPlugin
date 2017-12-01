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
package psdi.app.bim.viewer.lmv;

import java.rmi.RemoteException;

import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.server.MXServer;

public class LMVServiceImpl
    extends DataRESTAPI
{

	private final MXServer _server;
	
	public LMVServiceImpl() 
		throws RemoteException
	{
		super();
		_server = MXServer.getMXServer();
	}

	@Override
	public String lookupHostname()
	{
		return _server.getProperty( LMVService.LMV_HOST );
	}

	@Override
	public String lookupKey()
	{
		return _server.getProperty( LMVService.LMV_KEY );
	}

	@Override
	public String lookkupSecret()
	{
		return _server.getProperty( LMVService.LMV_SECRET );
	}

	@Override
	public boolean  requestRights( 
		String scope 
	) {
		return true;
	}
}
