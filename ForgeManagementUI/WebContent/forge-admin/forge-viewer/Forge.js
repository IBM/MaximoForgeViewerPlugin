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
   
//************************************************************************************************
//
//		Namespace creation function
//
//************************************************************************************************
var IBM = {};
IBM.createNS = function(
	namespace
) {
    var nsparts = namespace.split(".");
    var parent = IBM;
 
    if (nsparts[0] === "IBM") 
	{
        nsparts = nsparts.slice(1);
    }
 
    for (var i = 0; i < nsparts.length; i++) 
	{
        var partname = nsparts[i];
        if (typeof parent[partname] === "undefined") 
		{
            parent[partname] = {};
        }
        parent = parent[partname];
    }
    return parent;
};

/**
 *****************************************************************************************************
 *
 * LMV Name Space
 * 
 *****************************************************************************************************
 */
IBM.createNS( "IBM.LMV" );

IBM.LMV.AUTH_REST_CONTEXT = "/rest/ss/BIMLMV/getAuthToken";
IBM.LMV.ID_VIEWER         = "IBM_ForgeViewerContainer";

IBM.LMV.markup            = null;

if( !IBM.LMV.Strings  ) IBM.LMV.Strings = {};
/**
  * The values in the string table are over written by consuming applcations with values for the correct locale
  */
IBM.LMV.Strings.DLG_BTN_YES              = "Yes";
IBM.LMV.Strings.DLG_BTN_NO               = "No";
IBM.LMV.Strings.DLG_BTN_APPLY            = "Apply";
IBM.LMV.Strings.DLG_BTN_CREATE           = "Create";      
IBM.LMV.Strings.DLG_BTN_CANCEL           = "Cancel";     
IBM.LMV.Strings.DLG_BTN_CLOSE            = "Close";
IBM.LMV.Strings.DLG_BTN_OK               = "OK"; 
IBM.LMV.Strings.ERR_LOAD                 = "Load Error: ";
IBM.LMV.Strings.ERR_AUTH                 = "Auth request error ";
IBM.LMV.Strings.ERR_PROPERTY             = "Property Error: ";
IBM.LMV.Strings.ERR_SEARCH               = "Search Error: ";

// Override the LMV default color scheme.  15 is dark blue, 8 is light blue
Autodesk.Viewing.Private.DefaultLightPreset = 15;
Autodesk.Viewing.DefaultSettings.lightPreset = 15;

IBM.LMV.viewer        = null;

IBM.LMV.extensions    = null;
IBM.LMV.modelLoadListeners = [];
IBM.LMV.screenModeChangeListeners = [];

IBM.LMV.addModelLoadistener = function(
	listener
) {
	IBM.LMV.modelLoadListeners.push( listener );
};

IBM.LMV.addMScreenModeChangeistener = function(
	listener
) {
	IBM.LMV.screenModeChangeListeners.push( listener );
};

IBM.LMV.displayError = function(
	errorMsg
) {
	console.log( errorMsg );
};

IBM.LMV.RESTError = function(
	status, 			// HTTP Status
	source, 			// Text identifying operation that generated the error
	responseText		// HTTP Error text
) {
		IBM.LMV.displayError( source + status + "\n" + responseText );
};

IBM.LMV.trace = function(
	msg
) {
	console.log( msg );
};

/**
  *****************************************************************************************************
  * Top level controler class for the IBM implementation of the AUtodesk Forge Viewer
  *****************************************************************************************************
  */
