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

IBM.createNS( "IBM.LMV" );

IBM.LMV.toolBar           = null;

if( !IBM.LMV.Strings  ) IBM.LMV.Strings = {};

/**
  * The values in the string table are over written by consuming application with values for the correct locale
  */
IBM.LMV.Strings[ "TOOLBAR_HIDE_SELECTION" ]   = "Hide selection";
IBM.LMV.Strings[ "TOOLBAR_ISOLATE_SELECTION" ]= "Isolate Selection";
IBM.LMV.Strings[ "TOOLBAR_SEARCH" ]           = "Search";
IBM.LMV.Strings[ "TOOLBAR_SELECTION_MODE" ]   = "Set selection mode";
IBM.LMV.Strings[ "TOOLBAR_ZOOM_MODEL" ]       = "Zoom to model";

IBM.LMV.Strings[ "DLG_TITLE_SAVE_VIEW" ]      = "Save Current View";
IBM.LMV.Strings[ "DLG_LABEL_LOCATION" ]       = "Building";
IBM.LMV.Strings[ "DLG_LABEL_CREATOR" ]        = "Creator";
IBM.LMV.Strings[ "DLG_LABEL_LONG_DESC" ]      = "Details";
IBM.LMV.Strings[ "DLG_LABEL_DESCRIPTION" ]    = "Description";
IBM.LMV.Strings[ "DLG_LABEL_SAVE_VIEW" ]      = "Save View";
IBM.LMV.Strings[ "DLG_LABEL_APPLY_VIEW" ]     = "Apply Saved View";
IBM.LMV.Strings[ "DLG_LABEL_PUBLIC" ]         = "Public";
IBM.LMV.Strings[ "DLG_LABEL_OWNER" ]          = "Owner";


IBM.LMV.Strings[ "MSG_TITLE_REQUIRED_FIELD" ] = "Required Field Missing %1";
IBM.LMV.Strings[ "DLG_TITLE_APPLY_VIEW" ]     = "Select View to Restore";
IBM.LMV.Strings[ "DLG_TXT_DELETE_VIEW" ]      = "Delete saved view {0}";
IBM.LMV.Strings[ "DLG_BTN_DELETE_VIEW" ]      = "Delete Saved View";     

/*
IBM.LMV.Strings[ "DLG_TT_SAVE_VIEW" ]         = "Save View...";
IBM.LMV.Strings[ "DLG_TT_APPLY_VIEW" ]        = "Apply Saved View...";


IBM.LMV.Strings[ "ERR_NO_GUID" ]              = "The selected item doesn't have a unique identifier.  It may be necessary to define a binding in the Manage Building Models application";
*/

IBM.LMV.Strings[ "ERR_REST" ]                 = "REST Error";  

IBM.LMV.Strings[ "KEY_DISABLE_AUTO_ZOOM" ]    = "Disable Auto Zoom";  
IBM.LMV.Strings[ "KEY_ENABLE_AUTO_ZOOM" ]     = "Enable Auto Zoom";  

IBM.LMV.Strings[ "SELECT" ]                   = "Selection Tools";  
IBM.LMV.Strings[ "TRIRIGA" ]                  = "TRIRIGA Tools";  

/**
 *****************************************************************************************************
 * Toolbar
 *****************************************************************************************************
 */

var toolBar = IBM.createNS( "IBM.LMV.ToolBar" );

toolBar.onToolbarCreate    = null;		// Called after the toolbar is created but before it is displayed
										// Intended to allow consuming applications to add items to the 
										// toolbar.

toolBar.ID_TOOLBAR_GROUP   = "TRIIRGA_toolbar_group";
toolBar.ID_TOOLBAR_SELECT  = "TRIIRGA_toolbar_submenu_select"
toolBar.ID_TOOLBAR_TRIIRGA  = "TRIIRGA_toolbar_submenu";


