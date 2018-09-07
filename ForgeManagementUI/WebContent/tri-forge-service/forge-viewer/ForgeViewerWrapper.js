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

/**
 * TRIRIGA dictionary for auto translation
*/
var __dictionary__DLG_BTN_YES              = "Yes";
var __dictionary__DLG_BTN_NO               = "No";
var __dictionary__DLG_BTN_APPLY            = "Apply";
var __dictionary__DLG_BTN_CREATE           = "Create";      
var __dictionary__DLG_BTN_CANCEL           = "Cancel";     
var __dictionary__DLG_BTN_CLOSE            = "Close";
var __dictionary__DLG_BTN_OK               = "OK"; 
var __dictionary__ERR_LOAD                 = "Load Error: ";
var __dictionary__ERR_AUTH                 = "Auth request error ";
var __dictionary__ERR_PROPERTY             = "Property Error: ";
var __dictionary__ERR_REST                 = "REST Error";  
var __dictionary__ERR_UNAUTHORIZED         = "Unauthorized";
var __dictionary__ERR_SEARCH               = "Search Error: ";

var __dictionary__TOOLBAR_HIDE_SELECTION   = "Hide selection";
var __dictionary__TOOLBAR_ISOLATE_SELECTION= "Isolate Selection";
var __dictionary__TOOLBAR_SEARCH           = "Search";
var __dictionary__TOOLBAR_SELECTION_MODE   = "Set selection mode";
var __dictionary__TOOLBAR_ZOOM_MODEL       = "Zoom to model";
var __dictionary__TOOLBAR_FULLSCREEN       = "Full Screen";

var __dictionary__KEY_DISABLE_AUTO_ZOOM    = "Disable Auto Zoom";  
var __dictionary__KEY_ENABLE_AUTO_ZOOM     = "Enable Auto Zoom";  

var __dictionary__SELECT                   = "Selection Tools";  
var __dictionary__TRIRIGA                  = "TRIRIGA Tools";  

var __dictionary__DLG_TITLE_SAVE_VIEW      = "Save Current View";
var __dictionary__MSG_TITLE_REQUIRED_FIELD = "Required Field Missing %1";
var __dictionary__DLG_TITLE_APPLY_VIEW     = "Select View to Restore";
var __dictionary__DLG_LABEL_CREATOR        = "Creator";
var __dictionary__DLG_LABEL_LOCATION       = "Building";
var __dictionary__DLG_LABEL_LONG_DESC      = "Details";
var __dictionary__DLG_LABEL_DESCRIPTION    = "Description";
var __dictionary__DLG_LABEL_SAVE_VIEW      = "Save View";
var __dictionary__DLG_LABEL_APPLY_VIEW     = "Apply Saved View";
var __dictionary__DLG_LABEL_OWNER          = "Owner";
var __dictionary__DLG_LABEL_PUBLIC         = "Public";

var __dictionary__DLG_TXT_DELETE_VIEW      = "Delete saved view for {0}";
var __dictionary__DLG_BTN_DELETE_VIEW      = "Delete Saved View";     

var __dictionary__MARKUP_START_EDIT        = "Enter markup mode...";

var __dictionary__MARKUP_CANCEL            = "Cancel markup mode";
var __dictionary__MARKUP_STYLE             = "Markup Style...";
var __dictionary__MARKUP_SAVE              = "Save markup with work order...";
var __dictionary__MARKUP_SHOW              = "Show markup...";

var __dictionary__MARKUP_ARROW             = "Select arrow markup tool";
var __dictionary__MARKUP_CLOUD             = "Select cloud markup tool";
var __dictionary__MARKUP_FREEHAND          = "Select freehand markup tool";
var __dictionary__MARKUP_LINE              = "Select line markup tool";
var __dictionary__MARKUP_OVAL              = "Select oval markup tool";
var __dictionary__MARKUP_RECTANGLE         = "Select rectangle markup tool";
var __dictionary__MARKUP_TEXT              = "Select text markup tool";

var __dictionary__MARKUP_RED               = "R";
var __dictionary__MARKUP_GREEN             = "G";
var __dictionary__MARKUP_BLUE              = "B";

