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
import psdi.app.bim.viewer.BuildingModel;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * Functionality for the Locations.Location field.
 */
public class FldBase64URN extends MAXTableDomain {
	/**
	 * Construct and attach to the specified mbo value
	 * @throws RemoteException 
	 */
	public FldBase64URN(
	    MboValue mbv 
    )
	    throws MXException, RemoteException
	{
 		super( mbv );

		String query =  "(" + Constants.FIELD_SITEID + " = :" + Model.FIELD_SITEID + " OR " + Model.FIELD_SITEID + " IS NULL )"
		+ " AND (" + Constants.FIELD_ORGID + " = :" + Model.FIELD_ORGID + " OR " + Model.FIELD_ORGID + " IS NULL )";
 		
        String thisAttr = getMboValue().getAttributeName() ;
        setRelationship(  Viewable.TABLE_NAME, query );

 		setLookupKeyMapInOrder(
   				new String[] { thisAttr, BuildingModel.FIELD_URL, BuildingModel.FIELD_PARAMNAME, BuildingModel.FIELD_DESCRIPTION,
   						       BuildingModel.FIELD_LONGDESCRIPTION }
				,
				new String[] {  Viewable.FIELD_OBJECTKEY, Viewable.FIELD_BASE64URN, Viewable.FIELD_OBJECTKEY, Viewable.FIELD_DESCRIPTION,
   						        Viewable.FIELD_LONGDESCRIPTION }
				); 

 		setListCriteria( query );
	}
	
	
	/**
	 * Sets title if it is blank.
	 */
	@Override
    public void action() 
		throws MXException, 
		       RemoteException
	{
		super.action();
		
		if( !getMboValue().isModified() )
		{
			return;
		}

		Mbo mbo = getMboValue().getMbo();
		// Clear object key on bucket change during lookup
		if( mbo != null && mbo instanceof BuildingModel )
		{
			String title = mbo.getString( BuildingModel.FIELD_TITLE );
			if( title == null || title.length() == 0 )
			{
				String objectKey = getMboValue().getString();
				int fieldLength = mbo.getThisMboSet().getMboValueData( BuildingModel.FIELD_TITLE ).getLength();
				if( objectKey.length() > fieldLength )
				{
					objectKey = objectKey.substring( 0, fieldLength );
				}
				
				mbo.setValue( BuildingModel.FIELD_TITLE, objectKey );
			}
		}
	}

}