IBM.LMV.ForgeViewer = function()
{
	var _selectionMgr      = null;
	var _modelLoaded       = false;

	this.viewer     = null;
	this.ctrlViewer = null;			// DIV that is the container for the viewer
	this.docURN     = null;
	
	// Forge service authoerization token			
	this.authToken          = null;
	this.authTokenFetchTime = null;
	
	//Called by constructor after the class is built;
	this.init = function()
	{
		var _self = this;
		window.document.body.onresize = function( e ) { _self.onResize( e ) };
	}
	
	this.createViewer = function(
		parentCtrl
	) {
		var _self = this;

		var container = document.getElementById( IBM.LMV.ID_VIEWER );
		if( container != null )
		{
			if( this.viewer != null )
			{
				if( _selectionMgr )
				{
					_selectionMgr.finish();
					_selectionMgr = null;
				}
				try
				{
					this.viewer.finish();
				}
				catch( e ) {}		// Don't like finish beng called if viewer was not initialized
				IBM.LMV.viewer = null;
				this.viewer    = null;
			}
			var parent = IBM.LMV.ctrlViewer.parentNode;
			if( parent != null )
			{
				parent.removeChild( container );
				IBM.LMV.ctrlViewer = null
				this.ctrlViewer    = null;
			}
			IBM.LMV.displayError( "" );
		}
		
		this.ctrlViewer = document.createElement("DIV");
		IBM.LMV.ctrlViewer = this.ctrlViewer;
		this.ctrlViewer.id = IBM.LMV.ID_VIEWER;
		this.ctrlViewer.style.width    = "100%";
		this.ctrlViewer.style.height   = "100%";
	
		parentCtrl.appendChild( this.ctrlViewer );
		this.viewer = new Autodesk.Viewing.Private.GuiViewer3D( this.ctrlViewer, {});
		IBM.LMV.viewer = this.viewer;
	};

	/**
	  * @param documentId URN of document to load - Base64 encoded
	  * @param binding    Property used to bind viewer object to external content.  Typicall 
	  *                   some variant of Guid.  Selection are made and reported as values
	  *                   of this property
	  */
	this.loadDocument = function( 
		documentId,
		binding
	) {
		if( documentId == null || documentId == "" )
		{
			return;
		}
		this.docURN   = documentId;
	
		_modelLoaded  = false;
		
		_selectionMgr = new IBM.LMV.SelectionMgr( this, binding );
		_selectionMgr._deferedSearch = _deferedSearch;
		_selectionMgr._deferedZoom   = _deferedZoom;
		_deferedSearch = null;
		_deferedZoom   = false;
		
		if( !this.isTokenValid() )
		{
			this.authenticate();
		}
		else
		{
			this.initDocument();
		}
	};
	
	this.resize = function(
		height, width
	) {
		if( this.viewer && this.viewer.container )
		{
			this.viewer.container.style.height = "" + height + "px";
			this.viewer.container.style.width  = "" + width + "px";
			try
			{
				this.viewer.resize();
				this.viewer.updateToolbarButtons();
			}
			catch( e )	{} // view may not be initialized on the first call
		}
	}

	this.initDocument = function()
	{
		var _self = this;
		if( this.docURN != null )
		{
			IBM.LMV.trace( "initDocument: IBM.LMV.createViewer()"  );

			var opts = {
				getAccessToken : function() { return _self.getAuthToken() },
				refreshToken   : function() { return _self.getAuthToken() }
			};
			Autodesk.Viewing.Initializer( opts, function( httpProgress ) { _self.onViewerInit( httpProgress ) } );
		}
	}; 

	this.onViewerInit = function( httpProgress ) 
	{
		var _self = this;

		this.viewer.initialize();
		_selectionMgr.initialize( this.docURN );
							
		var doc = this.docURN;
		Autodesk.Viewing.Document.load( doc, 
										function( doc ) { _self.onDocumentLoad( doc ) },
										function( errorNum, errorMsg ) { _self.onLoadError( errorNum, errorMsg ) } );
	};
	
	this.onDocumentLoad = function(  // onLoadCallback
		doc
	){
		var _self = this;
		IBM.LMV.trace( "onDocumentLoad" );
		
		var geometryItems = [];
		geometryItems = Autodesk.Viewing.Document.getSubItemsWithProperties(
				doc.getRootItem(), 
				{
					'type' : 'geometry',
					'role' : '3d'
				}, 
				true);
	
		if( geometryItems.length > 0 ) 
		{
			_modelLoaded = false;
			
			this.viewer.load( doc.getViewablePath(geometryItems[0]), "",  
							  function( d ) { _self.onModelLoad( d ) },
							  function( errorNum, errorMsg ) { _self.onLoadError( errorNum, errorMsg ) } );
			_selectionMgr.loadGUIDS(doc);
		}
		var height = this.ctrlViewer.clientHeight;
		var width  = this.ctrlViewer.clientWidth;
		this.viewer.container.style.height = "" + height + "px";
		this.viewer.container.style.width  = "" + width + "px";
		this.viewer.resize();
		
	};
	
	this.onModelLoad = function( d )
	{
		if( _modelLoaded  ) return;
	
		IBM.LMV.trace( "onModelLoad" );
		
		this.setMultiSelect( false );
		this.viewer.navigation.fitBounds(true, this.viewer.model.getBoundingBox());
		for( var i = 0; i < IBM.LMV.modelLoadListeners.length; i++ )
		{
			try
			{
				IBM.LMV.modelLoadListeners[i]();
			}
			catch( e )
			{
				IBM.LMV.displayError( e.message );
				console.log( e );
			}
		}
		
		this.initMaterialMgr();
		
		_modelLoaded  = true;
	};
	
	this.onResize = function( e )
	{
		if( !this.ctrlViewer ) return;
		
		var height = this.ctrlViewer.clientHeight;
		var width  = this.ctrlViewer.clientWidth;
		this.viewer.container.style.height = "" + height + "px";
		this.viewer.container.style.width  = "" + width + "px";
		this.viewer.resize();
	};
	
	this.onLoadError = function(		// onErrorCallback
		errorNum,
		errorMsg
	) {		
		IBM.LMV.docLoading = false;
		IBM.LMV.displayError( IBM.LMV.Strings.ERR_LOAD + errorMsg);
	};
	
	//==================================================================================
	// Forge Authentication
	//
	// Request a token for the Forge service from a REST service hosted by the server
	//==================================================================================
	this.authenticate = function()
	{
		var _self = this;
		
		if( this.isTokenValid() )
		{
			return;
		}
		
		var url = IBM.LMV.Auth.getAuthURL();
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onAuthToken( this ); };
		xmlReq.open( "GET", url, true );
	
		xmlReq.setRequestHeader( "Access-Control-Allow-Origin", "*");
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		authNS.addAuthHeaders( xmlReq );
		xmlReq.send();
	};
	
	this.onAuthToken = function( request ) 
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}
	
		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, "Auth request error ",  request.responseText );
			return;
		}
		var token;
		var authReturn = JSON.parse( request.responseText );
		if( authReturn.access_token || authReturn.ErrorCode )
		{
			token = authReturn;
		}
		else		// Token is from Maximo REST service and need to be unpacked
		{
			var token = JSON.parse( authReturn.getAuthTokenResponse.return.content );
			IBM.LMV.trace( "onAuthToken: " + authReturn.getAuthTokenResponse.return.content );
		}
		if( token.ErrorCode )
		{
			IBM.LMV.displayError( IBM.LMV.Strings.ERR_AUTH + 
								  token.ErrorCode + ", " +
								  token.ErrorType + ", " +
								  token.ErrorMessage );
			return;
		}
	
		this.authToken = token;
		var d = new Date();
		this.authTokenFetchTime = d.getTime();
		
		IBM.LMV.trace( "onAuthenticate: " + token.access_token + " " +  token.expires_in );
		this.initDocument();
	};
	
	this.getAuthToken = function()
	{
		var token = this.authToken.access_token;
		IBM.LMV.trace( "getAuthToken: " + this.authToken.access_token + " " +  this.authToken.expires_in );
		return token;
	};
	
	this.isTokenValid = function()
	{
		if( this.authToken == null || this.authTokenFetchTime == null )
		{
			return false;
		}
		var curTime = new Date().getTime();
		if( this.authTokenFetchTime + (( this.authToken.expires_in  - 2 ) * 1000 ) > curTime )
		{
			return true;
		}
		return false;
	};

	//==================================================================================
	// Selection Manager
	//==================================================================================
	var   _onSelectListeners = [];
	var   _onFocusListeners  = [];
	var   _deferedSearch     = null;
	var   _deferedZoom       = false;


	this.addSelectionListener = function(
		listener
	) {
		_onSelectListeners.push( listener );
	};

	this.addFocusListener = function(
		listener
	) {
		_onFocusListeners.push( listener );
	};

	this.fireFocusListener = function(
		focusIndex
	) {
		for( var i = 0; _onFocusListeners != null && i < _onFocusListeners.length; i++ )
		{
			if( _onFocusListeners[i] != null )
			{
				try
				{
					_onFocusListeners[i](  focusIndex );
				}
				catch( e )
				{
					IBM.LMV.displayError( e.message );
				}
			}
		}
	}

	this.fireSelectionListener = function(
		selection
	) {
		for( var i = 0; _onSelectListeners != null && i < _onSelectListeners.length; i++ )
		{
			if( _onSelectListeners[i] != null )
			{
				try
				{
					_onSelectListeners[i](  selection );
				}
				catch( e )
				{
					IBM.LMV.displayError( e.message );
				}
			}
		}
	}

	// Zoom the current view to focus on the item in the selection set indicated by index
	this.focusOnSelectedItem = function(
		itemIndex	// The idex of the item in the selection list
	) {
		if( _selectionMgr )
		{
			_selectionMgr.focusOnSelectedItem( itemIndex );
		}
	}

	this.getSelection = function()
	{
		if( _selectionMgr )
		{
			return _selectionMgr.getSelection();
		}
		return null;
	}

	this.getSelectionList = function()
	{
		if( _selectionMgr )
		{
			return _selectionMgr.getSelectionList();
		}
		return null;
	}

	this.select = function(
		dbIds
	) {
		if( _selectionMgr )
		{
			_selectionMgr.select( dbIds );
		}
	};

	this.zoomIntoSelection = function()
	{
		if( _selectionMgr )
		{
			_selectionMgr.zoomIntoSelection();
		}
		else
		{
			_deferedZoom = "center";
		}
	}

	this.setAutoZoomMode = function(
		enable
	) {
		if( _selectionMgr )
		{
			_selectionMgr.setAutoZoomMode( enable );
		}
	},

	this.setMultiSelect = function(
		enable
	) {
		if( _selectionMgr )
		{
			_selectionMgr.enableMultiSelect( enable );
		}
	}

	this.selectByGUID = function(
		GUIDs
	) {
		if( _selectionMgr )
		{
			_selectionMgr.selectByGUID( GUIDs );
		}
		else
		{
			_deferedSearch = GUIDs;
		}
	}
	
	//==================================================================================
	// Material Manager
	//==================================================================================
	this.originalMaterial = [];
	this.fragList         = null;

	this.dbId2Frag        = null;
	this.colorList        = [];
	this.originalMaterial = [];

	this.initMaterialMgr = function()
	{
		this.originalMaterial = [];
		this.dbId2Frag        = null;
		this.colorList        = [];
		this.originalMaterial = [];
		this.fragList         = this.viewer.model.getFragmentList();
	}

	this.setColor = function(
		color
	) {
		if( color == null || color == "" ) return;
		color = color.toLowerCase();
		
		var sel = getSelection();
		if( sel == null || sel.length == null || sel.length == 0 )
		{
			return;
		}
		
		var material = this.colorList[ color ];
		if( material == null )
		{
			material = this.addMaterial( color );
			this.colorList[ color ] = material;
		}
		
		var fragId2dbId = this.fragList.fragments.fragId2dbId;
		if( this.dbId2Frag == null )
		{
			this.dbId2Frag = [];
			for( var d = 0; d < fragId2dbId.length; d++ )
			{
				if( this.dbId2Frag[ fragId2dbId[d] ] == null )
				{
					this.dbId2Frag[ fragId2dbId[d] ] =  [];
				}
				this.dbId2Frag[ fragId2dbId[d] ].push( d );
			}
		}  
		
		for( var i = 0; i < sel.length; i++ )
		{
			var frags = this.dbId2Frag[ sel[i].dbId ];
			for( var f = 0; frags != null && f < frags.length; f++ )
			{
				var fragId = frags[f];
				if( this.originalMaterial[ fragId ] == null )
				{
					var materialId = this.fragList.materialids[ fragId ];
					this.originalMaterial[ fragId ] = this.fragList.materialmap[ materialId ];
				}
				this.setMaterial( fragId, material );
			}
		}
		this.viewer.impl.invalidate(true);
	}

	this.setMaterial = function(
		frags, material
	) {
		if( !Array.isArray(frags) )
		{
			frags = [ frags ];
		}
		
		for( var i = 0; i < frags.length; i++ )
		{
			if( this.originalMaterial[ frags[i] ] == null )
			{
				var materialId = this.fragList.materialids[ frags[i] ];
				this.originalMaterial[ frags[i] ] = this.fragList.materialmap[ materialId ];
			}
			this.fragList.setMaterial( frags[i], material );
		}
		this.viewer.impl.invalidate(true);
	}
	
	this.getMaterial = function(
		frag
	) {
		var materialId = this.fragList.materialids[ frag ];
		return this.fragList.materialmap[ materialId ];
	}
	
	this.restoreMaterial = function(
		frags, norefresh 
	) {
		if( !Array.isArray(frags) )
		{
			frags = [ frags ];
		}
		
		var invalidate = false;
		
		for( var i = 0; i < frags.length; i++ )
		{
			if( this.originalMaterial[ frags[i] ] != null )
			{
				this.fragList.setMaterial( frags[i], this.originalMaterial[ frags[i] ] );
				invalidate = true;
			}
		}
		if( invalidate && !norefresh )
		{
			this.viewer.impl.invalidate(true);
		}
	}

	this.resetAllMaterial = function()
	{
		for( var i = 0; i < this.originalMaterial.length; i++ )
		{
			if( this.originalMaterial[i] != null )
			{
				this.setMaterial( i, this.originalMaterial[i] );
			}
		}
		this.originalMaterial = [];
	}
	
	this.guid = function() 
	{
		var d = new Date().getTime();

		var guid = 'xxxx-xxxx-xxxx-xxxx'.replace(
			/[xy]/g,
			function (c) {
				var r = (d + Math.random() * 16) % 16 | 0;
				d = Math.floor(d / 16);
				return (c == 'x' ? r : (r & 0x7 | 0x8)).toString(16);
		});
		
		return guid;
	};
	
	this.addMaterial = function(
		color
	) {
	
		var material = new THREE.MeshPhongMaterial
		(
			{ color: color }
		);
		
		this.viewer.impl.matman().addMaterial( this.guid(), material );
		return material;
	}
	
	// Setup class
	this.init();
}

