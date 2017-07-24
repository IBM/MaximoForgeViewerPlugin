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

import psdi.app.bim.viewer.dataapi.BucketDescription;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMbo;
import psdi.util.MXException;

public class BucketLookup extends NonPersistentMbo implements BucketLookupRemote
{
	static final long serialVersionUID = 7195841498189578582L;
	
   	public static final String TABLE_NAME = "BIMLMVBUCKETLOOKUP";

   	/**
	 * Constructor.
	 * @param ms The NonPersistentMboSet.
	 */
	public BucketLookup(MboSet ms) throws RemoteException
	{
		super(ms);
	}

    public void populate(
    	BucketDescription bd,
    	String            key
	) 
		throws RemoteException, 
		       MXException
    {
    	String bukcetKeyFull = bd.getBucketKey();
    	String bucketKey     = bukcetKeyFull;
    	boolean isAppendKey  = false;
    	key = key .toLowerCase();
    	if( bukcetKeyFull.endsWith( key ))
    	{
    		bucketKey   = bucketKey.substring( 0, bucketKey.length() - key.length() );
    		isAppendKey = true;
    	}
    	setValue( Bucket.FIELD_BUCKETKEY,     bucketKey );
    	setValue( Bucket.FIELD_BUCKETKEYFULL, bukcetKeyFull );
    	setValue( Bucket.FIELD_CREATEDATE,    bd.getCreateDate() );
    	setValue( Bucket.FIELD_ISAPPENDKEY,   isAppendKey );
    	setValue( Bucket.FIELD_POLICYKEY,     "!" + bd.getPolicyKey() + "!" );
    }
}