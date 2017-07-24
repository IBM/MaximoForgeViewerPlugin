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

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;



/**
 * @author Doug Wood	
 * This bean is used to manage storage buckets for the Autodesk Large Model Viewer.
 */
public class BaseManageBean extends DataBean
{
	private int _insertRow = -1;
	private int _currentRow = -1;
	
    protected String getDlgId()
    {
	    return getId();
    }
		
	protected int displayDialog(
		String dialogId
	) 
		throws MXException, 
		       RemoteException 
	{
		_currentRow = getCurrentRow();
		insert( _currentRow + 1);
		for( int i = 0; i < count(); i++ )
		{
			MboRemote mbo =getMbo( i );
			if( mbo.toBeAdded() )
			{
				_insertRow = i;
				break;
			}
		}
		highlightrow( _insertRow );
		WebClientEvent event = new WebClientEvent( dialogId, dialogId, getDlgId(), clientSession);
		event.setRow( _insertRow );
		clientSession.queueEvent( event );
		return EVENT_HANDLED;
	}
	
	public int dialogCanceled() 
		throws MXException
	{
		instantdelete();
		highlightrow( _currentRow );
		_currentRow = -1;
		_insertRow  = -1;
		return EVENT_HANDLED;
	}
	
	public void focusOnMbo() 
		throws RemoteException, 
		       MXException
	{
		WebClientEvent event = clientSession.getCurrentEvent();

		Object o = event.getValue();
		
		if( o != null && o instanceof Long )
		{
			long uid = ((Long)o).longValue();
			MboSetRemote mboSet = getMboSet();
			int count = mboSet.count();
			for( int i = 0; i < count; i++ )
			{
				MboRemote mbo = mboSet.getMbo( i );
				if( mbo.getUniqueIDValue() == uid )
				{
					highlightrow( i );
					break;
				}
			}
		}
		tableStateFlags.setFlag(TABLE_DETAILS_EXPANDED, true);
		_insertRow = -1;
	}

	public void asyncError() 
		throws RemoteException, 
		       MXException
	{
		WebClientEvent event = clientSession.getCurrentEvent();
		Object o = event.getValue();
		if( o != null && o instanceof MXException )
		{
			throw (MXException)o;
		}
	}
}