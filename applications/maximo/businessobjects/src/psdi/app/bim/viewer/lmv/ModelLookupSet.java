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

import psdi.app.bim.viewer.dataapi.ResultObjectList;
import psdi.app.bim.viewer.dataapi.ViewerObject;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMboSet;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class ModelLookupSet extends NonPersistentMboSet implements ModelLookupSetRemote 
{
	static final long serialVersionUID = -5008808652019261288L;
	
	private String _bucketKeyFull = "";		// Storage bucket containing models to be queried
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
	public ModelLookupSet(MboServerInterface ms) 
    	throws RemoteException
	{
        super(ms);
    }

    @Override
    public void init() throws MXException, RemoteException
	{
		populate();
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

	@Override
	public void setOwner(MboRemote mbo) throws MXException, RemoteException
	{
		super.setOwner( mbo );
		if( mbo == null )
		{
			_bucketKeyFull = "";
			clear();
			return;
		}
		if( mbo instanceof Bucket ||  mbo instanceof Model )
		{
			boolean populate = false;
	    	if( _bucketKeyFull == null || _bucketKeyFull.length() == 0 )
	    	{
	    		populate = true;
	    	}
			_bucketKeyFull = mbo.getString( Model.FIELD_BUCKETKEYFULL );
			if( populate )
			{
				populate();
			}
		}
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
		return new ModelLookup(ms);
	}

    public ModelLookupRemote populate() 
		throws RemoteException, 
		       MXException
	{
    	if( !_fetchRemote ) return null;

    	if( _bucketKeyFull == null || _bucketKeyFull.length() == 0 )
    	{
    		return null;
    	}
    	
    	String objectKeyPrfix = "";		// Search on ObjectKey starts with
    	MboRemote ownerMbo = getOwner();
    	if( ownerMbo != null )
    	{
    		objectKeyPrfix = ownerMbo.getString( Model.FIELD_OBJECTKEY );
    	}
    	
		MXServer server      = MXServer.getMXServer();
    	String key           = server.getProperty( LMVService.LMV_KEY );
		LMVServiceRemote lmv = (LMVServiceRemote)server.lookup( "BIMLMV" );

		ModelSetRemote modelSet = (ModelSetRemote)server.getMboSet( Model.TABLE_NAME, this.getUserInfo() );
		modelSet.setFetchFromForge( false );
		modelSet.setWhere( "'" + _bucketKeyFull + "' = " + Model.FIELD_BUCKETKEYFULL  );
		modelSet.reset();
		HashSet<String> models = new HashSet<String>();
		int size = modelSet.count();
		for( int i = 0; i < size; i++ )
		{
			MboRemote model = modelSet.getMbo( i );
			String objectKey = model.getString( Model.FIELD_OBJECTKEY );
			models.add( objectKey );
		}

		ResultObjectList result = null;
		try
        {
	        result = lmv.objectList( _bucketKeyFull, objectKeyPrfix );
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
		ModelLookup mbo = (ModelLookup)add();
		ModelLookupRemote startMbo = mbo; 
		LMVService.testForError( mbo, result );
		remove(0);
		
		for( int i = 0; i < result.size(); i++ )
		{
			ViewerObject vo = result.getObject( i );
			if( models.contains( vo.getKey() ) )
			{
				continue;
			}
			mbo = (ModelLookup)add();
			mbo.populate( vo, key );
		}
		
		return startMbo;
	}
}