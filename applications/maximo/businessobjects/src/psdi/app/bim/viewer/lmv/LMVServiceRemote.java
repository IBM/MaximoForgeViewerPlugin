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
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.AppServiceRemote;
import psdi.util.MXException;

public interface LMVServiceRemote extends AppServiceRemote 
{
	public ResultAuthentication authenticate( String scope[] )
		    throws IOException, URISyntaxException;

	public ResultCreateBucket bucketCreate(
		String bucketKey,
		String policy,
		String region
	) throws IOException, URISyntaxException;

    public Result bucketDelete(
    	String bucketKey
	) throws IOException, URISyntaxException; 

	public Result bucketGrantRights(
		String bucketKey,
		String serviceId,
		String access
	) throws IOException, URISyntaxException;

    public ResultBucketList bucketList(
    	String region
	) 
		throws IOException, 
		       URISyntaxException; 

	public ResultBucketDetail bucketQueryDetails(
		String bucketKey
	) throws IOException, URISyntaxException;

	public Result bucketRevokeRights(
		String bucketKey,
		String serviceId
	) throws IOException, URISyntaxException;
	
	public String getAuthToken() 
		throws IOException, URISyntaxException;

	public void clearAuthCache() throws RemoteException;

	public MboSetRemote getSavedViews(
		UserInfo userInfo,
		String   modelId,
		String   siteId
	) throws RemoteException, MXException; 

	public Result linkFileSet(
		FileReference master,
		FileReference children[]
	) throws IOException, URISyntaxException;
	
	public MboRemote linkModel(
        UserInfo userInfo,
        String storageName,
        String modelName,
		String description,
        String orgId,
        String siteId,
        boolean linkViewable ) throws RemoteException, MXException;
	
	public MboRemote linkStorage(
        UserInfo userInfo,
        String storageName,
		String description,
        String origId,
        String siteId,
        boolean appendKey ) throws RemoteException, MXException ;

	public ResultObjectList objectList(
		String bucketKey,
		String objectKeyPrefix
	) throws IOException, URISyntaxException;

	public Result objectDelete(
		String bucketKey,
		String objectKey
	) throws IOException, URISyntaxException;

	public ResultObjectDetail objectQueryDetails(
		String bucketKey,
		String objectKey
	) throws IOException, URISyntaxException;

	public ResultObjectDetail objectUploadChunked(
		String         bucketKey,
		String         objectKey,
		String         fileName,
		UploadProgress tracker
	) throws IOException, URISyntaxException, GeneralSecurityException;

	public Result viewableDeregister(
		String viewableURN
	) throws IOException, URISyntaxException;

	public ResultViewerService viewableQuery(
		String viewableURN
	) throws IOException, URISyntaxException;

	public Result viewableRegister(
		String  viewableURN,
		String  region,
		boolean compressed,
		String  rootFilename,
		boolean test,
		boolean force
	) throws IOException, URISyntaxException;
}
