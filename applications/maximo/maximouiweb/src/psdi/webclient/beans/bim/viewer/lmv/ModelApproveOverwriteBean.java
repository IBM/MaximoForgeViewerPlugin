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
package psdi.webclient.beans.bim.viewer.lmv;

import java.rmi.RemoteException;

import psdi.app.bim.viewer.lmv.Model;
import psdi.app.bim.viewer.lmv.ModelRemote;
import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.bim.Constants;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.session.WebClientSession;

/**
 * @author Doug Wood	
 * This bean is used to display the save/cancel dialog for the project when the upload dialog is loaded.
 */
public class ModelApproveOverwriteBean extends DataBean
{
	private final static String CTRL_LABEL = "model_approve_overwrite_test_1";

	private boolean _initialized = false;

	@Override
    public void initialize() throws MXException, RemoteException
	{
		super.initialize();
		if( _initialized )
		{
			return;
		}

		WebClientEvent event = clientSession.getCurrentEvent();
		Object o = event.getValue();
		if( o instanceof ModelRemote )
		{
			MboRemote mbo = (ModelRemote)o;
			String bucketName = mbo.getString( Model.FIELD_BUCKETKEY );
			String objectName = mbo.getString( Model.FIELD_OBJECTKEY );
			String params[] = { "", objectName, bucketName };
			String msg = mbo.getMessage( LMVConstants.BUNDLE_MSG, LMVConstants.MSG_CONFIRM_MODEL_OVERWRITE, params );
			
			WebClientSession wcs = this.app.getWebClientSession();
			ControlInstance ctrlLable = wcs.findControl( CTRL_LABEL );
			ctrlLable.setProperty( "label", msg );
		}
		else
		{
			throw new MXApplicationException( Constants.BUNDLE_MSG, Constants.MSG_INTERNAL_ERR );	
		}

		_initialized = true;
	}

	@Override
    public synchronized int execute() 
		throws MXException, RemoteException
	{	
		clientSession.queueEvent( new WebClientEvent( "doModelUpload", ModelManageBean.DLG_MODEL_UPLOAD, null, clientSession) );
		return EVENT_HANDLED;
	}
}