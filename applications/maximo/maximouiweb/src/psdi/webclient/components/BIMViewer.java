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

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import psdi.app.asset.AssetSetRemote;
import psdi.app.bim.BIMService;
import psdi.app.bim.Constants;
import psdi.app.bim.viewer.BuildingModel;
import psdi.app.bim.viewer.BuildingModelSet;
import psdi.app.bim.viewer.BuildingModelSetRemote;
import psdi.app.location.Location;
import psdi.app.location.LocationRemote;
import psdi.app.location.LocationSetRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.asset.AssetAppBean;
import psdi.webclient.beans.bim.viewer.AssetLookup;
import psdi.webclient.beans.bim.viewer.AssetLookupMulti;
import psdi.webclient.beans.bim.viewer.ModelAppBean;
import psdi.webclient.beans.bim.viewer.WOModelLocBean;
import psdi.webclient.beans.location.LocationAppBean;
import psdi.webclient.beans.workorder.WOAppBean;
import psdi.webclient.controls.TabGroup;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.beans.WebClientBean;
import psdi.webclient.system.controller.AppInstance;
import psdi.webclient.system.controller.BaseInstance;
import psdi.webclient.system.controller.BoundComponentInstance;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.PageInstance;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.session.WebClientSession;

/**
 * Component class to expose an API for integrating 3D BIM viewers with Maximo.
 * The reference implementation is the Autodesk NavisWOrks ActiveX control
 * 
 * <table>
 * 	<tr>
 * 		<td><b>Prefix</b></td><td><b>Use</b></td>
 * 	</tr>
 * 	<tr>
 * 		<td>event</td><td>Call from control jsp via JavaScript sentEvent function</td>
 * 	</tr>
 * 	<tr>
 * 		<td>jsp</td><td>Call from control jsp to dynamically generate JavaScriptn</td>
 * 	</tr>
 * 	<tr>
 * 		<td>jsp</td><td>Call from control jsp to dynamically generate JavaScriptn</td>
 * 	</tr>
 * </table>
 * 
 * @author Doug Wood
 */
public class BIMViewer extends BoundComponentInstance
{
	public final static int    VERSION_LESS_THAN_7116  = BIMService.VERSION_LESS_THAN_7116;
	public final static int    VERSION_7116_OR_GREATER = BIMService.VERSION_7116_OR_GREATER;
	public final static int    VERSION_75_OR_GREATER   = BIMService.VERSION_75_OR_GREATER;

	
	public final static String PROP_DATA_ATTRIB  = "dataattribute";
	public final static String PROP_MODEL_ATTRIB = "modelattribute";
	
	public final static String FIELD_ASSETUID        = "ASSETUID";
    public final static String FIELD_ASSETNUM        = "ASSETNUM";
    public final static String FIELD_BUILDINGMODELID = "BUILDINGMODELID";
    public final static String FIELD_ORGID           = "ORGID";
    public final static String FIELD_SITEID          = "SITEID";
    public final static String FIELD_LOCATION        = "LOCATION";
    public final static String FIELD_LOCATIONUID     = "LOCATIONSID";
    public final static String FIELD_MODELID         = "MODELID";
    public final static String FIELD_NETWORK         = "NETWORK";
    public final static String FIELD_PARENT          = "PARENT";
    public final static String FIELD_PRIMARYSYSTEM   = "PRIMARYSYSTEM";
    public final static String FIELD_SYSTEMID        = "SYSTEMID";
    public final static String FIELD_WO_NUM          = "WONUM";
    
    public final static String TABLE_ASSET        = "ASSET";
    public final static String TABLE_LOCATIONS    = "LOCATIONS";
    public final static String TABLE_WORKORDER    = "WORKORDER";
    public final static String TABLE_LOCACCESTOR  = "LOCACCESTOR";
    public final static String TABLE_LOCHIERARCHY = "LOCHIERARCHY";
    public final static String TABLE_LOCSYSTEM    = "LOCSYSTEM";
    
    public final static String HOST_PARAM_MARKER  = "<HOSTNAME>";
    
	/**
	 * Used to store the current resize option in the HTTP Session
	 */
    public final static String ATTRIB_RESIZE     = "bim.resize";
    public final static String ATTRIB_RESIZE_DLG = "bim.resize.dlg";

	public final static int    TYPE_UNKNOWN   = 0;
    public final static int    TYPE_ASSET     = 1;
    public final static int    TYPE_LOCATION  = 2;
    public final static int    TYPE_LOOKUP    = 3;
    public final static int    TYPE_WORKORDER = 4;
    public final static int    TYPE_MODEL     = 5;
    
    public final static int    RECORD_UNKNOWN  = 0;
    public final static int    RECORD_ASSET    = 1;
    public final static int    RECORD_LOCATION = 2;
    public final static int    RECORD_MODEL    = 3;

	// 1 =:modelid, 2 =:modelLocation 3 =:SiteId
	private final static String QUERY_LOC_MODELID = 
			"=:1 and location in ( select location from locancestor where ancestor =:2 " + 
			" and systemid in (select systemid from locsystem where siteid =:3 and primarysystem = 1) ) ";
	
	private final static String QUERY_MODEL_FILE =
			"location in (select ancestor from locancestor where location =:1 and siteid =:2 and systemid =:3) and siteid =:2";
    
    private MXServer         _server;
	private WebClientSession _wcs = null;
	
	/**
	 * Get an instance of the building model set to use for quering building models
	 */
	private BuildingModelSetRemote _modelSet;
	
	/**
	 * Indication the type of application the control is associated with
	 * Determined by the class of the data bean 
	 */
	private int _type = TYPE_UNKNOWN;
	
	/**
	 * The base type of the parent Mbo
	 */
	private int _recordType = RECORD_UNKNOWN;
	
	/**
	 * Track if the model file has change from the last one loaded
	 */
	private Vector<BIMModelSpec>  _currentModelList    = new Vector<BIMModelSpec>();
	private boolean            _hasModelListChanged = true;
	private String             _currentValue        = "";
	private boolean            _hasValueChanged     = true;
	
	/**
	 * Forces the model file list cache to be flushed and reloaded
	 */
	private boolean            _forceUpdate         = false;
	
	/**
	 * Tracks if the control should allow the user to select more that a single
	 * item - True when the control is used for multi-select lookup on the
	 * Service Request and Work order applications
	 * 
	 * This value is set at initilzation based on the class of the parent
	 * DataBean
	 */
	private boolean _isMultiSelectAllowed = false;
	
	/**
	 * Tracks the currently selected value in the model viewer.
	 * Updated by eventSetContext sent from the .jsp a
	 */
	private String _lookupValue = null;
	
	/**
	 * Tracks if the app is mapped into the visible are of the screen
	 */
	private boolean _controlVisible = false;
	
	/**
	 * Set of all values in the current selection
	 * Used by dialogs that ned the current selection
	 */
	private Set<String> _currentSelection;
	
	/**
	 * Set of item to push to the model for selection.
	 * Cleared after push.  The viewer has different behavior for single and
	 * multi selections
	 */
	private Set<String> _multiSelection = null;
	
