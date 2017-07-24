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
package psdi.app.bim.viewer.lmv;

import java.rmi.RemoteException;

import psdi.app.bim.viewer.BuildingModel;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.event.EventListener;
import psdi.server.event.EventMessage;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 * @author Doug Wood
 */
public class BuildingModelEventListener implements EventListener
{
	public BuildingModelEventListener()
	{
	}
	

	/* (non-Javadoc)
	 * @see psdi.server.event.EventListener#postCommitEventAction(psdi.server.event.EventMessage)
	 */
	@Override
    public void postCommitEventAction(
	    EventMessage em 
    ) 
		throws MXException
	{
		MboRemote mbo = (MboRemote)em.getEventObject();

		try
		{
			if( em.getEventName().toLowerCase().endsWith( ".delete" ) )
			{
				MboSetRemote savedViewSet = mbo.getMboSet( SavedView.RELATIONSHIP_SAVEDVIEWS );
				savedViewSet.deleteAll();
				savedViewSet.save();
				getLogger().info( "BuildingModelEventListener Event " + em.getEventName() + " has been fired for mbo "
				                          + mbo.getName() + " with id " + mbo.getUniqueIDValue() );
			}
		}
		catch( RemoteException e )
		{
			getLogger().error( e.getMessage(), e );
		}
	}
	
	/**
	 * @return the script logger 
	 */
	protected MXLogger getLogger()
	{
		return MXLoggerFactory.getLogger("maximo.mbo." + BuildingModel.TABLE_NAME.toLowerCase() );
	}

	@Override
	public boolean eventValidate(
	    EventMessage em 
    ) 
		throws MXException
	{
		return true;
	}

	@Override
	public void preSaveEventAction(EventMessage em) throws MXException {
	}

	@Override
	public void eventAction(EventMessage em) throws MXException {
	}
}
