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

import psdi.app.bim.viewer.lmv.ModelRemote;
import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.WebClientEvent;



/**
 * @author Doug Wood	
 * This bean is used to manage storage buckets for the Autodesk Large Model Viewer.
 */
public class ModelManageBean extends BaseManageBean
{
	public static final String DLG_MODEL                   = "LMV_MODEL";
	public static final String DLG_MODEL_ATTACH            = "LMV_MODEL_ATTACH";
	public static final String DLG_MODEL_APPROVE_DELETE    = "LMV_MODEL_APPROVE_DELETE";
	public static final String DLG_MODEL_APPROVE_OVERWRITE = "LMV_MODEL_APPROVE_OVERWRITE";
	public static final String DLG_MODEL_UPLOAD            = "LMV_MODEL_UPLOAD";
	public static final String CTRL_MODEL_TABLE            = "model_manage_tbl";
	public static final String CTRL_UPLOAD_TABLE           = "model_manage_upload_tbl";
	public static final String CTRL_LINK_TBL               = "model_manage_link_tbl";
	public static final String DLG_MODEL_LINK              = "LMV_MODEL_LINK";
	
	ControlInstance _ctrl_link_tbl = null;
	
	@Override
    public void initialize() throws MXException, RemoteException
	{
		super.initialize();
		if( _ctrl_link_tbl == null )
		{
			_ctrl_link_tbl = clientSession.findControl( CTRL_LINK_TBL );
		}
	}
	
	public int attachModel() 
		throws MXException, 
		       RemoteException
	{
		return displayDialog( DLG_MODEL_ATTACH );
	}
	
	public int detatchModel() 
		throws MXException, 
		       RemoteException
	{
		instantdelete();
		return EVENT_HANDLED;
	}
	
	public int deleteModelDlg() 
		throws MXException, 
		       RemoteException
	{
		clientSession.loadDialog( DLG_MODEL_APPROVE_DELETE );
		return EVENT_HANDLED;
	}
		
	public int link() 
		throws MXException, 
		       RemoteException
	{
		displayDialog( DLG_MODEL_LINK );
		WebClientEvent event = new WebClientEvent( DLG_MODEL_LINK, DLG_MODEL_LINK, _ctrl_link_tbl, clientSession);
		clientSession.queueEvent( event );
		return EVENT_HANDLED;
	}

	public int uploadModel() 
		throws MXException, 
		       RemoteException
	{
		return displayDialog( DLG_MODEL_UPLOAD );
	}
	
	public int deleteModel() 
		throws MXException, 
		       RemoteException
	{
		MboRemote mbo = getMbo();
		if( mbo != null && mbo instanceof ModelRemote )
		{
//			int row = getCurrentRow();
			ModelRemote model = (ModelRemote)mbo;
			model.deleteModel( false );
//			save();
//			reset();
//			if( row >= count() ) row--;
//			highlightrow( row );
		}
		instantdelete();
		return EVENT_HANDLED;
	}

	/**
	 * Event sent from linked model lookup dialog to cause the table of links to refresh
	 * @return
	 * @throws MXException
	 * @throws RemoteException
	 */
	public int refreshLink() 
		throws MXException, 
		       RemoteException
	{
		if( _ctrl_link_tbl != null )
		{
			DataBean db = _ctrl_link_tbl.getDataBean();
			db.refreshTable();
			db.fireStructureChangedEvent();
		}
		return EVENT_HANDLED;
	}
}