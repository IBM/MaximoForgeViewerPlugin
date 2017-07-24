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

import psdi.app.bim.viewer.lmv.Bucket;
import psdi.app.bim.viewer.lmv.BucketRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.controller.WebClientEvent;



/**
 * @author Doug Wood	
 * This bean is used to manage storage buckets for the Autodesk Large Model Viewer.
 */
public class BucketManageBean extends BaseManageBean
{
	public static final String DLG_BUCKET                = "LMV_BUCKET";
	public static final String DLG_BUCKET_ATTACH         = "LMV_BUCKET_ATTACH";
	public static final String DLG_BUCKET_APPROVE_DELETE = "LMV_BUCKET_APPROVE_DELETE";
	public static final String DLG_BUCKET_CREATE         = "LMV_BUCKET_CREATE";
	public static final String PARM_DELETE_LOCS          = "DELETE_LINKED_LOCATIONS";

	@Override
    public void initialize() throws MXException, RemoteException
	{
		super.initialize();
	}
	
	public int attachStorage() 
		throws MXException, 
		       RemoteException
	{
		return displayDialog( DLG_BUCKET_ATTACH );
	}
	
	public int createStorage() 
		throws MXException, 
		       RemoteException
	{
		return displayDialog( DLG_BUCKET_CREATE );
	}
		
	public int detatchStorage() 
		throws MXException, 
		       RemoteException
	{
		instantdelete();
		save();
		return EVENT_HANDLED;
	}
	
	public int deleteBucketDlg() 
		throws MXException, 
		       RemoteException
	{
		clientSession.loadDialog( DLG_BUCKET_APPROVE_DELETE );
		return EVENT_HANDLED;
	}
			
	@Override
    protected String getDlgId()
	{
		return DLG_BUCKET;
	}

	
	public int deleteBucket() 
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
			viewerSet = mbo.getMboSet( Bucket.RELATIONSHIP_LINKEDLOCATIONS );
			if( viewerSet != null )
			{
				viewerSet.deleteAll();
			}
		}

		if( mbo != null && mbo instanceof BucketRemote )
		{
			BucketRemote bucket = (BucketRemote)mbo;
			bucket.deleteBucket( false );
		}
		if( viewerSet != null )
		{
			viewerSet.save();
		}

		instantdelete();
		return EVENT_HANDLED;
	}
}