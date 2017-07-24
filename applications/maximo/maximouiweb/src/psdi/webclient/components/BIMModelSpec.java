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
package psdi.webclient.components;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import psdi.app.bim.BIMService;
import psdi.app.bim.viewer.BuildingModel;
import psdi.mbo.MboRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class BIMModelSpec {

	public final static String TABLE_BUILDINGMODEL = "BUILDINGMODEL";
	public final static String FIELD_BUILDINGMODELID = "BUILDINGMODELID";
	
	BIMModelSpec()
	{
		
	}
	
	BIMModelSpec(
        String    locationName,
        String    location,
        String    binding,
        MboRemote model
	) 
		throws RemoteException, 
		       MXException 
	{
		_locationName  = locationName;
		_location      = location;
		_binding       = binding;
		_modelId       = model.getLong(   FIELD_BUILDINGMODELID );
		setTitle( model.getString( BuildingModel.FIELD_TITLE ) );
		_description   = model.getString( BuildingModel.FIELD_DESCRIPTION );
		_assetView     = model.getString( BuildingModel.FIELD_ASSETVIEW );
		_lookupView    = model.getString( BuildingModel.FIELD_LOOKUPVIEW );
		_locationView  = model.getString( BuildingModel.FIELD_LOCATIONVIEW );
		_attribClass   = model.getString( BuildingModel.FIELD_ATTRIBUTECLASS );
		_attribName    = model.getString( BuildingModel.FIELD_ATTRIBUTENAME );
		_paramClass    = model.getString( BuildingModel.FIELD_PARAMCLASS );
		_paramName     = model.getString( BuildingModel.FIELD_PARAMNAME );
		_siteId        = model.getString( BuildingModel.FIELD_SITEID );
		_workOrderView = model.getString( BuildingModel.FIELD_WORKORDERVIEW );
		
		if( !model.getMboValueData( BuildingModel.FIELD_SELMODE ).isNull() )
		{
			try
			{
				_selectionMode = Integer.parseInt( model.getString( BuildingModel.FIELD_SELMODE ) );
			}
			catch( Throwable t )
			{ // Ignore  
			}
		}
		
		String modelURL = model.getString( BuildingModel.FIELD_URL );
		try 
		{
			URL url = new URL( modelURL );
			String hostName = url.getHost(); 
			if( hostName.equalsIgnoreCase( BIMViewer.HOST_PARAM_MARKER ) )
			{
				hostName = getModelHostname();
				_modelURL = url.getProtocol() + "://" + getModelHostname() + url.getPath();
			}
			else
			{
				_modelURL = modelURL;
			}
		} 
		catch (MalformedURLException e) 
		{
			_modelURL = modelURL;
		}

		if( _attribClass == null ) _attribClass = "LcRevitData";
		if( _attribName == null )  _attribName  = "Element";
		if( _paramClass == null )  _paramClass  = "LcRevitData";
		if( _paramName == null )   _paramName   = "Guid";
	}
			
	BIMModelSpec(
        DataBean modelBean
	) 
		throws RemoteException, 
		       MXException 
	{
		_locationName  = modelBean.getString( BuildingModel.FIELD_LOCATION );
		_location      = modelBean.getString( BuildingModel.FIELD_LOCATION );
//			_binding       = binding;
		setTitle( modelBean.getString( BuildingModel.FIELD_TITLE ) );
		_description   = modelBean.getString( BuildingModel.FIELD_DESCRIPTION );
		_assetView     = modelBean.getString( BuildingModel.FIELD_ASSETVIEW );
		_lookupView    = modelBean.getString( BuildingModel.FIELD_LOOKUPVIEW );
		_locationView  = modelBean.getString( BuildingModel.FIELD_LOCATIONVIEW );
		_attribClass   = modelBean.getString( BuildingModel.FIELD_ATTRIBUTECLASS );
		_attribName    = modelBean.getString( BuildingModel.FIELD_ATTRIBUTENAME );
		_paramClass    = modelBean.getString( BuildingModel.FIELD_PARAMCLASS );
		_paramName     = modelBean.getString( BuildingModel.FIELD_PARAMNAME );
		_siteId        = modelBean.getString( BuildingModel.FIELD_SITEID );
		_workOrderView = modelBean.getString( BuildingModel.FIELD_WORKORDERVIEW );
		
		MboRemote modelMbo = modelBean.getMbo();
		_modelId = modelMbo.getLong( FIELD_BUILDINGMODELID );

		
		String modelURL = modelBean.getString( BuildingModel.FIELD_URL );
		
		if( !modelBean.getMboValueData( BuildingModel.FIELD_SELMODE ).isNull() )
		{
			try
			{
				_selectionMode = Integer.parseInt( modelBean.getString( BuildingModel.FIELD_SELMODE ) );
			}
			catch( Throwable t )
			{ // Ignore  
			}
		}
		
		init( modelURL );
	}
	
	private void init(
		String modelURL
	) {
		try 
		{
			URL url = new URL( modelURL );
			String hostName = url.getHost(); 
			if( hostName.equalsIgnoreCase( BIMViewer.HOST_PARAM_MARKER ) )
			{
				hostName = getModelHostname();
				_modelURL = url.getProtocol() + "://" + getModelHostname() + url.getPath();
			}
			else
			{
				_modelURL = modelURL;
			}
		} 
		catch (MalformedURLException e) 
		{
			_modelURL = modelURL;
		}

		if( _attribClass == null ) _attribClass = "LcRevitData";
		if( _attribName == null )  _attribName  = "Element";
		if( _paramClass == null )  _paramClass  = "LcRevitData";
		if( _paramName == null )   _paramName   = "Guid";
	}

	public boolean equals( BIMModelSpec modelSpec )
	{
		if( modelSpec == null )                               return false;
		if( _modelId != modelSpec._modelId )                  return false;
		if( !_locationName.equals( modelSpec._locationName )) return false;
		if( !_location.equals( modelSpec._location ))         return false;
		if( !_binding.equals( modelSpec._binding ))           return false;
		if( !_modelURL.endsWith( modelSpec._modelURL ))       return false;
		if( !_title.equals( modelSpec._title ))               return false;
		if( !_assetView.equals( modelSpec._assetView ))       return false;
		if( !_lookupView.equals( modelSpec._lookupView ))     return false;
		if( !_locationView.equals( modelSpec._locationView )) return false;
		if( !_attribClass.equals( modelSpec._attribClass ))   return false;
		if( !_attribName.equals( modelSpec._attribName ))     return false;
		if( !_paramClass.equals( modelSpec._paramClass )    ) return false;
		if( !_paramName.equals( modelSpec._paramName ))       return false;
		if( _selectionMode != modelSpec._selectionMode )	  return false;
		return true;
	}

	
	public String getLocationName()  { return _locationName; }
	public String getLocation()      { return _location; }
	public String getBinding()       { return _binding; }
	public long   getModelId()       { return _modelId; }
	public String getModelURL()      { return _modelURL; }
	public String getTitle()         { return _title; }
	public String getDescription()   { return _description; }
	public String getAssetView()     { return _assetView; }
	public String getLookupView()    { return _lookupView; }
	public String getLocationView()  { return _locationView; }
	public String getAttribClass()   { return _attribClass; }
	public String getAttribName()    { return _attribName; }
	public String getParamName()     { return _paramName; }
	public String getParamClass()    { return _paramClass; }
	public int    getSelectionMode() { return _selectionMode; }
	public String getSiteId()        { return _siteId; }
	public String getWorkOrderView() { return _workOrderView; }
	
	private void setTitle(
		String title
	) {
		_title = title;
		_title = _title.replace( "\"", "" );
		_title = _title.replace( "'", "" );
	}

	public String getModelHostname()
	{
		String hostName = "";
        try
        {
	        hostName = MXServer.getMXServer().getProperty( BIMService.PROP_NAME_BIM_MODEL_HOST );
        }
        catch( RemoteException e )
        { // Do Nothing   
		}
		if( hostName == null ) hostName = "";
		return hostName;
	}

	private String _assetView = "";			// Default saved view for the asset application
	private String _locationName = "";
	private String _location = "";			// Location attribute value of location model is associated with
	private String _binding = "";			// Value of attribute from associated location that is bound to model
	private long   _modelId = 0;			// Unique ID for mode MBO
	private String _modelURL = "";
	private String _title = "";				// Display title in UI
	private String _description = "";
	private String _lookupView = "";		// Default saved view for the lookup dialog
	private String _locationView = "";		// Default saved view for the location app
	private String _attribClass = "";
	private String _attribName = "";
	private String _paramClass = "";
	private String _paramName = "";
	private int    _selectionMode = -1;		// NavisWorks selection. -1 = use default behavior
	private String _siteId = "";
	private String _workOrderView = "";		// Default saved view for the Work order tracking app
}
