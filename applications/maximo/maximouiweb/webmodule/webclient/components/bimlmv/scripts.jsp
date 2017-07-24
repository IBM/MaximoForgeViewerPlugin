<%--
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
--%>
<%
//****************************************************************************************
//Load tralsateable stringa nd user settings from the control definition
//****************************************************************************************
//Setup geometry
viewerVendor = VENDOR_A360;

String side_pannel_width = bldgMdl.getProperty("side_pannel_width");

String selectMode1Id    = id + "_selection_mode_1";	// Single/Multi select mode
String selectMode2Id    = id + "_selection_mode_2";		// Single/Multi select mode
%>

<script id=<%=id%>_container type="text/javascript" >
"use strict";

/**********************************************************************/
// Translatable strings
/**********************************************************************/
var StringTable = [];
IBM.LMV.AUTH_REST_CONTEXT = "<%=wcs.getMaximoRequestContextURL()%>/rest/ss/BIMLMV/getAuthToken";

//Override the LMV default color scheme.  15 is dark blue, 8 is light blue
Autodesk.Viewing.Private.DefaultLightPreset  = <%=lmvtheme%>;
Autodesk.Viewing.DefaultSettings.lightPreset = <%=lmvtheme%>;


/**********************************************************************/
// Loads the NavisWorks control tryng different versions then sets the
// lable next to the AD logo to the version loaded
/**********************************************************************/
function loadControl(
	parentCtrl,
	versionLableCtrl
) {
	var parentCell        = document.getElementById( parentCtrl );
	var versionCell       = document.getElementById( versionLableCtrl );
	IBM.LMV.ID_VIEWER     = "<%=ctrlId%>";
	IBM.LMV.ctrlContainer = parentCell;

	// Create a blank div so the ID is a valid reference util the real viewer is created
	IBM.LMV.ctrlViewer    = document.createElement("DIV");
	IBM.LMV.ctrlViewer.id = IBM.LMV.ID_VIEWER;
	IBM.LMV.ctrlViewer.style.display = "none";
	parentCell.appendChild( IBM.LMV.ctrlViewer );
}

