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

import java.io.File;
import java.rmi.RemoteException;

import psdi.app.bim.project.ImportBase;
import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.ResultObjectDetail;
import psdi.app.bim.viewer.dataapi.UploadProgress;
import psdi.app.bim.viewer.dataapi.ViewerObject;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;


public class      ModelFileLoader
       implements Runnable,
                  UploadProgress
{
	private final long		  _modelUID;
	private ModelUploadLogger _logger;
	private final String      _fileName;
	private final String      _bucketKey;
	private final String      _objectKey;
	private final boolean     _autoRegister;
	
	private boolean           _deleteFiles = true;

	public ModelFileLoader(
		long    		   modelMboUID,
		ModelUploadLogger  logger,
    	String             fileName,
    	String             bucketKey,
    	String             objectKey,
    	boolean            autoRegister
	) 
		throws RemoteException, 
		       MXException 
	{
		_modelUID      = modelMboUID;
		_logger        = logger;
		_fileName      = fileName.replace( "\\", "/" );
		_bucketKey     = bucketKey;
		_objectKey     = objectKey;
		_autoRegister  = autoRegister;
	}
	
	public void setDeleteFiles(
		boolean deleteFiles
	) {
		_deleteFiles = deleteFiles;
	}
	
	@Override
    public void run()
	{
		_logger.setLogLevel( ImportBase.LOG_ALL );
		try
		{
			_logger.start( ImportBase.STATUS_WORKING);

			LMVServiceRemote lmv = (LMVServiceRemote) MXServer.getMXServer().lookup( "BIMLMV" );
			ResultObjectDetail result = null;

	        result = lmv.objectUploadChunked( _bucketKey, _objectKey, _fileName, this );
	        if( result != null && result.isError() )
	        {
	        	String params[] = { result.getErrorCode(), result.getErrorMessage() };
				throw new MXApplicationException( Messages.BUNDLE_MSG, Messages.ERR_AUTODESK_API, params );
	        }
	        
	        MXServer server = MXServer.getMXServer();
			MboSetRemote modelSet = server.getMboSet( Model.TABLE_NAME, _logger.getUserInfo() );
			ModelRemote modelMbo = (ModelRemote)modelSet.getMboForUniqueId( _modelUID );

	        modelMbo.setBound( true );
	        
			_logger.message(  Messages.MSG_UPLOAD_CONPLETE );

			try
	        {
				if( _autoRegister )
				{
					MboSetRemote viewableSet = server.getMboSet( Viewable.TABLE_NAME, _logger.getUserInfo() );
					ViewablelRemote viewable = (ViewablelRemote)viewableSet.add();

					String modelURN = "";
					ViewerObject objects[] = result.getObjects();
					if( objects != null && objects.length > 0 )
					{
						modelURN = objects[0].getId();
					}

					String objecKey = modelMbo.getString( Model.FIELD_OBJECTKEY );
					
					viewable.setValue( Viewable.FIELD_SITEID,          modelMbo.getString( Model.FIELD_SITEID ) );
					viewable.setValue( Viewable.FIELD_ORGID,           modelMbo.getString( Model.FIELD_ORGID ) );
					viewable.setValue( Viewable.FIELD_DESCRIPTION,     modelMbo.getString( Model.FIELD_DESCRIPTION ) );
					viewable.setValue( Viewable.FIELD_LONGDESCRIPTION, modelMbo.getString( Model.FIELD_LONGDESCRIPTION ) );
					viewable.setValue( Viewable.FIELD_OBJECTKEY,       objecKey );
					viewable.setValue( Viewable.FIELD_MODELURN,        modelURN );

					viewable.register();
					viewableSet.save();
					
					String params[] = { objecKey };
					_logger.message(  Messages.MSG_AUTO_REG, params );
				}
	        }
	        catch( Throwable t ) 
	        {
				_logger.exception( t );
	        }


	        _logger.loadComplete();
		}
        catch( Throwable t )			// Be sure anything nasty makes it into the logfile
        {
			_logger.exception( t );
			try
            {
	            _logger.start( ImportBase.STATUS_FAILED );
            }
            catch( Exception e )
            { /* Ignore */   }
			t.printStackTrace();
        }
		finally
		{
	        cleanup();
		}
	}
	
	protected void cleanup()
	{
		_logger.cleanup();
		_logger = null;
		
		if( _deleteFiles )
		{
			File file = new File( _fileName );
			file.delete();
		}
	}

	@Override
    public void progress(
        Result result,
        long processed,
        long total 
    ) {
		if( processed == 0 )
		{
			_logger.setItemCount( total );
		}
		else
		{
			_logger.itemProcessed( processed );
		}
    }
}