	/**
	 * Determines if the multiSelect call requests a zoom to context.  This is set to true
	 * by the eventMultiSelect method and set to false in the jspScript method when a request
	 * is made
	 */
	private boolean     _multeSelectZoomToContext = false;

	/**
	 * Used to communicate back to the .jsp the results of a eventSerContext.
	 * If the model ID sent in the event is not found in Maximo, this is
	 * set to false
	 */
	private boolean _isSelectionValid = true;
	
	/**
	 * The name of the Mbo field used to map elements of the model
	 * to Maximo.  It must be unique within siteid
	 * 
	 * This is read from the datattribtue value if the UI if specified.
	 * The default is "assetnum" for asset and "location" for location 
	 */
	private String _binding = null;
	
	/**
	 * Specified in the presentation XML.  When the control is bound to
	 * an Asset MBO, the is the filed in the parent location for the
	 * current asset that is used for selecting items in the model
	 */
	private String _modelId	= null;
	
	/**
	 * The calue of the location field of the Macimo location record with which the
	 * model currenly displayed in the viewer is assocated
	 * <p>
	 * It is set by the eventSelect method.  could be null if nothing is selected
	 */
	private String _modelLocation = null;
	
	/**
	 * Controls if selecting items in the viewer up date the current maximo 
	 * contect.  If false, the eventSelect is not processed
	 */
	private boolean selectionEnabled = true;

	/**
	 * Although the control appears on a tab, it is really in the client 
	 * area. To maintain the appears of being on a tab, it must manage its
	 * visibility based on the current;y displayed tab.  The following 
	 * attributes provide the refresh method the name of the tab group
	 * the control is part of and the name of the tab that the control 
	 * should be visible when selected
	 */
	private String _tabGroupName = null;
	private String _tabName = null;
	
	/**
	 * Used to push updates to the status line of the control 
	 */
	private String _statusUpdate = null;
	
	private String _width  = "950px";
	private int    _height = 468;
	private int    _topOffset = 0;
	private int    _leftOffset = 0;

	/**
	 * used to control version specific implementations in the linked .jsp
	 */
	private int _mxVersion = VERSION_LESS_THAN_7116;
	
	private String _activeViewer = "navisworks";

	@Override
	public void initialize() 
    {
        super.initialize();
        _wcs              = getWebClientSession();
		_controlVisible   = false;
		_currentSelection = new HashSet<String>();

		try
		{
			_server = MXServer.getMXServer();
			
			String version = _server.getMaxupgValue();
			if( version.compareTo( "V7116" ) >= 0 )
			{
				_mxVersion = VERSION_7116_OR_GREATER;
			}
			if( version.compareTo( "V75" ) >= 0 )
			{
				_mxVersion = VERSION_75_OR_GREATER;
			}
			if( version.compareTo( "V76" ) >= 0 )
			{
				_mxVersion = VERSION_75_OR_GREATER;
			}  
			
			try
			{
	    		_activeViewer = _server.getProperty( BIMService.PROP_NAME_ACTIVE_VIEWER );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}

			
//			MboRemote mbo = getDataBean().getMbo(0);
			MboSetRemote mboSet = getDataBean().getMboSet();
			UserInfo userInfo = mboSet.getUserInfo();
			if( mboSet instanceof AssetSetRemote )
			{
				_recordType = RECORD_ASSET;
			}
			else if( mboSet instanceof LocationSetRemote )
			{
				_recordType = RECORD_LOCATION;
			}
			else if( mboSet instanceof BuildingModelSet )
			{
				_recordType = RECORD_MODEL;
			}
			
			
			_modelSet = (BuildingModelSetRemote)_server.getMboSet( "BUILDINGMODEL", userInfo );
			_modelSet.setWhere( "siteid is null" );
			_modelSet.reset();

			if( !setupControlType( dataBean ) )
			{
				DataBean bean = dataBean.getParent();
				setupControlType( bean );
			}
			
		}
		catch( Exception e )
		{
			// Do nothing type is TYPE_UNKNOWN
		}
		
		String binding = getProperty( PROP_DATA_ATTRIB );
		if( binding != null && binding.length() > 0 )
		{
			_binding = binding;
		}
		_modelId = getProperty( PROP_MODEL_ATTRIB );
		if( _modelId == null || _modelId.length() == 0 )
		{
			_modelId = "MODELID";
		}
		_tabGroupName = getProperty( "tabgroup" );
		if( _tabGroupName == null || _tabGroupName.length() == 0 )
		{
			_tabGroupName = "maintabs";
		}
		_tabName = getProperty( "tab" );
		if( _tabName == null || _tabName.length() == 0 )
		{
			_tabName = "view";
		}
		
		// Require for the Create WOrk ORder button when bound to asset
		setProperty( BoundComponentInstance.ATTRIBUTE_NAME, _binding );

		String tmp = getProperty("topoffset");
		if( tmp != null && tmp.length() > 0 )
		{
			_topOffset = Integer.parseInt( tmp );
		}
		tmp = getProperty("leftoffset");
		if( tmp != null && tmp.length() > 0 )
		{
			_leftOffset = Integer.parseInt( tmp );
		}

		tmp = getProperty("height");
		_height = Integer.parseInt( tmp );
		if( _height <= 0 )
		{
			_height = 368;
		}   
		
		// This might be 100% or some other CSS string value
		_width = getProperty("width");
		int intVal = -1;
		try
		{ 
			intVal = Integer.parseInt( _width );
		}
		catch( Throwable T )
		{ /* Do Nothing */ }
		if( intVal > 0 )
		{
			_width = "" + (intVal - _leftOffset);
		}
		     
		tmp = getProperty("selection_enabled");
		if( tmp != null && tmp.length() > 0 )
		{
			if( tmp.equalsIgnoreCase( "FALSE" ))
			{
				selectionEnabled = false;
			}
		}
		  
		if(	_type == TYPE_WORKORDER )
		{
			AppInstance app = getWebClientSession().getCurrentApp();
			try
			{
				if( app.isSigOptionCheck( "BIMVIEWER" ) )
				{
					_wcs.queueEvent(new WebClientEvent("bimviewer", getId(), _binding, _wcs));
				}
			}
			catch( MXException mxe )
			{
				// Ignore event not queued if there is not access
			}
		}
		else if( _type == TYPE_LOOKUP  )
		{
			_wcs.queueEvent(new WebClientEvent("bimviewer", getId(), _binding, _wcs));
		}
	}
	
	private boolean setupControlType(
		DataBean bean
	) {
		if( bean instanceof AssetAppBean )
		{
			_type = TYPE_ASSET;
			return true;
		}
		if( bean instanceof LocationAppBean )
		{
			_type = TYPE_LOCATION;
			_isMultiSelectAllowed = true;
			return true;
		}
		if( bean instanceof AssetLookup )
		{
			_type = TYPE_LOOKUP;
			return true;
		}
		if( bean instanceof AssetLookupMulti )
		{
			_type = TYPE_LOOKUP;
			_isMultiSelectAllowed = true;
			return true;
		}
		if( bean instanceof WOAppBean || dataBean instanceof WOModelLocBean )
		{
			_type = TYPE_WORKORDER;
			_isMultiSelectAllowed = true;
			return true;
		}
		if( bean instanceof ModelAppBean )
		{
			_type = TYPE_MODEL;
			_isMultiSelectAllowed = false;
			return true;
		}
		
		return false;
	}
    