IBM.LMV.toolBarExtension = function(
	forgeViewer, wrapper
) {
	const _forgeViewer      = forgeViewer;
	const _wrapper          = wrapper;
	this.markupMgr          = wrapper.markupMgr;
		
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
		var features = _wrapper.features;
		if( !features ) features = {};

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
		var buttonFitModel = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_button_fit_model");
		buttonFitModel.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_expandToFit.png" + ")";
		buttonFitModel.setToolTip( IBM.LMV.Strings.TOOLBAR_ZOOM_MODEL );
		buttonFitModel.onClick = function() { _forgeViewer.viewer.navigation.fitBounds(true, _forgeViewer.viewer.model.getBoundingBox());};
		var options = new Object();
		options.index = 4;
		cameraSubMenu.subMenu.addControl( buttonFitModel, options );
	
		var buttonFitToView = cameraSubMenu.subMenu.getControl( "toolbar-fitToViewTool" );
		buttonFitToView.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_zoomToSelected.png" + ")";
	
		// Add seach entry to the TRIIRGA toolbar	
		maximoSubToolbar.addControl( this.makeSeachControl() );
	
		// Add a sub-menu to the TRIIRGA toolbar to manage selection related options
		var submenuMaximoSelect = new Autodesk.Viewing.UI.ComboButton( IBM.LMV.ToolBar.ID_TOOLBAR_SELECT );
		submenuMaximoSelect.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_select.png" + ")";
		submenuMaximoSelect.setToolTip( IBM.LMV.Strings.SELECT );
		maximoSubToolbar.addControl( submenuMaximoSelect );
		
		if( features.multiselect )
		{
			// Button to toggle between single and multi-select modes
			this.buttonSelect = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_button_single_selection");
			this.buttonSelect.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_selectSingle.png" + ")";
			this.buttonSelect.setToolTip( IBM.LMV.Strings.TOOLBAR_SELECTION_MODE );
			this.buttonSelect.onClick = function( evt ) { _self.setSelectMode( evt );};
		}
		
		// Add Hidel Selection button
		var buttonHideSelection = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_button_hide_selection");
		buttonHideSelection.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "hide_selection.png" + ")";
		buttonHideSelection.setToolTip( IBM.LMV.Strings.TOOLBAR_HIDE_SELECTION );
		buttonHideSelection.onClick = function() { _self.doHideSelection();};
		
		var buttonIsolateSelection = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_button_isolate_selection");
		buttonIsolateSelection.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_isolateSelected.png" + ")";
		buttonIsolateSelection.setToolTip( IBM.LMV.Strings.TOOLBAR_ISOLATE_SELECTION );
		buttonIsolateSelection.onClick = function() { _self.doIsolateSelection();};
	
		this.buttonAutoZoom = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_button_autozoomn");;
		this.buttonAutoZoom.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_autoZoomToSelected.png" + ")";
		this.buttonAutoZoom.onClick = function() { _self.setAutoZoomMode(); };
		this.setAutoZoomMode( this.isAutoZoom );
	
		submenuMaximoSelect.addControl( this.buttonAutoZoom );
		submenuMaximoSelect.addControl( buttonHideSelection );
		submenuMaximoSelect.addControl( buttonIsolateSelection );
		if( this.buttonSelect )
		{
			submenuMaximoSelect.addControl( this.buttonSelect );
		}
		
		// Sub-menu for launching TRIRIGA functions
		var appSubMenu = new Autodesk.Viewing.UI.ComboButton( IBM.LMV.ToolBar.ID_TOOLBAR_TRIIRGA );
		appSubMenu.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "TRIRIGA.png" + ")";
		appSubMenu.setToolTip( IBM.LMV.Strings.TRIRIGA );

		var toolCount = 0;
		
		var buttoToolbarOpt

		if( features.views )
		{
			buttoToolbarOpt = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_submenu.restoreState");
			buttoToolbarOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_restore_view.png )";
			buttoToolbarOpt.setToolTip( IBM.LMV.Strings.DLG_LABEL_APPLY_VIEW );
			buttoToolbarOpt.onClick = function() { _self.displayRestoreStateDlg(); };
			appSubMenu.addControl( buttoToolbarOpt );
			toolCount++;
		
			buttoToolbarOpt = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_submenu.saveState");
			buttoToolbarOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_save_view.png )";
			buttoToolbarOpt.setToolTip( IBM.LMV.Strings.DLG_LABEL_SAVE_VIEW );
			buttoToolbarOpt.onClick = function() { _self.displaySaveStateDlg(); };
			appSubMenu.addControl( buttoToolbarOpt );
			toolCount++;
		}
		
		if( features.markup )
		{
			buttoToolbarOpt = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_submenu.restoreWorkView");
			buttoToolbarOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_show.png )";
			buttoToolbarOpt.setToolTip( IBM.LMV.Strings.MARKUP_SHOW );
			buttoToolbarOpt.onClick = function() { _self.displayShowMarkupDlg(); };
			appSubMenu.addControl( buttoToolbarOpt );

			buttoToolbarOpt = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_submenu.createMarkup");
			buttoToolbarOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline.png )";
			buttoToolbarOpt.setToolTip( IBM.LMV.Strings.MARKUP_START_EDIT );
			buttoToolbarOpt.onClick = function() { _self.createMarkup(); };
			appSubMenu.addControl( buttoToolbarOpt );
			toolCount++;
		}
		
		if( features.views || features.markup )
		{
			IBM.LMV.GetCurrentUser( _forgeViewer );
		}

		if( toolCount > 0 )
		{
			maximoSubToolbar.addControl( appSubMenu );
		}
	
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
		var buttonSearch = new Autodesk.Viewing.UI.Button("TRIIRGA_toolbar_button_search");
		buttonSearch.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_find.png" + ")";
		buttonSearch.setToolTip( IBM.LMV.Strings.TOOLBAR_SEARCH );
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
	this.setAutoZoomMode = function(
		enable
	) {
		if( enable == null )
		{
			enable = !this.isAutoZoom;
		}
		if( this.buttonAutoZoom == null )
		{
			return;
		}
		if( !enable )
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
	this.displaySaveStateDlg = function()
	{
		var saveViewDlg = new IBM.LMV.SaveViewDlg( _forgeViewer );
		saveViewDlg.setVisible( true );
	}

	// Called from ToolBar Button
	this.displayRestoreStateDlg = function()
	{
		var LoadViewDlg = new IBM.LMV.LoadViewDlg( _forgeViewer );
		LoadViewDlg.setVisible( true );
	}
		
	// Called from ToolBar Button
	this.displayShowMarkupDlg = function()
	{
		var loadWorkViewDlg = new IBM.LMV.Markup.ShowDlg( this.markupMgr, _forgeViewer.viewer, IBM.LMV.PATH_IMAGES,
		                                                  _wrapper.model.mboKey );
		loadWorkViewDlg.setVisible( true );
	}

	// Called from ToolBar Button
	this.createMarkup = function()
	{
//		this.markupMgr.createMarkup( _forgeViewer.viewer, _wrapper.model.mboKey );
		this.markupMgr.createMarkup( _forgeViewer.viewer, "" );
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
				console.error( e );
			}
		}
	};
	
	this.setSelectMode = function( e )
	{
		if( !_wrapper.features || _wrapper.features.multiselect ) return;
		
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

IBM.LMV._userLookupURL   = "/p/webapi/rest/v2/triBIMViewer/-1/user?query=true";
IBM.LMV._viewLookupURL   = "/p/webapi/rest/v2/triBIMViewer/-1/forgeViews?query=true"
IBM.LMV._uploadDataURL   = "/p/webdata?name=forgeViewerState";
IBM.LMV._downloadDataURL = "/p/webdata";
IBM.LMV._deleteDataURL   = "/p/webdata";
IBM.LMV._viewSaveURL     = "/p/webapi/rest/v2/triBIMViewer/-1/forgeViews?actionGroup=actions&action=create";
IBM.LMV._viewDeleteURL   = "/p/webapi/rest/v2/triBIMViewer/-1/forgeViews?actionGroup=actions&action=delete";

/**
 *****************************************************************************************************
 * Get Current User
 * 
 * Get and cache current user for use with saved views
 *****************************************************************************************************
 */
IBM.LMV.GetCurrentUser = function(
	forgeViewer
) {
	if( forgeViewer.currentUser ) return;
	
	var url = forgeViewer.contextRoot + IBM.LMV._userLookupURL;
	var xmlReq = new XMLHttpRequest();
	xmlReq.onreadystatechange = function() { IBM.LMV._onCurrentUser( this, forgeViewer ); };
	xmlReq.open( "GET", url, true );
	xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
	IBM.LMV.Auth.addAuthHeaders( xmlReq );

	xmlReq.send();
}

IBM.LMV._onCurrentUser = function(
	response, forgeViewer
) {
	if( response.readyState != 4 )  
	{ 
		return; 
	}

	if( response.status != 200 )
	{
		IBM.LMV.RESTError( response.status, IBM.LMV.Strings.ERR_REST, response.responseText );
		return;
	}

	var json = JSON.parse( response.responseText );
	forgeViewer.currentUser = json.data;
};


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
	
	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "TRIIRGA-SaveView-DLG", IBM.LMV.Strings.DLG_TITLE_SAVE_VIEW );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "auto";
	this.container.style.width  = "418px";
	this.container.style.resize = "none";
	
	// Save the viewer state in DM_CONTENT table
	this.saveView = function()
	{
		var _self = this;
		if( this.descInput.value == null || this.descInput.value == "" )
		{
			var messageBox = new IBM.LMV.MessageBox( this.parent, 
					                                 IBM.LMV.Strings.DLG_LABEL_DESCRIPTION, 
													 IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD );
			messageBox.setVisible( true );
			return;
		}
		var state = this.forgeViewer.viewer.getState();
		var stateJSON = JSON.stringify( state ) ;

		var url = forgeViewer.contextRoot + IBM.LMV._uploadDataURL;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onSaveData( this ); };;
		xmlReq.open( "PUT", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/json; charset=utf-8" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( stateJSON );
	}

	// Save the view in triForgeViews business object
	this.saveViewRecord = function(
		contentId
	) {
		var _self = this;

		var body = {
				data :{
					description : this.descInput.value,
					detail      : this.detailsInput.value,
					urn         : this.forgeViewer.docURN,
					viewerState : 
						{
							contentID : contentId.contentID,
							fileName  : contentId.fileName
						},
					shared      : this.sharedCB.checked,
					user        : this.forgeViewer.currentUser._userAccount,
				}
			};

		var url = forgeViewer.contextRoot + IBM.LMV._viewSaveURL;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onSaveView( this ); };;
		xmlReq.open( "POST", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/json; charset=utf-8" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( JSON.stringify( body ) );
	}

	this.makeSaveViewScreen = function() 
	{
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
		dlgTable.name      = "TRIIRGA-BIMField-SAVEVIEW-Table";
		dlgTable.className = "maxlmv_DlgTable";

		var i = 0;
		var row          = dlgTable.insertRow( i++ );
		var cell         = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "auto";
		cell.innerHTML   = IBM.LMV.Strings.DLG_TITLE_SAVE_VIEW;
		
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "50%";
		cell.innerHTML   = IBM.LMV.title;
		
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "auto";
		cell.innerHTML   = IBM.LMV.Strings.DLG_LABEL_LOCATION;
		
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
		cell.innerHTML   = IBM.LMV.Strings.DLG_LABEL_DESCRIPTION;

		row               = dlgTable.insertRow( i++ );
		cell              = row.insertCell();
		cell.className    = "maxlmv_DlgText";
		cell.colSpan      = 4;
		this.descInput    = document.createElement("INPUT");
		this.descInput.id = "TRIIRGA-Field-WO-Description";
		this.descInput.style.width = "400px";
		cell.appendChild( this.descInput );

		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.colSpan     = 4;
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = IBM.LMV.Strings.DLG_LABEL_LONG_DESC;

		row                  = dlgTable.insertRow( i++ );
		cell                 = row.insertCell();
		cell.colSpan         = 4;
		cell.className       = "maxlmv_DlgText";
		this.detailsInput    = document.createElement("TEXTAREA");
		this.detailsInput.rows = 4;
		this.detailsInput.id = "TRIIRGA-Field-SAVEVIEW-Detail";
		this.detailsInput.style.width = "400px";
		cell.appendChild( this.detailsInput );

		row                = dlgTable.insertRow( i++ );
		cell               = row.insertCell();
		cell.innerHTML     = IBM.LMV.Strings.DLG_LABEL_PUBLIC;
		cell.colSpan       = 4;
		cell.className     = "maxlmv_DlgText";
		this.sharedCB      = document.createElement("INPUT");
		this.sharedCB.type = "checkbox";
		this.sharedCB.id   = "TRIIRGA-Field-SAVEVIEW-Shared";
		cell.appendChild( this.sharedCB );


		this.scrollContainer.appendChild( dlgTable );
	}
	
	this.onSaveData = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_SAVE_VIEW );
			return;
		}

		var json = JSON.parse( request.responseText );
		this.saveViewRecord( json );
	};

	this.onSaveView = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_SAVE_VIEW );
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
		this.makeSaveViewScreen();

		var that = this;
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
  * Gets the viewer state, conversts to JSON, Base 64 encodes and saves to TRIIRGA
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
	this.selectedRow    = null;
	this.eventsDisabled = false;
	

	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "TRIIRGA-LoadView-DLG", IBM.LMV.Strings.DLG_TITLE_APPLY_VIEW );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "auto";
	this.container.style.width  = "418px";
	this.container.style.resize = "none";
	
	this.applyView = function()
	{
		var _self = this;
		if( this.eventsDisabled )
		{
			return;
		}

		if( this.selectedRow == null )
		{
			return;
		}
		var view = this.viewList[ this.selectedRow.rowIndex - 1 ];

		var url = forgeViewer.contextRoot + IBM.LMV._downloadDataURL;
		url += "/" + view.viewerState.contentID;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onViewerState( this ); };;
		xmlReq.open( "GET", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/json; charset=utf-8" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );
		xmlReq.send();
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
		var msg = IBM.LMV.Strings.DLG_TXT_DELETE_VIEW;
		var msg = msg.replace( "{0}",  this.viewList[ this.currentView ].description );
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

			var url = forgeViewer.contextRoot + IBM.LMV._deleteDataURL;
			url += "/" + this.viewList[ this.currentView ].viewerState.contentID;
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { _self.onDeleteData( this ); };
			xmlReq.open( "DELETE", url, true );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
			xmlReq.send();
		}
		else
		{
			this.eventsDisabled = false;
		}
	}

	this.lookupSavedViews = function() 
	{
		var that = this;
		
		var query = {
				"page":	{"from":0,"size":20},
  				"filters":[
						{
							name     : "urn",
						 	operator : "equals",
						 	value    : this.forgeViewer.docURN,
						 },
						 {operator: "and"},
						 {operator: "open parenthesis"},
							 {
								 name     : "user",
								 operator : "equals",
								 value    : this.forgeViewer.currentUser._userAccount,
							 },
							 {operator: "or"},
							 {
								 name     : "shared",
								 operator : "equals",
								 value    : "true"
							 },
						 {operator: "close parenthesis"}
				],
				"sorts": [
					{
						"name":"description",
						"desc":false
					}
				]
			}

		var url = this.forgeViewer.contextRoot + IBM.LMV._viewLookupURL;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onViews( this ); };
		xmlReq.open( "POST", url, true );
		xmlReq.setRequestHeader( "content-type", "application/json" );
		xmlReq.setRequestHeader( "Accept", "application/json" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( JSON.stringify( query ) );
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

	this.onDeleteData = function( request )
	{
		var _self = this;

		if( request.readyState != 4 )  
		{ 
			return; 
		}

		this.eventsDisabled = false;

		if( request.status != 200 && request.status != 404 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_APPLY_VIEW );
			return;
		}
		
		var body = {
				data :{
					"_id" :  this.viewList[ this.currentView ]._id,
					"ID" :  this.viewList[ this.currentView ]._id
				}
			};

		var url = this.forgeViewer.contextRoot + IBM.LMV._viewDeleteURL;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onDelete( this ); };
		xmlReq.open( "PUT", url, true );
		xmlReq.setRequestHeader( "content-type", "application/json" );
		xmlReq.setRequestHeader( "Accept", "application/json" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( JSON.stringify( body ) );
	}

	this.onDelete = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		this.eventsDisabled = false;

		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_APPLY_VIEW );
			return;
		}
		
		this.viewTable.deleteRow( this.currentView + 1 );
		this.viewList.splice( this.currentView, 1 );
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
		
		var text = this.viewList[ this.currentView ].detail;
		var detailsDlg = new IBM.LMV.DetailsDlg( this.parent, text, IBM.LMV.Strings.DLG_LABEL_LONG_DESC );
		detailsDlg.setVisible( true );
	}
	
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
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_APPLY_VIEW );
			this.uninitialize();
			return;
		}

		var json = JSON.parse( request.responseText );
		if( json.data )
		{
			if( !Array.isArray( json.data ) )
			{
				this.viewList = [];
				this.viewList.push( json.data );
			}
			else
			{
				this.viewList = json.data;
			}
			this.populateList();
		}
	};
	
	this.onViewerState = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		this.eventsDisabled = false;

		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_APPLY_VIEW );
			return;
		}

		var state = JSON.parse( request.responseText );
		this.forgeViewer.viewer.restoreState( state ); 

		this.uninitialize();
	}

	this.populateList = function()
	{
		var _self = this;
		
		this.header.cells[0].innerHTML = IBM.LMV.Strings.DLG_LABEL_DESCRIPTION;
		this.header.cells[1].innerHTML = IBM.LMV.Strings.DLG_LABEL_OWNER;
		this.header.cells[2].innerHTML = IBM.LMV.Strings.DLG_LABEL_LONG_DESC;
		this.header.cells[3].innerHTML = IBM.LMV.Strings.DLG_LABEL_PUBLIC;

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
			cell.innerHTML = this.viewList[i].description;
			
			cell = row.insertCell( 1 );
			cell.className = "maxlmv_DlgText";
			cell.innerHTML = this.viewList[i].user;

			cell = row.insertCell( 2 );
			cell.className = "maxlmv_DlgText";
			if( this.viewList[i].detail )
			{
				var ctrl       = document.createElement("IMG");
				ctrl.src       = IBM.LMV.PATH_IMAGES + "img_longdescription.png";
				ctrl.alt       = IBM.LMV.Strings.DLG_LABEL_LONG_DESC;
				ctrl.title     = IBM.LMV.Strings.DLG_LABEL_LONG_DESC;
				ctrl.className = "maxlmv_clickableImage";
				cell.onclick   = function( evt ) { _self.onDetailButton( this, evt ); };
				cell.appendChild( ctrl );
			}
			
			cell = row.insertCell( 3 );
			cell.className = "maxlmv_DlgText";
			if( this.viewList[i].user == this.forgeViewer.currentUser._userAccount )
			{
				var ctrl       = document.createElement("IMG");
				ctrl.src       = IBM.LMV.PATH_IMAGES + "360_delete.png";
				ctrl.alt       = IBM.LMV.Strings.DLG_BTN_DELETE_VIEW;
				ctrl.title     = IBM.LMV.Strings.DLG_BTN_DELETE_VIEW;
				ctrl.className = "maxlmv_clickableImage";
				cell.onclick   = function( evt ) { _self.onDeleteButton( this, evt ); };
				cell.appendChild( ctrl );
			}
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
		this.viewTable.name      = "TRIIRGA-BIMField-LOADVIEW-Table";
		var thead                = this.viewTable.createTHead();
		this.header              = thead.insertRow( 0 );
		var cell                 = this.header.insertCell( 0 );
		cell                     = this.header.insertCell( 1 );
		cell                     = this.header.insertCell( 2 );
		cell                     = this.header.insertCell( 3 );

		this.scrollContainer.appendChild( this.viewTable );

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
	this.text           = text;

	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "TRIIRGA-Details-DLG", title );

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
		detailsInput.id           = "TRIIRGA-Field-Details-Detail";
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