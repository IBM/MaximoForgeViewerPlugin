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

import psdi.app.bim.viewer.lmv.Viewable;
import psdi.app.bim.viewer.lmv.ViewablelRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.controller.WebClientEvent;



/**
 * This bean is used to manage storage buckets for the Autodesk Large Model Viewer.
 */
public class ViewableManageBean extends BaseManageBean
{
	public static final String DLG_VIEW                    = "LMV_VIEW";
	public static final String DLG_VIEW_ATTACH             = "LMV_VIEW_ATTACH";
	public static final String DLG_VIEW_REGISTER           = "LMV_VIEW_REGISTER";
	public static final String DLG_VIEWABLE_APPROVE_DELETE = "LMV_VIEWABLE_APPROVE_DELETE";
	public static final String PARM_DELETE_LOCS            = "DELETE_LINKED_LOCATIONS";

	@Override
    public void initialize() throws MXException, RemoteException
	{
		super.initialize();
	}
	
	public int attachViewable() 
		throws MXException, 
		       RemoteException
	{
		return displayDialog( DLG_VIEW_ATTACH );
	}
	
	public int registerViewable() 
		throws MXException, 
		       RemoteException
	{
		return displayDialog( DLG_VIEW_REGISTER );
	}
		
	public int detatchViewable() 
		throws MXException, 
		       RemoteException
	{
		instantdelete();
		save();
		return EVENT_HANDLED;
	}
	
	public int deleteViewableDlg() 
		throws MXException, 
		       RemoteException
	{
		clientSession.loadDialog( DLG_VIEWABLE_APPROVE_DELETE );
		return EVENT_HANDLED;
	}
	
	public int deleteViewable() 
		throws MXException, 
		       RemoteException
	{
		boolean deleteLocs = false;
		WebClientEvent event = clientSession.getCurrentEvent();
		Object o = event.getValue();
		if( o != null && o instanceof String[] )
		{
			String deleteList[] = (String[])o;
			for( int i = 0; i < deleteList.length; i++ )
			{
				if( deleteList[i].equals( PARM_DELETE_LOCS ))
				{
					deleteLocs = true;
				}
			}
		}

		MboRemote mbo = getMbo();
		MboSetRemote viewerSet = null;
		if( deleteLocs )
		{
			viewerSet = mbo.getMboSet( Viewable.RELATIONSHIP_USEDBY );
			if( viewerSet != null )
			{
				viewerSet.deleteAll();
			}
		}

		if( mbo != null && mbo instanceof ViewablelRemote )
		{
			ViewablelRemote viewable = (ViewablelRemote)mbo;
			viewable.deleteViewable( false );
		}
		instantdelete();
		if( viewerSet != null )
		{
			viewerSet.save();
		}

		return EVENT_HANDLED;
	}

	public int refresh() 
		throws MXException, 
		       RemoteException
	{
		MboRemote mbo = getMbo();
		if( mbo != null && mbo instanceof ViewablelRemote )
		{
			ViewablelRemote view = (ViewablelRemote)mbo;
			view.populate();
			fireDataChangedEvent();
		}
		return EVENT_HANDLED;
	}
	
	@Override
    protected String getDlgId()
	{
		return DLG_VIEW;
	}
}