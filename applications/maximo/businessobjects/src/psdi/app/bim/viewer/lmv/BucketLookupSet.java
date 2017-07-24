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
import java.util.HashSet;

import psdi.app.bim.viewer.dataapi.BucketDescription;
import psdi.app.bim.viewer.dataapi.ResultBucketList;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMboSet;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class BucketLookupSet extends NonPersistentMboSet implements BucketLookupSetRemote 
{
	static final long serialVersionUID = -5008808652019261288L;
	
	/**
	 * Controls is records are fetched from the Forge service.  Set to false during
	 * cleanup to optimize performance
	 */
	private boolean _fetchRemote = true;
	
    /** 
     * Construct the set of Non-Persistent Custom Mbos.
     * @param ms The MboServerInterface NonPersistentCustomMboSet uses to access 
     * internals of the MXServer.
     */
	public BucketLookupSet(MboServerInterface ms) 
    	throws RemoteException
	{
        super(ms);
    }
	
	@Override
	public void cleanup() 
		throws MXException, 
		       RemoteException
	{
		_fetchRemote = false;
		super.cleanup();
		_fetchRemote = true;
	}


    /** 
     * Factory method to create non-persistentcustom mbos.
     * @param ms the NonPersistentCustomMbo  MboSet.
     * @return a non-persistent custom Mbo.
     */
	@Override
    protected Mbo getMboInstance(
		MboSet ms
	) 
        throws MXException, RemoteException
	{
		return new BucketLookup(ms);
	}
	
	@Override
	public MboRemote setup() 
		throws MXException, 
		       RemoteException
	{
		// remove any existing Mbos
		while (getMbo(0) != null)
		{
			remove(0);
		}
		
		return populate();
	} // setup


    public BucketLookupRemote populate() 
		throws RemoteException, 
		       MXException
	{
    	if( !_fetchRemote ) return null;
    	
		MXServer server      = MXServer.getMXServer();
    	String key           = server.getProperty( LMVService.LMV_KEY );
		LMVServiceRemote lmv = (LMVServiceRemote)server.lookup( "BIMLMV" );
		
		BucketSetRemote bucketSet = (BucketSetRemote)server.getMboSet( Bucket.TABLE_NAME, this.getUserInfo() );
		bucketSet.setFetchFromForge( false );
		HashSet<String> buckets = new HashSet<String>();
		int size = bucketSet.count();
		for( int i = 0; i < size; i++ )
		{
			MboRemote bucket = bucketSet.getMbo( i );
			String bucketKey = bucket.getString( Bucket.FIELD_BUCKETKEYFULL );
			buckets.add( bucketKey );
		}

		String region = "";
    	MboRemote ownerMbo = getOwner();
    	if( ownerMbo != null && ownerMbo instanceof Bucket)
    	{
    		region = ownerMbo.getString( Bucket.FIELD_REGION );
    		if( region != null && region.length() > 0 )
    		{
    			region = getTranslator().toInternalString( Bucket.DOMAIN_BIMLMVBUCKEREGION, region );
    		}
    	}
		
		ResultBucketList result = null;
		try
        {
	        result = lmv.bucketList( region );
        }
        catch( Exception e )
        {
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		if( result.getHttpStatus() == 404 )
		{
			String bucket = getString(  Bucket.FIELD_BUCKETKEY );
			String params[] = { bucket, key };
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.WRN_BUCKET_NOT_FOUND, params );
		}
		BucketLookup mbo = (BucketLookup)add();
		BucketLookupRemote startMbo = mbo; 
		LMVService.testForError( mbo, result );
		remove(0);
		
		for( int i = 0; i < result.size(); i++ )
		{
			BucketDescription bd = result.getBucket( i );
			if( buckets.contains( bd.getBucketKey() ) )
			{
				continue;
			}
			mbo = (BucketLookup)add();
			mbo.populate( bd, key );
		}
		
		return startMbo;
	}
}