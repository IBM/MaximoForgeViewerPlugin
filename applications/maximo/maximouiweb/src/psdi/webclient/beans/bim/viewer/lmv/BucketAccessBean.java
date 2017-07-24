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

import psdi.app.bim.viewer.lmv.BucketAccessRemote;
import psdi.app.bim.viewer.lmv.BucketRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.bim.Constants;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;



/**
 * @author Doug Wood	
 * This bean is used to manage storage buckets for the Autodesk Large Model Viewer.
 */
public class BucketAccessBean extends DataBean
{
	public static final String DLG_BUCKET_GRANT = "LMV_BUCKET_GRANT";
	
	public int grantAccess() 
		throws MXException, 
		       RemoteException
	{
		MboSetRemote mboSet = getMboSet();
		MboRemote mbo = mboSet.getOwner();
		WebClientEvent event = new WebClientEvent( DLG_BUCKET_GRANT, DLG_BUCKET_GRANT, mbo, clientSession);
		clientSession.queueEvent( event );
   		return EVENT_HANDLED;
	}
	
	public int revokeAccess() 
		throws MXException, 
		       RemoteException
	{
		MboRemote access = getMbo();
		if( access == null )
		{
			WebClientEvent event = clientSession.getCurrentEvent();
			int row = event.getRow();
			access = getMbo( row );
		}
		if( !(access instanceof BucketAccessRemote ))
		{
			throw new MXApplicationException( Constants.BUNDLE_MSG, Constants.MSG_INTERNAL_ERR );	
		}
		((BucketAccessRemote)access).revoke();
		BucketRemote bucket = (BucketRemote)access.getOwner();
		relaodTable( bucket );
		return EVENT_HANDLED;
	}
	
	public void relaodTable(
		BucketRemote bucket
	) 
		throws MXException, 
		       RemoteException 
	{
		reset();
		bucket.populate();
		invalidateTableData();
		refreshTable();
		reloadTable();
	}

}