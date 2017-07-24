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

import psdi.app.bim.Constants;
import psdi.app.site.FldOrganizationID;
import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldModelOrgId
    extends FldOrganizationID
{

	public FldModelOrgId(
        MboValue mbv )
        throws MXException,
        RemoteException
    {
	    super( mbv );
    }

	/**
	 * Sets the orgid in the table that is referenced for the site selected.
	 */
	@Override
    public void action() 
		throws MXException, 
		       RemoteException
	{
		super.action();
		
		Mbo mbo = getMboValue().getMbo();
		mbo.setValue( Constants.FIELD_SITEID, "", NOACCESSCHECK );
		mbo.setValue( Model.FIELD_BUCKETKEY, "", NOACCESSCHECK );
		mbo.setValue( Model.FIELD_BUCKETKEYFULL, "", NOACCESSCHECK );
	}
}
