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

import psdi.mbo.MboConstants;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.beans.bim.Constants;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.WebClientEvent;



/**
 * @author Doug Wood	
 * This bean is used to manage storage buckets for the Autodesk Large Model Viewer.
 */
public class BaseDlgBean extends DataBean
{
	private boolean         _initialized = false;
	private int             _row = -1;
	private ControlInstance _ctrl;

	@Override
    public void initialize() throws MXException, RemoteException
	{
		super.initialize(); 
		
		if( _initialized )
		{
			moveTo( _row );
			select( _row );
			return;
		}
		
		WebClientEvent event = clientSession.getCurrentEvent();
		_row = getRowIndexFromEvent( event );
		_ctrl = event.getSourceControlInstance();
		moveTo( _row );
		select( _row );

		MboRemote mbo = getMbo();
		
		// Avoid ugly exception in the log file
		mbo.getThisMboSet().setLogLargFetchResultDisabled( true );

		try
		{
			String siteId     = app.getDataBean().getString( Constants.FIELD_SITEID );
			if( siteId.length() > 0 )
			{
				mbo.setValue( Constants.FIELD_SITEID, siteId, MboConstants.NOACCESSCHECK );
			}
		}
		catch( Throwable t )
		{ /* Ignore - not all mbos have siteId */ }
		
		_initialized = true;
	}
	
	protected ControlInstance getControl()
	{
		return _ctrl;
	}
	
	protected void refresh() 
		throws RemoteException, 
		       MXException
	{
		this.reset();
		if( _ctrl != null )
		{
			DataBean db = _ctrl.getDataBean();
			if( db != null )
			{
				MboSetRemote mboSet = db.getMboSet();
				mboSet.reset();
				db.refreshTable();
			}
		}
	}

	/**
	 * Called by the dialog control when the dialog is canceled. Override this
	 * method if any special processing needs to be done when the user closes
	 * a dialog.
	 */
	@Override
    public int cancelDialog() throws MXException, RemoteException
	{
		DataBean db = _ctrl.getDataBean();
		if( db instanceof BaseManageBean )
		{
			BaseManageBean baseBean = (BaseManageBean)db;
			return baseBean.dialogCanceled();
		}
		return super.cancelDialog();
	}
	
	@Override
	public synchronized int execute() 
		throws MXException, RemoteException
	{
		DataBean db = _ctrl.getDataBean();
		db.refreshTable();
		return super.execute();
	}

	
	@Override
 	protected MboSetRemote getMboSetRemote() 
		throws MXException, 
		       RemoteException
	{
		if( _ctrl == null )
		{
			WebClientEvent event = clientSession.getCurrentEvent();
			_ctrl = event.getSourceControlInstance();
		}
		if( _ctrl == null )
		{
			return null;
		}
		
		DataBean bean = _ctrl.getDataBean();
		MboSetRemote mboSet = bean.getMboSet();
		return mboSet;
	}
}