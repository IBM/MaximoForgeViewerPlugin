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

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import psdi.app.bim.viewer.dataapi.ResultObjectDetail;
import psdi.app.bim.viewer.lmv.LMVServiceRemote;
import psdi.app.bim.viewer.lmv.Model;
import psdi.app.bim.viewer.lmv.ModelRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.WebClientEvent;

/**
 * @author Doug Wood
 * This bean is used to select and upload model files from the browser to the Maximo server
 * 
 */
public class ModelUploadBean extends BaseDlgBean
{
	ControlInstance _ctrl = null;

	public synchronized int uploadModel() 
		throws MXException, RemoteException
	{
		ModelRemote model = (ModelRemote)getMbo();
		model.validate();

		// Test if the model already exist and if so, prompt to overwrite
		String bucketKeyFull = model.getString( Model.FIELD_BUCKETKEYFULL );
		String objectKey     = model.getString( Model.FIELD_OBJECTKEY );
		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		try
        {
			ResultObjectDetail result = lmv.objectQueryDetails( bucketKeyFull, objectKey );
			if( !result.isError() )
			{
				WebClientEvent event = new WebClientEvent( ModelManageBean.DLG_MODEL_APPROVE_OVERWRITE, 
				                                           ModelManageBean.DLG_MODEL_APPROVE_OVERWRITE, 
				                                           model, clientSession);
				clientSession.queueEvent( event );
				return EVENT_HANDLED;
			}
        }
        catch( IOException e )
        { /* Ignore - This will be caught and reported when the file is uploaded */  }
        catch( URISyntaxException e )
        { /* Ignore - This will be caught and reported when the file is uploaded */  }
		clientSession.queueEvent( new WebClientEvent( "doModelUpload", this.getId(), null, clientSession) );
		return EVENT_HANDLED;
	}

	public void doModelUpload() 
			throws MXException, RemoteException
		{
			ModelRemote model = (ModelRemote)getMbo();
			long uid = getMbo().getUniqueIDValue();
			model.uploadFile();
			clientSession.queueEvent( new WebClientEvent( "dialogok", this.getId(), null, clientSession) );
			clientSession.queueEvent( new WebClientEvent( "focusOnMbo", ModelManageBean.DLG_MODEL, uid, clientSession) );
		}
}