/**********************************************************************/
// This class contains all the functions needed to interact with the 
// native viewer 
/**********************************************************************/
function ViewerWrapper(
	ctrl	// The control handle
) {
	var _forgeViewer   = null
	var _propertyPanel = null;

	this.modelMgr    = null;
	this.selMgr      = null;
	this.markupMgr   = null;
	this.model       = null;
	this.ctrl        = ctrl;
	
    // Return the version of this interface.  Implementations written to
    // this spec should always return "1.0"
    this.getInterfaceVersion = function()
    {
      return "1.1";
    };
	
    // Called once after all objects are created, but before any methods
    // are called with the exception of setCurrentModel.
    // modelMgr       The ModelManager instance
    // selectionMgr   The SelectionManager instance
    // Return:        Nothing
	this.initialize = function(
		modelMgr,
		selectionMgr
	) {
		var _self      = this;
		this.modelMgr  = modelMgr;
		this.selMgr    = selectionMgr;
		
		<%@ include file="strings.jsp" %>

		_forgeViewer = new IBM.LMV.ForgeViewer();

		<%if( appType ==  BIMViewer.TYPE_WORKORDER )
		{%> 
			this.markupMgr = new IBM.LMV.Markup.MarkupMgr();
		<%}%>
		
		this.topToolBar = new MaximoToolBar( this, _forgeViewer );

		IBM.LMV.PATH_IMAGES      = "<%=IMAGE_PATH%>/bim/";
		IBM.LMV.Auth.contextRoot = "/rest";

		IBM.LMV.addMScreenModeChangeistener( this.onScreeModeChange );
		_forgeViewer.addSelectionListener( this.onSelect );
		IBM.LMV.ToolBar.onToolbarCreate = function( forgeViewer, parentToolBar ) { _self.onToolbarCreate( forgeViewer, parentToolBar ); };
		IBM.LMV.displayError            = function( msg ) { _self.displayError( msg ); };
	};
	
    // Request that the viewer load the specified file.  Errors should
    // be reported on the viewer status line by calling 
    // setStatus( status )
    // file:          The URL attribute from the Maximo 
    //                BuildingModel table
    // Return:        Nothing
	this.loadFile = function(  
		file
	) {
		if( this.model == null  )
		{
			return;
		}
		_propertyPanel = null;

		_forgeViewer.createViewer( IBM.LMV.ctrlContainer );
		IBM.LMV.toolBar = new IBM.LMV.toolBarExtension( _forgeViewer );
		_forgeViewer.viewer.addEventListener( Autodesk.Viewing.TOOLBAR_CREATED_EVENT, 
										      function() { IBM.LMV.toolBar.onCreate() } );
		_forgeViewer.ctrlViewer.style.top = "<%=toolbar_height%>" + "px";

		IBM.LMV.modelId  = this.model.modelId;
		IBM.LMV.location = this.model.location;
		IBM.LMV.title    = this.model.title;
		IBM.LMV.siteId   = this.model.siteId;

		_forgeViewer.loadDocument( this.model.url, this.model.attribName );		
		setSize();
	};
	
	// Requests the viewer plugin to select a single item clearing
	// any previous selection
	// Value          Id of item to select
    // zoomToContext: Flag indicating if the viewer should zoom in on 
    //                the resulting selection set.
    // Return:        Nothing
	this.selectValue = function( 
		value, 
		zoomToContext 
	) {
		_forgeViewer.selectByGUID( value );
	};

    // Request the view plugin to select a list of items clearing
    // any previous selection
    // valueList:     Array of ids that is the desired selection set.
    // zoomToContext: Flag indicating if the viewer should zoom in on 
    //                the resulting selection set.
    // Return:        Number of items found and selected
	this.selectValueList = function(
		valueList,		// Array of itds to select
		zoomToContext
	) {
		_forgeViewer.selectByGUID( valueList );
	};

	// Called when the current model is changed. This method does not need to load
	// the new model.  loadFile is called for that
    // Return:        Nothing
	this.setCurrentModel = function(
		currentModel
	) {
		this.model = currentModel;
	};
	
    // Requests the value (id) of the selected item with a specified
    // index in the selection list.  
    // index   The index of the active selection item in the current
    //         selection list 
    // return  The id of the item in the selection list referenced by
    //         index. If the index is out of bounds then an empty string
    //         is returned
	this.getSelection = function( 
		index 
	) {
		var sel = _forgeViewer.getSelectionList();
		if( sel == null || index > sel.length )
		{
			return "";
		}
		return sel[index-1].guid;
	};
	
	// An array of the currently selected items ids
    this.getSelectionList = function() 
    {
		var ids = [];
		var sel = _forgeViewer.getSelectionList();
		if( sel == null )
		{
			return ids;
		}

		for( var i = 0; i < sel.length; i++ )
		{
			ids.push( sel[i].guid );
		}
		return ids;
    };
    
    // Return:  THe number of items currently selected
    this.getSelectionCount = function()
    {
		var sel = _forgeViewer.getSelectionList();
		if( sel == null ) return 0;
		return sel.length;
    };
    
    // Search the selection list for selected item and return the imdex
    // Return the index of the selected item in the selection list.
    this.getItemIndex = function(
    	selectedItem	// Any item in the list of selected items
 	) {
		if( !selectedItem ) return -1;
		
		var sel = _forgeViewer.getSelectionList();
		for( var i = 0; i < sel.length; i++ )
		{
			if( sel[i].guid == selectedItem )
			{
				return i + 1;
			}
		}
		return -1;
    };
    
    // Clear all selected item and set the selection list to zero length
    this.clearSelection = function()
    {
		_forgeViewer.viewer.clearSelection();
    };
    
    // Soom the current view to focus on the item in the selection set indicated by index
    this.focusOnSelectedItem = function(
    	itemIndex	// The idex of the item in the selection list
	) {
		_forgeViewer.focusOnSelectedItem( itemIndex - 1 );
    };
    
    this.enableMultiSelect = function (
   		enable
	) {
		_forgeViewer.setMultiSelect( enable );
    };
	
	this.setAutoZoom = function(
		enable
	) {
		_forgeViewer.setAutoZoomMode( enable );
	};
	
	// Called when the controling applcation has resized the viewer container
	// The viewer must adjust to the new container size
	this.reziseViewer = function(
		height,
		width
	) {
		
		if( _forgeViewer.viewer != null )
		{
			var mode = 0;
			if( _forgeViewer.viewer.screenModeDelegate )
			{
				mode = _forgeViewer.viewer.screenModeDelegate.getMode();
			}
			if( mode == 2 )		// Full Screen
			{
				height = screen.height;
				width  = screen.width;
			}
			else
			{
				height = IBM.LMV.ctrlViewer.clientHeight;
				width  = IBM.LMV.ctrlViewer.clientWidth;
			}

			_forgeViewer.viewer.container.style.height = "" + height + "px";
			_forgeViewer.viewer.container.style.width  = "" + width + "px";
			try
			{
				_forgeViewer.viewer.resize();
			}
			catch( e )	{} // view may not be initialized on the first call
		}
	};
    
    // Called when the selection changes after the common processing
    // ctrl           HTML id of the viewer control
    // selectionList  Array of Ids that are currently selected. this
    //                is the list returned from calling  
    //                this.getSelectionList
    // selection,     The active item in the selection set.
    // count,         The number of items in the selection set.  This is
    //                the value returned from calling
    //                this.getSelectionCount
    // index          The index in the selection set of the active item
    //                This is the result of calling this.getItemIndex
    // Return:        Nothing
    this.onSelectionChange = function(
		ctrl,
		selectionList,
		selection,
		count,
		index 
	) {
		var ctrlCount = document.getElementById("Maximo-Toolbar-Selection-Count");
		var ctrlIndex = document.getElementById("Maximo-Toolbar-Selection-Index");
		if( ctrlCount )
		{
			ctrlCount.innerHTML = count;
		 	ctrlIndex.value     = index;
		}
	};
	
	this.onSelect = function(
		selection
	) {
		selMgr.updateSelectionSet( IBM.LMV.ctrlContainer );
	};
	
	this.displayError = function(
		message
	) {
		if( message instanceof Object )
		{
			var temp = "";
			for( prop in message ) 
			{
				temp = temp + prop + ": " + message[ prop ]; + "  ";
			}
			message = temp;
		}

		var ctrlStatus = document.getElementById( "<%=statusId%>" );
		if( ctrlStatus != null )
		{
			if( message == null ) message = "";
			ctrlStatus.value = message;
		}
		else
		{
			console.log( message );
		}
	};
	
	this.onScreeModeChange = function(
		mode
	) {
		switch( mode )
		{
			case 0:
				var id = _resolution;
				_resolution = -2;
				resize( "" + id );
				break;
			case 2:
				_height = screen.height + 2 * <%=toolbar_height%>;
				_width  = screen.width;
				setSize();
				break;
		}
	};
	
	// Forge Viewer toolbar event to display the location Details pannel
	this.displayProperties = function(
		mbo
	) {
		if( _propertyPanel == null )
		{
			_propertyPanel = new IBM.LMV.MaximoPropertyPanel( _forgeViewer.viewer.container, _forgeViewer );
		}
		else
		{
			if( _propertyPanel.isVisible() )
			{
				if( _propertyPanel.mboName == mbo )
				{
					_propertyPanel.setVisible( false );
					return;
				}
			}
		}
		_propertyPanel.baseMbo = mbo;
		_propertyPanel.reset( mbo );
		_propertyPanel.setVisible( true );
		_propertyPanel.requestProperties();
	}

	this.onToolbarCreate = function(
		forgeViewer, parentToolBar
	) {
		const _parentToolBar = parentToolBar;

		var _self = this;

		var mainToolbar = _forgeViewer.viewer.getToolbar(true);     // get the main toolbar from the viewer
		console.assert(mainToolbar != null);
		
		var subToolBar = new Autodesk.Viewing.UI.ControlGroup("Maximo_toolabr_group_2");

		var buttonPrev = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_prev_item");
		buttonPrev.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_previous.png" + ")";
		buttonPrev.setToolTip( "Previous item" );
		buttonPrev.onClick = function() { selMgr.selectionPrev(); };

		var textCount = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_item_count");

		var buttonNext = new Autodesk.Viewing.UI.Button("Maximo_toolabr_button_next_item");
		buttonNext.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_next.png" + ")";
		buttonNext.setToolTip( "Next item" );
		buttonNext.onClick = function() { selMgr.selectionNext(); };

		subToolBar.addControl( buttonPrev );
		subToolBar.addControl( textCount );
		subToolBar.addControl( buttonNext );

		buttonNext.container.style.marginLeft   = "0";
		buttonPrev.container.style.marginRight  = "0";
		buttonNext.container.style.paddingLeft  = "0";
		buttonPrev.container.style.paddingRight = "0";

		textCount.container.style.marginLeft   = "0";
		textCount.container.style.marginRight  = "0";
		textCount.container.style.paddingLeft  = "0";
		textCount.container.style.paddingRight = "0";

		mainToolbar.addControl( subToolBar );
		
		var ctrlTable             = document.createElement("TABLE");
		ctrlTable.cellspacing = 0;
		ctrlTable.cellpadding = 0;
		var idx = 0;

		var row               = ctrlTable.insertRow();
		var cell              = row.insertCell( idx++ );
		cell.style.minWidth   = "30px";
		var ctrlIndex         = document.createElement("INPUT");
		ctrlIndex.id          = "Maximo-Toolbar-Selection-Index";
		ctrlIndex.style.width = "38px";
		ctrlIndex.onkeypress  = function( event ) { viewer.indexKeyPress(); };
		cell.className        = "adsk-control-group";
		cell.appendChild( ctrlIndex );

		cell                    = row.insertCell( idx++ );
		cell.innerHTML          = IBM.LMV.Strings[ "KEY_OF" ];
		cell.style.paddingRight = "2px";
		cell.style.paddingLeft  = "2px";
		cell.style.marginLeft   = "0";
		cell.style.marginRight  = "0";
		cell.noWrap             = true;
		cell.className          = "adsk-control-group";

		cell                  = row.insertCell( idx++ );
		cell.id               = "Maximo-Toolbar-Selection-Count";
		cell.style.minWidth   = "40px";
		cell.style.align      = "center";
		cell.className        = "adsk-control-group";

		var ctrl = textCount.container;
		ctrl.style.width        = "122px";
		ctrl.style.paddingLeft  = "0";
		ctrl.style.marginLeft   = "0";
		ctrl.style.paddingRight = "0";
		ctrl.style.marginRight  = "0";
		ctrl.childNodes[0].appendChild( ctrlTable );
		
		var maximoSubToolbar = mainToolbar.getControl( IBM.LMV.ToolBar.ID_TOOLBAR_GROUP );

		var selectSubToolbar = maximoSubToolbar.getControl( IBM.LMV.ToolBar.ID_TOOLBAR_SELECT );
		if( selectSubToolbar )
		{
			<%-- Cancel selection --%>
			var buttonCreateWO = new Autodesk.Viewing.UI.Button( "btnGotoLoci" );
			buttonCreateWO.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_deselect.png" + ")";
			buttonCreateWO.setToolTip( "<%=strings.gotoLocBtn%>" );
			buttonCreateWO.onClick = function() { selMgr.clear( <%=ctrlId%> ); };
			selectSubToolbar.addControl( buttonCreateWO );
		}
		
		// Create Mximo sub-menu
		var maximoSubMenu    = maximoSubToolbar.getControl( IBM.LMV.ToolBar.ID_TOOLBAR_MAXIMO );

	    <%if( appType !=  BIMViewer.TYPE_ASSET )
	    {%>
			var buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolabr_submenu.asset");
			buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_findAsset.png" + ")";
			buttonMaximoOpt.setToolTip( IBM.LMV.Strings[ "Asset Detail" ] );
			buttonMaximoOpt.onClick = function() { _self.displayProperties( "ASSET" ); };
			maximoSubMenu.addControl( buttonMaximoOpt );
	  	<%}%>  
	
		maximoSubToolbar.addControl( maximoSubMenu );
	};
	
	/**********************************************************************/
	// Catch ENTER key in search field
	/**********************************************************************/
	this.indexKeyPress = function()
	{
		var keynum = 0;
		
		if(window.event) 	// IE
		{
			keynum = window.event.keyCode;
		}
		else if( e.which )	 // Netscape/Firefox/Opera
		{
			keynum = window.event.which;
		}
		
		if( keynum == 13 )
		{
			var ctrlIndex = document.getElementById("Maximo-Toolbar-Selection-Index");
			var idx = ctrlIndex.value;
			idx =  parseInt( idx );
			selMgr.selectionByIndex( idx );
		}
		return true;
	};
}