	@Override
	public int render() 
		throws NoSuchMethodException, 
		       IllegalAccessException, 
		       InvocationTargetException
	{
		checkVisibility();
		return super.render();
	}
	
	/**
	 * Determine if the control is visible or on a tab that is hidden
	 * @return
	 */
	public int checkVisibility()
	{		
		PageInstance currentPage = getPage();
		TabGroup maintab = (TabGroup) currentPage.getControlInstance( _tabGroupName );
		boolean shouldAppVis = true;
		if( maintab != null )
		{
			shouldAppVis = maintab.getCurrentTab().getId().equalsIgnoreCase( _tabName );
		}
		if( shouldAppVis != isControlVisible() )
		{
			setControlVisible( shouldAppVis );
			setChangedFlag();
		}
		return WebClientBean.EVENT_HANDLED;
	}
	
	//**************************************************************************
	// This section has handler for messages sent from the .jsp
	//**************************************************************************
	/**
	 * Called from the viewer .jsp every time the selection is changed.
	 * The event parameter is string that is a ; delimited set of values.
	 * The first value in the list is the the location ID for the model. 
	 * The second value is the currently selected item.  The
	 * remaining members are the members of the selection set including
	 * the selected item
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	public int eventSelect() 
		throws RemoteException, 
		       MXException
	{
		if( !selectionEnabled )
		{
			return WebClientBean.EVENT_HANDLED;
		}
		
		WebClientEvent event = _wcs.getCurrentEvent();
		Object o = event.getValue();
		if( o == null || !(o instanceof String ))
		{
			// Should never happen unless the .jsp is altered
			return WebClientBean.EVENT_HANDLED;
		}
		String result[] = ((String)o).split( ";" );
		
		_currentSelection = new HashSet<String>();
		
		if( result.length < 2 )	// Shouldn't happen
		{
			return WebClientBean.EVENT_HANDLED;
		}
		
		for( int i = 2; i < result.length; i++ )
		{
			_currentSelection.add( result[i] );
		}
		_modelLocation = result[0];
		setCurrentSelection( result[1] );
		return WebClientBean.EVENT_HANDLED;
	}

	/**
	 * Called from the .jsp when the user selects a new size for the control.  
	 * The size is saved in the session so it persists across refreshes
	 */
	public void eventRezise()
	{
		WebClientEvent event = _wcs.getCurrentEvent();
		Object o = event.getValue();
		HttpServletRequest thisRequest = _wcs.getRequest();
	    HttpSession session = thisRequest.getSession();
	    session.setAttribute( ATTRIB_RESIZE, o );
	}

	/**
	 * Called from the .jsp when the user selects a new size for the control and 
	 * the control is displayed on a dialog. The size is saved in the session so 
	 * it persists across refreshes
	 */
	public void eventReziseDlg()
	{
		WebClientEvent event = _wcs.getCurrentEvent();
		Object o = event.getValue();
		HttpServletRequest thisRequest = _wcs.getRequest();
	    HttpSession session = thisRequest.getSession();
	    session.setAttribute( ATTRIB_RESIZE_DLG, o );
	}
	
	protected void updateCurrentSelection(
		String value
	) {
		_currentSelection = new HashSet<String>();
		if( value != null & !value.equals( ""))
		{
			_currentSelection.add( dataBean.getString( FIELD_MODELID ) );
		}
	}
	
	/**
	 * Makes an item selected in the viewer the current Maximo record
	 * @param locationId	The location field value of the location associated with
	 *                      the current model file
	 * @param modelId		The modelId of the item in the model to select
	 * @return				True if the requested selection is found
	 * @throws RemoteException
	 * @throws MXException
	 */
	private boolean setCurrentSelection(
		String modelId
	) 
		throws RemoteException, MXException 
	{
		long uid = lookupUid( modelId );

		if( uid == -1 )
		{
			setNotFoundStatus();
			_isSelectionValid = false;
			return false;
		}
		long oldUid = dataBean.getUniqueIdValue();
		if( oldUid == uid )
		{
			return true;
		}
		_currentValue = modelId;
		if( dataBean instanceof AppBean )
		{
			AppBean appBean = (AppBean)dataBean;
			if( appBean.saveYesNoInteractionCheck() )
			{
				appBean.getMboForUniqueId( uid );
				dataBean.fireDataChangedEvent();
				dataBean.fireStructureChangedEvent();
				_isSelectionValid = true;
			}
		}
		else
		{
			dataBean.getMboForUniqueId( uid );
			dataBean.fireDataChangedEvent();
			dataBean.fireStructureChangedEvent();
			_isSelectionValid = true;
		}
		
		if( dataBean instanceof AssetLookup )
		{
			_lookupValue = modelId;
		}
		return true;
	}
	
	//******************************************************************************
	// The section has methods that are called from the .jsp to move data from
	// Maximo to the .jsp.  Many of the methods clear the data once it is transfered
	//
	// These methods should not be used except by the .jsp
	//******************************************************************************

	public Set<String> jspGetMultiSelection()
	{
		Set<String> selection = _multiSelection;
		_multiSelection = null;
		return selection;
	}
	
