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
import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * Functionality for the Locations.Location field.
 */
public class FldObjectKeyView extends MAXTableDomain {
	/**
	 * Construct and attach to the specified mbo value
	 * @throws RemoteException 
	 */
	public FldObjectKeyView(
	    MboValue mbv 
    )
	    throws MXException, RemoteException
	{
 		super( mbv );

		String query =  "(" + Constants.FIELD_SITEID + " = :" + Model.FIELD_SITEID + " OR " + Model.FIELD_SITEID + " IS NULL )"
		+ " AND (" + Constants.FIELD_ORGID + " = :" + Model.FIELD_ORGID + " OR " + Model.FIELD_ORGID + " IS NULL )";
 		
        setRelationship(  Model.TABLE_NAME, query );

 		setLookupKeyMapInOrder(
  				new String[] { Viewable.FIELD_MODELURN, Viewable.FIELD_OBJECTKEY, Viewable.FIELD_BIMLMVMODELID, Viewable.FIELD_DESCRIPTION,
  						       Viewable.FIELD_LONGDESCRIPTION, Viewable.FIELD_BUCKETKEYFULL }
				,
				new String[] {  Model.FIELD_MODELURN, Model.FIELD_OBJECTKEY, Model.FIELD_BIMLMVMODELID, Model.FIELD_DESCRIPTION,
  								Model.FIELD_LONGDESCRIPTION, Model.FIELD_BUCKETKEYFULL }
				); 

 		
 		String listQuery = "(" + query + " ) AND  not exists ( select 1 from bimlmvviewable where bimlmvmodel.objectkey = bimlmvviewable.objectkey and bimlmvmodel.bucketkeyfull = bimlmvviewable.bucketkeyfull )";
 		setListCriteria( listQuery );
	}
}