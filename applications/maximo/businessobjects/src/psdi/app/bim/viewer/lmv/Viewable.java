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

import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.app.bim.viewer.dataapi.FileReference;
import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.ResultViewerService;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.SqlFormat;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class Viewable extends Mbo implements ViewablelRemote
{
   	public static final String TABLE_NAME = "BIMLMVVIEWABLE";
   	
   	public final static String RELATIONSHIP_SOURCEMODEL = "SOURCEMODEL";
   	public final static String RELATIONSHIP_USEDBY      = "USEDBY";
	
	public final static String FIELD_BUCKETKEYFULL    = "BUCKETKEYFULL";
   	public final static String FIELD_BIMLMVMODELID    = "BIMLMVMODELID";
	public final static String FIELD_MODELURN         = "MODELURN";
   	public final static String FIELD_OBJECTKEY        = "OBJECTKEY";
	public final static String FIELD_ORGID            = "ORGID";
	public final static String FIELD_SITEID           = "SITEID";
	public final static String FIELD_BASE64URN        = "BASE64URN";
	public final static String FIELD_ISBOUND          = "ISBOUND";
	public final static String FIELD_CHANGEBY         = "CHANGEBY";
	public final static String FIELD_CHANGEDATE       = "CHANGEDATE";
	public final static String FIELD_COMPRESSED       = "COMPRESSED";
	public final static String FIELD_DESCRIPTION      = "DESCRIPTION";
	public final static String FIELD_LONGDESCRIPTION  = "DESCRIPTION_LONGDESCRIPTION";
	public final static String FIELD_DETAILS          = "DETAILS";
	public final static String FIELD_GUID             = "GUID";
	public final static String FIELD_HASTHUMBNAIL     = "HASTHUMBNAIL"; 
	public final static String FIELD_STARTEDAT        = "STARTEDAT";
	public final static String FIELD_PROGRESS         = "PROGRESS";
	public final static String FIELD_REGION           = "REGION";
	public final static String FIELD_ROOTFILENAME     = "ROOTFILENAME";
	public final static String FIELD_STATUS           = "STATUS";
	public final static String FIELD_SUCCESS          = "SUCCESS";
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
		FIELD_OBJECTKEY,
		FIELD_MODELURN,
		FIELD_BASE64URN,
		FIELD_ORGID, 
		FIELD_SITEID
	};

	/**
	 * Constructor.
	 * @param ms The NonPersistentMboSet.
	 */
	public Viewable(MboSet ms) throws RemoteException
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
			else
			{
				setValue( FIELD_ORGID, "" );
				setValue( FIELD_SITEID, "" );
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
		populate();
		setValue( FIELD_ISBOUND, true );
		
		// model URN is of the format:
		// urn:adsk.objects:os.object:<bucketkey-full>/<objectkey>
		String objectKey = getString( Viewable.FIELD_OBJECTKEY );
		if( objectKey == null || objectKey.length() == 0 )
		{
			String modelURN = getString( Viewable.FIELD_MODELURN );
			int idx = modelURN.lastIndexOf( '/' );
			if( idx > 0 )
			{
				objectKey = modelURN.substring( idx+1 );
				setValue( Viewable.FIELD_OBJECTKEY, objectKey );
			}
		}
		save();
		getThisMboSet().save();
	}
	

	@Override
	/**
	 * Deletes the model from the Autodesk cloud and optional also deletes the Mbo
	 * @param deleteThis	If True, delete the Mbo
	 * @throws RemoteException
	 * @throws MXException
	 */
	public void deleteViewable(
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
		
		String viewableURN   = getString( Viewable.FIELD_MODELURN );

		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		Result result = null;
		try
        {
	        result = lmv.viewableDeregister( viewableURN );
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
    public void populate() 
		throws RemoteException, 
		       MXException
	{
		String modelURN = getString( Viewable.FIELD_MODELURN );

		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		ResultViewerService result = null;
		try
        {
	        result = lmv.viewableQuery( modelURN );
        }
        catch( Exception e )
        {
	        e.printStackTrace();
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
		if( result.getHttpStatus() == 404 )
		{
			String objectKey = getString( Viewable.FIELD_OBJECTKEY );
			String params[] = { objectKey };
			String msg      = getMessage( Messages.BUNDLE_MSG, Messages.WRN_VIEWABLE_NOT_FOUND, params  );
            setValue( FIELD_ONLINE, false, MboValue.NOACCESSCHECK );
            setValue( Bucket.FIELD_LASTERROR,msg );
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.WRN_VIEWABLE_NOT_FOUND, params );
		}
		LMVService.testForError( this, result );
		
        String base64URN = result.getURN();
        String bucketKey = DataRESTAPI.bucketFromBase64URN( base64URN );
        if( !base64URN.toLowerCase().startsWith( "urn:" ) )
        {
        	base64URN = "urn:" + base64URN;
        }
		setValue( FIELD_BUCKETKEYFULL, bucketKey,               NOACCESSCHECK );
		setValue( FIELD_BASE64URN,     base64URN,               NOACCESSCHECK );
		setValue( FIELD_PROGRESS,      result.getProgress(),    NOACCESSCHECK );
		setValue( FIELD_GUID,          result.getGuid(),        NOACCESSCHECK );
		setValue( FIELD_HASTHUMBNAIL,  result.isHasThumbnail(), NOACCESSCHECK );
		setValue( FIELD_STATUS,        result.getStatus(),      NOACCESSCHECK );
		setValue( FIELD_STARTEDAT,     result.getStartedAt(),   NOACCESSCHECK );
		setValue( FIELD_SUCCESS,       result.getSuccess(),     NOACCESSCHECK );
		result.setShowDetails( true );
		setValue( FIELD_DETAILS,       result.toString(),       NOACCESSCHECK );
	}
	
	@Override
    public Result register() 
		throws RemoteException, 
		       MXException
    {
		linkFileSet();
		
		String  modelURN     = getString( Viewable.FIELD_MODELURN );
		String  region       = getString( Viewable.FIELD_REGION );
		boolean compressed   = getBoolean( FIELD_COMPRESSED );
		String  rootFileName = getString( FIELD_ROOTFILENAME );
		if( region == null || region.length() == 0 )
		{
			region = "us";
		}
		MXServer server = MXServer.getMXServer();
   		LMVServiceRemote lmv = (LMVServiceRemote) server.lookup( "BIMLMV" );
		Result result = null;
		try
        {
	        result = lmv.viewableRegister( modelURN, region, compressed, rootFileName, false, true );
        }
        catch( Exception e )
        {
	        e.printStackTrace();
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		LMVService.testForError( this, result );
        
		populate();
		setValue( FIELD_ISBOUND, true );
		save();
		getThisMboSet().save();
        return result;
    }
	
	public void linkFileSet() 
		throws RemoteException, 
		       MXException
	{
		MXServer server = MXServer.getMXServer();
	    MboSetRemote linkSet = server.getMboSet( ModelLink.TABLE_NAME, getUserInfo() );

	    try
	    {
			String objectKey = getString( Viewable.FIELD_BIMLMVMODELID );
			SqlFormat sqf = new SqlFormat( ModelLink.FIELD_PARENTMODELID + " = :1");
			sqf.setObject( 1, ModelLink.TABLE_NAME, ModelLink.FIELD_PARENTMODELID, objectKey );
			linkSet.setWhere( sqf.format() );
			linkSet.reset();
	    }
	    catch( Exception e )
	    {
	    	e.printStackTrace();
	    }
		
		if( linkSet.isEmpty() )
		{
			return;
		}
		
		FileReference master = new FileReference( getString( FIELD_MODELURN ), getString( FIELD_OBJECTKEY ) );
		FileReference children[] = new FileReference[ linkSet.count() ];
		
		int count = linkSet.count();
		for( int i = 0; i < count; i++ )
		{
			MboRemote link = linkSet.getMbo( i );
			children[i] = new FileReference( link.getString( ModelLink.FIELD_CHILDURN ), 
			                                 link.getString( ModelLink.FIELD_CHILDKEY ) );
		}

		LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
		Result result = null;
		try
        {
	        result = lmv.linkFileSet( master, children );
        }
        catch( Exception e )
        {
	        e.printStackTrace();
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NETWORK_FAULT, e );
        }
		LMVService.testForError( null, result );
	}
}