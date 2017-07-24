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

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * Functionality for the Locations.Location field.
 */
public class FldModelLinkURN extends MAXTableDomain {
	/**
	 * Construct and attach to the specified mbo value
	 * @throws RemoteException 
	 */
	public FldModelLinkURN(
	    MboValue mbv 
    )
	    throws MXException, RemoteException
	{
 		super( mbv ); 

		String query = Model.FIELD_BIMLMVMODELID  + " <> :" + ModelLink.FIELD_PARENTMODELID
				+ " AND (" + ModelLink.FIELD_SITEID + " = :" + Model.FIELD_SITEID + " OR " + Model.FIELD_SITEID + " IS NULL )"
		        + " AND (" + ModelLink.FIELD_ORGID + " = :" + Model.FIELD_ORGID + " OR " + Model.FIELD_ORGID + " IS NULL )"
		        + " AND " + ModelLink.FIELD_BUCKETKEYFULL + " = :" + Model.FIELD_BUCKETKEYFULL 
		        + " AND " + Model.FIELD_ISBOUND + " = 1";

        setRelationship(  Model.TABLE_NAME, query );

 		setLookupKeyMapInOrder(
  				new String[] { ModelLink.FIELD_CHILDURN, ModelLink.FIELD_CHILDKEY, ModelLink.FIELD_CHILDMODELID }
				,
				new String[] {  Model.FIELD_MODELURN, Model.FIELD_OBJECTKEY, Model.FIELD_BIMLMVMODELID }
				); 

 		setListCriteria( query );
	}
}