var __dictionary__DLG_TITLE_MARKUP_STYLE     = "Markup Style";
var __dictionary__DLG_LABEL_LINE_WEIGHT      = "Line Weight";
var __dictionary__DLG_LABLE_FILL             = "Fill";
var __dictionary__DLG_LABLE_FONT_SIZE        = "Font Size";
var __dictionary__DLG_LABLE_ITALICS          = "Italics";
var __dictionary__DLG_LABLE_BOLD             = "Bold";

var __dictionary__DLG_TITLE_SAVE_MARKUP      = "Save Markup";
var __dictionary__DLG_TITLE_DISPLAY_MARKUP   = "Display Markup";
var __dictionary__DLG_LABEL_MARKUP_DESC      = "Markup Description";
var __dictionary__MSG_CONFIRM_DELETE_MAKRUP  = "Delete makrup {0}";

var _isForgeLoad = false;
var _onForgeLoad = null;

var headers = [
	{
		src  : "https://developer.api.autodesk.com/viewingservice/v1/viewers/style.css?v=v3.2.1",
		type : "CSS",
	},
	{
		src  : "ForgeViewer.css",
		type : "CSS",
	},
	{
		src  : "https://developer.api.autodesk.com/viewingservice/v1/viewers/three.min.js?v=v3.2.1",
		type : "SCRIPT",
	},
	{
		src  : "https://developer.api.autodesk.com/viewingservice/v1/viewers/viewer3D.js?v=v3.2.1",
		type : "SCRIPT",
	},
	{
		src  : "gunzip.min.js",
		type : "SCRIPT",
	},
	{
		src  : "Forge.js",
		type : "SCRIPT",
	},
	{
		src  : "ForgeToolbar.js",
		type : "SCRIPT",
		onload : function() { onForgeJSLoad() },
	}
];

function loadHeaderListSync(
	headers, callback
) {
	var _index = 0;
	var _headers = headers;

	this.load = function()
 	{
		var _self = this;
		if( _index >= _headers.length )
		{
			return;
		}
		var header =_headers[ _index ];
		_index++;

		var ell;
		if( header.type == "CSS" )
		{
			var ell  = document.createElement('LINK' );
			ell.type = "text/css";
			ell.rel  = 'stylesheet';
			ell.href = header.src;
		}
		else if( header.type == "SCRIPT" )
		{
			var ell  = document.createElement('SCRIPT' );
			ell.type = "text/javascript";
			ell.src  = header.src;
			ell.onload = function() 
			{ 
				if( header.onload ) header.onload();
				_self.load() 
			};
			ell.onerror  = function( evt ) 
			{ 
				console.error( evt);  
				_self.load(); 
			};
		}
		var headElls = document.getElementsByTagName('head');
		var head = headElls[0];
		head.appendChild( ell );

		// Hack to work arojnd onload not working for LINK
		if( header.type == "CSS" )
		{
		    var img = document.createElement('img');
	        img.onerror = function()
	        {
	            _self.load();
	        }
	        img.src = header.src;
    	}
	};
};

function loadSriptFile(
	name,     // Name of viewer to use  
	onLoad
) {
	var jsLibrary    = document.createElement('SCRIPT' );
	jsLibrary.type   = "text/javascript";
	jsLibrary.onload = onLoad;
	jsLibrary.src    = name;
	var headers      = document.getElementsByTagName('head');
	var head         = headers[0];
	head.appendChild( jsLibrary );
}


var loader = new  loadHeaderListSync( headers );
loader.load();

