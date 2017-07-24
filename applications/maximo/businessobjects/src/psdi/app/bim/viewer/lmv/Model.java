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
import java.util.Calendar;
import java.util.Date;

import psdi.app.bim.Constants;
import psdi.app.bim.project.ImportBase;
import psdi.app.bim.project.ImportBaseRemote;
import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.ResultObjectDetail;
import psdi.app.bim.viewer.dataapi.ViewerObject;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class      Model 
       extends    Mbo 
       implements ModelRemote
{
	static final long serialVersionUID = 7195841498189578582L;
	
   	public static final String TABLE_NAME           = "BIMLMVMODEL";
   	
   	public static final String RELATIONSHOP_UPLOADSTATUS = "UPLOADSTATUS";
   	public static final String RELATIONSHOP_MODELLINK    = "MODELLINK";
   	public static final String RELATIONSHOP_BUCKET       = "BUCKET";
	
   	public final static String FIELD_BIMLMVMODELID    = "BIMLMVMODELID";
   	public final static String FIELD_AUTOLINK         = "AUTOLINK";
   	public final static String FIELD_AUTOREG          = "AUTOREG";
   	public final static String FIELD_OBJECTKEY        = "OBJECTKEY";
	public final static String FIELD_MODELURN         = "MODELURN";
	public final static String FIELD_BUCKETKEY        = "BUCKETKEY";
	public final static String FIELD_BUCKETKEYFULL    = "BUCKETKEYFULL";
	public final static String FIELD_DESCRIPTION      = "DESCRIPTION";
	public final static String FIELD_LONGDESCRIPTION  = "DESCRIPTION_LONGDESCRIPTION";
	public final static String FIELD_ORGID            = "ORGID";
	public final static String FIELD_SITEID           = "SITEID";
	public final static String FIELD_CHANGEBY         = "CHANGEBY";
	public final static String FIELD_CHANGEDATE       = "CHANGEDATE";
	public final static String FIELD_ISBOUND          = "ISBOUND";
    public final static String FIELD_URL              = "URL";
	public final static String FIELD_SIZE             = "SIZE";
	public final static String FIELD_SHA1             = "SHA1";
	public final static String FIELD_CONTENTTYPE      = "CONTENTTYPE";
	public final static String FIELD_FILENAME         = "FILENAME";
	public final static String FIELD_UPLOADSTATUS     = "UPLOADSTATUS";
	public final static String FIELD_ONLINE           = "ONLINE";
	public final static String FIELD_LASTERROR        = "LASTERROR";		
	public final static String FIELD_LONGLASTERROR    = "LASTERROR_LONGDESCRIPTION";		
	
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
		FIELD_OBJECTKEY,
		FIELD_MODELURN,
		FIELD_ORGID, 
		FIELD_SITEID
	};

	/**
	 * Constructor.
	 * @param ms The NonPersistentMboSet.
	 */
	public Model(MboSet ms) throws RemoteException
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
	public void delete(
		long accessModifier
	) 
		throws MXException, RemoteException
	{
		MboSetRemote mboSet = getMboSet( RELATIONSHOP_UPLOADSTATUS );
		mboSet.deleteAll( accessModifier );
		
		mboSet = getMboSet( RELATIONSHOP_MODELLINK );
		mboSet.deleteAll( accessModifier );
		
		super.delete( accessModifier );
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
		populate();
		setValue( FIELD_ISBOUND, true );
		MboSetRemote uploadSet = getMboSet( RELATIONSHOP_UPLOADSTATUS ); 
		ModelUploadRemote upload = (ModelUploadRemote)uploadSet.add(); 
		upload.setValue( ImportBase.FIELD_IMPORTEDBY, getUserInfo().getUserName(), NOACCESSCHECK  );
 		String transValue = getTranslator().toExternalDefaultValue( ModelUpload.DOMAIN_BIMIMPORTSTATUS, ModelUpload.STATUS_LINKED, this );
 		upload.setValue( ModelUpload.FIELD_STATUS, transValue );
 		upload.setValue( ModelUpload.FIELD_FILENAME, getString( FIELD_OBJECTKEY ) );
 		upload.setValue( ModelUpload.FIELD_PERCENTCOMP, 100 );
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( System.currentTimeMillis() );
		upload.setValue( ImportBase.FIELD_UPLOADTIME, cal.getTime() );
		uploadSet.save();
		save();
	}
	
	@Override
    public void linkViewable() 
		throws RemoteException, 
		       MXException
	{
		MXServer server = MXServer.getMXServer();
		MboSetRemote viewableSet = server.getMboSet( Viewable.TABLE_NAME, getUserInfo() );
		ViewablelRemote viewable = (ViewablelRemote)viewableSet.add();

		viewable.setValue( Viewable.FIELD_SITEID,          getString( FIELD_SITEID ) );
		viewable.setValue( Viewable.FIELD_ORGID,           getString( FIELD_ORGID ) );
		viewable.setValue( Viewable.FIELD_DESCRIPTION,     getString( FIELD_DESCRIPTION ) );
		viewable.setValue( Viewable.FIELD_LONGDESCRIPTION, getString( FIELD_LONGDESCRIPTION ) );
		viewable.setValue( Viewable.FIELD_OBJECTKEY,       getString( FIELD_OBJECTKEY ) );
		viewable.setValue( Viewable.FIELD_MODELURN,        getString( FIELD_MODELURN ) );

		viewable.attach();
		viewableSet.save();
	}
	
	@Override
	/**
	 * Deletes the model from the Autodesk cloud and optional also deletes the Mbo
	 * @param deleteThis	If True, delete the Mbo
	 * @throws RemoteException
	 * @throws MXException
	 */
	public void deleteModel(
    	boolean deleteThis
	) 
		throws RemoteException, 
		       MXException
	{
		MboSetRemote mboSet = getMboSet( RELATIONSHOP_UPLOADSTATUS );
		int count = mboSet.count();
		for( int i = 0; i < count; i++ )
		{
			MboRemote mbo = mboSet.getMbo( i );
			if( mbo instanceof ImportBaseRemote )
			{
				if( !((ImportBaseRemote)mbo).isStatusCompelete() )
				{
					throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.MSG_NO_DELETE_WHILE_UPLOAD );
				}
			}
		}
		
		boolean isBound = getBoolean( FIELD_ISBOUND );
		if( !isBound && deleteThis )
		{
			delete();
			getThisMboSet().save();
			return;
		}
		
		String bucketKeyFull = getString( Model.FIELD_BUCKETKEYFULL );
		String objectKey     = getString( Model.FIELD_OBJECTKEY );

		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		Result result = null;
		try
        {
	        result = lmv.objectDelete( bucketKeyFull, objectKey );
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
		
		if( deleteThis )
		{
			delete();
			getThisMboSet().save();
		}
	}

	@Override
    public void uploadFile()
		throws RemoteException, 
		       MXException 
    {
		MboSetRemote uploadSet = getMboSet( RELATIONSHOP_UPLOADSTATUS ); 
		ModelUploadRemote upload = (ModelUploadRemote)uploadSet.add(); 
		upload.setValue( ImportBase.FIELD_IMPORTEDBY, getUserInfo().getUserName(), NOACCESSCHECK  );
		uploadSet.save();
		save();

		long importId    = upload.getLong( ModelUpload.FIELD_BIMLMVMODELUPLOADID );
		ModelUploadLogger logger; 
		logger = new ModelUploadLogger( upload, "" + importId, Constants.BUNDLE_MSG );
    	logger.setLogLevel( ModelUpload.LOG_ERRORS );
    	
		String  bucketKeyFull = getString( Model.FIELD_BUCKETKEYFULL );
		String  objectKey     = getString( Model.FIELD_OBJECTKEY );
		String  fileName      = getString( Model.FIELD_FILENAME );
		boolean autoReg       = getBoolean(  FIELD_AUTOREG );

		ModelFileLoader loader = new ModelFileLoader( this.getUniqueIDValue(), logger, fileName, 
		                                              bucketKeyFull, objectKey, autoReg );
		Thread thread = new Thread( loader );
		thread.start();
    }

	private void populate() 
		throws RemoteException, 
		       MXException
	{
		
		ModelSet modelSet = (ModelSet)getThisMboSet();
		if( !modelSet.isFetchFromForge() )
		{
			return;
		}
		String bucketKeyFull = getString( Model.FIELD_BUCKETKEYFULL );
		String objectKey     = getString( Model.FIELD_OBJECTKEY );

		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		ResultObjectDetail result = null;
		try
        {
	        result = lmv.objectQueryDetails( bucketKeyFull, objectKey );
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
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		// Other application keys can be granted access to a bucket, but only the  application that uploaded 
		// a model can query the details of a model so attaching to a model from a different application 
		// key fails here with a 403 but the model can still be used for viewable registration and to view 
		// models so the attach must be allowed.
		if( result.getHttpStatus() == 403 )
		{
            setValue( FIELD_ONLINE, false, MboValue.NOACCESSCHECK );
            setValue( Bucket.FIELD_LASTERROR, result.getErrorMessage() );
            return;
		}
		if( result.getHttpStatus() == 404 )
		{
			if( testExpired() )
			{
				return;
			}
			String params[] = { objectKey };
			String msg      = getMessage( Messages.BUNDLE_MSG, Messages.WRN_MODEL_NOT_FOUND, params  );
            setValue( FIELD_ONLINE, false, MboValue.NOACCESSCHECK );
            setValue( Bucket.FIELD_LASTERROR,msg );
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.WRN_MODEL_NOT_FOUND, params );
		}
		LMVService.testForError( this, result );
		
        valuesFromResult( result );
	}
	
	@Override
    public void valuesFromResult(
		ResultObjectDetail result
	) 
		throws RemoteException, 
		       MXException 
	{
		ViewerObject objects[] = result.getObjects();
		if( objects != null && objects.length > 0 )
		{
			setValue( Model.FIELD_URL,         objects[0].getLocation(),    NOACCESSCHECK );
			setValue( Model.FIELD_SIZE,        objects[0].getSize(),        NOACCESSCHECK );
			setValue( Model.FIELD_CONTENTTYPE, objects[0].getContentType(), NOACCESSCHECK );
			setValue( Model.FIELD_MODELURN,    objects[0].getId(),          NOACCESSCHECK );
			setValue( Model.FIELD_SHA1,        objects[0].getSha1(),        NOACCESSCHECK );
		}
	}
	
	@Override
    public void setBound(
		boolean bound
	) 
		throws RemoteException, 
		       MXException 
	{
		setValue( Model.FIELD_ISBOUND, bound );
		getThisMboSet().save();
	}
	
	private boolean testExpired() 
		throws RemoteException, 
		       MXException
	{
		boolean isBound = getBoolean( FIELD_ISBOUND );
		if( !isBound ) return false;
		
		MboSetRemote bucketSet = getMboSet( RELATIONSHOP_BUCKET );
		MboRemote mbo = bucketSet.getMbo( 0 );
		if( mbo == null || !(mbo instanceof BucketRemote ))
		{
			return false;
		}
		BucketRemote bucket = (BucketRemote)mbo;
		int policy = bucket.retentionPolicy();
		if( policy == Bucket.STORAGE_POLICY_UNKNOWN || policy == Bucket.STORAGE_POLICY_PERSISTENT )
		{
			return false;
		}
		MboSetRemote statusSet = getMboSet( RELATIONSHOP_UPLOADSTATUS );
		mbo = statusSet.getMbo( 0 );
		if( mbo == null ) return false;		// Should never happen;
		Date upLoadTime = mbo.getDate( ModelUpload.FIELD_UPLOADTIME );
		long currentTime = System.currentTimeMillis();
		long expiresIn = 0;
		switch( policy )
		{
		case Bucket.STORAGE_POLICY_TRANSIENT:
			expiresIn = 1;
			break;
		case Bucket.STORAGE_POLICY_TEMPORARY:
			expiresIn = 30;
			break;
		}
		long timeStamp = upLoadTime.getTime();
		if( expiresIn == 0 ) return false;		// Only happens if a new policy is added and not handled here
		expiresIn = expiresIn * 24 * 3600 * 1000;	// Convert days to milliseconds
		if( timeStamp + expiresIn < currentTime )
		{
			ModelUploadRemote status = (ModelUploadRemote)statusSet.add(); 
			status.setValue( ImportBase.FIELD_IMPORTEDBY, getUserInfo().getUserName(), NOACCESSCHECK  );
	 		String transValue = getTranslator().toExternalDefaultValue( ModelUpload.DOMAIN_BIMIMPORTSTATUS, ModelUpload.STATUS_EXPIRED, this );
	 		status.setValue( ModelUpload.FIELD_STATUS, transValue );
	 		status.setValue( ModelUpload.FIELD_PERCENTCOMP, 100 );
	 		status.setValue( ModelUpload.FIELD_FILENAME, getString( FIELD_OBJECTKEY ) );
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(currentTime );
			status.setValue( ImportBase.FIELD_UPLOADTIME, cal.getTime() );
			statusSet.save();
			setValue( FIELD_ISBOUND, false );
			save();
			getThisMboSet().save();
			return true;
		}
		return false;
	}
}