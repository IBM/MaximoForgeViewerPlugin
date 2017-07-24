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
import java.util.Iterator;
import java.util.Vector;

import psdi.app.bim.viewer.lmv.Model;
import psdi.app.bim.viewer.lmv.ModelLink;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.bim.Constants;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.WebClientEvent;



/**
 * @author Doug Wood	
 * This bean is used to manage storage buckets for the Autodesk Large Model Viewer.
 */
public class ModelLinkBean extends DataBean
{
	ControlInstance _ctrl_link_tbl = null;
	
	public ModelLinkBean ()
	{
		super();
	}
	
	@Override
    public void initialize() throws MXException, RemoteException
	{
		super.initialize();
		WebClientEvent event = clientSession.getCurrentEvent();
		Object o = event.getValue();
		if( o != null && o instanceof ControlInstance )
		{
			_ctrl_link_tbl = (ControlInstance)o;
		}
	}

	@Override
	public synchronized int execute() 
		throws MXException, 
		       RemoteException
	{
		MboSetRemote lookupSet = getMboSet();
		MboRemote    modelMbo  = lookupSet.getOwner();
		if( modelMbo == null )
		{
			throw new MXApplicationException( Constants.BUNDLE_MSG, Constants.MSG_INTERNAL_ERR );	
		}
		MboSetRemote linkSet = modelMbo.getMboSet( Model.RELATIONSHOP_MODELLINK );
		@SuppressWarnings( "unchecked" )
        Vector<MboRemote> selection = lookupSet.getSelection();
		Iterator<MboRemote> itr = selection.iterator();
		while( itr.hasNext() )
		{
			MboRemote lookupMbo = itr.next();
			MboRemote linkMbo = linkSet.add();
			linkMbo.setValue( ModelLink.FIELD_CHILDMODELID, lookupMbo.getString( Model.FIELD_BIMLMVMODELID ) );
			linkMbo.setValue( ModelLink.FIELD_CHILDKEY,     lookupMbo.getString( Model.FIELD_OBJECTKEY ) );
			linkMbo.setValue( ModelLink.FIELD_CHILDURN,     lookupMbo.getString( Model.FIELD_MODELURN ) );
		}
		linkSet.save();
		
		WebClientEvent event = new WebClientEvent( "refreshLink", ModelManageBean.DLG_MODEL, "refresh", clientSession);
		clientSession.queueEvent( event );

		return EVENT_HANDLED;
	}
}