//************************************************************************************************
//
//		Auth Name Space
//
// Manages Autodesk Authentication token
//************************************************************************************************
var authNS = IBM.createNS( "IBM.LMV.Auth" );

authNS.contextRoot        = "";

authNS.getAuthURL = function()
{
	var url;
	if( IBM.LMV.AUTH_REST_CONTEXT.indexOf( "http" ) == 0 )
	{
		url = IBM.LMV.AUTH_REST_CONTEXT;
	}
	else
	{
		var path = location.pathname;
		var idx = path.lastIndexOf( "/" );
		path = path.substring( 0, idx );
		url = location.protocol + "//" + location.hostname;
		url = url + path + IBM.LMV.AUTH_REST_CONTEXT;
	}
	return url;
};

authNS.getRestURL = function()
{
	var url = "/";

	var path = location.pathname;
	path = path.substring( 1 );
	var idx = path.indexOf( "/webclient" );
	path = path.substring( 0, idx );
	if( path != null && path.length > 0 )
	{
		url = url + path;
	}
	url = url + IBM.LMV.Auth.contextRoot;
	return url;
};

authNS.setRequestHeaders = function(
	xmlReq
) {
	xmlReq.setRequestHeader("Access-Control-Allow-Origin", "*");
	xmlReq.setRequestHeader( "Content-Type", "application/json; charset=UTF-8" );
	xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
};