function onForgeJSLoad()
{
	IBM.LMV.Strings.DLG_BTN_YES              = __dictionary__DLG_BTN_YES;
	IBM.LMV.Strings.DLG_BTN_NO               = __dictionary__DLG_BTN_NO;
	IBM.LMV.Strings.DLG_BTN_APPLY            = __dictionary__DLG_BTN_APPLY;
	IBM.LMV.Strings.DLG_BTN_CREATE           = __dictionary__DLG_BTN_CREATE;      
	IBM.LMV.Strings.DLG_BTN_CANCEL           = __dictionary__DLG_BTN_CANCEL;     
	IBM.LMV.Strings.DLG_BTN_CLOSE            = __dictionary__DLG_BTN_CLOSE;
	IBM.LMV.Strings.DLG_BTN_OK               = __dictionary__DLG_BTN_OK; 
	IBM.LMV.Strings.ERR_LOAD                 = __dictionary__ERR_LOAD;
	IBM.LMV.Strings.ERR_AUTH                 = __dictionary__ERR_AUTH;
	IBM.LMV.Strings.ERR_PROPERTY             = __dictionary__ERR_PROPERTY;
	IBM.LMV.Strings.ERR_REST                 = __dictionary__ERR_REST;  
	IBM.LMV.Strings.ERR_UNAUTHORIZED         = __dictionary__ERR_UNAUTHORIZED;  
	IBM.LMV.Strings.ERR_SEARCH               = __dictionary__ERR_SEARCH;

	IBM.LMV.Strings.TOOLBAR_HIDE_SELECTION   = __dictionary__TOOLBAR_HIDE_SELECTION;
	IBM.LMV.Strings.TOOLBAR_ISOLATE_SELECTION= __dictionary__TOOLBAR_ISOLATE_SELECTION;
	IBM.LMV.Strings.TOOLBAR_SEARCH           = __dictionary__TOOLBAR_SEARCH;
	IBM.LMV.Strings.TOOLBAR_SELECTION_MODE   = __dictionary__TOOLBAR_SELECTION_MODE;
	IBM.LMV.Strings.TOOLBAR_ZOOM_MODEL       = __dictionary__TOOLBAR_ZOOM_MODEL;
	IBM.LMV.Strings.TOOLBAR_FULLSCREEN       = __dictionary__TOOLBAR_FULLSCREEN;

	IBM.LMV.Strings.KEY_DISABLE_AUTO_ZOOM    = __dictionary__KEY_DISABLE_AUTO_ZOOM;  
	IBM.LMV.Strings.KEY_ENABLE_AUTO_ZOOM     = __dictionary__KEY_ENABLE_AUTO_ZOOM;  

	IBM.LMV.Strings.SELECT                   = __dictionary__SELECT;  
	IBM.LMV.Strings.TRIRIGA                  = __dictionary__TRIRIGA;  

	IBM.LMV.Strings.DLG_TITLE_SAVE_VIEW      = __dictionary__DLG_TITLE_SAVE_VIEW;
	IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD = __dictionary__MSG_TITLE_REQUIRED_FIELD;
	IBM.LMV.Strings.DLG_TITLE_APPLY_VIEW     = __dictionary__DLG_TITLE_APPLY_VIEW;
	IBM.LMV.Strings.DLG_LABEL_CREATOR        = __dictionary__DLG_LABEL_CREATOR;
	IBM.LMV.Strings.DLG_LABEL_LOCATION       = __dictionary__DLG_LABEL_LOCATION;
	IBM.LMV.Strings.DLG_LABEL_LONG_DESC      = __dictionary__DLG_LABEL_LONG_DESC;
	IBM.LMV.Strings.DLG_LABEL_DESCRIPTION    = __dictionary__DLG_LABEL_DESCRIPTION;
	IBM.LMV.Strings.DLG_LABEL_SAVE_VIEW      = __dictionary__DLG_LABEL_SAVE_VIEW;
	IBM.LMV.Strings.DLG_LABEL_APPLY_VIEW     = __dictionary__DLG_LABEL_APPLY_VIEW;
	IBM.LMV.Strings.DLG_LABEL_OWNER          = __dictionary__DLG_LABEL_OWNER;
	IBM.LMV.Strings.DLG_LABEL_PUBLIC         = __dictionary__DLG_LABEL_PUBLIC;

	IBM.LMV.Strings.DLG_TXT_DELETE_VIEW      = __dictionary__DLG_TXT_DELETE_VIEW;
	IBM.LMV.Strings.DLG_BTN_DELETE_VIEW      = __dictionary__DLG_BTN_DELETE_VIEW;     

	IBM.LMV.Strings.MARKUP_START_EDIT        = __dictionary__MARKUP_START_EDIT;
	IBM.LMV.Strings.MARKUP_CANCEL            = __dictionary__MARKUP_CANCEL;
	IBM.LMV.Strings.MARKUP_STYLE             = __dictionary__MARKUP_STYLE;
	IBM.LMV.Strings.MARKUP_SAVE              = __dictionary__MARKUP_SAVE;
	IBM.LMV.Strings.MARKUP_SHOW              = __dictionary__MARKUP_SHOW;

	IBM.LMV.Strings.MARKUP_ARROW             = __dictionary__MARKUP_ARROW;
	IBM.LMV.Strings.MARKUP_CLOUD             = __dictionary__MARKUP_CLOUD;
	IBM.LMV.Strings.MARKUP_FREEHAND          = __dictionary__MARKUP_FREEHAND;
	IBM.LMV.Strings.MARKUP_LINE              = __dictionary__MARKUP_LINE;
	IBM.LMV.Strings.MARKUP_OVAL              = __dictionary__MARKUP_OVAL;
	IBM.LMV.Strings.MARKUP_RECTANGLE         = __dictionary__MARKUP_RECTANGLE;
	IBM.LMV.Strings.MARKUP_TEXT              = __dictionary__MARKUP_TEXT;

	IBM.LMV.Strings.MARKUP_RED               = __dictionary__MARKUP_RED;
	IBM.LMV.Strings.MARKUP_GREEN             = __dictionary__MARKUP_GREEN;
	IBM.LMV.Strings.MARKUP_BLUE              = __dictionary__MARKUP_BLUE;

	IBM.LMV.Strings.DLG_TITLE_MARKUP_STYLE     = __dictionary__DLG_TITLE_MARKUP_STYLE;
	IBM.LMV.Strings.DLG_LABEL_LINE_WEIGHT      = __dictionary__DLG_LABEL_LINE_WEIGHT;
	IBM.LMV.Strings.DLG_LABLE_FILL             = __dictionary__DLG_LABLE_FILL;
	IBM.LMV.Strings.DLG_LABLE_FONT_SIZE        = __dictionary__DLG_LABLE_FONT_SIZE;
	IBM.LMV.Strings.DLG_LABLE_ITALICS          = __dictionary__DLG_LABLE_ITALICS;
	IBM.LMV.Strings.DLG_LABLE_BOLD             = __dictionary__DLG_LABLE_BOLD;

	IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP      = __dictionary__DLG_TITLE_SAVE_MARKUP;
	IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP   = __dictionary__DLG_TITLE_DISPLAY_MARKUP;
	IBM.LMV.Strings.DLG_LABEL_MARKUP_DESC      = __dictionary__DLG_LABEL_MARKUP_DESC;
	IBM.LMV.Strings.MSG_CONFIRM_DELETE_MAKRUP  = __dictionary__MSG_CONFIRM_DELETE_MAKRUP;
	_isForgeLoad = true;
	if( _onForgeLoad )
	{
		_onForgeLoad();
	}
}

