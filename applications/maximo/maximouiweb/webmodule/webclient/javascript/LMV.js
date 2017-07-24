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
"use strict";

/**
 *****************************************************************************************************
 *
 * LMV Name Space
 * 
 *****************************************************************************************************
 */
IBM.createNS( "IBM.LMV" );

IBM.LMV.PATH_IMAGES       = 'LMV/mages/';

IBM.LMV.toolBar           = null;
IBM.LMV.dataDictionary    = null;
IBM.LMV.markupMgr         = null;

if( !IBM.LMV.Strings  ) IBM.LMV.Strings = {};

/**
  * The values in the string table are over written by consuming applcations with values for the correct locale
  */
IBM.LMV.Strings[ "TOOLBAR_HIDE_SELECTION" ]   = "Hide selection";
IBM.LMV.Strings[ "TOOLBAR_ISOLATE_SELECTION" ]= "Isolate Selection";
IBM.LMV.Strings[ "TOOLBAR_SEARCH" ]           = "Search";
IBM.LMV.Strings[ "TOOLBAR_SELECTION_MODE" ]   = "Set selection mode";
IBM.LMV.Strings[ "TOOLBAR_ZOOM_MODEL" ]       = "Zoom to model";

IBM.LMV.Strings[ "DLG_TT_SAVE_VIEW" ]         = "Save View...";
IBM.LMV.Strings[ "DLG_TT_APPLY_VIEW" ]        = "Apply Saved View...";
IBM.LMV.Strings[ "DLG_LABEL_SAVE_VIEW" ]      = "Save View";
IBM.LMV.Strings[ "DLG_LABEL_APPLY_VIEW" ]     = "Apply Saved View";
IBM.LMV.Strings[ "DLG_TXT_DELETE_VIEW" ]      = "Delete saved view";
IBM.LMV.Strings[ "DLG_TITLE_SAVE_VIEW" ]      = "Save Current View";
IBM.LMV.Strings[ "DLG_TITLE_APPLY_VIEW" ]     = "Select View to Restore";
IBM.LMV.Strings[ "DLG_BTN_DELETE_VIEW" ]      = "Delete Saved View";     

IBM.LMV.Strings[ "MSG_TITLE_REQUIRED_FIELD" ] = "Required Field Missing %1";
IBM.LMV.Strings[ "ERR_NO_GUID" ]              = "The selected item doesn't have a unique identifier.  It may be necessary to define a binding in the Manage Building Models application";
IBM.LMV.Strings[ "ERR_REST" ]                 = "REST Error";  

IBM.LMV.Strings[ "KEY_ATTACHMENT" ]           = "Attachments for {0}";
IBM.LMV.Strings[ "KEY_CLASS_STRUCT" ]         = "Hierarchy Path";
IBM.LMV.Strings[ "KEY_PROP_LOADIN_MSG" ]      = "Loading properties...";
IBM.LMV.Strings[ "KEY_PREV_PAGE" ]            = "Previous Page";
IBM.LMV.Strings[ "KEY_DISABLE_AUTO_ZOOM" ]    = "Disable Auto Zoom";  
IBM.LMV.Strings[ "KEY_ENABLE_AUTO_ZOOM" ]     = "Enable Auto Zoom";  
IBM.LMV.Strings[ "KEY_PARENT" ]               = "Parent";  

IBM.LMV.Strings[ "KEY_ASSET_PROP_TITLE" ]     = "Asset = {0}";
IBM.LMV.Strings[ "KEY_COMPANY_PROP_TITLE" ]   = "Company= {0}";
IBM.LMV.Strings[ "KEY_LOCATION_PROP_TITLE" ]  = "Location= {0}";
IBM.LMV.Strings[ "KEY_PRODUCT_PROP_TITLE" ]   = "Product= {0}";
IBM.LMV.Strings[ "KEY_WO_PROP_TITLE" ]        = "Work Order= {0}";  

IBM.LMV.Strings[ "CAT_ADDRESS" ]              = "Address";
IBM.LMV.Strings[ "CAT_COST" ]                 = "Costs";
IBM.LMV.Strings[ "CAT_BASE" ]                 = "Basic Information";
IBM.LMV.Strings[ "CAT_DETAIL" ]		          = "Details";
IBM.LMV.Strings[ "CAT_DOWN" ]		          = "Down Time";
IBM.LMV.Strings[ "CAT_FACILITY" ]		      = "Facilities";
IBM.LMV.Strings[ "CAT_MODIFY" ]		          = "Modified";
IBM.LMV.Strings[ "CAT_PURCHASE" ]		      = "Purchase Information";
IBM.LMV.Strings[ "CAT_OTHER" ]		          = "Other";
IBM.LMV.Strings[ "CAT_SPEC" ]		          = "Specifications";

IBM.LMV.modelId   = null;
IBM.LMV.location  = null;
IBM.LMV.title     = null;
IBM.LMV.siteId    = null;

/**
 *****************************************************************************************************
 * Toolbar
 *****************************************************************************************************
 */

var toolBar = IBM.createNS( "IBM.LMV.ToolBar" );

toolBar.onToolbarCreate    = null;		// Called after the toolbar is created but before it is displayed
										// Intended to allow consuming applications to add items to the 
										// toolbar.

toolBar.ID_TOOLBAR_GROUP   = "Maximo_toolabr_group";
toolBar.ID_TOOLBAR_SELECT  = "Maximo_toolabr_submenu_select"
toolBar.ID_TOOLBAR_MAXIMO  = "Maximo_toolabr_submenu";


IBM.LMV.toolBarExtension = function(
	forgeViewer
) {
	const _forgeViewer      = forgeViewer;
		
	this.ctrl               = null;
	this.ctrlSearch         = null;
		
	this.toolbar            = null;
	this.buttonSelect       = null;
	this.buttonAutoZoom     = null;
		
	this.SELECT_MODE_SINGLE = 0;
	this.SELECT_MODE_MULTI  = 1;
	this.selectMode         = 0;
	
	this.isAutoZoom         = false;		// Get set to true when menu buttonm is created

	this.onCreate = function( e )
	{
		var _self = this;
		
		_forgeViewer.viewer.addEventListener( Autodesk.Viewing.FULLSCREEN_MODE_EVENT, 
								              function( evt ) { _self.onScreenModeChange( evt ) } );
		
		var mainToolbar = _forgeViewer.viewer.getToolbar(true);     // get the main toolbar from the viewer
		console.assert(mainToolbar != null);
		
		var maximoSubToolbar = new Autodesk.Viewing.UI.ControlGroup( IBM.LMV.ToolBar.ID_TOOLBAR_GROUP );
		// Get the "Tools" Tool bar section which has the "Camera" sub-menu
		var toolsSubMenu     = mainToolbar.getControl( "navTools" );
		
		// Find Existing Camera Submenu
		var cameraSubMenu       = toolsSubMenu.getControl("toolbar-cameraSubmenuTool");
	
		// Add Fit-To-View to existing menu
		var buttonFitModel = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_fit_model");
		buttonFitModel.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_expandToFit.png" + ")";
		buttonFitModel.setToolTip( IBM.LMV.Strings[ IBM.LMV.Strings.TOOLBAR_ZOOM_MODEL ] );
		buttonFitModel.onClick = function() { _forgeViewer.viewer.navigation.fitBounds(true, _forgeViewer.viewer.model.getBoundingBox());};
		var options = new Object();
		options.index = 4;
		cameraSubMenu.subMenu.addControl( buttonFitModel, options );
	
		var buttonFitToView = cameraSubMenu.subMenu.getControl( "toolbar-fitToViewTool" );
		buttonFitToView.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_zoomToSelected.png" + ")";
	
		// Add seach entry to the Maximo toolbar	
		maximoSubToolbar.addControl( this.makeSeachControl() );
	
		// Add a sub-menu to the Maximo toolbar to manage selection related options
		var submenuMaximoSelect = new Autodesk.Viewing.UI.ComboButton( IBM.LMV.ToolBar.ID_TOOLBAR_SELECT );
		submenuMaximoSelect.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_select.png" + ")";
		submenuMaximoSelect.setToolTip( IBM.LMV.Strings[ "Maximo" ] );
		maximoSubToolbar.addControl( submenuMaximoSelect );
		
		// Button to toggle between single and multi-select modes
		this.buttonSelect = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_single_selection");
		this.buttonSelect.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_selectSingle.png" + ")";
		this.buttonSelect.setToolTip( IBM.LMV.Strings[ IBM.LMV.Strings.TOOLBAR_SELECTION_MODE ] );
		this.buttonSelect.onClick = function( evt ) { _self.setSelectMode( evt );};
		
		// Add Hidel Selection button
		var buttonHideSelection = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_hide_selection");
		buttonHideSelection.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "hide_selection.png" + ")";
		buttonHideSelection.setToolTip( IBM.LMV.Strings[ IBM.LMV.Strings.TOOLBAR_HIDE_SELECTION ] );
		buttonHideSelection.onClick = function() { _self.doHideSelection();};
		
		var buttonIsolateSelection = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_isolate_selection");
		buttonIsolateSelection.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_isolateSelected.png" + ")";
		buttonIsolateSelection.setToolTip( IBM.LMV.Strings[ IBM.LMV.Strings.TOOLBAR_ISOLATE_SELECTION ] );
		buttonIsolateSelection.onClick = function() { _self.doIsolateSelection();};
	
		this.buttonAutoZoom = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_autozoomn");;
		this.buttonAutoZoom.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_autoZoomToSelected.png" + ")";
		this.buttonAutoZoom.onClick = function() { _self.setAutoZoomMode(); };
		this.setAutoZoomMode();
	
		submenuMaximoSelect.addControl( this.buttonAutoZoom );
		submenuMaximoSelect.addControl( buttonHideSelection );
		submenuMaximoSelect.addControl( buttonIsolateSelection );
		submenuMaximoSelect.addControl( this.buttonSelect );
		
		// Sub-menu for launcing Maximo functions
		var maximoSubMenu = new Autodesk.Viewing.UI.ComboButton( IBM.LMV.ToolBar.ID_TOOLBAR_MAXIMO );
		maximoSubMenu.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "mx_icon.png" + ")";
		maximoSubMenu.setToolTip( IBM.LMV.Strings[ "Maximo" ] );
		
		var buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolabr_submenu.restoreState");
		buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_restore_view.png )";
		buttonMaximoOpt.setToolTip( IBM.LMV.Strings.DLG_LABEL_APPLY_VIEW );
		buttonMaximoOpt.onClick = function() { _self.displayRestoreStateDlg(); };
		maximoSubMenu.addControl( buttonMaximoOpt );
	
		buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolabr_submenu.saveState");
		buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_save_view.png )";
		buttonMaximoOpt.setToolTip( IBM.LMV.Strings.DLG_LABEL_SAVE_VIEW );
		buttonMaximoOpt.onClick = function() { _self.displaySaveStateDlg(); };
		maximoSubMenu.addControl( buttonMaximoOpt );
		
		maximoSubToolbar.addControl( maximoSubMenu );
	
		mainToolbar.addControl( maximoSubToolbar );
	
		if( IBM.LMV.ToolBar.onToolbarCreate != null )
		{
			try
			{
				IBM.LMV.ToolBar.onToolbarCreate( _forgeViewer, this );
			}
			catch( e )
			{
				IBM.LMV.displayError( e.message );
			}
		}
	};
	
	this.makeSeachControl = function()
	{
		var _self = this;
		var buttonSearch = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_search");
		buttonSearch.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_find.png" + ")";
		buttonSearch.setToolTip( IBM.LMV.Strings[ IBM.LMV.Strings.TOOLBAR_SEARCH ] );
		buttonSearch.onClick = function( e ) { _self.doSearch( e );};
		var ctrl = buttonSearch.container;
		ctrl.style.width = "168px";
		ctrl = ctrl.childNodes[0];
		ctrl.style.backgroundPosition = "95%";
		ctrl.style.paddingRight = "20px";
	
		this.ctrlSearch     = document.createElement("INPUT");
		this.ctrlSearch.style.width = "140px";
		this.ctrlSearch.onkeypress = function( evt ) { _self.searchKeyPress( evt ); };
		this.ctrlSearch.style.boxShadow = "0 3px 5px rgba(0,0,0,.5)";
		ctrl.appendChild( this.ctrlSearch );
		
		return buttonSearch;
	};
	
	// Toggle Auto-zoom
	this.setAutoZoomMode = function()
	{
		if( this.buttonAutoZoom == null )
		{
			return;
		}
		if( this.isAutoZoom )
		{
			this.isAutoZoom = false; 
			_forgeViewer.setAutoZoomMode( false );
			this.buttonAutoZoom.setToolTip( IBM.LMV.Strings.KEY_ENABLE_AUTO_ZOOM );
			this.buttonAutoZoom.icon.style.backgroundColor = "";
		}
		else
		{
			this.isAutoZoom = true;
			_forgeViewer.setAutoZoomMode( true );
			this.buttonAutoZoom.setToolTip( IBM.LMV.Strings.KEY_DISABLE_AUTO_ZOOM );
			this.buttonAutoZoom.icon.style.backgroundColor = "rgba(000, 53, 104, 0.7)";
		}
	}
	
	// Called from ToolBar Button
	this.displayRestoreStateDlg = function()
	{
		var LoadViewDlg = new IBM.LMV.LoadViewDlg( _forgeViewer );
		LoadViewDlg.setVisible( true );
	}
	
	// Called from ToolBar Button
	this.displaySaveStateDlg = function()
	{
		var saveViewDlg = new IBM.LMV.SaveViewDlg( _forgeViewer );
		saveViewDlg.setVisible( true );
	}
	
	this.doHideSelection = function()
	{
		_forgeViewer.viewer.hide( _forgeViewer.getSelectionList() );
	};
	
	this.doIsolateSelection = function()
	{
		_forgeViewer.viewer.isolate( _forgeViewer.getSelectionList() );
	};
	
	this.doSearch = function()
	{
		var _self = this;
		var value = this.ctrlSearch.value;
		if( value == null || value == "" )
		{
			return;
		}
		_forgeViewer.viewer.search( value, 
							function( dbids ) { _self.onSearch( dbids ) }, 
							function( msg ) { _self.onLoadError( msg ) } );
	};
	
	this.onSearch = function( 
		dbIds
	) {
		_forgeViewer.select( dbIds );
	};
	
	this.height  = null;
	this.width   = null;
	
	this.onScreenModeChange = function(
		e
	) {
		var _self = this;
		var mode = e.mode;
		
		switch( e.mode )
		{
			case 0:
				var height = IBM.LMV.ctrlViewer.clientHeight;
				var width  = IBM.LMV.ctrlViewer.clientWidth;
				if( this.height != null && this.width != null )
				{
					_forgeViewer.viewer.container.style.height = "" + this.height + "px";
					_forgeViewer.viewer.container.style.width  = "" + this.width + "px";
				}
				this.fireModeChangeCallback( e.mode );
				break;
			case 1:
				this.fireModeChangeCallback( e.mode );
				break;
			case 2:
				this.height = IBM.LMV.ctrlViewer.clientHeight;
				this.width  = IBM.LMV.ctrlViewer.clientWidth;
				_forgeViewer.viewer.container.style.height = "" + screen.height + "px";
				_forgeViewer.viewer.container.style.width  = "" + screen.width + "px";
				_forgeViewer.viewer.resize();
				setTimeout( function() { _self.fireModeChangeCallback( mode ); }, 300 );
				break;
		}
	};
	
	this.fireModeChangeCallback = function( mode )
	{
		for( var i = 0; i < IBM.LMV.screenModeChangeListeners.length; i++ )
		{
			try
			{
				IBM.LMV.screenModeChangeListeners[i]( mode );
			}
			catch( e )
			{
				IBM.LMV.displayError( e.message );
				console.log( e );
			}
		}
	};
	
	this.setSelectMode = function( e )
	{
		var ctrlMode = e.currentTarget;
		if( this.selectMode == this.SELECT_MODE_SINGLE )
		{
			_forgeViewer.setMultiSelect( true );
			this.selectMode = this.SELECT_MODEL_MULTI;
			ctrlMode.src = IBM.LMV.PATH_IMAGES + "tb_selectMultiple.png";
			this.buttonSelect.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_selectMultiple.png" + ")";
		}
		else
		{
			_forgeViewer.setMultiSelect( false );
			this.selectMode = this.SELECT_MODE_SINGLE;
			ctrlMode.src = IBM.LMV.PATH_IMAGES + "tb_selectSingle.png";
			this.buttonSelect.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_selectSingle.png" + ")";
		}
	};
	
	
	/**********************************************************************/
	// Catch ENTER key in search field
	/**********************************************************************/
	this.searchKeyPress = function( evt )
	{
		var _self = this;
		var keynum   = 0;
		
		if( evt != null )
		{
			keynum = evt.which;
		}
		else if(window.event) // IE
		{
			evt = window.event;
			if( evt.which ) // Netscape/Firefox/Opera
			{
				keynum = evt.which;
			}
			else
			{
				keynum = evt.keyCode;
			}
		}
		evt.stopPropagation();
		
		if( keynum == 13 )
		{
			this.doSearch();;
		}
		return false;
	};
};

