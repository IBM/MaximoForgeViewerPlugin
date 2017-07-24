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
package psdi.webclient.beans.bim.viewer.lmv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;

import psdi.app.bim.BIMService;
import psdi.app.bim.viewer.lmv.LMVService;
import psdi.app.bim.viewer.lmv.Model;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.MXObjectNotFoundException;
import psdi.util.MXSystemException;
import psdi.webclient.beans.bim.Constants;
import psdi.webclient.beans.bim.viewer.Messages;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.MPFormData;
import psdi.webclient.system.controller.UploadFile;
import psdi.webclient.system.controller.WebClientEvent;

/**
 * @author Doug Wood
 * This bean is used to select and upload model files from the browser to the Maximo server
 * 
 */
public class ModelUploadSelectBean extends DataBean
{
	/**
	 * The model URL support paramter subsitution of the hostname.  The subsistution
	 * value is specified as the Maximo property bim.model.hostname.  This property
	 * holds the value of that property and is set the first time it is needed
	 */
	private MPFormData    _mpData = null;
	private String        _rootDir = "";
	
	
   /**
     *  Method to import xml/Flat file. Both preview mode and writing into Queue are supported.
	 * @return
	 * @throws MXException
	 * @throws RemoteException 
	 */
	public int loadModelFile() 
		throws MXException, 
		       RemoteException
	{
		HttpServletRequest request = clientSession.getRequest();
		
		try
        {
	    	_rootDir = MXServer.getMXServer().getProperty( BIMService.PROP_NAME_BIM_MODEL_DIR );
			// Check root dir to be sure it exists.
			File file=new File( _rootDir );
			 boolean exists = file.exists();
			 if (!exists) 
			 {
				 throw new MXObjectNotFoundException(Constants.BUNDLE_MSG, Messages.ERR_MODEL_DIR_MISSING );
			 }
        }
        catch( Exception e )
        {
            throw new MXObjectNotFoundException("system", "objectnotfound",e);
        }
		
		WebClientEvent wce = clientSession.getCurrentEvent();
		ControlInstance uploadfileControl = wce.getSourceControlInstance();

		MXServer server = MXServer.getMXServer();
		String maxfilesize = server.getProperty( LMVService.LMV_UPLOAD_LIMIT );
		if( maxfilesize != null && maxfilesize.length() > 0 )
		{
			uploadfileControl.setProperty( "maxfilesize", maxfilesize );
		}

		maxfilesize = uploadfileControl.getProperty("maxfilesize");

		try
		{
			_mpData = new MPFormData(request, Integer.parseInt(maxfilesize));
			UploadFile uf = new UploadFile( _mpData.getFileName(), _mpData.getFullFileName(), 
					                        _mpData.getFileContentType(), _mpData.getFileOutputStream());
			uf.setDirectoryName( _rootDir );
			uf.writeToDisk();
	    	String srcFileName    = _mpData.getFullFileName();
	    	String uploadFileName = uf.getFileName();
			String fileName       = _rootDir + File.separatorChar + uploadFileName;
		
			setValue( Model.FIELD_FILENAME,  fileName );
			setValue( Model.FIELD_OBJECTKEY, srcFileName );

			if (MPFormData.isRequestMultipart(request))
			{
		        DataBean bean = app.getDataBean( ModelManageBean.DLG_MODEL_UPLOAD );
		        if( bean == null )
		        {
		        	bean = getParent();
		        }
		        if( bean != null )
		        {
					bean.setValue( Model.FIELD_OBJECTKEY, srcFileName );
					bean.setValue( Model.FIELD_FILENAME,  fileName );
					app.getAppBean().fireDataChangedEvent();
		        }
			}
		}
        catch( FileNotFoundException e )
        {
        	throw new MXApplicationException(Constants.BUNDLE_MSG_IMPORT, Constants.MSG_BIM_FILE_MISSING );
        }
        catch( IOException e )
        {
            
        	throw new MXSystemException("system", "major", e);
        }
		return EVENT_HANDLED;
	}
}