	/**
	 * Generate JavaScript to configure the current state of the control
	 * <p>
	 * This method supports the following control states and changes between those states:
	 * - The entire control is either in its proper position on the screen or is "Stored"
	 *   off the top of the screen
	 * - There is a model file for the current bound value and the active X control is 
	 *   displayed, or there is not and it is hidden and a message is displayed
	 * - If it is a partial refresh and the HTML does not need to be rerendered, then this
	 *   code is executed inside a script tag in the hidden frame.  IF it is run as part 
	 *   of rendering the control then it is run wint the control HTML in the MAINDOC
	 * - If it is a partial refresh, it may update the selected value, the both the model file
	 *   and the selected value, or post a message to the status line
	 */
	public String jspScript(
		String id
	) 
		throws RemoteException, 
		       MXException
	{
		StringBuffer script = new StringBuffer();
		script.append( "" );
		
	    boolean designmode = _wcs.getDesignmode();

		String containerTable   = id + "container";

		boolean needsRendered = needsRender();

		//If the code is run in the hidden frame, then calls need to be prefixed with
		// MAINDOC this only applies to pre 7.5 versions
		String doc = "";
		if(    !needsRendered 
			&& getMxVersion() < VERSION_75_OR_GREATER )
		{
			doc = "MAINDOC.";
		}

		String value   = null;
		
		// Set the top value
		script.append( "try {" );
		script.append( "var containerTbl = " + doc + "document.getElementById( \"" );
		script.append( containerTable );
		script.append( "\" ); " );
		script.append( "if( containerTbl != undefined ) { " );
			script.append( "containerTbl.style.top = \"" + jspGetViewerTop() + "px\";" );
			script.append( "containerTbl.style.left = \"" + _leftOffset + "px\";" );
		script.append( "}" );
		
		script.append( "var isLoaded = true; " );
		script.append( "AF = window.frames." + id + "_frame; " );
		script.append( "var frame = window.top.frames." + id + "_frame; " );
		script.append( "if( frame != undefined && frame.contentWindow != undefined ) { " );
			script.append( "frame = frame.contentWindow;" );	// Chrome
		script.append( "} " );
		script.append( "if( frame == undefined || frame.setModelVisibility == undefined ) { " );
			script.append( "isLoaded = false; " );
		script.append( "} " );

		// The controls has two forms:  
		// - A message indicating there is no model file
		// - The actual control
		// The correct form is selected based on wether the current location has a model file
		//
		// If the control is alredy displayed, then it may be necessary to select between
		// The message window and the control window. Its simpler to always set these values
		// The to track the current state
		boolean showModel = false;

		script.append( scriptResize() );

		if( isControlVisible() )
		{
			showModel = itemHasModel();
			value = getValue();
			script.append( "if( isLoaded ) { " );
				script.append( "frame.setModelVisibility( " );
				script.append( showModel );
				script.append( " ); " );
			script.append( "} " );
		}

		//View tab is selected so the control should be visible on the page
		if( isControlVisible() && showModel && !designmode )
		{
			boolean hasScript = false;
			if( jspHasStatusUpdate() )
			{
				script.append( "frame.setStatus( \"" );
				script.append( jspGetStatusUpdate().trim() );
				script.append( "\" ); " );
				hasScript = true;
			}


			if( isHasMultiSelect() )
			{
				Set<String> selection = jspGetMultiSelection();
				Iterator<String> itr = selection.iterator();
				script.append( "var selection = new Array(); " );
				while( itr.hasNext() )
				{
					script.append( "selection[ selection.length ] = \"" );
					script.append( itr.next() );
					script.append( "\"; " );
				}
				script.append( "frame.multiSelect( selection, \"" );
				script.append( value );
				script.append( "\", " );
				if( _multeSelectZoomToContext )
				{
					script.append( "true" );
				}
				else
				{
					script.append( "false" );
				}
				script.append( " ); " );
				
				_multeSelectZoomToContext = false;
				hasScript = true;
			}

			if( hasScript )
			{
				script.append( "} catch( e ) { console.log( \"BIMViewer error\\\n\" ); console.log( e ); }" );
				return script.toString();
			}

			// If the model file has changed due to a change in the value bound to
			// the control, or the control is being rerendered, then the models
			// must be reloaded into the control
			if( isModelListChanged() || needsRendered )
			{
				script.append( "if( isLoaded ) { " );
					script.append( "var mm = frame.initModelManager(); " );
					script.append( "if( mm != null ) { " );
						script.append( "mm.resetModelList(); " );
						script.append( jspGetScriptModelList() );
						script.append( "mm.populateModelList( \"" );
						script.append( value );
						script.append( "\" ); " );
					script.append( "} " );
				script.append( "} " );
			}
			else if(  (  isValueChanged() || needsRendered ) 
					 &&  getAppType() !=  BIMViewer.TYPE_LOOKUP )	// popup doesn't need to be bound to a value
			{
				script.append( "if( isLoaded ) { " );
				script.append( "frame.select( \"" );
				script.append( value );
				script.append( "\" ); " );
				script.append( "} " );
				setValueChanged( false );
			}
		} // close if( isControlVisible() )
		
		if( needsRendered  && getAppType() != TYPE_LOOKUP )
		{
			// Stack<AppInstance> appStack = _wcs.getAppStack(); Might sometime fixe return from app
			if( !getViewerType().equals( "lmv" ))
			{
				script.append( " rehideObjs();" );
			}
		}
		script.append( "} catch( e ) { console.log( e ); }" );
		
		return script.toString();
	}
	
	public String scriptResize()
	{
		StringBuffer resize = new StringBuffer();
		resize.append( "" );
		if( !isControlVisible() )
		{
			resize.append( "if( isLoaded ) { frame.resize( \"-1\" ); }" );
			return resize.toString();
		}

		int opt;
		if( getAppType() ==  BIMViewer.TYPE_LOOKUP  )
		{
			resize.append( "if( isLoaded ) { frame.resizeDlg( \"" );
			opt = jspGetRezieDlgOption();
		}
		else
		{
			resize.append( "if( isLoaded ) { frame.resize( \"" );
			opt = jspGetRezieOption();
		}
		
		resize.append( opt );
		resize.append( "\" ); } " );
		return resize.toString();
	}
	
	/**
	 * Generate JavaScript to add all the models in _currentModelList
	 * to the ComboBox specified by listCtrlId
	 * @param listCtrlId
	 * @return JavaScript fragment
	 */
	public String jspGetScriptModelList()
	{
		StringBuffer script = new StringBuffer();
	
		for( int i = 0; i < _currentModelList.size(); i++ )
		{
			BIMModelSpec modelSpec = _currentModelList.get( i );
			
			script.append( "mm.addModel(" );
			script.append( modelSpec.getModelId() );
			script.append( ", \"" );
			script.append( modelSpec.getLocation() );
			script.append( "\", \"" );
			script.append( modelSpec.getBinding() );
			script.append( "\", \"" );
			if( modelSpec.getTitle() != null && modelSpec.getTitle().length() > 0  )
			{
				script.append( modelSpec.getTitle() );
			}
			else if( modelSpec.getDescription() != null && modelSpec.getDescription().length() > 0  )
			{
				script.append( modelSpec.getDescription() );
			}
			else
			{
				script.append( modelSpec.getLocationName() );
			}
			script.append( "\", \"" );
			script.append( modelSpec.getModelURL() );
			script.append( "\", \"" );
			script.append( modelSpec.getAttribClass() );
			script.append( "\", \"" );
			script.append( modelSpec.getAttribName() );
			script.append( "\", \"" );
			script.append( modelSpec.getParamClass() );
			script.append( "\", \"" );
			script.append( modelSpec.getParamName() );
			script.append( "\", \"" );
			if( _type == TYPE_ASSET )
			{
				script.append( modelSpec.getAssetView() );
			}
			else if( _type == TYPE_LOCATION )
			{
				script.append( modelSpec.getLocationView() );
			}
			else if( _type == TYPE_LOOKUP )
			{
				script.append( modelSpec.getLookupView() );
			}
			else if( _type == TYPE_WORKORDER )
			{
				script.append( modelSpec.getWorkOrderView() );
			}
			script.append( "\", \"" );
			script.append( modelSpec.getSelectionMode() );
			script.append( "\", \"" );
			script.append( modelSpec.getSiteId() );
			String mboKey = getMboKey();
			if( mboKey != null && mboKey.length() > 0 )
			{
				script.append( "\", \"" );
				script.append( mboKey );
			}
			script.append( "\" ); " );
		}

		_hasModelListChanged = false;
		String tmp = script.toString();
		return tmp;
	}

	public String scriptFooter()
	{
		StringBuffer footer = new StringBuffer();
		footer.append( "" );
		return footer.toString();
	}