/**
  *****************************************************************************************************
  * dataDictionary
  * Retrieve and cache labels for any Mbo.  The Title field from MaxAttribute is used for the label
  *
  * Retrieve and cache labels for Specifications.  The description field from ASSETATTRIBUTE is used for 
  * label
  *
  * Each instance of MaxAttribute retrieved is converted into a object (map) with the attribute name as 
  * the JavaScript attribute name and the title as the value.  Then resulting object is then assigned
  * to this object with the OBJECTNAME value as the property name;
  *****************************************************************************************************
  */
IBM.LMV.DataDictionary = function ()
{
	this.requestList     = [];
	this.requestSpecList = [];
	this.specNameMap     = null;

	this.getLabels = function(
		mbo, onSuccess, onError
	) {
		mbo = mbo.toUpperCase();
		if( this[ mbo ] )
		{
			onSuccess( mbo, this[ mbo ] );
			return;
		}
		var request = {};
		request.mbo       = mbo;
		request.onSuccess = onSuccess;
		request.onError   = onError;
		this.requestList.push( request );
		
		this.lookupLabels( mbo );
	};
	
	this.getSpecifications = function(
		onSuccess, onError
	) {
		if( this.specNameMap )
		{
			onSuccess( this.specNameMap );
			return;
		}
		var request = {};
		request.onSuccess = onSuccess;
		request.onError   = onError;
		this.requestSpecList.push( request );
		
		this.lookupSpecifications();
	};
	
	this.lookupLabels = function(
		mbo
	) {
		var that = this;
		
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/MaxAttribute" ;
		url = url + "?OBJECTNAME=~eq~" + mbo + "&_compact=1";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onLabels( this ); };
		xmlReq.open( "GET", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	};
	
	this.lookupSpecifications = function() 
	{
		var that = this;
		
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/ASSETATTRIBUTE?_compact=1" ;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onSpecifications( this ); };
		xmlReq.open( "GET", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	}

	this.onLabels = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			for( var i in this.requestList )
			{
				if( this.requestList[i].mbo === mbo )
				{
					var request = this.requestList[i];
					this.requestList.splice( i--, 1 );
					if(  request.onError )
					{
						request.onError( request );
					}
				}
			}
			return;
		}
  
		var mboSet = JSON.parse( request.responseText );
		var attributes = mboSet.MAXATTRIBUTEMboSet.MAXATTRIBUTE;
		var mbo = "";
		if( attributes.length > 0 )
		{
			mbo = attributes[0].OBJECTNAME;
		}
		var labels = {};
		for( var i = 0; i < attributes.length; i++ )
		{
			var name  = attributes[i].ATTRIBUTENAME;
			var title = attributes[i].TITLE;
			if( name == "HIERARCHYPATH" )
			{
				title = IBM.LMV.Strings.KEY_CLASS_STRUCT;
			}
			if( name == "PARENT" && mbo == "LOCATIONS" )
			{
				title = IBM.LMV.Strings.KEY_PARENT;
			}
			if( title != null && title.length > 0 )
			{
				labels[ name ] = title;
			}
		}
		this[ mbo ] = labels;
		
		for( var i in this.requestList )
		{
			if( this.requestList[i].mbo === mbo )
			{
				var request = this.requestList[i];
				this.requestList.splice( i--, 1 );
				if(  request.onSuccess )
				{
					try
					{
						request.onSuccess( mbo, this[ mbo ] );
					}
					catch( e ) 
					{
						console.log( e );
					}
				}
			}
		}
	};

	this.onSpecifications = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			for( var i in this.requestList )
			{
				var request = this.requestList[i];
				this.requestList.splice( i--, 1 );

			}
			return;
		}  

		var mboSet = JSON.parse( request.responseText );
		var attributes = mboSet.ASSETATTRIBUTEMboSet.ASSETATTRIBUTE;
		this.specNameMap = {};
		for( var i = 0; i < attributes.length; i++ )
		{
			var spec = {};
			spec.title =  attributes[i].DESCRIPTION;
			if( attributes[i].MEASUREUNITID )
			{
				spec.units = attributes[i].MEASUREUNITID;
			}
			this.specNameMap[ attributes[i].ASSETATTRID ] = spec;
		}

		for( var i in this.requestSpecList )
		{
			var request = this.requestSpecList[i];
			this.requestSpecList.splice( i--, 1 );
			if(  request.onSuccess )
			{
				request.onSuccess( this.specNameMap );
			}
		}
	};
};

IBM.LMV.dataDictionary = new IBM.LMV.DataDictionary();



/**
  *****************************************************************************************************
  * Create Save View Dialog
  *****************************************************************************************************
  */