function LoadForge(
	onForgeLoad
) {
	_onForgeLoad = onForgeLoad;
	if( _isForgeLoad )
	{
		onForgeLoad();
	}
}

// Features:
// A list of features expressed as toolbar icons to enable or disable.  supported values are:
// - markup      : Loads the markup extensions and displayes markup tools on the toolbar
// - multiselect : displayes the single select/multi select tool on the toolbar.
// - view        : Displayes the save and restore view tools on the toolbar
// - explode     : Autodesk explode tool
// - measure     : Autodesk measure tool
// - settings    : Autodesk viewer settings
// If the object has a property matching the feature name, the feature is enabled
function ViewerWrapper(
	ctrl,	  // The control handle
	features  // Object, if the object has a property matching the feature name, the feature is enabled
) {
	var _forgeViewer   = null
	var _propertyPanel = null;

	this.modelMgr    = null;
	this.selMgr      = null;
	this.markupMgr   = null;
	this.model       = null;
	this.ctrl        = ctrl;
	this.features    = features;
	this.autoZoom    = false;

	
    // Return the version of this interface. 
    this.getInterfaceVersion = function()
    {
      return "1.2";
    };
	
    // Called once after all objects are created, but before any methods
    // are called with the exception of setCurrentModel.
    // modelMgr       The ModelManager instance
    // selectionMgr   The SelectionManager instance
    // Locale is one of:
    //     Chinese Simplified: zh-cn
    //     Chinese Traditional: zh-tw
    //     Czech: cs
    //     English: en
    //     French: fr
    //     German: de
    //     Italian: it
    //     Japanese: ja
    //     Korean: ko
    //     Polish: pl
    //     Portuguese Brazil: pt-br
    //     Russian: ru
    //     Spanish: es
    //     Turkish: tr
    // Return:        Nothing
	this.initialize = function(
		modelMgr,
		selectionMgr,
		locale,
		contextRoot
	) {
		this.modelMgr    = modelMgr;
		this.selMgr      = selectionMgr;
		this.contextRoot = contextRoot;

		if( this.features == null ) this.features = {};

		_forgeViewer    = new IBM.LMV.ForgeViewer();
		_forgeViewer.contextRoot = this.contextRoot;
		if( locale ) _forgeViewer.setLocale( locale );

		if( this.features.markup )
		{
			loadSriptFile( "ForgeMarkup.js", ()=> { this.onMarkupLoad(); } );
			loadSriptFile( "TriForgeMarkup.js" );	// TRIRIGA specific dialogs to display and save
		}

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
		_forgeViewer.createViewer( this.ctrl );
		if( urn != null && urn.length > 0 )
		{
			if( this.model )
			{
				IBM.LMV.modelId  = this.model.modelId;
				IBM.LMV.location = this.model.location;
				IBM.LMV.title    = this.model.title;
			}
			else
			{
				IBM.LMV.modelId  = "";
				IBM.LMV.location = "";
				IBM.LMV.title    = "";
			}

			if( !binding ) binding = "GUID";
			if( !urn.startsWith( "urn") ) urn = "urn:" + urn;
			IBM.LMV.toolBar = new IBM.LMV.toolBarExtension( _forgeViewer, this );
			IBM.LMV.toolBar.setAutoZoomMode( this.autoZoom );
			_forgeViewer.viewer.addEventListener( Autodesk.Viewing.TOOLBAR_CREATED_EVENT, 
											      function() { IBM.LMV.toolBar.onCreate() } );
			_forgeViewer.loadDocument( urn, binding );		

			if( this.markupMgr )
			{
				window.setTimeout( ()=>{
					this.markupMgr.init( _forgeViewer.viewer );
				}, 1000 );
			}
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
		this.autoZoom = enable;
		_forgeViewer.setAutoZoomMode( enable );

		// Only call if everything is loaded
		if( IBM && IBM.LMV && IBM.LMV.toolBar )
		{
			IBM.LMV.toolBar.setAutoZoomMode( enable );
		}
	};
	
	// The Record key is used to associate makeup with user or business context
	// When a markeup record is created, it is stamped with the current record key
	// When available markup is queried, the record key is part of the filter.
	// From the viewer's perspective, the record key is opaque
	this.setRecordKey = function(
		recordKey
	) {
		
	}

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
	
	this.addScreenModeChangeCallback = function(
		callback
	) {
		if( IBM && IBM.LMV  )
		{
			IBM.LMV.addMScreenModeChangeListener( callback );					
		}
	}
	
	// iOS/Safari nad possible others doing support full screen mode for iFrames.
	// This passes a request for to fake full screen mode up to the hosing element
	// which alows it to write whatever logic is appropate to give the viewer more
	// of the screen
	this.onFakeFullScreen = function(
		isFullScreen
	) {
		
	}

	this.onSelect = function(
		selection
	) {
		this.selMgr.updateSelectionSet( this.ctrl );
	};

	this.displayView = function(
		view
	) {
		_forgeViewer.displayView( view ); 
	}

	this.onMarkupLoad = function()
	{

		try
		{
			this.markupMgr = new IBM.LMV.Markup.MarkupMgr( _forgeViewer );;
			IBM.LMV.toolBar.markupMgr = this.markupMgr;
		}
		catch( e ) { /* Ignore - Mark-up may not be included */ }
	}
}
