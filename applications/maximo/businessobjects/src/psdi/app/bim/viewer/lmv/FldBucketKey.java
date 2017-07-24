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
import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * Functionality for the Locations.Location field.
 */
public class FldBucketKey extends MAXTableDomain {
	/**
	 * Construct and attach to the specified mbo value
	 * @throws RemoteException 
	 */
	public FldBucketKey(
	    MboValue mbv 
    )
	    throws MXException, RemoteException
	{
 		super( mbv );

		String query =  "(" + Constants.FIELD_SITEID + " = :" + Bucket.FIELD_SITEID + " OR " + Bucket.FIELD_SITEID + " IS NULL )"
		         + " AND (" + Constants.FIELD_ORGID + " = :" + Bucket.FIELD_ORGID + " OR " + Bucket.FIELD_ORGID + " IS NULL )"
		         + " AND " + Bucket.FIELD_ISBOUND + "= 1";
 		
        String thisAttr = getMboValue().getAttributeName() ;
       	setRelationship( Bucket.TABLE_NAME, Bucket.FIELD_BUCKETKEY + " = :" + thisAttr + " AND " + query );

 		setLookupKeyMapInOrder(
   				new String[] { thisAttr, Bucket.FIELD_BUCKETKEYFULL }
				,
				new String[] { Bucket.FIELD_BUCKETKEY, Bucket.FIELD_BUCKETKEYFULL }
				); 

 		setListCriteria( query );
	}
	
	/**
	 * Sets BucketKey full and clears ObjectKey.
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
		String bucketKey = getMboValue().getString();
		if( bucketKey == null || bucketKey.length() == 0 )
		{
			mbo.setValue( Model.FIELD_BUCKETKEYFULL, "" );
		}
		
		// Clear object key on bucket change during lookup
		if( mbo instanceof Model )
		{
			mbo.setValue( Model.FIELD_OBJECTKEY, "" );
		}
	}
}