IBM.LMV.SaveViewDlg = function (
	forgeViewer
) {
	var scrollContainer;
	var descInput;
	var detailsInput;
	var priorityInput;
 
	this.forgeViewer = forgeViewer;			
	this.parent      = forgeViewer.viewer.container;
	
	var sharedCB     = null;
	var descInput    = null;
	var detailsInput = null;
	
	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "Maximo-SaveView-DLG", IBM.LMV.Strings.DLG_TITLE_SAVE_VIEW );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "auto";
	this.container.style.width  = "418px";
	this.container.style.resize = "none";
	
	this.saveView = function()
	{
		if( this.descInput.value == null || this.descInput.value == "" )
		{
			var messageBox = new IBM.LMV.MessageBox( this.parent, 
													 this[ "BIMLMVSAVEDVIEW" ].DESCRIPTION, 
													 IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD );
			messageBox.setVisible( true );
			return;
		}
		var state = this.forgeViewer.viewer.getState();
		var stateJSON = JSON.stringify( state ) ;
		this.viewerState = btoa( stateJSON );

		var that = this;
		var form = "";
		form = form + "BUILDINGMODELID"     + "=" + IBM.LMV.modelId;
		form = form + "&siteid"      + "=" + IBM.LMV.siteId;
		form = form + "&description" + "=" + this.descInput.value;
		form = form + "&viewerstate" + "=" + this.viewerState;
		if( this.detailsInput.value && this.detailsInput.value != "" )
		{
			form = form + "&DESCRIPTION_LONGDESCRIPTION" + "=" + this.detailsInput.value;
		}
		if( this.sharedCB.checked === true )
		{
			form = form + "&SHARED=1";
		}

		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/bimlmvsavedview";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onSaveView( this ); };;
		xmlReq.open( "POST", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( form );
	}
	
	this.makeSaveViewScreen = function(
		attributes	
	) {
		var that = this;
		var btnBar       = document.createElement("TABLE");
		btnBar.name      = "MessageBoxBtnBar";
		btnBar.style.cssFloat = "right";
		var row          = btnBar.insertRow( 0 );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CREATE;
		cell.onclick   = function() { that.saveView( this ); };

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CLOSE;
		this.initializeCloseHandler( cell );

		this.container.appendChild( btnBar );
		
		this.addEventListener( cell, 'click', function (e) {
			that.uninitialize();
		}, false );
		
		this.scrollContainer.innerHTML = "";
		
		var dlgTable       = document.createElement("TABLE");
		dlgTable.name      = "Maximo-BIMField-SAVEVIEW-Table";
		dlgTable.className = "maxlmv_DlgTable";

		var i = 0;
		var row          = dlgTable.insertRow( i++ );
		var cell         = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "auto";
		cell.innerHTML   = this[ "BUILDINGMODEL" ].TITLE;
		
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "50%";
		cell.innerHTML   = IBM.LMV.title;
		
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "auto";
		cell.innerHTML   = this[ "BUILDINGMODEL" ].LOCATION;
		
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "50%";
		cell.innerHTML   = IBM.LMV.location;

		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgHeading";
		cell.colSpan     = 4;
		cell.innerHTML   = IBM.LMV.Strings.DLG_LABEL_SAVE_VIEW;

		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.colSpan     = 4;
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this[ "BIMLMVSAVEDVIEW" ].DESCRIPTION;

		row               = dlgTable.insertRow( i++ );
		cell              = row.insertCell();
		cell.className    = "maxlmv_DlgText";
		cell.colSpan      = 4;
		this.descInput    = document.createElement("INPUT");
		this.descInput.id = "Maximo-Field-WO-Description";
		this.descInput.style.width = "400px";
		cell.appendChild( this.descInput );

		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.colSpan     = 4;
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this[ "BIMLMVSAVEDVIEW" ].DESCRIPTION_LONGDESCRIPTION;

		row                  = dlgTable.insertRow( i++ );
		cell                 = row.insertCell();
		cell.colSpan         = 4;
		cell.className       = "maxlmv_DlgText";
		this.detailsInput    = document.createElement("TEXTAREA");
		this.detailsInput.rows = 4;
		this.detailsInput.id = "Maximo-Field-SAVEVIEW-Detail";
		this.detailsInput.style.width = "400px";
		cell.appendChild( this.detailsInput );

		row                = dlgTable.insertRow( i++ );
		cell               = row.insertCell();
		cell.innerHTML     = this[ "BIMLMVSAVEDVIEW" ].SHARED;
		cell.colSpan       = 4;
		cell.className     = "maxlmv_DlgText";
		this.sharedCB      = document.createElement("INPUT");
		this.sharedCB.type = "checkbox";
		this.sharedCB.id   = "Maximo-Field-SAVEVIEW-Shared";
		cell.appendChild( this.sharedCB );


		this.scrollContainer.appendChild( dlgTable );
	}
	
	this.onLabelLookup = function(
		mbo, labels
	) {
		this[ mbo ] = labels;
		if( this[ "BUILDINGMODEL" ] && this[ "BIMLMVSAVEDVIEW" ] )
		{
			this.makeSaveViewScreen( labels );
		}
	}

	this.onSaveView = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
			return;
		}
		this.uninitialize();
	};
};

IBM.LMV.SaveViewDlg.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.SaveViewDlg.prototype.constructor = IBM.LMV.SaveViewDlg;

IBM.LMV.SaveViewDlg.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );
	
	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
};

IBM.LMV.SaveViewDlg.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		var that = this;
		IBM.LMV.dataDictionary.getLabels( "BUILDINGMODEL",   function( mbo, labels ) { that.onLabelLookup(  mbo, labels  ); } );
		IBM.LMV.dataDictionary.getLabels( "BIMLMVSAVEDVIEW", function( mbo, labels ) { that.onLabelLookup(  mbo, labels  ); } );

		var parentHeight = this.parent.clientHeight;
		var parentWidth  = this.parent.clientWidth;
		var height       = this.container.clientHeight;
		var width        = this.container.clientWidth;
	
		if( width > parentWidth )
		{
			this.container.style.left = 0;
		}
		else
		{
			var left = (parentWidth  - width ) / 2;
			this.container.style.left = "" + left + "px";
		}

		if( height > parentHeight )
		{
			this.container.style.top = 0;
		}
		else
		{
			var top  = (parentHeight - height ) /2;
			this.container.style.top = "" + top + "px";
		}
	}
};

/**
  *****************************************************************************************************
  * Select Save View Dialog
  * Gets the viewer state, conversts to JSON, Base 64 encodes and saves to Maximo
  *****************************************************************************************************
  */
IBM.LMV.LoadViewDlg = function (
	forgeViewer
) {
	var scrollContainer;
	var viewTable			// Ui table for view listing
	var descInput;
	var detailsInput;
	var viewList;			// JSON Data from Maxumo on the list of curtrent views
	var currentView;		// Index of currently selected view in teh UI table
 
	this.forgeViewer    = forgeViewer;			
	this.parent         = forgeViewer.viewer.container;
	this.heading        = null;
	this.selectedRow    = null;
	this.eventsDisabled = false;
	

	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "Maximo-LoadView-DLG", IBM.LMV.Strings.DLG_TITLE_APPLY_VIEW );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "auto";
	this.container.style.width  = "418px";
	this.container.style.resize = "none";
	
	this.applyView = function()
	{
		if( this.eventsDisabled )
		{
			return;
		}

		if( this.selectedRow == null )
		{
			return;
		}
		var view = this.viewList[ this.selectedRow.rowIndex - 1 ];

		var decoded = atob( view.VIEWERSTATE );
		var state = JSON.parse( decoded );
		this.forgeViewer.viewer.restoreState( state ); 

		this.uninitialize();
	};

	this.onDeleteButton = function(
		cell, evt
	) {
		if( this.eventsDisabled )
		{
			return;
		}
		
		if( evt == null )			// All but Firefox
		{
			evt = event;
		}
		
		this.eventsDisabled = true;
		var row          = cell.parentNode;
		this.currentView = row.rowIndex -1;
		evt.stopPropagation();
		
		var _self = this;
		var msg = IBM.LMV.Strings.DLG_TXT_DELETE_VIEW + this.viewList[ this.currentView ].DESCRIPTION;
		var messageBox = new IBM.LMV.MessageBox( this.parent, msg, IBM.LMV.Strings.DLG_TITLE_CONFIRM_DELETE, [ "OK", "CANCEL" ], 
		                                         function( button ) {_self.onDeleteResponse( button ); } );
		messageBox.setVisible( true );
		
	}
	
	this.onDeleteResponse = function(
		buttonId
	) {
		if( buttonId == "OK" )
		{
			var _self = this;
			var url = IBM.LMV.Auth.getRestURL();
			url = url + "/mbo/bimlmvsavedview/" + this.viewList[ this.currentView ].BIMLMVSAVEDVIEWID;
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { _self.onDelete( this ); };;
			xmlReq.open( "DELETE", url, true );
			IBM.LMV.Auth.setRequestHeaders( xmlReq );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
	
			xmlReq.send();
	
			this.viewTable.deleteRow( this.currentView + 1 );
			this.viewList.splice( this.currentView, 1 );
		}
		else
		{
			this.eventsDisabled = false;
		}
	}
	
	this.lookupSavedViews = function() 
	{
		var that = this;
		
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/bimlmvsavedview" ;
        url = url +  "?~modelId=" + IBM.LMV.modelId;
        url = url +  "&~siteId=" + IBM.LMV.siteId;
		url = url + "&_compact=1";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onViews( this ); };
		xmlReq.open( "POST", url, true );
		xmlReq.setRequestHeader("x-http-method-override", "getSavedViews");
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	};
	
	this.onApplyRow = function( row )
	{
		if( this.eventsDisabled )
		{
			return;
		}

		this.selectedRow = row;
		this.applyView();
	};

	this.onDelete = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		this.eventsDisabled = false;

		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
		}
	}
	
	this.onDetailButton = function(
		cell, evt
	) {
		if( this.eventsDisabled )
		{
			return;
		}
		
		if( evt == null )			// All but Firefox
		{
			evt = event;
		}
		
		var row          = cell.parentNode;
		this.currentView = row.rowIndex -1;
		evt.stopPropagation();
		
		var text = this.viewList[ this.currentView ].DESCRIPTION_LONGDESCRIPTION;
		var detailsDlg = new IBM.LMV.DetailsDlg( this.parent, text, this.heading[ "DESCRIPTION_LONGDESCRIPTION" ] );
		detailsDlg.setVisible( true );
	}
	
	this.onHeadings = function( mbo, headings )
	{
		this.heading = headings;
		this.header.cells[0].innerHTML = headings[ "DESCRIPTION" ];
		this.header.cells[1].innerHTML = headings[ "OWNER" ];
		this.header.cells[2].innerHTML = headings[ "DESCRIPTION_LONGDESCRIPTION" ];
		this.header.cells[3].innerHTML = headings[ "SHARED" ];
		this.populateList();
	};

	this.onSelectRow = function( row )
	{
		if( this.eventsDisabled )
		{
			return;
		}

		if( this.selectedRow )
		{
			this.selectedRow.className  = "maxlmv_selectable";
		}
		this.selectedRow = row;
		this.selectedRow.className  = "maxlmv_selected";
	};

	this.onViews = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
			this.uninitialize();
			return;
		}

		var mboSet = JSON.parse( request.responseText );
		this.viewList = mboSet.BIMLMVSAVEDVIEWMboSet.BIMLMVSAVEDVIEW;
		this.populateList();
	};
	
	this.populateList = function()
	{
		// Wailt for both headings and view list to load
		if( this.heading == null || this.viewList == null )
		{
			return;
		}
		
		var _self = this;
		
		for( var i = 0; i < this.viewList.length; i++ )
		{
			var row        = this.viewTable.insertRow( i + 1 );	// Skip heading and filter
			row.onclick    = function() { _self.onSelectRow( this ) };
			row.ondblclick = function() { _self.onApplyRow( this ) };
			if( i == 0 )
			{
				this.selectedRow = row;
				this.selectedRow.className  = "maxlmv_selected";
			}
			else
			{
				row.className  = "maxlmv_selectable";
			}
			
			var cell       = row.insertCell( 0 );
			cell.className = "maxlmv_DlgText";
			cell.innerHTML = this.viewList[i].DESCRIPTION;
			
			cell = row.insertCell( 1 );
			cell.className = "maxlmv_DlgText";
			cell.innerHTML = this.viewList[i].OWNER;

			cell = row.insertCell( 2 );
			cell.className = "maxlmv_DlgText";
			if( this.viewList[i].DESCRIPTION_LONGDESCRIPTION )
			{
				var ctrl       = document.createElement("IMG");
				ctrl.src       = IBM.LMV.PATH_IMAGES + "img_longdescription.png";
				ctrl.alt       = this.heading[ "DESCRIPTION_LONGDESCRIPTION" ];
				ctrl.title     = this.heading[ "DESCRIPTION_LONGDESCRIPTION" ];
				ctrl.className = "maxlmv_clickableImage";
				cell.onclick   = function( evt ) { _self.onDetailButton( this, evt ); };
				cell.appendChild( ctrl );
			}
			
			cell = row.insertCell( 3 );
			cell.className = "maxlmv_DlgText";
			var ctrl       = document.createElement("IMG");
			ctrl.src       = IBM.LMV.PATH_IMAGES + "360_delete.png";
			ctrl.alt       = IBM.LMV.Strings.DLG_BTN_DELETE_VIEW;
			ctrl.title     = IBM.LMV.Strings.DLG_BTN_DELETE_VIEW;
			ctrl.className = "maxlmv_clickableImage";
			cell.onclick   = function( evt ) { _self.onDeleteButton( this, evt ); };
			cell.appendChild( ctrl );
		}
		
		this.sizeAndPosition();
	}

	this.sizeAndPosition = function()
	{
		var parentHeight = this.parent.clientHeight;
		var parentWidth  = this.parent.clientWidth;
		var height       = this.container.clientHeight;
		var width        = this.container.clientWidth;
		
		if( width > parentWidth )
		{
			this.container.style.left = 0;
			this.container.style.width = "" + parentWidth + "px";
		}
		else
		{
			var left = (parentWidth  - width ) / 2;
			this.container.style.left = "" + left + "px";
		}
		
		if( height > parentHeight -50 )
		{
			this.container.style.top = 0;
			this.scrollContainer.style.height = "" + (parentHeight - 75) + "px";
		}
		else
		{
			var top  = (parentHeight - height ) /2;
			this.container.style.top = "" + top + "px";
		}
	};
};

