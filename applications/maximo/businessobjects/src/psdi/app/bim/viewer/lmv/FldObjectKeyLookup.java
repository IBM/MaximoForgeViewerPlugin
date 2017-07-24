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
public class FldObjectKeyLookup extends MAXTableDomain {
	/**
	 * Construct and attach to the specified mbo value
	 * @throws RemoteException 
	 */
	public FldObjectKeyLookup(
	    MboValue mbv 
    )
	    throws MXException, RemoteException
	{
 		super( mbv );

        String thisAttr = getMboValue().getAttributeName() ;
        
		String query =  Model.FIELD_OBJECTKEY + "=:" +  Model.FIELD_OBJECTKEY;
		
 		setLookupKeyMapInOrder(
   				new String[] { thisAttr }
				,
				new String[] { Model.FIELD_OBJECTKEY  }
				); 

 		setListCriteria( query );
	}
	
	@Override
    public void validate() 
		throws MXException, RemoteException
    {
    }
}