// Can be overridden
authNS.addAuthHeaders = function( xmlReq )
{
}

//************************************************************************************************
// Selection Manager
//************************************************************************************************
IBM.LMV.SelectionMgr = function(
	forgeViewer,
	binding
) {
	const _forgeViewer       = forgeViewer;
	const _binding           = binding || "GUID";

	var   _initialized       = false;

	var   _docType           = "";

	var   _isAutoZoom        = true;
	var   _selectionList     = [];
	var   _focusIndex        = 0;				// Index into selectionList for current focus
	var   _deferedSearch     = null;
	var   _deferedZoom       = false;
	var   _gemotryLoaded     = false;
	var   _objectCount       = 0;
	var   _objectTreeCreate  = false;

	var   _guidLoading       = false;
	var   _idsLoading        = false;
	var   _modelLoading      = true;

	// Translation between dbIDs and GUIDs
	var   _dbId2GuidArray = [];
	var   _guid2dbIdArray = {};


	// Counter to synchronize callbacks from search
	var _searchCount       = 0;
	var _GUIDs             = [];
	var _searchResult      = [];

	const _clickActionsSingleSelect = {
		  "click": {
			  "onObject":  ["selectOnly" ],
			  "offObject": ["deselectAll"]
		  },
		  "clickShift": {
			  "offObject": ["deselectAll"]
		  },
		  "clickCtrl": {
			  "onObject":  ["isolate"],
			  "offObject": ["setCOI"]
		  },
		  "clickCtrlShift": {
			  "onObject":   ["hide"],
			  "offObject":  ["showAll"]
		  }
	};

	const _clickActionsMultiSelect = {
		  "click": {
			  "onObject":  ["selectOnly" ],
			  "offObject": ["deselectAll"]
		  },
		  "clickShift": {
			  "onObject":  ["selectToggle"],
			  "offObject": ["deselectAll"]
		  },
		  "clickCtrl": {
			  "onObject":  ["isolate"],
			  "offObject": ["setCOI"]
		  },
		  "clickCtrlShift": {
			  "onObject":  ["hide"],
			  "offObject": ["showAll"]
		  }
	};

	var _Node = function ()
	{
		var dbId;
		var name;
		var guid;
	};

	this.initialize = function(
		doc
	) {
		if( _initialized ) return;

		var _self = this;

		var decoded = doc.substring( 4 );
		decoded = atob( decoded );
		var idx = decoded.lastIndexOf( "." );
		if( idx > 0 )
		{
			_docType = decoded.substring( idx + 1 );
			if( _docType )
			{
				_docType = _docType.toLowerCase();
			}
		}

		_forgeViewer.viewer.addEventListener( Autodesk.Viewing.SELECTION_CHANGED_EVENT, 
			                                  function( selection ) { _self.onSelectedCallback( selection ) } );
		_forgeViewer.viewer.addEventListener( Autodesk.Viewing.GEOMETRY_LOADED_EVENT, 
									          function( eventData ) { _self.onGeometryLoad( eventData ) } );
		_forgeViewer.viewer.addEventListener( Autodesk.Viewing.OBJECT_TREE_CREATED_EVENT, 
									          function( eventData ) { _self.onObjectTreeCreate( eventData ) } );
		_forgeViewer.viewer.setCanvasClickBehavior( _clickActionsSingleSelect );
		_initialized = true;
	};

	this.finish = function()
	{
		_initialized = false;
	}

	this.doDeferedSearch = function()
	{
		if(    _gemotryLoaded      == true
			&& _objectTreeCreate   == true 
			&& _idsLoading         == false
			&& _guidLoading == false )
		{
			_modelLoading = false;
			if( _deferedSearch != null )
			{
				try
				{
					var search = _deferedSearch;
					var zoom   = _deferedZoom;
					_deferedSearch = null;
					_deferedZoom   = false;
					this.selectByGUID( search );
					if( zoom == "center" )
					{
						this.zoomIntoSelection();
					}
				}
				catch( e )
				{
					console.log( e );
				}
			}
			else if( _deferedZoom == "center" )
			{
				_deferedZoom   = false;
				this.zoomIntoSelection();
			}
		}
	}

	this.doSearch = function()
	{
		var _self = this;
		while( _searchCount < _GUIDs.length )
		{
			var idx = _searchCount;
			if( _GUIDs[idx] == null || _GUIDs[idx] == "" )
			{
				_searchCount++;
				continue;
			}
			_forgeViewer.viewer.search( _GUIDs[idx], 
								        function( dbIds ) { _self.onSearch( dbIds ) }, 
								        function( errorNum, errorMsg ) { _self.onSearchErro(  errorNum, errorMsg ) } );
			return;
		}
	};

	this.enableMultiSelect = function(
		enable
	) {
		if( enable )
		{
			_forgeViewer.viewer.setCanvasClickBehavior( _clickActionsMultiSelect );
		}
		else
		{
			_forgeViewer.viewer.setCanvasClickBehavior( _clickActionsSingleSelect );
		}
	};

	//==============================================================================
	// Processing for when the selection has changed and all necessary properties 
	// for the new selection set have been fetched
	//==============================================================================
	this.fireSelectionChange = function(
		selection
	) {
		if( _isAutoZoom && selection.length > 0 )
		{
			try  // This is failing in 1.2.16.  Possible time race?
			{
				var boundingBox = _forgeViewer.viewer.utilities.getBoundingBox( false );
				_forgeViewer.viewer.navigation.fitBounds( true, boundingBox );
			}
			catch( e )
			{
				console.log( e );
			}
		}
		
		_forgeViewer.fireSelectionListener( selection );
	};

	this.fixupSelection = function(
		dbIds
	) {
		if( typeof dbIds === "number" ) 
		{
			dbIds = [dbIds];
		}
		return dbIds;
	};

	// Soom the current view to focus on the item in the selection set indicated by index
	this.focusOnSelectedItem = function(
		itemIndex	// The idex of the item in the selection list
	) {
		if( itemIndex < 0 || itemIndex >= _selectionList.length )
		{
			return;
		}
		if( _focusIndex == itemIndex )
		{
			return;
		}
		var dbIds = [];
		_focusIndex = itemIndex;
		dbIds[0] = _selectionList[itemIndex].dbId;
		this.zoomToContext( dbIds );
		
		_forgeViewer.fireFocusListener( _focusIndex );
	};

	this.getSelection = function()
	{
		if( !_selectionList || _selectionList.length == 0 )
		{
			return null;
		}	
		return _selectionList[ _focusIndex ];
	}

	this.getSelectionList = function()
	{
		return _selectionList;
	}

	this.loadIds = function()
	{
		var _self = this;

		if( _gemotryLoaded  && _objectTreeCreate )
		{
			if( _docType != "rvt"  && _docType != "zip" )
			{
				var dbIds = [];
				for( var i = 1; i < _objectCount; ++i )
				{
					dbIds.push(i);
				}
				
				var propList = [];
				propList.push( _binding );
				IBM.LMV.attribLoading = true;
				_idsLoading = true;
				_forgeViewer.viewer.model.getBulkProperties( dbIds, propList, 
					                                    function( results )            { _self.onExternalIdLoad( results ) }, 
					                                    function( errorNum, errorMsg ) { _self.onExternalIdLoadError(  errorNum, errorMsg ) } );
			}
		}
	}

	this.onExternalIdLoad = function(
		results
	) {
		IBM.LMV.attribLoading = false;
		if( !results || results.lenght == 0 ) 
		{ 
			_idsLoading = false;
			this.doDeferedSearch();
			return;
		}
		
		var guid2dbIdArray = {};
		var dbId2GuidArray = [];
		var match = false;
		for( var i = 0; i < results.length; i++ )
		{
			if( results[i].properties && results[i].properties.length > 0 )
			{
				var guid = results[i].properties[0].displayValue;
				guid2dbIdArray[ guid ] = results[i].dbId;
				dbId2GuidArray[ results[i].dbId ] =  guid;
				match = true;
			}
		}

		if( match )
		{
			_guid2dbIdArray = guid2dbIdArray;
			_dbId2GuidArray = dbId2GuidArray;
		}

		_idsLoading = false;

		this.doDeferedSearch();
	}

	this.onExternalIdLoadError = function(
		errorNum,
		errorMsg
	) {
		IBM.LMV.displayError( errorMsg );
		_idsLoading = false;

		this.doDeferedSearch();
	}

	// Search cannot be executed until the geometry is fully loaded
	this.onGeometryLoad = function(
		eventData
	) {
		_gemotryLoaded = true;

		if( _docType == "nwd" || _docType == "ifc" )
		{
			_forgeViewer.viewer.setSelectionMode( Autodesk.Viewing.SelectionMode.FIRST_OBJECT );
		}

		if( _gemotryLoaded && _objectTreeCreate )
		{
			this.loadIds();
			this.doDeferedSearch();
		}
	};

	this.onObjectTreeCreate = function(
		eventData
	) {
		_objectTreeCreate = true;
		_objectCount = eventData.svf.instanceTree.objectCount;
		if( _gemotryLoaded && _objectTreeCreate )
		{
			this.loadIds();
			this.doDeferedSearch();
		}
	}

	//==============================================================================
	// Callback method for viewer getProperty method used to get GUIDs for each
	// item in the selection set
	//==============================================================================
	this.onPropertyLoad = function( 
		propertyList
	) {  
		for( var i = 0; propertyList != null && i < propertyList.length; i++ )
		{
			var properties = propertyList[i];
			var bindingValue = null;
			for( var p = 0; p < properties.properties.length; p++ ) 
			{
				if( properties.properties[p].displayName === _binding ) 
				{
					bindingValue = properties.properties[p].displayValue;
					break;
				}
			}
			if( bindingValue != null )
			{
				for( var i = 0; i < _selectionList.length; i++ )
				{
					if( _selectionList[i].dbId == properties.dbId )
					{
						_selectionList[i].guid = bindingValue;
						break;
					}
				}
			}
		}

		this.fireSelectionChange( _selectionList );
	};

	this.onPropertyLoadError = function(		// onErrorCallback
		errorNum,
		errorMsg
	) {	
		IBM.LMV.displayError( IBM.LMV.Strings.ERR_PROPERTY + errorNum + " - " + errorMsg);
		this.fireSelectionChange( _selectionList );
	};

	this.onSearch = function( 
		dbIds
	) {
		_searchCount++;
		_searchResult = _searchResult.concat( dbIds );
		if( _searchCount >= _GUIDs.length )
		{
			_searchCount       = 0;
			_GUIDs = [];
			this.select( _searchResult );
		}
		else
		{
			this.doSearch();
		}
	};

	this.onSearchError = function(		// onErrorCallback
		errorNum,
		errorMsg
	) {		
		if( _searchCount >= _GUIDs.length )
		{
			_searchCount       = 0;
			_GUIDs = [];
		}
		IBM.LMV.displayError( IBM.LMV.Strings.ERR_SEARCH + errorMsg);
		this.doSearch();
	};

	//==============================================================================
	// Registered with the viewer as SELECTION_CHANGED_EVENT listener
	//==============================================================================
	this.onSelectedCallback = function(
		selection
	) {
		if( !_initialized ) return;

		var newSelection = [];
		if( selection.dbIdArray.length > 0 ) 
		{
			for( var i = 0; i < selection.dbIdArray.length; i++ ) 
			{
				var dbId = selection.dbIdArray[i];
				
				// Check to see if the modefied node is a duplicate. This could happen if nodes
				// are selected using different mechanisms
				var match = false;
				for( var k = 0; k < newSelection.length; k++ )
				{
					if( dbId == newSelection[k].dbId )
					{
						match = true;
						break;
					}
				}
				if( !match )
				{
					var sel = new _Node();
					sel.dbId = dbId;
					sel.guid = "";
					newSelection.push( sel );
				}
			}
			
			this.updateSelection( newSelection );
		} 
		else 
		{
			_selectionList = [];
			this.fireSelectionChange( _selectionList );
		}
	};

	this.select = function(
		dbIds
	) {
		dbIds = this.fixupSelection( dbIds );
		_forgeViewer.viewer.select( dbIds );
	};

	this.selectByGUID = function(
		GUIDs
	) {
		if( !_initialized || _modelLoading ) 
		{
			_deferedSearch = GUIDs;
			return;
		}

		if( GUIDs == null )
		{
			return;
		}
		
		// Only one search active at a time
		if( _searchCount > 0 ) return;
		
		if( typeof GUIDs === "string" ) 
		{
			if( GUIDs == null || GUIDs == "" )
			{
				return;
			}
			GUIDs = [GUIDs];
		}
		if( GUIDs.length == 0 )
		{
			return;
		}

		_searchCount       = 0;
		_searchResult      = [];
		
		// Revit models have sidecar GUID map - Search synchornusly
		if( _dbId2GuidArray.length > 0 )
		{
			for( var i = 0; i < GUIDs.length; i++ )
			{
				var dbId = _guid2dbIdArray[ GUIDs[i] ];
				if( dbId != null )
				{
					_searchResult.push( dbId );
				}
			}
			this.select( _searchResult );
			return;
		}
		
		_GUIDs = GUIDs;
		this.doSearch();
	};

	this.zoomIntoSelection = function()
	{
		if( _deferedSearch )
		{
			_deferedZoom = "center";
			return;
		}
		_forgeViewer.viewer.setActiveNavigationTool( "firstperson" );
		
		_forgeViewer.viewer.navigation.orientCameraUp();
		var boundingBox = _forgeViewer.viewer.utilities.getBoundingBox( false );
		var l = boundingBox.max.x - boundingBox.min.x;
		var w = boundingBox.max.y - boundingBox.min.y;
		var h = boundingBox.max.z - boundingBox.min.z;

		// The camera points toward the center of the selection of position it a bit off center.
		var pos = new THREE.Vector3( boundingBox.min.x + l / 3, 
			                         boundingBox.min.y + w / 3, 
			                         boundingBox.min.z + h / 2 );
		_forgeViewer.viewer.navigation.setPosition( pos );
	}
	
	this.setAutoZoomMode = function(
		enable
	) {
		_isAutoZoom = enable;
	},

	this.toggle = function(
		dbIds
	) {
		dbIds = this.fixupSelection( dbIds );
		_forgeViewer.viewer.toggleSelect( dbIds );
	};

	//==============================================================================
	// Compare the old selection to the new to determin if it has changed.  For 
	// any element common between old and new selection, copy the GUID.  For any
	// element unique to the new selection, spawn a async getPropery call to get
	// the GUID. 
	// If there is an async getProperty call, the fireSelectionChanged event is 
	// called from that call back, if not it is called from here.
	// @return true if the selection has changed, false if not
	//
	// Some model contain info to build a static GUI<=>dbid map set.  If this is
	// available, us it instead of async calls.
	//==============================================================================
	this.updateSelection = function(
		newSelection
	) {
		var _self = this;

		// Can't lookup the GUID until the model is fully loaded
		if( _modelLoading )
		{
			return;
		}

		var match = true;
		if(	_selectionList.length !=  newSelection.length )
		{
			match = false;
		}
		
		var foucsGUID = _selectionList[ _focusIndex ];
		_focusIndex = 0;
		for( var s = 0; s < newSelection.length; s++ )
		{
			for( var i = 0; i < _selectionList.length; i++ )
			{
				if( _selectionList[i].dbId != newSelection[s].dbId )
				{
					match = false;
					continue;
				}
				newSelection[s].guid = _selectionList[i].guid;
				if( newSelection[s].guid == foucsGUID )
				{
					_focusIndex = s;
				}
			}
		}
		if( match )
		{
			return false;
		}

		_selectionList = newSelection;

		var async = false;
		if( _dbId2GuidArray.length > 0 )
		{
			for( var i = 0; i < _selectionList.length; i++ )
			{
				var dbId = _selectionList[i].dbId;
				var guid = this.getGuidByNodeId( dbId );
				if( !guid )
				{
					_selectionList.splice( i, 1 );
					i--;
					continue;
				}
				_selectionList[i].guid = guid;
			}
		}
		else
		{
			async = true;
			var dbIds = [];
			for( var s = 0; s < _selectionList.length; s++ )
			{
				dbIds.push( _selectionList[s].dbId );
			}

			var filter = [];
			filter.push( _binding );

			_forgeViewer.viewer.model.getBulkProperties( dbIds, filter, 
				                                         function( propertyList )       { _self.onPropertyLoad( propertyList ) }, 
				                                         function( errorNum, errorMsg ) { _self.onPropertyLoadError( errorNum, errorMsg ) } );
		}
		
		if( ! async )
		{
			this.fireSelectionChange( _selectionList );
		}
		return true;
	};

	this.zoomToContext = function( 
		dbIds
	) {
		if( !_isAutoZoom ) return;
		try	
		{
			_forgeViewer.viewer.fitToView( dbIds );	
		}
		catch( e )
		{
			console.log( e );
		}
	};

	//************************************************************************************************
	//
	//		GUID Array Manager
	//
	// Revit models keep the GUID in a sidecar property file that must me loaded seperatly
	//************************************************************************************************
	//call this in loadDocument()
	this.loadGUIDS = function (doc) 
	{
		if( _docType != "rvt" && _docType != "zip"  )
		{
			_guidLoading = false;
			return;
		}

		var _self = this;
		_guidLoading = true;
		//get property db path
		var propDbPath = doc.getPropertyDbPath();
		propDbPath = propDbPath + "objects_ids.json.gz";

		if( !propDbPath.startsWith( "http") )
		{
			propDbPath = "https://developer.api.autodesk.com//derivativeservice/v2/derivatives/"  + propDbPath;
		}

		IBM.LMV.trace('propDbPath:' + propDbPath);
	 
		var xhr = new XMLHttpRequest();
		xhr.open('GET', propDbPath, true);
		xhr.setRequestHeader('Access-Control-Allow-Origin', '*');
		var token = this.authToken;
		xhr.setRequestHeader('Authorization', 'Bearer ' + _forgeViewer.authToken.access_token );
		xhr.responseType = 'arraybuffer';
	 
		xhr.onload = function () { _self.onGUIDLoad( this ); };
		xhr.send();
	};
	
	this.onGUIDLoad = function (
		xhr
	) {
 
		if( xhr.readyState != 4 )  
		{ 
			return; 
		}

		if( xhr.status != 200 )
		{
			var msg;
			try
			{
				msg = xhr.responseText;
			}
			catch( e ) {} // Ignore
			if( !msg ) msg = xhr.statusText;
			IBM.LMV.RESTError( xhr.status, IBM.LMV.Strings.ERR_REST, msg );
			_guidLoading = false;
			this.doDeferedSearch();
			return;
		}
 
 		var dbs = xhr.response;
 
		var rawbuf = new Uint8Array(dbs);
		//It's possible that if the Content-Encoding header is set,
		//the browser unzips  the file by itself, so let's check if it did.
		if (rawbuf[0] == 31 && rawbuf[1] == 139) 
		{
			try
			{
				rawbuf = new Zlib.Gunzip(rawbuf).decompress();
			}
			catch( e )
			{
				_guidLoading = false;
				IBM.LMV.displayError( e );
				console.error( e );
				return;
			}
		}
 
		var str = this.ab2str(rawbuf);
 
		// Check if there is a JSom error message
		if( str.charAt(0) === '{' )
		{  
			var msg = JSON.parse(str);
			var errorText = "";
			for( var txt in msg )
			{
				errorText = errorText + msg[txt] + "\n";
			}
			IBM.LMV.displayError( errorText );
			_guidLoading = false;
			this.doDeferedSearch();
			return;
		}

		_dbId2GuidArray = str.split(',');
		
		for( var i = 0; i < _dbId2GuidArray.length; i++ )
		{
			var entry = _dbId2GuidArray[i];
			_dbId2GuidArray[i] = this.uniqueId2GUID( _dbId2GuidArray[i] );
			if(  _dbId2GuidArray[i] != "" )
			{
				_guid2dbIdArray[ _dbId2GuidArray[i] ] = i;
			}
		}
		_guidLoading = false;
		this.doDeferedSearch();
	};

	// ArrayBuffer to string
	this.ab2str = function(buf) 
	{
		var chars = new Uint8Array(buf);
	 
		//http://codereview.stackexchange.com/questions/3569/pack-and-unpack-bytes-to-strings 
		//throw a "RangeError: Maximum call stack size exceeded" exception 
		//in browsers using JavaScriptCore (i.e. Safari) if chars has a length 
		//greater than 65536
		//return String.fromCharCode.apply(null, chars);
	 
		var s = "";
		for (var i = 0, l = chars.length; i < l; i++)
		{
			s += String.fromCharCode(chars[i]);
		}
		return s;
	};
	
	this.uniqueId2GUID = function( uniqueId )
	{	
		if( uniqueId == null )
		{
			return null;
		}
		
		// uniqueId formate:
		// "49b86b5c-d743-45a4-83ea-d49359f1ad2a-00093467"
		var uniqueId     = uniqueId.substring( 3, 48 );
		if( uniqueId == "" )
		{
			return "";
		}
		var idString     = uniqueId.substring( 37 );
		var end          = uniqueId.substring( 28, 36 );
	
		var xor          = "";
		for( var i = 0; i < 8; i++ )
		{
			var char1 = idString.charAt( i );
			var char2 = end.charAt( i );
			var v1 = parseInt( char1, 16 );
			var v2 = parseInt( char2, 16 );
			var char3 = (v1 ^ v2).toString( 16 );
			xor = xor +  char3;	
		}
	
		return uniqueId.substring( 0, 28 ) + xor;
	};

	this.getGuidByNodeId = function( nodeId ) 
	{
		var guid = null;
		if( _dbId2GuidArray.length > 0 ) 
		{
			guid = _dbId2GuidArray[nodeId];
		}
		return guid;
	};
};