IBM.LMV.LoadViewDlg.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.LoadViewDlg.prototype.constructor = IBM.LMV.LoadViewDlg;

IBM.LMV.LoadViewDlg.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );
	
	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
};

IBM.LMV.LoadViewDlg.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		var _self = this;
		var btnBar       = document.createElement("TABLE");
		btnBar.name      = "MessageBoxBtnBar";
		btnBar.style.cssFloat = "right";
		var row          = btnBar.insertRow( 0 );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_APPLY;
		cell.onclick   = function() { _self.applyView( this ); };

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CLOSE;
		this.initializeCloseHandler( cell );

		this.container.appendChild( btnBar );
		
		this.addEventListener( cell, 'click', function (e) {
			_self.uninitialize();
		}, false );

		// Setup header row. Labels are filled async when they are downloaded
		this.viewTable           = document.createElement("TABLE");
		this.viewTable.className = "maxlmv_DlgTable";
		this.viewTable.name      = "Maximo-BIMField-LOADVIEW-Table";
		var thead                = this.viewTable.createTHead();
		this.header              = thead.insertRow( 0 );
		var cell                 = this.header.insertCell( 0 );
		cell                     = this.header.insertCell( 1 );
		cell                     = this.header.insertCell( 2 );
		cell                     = this.header.insertCell( 3 );

		this.scrollContainer.appendChild( this.viewTable );

		var that = this;
		IBM.LMV.dataDictionary.getLabels( "BIMLMVSAVEDVIEW", function( mbo, labels ) { that.onHeadings(  mbo, labels  ); } );
		
		this.lookupSavedViews();
	}
};


/**
  *****************************************************************************************************
  * Display Detials (long description) text where needed
  *****************************************************************************************************
  */
IBM.LMV.DetailsDlg = function (
	parent, 
	text,
	title
) {
	this.parent         = parent;
	if( text.endsWith( "<!-- RICH TEXT -->" ) )
	{
		text = text.substring( 0, text.length - 18 );
	}
	
	this.text           = text;

	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "Maximo-Details-DLG", title );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "300px";
	this.container.style.width  = "418px";
	this.container.style.resize   = "auto";
	this.container.style.zIndex = 950;

	this.sizeAndPosition = function()
	{
		var parentHeight = this.parent.clientHeight;
		var parentWidth  = this.parent.clientWidth;
		var height       = this.container.clientHeight;
		var width        = this.container.clientWidth;
	
		if( width > parentWidth )
		{
			this.container.style.left = 0;
		}
		else
		{
			var left = (parentWidth  - width ) / 2;
			this.container.style.left = "" + left + "px";
		}
		
		if( height > parentHeight )
		{
			this.container.style.top = 0;
		}
		else
		{
			var top  = (parentHeight - height ) /2;
			this.container.style.top = "" + top + "px";
		}
	};
};

IBM.LMV.DetailsDlg.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.DetailsDlg.prototype.constructor = IBM.LMV.DetailsDlg;

IBM.LMV.DetailsDlg.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );
	
	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
};

IBM.LMV.DetailsDlg.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		this.scrollContainer.style.height = 0;		// Not used

		var detailsInput          = document.createElement("TEXTAREA");
		detailsInput.value        = this.text;
		detailsInput.id           = "Maximo-Field-Details-Detail";
		detailsInput.style.width  = "100%";
		detailsInput.style.height = "calc(100% - 80px)";
		detailsInput.style.resize = "none";
		this.container.appendChild( detailsInput );
		
		var _self = this;
		var btnBar       = document.createElement("TABLE");
		btnBar.name      = "DetailBoxBtnBar";
		btnBar.style.cssFloat = "right";

		var row          = btnBar.insertRow( 0 );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CLOSE;
		this.initializeCloseHandler( cell );

		this.container.appendChild( btnBar );
		
		this.addEventListener( cell, 'click', function (e) {
			_self.uninitialize();
		}, false );
		
		this.sizeAndPosition();
	}
};

/**
  *****************************************************************************************************
  * Base property pannel for displaying Maximo Mbo data including associated specifications
  *****************************************************************************************************
  */
IBM.createNS( "IBM.LMV.MaximoPropertyPanel" );
IBM.LMV.MaximoPropertyFetchers = {};
IBM.LMV.MaximoPropertyFetchers.ASSET = 
	{
		mboName     : "ASSET",
		categoryMap : { 
						 ASSETID                : "NONE",
						 ASSETSPEC              : "NONE",
						 AUTOWOGEN              : "NONE",
						 CHANGEPMSTATUS         : "NONE",
						 LANGCODE               : "NONE",
						 MAINTHIERCHY           : "NONE",
						 MODELID                : "NONE",
						 PLUSCSOLUTION          : "NONE",
						 REMOVEFROMACTIVEROUTES : "NONE",
						 REMOVEFROMACTIVESP     : "NONE",
						 ROLLTOALLCHILDREN      : "NONE",
	
						 ASSETNUM               : "BASE",
						 ASSETTYPE              : "BASE",
						 DESCRIPTION            : "BASE",
						 ITEMSETID              : "BASE",
						 LOCATION               : "BASE",
						 ORGID                  : "BASE",
						 SITEID                 : "BASE",
						 STATUS                 : "BASE",
						 TEMPLATEID             : "BASE",
						 
						 CALNUM                 : "DETAIL",
						 CONDITIONCODE          : "DETAIL",
						 FAILURECODE            : "DETAIL",
						 GROUPNAME              : "DETAIL",
						 ITEMNUM                : "DETAIL",
						 PARENT                 : "DETAIL",
						 PRIORITY               : "DETAIL",
						 SERIALNUM              : "DETAIL",
						 SHIFTNUM               : "DETAIL",
						 TOOLRATE               : "DETAIL",
						 USAGE                  : "DETAIL",
	
						 CHANGEBY               : "MODIFY",
						 CHANGEDATE             : "MODIFY",
	
						 BUDGETCOST             : "COST",
						 INVCOST                : "COST",
						 TOTALCOST              : "COST",
						 TOTUNCHARGEDCOST       : "COST",
						 UNCHARGEDCOST          : "COST",
						 YTDCOST                : "COST",							   
	
						 INSTALLDATE            : "PURCHASE",
						 MANUFACTURER           : "PURCHASE",
						 PRODUCTID              : "PURCHASE",
						 PURCHASEPRICE          : "PURCHASE",
						 REPLACECOST            : "PURCHASE",
						 VENDOR                 : "PURCHASE",
	
						 STATUSDATE             : "DOWN",
						 ISRUNNING              : "DOWN",
						 TOTDOWNTIME            : "DOWN",
					},
					
		categoryToNameMap : { BASE     : IBM.LMV.Strings.CAT_BASE,
							  DETAIL   : IBM.LMV.Strings.CAT_DETAIL,
							  MODIFY   : IBM.LMV.Strings.CAT_MODIFY,
							  PURCHASE : IBM.LMV.Strings.CAT_PURCHASE,
							  DOWN     : IBM.LMV.Strings.CAT_DOWN,
							  COST     : IBM.LMV.Strings.CAT_COST,
							  OTHER    : IBM.LMV.Strings.CAT_OTHER,
						   },
				
		lableToNameMap : {
						LOCATION     : null,
						PRODUCTID    : null,
						MANUFACTURER : null,
					   },
		
		getMbiId : function(
			mbo
		) {
			if( mbo == null ) return "";
			return mbo.ASSETNUM;
		},
		
		lookupMboByModelId : function(
			panel, guid
		) {
			var that = this;
			
			panel.mboLoadSemiphore = false;
			panel.currentMbo = null;
			panel.currentSpecs = null;
			if( guid == null || guid.length == 0 )
			{
				panel.showMessage( IBM.LMV.Strings.ERR_NO_GUID );
				return;
			}
			var url = IBM.LMV.Auth.getRestURL();
			url = url + "/os/MXAsset";
			url = url + "?MODELID=~eq~" + guid  + "&_compact=1";
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { that.onAsset( panel, this ); };;
			xmlReq.open( "GET", url, true );
			IBM.LMV.Auth.setRequestHeaders( xmlReq );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
	
			xmlReq.send();
		},
	
		onAsset : function( 
			panel, request 
		) {
			if( request.readyState != 4 )  
			{ 
				return; 
			}
	
			panel.fetcher          = this;
			if( request.status != 200 )
			{
				IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
				return;
			}
			
			var mboSet = JSON.parse( request.responseText );
			var assets = mboSet.QueryMXASSETResponse.MXASSETSet.ASSET;
			if( assets == null || assets.length == 0 )
			{
				panel.mboLoadSemiphore = true;
				panel.currentMbo = null;
				panel.currentSpecs = null;
				panel.showNoProperties();
				return;
			}
			panel.currentMbo = assets[0];
			panel.currentSpecs = assets[0].ASSETSPEC;
			IBM.LMV.dataDictionary.getLabels( "ASSET", function( mbo, labels ) { panel.onLabels(  mbo, labels ); } );
			panel.mboLoadSemiphore = true;
			panel.displayMbo();				
			panel.lookupAttachments( "ASSET", assets[0].ASSETID );		
		},
		
		makeTitle : function(
			mbo
		) {
			var name = "";
			var title = IBM.LMV.Strings.KEY_ASSET_PROP_TITLE;
			if( mbo != null )
			{
				name = mbo.ASSETNUM;
			}
			title = title.replace( "{0}",  name );
				
			return title;
		}
	},

/**
  *****************************************************************************************************
  * Fetches data from the Product Mbo to populate the property panel
  *****************************************************************************************************
  */
