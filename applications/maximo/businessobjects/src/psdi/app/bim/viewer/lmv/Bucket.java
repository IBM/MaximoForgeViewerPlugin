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

import psdi.app.bim.viewer.dataapi.Permission;
import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.ResultBucketDetail;
import psdi.app.bim.viewer.dataapi.ResultCreateBucket;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class Bucket extends Mbo implements BucketRemote
{
	static final long serialVersionUID = 7195841498189578582L;
	
   	public static final String TABLE_NAME             = "BIMLMVBUCKET";
	public final static String FIELD_BUCKETID         = "BUCKETID";
	public final static String FIELD_BUCKETKEY        = "BUCKETKEY";
	public final static String FIELD_BUCKETKEYFULL    = "BUCKETKEYFULL";
	public final static String FIELD_CHANGEBY         = "CHANGEBY";
	public final static String FIELD_CHANGEDATE       = "CHANGEDATE";
	public final static String FIELD_CREATEDATE       = "CREATEDATE";
	public final static String FIELD_DESCRIPTION      = "DESCRIPTION";
	public final static String FIELD_LONGDESCRIPTION  = "DESCRIPTION_LONGDESCRIPTION";
	public final static String FIELD_ISAPPENDKEY      = "ISAPPENDKEY";
	public final static String FIELD_ISBOUND          = "ISBOUND";
	public final static String FIELD_REGION           = "REGION";
	public final static String FIELD_OWNER            = "OWNER";
	public final static String FIELD_ORGID            = "ORGID";
	public final static String FIELD_POLICYKEY        = "POLICYKEY";
	public final static String FIELD_SITEID           = "SITEID";
	public final static String FIELD_ONLINE           = "ONLINE";
	public final static String FIELD_LASTERROR        = "LASTERROR";		
	public final static String FIELD_LONGLASTERROR    = "LASTERROR_LONGDESCRIPTION";		
	
	public final static String DOMAIN_BIMLMVBUCKETPOLICY = "BIMLMVBUCKETPOLICY";
	public final static String DOMAIN_BIMLMVBUCKETACCESS = "BIMLMVBUCKETACCESS";
	public final static String DOMAIN_BIMLMVBUCKEREGION  = "BIMLMVBUCKEREGION";
	
	public final static String RELATIONSHIP_MODELS          = "MODELS";
	public final static String RELATIONSHIP_VIEWABLES       = "VIEWABLES";
	public final static String RELATIONSHIP_LINKEDLOCATIONS = "LINKEDLOCATIONS";
	
	public final static int STORAGE_POLICY_UNKNOWN    = -1;
	public final static int STORAGE_POLICY_TRANSIENT  = 1;		// Files retained for 1 day
	public final static int STORAGE_POLICY_TEMPORARY  = 30;		// Files retained for 30 days
	public final static int STORAGE_POLICY_PERSISTENT = 0;
	
	
	/**
	* Used to determine the first modification in the Product object.
	*
	* @see modify
	*/
	boolean isModified = false;
	
	private static final String readOnlyWhenNotNew[] = 
	{
		FIELD_BUCKETKEY,
		FIELD_BUCKETKEYFULL,
		FIELD_ORGID, 
		FIELD_SITEID
	};

	/**
	 * Constructor.
	 * @param ms The NonPersistentMboSet.
	 */
	public Bucket(MboSet ms) throws RemoteException
	{
		super(ms);
	}

	@Override
     public void add() 
     	throws MXException, RemoteException
     {
        super.add();
        
		setValue( FIELD_CHANGEBY, getUserInfo().getUserName(),
		          NOACCESSCHECK | NOVALIDATION_AND_NOACTION );
		setValue( FIELD_CHANGEDATE, MXServer.getMXServer().getDate(),
		           NOACCESSCHECK | NOVALIDATION_AND_NOACTION );

     } // add
	
	@Override
	public void init() 
		throws MXException
	{
		try
		{
			if( !toBeAdded() )
			{
				setFieldFlag( readOnlyWhenNotNew, READONLY, true) ;
			}
		}
		catch ( Exception e )
		{
			// this will never fail...
		}

		boolean isBound = false;
		try
		{
			isBound = getBoolean( FIELD_ISBOUND );
		}
		catch( Exception e )
		{
			return;
		}
		if( isBound )
		{
			try
            {
	            populate();
	            setValue( FIELD_ONLINE, true, MboValue.NOACCESSCHECK );
            }
            catch( RemoteException e )
            {
        		try
                {
                    setValue( Bucket.FIELD_LASTERROR, e.getLocalizedMessage() );
                }
                catch( RemoteException re )
                { /* Ignore */ }
                catch( MXException re )
                { /* Ignore */ }
            }
            catch( MXApplicationException e )
            { /* Ignore - Error message is set inside populate method */ }
		}
	}

	/**
	 * Called when ever a field is modified so we can update the changedate/changeby.
	 *
	 */
	@Override
	public void modify() 
		throws MXException, 
		       RemoteException
	{
		// if isModified has been set, return. Just do once.
		if( isModified )
		{
			return;
		}
		// if isModified has not been set to true.
		// Update the changeby and changedate fields when a change is
		// made to the Location.
		isModified = true;
		if( !getMboValue( FIELD_CHANGEDATE ).isModified() )
		{
			setValue( FIELD_CHANGEDATE, MXServer.getMXServer().getDate(), NOACCESSCHECK );
		}
		if( !getMboValue( FIELD_CHANGEBY ).isModified() )
		{
			setValue( FIELD_CHANGEBY, getUserInfo().getUserName(), NOACCESSCHECK );
		}
	}
	
	@Override
	public void save() 
		throws MXException, 
		       RemoteException
	{

		super.save();
		isModified = false;
	}

	@Override
    public void attach() 
		throws RemoteException, 
		       MXException
	{
		String bucketKey = getString( Bucket.FIELD_BUCKETKEY );
		String bucketKeyFull;
		boolean isAppendKey = getBoolean( FIELD_ISAPPENDKEY );
		if( isAppendKey )
		{
			MXServer server = MXServer.getMXServer();
	    	String key       = server.getProperty( LMVService.LMV_KEY );
	    	bucketKeyFull = bucketKey + key.toLowerCase();
		}
		else
		{
	    	bucketKeyFull = bucketKey;
		}
		setValue( FIELD_BUCKETKEYFULL, bucketKeyFull );
		populate();
		setValue( FIELD_ISBOUND, true );
		save();
	}
	
	@Override
    public ResultCreateBucket create() 
		throws RemoteException, 
		       MXException
    {
		String bucketKey = getString( Bucket.FIELD_BUCKETKEY );
		String bucketKeyFull;
		boolean isAppendKey = getBoolean( FIELD_ISAPPENDKEY );
		if( isAppendKey )
		{
			MXServer server = MXServer.getMXServer();
	    	String key       = server.getProperty( LMVService.LMV_KEY );
	    	bucketKeyFull = bucketKey + key.toLowerCase();
		}
		else
		{
	    	bucketKeyFull = bucketKey;
		}
		String policy    = getString( Bucket.FIELD_POLICYKEY );
		policy = getTranslator().toInternalString( DOMAIN_BIMLMVBUCKETPOLICY, policy );
		
		String region = getString( Bucket.FIELD_REGION );
		if( region != null && region.length() > 0 )
		{
			region = getTranslator().toInternalString( DOMAIN_BIMLMVBUCKEREGION, region );
		}

		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		ResultCreateBucket result = null;
		try
        {
	        result = lmv.bucketCreate( bucketKeyFull, policy, region );
        }
        catch( Exception e )
        {
	        e.printStackTrace();
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		
		LMVService.testForError( this, result );

		setValue( FIELD_BUCKETKEYFULL, bucketKeyFull );
		populate();
		setValue( FIELD_ISBOUND, true );
		save();
		return result;
    }


	@Override
	/**
	 * Deletes the Bucket from the Autodesk cloud and optional also deletes the Mbo
	 * @param deleteThis	If True, delete the Mbo
	 * @throws RemoteException
	 * @throws MXException
	 */
	public void deleteBucket(
    	boolean deleteThis
	) 
		throws RemoteException, 
		       MXException
	{
		boolean isBound = getBoolean( FIELD_ISBOUND );
		if( !isBound && deleteThis )
		{
			delete();
			getThisMboSet().save();
			return;
		}
		
		String bucketKeyFull = getString( Bucket.FIELD_BUCKETKEYFULL );

		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		Result result = null;
		try
        {
	        result = lmv.bucketDelete( bucketKeyFull );
        }
        catch( Exception e )
        {
	        e.printStackTrace();
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		if( result.getHttpStatus() != 404 )
		{
			LMVService.testForError( null, result );
		}
		
		setValue( FIELD_ISBOUND, false );
		
		MboSetRemote mboSet = getMboSet( RELATIONSHIP_MODELS );
		mboSet.deleteAll();
		mboSet.save();

		mboSet = getMboSet( RELATIONSHIP_VIEWABLES );
		mboSet.deleteAll();
		mboSet.save();

		if( deleteThis )
		{
			delete();
			getThisMboSet().save();
		}
	}

	@Override
    public void populate() 
		throws RemoteException, 
		       MXException
	{
		BucketSet bucketSet = (BucketSet)getThisMboSet();
		if( !bucketSet.isFetchFromForge() )
		{
			return;
		}
		String bucketKey = getString( Bucket.FIELD_BUCKETKEYFULL );

		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );

		ResultBucketDetail result = null;
		try
        {
	        result = lmv.bucketQueryDetails( bucketKey );
        }
        catch( Exception e )
        {
    		try
            {
                setValue( Bucket.FIELD_LASTERROR, e.getLocalizedMessage() );
            }
            catch( RemoteException re )
            { /* Ignore */ }
            catch( MXException re )
            { /* Ignore */ }
	        e.printStackTrace();
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		// Other application keys can be granted access to a bucket, but only the creating application can
		// query the bucket details so attaching to a bucket from a different application key fails here
		// with a 403 but the bucket can still be used for model upload, viewable registration and to 
		// view models so the attach must be allowed.
		if( result.getHttpStatus() == 403 )
		{
            setValue( FIELD_ONLINE, false, MboValue.NOACCESSCHECK );
            setValue( Bucket.FIELD_LASTERROR, result.getErrorMessage() );
            return;
		}
		if( result.getHttpStatus() == 404 )
		{
			MXServer server = MXServer.getMXServer();
			String bucket   = getString(  Bucket.FIELD_BUCKETKEY );
	    	String key      = server.getProperty( LMVService.LMV_KEY );
			String params[] = { bucket, key };
			String msg      = getMessage( Messages.BUNDLE_MSG, Messages.WRN_BUCKET_NOT_FOUND, params  );
            setValue( FIELD_ONLINE, false, MboValue.NOACCESSCHECK );
            setValue( Bucket.FIELD_LASTERROR,msg );
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.WRN_BUCKET_NOT_FOUND, params );
		}
		LMVService.testForError( this, result );

		setValue( Bucket.FIELD_CREATEDATE, result.getCreateDate(), NOACCESSCHECK );
		setValue( Bucket.FIELD_OWNER, result.getOwner(), NOACCESSCHECK );
		String trnaslatedValue = getTranslator().toExternalDefaultValue( DOMAIN_BIMLMVBUCKETPOLICY, result.getPolicyKey(), this );
		setValue( Bucket.FIELD_POLICYKEY, trnaslatedValue, NOACCESSCHECK  );
		
		Permission access[] = result.getPermissions();
		MboSetRemote accessSet = getMboSet( "ACCESS" );
		if( accessSet != null )
		{
			accessSet.deleteAll();
			for( int i = 0; access != null && i < access.length; i++ )
			{
				MboRemote accessMbo = accessSet.add();
				accessMbo.setValue( BucketAccess.FIELD_BUCKETKEY, bucketKey, NOACCESSCHECK );
				accessMbo.setValue( BucketAccess.FIELD_SERVICEID, access[i].getServiceId(), NOACCESSCHECK );
				trnaslatedValue = getTranslator().toExternalDefaultValue( DOMAIN_BIMLMVBUCKETACCESS, access[i].getAccess(), this );
				accessMbo.setValue( BucketAccess.FIELD_ACCESS, trnaslatedValue, NOACCESSCHECK );
			}
		}
	}
	
	@Override
    public int retentionPolicy() 
		throws RemoteException, 
		       MXException
	{
		String value = getString( FIELD_POLICYKEY );
		String internalValue = getTranslator().toInternalString( DOMAIN_BIMLMVBUCKETPOLICY, value );
		if( internalValue == null ) return STORAGE_POLICY_UNKNOWN;
		if( internalValue.equals(  "transient" ))  return STORAGE_POLICY_TRANSIENT;
		if( internalValue.equals(  "temporary" ))  return STORAGE_POLICY_TEMPORARY;
		if( internalValue.equals(  "persistent" )) return STORAGE_POLICY_PERSISTENT;
		return 0;
	}
}