	/**
	 * Retrieves a stored resize option from the HTTP session and makes it 
	 * available to the .jsp.  This version is used when the viewer is
	 * displayed on an application main tab
	 * @return
	 */
	public int jspGetRezieOption()
	{
		HttpServletRequest thisRequest = _wcs.getRequest();
	    HttpSession session = thisRequest.getSession();
	    Object o = session.getAttribute( ATTRIB_RESIZE );
	    if( o == null || !(o instanceof String) )
	    {
	    	return 0;
	    }
	    String option = (String)o;
	    if( option.length() == 0 )
	    {
	    	return 0;
	    }
	    return Integer.parseInt( option );
	}
	
	/**
	 * Retrieves a stored resize option from the HTTP session and makes it 
	 * available to the .jsp.  This version is used when the viewer is
	 * displayed on an dialog
	 * @return
	 */
	public int jspGetRezieDlgOption()
	{
		HttpServletRequest thisRequest = _wcs.getRequest();
	    HttpSession session = thisRequest.getSession();
	    Object o = session.getAttribute( ATTRIB_RESIZE_DLG );
	    if( o == null || !(o instanceof String) )
	    {
	    	return 0;
	    }
	    String option = (String)o;
	    if( option.length() == 0 )
	    {
	    	return 0;
	    }
	    return Integer.parseInt( option );
	}
	

	
	/**
	 * Provides an update to the control status line and resets
	 * hasStatusUpdate to false
	 * @return Test of status update
	 */
	public String jspGetStatusUpdate()
	{
		if( _statusUpdate == null ) return "";		// Shouldm't be called in this situation
		String status = _statusUpdate;
		_statusUpdate = null;
		return status;
	}
	
	
	/**
	 * If the control is displayed on a tab of an application, its really not on the tab,
	 * it at the bottom of the page below the tab set.  This prevents reloading of the
	 * control and of control data as the use moved between tabs.  To cause it to 
	 * display correctly, the control class tracks which tab is visible and if the 
	 * control should be displayed.  If it should, top is set to a value on the tab
	 * if it shouldn't, top is set to a large negative value
	 * @return
	 */
	public int jspGetViewerTop()
	{
		//Is the control on the screen or in "Storage"?
		if( isControlVisible() )
		{
			return _topOffset;
		}
		return -5000;
	}

	/**
	 * Indicates if there is a status message to display on the control
	 * Status line
	 * @return True if there is a status message
	 */
	public boolean jspHasStatusUpdate()
	{
		return _statusUpdate != null;
	}
	
	//******************************************************************************
	
	/**
	 * Allows the .jsp to to application type specific processing 
	 * including setting appopiate lable text
	 * @return The type of the applcation the controls is supporting
	 */
	public int getAppType() { return _type; }
	
	public String getBackgroundColor()
	{
		String color = getProperty("background_color");
		if( color != null && color.length() > 0 )
		{
			return color;
		}
		String skin = _wcs.getSkin();
		if( skin.contains( "tivoli09" ) || skin.contains( "tivoli13" ) )
		{
			return "#FFFFFF";
		}
		return "#000000";
	}

	public String getBoarderColor()
	{
		String color = getProperty("boarder_color");
		if( color != null && color.length() > 0 )
		{
			return color;
		}
		String skin = _wcs.getSkin();
		if( skin.contains( "tivoli09" ) || skin.contains( "tivoli13" ) )
		{
			return "#E0E0E0";
		}
		return "#808080";
	}

	public String getForegroundColor()
	{
		String color = getProperty("foreground_color");
		if( color != null && color.length() > 0 )
		{
			return color;
		}
		String skin = _wcs.getSkin();
		if( skin.contains( "tivoli09" ) || skin.contains( "tivoli13" ) )
		{
			return "#000000";
		}
		return "#FFFFFF";
	}

	public String getHighlightColor()
	{
		String color = getProperty("highlight_color");
		if( color != null && color.length() > 0 )
		{
			return color;
		}
		String skin = _wcs.getSkin();
		if( skin.contains( "tivoli09" ) || skin.contains( "tivoli13" ) )
		{
			return "#0404B0";
		}
		return "#40B040";
	}

	public String getBinding()
	{
		return _binding;
	}
	
	public Set<String> getCurrentSelection()
	{
		return _currentSelection;
	}
	
	public int getHeight()
	{
		return _height;
	}

	/**
	 * Get the key value for the bound Mbo: Location, assetnum, wonum
	 */
	public String getMboKey()
	{
		switch( _type )
		{
		case TYPE_ASSET:
			return dataBean.getString( FIELD_ASSETNUM );
		case TYPE_LOCATION:
			return dataBean.getString( FIELD_LOCATION );
		case TYPE_WORKORDER:
			AppInstance app = getWebClientSession().getCurrentApp();
			DataBean db = app.getAppBean();
			return db.getString( FIELD_WO_NUM );
		case TYPE_MODEL:
			return dataBean.getString( FIELD_BUILDINGMODELID );
		case TYPE_LOOKUP:
			return "";
		}
		return "";
	}
	
	public int getLeftOffset()
	{
		return _leftOffset;
	}
	
	public String getLookupValue()
	{
		return _lookupValue;
	}

	public int getMxVersion()
	{
		return _mxVersion;
	}
	
	/**
	 * Allows the .jsp to do application type specific processing 
	 * including setting appropriate label text
	 * @return The type of the application the controls is supporting
	 */
	public int getRecordType() { return _recordType; }

	public String getSiteId()
	{
		return dataBean.getString( FIELD_SITEID ); 
	}
	
	public String getValue( )
	{
		String value = getString();
		if( value == null ) value = "";
		
		if( !value.equalsIgnoreCase( _currentValue ))
		{
			_hasValueChanged = true;
		}
		_currentValue = value;
		String modelId = value;
		if( !_binding.equalsIgnoreCase(  FIELD_MODELID ) )
		{
			modelId = dataBean.getString( FIELD_MODELID );
		}
		return modelId;
	}
	