IBM.LMV.MaximoPropertyFetchers.COMPANIES = 
	{
		mboName     : "COMPANIES",
		categoryMap : { 
				AUTOAPPROVEINV     : "NONE",
				AUTORECEIVEONASN   : "NONE",
				AUTOSENDPOCANCEL   : "NONE",
				BANKACCOUNT        : "NONE",
				BANKNUM            : "NONE",
				COMPANIESID        : "NONE",
				COMPCONTACT        : "NONE",
				ECOMMERCEENABLED   : "NONE",
				EXTERNALREFID      : "NONE",
				HASLD              : "NONE",
				INCLUSIVE1         : "NONE",
				INCLUSIVE2         : "NONE",
				INCLUSIVE3         : "NONE",
				INCLUSIVE4         : "NONE",
				INCLUSIVE5         : "NONE",
				LANGCODE           : "NONE",
				OWNERSYSID         : "NONE",
				REGISTRATION1      : "NONE",
				REGISTRATION2      : "NONE",
				REGISTRATION3      : "NONE",
				REGISTRATION4      : "NONE",
				REGISTRATION5      : "NONE",
				REMITADDRESS1      : "NONE",
				REMITADDRESS2      : "NONE",
				REMITADDRESS3      : "NONE",
				REMITADDRESS4      : "NONE",
				REMITADDRESS5      : "NONE",
				REMITCONTACT       : "NONE",
				SENDERSYSID        : "NONE",
				SOURCESYSID        : "NONE",
				TAX1CODE           : "NONE",
				TAX2CODE           : "NONE",
				TAX3CODE           : "NONE",
				TAX4CODE           : "NONE",
				TAX5CODE           : "NONE",
				TAXEXEMPTCODE      : "NONE",
				TAXEXEMPTNUM       : "NONE",
				TOOLCONTROLACCOUNT : "NONE",

				COMPANY              : "BASE",
				CURRENCYCODE         : "BASE",
				HOMEPAGE             : "BASE",
				LOCATION             : "BASE",
				NAME                 : "BASE",
				NAME_LONGDESCRIPTION : "BASE",
				ORGID                : "BASE",
				PARENTCOMPANY        : "BASE",
				TYPE                 : "BASE",

				ADDRESS1             : "ADDRESS",
				ADDRESS2             : "ADDRESS",
				ADDRESS3             : "ADDRESS",
				ADDRESS4             : "ADDRESS",
				ADDRESS5             : "ADDRESS",
				PHONE                : "ADDRESS",
				CELLPHONE            : "ADDRESS",
				FAX                  : "ADDRESS",
		
				CHANGEBY             : "MODIFY",
				CHANGEDATE           : "MODIFY",
			},
		
			/*		
			APCONTROLACC: "AP Control Account"
			APSUSPENSEACC: "Suspense Account"
			CATALOGNAME: "Catalog"
			CONSACCT: "Consignment Account"
			CONTACT: "Customer Contact"
			CUSTOMERNUM: "Customer #"
			DEFAULTWAREHOUSE: "Default Warehouse"
			DISABLED: "Disqualified Vendor"
			DUNSNUM: "DUNS #"
			ECOMINTERFACE: "E-commerce Supplier Location"
			FOB: "FOB Point"
			FREIGHTTERMS: "Freight Terms"
			FREIGHTTERMS_LONGDESCRIPTION: "Freight Terms Long Description"
			INSPECTIONREQUIRED: "Inspection Required"
			INSUREXPDATE: "Expiration Date of Insurance"
			MNETCOMPANYID: "E-commerce Supplier"
			PAYMENTTERMS: "Payment Terms"
			PAYONRECEIPT: "Payment on Receipt"
			PAYVENDOR: "Pay To"
			PUNCHOUTENABLED: "Punchout Enabled"
			RBNIACC: "RBNI Account"
			SHIPVIA: "Ship Via"
			USEPARENTREMITTO: "Use Parent Remit To"
			VENDORSENDSASN: "Vendor Sends ASN"
			VENDORSENDSINV: "Vendor Sends Invoice"
			VENDORSENDSSTATUS: "Vendor Sends Orde		
			*/

		categoryToNameMap : { BASE        : IBM.LMV.Strings.CAT_BASE,
							  ADDRESS     : IBM.LMV.Strings.CAT_ADDRESS,
							  MODIFY      : IBM.LMV.Strings.CAT_MODIFY,
							  OTHER       : IBM.LMV.Strings.CAT_OTHER,
							},
				
		lableToNameMap : {
							LOCATION : null,
							PARENT   : null,
						 },
	
		getMbiId : function(
			mbo
		) {
			if( mbo == null ) return "";
			return mbo.PRODUCTID;
		},
	
		lookupMboByKey : function(
			panel, company, site, org
		) {
			var that = this;
			
			panel.mboLoadSemiphore = false;
			panel.currentMbo = null;
			panel.currentSpecs = null;
			var url = IBM.LMV.Auth.getRestURL();
			url = url + "/os/MXVENDOR";
			url = url + "?COMPANY=~eq~" + company + "&ORGID=~eq~" + org + "&_compact=1";
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { that.onProduct( panel, this ); };;
			xmlReq.open( "GET", url, true );
			IBM.LMV.Auth.setRequestHeaders( xmlReq );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
	
			xmlReq.send();
		},
		
		onProduct : function( panel, request )
		{
			var _self = this;
	
			if( request.readyState != 4 )  
			{ 
				return; 
			}
	  
			panel.fetcher          = this;
			if( request.status != 200 )
			{
				IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
				return;
			}
			
			var mboSet = JSON.parse( request.responseText );
			var companies = mboSet.QueryMXVENDORResponse.MXVENDORSet.COMPANIES;
			if( companies == null || companies.length == 0 )
			{
				panel.mboLoadSemiphore = true;
				panel.currentMbo = null;
				panel.currentSpecs = null;
				panel.showNoProperties();
				return;
			}
			panel.currentMbo = companies[0];
			IBM.LMV.dataDictionary.getLabels( "COMPANIES", function( mbo, labels ) { panel.onLabels(  mbo, labels ); } );
			panel.mboLoadSemiphore = true;
			panel.displayMbo();		
			panel.lookupAttachments( "COMPANIES", companies[0].COMPANIESID );		
		},
		
		makeTitle : function(
			mbo
		) {
			var name = "";
			var title = IBM.LMV.Strings.KEY_COMPANY_PROP_TITLE;
			if( mbo != null )
			{
				name = mbo.COMPANIES;
			}
			title = title.replace( "{0}",  name );
			return title;
		}
	};

IBM.LMV.MaximoPropertyFetchers.LOCATIONS = 
	{
		mboName     : "LOCATION",
		categoryMap : { 
					AUTOWOGEN     : "NONE",
					BIMIMPORTSRC  : "NONE",
					CHILDREN      : "NONE",
					HASCHILDREN   : "NONE",
					HASPARENT     : "NONE",
					LOCATIONSID   : "NONE",
					LOCATIONSPEC  : "NONE",
					MODELID       : "NONE",
					PLUSCPMEXTDATE: "NONE",
					SADDRESSCODE  : "NONE",
					TYPE          : "NONE",		// Always operating so no need to show

					DESCRIPTION   : "BASE",
					FAILURECODE   : "BASE",
					LOCATION      : "BASE",
					LOCPRIORITY   : "BASE",
					ORGID         : "BASE",
					PARENT        : "BASE",
					SITEID        : "BASE",
					STATUS        : "BASE",
					SYSTEMID      : "BASE",

					BIMAREAUNITS   : "FACILITY",
					BIMELEVATION   : "FACILITY",
					BIMGROSSAREA   : "FACILITY",
					BIMHEIGHT      : "FACILITY",
					BIMLINEARUNITS : "FACILITY",
					BIMNETAREA     : "FACILITY",
					BIMPERIMETER   : "FACILITY",
					BIMROOMNAME    : "FACILITY",
					BIMUSAGE       : "FACILITY",
					
					CHANGEBY      : "MODIFY",
					CHANGEDATE    : "MODIFY",
					STATUSDATE    : "MODIFY",
				},
		
		categoryToNameMap : { BASE     : IBM.LMV.Strings.CAT_BASE,
							  ADDRESS  : IBM.LMV.Strings.CAT_ADDRESS,
							  FACILITY : IBM.LMV.Strings.CAT_FACILITY,
							  MODIFY   : IBM.LMV.Strings.CAT_MODIFY,
							  OTHER    : IBM.LMV.Strings.CAT_OTHER,
							},
				
		lableToNameMap : {
							LOCATION : null,
							PARENT   : null,
						 },
	
		lookupMboByKey : function(
			panel, location, site
		) {
			var that = this;
			
			panel.mboLoadSemiphore = false;
			panel.currentMbo = null;
			panel.currentSpecs = null;
			var url = IBM.LMV.Auth.getRestURL();
			url = url + "/os/MXOPERLOC";
			url = url + "?LOCATION=~eq~" + location + "&SITEID=~eq~" + site + "&_compact=1";
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { that.onLocation( panel, this ); };;
			xmlReq.open( "GET", url, true );
			IBM.LMV.Auth.setRequestHeaders( xmlReq );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
	
			xmlReq.send();
		},
		
		getMbiId : function(
			mbo
		) {
			if( mbo == null ) return "";
			return mbo.LOCATION;
		},
		
		lookupMboByModelId : function(
			 panel, guid
		) {
			var that = this;
			
			this.mboLoadSemiphore = false;
			this.currentMbo = null;
			this.currentSpecs = null;
			if( guid == null || guid.length == 0 )
			{
				this.showMessage( IBM.LMV.Strings.ERR_NO_GUID );
				return;
			}
			var url = IBM.LMV.Auth.getRestURL();
			url = url + "/os/MXOPERLOC";
			url = url + "?MODELID=~eq~" + guid + "&_compact=1";
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { that.onLocation( panel, this ); };;
			xmlReq.open( "GET", url, true );
			IBM.LMV.Auth.setRequestHeaders( xmlReq );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
	
			xmlReq.send();
		},
	
		onLocation : function( panel, request )
		{
			var _self = this;
	
			if( request.readyState != 4 )  
			{ 
				return; 
			}
	  
			panel.fetcher          = this;
			if( request.status != 200 )
			{
				IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
				return;
			}
			
			var mboSet = JSON.parse( request.responseText );
			var locations = mboSet.QueryMXOPERLOCResponse.MXOPERLOCSet.LOCATIONS;
			if( locations == null || locations.length == 0 )
			{
				panel.mboLoadSemiphore = true;
				panel.currentMbo = null;
				panel.currentSpecs = null;
				panel.showNoProperties();
				return;
			}
			panel.currentSpecs = locations[0].LOCATIONSPEC;
			panel.currentMbo = locations[0];
			IBM.LMV.dataDictionary.getLabels( "LOCATIONS", function( mbo, labels ) { panel.onLabels(  mbo, labels ); } );
			panel.mboLoadSemiphore = true;
			panel.displayMbo();		
			panel.lookupAttachments( "LOCATIONS", locations[0].LOCATIONSID );		
		},
		
		makeTitle : function(
			mbo
		) {
			var name = "";
			var title = IBM.LMV.Strings.KEY_LOCATION_PROP_TITLE;
			if( mbo != null )
			{
				name = mbo.LOCATION;
			}
			title = title.replace( "{0}",  name );
			return title;
		}
	};
	

/**
  *****************************************************************************************************
  * Fetches data from the Product Mbo to populate the property panel
  *****************************************************************************************************
  */
