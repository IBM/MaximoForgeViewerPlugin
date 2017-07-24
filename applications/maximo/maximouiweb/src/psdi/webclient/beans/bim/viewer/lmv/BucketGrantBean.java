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
import psdi.app.bim.viewer.lmv.BucketAccess;
import psdi.app.bim.viewer.lmv.BucketAccessRemote;
import psdi.app.bim.viewer.lmv.BucketRemote;
import psdi.mbo.MboRemote;
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
public class BucketGrantBean extends DataBean
{
	private boolean _initialized = false;
	private ControlInstance _ctrl;
	private BucketRemote    _bucket;

	@Override
    public void initialize() throws MXException, RemoteException
	{
		super.initialize();
		if( _initialized )
		{
			return;
		}
		insert();
		WebClientEvent event = clientSession.getCurrentEvent();
		_ctrl = event.getSourceControlInstance();
		MboRemote mbo = getMbo(); 
		Object o = event.getValue();
		if( o instanceof BucketRemote )
		{
			_bucket = (BucketRemote)o;
			mbo.setValue( BucketAccess.FIELD_BUCKETKEY,     _bucket.getString( Bucket.FIELD_BUCKETKEY ) );
			mbo.setValue( BucketAccess.FIELD_BUCKETKEYFULL, _bucket.getString( Bucket.FIELD_BUCKETKEYFULL ) );
		}
		else
		{
			throw new MXApplicationException( Constants.BUNDLE_MSG, Constants.MSG_INTERNAL_ERR );	
		}

		_initialized = true;
	}
	
	@Override
	public synchronized int execute() 
		throws MXException, 
		       RemoteException
	{
		BucketAccessRemote access = (BucketAccessRemote)getMbo();
		access.validate();

		access.grant();

		super.execute();
		
		DataBean parent = _ctrl.getDataBean();
		if( parent instanceof BucketAccessBean )
		{
			((BucketAccessBean)parent).relaodTable( _bucket );
		}
		return EVENT_HANDLED;
	}
	
}