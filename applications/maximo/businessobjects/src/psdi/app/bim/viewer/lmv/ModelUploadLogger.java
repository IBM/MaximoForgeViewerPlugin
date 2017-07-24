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

import psdi.app.bim.loader.ProgressLoggerBase;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXException;

public class   ModelUploadLogger
       extends ProgressLoggerBase<String>
{
    private final String   _sessionId;
	private final MXServer _server;
	private UserInfo       _userInfo = null;
	private MboSetRemote   _logSet = null;

    public ModelUploadLogger(
	    MboRemote mbo,
	    String    sessionId,
	    String    messageBundleName
	) 
		throws RemoteException 
	{
		super( messageBundleName );
		_sessionId = sessionId;
		_server    = MXServer.getMXServer();
		try
        {
	        _userInfo  = (UserInfo)mbo.getUserInfo().clone();
	        _userInfo.setInteractive( false );
        }
        catch( CloneNotSupportedException e )
        { /* Ignore - Will never happen */ }
	}
	
	@Override
    public void itemOfIntersetLoaded(
		String item 
    ) {
	}

	@Override
	protected MboSetRemote getLogSet()
	    throws RemoteException,
	           MXException
	{
		if( _logSet != null ) return _logSet;
		
		_logSet = _server.getMboSet( ModelUpload.TABLE_NAME, _userInfo );
		_logSet.setWhere( ModelUpload.FIELD_BIMLMVMODELUPLOADID + " = " + _sessionId );
		_logSet.reset();
		return _logSet;
	}
	
	@Override
	protected String getStartMsg()
	{
		try
        {
			return _logSet.getMessage( Messages.BUNDLE_MSG, Messages.MSG_UPLOADING_MODEL );
        }
        catch( RemoteException e )
        {
        	e.printStackTrace();
			return "Uploading model file";
        }
	}
	
	public UserInfo getUserInfo()
	{
		return _userInfo;
	}
	
	@Override
    public void setItemCount(
		long count
	) {
		String msg;
		try
        {
            String[] params = { "" + count };
			msg = _logSet.getMessage( Messages.BUNDLE_MSG, Messages.MSG_BYTES_TO_UPLOAD, params );
        }
        catch( RemoteException e )
        {
        	msg = "" + count + "K";
        	e.printStackTrace();
        }
		_itemCount = count;
		writeLog( msg );
	}

}