IBM.LMV.MaximoPropertyFetchers.PRODUCT = 
	{
		mboName     : "BIMPRODUCT",
		categoryMap : { 
					BIMPRODUCTBASEID             : "NONE",
					BIMPRODUCTBASESPEC           : "NONE",
					BIMPRODUCTJOB                : "NONE",
					BIMPRODUCTPART               : "NONE",
					CREATEDBY                    : "NONE",
					CREATEDON                    : "NONE",
					DESIGNSPEC                   : "NONE",
					EQ01                         : "NONE",
					EQ02                         : "NONE",
					EQ03                         : "NONE",
					EQ4                          : "NONE",
					EQ5                          : "NONE",
					EQ6                          : "NONE",
					EQ7                          : "NONE",
					EQ8                          : "NONE",
					EQ9                          : "NONE",
					HASLD                        : "NONE",
					LANGCODE                     : "NONE",
					MODELID                      : "NONE",
					PRODUCTID                    : "NONE",
					
					DESCRIPTION                  : "BASE",
					DESCRIPTION_LONGDESCRIPTION  : "BASE",
					ITEMSETID                    : "BASE",
					ITEMNUM                      : "BASE",
					NAME                         : "BASE",
					ORGID                        : "BASE",

					MANUFACTURER                 : "DESCRIPTION",
					MODELNUMBER                  : "DESCRIPTION",
					BIMASSETTYPE                 : "DESCRIPTION",
					CATEGORY                     : "DESCRIPTION",

					NOMINALHEIGHT                : "DESCRIPTION",
					HEIGHTUNITS                  : "DESCRIPTION",
					
					NOMINALLENGTH                : "DESCRIPTION",
					LENGTHUNITS                  : "DESCRIPTION",
					
					NOMINALWEIGHT                : "DESCRIPTION",
					WEIGHTUNITS                  : "DESCRIPTION",
					
					NOMINALWIDTH                 : "DESCRIPTION",
					WIDTHUNITS                   : "DESCRIPTION",

					CHANGEBY                     : "MODIFY",
					CHANGEDATE                   : "MODIFY",
					STATUS                       : "MODIFY",
					STATUSDATE                   : "MODIFY",

					LABORWARRANTYDURATION        : "WARRANTY",
					LABORWARRANTYGUARANTOR       : "WARRANTY",
					PARTSWARRANTDURATION         : "WARRANTY",
					PARTSWARRANTYGUARANTOR       : "WARRANTY",
					WARRANTYCONTRACTNUM          : "WARRANTY",
					WARRANTYCONTRACTREV          : "WARRANTY",
					WARRANTYDESC                 : "WARRANTY",
					WARRANTYDESC_LONGDESCRIPTION : "WARRANTY",
					WARRANTYDURATIONUNIT         : "WARRANTY",
		},
		/*
		ACCESSIBILITYPERFORMANCE: "Accessibility Performance"
		CLASSSTRUCTUREID: "Class Structure"
		CODEPERFORMANCE: "Code Performance"
		COLOR: "Color"
		CONSTITUENTS: "Constituents"
		CURRENCYCODE: "Currency Units"
		DURATIONUNIT: "Duration Unit"
		EXPECTEDLIFE: "Expected Life"
		FEATURES: "Features"
		FEATURES_LONGDESCRIPTION: "Features Long description"
		FINISH: "Finish"
		GRADE: "Grade"
		MATERIAL: "Material"
		MODELREFERENCE: "Model Reference"
		PRODUCTSIZE: "Size"
		REPLACEMENTCOST: "Replacemnt Cost"
		SHAPE: "Shape"
		SUSTAINABILITYPERFORMANCE: "Sustainability Performance"
		*/

		categoryToNameMap : { BASE        : IBM.LMV.Strings.CAT_BASE,
							  DESCRIPTION :"Description",
							  MODIFY      : IBM.LMV.Strings.CAT_MODIFY,
							  WARRANTY    : "Warranty",
							  OTHER       : IBM.LMV.Strings.CAT_OTHER,
							},
				
		lableToNameMap : {
							LABORWARRANTYGUARANTOR       : null,
							PARTSWARRANTYGUARANTOR       : null,
							MANUFACTURER                 : null,
						 },
	
		// Merge unit and value fields
		fixupProperties : function(
			product
		) {
			if( product[ "NOMINALHEIGHT" ] )
			{
				product[ "NOMINALHEIGHT" ] = product[ "NOMINALHEIGHT" ] + " " + product[ "HEIGHTUNITS" ];
			}
			product[ "HEIGHTUNITS"  ] = null;

			if( product[ "NOMINALLENGTH" ] )
			{
				product[ "NOMINALLENGTH" ] = product[ "NOMINALLENGTH" ] + " " + product[ "LENGTHUNITS" ];
			}
			product[ "LENGTHUNITS" ] = null;

			if( product[ "NOMINALWEIGHT" ] )
			{
				product[ "NOMINALWEIGHT" ] = product[ "NOMINALWEIGHT" ] + " " + product[ "WEIGHTUNITS" ];
			}
			product[ "WEIGHTUNITS" ] = null;

			if( product[ "NOMINALWIDTH" ] )
			{
				product[ "NOMINALWIDTH" ] = product[ "NOMINALWIDTH" ] + " " + product[ "WIDTHUNITS" ];
			}
			product[ "WIDTHUNITS" ] = null;

			if( product[ "LABORWARRANTYDURATION" ] )
			{
				product[ "LABORWARRANTYDURATION" ] = product[ "LABORWARRANTYDURATION" ] + " " + product[ "WARRANTYDURATIONUNIT" ];
			}

			if( product[ "PARTSWARRANTDURATION" ] )
			{
				product[ "PARTSWARRANTDURATION" ] = product[ "PARTSWARRANTDURATION" ] + " " + product[ "WARRANTYDURATIONUNIT" ];
			}
			product[ "WARRANTYDURATIONUNIT" ] = null;

			return product;
		},
		
		getMbiId : function(
			mbo
		) {
			if( mbo == null ) return "";
			return mbo.PRODUCTID;
		},
	
		lookupMboByKey : function(
			panel, productId, site, org
		) {
			var that = this;
			
			panel.mboLoadSemiphore = false;
			panel.currentMbo = null;
			panel.currentSpecs = null;
			var url = IBM.LMV.Auth.getRestURL();
			url = url + "/os/MXPRODUCT";
			url = url + "?PRODUCTID=~eq~" + productId + "&ORGID=~eq~" + org + "&_compact=1";
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { that.onProduct( panel, this ); };;
			xmlReq.open( "GET", url, true );
			IBM.LMV.Auth.setRequestHeaders( xmlReq );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
	
			xmlReq.send();
		},
		
		onProduct : function( panel, request )
		{
			var _self = this;
	
			if( request.readyState != 4 )  
			{ 
				return; 
			}
	  
			panel.fetcher          = this;
			if( request.status != 200 )
			{
				IBM.LMV.RESTError( IBM.LMV.Strings.ERR_REST,  request.status, request.responseText );
				return;
			}
			
			var mboSet = JSON.parse( request.responseText );
			var products = mboSet.QueryMXPRODUCTResponse.MXPRODUCTSet.BIMPRODUCT;
			if( products == null || products.length == 0 )
			{
				panel.mboLoadSemiphore = true;
				panel.currentMbo = null;
				panel.currentSpecs = null;
				panel.showNoProperties();
				return;
			}
			panel.currentSpecs = products[0].BIMPRODUCTBASESPEC;
			panel.currentMbo = this.fixupProperties( products[0] );
			IBM.LMV.dataDictionary.getLabels( "BIMPRODUCT", function( mbo, labels ) { panel.onLabels(  mbo, labels ); } );
			panel.mboLoadSemiphore = true;
			panel.displayMbo();		
			panel.lookupAttachments( "BIMPRODUCT", products[0].BIMPRODUCTBASEID );		
		},
		
		makeTitle : function(
			mbo
		) {
			var name = "";
			var title = IBM.LMV.Strings.KEY_PRODUCT_PROP_TITLE;
			if( mbo != null )
			{
				name = mbo.NAME;
			}
			title = title.replace( "{0}",  name );
			return title;
		}
	};

/**
  *****************************************************************************************************
  * Fetches data from the WOrk Order Mbo to populate the property panel
  *****************************************************************************************************
  */
IBM.LMV.MaximoPropertyFetchers.WORKORDER = 
	{
		mboName     : "WORKORDER",
		categoryMap : { 
					WORKORDERID                  : "NONE",

					WOLOC                        : "BASE",
					WONUM                        : "BASE",
					WOPRIORITY                   : "BASE",
					WORKLOCATION                 : "BASE",
					ASSETNUM                     : "BASE",
					WORKTYPE                     : "BASE",
					DESCRIPTION                  : "BASE",
					DESCRIPTION_LONGDESCRIPTION  : "BASE",

					CHANGEBY                     : "MODIFY",
					CHANGEBYPARENT               : "MODIFY",
					CHANGEDATE                   : "MODIFY",
					STATUS                       : "MODIFY",
					STATUSDATE                   : "MODIFY",
			},


		categoryToNameMap : { BASE        : IBM.LMV.Strings.CAT_BASE,
							  MODIFY      : IBM.LMV.Strings.CAT_MODIFY,
							  OTHER       : IBM.LMV.Strings.CAT_OTHER,
							},
				
		lableToNameMap : {
							LOCATION : null,
							ASSETNUM : null,
						 },
	
		displayWorkOrder : function(
			panel, wo
		) {
			if( wo == null || wo.length == 0 )
			{
				panel.mboLoadSemiphore = true;
				panel.currentMbo = null;
				panel.currentSpecs = null;
				panel.showNoProperties();
				return;
			}
			panel.currentMbo = wo[0];
			IBM.LMV.dataDictionary.getLabels( "WORKORDER", function( mbo, labels ) { panel.onLabels(  mbo, labels ); } );
			panel.mboLoadSemiphore = true;
			panel.displayMbo();		
			panel.lookupAttachments( "WORKORDER", wo[0].WORKORDERID );		
			this.lookupMarkup( panel, wo );
		}, 
		
		getMbiId : function(
			mbo
		) {
			if( mbo == null ) return "";
			return mbo.PRODUCTID;
		},
		
		lookupMarkup : function(
			panel, wo
		) {
			var _self = this;
	
			var url = IBM.LMV.Auth.getRestURL();
			url = url + "/mbo/bimlmvworkview" ;
			url = url +  "?BUILDINGMODELID=~eq~" + IBM.LMV.modelId;
			url = url +  "&wonum=~eq~"           + wo[0].WONUM;
			url = url +  "&siteId=~eq~"          + IBM.LMV.siteId;
			url = url + "&_compact=1";
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { _self.onMarkup( panel, this ); };
			xmlReq.open( "GET", url, true );
			IBM.LMV.Auth.setRequestHeaders( xmlReq );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
	
			xmlReq.send();
		},

	
		lookupMboByKey : function(
			panel, wo, site, org
		) {
			var that = this;
			
			panel.mboLoadSemiphore = false;
			panel.currentMbo = null;
			panel.currentSpecs = null;
			var url = IBM.LMV.Auth.getRestURL();
			url = url + "/os/MXWODETAIL";
			url = url + "?WONUM=~eq~" + wo + "&SITEID=~eq~" + site  + "&ORGID=~eq~" + org + "&_compact=1";
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { that.onWorkOrder( panel, this ); };;
			xmlReq.open( "GET", url, true );
			IBM.LMV.Auth.setRequestHeaders( xmlReq );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
	
			xmlReq.send();
		},
		
		onMarkup : function( panel, request )
		{
			if( request.readyState != 4 )  
			{ 
				return; 
			}
	
			if( request.status != 200 )
			{
				IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
				this.uninitialize();
				return;
			}
	
			var mboSet = JSON.parse( request.responseText );
			var viewList = mboSet.BIMLMVWORKVIEWMboSet.BIMLMVWORKVIEW;
			if( viewList.length > 0 )
			{
				panel.markUpBtn.className = "maxlmv_propertyButton";
			}
		},

		onWorkOrder : function( panel, request )
		{
			var _self = this;
	
			if( request.readyState != 4 )  
			{ 
				return; 
			}
	  
			panel.fetcher          = this;
			if( request.status != 200 )
			{
				IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
				return;
			}
			
			var mboSet = JSON.parse( request.responseText );
			var wo = mboSet.QueryMXVENDORResponse.MXVENDORSet.COMPANIES;
			this.displayWorkOrder( panel, wo );
		},
		
		makeTitle : function(
			mbo
		) {
			var name = "";
			var title = IBM.LMV.Strings.KEY_WO_PROP_TITLE;
			if( mbo != null )
			{
				name = mbo.WONUM;
			}
			title = title.replace( "{0}",  name );
			return title;
		}
	};