/**
  *****************************************************************************************************
  * Message Box
  *****************************************************************************************************
  */
IBM.LMV.MessageBox = function (
	parent, message, title, buttons, callback
) {
	this.parent         = parent;
	this.message        = message;
	this.buttons        = buttons;
	this.callback       = callback;
	
	if( title == null || title == "" )
	{
		title = message;
	}
	
	if( this.buttons == null )
	{
		this.buttons = [ "CLOSE" ];
	}
	
	Autodesk.Viewing.UI.DockingPanel.call( this, parent, "MessageBox", title, { localizeTitle: false } );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height   = "auto";
	this.container.style.width    = "auto";
	this.container.style.maxWidth = "600px";
	this.container.style.resize   = "none";
	this.container.style.zIndex   = 1000;
	
	this.onButtonClick = function(
		e
	) {
		this.uninitialize();
		if( this.callback )
		{
			if( e.originalTarget ) 	// Firefox
			{
				this.callback( e.originalTarget.id );
			}
			else
			{
				this.callback( e.srcElement.id );
			}
		}
	};
	
	this.makeButton = function(
		row, id, label
	) {
		var cell       = row.insertCell();
		cell.id         = id;
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = label;
		this.initializeCloseHandler( cell );
		var _self = this;
		
		this.addEventListener( cell, 'click', function (e) { _self.onButtonClick( e ); }, false );
	}
};

