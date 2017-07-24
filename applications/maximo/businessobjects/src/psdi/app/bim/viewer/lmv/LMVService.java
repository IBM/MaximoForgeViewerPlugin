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

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;

import javax.jws.WebMethod;
import javax.jws.WebService;

import psdi.app.bim.viewer.BuildingModel;
import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.app.bim.viewer.dataapi.FileReference;
import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.ResultAuthentication;
import psdi.app.bim.viewer.dataapi.ResultBucketDetail;
import psdi.app.bim.viewer.dataapi.ResultBucketList;
import psdi.app.bim.viewer.dataapi.ResultCreateBucket;
import psdi.app.bim.viewer.dataapi.ResultObjectDetail;
import psdi.app.bim.viewer.dataapi.ResultObjectList;
import psdi.app.bim.viewer.dataapi.ResultViewerService;
import psdi.app.bim.viewer.dataapi.UploadProgress;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.security.UserInfo;
import psdi.server.AppService;
import psdi.server.MXServer;
import psdi.server.event.EventTopicTree;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

@WebService
public class LMVService extends AppService implements LMVServiceRemote
{
    public final static String VER = "v1";
    
    public final static String SERVICE_NAME = "BIMLMV";
    
    public final static String LMV_KEY          = "bim.viewer.LMV.key"; 
    public final static String LMV_SECRET       = "bim.viewer.LMV.secret";
    public final static String LMV_HOST         = "bim.viewer.LMV.host";
    public final static String LMV_VERSION      = "bim.viewer.LMV.api.version";
    public final static String LMV_UPLOAD_LIMIT = "bim.viewer.LMV.model.maxuploadsize";
    
    public final static String BUCKET_POLICY_TRANSIENT  = "transient";
	public final static String BUCKET_POLICY_TEMPORARY  = "temporary";
	public final static String BUCKET_POLICY_PERSISTENT = "persistent";
	
	private DataRESTAPI _restAPI  = null;
	private MXServer    _mxServer = null;
	
	public LMVService(
	    MXServer mxServer
    )
	    throws RemoteException
	{
		super(mxServer);
		_mxServer = mxServer;
    	_restAPI = new LMVServiceImpl();
	}

	public LMVService()
	    throws RemoteException
	{
		super();
    	_restAPI = new LMVServiceImpl();
	}
	
	@Override
    public void init()
	{
		super.init();
		EventTopicTree evt = MXServer.getEventTopicTree();
		try
		{
			BuildingModelEventListener svlistener = new BuildingModelEventListener();
			evt.register( "maximo." + BuildingModel.TABLE_NAME.toLowerCase() + ".delete", svlistener, true );
			WOTrackEventListener wvlistener = new WOTrackEventListener();
			evt.register( "maximo.workorder.delete", wvlistener, true );
			
//	    	BIMServiceRemote bsr = (BIMServiceRemote) MXServer.getMXServer().lookup( BIMService.SERVICE_NAME );
//	    	bsr.setFactory( new ExtCOBie24Factory() );
		}
		catch( MXException e )
		{
			e.printStackTrace();
		}
//        catch( RemoteException e )
//        {
//	        e.printStackTrace();
//        }
	}

