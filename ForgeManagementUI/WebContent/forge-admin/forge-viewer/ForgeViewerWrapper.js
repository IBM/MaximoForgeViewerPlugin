/**
* Copyright Wipro inc 2017
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

function loadControl(
	parentCtrl,
	versionLableCtrl
) {
	var parentCell        = document.getElementById( "forge_container" );
	IBM.LMV.ID_VIEWER     = "ManagementUI";
	IBM.LMV.ctrlContainer = parentCell;

	// Create a blank div so the ID is a valid reference util the real viewer is created
	IBM.LMV.ctrlViewer    = document.createElement("DIV");
	IBM.LMV.ctrlViewer.id = IBM.LMV.ID_VIEWER;
	IBM.LMV.ctrlViewer.style.display = "none";
	parentCell.appendChild( IBM.LMV.ctrlViewer );
}

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
		selectionMgr,
		contextRoot
	) {
		this.modelMgr    = modelMgr;
		this.selMgr      = selectionMgr;

		_forgeViewer   = new IBM.LMV.ForgeViewer();
		_forgeViewer.contextRoot = contextRoot;

		try
		{
			this.markupMgr = new IBM.LMV.Markup.MarkupMgr();
		}
		catch( e ) { /* Ignore - Mark-up may not be included */ }

		var context = "";

		var url = location.protocol + "//" + location.hostname;
		if( location.port != null && location.port.length > 0 )
		{
			url = url + ":" + location.port;
		}
	
		var idx = location.pathname.lastIndexOf( "/" );
		if( idx > 0 )
		{
			context = location.pathname.substring( 0, idx + 1 );
		}
		idx = context.indexOf( "/" );
		if( idx == 0 )
		{
			context = context.substring( 1 );
		}

		IBM.LMV.PATH_IMAGES   = "/" + context + "images/";

		IBM.LMV.AUTH_REST_CONTEXT = url + IBM.LMV.Auth.getRestURL() + "resource/proxy/auth/token";

		_forgeViewer.addSelectionListener( (ctrl) => { this.onSelect( ctrl ); } );
	}

	this.setErrorHandlers = function(
		errorHandler,
		restErrorHandler
	) { 
		if( errorHandler )     IBM.LMV.displayError = errorHandler;
		if( restErrorHandler ) IBM.LMV.RESTError = restErrorHandler;
	}
	
	this.setRestURL = function(
		restURL, authURL
	) {
		IBM.LMV.AUTH_REST_CONTEXT = restURL;
		IBM.LMV.Auth.getRestURL = () => { return restURL; }
		IBM.LMV.Auth.getAuthURL = () => { return authURL; }
	}
	
	this.loadFile = function(
		urn, binding
	) {
//		var container = document.getElementById( "forge_container" );
		_forgeViewer.createViewer( this.ctrl );
		if( urn != null && urn.length > 0 )
		{
			if( !binding ) binding = "GUID";
			if( !urn.startsWith( "urn") ) urn = "urn:" + urn;
			IBM.LMV.toolBar = new IBM.LMV.toolBarExtension( _forgeViewer, this );
			_forgeViewer.viewer.addEventListener( Autodesk.Viewing.TOOLBAR_CREATED_EVENT, 
											          function() { IBM.LMV.toolBar.onCreate() } );
			_forgeViewer.loadDocument( urn, binding );		
		}
	}
	
	
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
		if( zoomToContext == "center" )
		{
			_forgeViewer.zoomIntoSelection();
		}
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
		/*
		var ctrlCount = document.getElementById("Maximo-Toolbar-Selection-Count");
		var ctrlIndex = document.getElementById("Maximo-Toolbar-Selection-Index");
		if( ctrlCount )
		{
			ctrlCount.innerHTML = count;
		 	ctrlIndex.value     = index;
		}
		*/
	};

	this.onSelect = function(
		selection
	) {
		this.selMgr.updateSelectionSet( this.ctrl );
	};
}