/**
  *****************************************************************************************************
  * Base property pannel for displaying Maximo Mbo data including associated specifications
  *****************************************************************************************************
  */
IBM.LMV.MaximoPropertyPanel = function (
	parent, forgeViewer
) {
	this.forgeViewer        = forgeViewer;
	this.viewer             = forgeViewer.viewer;
	this.parent             = parent;
	this.mboLoadSemiphore   = false;		// Semiphore to synchonize load of labels and mbo data

	// 	Current data being viewed
	this.mboName            = null;
	this.currentMbo         = null;
	this.currentSpecs       = null;
	this.currentAttachments = null;
	this.labels             = null;
	this.fetcher            = null;

	// Specific to the type of Mbo viewwed and set by subclass
	this.categoryMap        = {};
	this.categoryToNameMap  = {};
	this.lableToNameMap     = {};

	// Viewer is updated as the selection changes	
    this.isDirty            = true;
	this.currentSelection   = this.forgeViewer.getSelectionList();
	this.baseMbo            = "ASSET";	// Mbo viewer orinially launched with.  Returns to thsi on selection change.
	
	this.attachmentDlg      = null;

	// As references are followed to related Mbo, the same viewer is used similare to a browser.  The previous
	// mbo is pushed onto the stack and new data is displyed.  The previous button pops one level off the stack
	// Changing the selection clears the stack and displays data for the new selection
	this.viewStack         = [];
	
	this.fieldToMboMap = {
			LOCATION                     : "LOCATIONS",
			PARENT                       : "LOCATIONS",
			PRODUCTID                    : "PRODUCT",
			LABORWARRANTYGUARANTOR       : "COMPANIES",
			PARTSWARRANTYGUARANTOR       : "COMPANIES",
			MANUFACTURER                 : "COMPANIES",
		};
	
	this.displayMbo = function() 
	{
		if( this.labels == null || !this.mboLoadSemiphore )
		{
			return;
		}
		var mbo = this.currentMbo;
		if( mbo == null )
		{
			this.showNoProperties();
			this.setTitle( this.fetcher.makeTitle() );
			this.resizeToContent();
			return;
		}
		var properties = [];
		for( var catToAdd in this.fetcher.categoryToNameMap )
		{
			this.makePropertyList( mbo, catToAdd, properties, this.labels );
		}
		
		this.setProperties( properties );
  		
		// If there are specifications, fetch the (Possible cached) labels, then add to list
		if( this.currentSpecs )
		{
			var _self = this;
			IBM.LMV.dataDictionary.getSpecifications( function( specMap ) { _self.onSpecifications(  specMap ); } );
		}
		this.setTitle( this.fetcher.makeTitle( mbo ) );
		this.resizeToContent();
	};

	this.displaySpecifications = function(
		specMap
	) {
		if( this.currentSpecs == null  )
		{
			return;
		}
		
		var specs = this.currentSpecs;
		for( var j = 0; j < specs.length; j++ )
		{
			var value = "";
			if( specs[j].ALNVALUE )
			{
				value = specs[j].ALNVALUE;
			}
			else if( specs[j].NUMVALUE )
			{
				value = specs[j].NUMVALUE;
			}
			else
			{
				continue;
			}
			var name = specMap[ specs[j].ASSETATTRID ].title;
			if( specMap[ specs[j].ASSETATTRID ].units )
			{
				value = value + " "  + specMap[ specs[j].ASSETATTRID ].units;
			}
			this.addProperty( name, value, IBM.LMV.Strings.CAT_SPEC );
		}
	};  

	this.lookupAttachments = function(
		ownerTable, ownerId
	) {
		var _self = this;
		
		this.currentAttachments = null;
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/DOCLINKS";
		url = url + "?OWNERTABLE=~eq~" + ownerTable + "&OWNERID=~eq~" + ownerId + "&_compact=1";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onAttachments( this ); };;
		xmlReq.open( "GET", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	};

	this.onAttachments = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}
  
		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
			return;
		}
		
		var mboSet = JSON.parse( request.responseText );
		this.currentAttachments = mboSet.DOCLINKSMboSet.DOCLINKS;
		if( this.currentAttachments == null || this.currentAttachments.length == 0 )
		{
			this.attacheBtn.className = "maxlmv_propertyButton_hidden";
		}
		else
		{
			this.attacheBtn.className = "maxlmv_propertyButton";
		}
		if( this.attachmentDlg )
		{
			this.attachmentDlg.displayAttachements( this.currentAttachments, 
			                                        this.fetcher.getMbiId( this.currentMbo ) );
		}
	};
	
	this.onAttachmetButton = function()
	{
		if( this.attachmentDlg == null )
		{
			this.attachmentDlg = new IBM.LMV.AttachmentDialog( this.parent );
		}
		this.attachmentDlg.displayAttachements( this.currentAttachments, 
												this.fetcher.getMbiId( this.currentMbo ) );
		this.attachmentDlg.setVisible( true );
	}

	this.onMarkupBtn = function()
	{
		if( !this.currentMbo || !this.currentMbo.WONUM ) return;
		
		if( !IBM.LMV.markupMgr )
		{
			IBM.LMV.markupMgr = new IBM.LMV.Markup.MarkupMgr();
		}
		this.showMarkupDlg = new IBM.LMV.Markup.ShowDlg( IBM.LMV.markupMgr, this.forgeViewer.viewer, IBM.LMV.PATH_IMAGES, this.currentMbo.WONUM );
		this.showMarkupDlg.setVisible( true );
	}

	this.onLabels = function(
 		mbo, labels
 	) {
		for( var attrib in this.fetcher.lableToNameMap )
		{
			if( labels[ attrib ] )
			{
				this.lableToNameMap[ attrib ] = labels[ attrib ];
			}
		}
		this.labels = labels;
		this.displayMbo();				
	}

	// Clammback when spcification name map is loaded
	this.onSpecifications = function( 
		specMap 
	) {
		this.displaySpecifications(  specMap );
		this.resizeToContent();
	};

	this.makePropertyList = function( 
		mbo, 
		categoryToAdd, 
		properties,
		labels
	) {
		for( var name in mbo ) 
		{
			if( !mbo.hasOwnProperty(name)) continue;    //Skip inherited properties

			var value = mbo[name];
			if( value == null || value == "" || value == 0 )
			{
				continue;
			}
			
			var title    = labels[name];
			var category = this.fetcher.categoryMap[name];
			
			if( !category ) category = "OTHER";
			if( category != categoryToAdd ) continue;
			
			var property = {};
			property.displayName     = title;
			property.displayValue    = value;
			property.assetProp       = name;
			if( this.categoryToNameMap[ category ] )
			{
				property.displayCategory = this.categoryToNameMap[ category ];
			}
			else
			{
				property.displayCategory = category;
			}
			properties.push( property );
		}
	};
	
	this.onPrevButton = function(
		ctrl
	) {
		if( this.viewStack == null || this.viewStack.length == 0 )
		{
			return;
		}
		var state = this.viewStack.pop();
		state.restore( this );
	};
	
	// Oversite in subclass
	this.makeTitle = function(
		mbo
	) {
		return "";
	}
	
	this.reset = function(
		mboName
	) {
		this.mboName             = mboName;	// Name, and therefor data type of the Mbo being displayed
		this.currentMbo          = null;
		this.currentSpecs        = null;
		this.currentAttachments  = null;
		this.labels              = null;
		this.viewStack           = [];
		this.isDirty             = true;
		this.prevBtn.className   = "maxlmv_propertyButton_disabled";
		this.markUpBtn.className = "maxlmv_propertyButton_hidden";
		
		if( this.showMarkupDlg )
		{
			try
			{
				var dlg = this.showMarkupDlg;
				this.showMarkupDlg = null;
				dlg.setVisible( false );
			}
			catch( e ) {}		// Might alraedy be closed
		}

		
		if( IBM.LMV.MaximoPropertyFetchers[ mboName ] )
		{
			this.fetcher           = IBM.LMV.MaximoPropertyFetchers[ mboName ];
			this.categoryMap       = this.fetcher.categoryMap;
			this.categoryToNameMap = this.fetcher.categoryToNameMap;
			this.lableToNameMap    = this.fetcher.lableToNameMap
		}
		else
		{
			this.categoryMap       = {};
			this.categoryToNameMap = {};
			this.lableToNameMap    = {};
		}
	}

	this.showMessage = function (
		message
	) {
		this.removeAllProperties();
		var rootContainer = this.tree.myRootContainer;
	
		var messageDiv = document.createElement('div');
		messageDiv.className = 'noProperties';
	
		messageDiv.textContent = message;
	
		rootContainer.appendChild( messageDiv );
	};

    Autodesk.Viewing.UI.PropertyPanel.call(this, parent, 'Maximo-" + mboName + "PropertyPanel', IBM.LMV.Strings.KEY_PROP_LOADIN_MSG );
}

IBM.LMV.MaximoPropertyPanel.prototype = Object.create( Autodesk.Viewing.UI.PropertyPanel.prototype );
IBM.LMV.MaximoPropertyPanel.prototype.constructor = IBM.LMV.MaximoPropertyPanel;

/**
 * Override so that the panel is updated with the currently selected node's properties,
 * and that default properties are loaded when the model is first loaded.
 */
IBM.LMV.MaximoPropertyPanel.prototype.initialize = function () 
{
    Autodesk.Viewing.UI.PropertyPanel.prototype.initialize.call(this);
	
	this.container.style.zIndex = 400;

    var _self = this;

	if( this.viewer != null )
	{
		this.forgeViewer.addSelectionListener( function( selection ) 
		{
			_self.currentSelection = selection;
			_self.isDirty = true;
			if( _self.isVisible() )
			{
				_self.reset( _self.baseMbo );
				_self.requestProperties();
			}
		});

		this.forgeViewer.addFocusListener( function( index ) 
		{
			_self.isDirty = true;
			if( _self.isVisible() )
			{
				_self.reset( _self.baseMbo );
				_self.requestProperties();
			}
		});
	}
};

IBM.LMV.MaximoPropertyPanel.prototype.uninitialize = function() {
	Autodesk.Viewing.UI.PropertyPanel.prototype.uninitialize.call(this);
	this.viewer = null;
};  
 
/**
 * Overload to set visual indcators for hot items
 */ 
IBM.LMV.MaximoPropertyPanel.prototype.addProperty = function(
	name, value, category, options
) {
	Autodesk.Viewing.UI.PropertyPanel.prototype.addProperty.call( this, name, value, category, options );
	var attrib = null;
		for( var mbo in this.lableToNameMap )
	{
		if( this.lableToNameMap[ mbo ] == name )
		{
			attrib = name;
			break;
		} 
	}

	if( attrib )
	{
	    var element = this.tree.getElementForNode({name: name, value: value, category: category});
		if( element )
		{
//			element.className = element.className + " maxlmv_PropertyLink";
			element.style.textDecoration = "underline";
			element.style.fontStyle      = "italics";
			element.style.color          = "CFF";
		}
	}
};