	@Override
    public ResultAuthentication authenticate(
    	String scope[]
	)
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.authenticate( scope );
	}
	
	@Override
    public ResultCreateBucket bucketCreate(
		String bucketKey,
		String policy,
		String region
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.bucketCreate( bucketKey, policy, region );
	}
	
    @Override
    public Result bucketDelete(
    	String bucketKey
	) 
		throws IOException, 
		       URISyntaxException 
    {
		return _restAPI.bucketDelete( bucketKey  );
    }
	
    @Override
    public Result bucketGrantRights(
		String bucketKey,
		String serviceId,
		String access
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.bucketGrantRightsV2( bucketKey, serviceId, access );
	}
	
	@Override
    public ResultBucketList bucketList(
    	String region
	) 
		throws IOException, 
		       URISyntaxException 
    {
		return _restAPI.bucketList( region );
    }
	
	@Override
    public Result bucketRevokeRights(
		String bucketKey,
		String serviceId
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.bucketRevokeRightsV2( bucketKey, serviceId );
	}

	@Override
    public ResultBucketDetail bucketQueryDetails(
		String bucketKey
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.bucketQueryDetails( bucketKey );
	}

	@Override
    public ResultObjectDetail objectQueryDetails(
		String bucketKey,
		String objectKey
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.objectQueryDetails( bucketKey, objectKey );
	}

	@Override
    public Result objectDelete(
		String bucketKey,
		String objectKey
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.objectDelete( bucketKey, objectKey );
	}

	@Override
	public ResultObjectList objectList(
		String bucketKey,
		String objectKeyPrefix
	) 
	    throws IOException, 
	           URISyntaxException
    {
		return _restAPI.objectList( bucketKey, objectKeyPrefix );
    }
	           
	@Override
	public Result viewableDeregister(
		String viewableURN
	) 
		throws IOException, 
		       URISyntaxException
	{
		return _restAPI.viewableDeregister( viewableURN );
	}

	@Override
  	public ResultViewerService viewableQuery(
		String viewableURN
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.viewableQuery( viewableURN );
	}

	@Override
    public Result viewableRegister(
		String  viewableURN,
		String  region,
		boolean compressed,
		String  rootFileName,
		boolean test,
		boolean force
	) 
	    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.viewableRegister( viewableURN, region, compressed, rootFileName, test, force );
	}
	
	@Override
	public void clearAuthCache()
		throws RemoteException 
	{
		_restAPI.clearAuthCache();
	}
	
	@Override
	@WebMethod
	public String getAuthToken() throws IOException, URISyntaxException
	{
		String scope[] = { DataRESTAPI.SCOPE_DATA_READ};
		ResultAuthentication result = _restAPI.authenticate( scope );
		return result.getAuthTokenJSOM();
	}
	
	@Override
	@WebMethod
	public MboSetRemote getSavedViews(
		UserInfo userInfo,
		String   modelId,
		String   siteId
	) 
		throws RemoteException, 
		       MXException 
	{
		MboSetRemote savedViewSet = _mxServer.getMboSet( SavedView.TABLE_NAME, userInfo );
		String query = SavedView.FIELD_BUILDINGMODELID + "=:1 AND " + SavedView.FIELD_SITEID + "=:2 " +
				       "AND ( " + SavedView.FIELD_OWNER + "=:3 OR " + SavedView.FIELD_SHARED + "= 1 )"; 
		SqlFormat sqlf = new SqlFormat( query );
		sqlf.setObject( 1, SavedView.TABLE_NAME, SavedView.FIELD_BUILDINGMODELID, modelId );
		sqlf.setObject( 2, SavedView.TABLE_NAME, SavedView.FIELD_SITEID, siteId );
		sqlf.setObject( 3, SavedView.TABLE_NAME, SavedView.FIELD_OWNER, userInfo.getUserName() );
		savedViewSet.setWhere( sqlf.format() );
		savedViewSet.reset();
		return savedViewSet;
	}

	@Override
	@WebMethod
	public MboRemote linkModel(
		UserInfo userInfo,
		String storageName,
		String modelName,
		String description,
		String orgId,
		String siteId,
		boolean linkViewable
	) 
		throws RemoteException, MXException 
	{
		MboSetRemote modelSet = _mxServer.getMboSet( Model.TABLE_NAME, userInfo );
		
		String userStorageName;
		MXServer server = MXServer.getMXServer();
    	String key       = server.getProperty( LMVService.LMV_KEY );
    	
    	if( storageName.endsWith( key.toLowerCase() ))
    	{
    		userStorageName = storageName.substring( 0, storageName.length() - key.length() );
    	}
    	else
    	{
    		userStorageName = storageName;
    	}
    	
		ModelRemote model = (ModelRemote)modelSet.add();
		model.setValue( Model.FIELD_ORGID,         orgId );
		model.setValue( Model.FIELD_SITEID,        siteId );
		model.setValue( Model.FIELD_BUCKETKEY,     userStorageName );
		model.setValue( Model.FIELD_BUCKETKEYFULL, storageName );
		model.setValue( Model.FIELD_DESCRIPTION,   description );
		model.setValue( Model.FIELD_OBJECTKEY,     modelName );
		model.setValue( Model.FIELD_AUTOLINK,      linkViewable );
		model.attach();
		modelSet.save();
		if( linkViewable )
		{
			model.linkViewable();
		}
		String query = Model.FIELD_SITEID + " = '" + siteId + "' AND " + Model.FIELD_OBJECTKEY + " = '" + modelName.toLowerCase() + "'";
		modelSet.setWhere( query );
		modelSet.reset();
		model = (ModelRemote)modelSet.getMbo( 0 );
		return model;
	}

	@Override
	@WebMethod
	public MboRemote linkStorage(
		UserInfo userInfo,
		String storageName,
		String description,
		String orgId,
		String siteId,
		boolean appendKey
	) 
		throws RemoteException, MXException 
	{
		MboSetRemote bucketSet = _mxServer.getMboSet( Bucket.TABLE_NAME, userInfo );
		BucketRemote bucket = (BucketRemote)bucketSet.add();
		bucket.setValue( Bucket.FIELD_BUCKETKEY,   storageName );
		bucket.setValue( Bucket.FIELD_DESCRIPTION, description );
		bucket.setValue( Bucket.FIELD_ORGID,       orgId );
		bucket.setValue( Bucket.FIELD_SITEID,      siteId );
		bucket.setValue( Bucket.FIELD_ISAPPENDKEY, appendKey );
		bucket.attach();
		String bucketKeyFull = bucket.getString( Bucket.FIELD_BUCKETKEYFULL );
		bucketSet.save();
		bucketSet.setWhere( Bucket.FIELD_BUCKETKEYFULL + " = '" + bucketKeyFull +"'"  );
		bucketSet.reset();
		return bucketSet.getMbo( 0 );
	}

	
	@Override
    public Result linkFileSet(
		FileReference master,
		FileReference children[]
	) 
		    throws IOException, 
	           URISyntaxException
	{
		return _restAPI.linkFileSet( master, children );
	}

	/**
	 * 
	 * @return
	 * @throws GeneralSecurityException 
	 */
	@Override
	public ResultObjectDetail objectUploadChunked(
		String         bucketKey,
		String         objectKey,
		String         fileName,
		UploadProgress tracker
	) 
	    throws URISyntaxException, 
	           GeneralSecurityException, 
	           IOException
    {
		return _restAPI.objectUploadChunked( bucketKey, objectKey, fileName, tracker );
	}
	
	static void testForError(
		Mbo    mbo, 
		Result result
	) 
		throws RemoteException, 
		       MXException 
	{
        if( result.isError() )
        {
        	if( result.getErrorType() == Result.ERROR_TYPE.API )
        	{
        		switch( result.getAPIErrorCode())
        		{
        		case Result.API_ERR_BAD_CHECKSUM:
        			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_BAD_CHECKSUM );
        		case Result.API_ERR_NO_OBJECT:
        			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_NO_OBJECT );
        		}
        	}
        	String errCode = result.getErrorCode();
        	if( errCode.length() == 0 )
        	{
        		errCode = getHTTPMessageFromCode( mbo, result.getHttpStatus() );
        	}
        	String errMsg = result.getErrorMessage();
        	if( errMsg.length() == 0 )
        	{
        		errMsg = "" + result.getRawError();
        	}
        	String params[] = { errCode, errMsg };
        	if( mbo != null )
        	{
        		try
                {
	                mbo.setValue( Bucket.FIELD_LASTERROR, errCode );
	                mbo.setValue( Bucket.FIELD_LONGLASTERROR, errMsg );
                }
                catch( RemoteException e )
                { /* Igmore */ }
                catch( MXException e )
                { /* Igmore */ }
        	}
			throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_AUTODESK_API, params );
        }
	}
	
	
	static String getHTTPMessageFromCode(
		MboRemote mbo,
		int       httpStatus
	) 
		throws RemoteException, 
		       MXException 
	{
		String key = "";
		
		switch( httpStatus )
		{
		case 400:
			key = Messages.HTTP_400;
			break;
		case 401: 
			key = Messages.HTTP_401;
			break;
		case 402:
			key = Messages.HTTP_402;
			break;
		case 403:
			key = Messages.HTTP_403;
			break;
		case 404:
			key = Messages.HTTP_404;
			break;
		case 405:
			key = Messages.HTTP_405;
			break;
		case 406:
			key = Messages.HTTP_406;
			break;
		case 407:
			key = Messages.HTTP_407;
			break;
		case 408:
			key = Messages.HTTP_408;
			break;
		case 409:
			key = Messages.HTTP_409;
			break;
		case 410:
			key = Messages.HTTP_401;
			break;
		case 411:
			key = Messages.HTTP_411;
			break;
		case 412:
			key = Messages.HTTP_412;
			break;
		case 413:
			key = Messages.HTTP_413;
			break;
		case 414:
			key = Messages.HTTP_414;
			break;
		case 415:
			key = Messages.HTTP_415;
			break;
		case 416:
			key = Messages.HTTP_416;
			break;
		case 417:
			key = Messages.HTTP_417;
			break;
		case 421:
			key = Messages.HTTP_421;
			break;
		case 422:
			key = Messages.HTTP_422;
			break;
		case 423:
			key = Messages.HTTP_423;
			break;
		case 424:
			key = Messages.HTTP_424;
			break;
		case 426:
			key = Messages.HTTP_426;
			break;
		case 428:
			key = Messages.HTTP_428;
			break;
		case 429:
			key = Messages.HTTP_429;
			break;
		case 431:
			key = Messages.HTTP_431;
			break;
		case 451:
			key = Messages.HTTP_451;
			break;

		case 500:
			key = Messages.HTTP_500;
			break;
		case 501:
			key = Messages.HTTP_501;
			break;
		case 502:
			key = Messages.HTTP_502;
			break;
		case 503:
			key = Messages.HTTP_503;
			break;
		case 504:
			key = Messages.HTTP_504;
			break;
		case 505:
			key = Messages.HTTP_505;
			break;
		case 506:
			key = Messages.HTTP_506;
			break;
		case 507:
			key = Messages.HTTP_507;
			break;
		case 508:
			key = Messages.HTTP_508;
			break;
		case 510:
			key = Messages.HTTP_510;
			break;
		case 511:
			key = Messages.HTTP_511;
			break;
		}

		return mbo.getMessage( Messages.BUNDLE_MSG, key, "" + httpStatus );
	}
}