/**********************************************************************/
// 
/**********************************************************************/
function MaximoToolBar(
	wrapper, forgeViewer
) {
	const _forgeViewer = forgeViewer;
	const _wrapper     = wrapper;
	this.modelMgr      = wrapper.modelMgr;
	this.selMgr        = wrapper.selMgr;
	this.markupMgr     = wrapper.markupMgr;
	this.toolBarDiv    = null;
	this.isMarkup      = false;
	
	this.makeToolBar = function()
	{
		var opts = { collapsible: true };
		var topToolBar = new Autodesk.Viewing.UI.ToolBar( "Maximo_top_toolbar", opts );
		
		this.makeModelSubToolbar( modelMgr, topToolBar );
		
		this.makeRequestSubToolbar( topToolBar );

		this.makeSystemSubToolbar( topToolBar );
		
		this.makeWOSubToolbar( topToolBar );

		this.makeResizeSubToolbar( topToolBar );
		
		var viewerWidth = _forgeViewer.viewer.container.clientWidth;
		var tbDiv = document.createElement("DIV");
		tbDiv.id = "Maximo-top_toolbar-div";
		tbDiv.style.position="absolute";
		tbDiv.style.top = 0;
		tbDiv.style.width = "100%";
		tbDiv.appendChild( topToolBar.container );
		_forgeViewer.viewer.container.appendChild( tbDiv );

		var tbCont  = topToolBar.container;
		var width = tbCont.clientWidth;
		tbCont.style.width = "" + width + "px";
		tbCont.style.left = "calc( 50% - " + width  / 2 + "px )";
		tbCont.style.top = "25px";
		this.toolBarDiv = tbDiv;
		
		this.selMgr.updateToolbar();
		
		if( this.markupMgr != null )
		{
			var _self = this;
			this.markupMgr.addModeChangeListerner( function( evt ) { _self.onMarkupModeChange( evt ); } );
		}
	}
	
	this.makeModelSubToolbar = function(
		modelMgr,
		toolBar	
	) {
		var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Maximo_top_toolabr_submenu_model" );
 
		<%if( appType ==  BIMViewer.TYPE_LOCATION )
		{%>
			<%-- Manage models associated with the location --%>
			var buttonManageModels = new Autodesk.Viewing.UI.Button("Maximo_top_toolabr_btnModel");
			buttonManageModels.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_viewModel.png" + ")";
			buttonManageModels.setToolTip( "<%=strings.loadModel%>" );
			buttonManageModels.onClick = function() { window.parent.sendEvent(  'bim_addmod', '<%=bldgMdl.getRenderId()%>' ); };
			subToolbar.addControl( buttonManageModels );
   		<%}
		if( appType !=  BIMViewer.TYPE_MODEL )
		{%>
			var _self = this;
			var buttonModelList = new Autodesk.Viewing.UI.Button("Maximo_top_toolabr_button_model_list");
			var ctrl = buttonModelList.container;
			ctrl.style.width = "140px";
			ctrl = ctrl.childNodes[0];
			ctrl.style.backgroundPosition = "95%";
			ctrl.style.paddingRight = "20px";
			
			this.selectModelList     = document.createElement("SELECT");
			this.selectModelList.id = "<%=modelId%>";
			this.selectModelList.style.width = "140px";
			this.selectModelList.style.boxShadow = "0 3px 5px rgba(0,0,0,.5)";
			this.selectModelList.onchange = function() { _self.selectModel() };
			ctrl.appendChild( this.selectModelList );
			subToolbar.addControl( buttonModelList );
			
			for( var i = 0; i < modelMgr.modelList.length; i++ )
			{
				var opt;
				opt = document.createElement( "OPTION" );
				opt.value = modelMgr.modelList[i].modelId;				
				opt.text  = modelMgr.modelList[i].title;
				this.selectModelList.add( opt );
				if( modelMgr.modelList[i].modelId == modelMgr.currentModel.modelId )
				{
					this.selectModelList.selectedIndex = i;
				}
			}
			
			toolBar.addControl( subToolbar );
		<%}%>
		
		return subToolbar;
	}

	this.selectModel = function()
	{
		IBM.LMV.trace( "Select Model" );
		this.modelMgr.selectModel( this.selectModelList, <%=ctrlId%> );
	}
	
	this.makeRequestSubToolbar = function(
		toolBar
	) {
		var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Maximo_top_toolabr_submenu_request" );
		
		var ctrlCount = 0;

		<%if(    appType ==  BIMViewer.TYPE_ASSET || appType ==  BIMViewer.TYPE_LOCATION )
		{%>
			<%-- Create a new work order --%>
			var buttonCreateWO = new Autodesk.Viewing.UI.Button( "btnNewWO" );
			buttonCreateWO.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_createWO.png" + ")";
			buttonCreateWO.setToolTip( "<%=strings.newWOBtn%>" );
			buttonCreateWO.onClick = function() { window.parent.sendEvent(  'CREATEWO', '<%=bldgMdl.getRenderId()%>' ); };
			subToolbar.addControl( buttonCreateWO );
			ctrlCount++;
		<%}
		if( appType !=  BIMViewer.TYPE_MODEL && appType !=  BIMViewer.TYPE_UNKNOWN )
		{%>
			<%-- Search for open work orders and service requests --%>
			var buttonSearchWO = new Autodesk.Viewing.UI.Button( "btnSearchWO" );
			buttonSearchWO.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_viewWOsandPMs.png" + ")";
			buttonSearchWO.setToolTip( "<%=strings.displayWOBtn%>" );
			buttonSearchWO.onClick = function() { maximoIntf.maxSearchWorkOrders(); };
			subToolbar.addControl( buttonSearchWO );
			ctrlCount++;
		<%}
		if(    appType ==  BIMViewer.TYPE_ASSET || appType ==  BIMViewer.TYPE_LOCATION )
		{%>
			<%-- Create a new service request --%>
			var buttonCreateSR = new Autodesk.Viewing.UI.Button( "btnNewSR" );
			buttonCreateSR.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_newTicket.png" + ")";
			buttonCreateSR.setToolTip( "<%=strings.createTicketBtn%>" );
			buttonCreateSR.onClick = function() { window.parent.sendEvent(  'CREATESR', '<%=bldgMdl.getRenderId()%>' ); };
			subToolbar.addControl( buttonCreateSR );
			ctrlCount++;
		<%}
		if( bldgMdl.getRecordType() ==  BIMViewer.RECORD_LOCATION )
		{%>
			<%-- Inspect asset details for an opperating location --%>
			var buttonInspectAsset = new Autodesk.Viewing.UI.Button( "<%=inspectAssetId%>" );
			buttonInspectAsset.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_findAsset.png" + ")";
			buttonInspectAsset.setToolTip( "<%=strings.inspectAssetBtn%>" );
			buttonInspectAsset.onClick = function() { maxAssetInspect(); };
			subToolbar.addControl( buttonInspectAsset );
			ctrlCount++;
		<%}%>
		
		if( ctrlCount == 0 )
		{
			return null;
		}

		toolBar.addControl( subToolbar );
		
		return subToolbar;
	}
	
	this.makeSystemSubToolbar = function(
		toolBar
	) {
		var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Maximo_top_toolabr_submenu_system" );

		var ctrlCount = 0;

		<%if( appType ==  BIMViewer.TYPE_LOCATION )
		{%>
			<%-- Create a new system from the current selection --%>
			var buttonNewSystem = new Autodesk.Viewing.UI.Button( "<%=newSystemId%>" );
			buttonNewSystem.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_newSystem.png" + ")";
			buttonNewSystem.setToolTip( "<%=strings.newSystemBtn%>" );
			buttonNewSystem.onClick = function() { maximoIntf.maxSystemsNew(); };
			subToolbar.addControl( buttonNewSystem );
			ctrlCount++;
		<%}
		if( appType !=  BIMViewer.TYPE_MODEL && appType !=  BIMViewer.TYPE_UNKNOWN && appType !=  BIMViewer.TYPE_LOOKUP )
		{%>
			<%-- Display the compnent of a system as the current selection --%>
			var buttonDisplaySystem = new Autodesk.Viewing.UI.Button( "<%=displaySystemId%>" );
			buttonDisplaySystem.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_openSystem.png" + ")";
			buttonDisplaySystem.setToolTip( "<%=strings.displaySystemBtn%>" );
			buttonDisplaySystem.onClick = function() { maximoIntf.maxSystemsDisplay(); };
			subToolbar.addControl( buttonDisplaySystem );
			ctrlCount++;
		<%}
		if( appType ==  BIMViewer.TYPE_LOCATION )
		{%>
			<%-- Update an existing system from the current selection --%>
			var buttoUpdateSystem = new Autodesk.Viewing.UI.Button( "<%=updateSystemId%>" );
			buttoUpdateSystem.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_editSystem.png" + ")";
			buttoUpdateSystem.setToolTip( "<%=strings.updateSystemBtn%>" );
			buttoUpdateSystem.onClick = function() { maximoIntf.maxSystemsUpdate(); };
			subToolbar.addControl( buttoUpdateSystem );
			ctrlCount++;
     	<%}%>
		
		if( ctrlCount == 0 )
		{
			return null;
		}

		toolBar.addControl( subToolbar );

		return subToolbar;
	}
	
	this.makeWOSubToolbar = function(
		toolBar
	) {
		<%if( appType ==  BIMViewer.TYPE_WORKORDER )
		{%> 
			var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Maximo_top_toolabr_submenu_WO" );
			
		    <%-- Display dialog with list of markups associated with the current work order --%>
			<%
			AppInstance app = bldgMdl.getWebClientSession().getCurrentApp();
			try
			{
				if( app.isSigOptionCheck( "BIM_SMU" ) )
				{%>
					var buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolabr_submenu.restoreWorkView");
					buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_show.png )";
					buttonMaximoOpt.setToolTip( IBM.LMV.Strings.MARKUP_SHOW );
					buttonMaximoOpt.onClick = function() { _self.displayShowMarkupDlg(); };
					subToolbar.addControl( buttonMaximoOpt );
				<%}
			}
			catch( MXException mxe )
			{ /* Ignore */	}
			%>

		    <%-- Enter Markup create mode and show Markup toolbar --%>
			<%
			try
			{
				if( app.isSigOptionCheck( "BIM_EMU" ) )
				{%>
					buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolabr_submenu.createMarkup");
					buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline.png )";
					buttonMaximoOpt.setToolTip( IBM.LMV.Strings.MARKUP_START_EDIT );
					buttonMaximoOpt.onClick = function() { _self.createMarkup(); };
					subToolbar.addControl( buttonMaximoOpt );
				<%}
			}
			catch( MXException mxe )
			{ /* Ignore */	}
			%>

		    <%-- Add or remove the current selection from a workorder or a ticket --%>
			var buttoAddToWO = new Autodesk.Viewing.UI.Button( "addSelectionBtn" );
			buttoAddToWO.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_add.png" + ")";
			buttoAddToWO.setToolTip( "<%=strings.addSelectionBtn%>" );
			buttoAddToWO.onClick = function() { maximoIntf.maxSelectionAdd(); };
			subToolbar.addControl( buttoAddToWO );

			var buttoRemoveFromWO = new Autodesk.Viewing.UI.Button( "removeSelectionBtn" );
			buttoRemoveFromWO.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_remove.png" + ")";
			buttoRemoveFromWO.setToolTip( "<%=strings.removeSelectionBtn%>" );
			buttoRemoveFromWO.onClick = function() { maximoIntf.maxSelectionRemove(); };
			subToolbar.addControl( buttoRemoveFromWO );

			toolBar.addControl( subToolbar );
	
			return subToolbar;
		<%}%>
	}
	
	this.makeResizeSubToolbar = function(
		toolBar
	) {
		var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Maximo_top_toolabr_submenu_resize" );

		<%if( appType !=  BIMViewer.TYPE_LOOKUP ) 
		{%>
			<%-- Create resize button for main page --%>
			var buttonResize = new Autodesk.Viewing.UI.Button( "btnDlgResizei" );
			buttonResize.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_resize.png" + ")";
			buttonResize.setToolTip( "<%=strings.resizeBtn%>" );
			buttonResize.onClick = function( event ) { resizeBtn( event ); };
			subToolbar.addControl( buttonResize );
		<%} else {%>  
			<%-- Create resize button for dilaogs --%>
			var buttonResize = new Autodesk.Viewing.UI.Button( "btnRestore" );
			buttonResize.icon.style.backgroundImage = "url(" + "<%=BIM_IMAGE_PATH%>/tb_resize.png" + ")";
			buttonResize.setToolTip( "<%=strings.resizeBtn%>" );
			buttonResize.onClick = function( event ) { resizeDlgBtn( event ); };
			subToolbar.addControl( buttonResize );
		<%}%>

		toolBar.addControl( subToolbar );

		return subToolbar;
	}
	
		
	<%if( appType ==  BIMViewer.TYPE_WORKORDER )
	{%> 
		// Called from ToolBar Button
		this.createMarkup = function()
		{
			this.markupMgr.createMarkup( _forgeViewer.viewer, _wrapper.model.mboKey );
		}
		
		this.showMarkup = function()
		{
			this.markupMgr.showMarkup( _forgeViewer.viewer );
		}
		
		// Called from ToolBar Button
		this.displayShowMarkupDlg = function()
		{
			var loadWorkViewDlg = new IBM.LMV.Markup.ShowDlg( this.markupMgr, _forgeViewer.viewer, "<%=IMAGE_PATH%>",
			                                                  _wrapper.model.mboKey );
			loadWorkViewDlg.setVisible( true );
		}
	<%}%>

	
	this.onScreenModeCahnge = function(
		mode
	) {
		if( !this.toolBarDiv ) return;
		switch( mode )
		{
			case 0:
				this.show( true );
				break;
			case 1:
				break;
			case 2:
				this.show( false );
				break;
		}
	}
	
	this.onMarkupModeChange = function(
		evt
	) {
		if(    evt.type == Autodesk.Viewing.Extensions.Markups.Core.EVENT_EDITMODE_ENTER 
		    || evt.type == IBM.LMV.Markup.EVENT_SHOW )
		{
			this.show( false );
			this.isMarkup = true;
		}
		else if(    evt.type == Autodesk.Viewing.Extensions.Markups.Core.EVENT_EDITMODE_LEAVE
		         || evt.type == IBM.LMV.Markup.EVENT_HIDE )
		{
			this.isMarkup = false;
			this.show( true );
		}
	}
	
	this.show = function(
		isShown
	) {
		if( isShown )
		{
			if( !this.isMarkup )
			{
				this.toolBarDiv.style.display = "";
				this.isShown = true;
			}
		}
		else
		{
			this.toolBarDiv.style.display = "none";
			this.isShown = false;
		}
	}
	
	var _self = this;
	
	IBM.LMV.addModelLoadistener( function() {_self.makeToolBar() } );
	IBM.LMV.addMScreenModeChangeistener( function( mode ) { _self.onScreenModeCahnge( mode ) } );
}
</script>