IBM.LMV.MaximoPropertyPanel.prototype.createTitleBar = function(
	title
) {
	var _self = this;
	var ctrlBar       = document.createElement("DIV");
	ctrlBar.name      = "maxlmv-PropertyPanel-Prev";
	ctrlBar.className = "maxlmv_propertyButtonPrev";

	this.prevBtn           = document.createElement("IMG");
	this.prevBtn.src       = IBM.LMV.PATH_IMAGES + "/tb_previous.png";
	this.prevBtn.alt       = IBM.LMV.Strings.KEY_PREV_PAGE;
	this.prevBtn.title     = IBM.LMV.Strings.KEY_PREV_PAGE;
	this.prevBtn.className = "maxlmv_propertyButton_disabled";
	this.prevBtn.onclick   = function() { _self.onPrevButton( this ); };
	ctrlBar.appendChild( this.prevBtn );
	
	this.container.appendChild( ctrlBar );

	ctrlBar           = document.createElement("DIV");
	ctrlBar.name      = "maxlmv-PropertyPanel-Attach";
	ctrlBar.className = "maxlmv_propertyButtonAttach";

	this.attacheBtn           = document.createElement("IMG");
	this.attacheBtn.src       = IBM.LMV.PATH_IMAGES + "/img_attach.gif";
	this.attacheBtn.alt       = IBM.LMV.Strings.KEY_ATTACHMENT;
	this.attacheBtn.title     = IBM.LMV.Strings.KEY_ATTACHMENT;
	this.attacheBtn.className = "maxlmv_propertyButton_disabled";
	this.attacheBtn.onclick   = function() { _self.onAttachmetButton( this ); };
	ctrlBar.appendChild( this.attacheBtn );
	
	this.container.appendChild( ctrlBar );

	ctrlBar           = document.createElement("DIV");
	ctrlBar.name      = "maxlmv-PropertyPanel-Markup";
	ctrlBar.className = "maxlmv_propertyButtonMarkup";

	this.markUpBtn           = document.createElement("IMG");
	this.markUpBtn.src       = IBM.LMV.PATH_IMAGES + "/360_redline_show.png";
	this.markUpBtn.alt       = IBM.LMV.Strings.KEY_ATTACHMENT;
	this.markUpBtn.title     = IBM.LMV.Strings.KEY_ATTACHMENT;
	this.markUpBtn.className = "maxlmv_propertyButton_hidden";
	this.markUpBtn.onclick   = function() { _self.onMarkupBtn( this ); };
	ctrlBar.appendChild( this.markUpBtn );
	
	this.container.appendChild( ctrlBar );
	this.titleBar = Autodesk.Viewing.UI.PropertyPanel.prototype.createTitleBar.call( this, title );
	this.titleBar.className = "maxlmv_propertyTitle";
	return this.titleBar;
};

IBM.LMV.MaximoPropertyPanel.prototype.setTitle = function(
	title,	options
) {
	if( !title ) 
	{
		title = this.title;;
		options = options || {};
		options.localizeTitle = false;
	}
	Autodesk.Viewing.UI.PropertyPanel.prototype.setTitle.call( this, title, options);
};


IBM.LMV.MaximoPropertyPanel.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	
	if( show )
	{
		this.resizeToContent();
	}
	else
	{
		if( this.attachmentDlg )
		{
			this.attachmentDlg.setVisible( false );
		}
	}
};

/**
 * Override so that collapsing and expanding a category using the category icon also
 * resizes the panel.
 */
IBM.LMV.MaximoPropertyPanel.prototype.onCategoryIconClick = function(
	category, 
	event
) {
	Autodesk.Viewing.UI.PropertyPanel.prototype.onCategoryIconClick.call(this, category, event);
	var dimensions = this.viewer.getDimensions();
	this.resizeToContent(
		{
			maxHeight : dimensions.height
		}
	);
};

// Drilldown into ralated Mbos
// drilldown field is ected to have the key of the related Mbo
IBM.LMV.MaximoPropertyPanel.prototype.onPropertyClick = function(
	property, event
) {
	var attrib = null;
	for( var name in this.lableToNameMap )
	{
		if( this.lableToNameMap[ name ] == property.name )
		{
			attrib = name;
			break;
		}
	}
	var mbo = this.fieldToMboMap[ attrib ];
	if( mbo && this.currentMbo )
	{
		var fetcher = IBM.LMV.MaximoPropertyFetchers[ mbo ];
		if( fetcher )
		{
			var state = new IBM.LMV.PropertyPanelState( this );
			fetcher.lookupMboByKey( this, property.value, this.currentMbo.SITEID, this.currentMbo.ORGID );
			this.setVisible( true );
		}
	}
};

IBM.LMV.MaximoPropertyPanel.prototype.requestProperties = function() {
	if( this.isVisible() && this.isDirty ) 
	{
		if( this.currentSelection  == null || this.currentSelection.length == 0 )
		{
			this.showNoProperties();
			return;
		}
		if( this.fetcher.lookupMboByModelId == null )
		{
			this.showNoProperties();
			return;
		}
		this.fetcher.lookupMboByModelId( this, this.forgeViewer.getSelection().guid );
		this.isDirty = false;
	}
};

IBM.LMV.MaximoPropertyPanel.prototype.showNoProperties = function() 
{
	Autodesk.Viewing.UI.PropertyPanel.prototype.showNoProperties.call(this);
	this.setTitle( this.fetcher.makeTitle() );
	this.resizeToContent();
};

IBM.LMV.PropertyPanelState = function(
	popertyPanel
) {
	this.mboName            = popertyPanel.mboName;
	this.currentMbo         = popertyPanel.currentMbo;
	this.currentSpecs       = popertyPanel.currentSpecs;
	this.currentAttachments = popertyPanel.currentAttachments;
	this.labels             = popertyPanel.labels;
	this.fetcher            = popertyPanel.fetcher;

	popertyPanel.viewStack.push( this );;
	popertyPanel.prevBtn.className = "maxlmv_propertyButton";	
	
	this.restore = function(
		popertyPanel
	) {
		popertyPanel.mboName            = this.mboName;
		popertyPanel.currentMbo         = this.currentMbo;
		popertyPanel.currentSpecs       = this.currentSpecs;
		popertyPanel.currentAttachments = this.currentAttachments;
		popertyPanel.labels             = this.labels;
		popertyPanel.fetcher            = this.fetcher;
		popertyPanel.displayMbo();
		if( popertyPanel.currentAttachments == null || popertyPanel.currentAttachments.length == 0 )
		{
			popertyPanel.attacheBtn.className = "maxlmv_propertyButton_hidden";
		}
		else
		{
			popertyPanel.attacheBtn.className = "maxlmv_propertyButton";
		}
		if( popertyPanel.viewStack == null || popertyPanel.viewStack.length == 0 )
		{
			popertyPanel.prevBtn.className = "maxlmv_propertyButton_disabled";
		}
		if( popertyPanel.attachmentDlg )
		{
			popertyPanel.attachmentDlg.displayAttachements( popertyPanel.currentAttachments, 
			                                                popertyPanel.fetcher.getMbiId( popertyPanel.currentMbo ) );
		}
	}
}
		
/**
  *****************************************************************************************************
  * Attachment dialog
  *****************************************************************************************************
  */
IBM.LMV.AttachmentDialog = function(
	parent
) {
	this.parent       = parent; 
	this.firstDisplay = true;		// Center the first iem its is displayed
	this.resize       = false;
	
	// Seperate method to provide closure for the attachement links
	this.addLink = function(
		ref, link
	) {
		ref.onclick   = function( evt ) { window.open( link ); };
	}

	this.displayAttachements = function(
		attachments, mboId
	) {
		this.makeTable( attachments );
		if( !mboId ) mboId = "";
		var title = IBM.LMV.Strings.KEY_ATTACHMENT.replace( "{0}", mboId );
		this.setTitle( title );
		if( this.isVisible() )
		{
			this.resizeContainer();
		}
		else
		{
			this.resize = true;
		}
	}
	
	// Make table with list of attachments
	this.makeTable = function(
		attachments
	) {
//		this.container.style.height    = "auto";
		if( this.table != null )
		{
			this.table.parentNode.removeChild( this.table );
			this.scrollContainer.style.height = 0;		// Need to jiggle the heigh to get the container to resize
		}
		
		this.table = document.createElement("TABLE");
		this.table.className = "maxlmv_DlgTable";

		for( var i = 0; i < attachments.length; i++ )
		{
			var attach = attachments[i];
			if( attach.WEBURL == null || attach.WEBURL.length == 0 )
			{
				continue;
			}
			var row = this.table.insertRow( i );	// Skip heading and filter
			row.className   = "maxlmv_selectable";
			var cell = row.insertCell( 0 );
			cell.className = "maxlmv_DlgText";

			var ref = document.createElement("A");
			ref.className = "maxlmv_DlgProperties";
			this.addLink( ref, attach.WEBURL );
			if( attach.DOCUMENT != null && attach.DOCUMENT.length > 0 )
			{
				ref.innerHTML = attach.DOCUMENT;
			}
			else
			{
				ref.innerHTML = attach.DESCRIPTION;
			}
			cell.appendChild( ref );
		}
		this.scrollContainer.appendChild( this.table );
	}  

	this.possitionOnScreen = function()
	{
		if( this.firstDisplay )
		{
			this.firstDisplay = false;
		
			// Center Top bottom
			var top = ( window.innerHeight  - this.container.clientHeight ) / 2;
			if( top < 0 ) top = 0;
			this.container.style.top = "" + top + "px";

			// Center left right
			var left = ( window.innerWidth  - this.container.clientWidth ) / 2;
			if( left < 0 ) left = 0;
			this.container.style.left = "" + left + "px";
		}

		// Insure atleast the title bar is on screen
		if( this.container.clientHeight > window.innerHeight - 50 )
		{
			this.container.style.height = "" + (window.innerHeight - 50) + "px";
			this.container.style.top = 0;
		}
		if( this.container.offsetTop < 0 )
		{
			this.container.style.top = 0;
		}
		if( this.container.offsetTop > window.innerHeight - 50 )
		{
			this.container.style.top = "" + (window.innerHeight - 50) + "px";
		}
	}
	
	this.resizeContainer = function()
	{
		this.scrollContainer.style.height = 0;		// Need to jiggle the heigh to get the container to resize
		if( this.table.clientHeight < window.innerHeight - 125 )
		{
			this.scrollContainer.style.height = "" + this.table.clientHeight + "px";;
		}
		else
		{
			this.scrollContainer.style.height = "" + (window.innerHeight - 125) + "px";;
		}
	}
	
  
    Autodesk.Viewing.UI.DockingPanel.call( this, parent, 'Maximo-" + ViewAttachmentsDlg', IBM.LMV.Strings.KEY_ATTACHMENT );
};
  
IBM.LMV.AttachmentDialog.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.AttachmentDialog.prototype.constructor = IBM.LMV.AttachmentDialog;

/**
 * Override so that the panel is updated with the currently selected node's properties,
 * and that default properties are loaded when the model is first loaded.
 */
IBM.LMV.AttachmentDialog.prototype.initialize = function () 
{
   Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call(this);
	this.container.style.zIndex = 500;
	this.container.style.height = "auto";
	
	this.createScrollContainer( { heightAdjustment : 80 } );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.dockRight = false;

	var btn       = document.createElement("DIV");
	btn.style.cssFloat = "right";
	btn.className = "maxlmv_DlgBoxButton";
	btn.innerHTML = IBM.LMV.Strings.DLG_BTN_CLOSE;
	this.initializeCloseHandler( btn );
	this.container.appendChild( btn );
};

IBM.LMV.AttachmentDialog.prototype.setVisible = function(
	show
) {
	this.container.dockRight = false;
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		this.container.style.maxHeight = "calc( 100% - 50px )";
		this.scrollContainer.style.maxHeight = "calc( 100% - 80px )";
		if( this.resize )
		{
			this.resizeContainer();
			this.resize = false;
		}
		this.possitionOnScreen();
	}
}