	public String getViewerType()
	{
		try
		{
     		MXServer server = MXServer.getMXServer();
    		String activeViewer = server.getProperty( BIMService.PROP_NAME_ACTIVE_VIEWER );
    		if( activeViewer != null && activeViewer.length() > 0 )
    		{
    			return activeViewer;
    		}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return "navisworks";
	}

	public String getWidth()
	{
		return _width;
	}
	
	public boolean isForceUpdate()
	{
		return _forceUpdate;
	}

	public boolean isHasMultiSelect()
	{
		return _multiSelection != null;
	}
	
	/**
	 * <p>
	 * Searches up the location hierarchy using the parent attribute of each location
	 * looking for a building model file.
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	private boolean itemHasModel()
		throws RemoteException, 
		       MXException 
    {
		Vector<BIMModelSpec> modelList;
		if( getRecordType() == RECORD_MODEL )
		{
			BIMModelSpec modelSpec;
			modelList = new Vector<BIMModelSpec>();
			modelSpec = new BIMModelSpec( dataBean );
			if( modelSpec.getModelURL() != null && modelSpec.getModelURL().length() > 0 )
			{
				modelList.add( modelSpec );
			}
		}
		else
		{
			// call to getValue() sets the isValueChanged flag
			getValue();
			if( !isValueChanged() && !_forceUpdate )
			{
				if( _currentModelList == null )     return false;
				if( _currentModelList.size() == 0 ) return false;
				return true;
			}
			_forceUpdate = false;
			
			LocationRemote location = lookupLeafLocation();
			
			if( location == null ) return false;
			
			modelList = new Vector<BIMModelSpec>();
			String field = _binding;
			if( _recordType == RECORD_ASSET )
			{
				field = _modelId;
			}
			
			MboSetRemote modelSet = lookupModelFileForLocation( location );
			int count = 0;
			try
			{
				count = modelSet.count();
			}
			catch( Exception e )
			{ /* Ignor.  May happen if the bim.activ.viewer sys prop is incorrectly set */ }
			for( int i = 0; modelSet != null && i < count; i++ )
			{
				MboRemote model = modelSet.getMbo( i );
				location = lookupLocation( model.getString( FIELD_LOCATION) );
				if( model != null )
				{
			        String    locationName = location.getString( FIELD_LOCATION );
			        String    locationId   = location.getString( FIELD_LOCATION );
			        String    binding      = location.getString( field );

//			        BIMModelSpec modelSpec = new BIMModelSpec();
			        BIMModelSpec modelSpec = new BIMModelSpec( locationName, locationId, binding, model );
					if( modelSpec.getModelURL() != null && modelSpec.getModelURL().length() > 0 )
					{
						modelList.add( modelSpec );
					}
				}
			}
		}
		
		BIMModelSpec modelSpec;
		if( modelList.size() > 0 )
		{
			modelSpec = modelList.get( 0 );
		}
		if( !compareModelLists( _currentModelList, modelList ) )
		{
			_hasModelListChanged = true;
			_modelLocation = null;
			_currentSelection = new HashSet<String>();
			_currentSelection.add( dataBean.getString( FIELD_MODELID ) );
			if( !modelList.isEmpty() )
			{
				modelSpec = modelList.elementAt( 0 ); 
				_wcs.queueEvent(new WebClientEvent("bimModelListChanged", getId(), modelSpec.getLocation(), _wcs));
			}
		}
		_currentModelList = modelList;
		if( modelList.size() > 0 ) return true;
		return false;
	}
	
	/**
	 * Track if the list of models in the location hierarchy from the
	 * current asset of location up to the building has changed
	 * @return
	 */
	public boolean isModelListChanged() 
	{ 
		return _hasModelListChanged; 
	}
	
	/**
	 * Determines if the user is allowed to select more than one item at a time
	 * @return 
	 */
	public boolean isMultiSelectAllowed()
	{
		return _isMultiSelectAllowed;
	}
	
	public boolean isSelectionValid()
	{
		return _isSelectionValid;
	}

	public void forceUpdate()
	{
		_forceUpdate = true;
	}
	
	public void setModelListChanged( boolean state )
	{
		_hasModelListChanged = state;
	}
	
	private void setNotFoundStatus()
	{
		if( _recordType == RECORD_ASSET )
		{
			_statusUpdate = getProperty( "msg_not_asset" );
		}
		else if( _recordType == RECORD_LOCATION )
		{
			_statusUpdate = getProperty( "msg_not_location" );
		}
		else
		{
			return;			// No message needed
		}
		setChangedFlag();
	}
	
	/**
	 * Track if the selected value is different form the currently
	 * selected value to allow optimization
	 */
	public boolean isValueChanged() { return _hasValueChanged; }
	public void setValueChanged( boolean state ) 
	{ 
		_hasValueChanged = state; 
	}
	
	
	public boolean isControlVisible() { return _controlVisible; }
	public void setControlVisible(boolean vis)
	{
		_controlVisible = vis;
	}
	
	/**
	 * 
	 * @param selection	A set of items using the value bound to the control to display as the 
	 *        current selection in the control
	 *        
	 *        Called from dialogs that use the model as a display mechanism
	 * @throws RemoteException
	 * @throws MXException
	 */
	public void setMultiSelect( 
		String      modelLocation,
		Set<String> selection 
	) 
		throws RemoteException, 
			   MXException
	{
		setChangedFlag();
		if( selection.size() > 0 )
		{
			Iterator<String> itr = selection.iterator();
			_modelLocation = modelLocation;
			while( itr.hasNext() )
			{
				String sel = itr.next();
				if( setCurrentSelection( sel ))
				{
					break;
				}
			}
		}

		// _multiSelect is set to null as soon as the .jsp reads it.  _currentSelection
		// persists until changed by a new selection
		_currentSelection = selection;
		_multiSelection   = selection;
		_multeSelectZoomToContext = true;
	}
	
	//**************************************************************************
	// This section has various query functions call both from this class and
	// from dialogs launched from this class
	//**************************************************************************
	/**
	 * Gets the starting location for search for a model file.  If this is an asset
	 * the the location is the location attribute of the asset.  If this is a location
	 * the the location is itself
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	protected Location lookupLeafLocation() 
		throws RemoteException, 
		       MXException
	{
		MboRemote mbo = null;
		if( getRecordType() == BIMViewer.RECORD_ASSET )
		{
			String locName = getDataBean().getString( FIELD_LOCATION );
			if( locName == null || locName.length() == 0 ) return null;
			mbo = lookupLocation( locName );
			if( mbo != null && mbo instanceof Location )
			{
				return (psdi.app.location.Location)mbo;
			}
			return null;
		}
		else if( getRecordType() == RECORD_LOCATION )
		{
			mbo = getDataBean().getMbo();
			if(  mbo != null && mbo instanceof Location )
			{
				return (psdi.app.location.Location)mbo;
			}
			return null;
		}
		return null;
	}
	
	protected MboSetRemote lookupModelFileForLocation(
		LocationRemote locMbo
	) {
		try
		{
	   		String siteId   = locMbo.getString( FIELD_SITEID );
	   		String orgId    = locMbo.getString( FIELD_ORGID );
	   		String systemId = locMbo.getString( FIELD_SYSTEMID );
	   		String location = locMbo.getString( FIELD_LOCATION );
	   		String viewerTypeList = _modelSet.getVieweerTypeList( siteId, orgId );
	   		
	   		if( systemId == null || systemId.length() == 0 )
	   		{
	   			SqlFormat sqlPrimarySystem = new SqlFormat(locMbo, "siteid=:1 AND PRIMARYSYSTEM=1");
	   			sqlPrimarySystem.setObject( 1, "LOCSYSTEM", "SITEID", siteId );
	   			MboSetRemote primarySystemSet = locMbo.getMboSet("$getSystems", "LOCSYSTEM", sqlPrimarySystem.format());
	   			String[] params = { location, siteId };
	   			if( primarySystemSet.isEmpty() )
	   			{
	   				throw new MXApplicationException( Constants.BUNDLE_MSG, 
	   				                                  Constants.ERR_NO_PRIMARY_SYS, params );	
	   			}
	   			MboRemote ancestorMbo = primarySystemSet.getMbo(0);
	   			systemId = ancestorMbo.getString("SYSTEMID");
	   		}
	   		
	   		StringBuffer query = new StringBuffer( "( " );
	   			query.append( QUERY_MODEL_FILE );
	   		query.append( " )" );
	   		query.append( " AND " );
	   		query.append( " ( " );
		   		query.append( BuildingModel.FIELD_VIEWERTYPE );
		   		query.append( " IN ( ");
			   		query.append( viewerTypeList );
		   		query.append( " )" );
		   		
		   		// To support legacy installations, treat all models that don't have a value for viewerType as NavisWorks
		   		if( _activeViewer.equals(  "navisworks" ))
		   		{
			   		query.append( "OR (" );
				   		query.append( BuildingModel.FIELD_VIEWERTYPE );
				   		query.append( " IS NULL" );
			   		query.append( " )" );
		   		}
	   		
	   		query.append( ")" );
	   		
	   		SqlFormat sqlf;
			sqlf = new SqlFormat( query.toString() );
			sqlf.setObject(1, TABLE_LOCATIONS, FIELD_LOCATION, location );
			sqlf.setObject(2, TABLE_LOCATIONS, FIELD_SITEID, siteId ) ; 
			sqlf.setObject(3, TABLE_LOCATIONS, FIELD_SYSTEMID, systemId ) ; 
	        _modelSet.setWhere( sqlf.format() );
	        _modelSet.setOrderBy( BuildingModel.FIELD_PRIORITY + " DESC" );
	        _modelSet.reset();
			return _modelSet;
		}
		catch( Exception e )
		{
			e.printStackTrace( System.err );
			return null;
		}
	}


	/**
	 * Queries the database for a location record based on location and siteid attributes
	 * @param location
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	public LocationRemote lookupLocation(
		String location
	) 
		throws RemoteException, 
		       MXException 
	{
   		String siteId  = dataBean.getString( FIELD_SITEID );
   		return BIMViewer.lookupLocation( dataBean.getMbo(), location, siteId );
	}
	
	/**
	 * Use the value of an item in the model to find the Maximo location for that
	 * item using the current dataattibute binding of the control
	 * 
	 * Used by dialogs that include or are launched by the control
	 * @param value The value of the datafield bound to the control.  Usually is modelid
	 * @return 
	 * @throws RemoteException
	 * @throws MXException
	 */
	public LocationRemote lookupLocationFromModelId(
		String modelId
	) 
		throws RemoteException, 
			   MXException 
	{
		String siteId = dataBean.getString( FIELD_SITEID );
		return lookupLocationFromModelId( _modelLocation, modelId, siteId );
	}
	
	public LocationRemote lookupLocationFromModelId(
		String modelLocation,
		String modelId
	) 
		throws RemoteException, 
			   MXException 
	{
		String siteId = dataBean.getString( FIELD_SITEID );
		return lookupLocationFromModelId( modelLocation, modelId, siteId );
	}
	
	/**
	 * Get the location from the bound value if the control is bound to either location of asset
	 * @param location
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	public LocationRemote lookupLocationFromModelId(
		String modelLocation,
		String modelId,
		String siteId
	) 
		throws RemoteException, 
			   MXException 
	{
		if( modelLocation == null )
		{
			return null;
		}
		
		String field = _binding;
		if( _recordType == RECORD_ASSET ) field = _modelId;

		try
		{
			SqlFormat sqlf = new SqlFormat( dataBean.getMbo(), field + QUERY_LOC_MODELID );
			sqlf.setObject(1, TABLE_LOCATIONS,_modelId, modelId );
			sqlf.setObject(2, TABLE_LOCATIONS, FIELD_LOCATION, modelLocation );
			sqlf.setObject(3, TABLE_LOCATIONS, FIELD_SITEID, siteId ) ; 
			MboSetRemote locSet = dataBean.getMbo().getMboSet("$getLocations", TABLE_LOCATIONS, sqlf.format());
			if( locSet.isEmpty() )	return null;
			if( locSet.count() > 1 )
			{
				// TO DO issue error
			}
			return (LocationRemote)locSet.getMbo( 0 );
		}
		catch( Exception e )
		{
			return null;
		}
	}
	
	/**
	 * Looks up a location based on the datasrc for the control (Usually modelId)
	 * @param location
	 * @param siteId
	 * @return The location field of the location associated with the imput value, or null
	 * @throws RemoteException
	 * @throws MXException
	 */
	public String lookupLocationModelId(
		String location,
		String siteId
	) 
		throws RemoteException, 
			   MXException 
	{
		if( _binding.equalsIgnoreCase( FIELD_LOCATION ))
		{
			return location;
		}
		String field = _binding;
		if( _type == TYPE_ASSET ) field = _modelId;

		MboRemote locMbo = BIMViewer.lookupLocation( dataBean.getMbo(), location, siteId );
		if( locMbo == null ) return null;
		return locMbo.getString( field );
	}
	
	/**
	 * SQL Lookup of the location associated with a work order
	 * @param woKey
	 * @param siteId
	 * @return
	 */
	public String lookupLocationFromWO(
		String woKey,
		String siteId
	) {
		MboRemote mbo;
        try
        {
	        mbo = dataBean.getMbo();
			SqlFormat sqlf = new SqlFormat( mbo, FIELD_WO_NUM + "=:1 and siteid=:2");
			sqlf.setObject(1, TABLE_WORKORDER, FIELD_WO_NUM, woKey );
			sqlf.setObject(2, TABLE_WORKORDER, FIELD_SITEID, siteId ) ; 
			MboSetRemote resultSet = mbo.getMboSet("$getWorkOrder", TABLE_WORKORDER, sqlf.format());
			if( resultSet.isEmpty())
			{
				return null;
			}
			MboRemote resultMbo = resultSet.getMbo(0);	
			String location = resultMbo.getString( FIELD_LOCATION );
			
			// Figure out which field is bound to the model and return the value fo it
			String field = _binding;
			if( _type == TYPE_ASSET ) field = _modelId;
			if( field.equalsIgnoreCase( FIELD_LOCATION ))
			{
				return location;
			}
			resultMbo = BIMViewer.lookupLocation( mbo, location, siteId );
			return resultMbo.getString( field );
        }
        catch( Exception e )
        {
        	return null;
        }
	}

	/**
	 * Get a building model definition associated with a location if one exists
	 * @param location
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	protected MboSetRemote lookupModelsForLocation(
		String location
	) 
		throws RemoteException, 
			   MXException 
	{
		MboRemote mbo     = dataBean.getMbo();
		String siteId  = dataBean.getString( FIELD_SITEID );

		SqlFormat sqlf = new SqlFormat( mbo, "location=:1 and siteid=:2");
		sqlf.setObject(1, BIMModelSpec.TABLE_BUILDINGMODEL, FIELD_LOCATION, location );
		sqlf.setObject(2, BIMModelSpec.TABLE_BUILDINGMODEL, FIELD_SITEID, siteId ) ; 
		return mbo.getMboSet("$getBuildingModel", BIMModelSpec.TABLE_BUILDINGMODEL, sqlf.format());
	}
	
	/**
	 * <p>Get the UID for the Maximo record associated with the modelId<p>
	 * Model IDs always refer t locations so if the control is bound to asset
	 * The asset at the location must be queried.  It is assumed the operating locations
	 * are used and therefore there is no more than one asset at a location
	 * @param locationId	The location for the model.  Used to disambiguate the modelId
	 * @param modelId
	 * @param siteId
	 * @return Unique identifier for location or asset associated with a model ID 
	 * @throws MXException 
	 * @throws RemoteException 
	 */
	protected long lookupUid(
		String modelId
	) 
		throws RemoteException, 
		       MXException 
	{
		if( _modelLocation == null )
		{
			return -1;
		}
		if( _recordType != RECORD_LOCATION &&  _recordType != RECORD_ASSET  )
		{
			return -1;
		}
		
		String siteId = dataBean.getString( FIELD_SITEID );
		MboRemote mbo     = dataBean.getMbo();
		SqlFormat sqlf = new SqlFormat( mbo, _modelId + QUERY_LOC_MODELID );
		sqlf.setObject(1, TABLE_LOCATIONS,_modelId, modelId );
		sqlf.setObject(2, TABLE_LOCATIONS, FIELD_LOCATION, _modelLocation );
		sqlf.setObject(3, TABLE_LOCATIONS, FIELD_SITEID, siteId ) ; 
		String query = sqlf.format();
		MboSetRemote resultSet = mbo.getMboSet("$getLocationsSet", TABLE_LOCATIONS, query );
		if( _recordType == RECORD_LOCATION )
		{
			if( !resultSet.isEmpty())
			{
				MboRemote resultMbo = resultSet.getMbo(0);	
				return resultMbo.getLong( "LOCATIONSID" );
			}
			return -1;
			
			/*
			// If the location is not found, the modelId might be for an asset.  Try to find the
			// Asset then return the location for the asset
			 * Because the viewer cycles the new selection back as a selection event the following code
			 * make it impossible to select an asset and there for to view the model properties
			 * There ought to be an alternate selection mode and event such as ctrl select to enable
			 * this path.
			resultSet = mbo.getMboSet("$getAssetSet", TABLE_ASSET, query );
			if( resultSet.isEmpty())
			{
				return -1;
			}

			MboRemote resultMbo = resultSet.getMbo(0);	
			if( resultSet.isEmpty())
			{
				return -1;
			}
			resultMbo = resultSet.getMbo(0);	
			String location = resultMbo.getString(FIELD_LOCATION);
			resultMbo = BIMViewer.lookupLocation(mbo, location, siteId);
			if( resultMbo == null )
			{
				return -1;
			}
			return resultMbo.getLong( "LOCATIONSID" );
			*/
		}
		
		boolean matchLocation = true;
		// Record type is ASSET
		if( resultSet.isEmpty())
		{
			resultSet = mbo.getMboSet("$getAssetSet", TABLE_ASSET, query );
			matchLocation = false;
		}
		if( resultSet.isEmpty())
		{
			return -1;
		}

		MboRemote resultMbo = resultSet.getMbo(0);	
		
		if( matchLocation )
		{
	 		resultSet = BIMViewer.lookupAssetsAtLocation( resultMbo );
			if( resultSet.isEmpty())
			{
				return -1;
			}
		}
		resultMbo = resultSet.getMbo(0);	
		return resultMbo.getLong( "ASSETUID" );
	}
	
	/**
	 * Compare two lists of model specification
	 * @param list1
	 * @param list2
	 * @return true if the list are identical
	 */
	private boolean compareModelLists(
		Vector<BIMModelSpec> list1,
		Vector<BIMModelSpec> list2
	) {
		if( list1 == null && list2 == null ) return true;
		if( list1 == null || list2 == null ) return false;
		if( list1.size() != list2.size() ) return false;
		for( int i = 0; i < list1.size(); i++ )
		{
			if( !list1.get(i).equals( list2.get(i) )) return false;
		}
		return true;
	}
	
	static public BaseInstance findByRenderId(
		BaseInstance root,
		String       renderId
	) {
		String altRenderId = "NO MATCH";
		int idx = renderId.indexOf( '_' );
		if( idx > 0 )
		{
			altRenderId = renderId.replaceFirst( "_", "-" );;
//			renderId = renderId.substring( 0, idx );
		}
		if( root == null ) return root;       
		if( root.getRenderId().equals( renderId ) || root.getRenderId().equals( altRenderId ))				
		{
			return root;  
		}
	
		List<?> l;
		if( root instanceof ControlInstance )
		{
			l = ((ControlInstance)root).getComponents();
		}
		else
		{
			l = root.getChildren();
		}
		Iterator<?> itr = l.iterator();
		while( itr.hasNext() )
		{
			Object o = itr.next();
			if( !(o instanceof BaseInstance )) continue;
			BaseInstance bi = (BaseInstance)o;
			if( bi.getRenderId().equals( renderId ) || bi.getRenderId().equals( altRenderId ))				
			{
				return bi;
			}
			bi = findByRenderId( bi, renderId );
			if( bi != null ) return bi;
		}  
		return null;
	} 
	
	/**
	 * Queries the database for a location record based on location and siteid attributes
	 * @param location
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	public static LocationSetRemote lookupLocations(
		MboRemote mbo,
		String    location,
		String    siteId
	) 
		throws RemoteException, 
		       MXException 
	{
		if( mbo == null )
		{
			return null;
		}
		
		SqlFormat sqlf = new SqlFormat( mbo, FIELD_LOCATION + "=:1 and siteid=:2");
		sqlf.setObject(1, TABLE_LOCATIONS, FIELD_LOCATION, location );
		sqlf.setObject(2, TABLE_LOCATIONS, FIELD_SITEID, siteId ) ; 
		return (LocationSetRemote)mbo.getMboSet("$getLocations", TABLE_LOCATIONS, sqlf.format());
	}
	
	/**
	 * Queries the database for a location record based on location and siteid attributes
	 * @param location
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	public static LocationRemote lookupLocation(
		MboRemote mbo,
		String    location,
		String    siteId
	) 
		throws RemoteException, 
		       MXException 
	{
		MboSetRemote locationSet = lookupLocations( mbo, location, siteId );
		if( locationSet.isEmpty())
		{
			return null;
		}
		return (LocationRemote)locationSet.getMbo(0);	
	}
	
	/**
	 * Get all the assets at the specified location
	 * @param locationMbo
	 * @return null or a set of assets
	 */
	public static MboSetRemote lookupAssetsAtLocation(
		MboRemote locationMbo
	) {
		if( locationMbo == null ) return null;

		try
		{
			String location = locationMbo.getString( FIELD_LOCATION );
			String siteId = locationMbo.getString( FIELD_SITEID );

			SqlFormat sqlf = new SqlFormat( locationMbo, FIELD_LOCATION + "=:1 and siteid=:2");
			sqlf.setObject(1, TABLE_ASSET, FIELD_LOCATION, location );
			sqlf.setObject(2, TABLE_ASSET, FIELD_SITEID, siteId ) ; 
			MboSetRemote assetSet = locationMbo.getMboSet("$getAssetSet", TABLE_ASSET, sqlf.format());
			return assetSet;
		}
		catch( Exception e )
		{
			return null;
		}
	}
	
	/**
	 * Holds the definition of a building model
	 * @author Doug
	 *
	 */
}