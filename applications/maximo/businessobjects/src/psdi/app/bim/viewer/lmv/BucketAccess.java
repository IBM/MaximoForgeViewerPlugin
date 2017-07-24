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

import psdi.app.bim.viewer.dataapi.Result;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMbo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class BucketAccess extends NonPersistentMbo implements BucketAccessRemote
{
	static final long serialVersionUID = 7195841498189578582L;
	
   	public static final String TABLE_NAME          = "BIMLMVBUCKETACCESS";
	public final static String FIELD_BUCKETKEY     = "BUCKETKEY";
	public final static String FIELD_BUCKETKEYFULL = "BUCKETKEYFULL";
	public final static String FIELD_SERVICEID     = "SERVICEID";
	public final static String FIELD_ACCESS        = "ACCESS";
	
	/**
	 * Constructor.
	 * @param ms The NonPersistentMboSet.
	 */
	public BucketAccess(MboSet ms) throws RemoteException
	{
		super(ms);
	}

	@Override
	public void init() 
		throws MXException
	{
		MboRemote owner = getOwner();
		if( owner != null && owner instanceof BucketRemote )
		{
			try
            {
	            setValue( FIELD_BUCKETKEY,     owner.getString( Bucket.FIELD_BUCKETKEY ) );
	            setValue( FIELD_BUCKETKEYFULL, owner.getString( Bucket.FIELD_BUCKETKEYFULL ) );
            }
            catch( RemoteException e )
            {
	            e.printStackTrace();
            }
		}
	}

	@Override
    public Result grant(
	) 
		throws RemoteException, 
	       MXException
	{ 
		String bucketKey = getBucketKey();
		String serviceId = getString( FIELD_SERVICEID );
		String access    = getString( FIELD_ACCESS );
		access = getTranslator().toInternalString( Bucket.DOMAIN_BIMLMVBUCKETACCESS, access );
		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		Result result = null;
		try
        {
	        result = lmv.bucketGrantRights( bucketKey, serviceId, access );
        }
        catch( Exception e )
        {
	        e.printStackTrace();
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		LMVService.testForError( null, result );
        return result;
    }

	@Override
    public Result revoke(
	) 
		throws RemoteException, 
	       MXException
	{ 
		String bucketKey = getBucketKey();
		String serviceId = getString( FIELD_SERVICEID );
		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		Result result = null;
		try
        {
	        result = lmv.bucketRevokeRights( bucketKey, serviceId );
        }
        catch( Exception e )
        {
	        e.printStackTrace();
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		LMVService.testForError( null, result );
        return result;
    }

	private String getBucketKey() 
		throws RemoteException, 
		       MXException
	{
		MboRemote bucket = getOwner();
		if( bucket == null || !(bucket instanceof BucketRemote ))
		{
			String buckeKey = getString( FIELD_BUCKETKEYFULL );
			if( buckeKey == null )
			{
				// Throw something
			}
			return buckeKey;
		}
		return bucket.getString( FIELD_BUCKETKEYFULL );
	}
}