IBM.LMV.MessageBox.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.MessageBox.prototype.constructor = IBM.LMV.MessageBox;

IBM.LMV.MessageBox.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );

	var scrollContainer = this.createScrollContainer( {} );
	scrollContainer.innerHTML = this.message;
	scrollContainer.className = "maxlmv_messageBox";
	this.container.appendChild( scrollContainer );
	
	var btnBar       = document.createElement("TABLE");
	btnBar.name      = "MessageBoxBtnBar";
	btnBar.style.cssFloat = "right";
	this.btnBar      = btnBar.insertRow( 0 );
	

	this.container.appendChild( btnBar );
	
};

IBM.LMV.MessageBox.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		for( var i = 0; i < this.buttons.length; i++ )
		{
			switch( this.buttons[i] )
			{
				case "CANCEL":
					this.makeButton( this.btnBar, this.buttons[i], IBM.LMV.Strings.DLG_BTN_CANCEL );
					break;
				case "CLOSE":
					this.makeButton( this.btnBar, this.buttons[i], IBM.LMV.Strings.DLG_BTN_CLOSE );
					break;
				case "OK":
					this.makeButton( this.btnBar, this.buttons[i], IBM.LMV.Strings.DLG_BTN_OK );
					break;
				case "YES":
					this.makeButton( this.btnBar, this.buttons[i], IBM.LMV.Strings.DLG_BTN_YES );
					break;
				case "NO":
					this.makeButton( this.btnBar, this.buttons[i], IBM.LMV.Strings.DLG_BTN_NO );
					break;
			}
		}
		
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
	}
};