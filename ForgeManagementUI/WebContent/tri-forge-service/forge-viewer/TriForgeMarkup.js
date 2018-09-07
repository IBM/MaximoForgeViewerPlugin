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
 
IBM.createNS( "IBM.LMV.Markup" );

IBM.LMV.Markup._lookupURL       = "/p/webapi/rest/v2/triBIMViewer/-1/forgeMarkup?query=true"
IBM.LMV.Markup._uploadStateURL  = "/p/webdata?name=forgeViewerState";
IBM.LMV.Markup._uploadMarkupURL = "/p/webdata?name=forgeMarkup";
IBM.LMV.Markup._downloadDataURL = "/p/webdata";
IBM.LMV.Markup._deleteDataURL   = "/p/webdata";
IBM.LMV.Markup._saveURL         = "/p/webapi/rest/v2/triBIMViewer/-1/forgeMarkup?actionGroup=actions&action=create";
IBM.LMV.Markup._deleteURL       = "/p/webapi/rest/v2/triBIMViewer/-1/forgeMarkup?actionGroup=actions&action=delete";

/**
  *****************************************************************************************************
  * Selet and Display MArkup Dialog
  * Lists all available Markup, displays selection in the viewer
  *****************************************************************************************************
  */
IBM.LMV.Markup.ShowDlg = function(
	markupMgr, 		// View Wrapper
	viewer, 		// Autodesk LMV class
	imagePath,
	wonum
) {
	if( !wonum ) wonum = "000000";

	var _markupMgr = markupMgr;
	var _wonum     = wonum;
	
	this.forgeViewer   = _markupMgr.forgeViewer;
	this.contextRoot   = this.forgeViewer.contextRoot;
	
	var scrollContainer;
	var viewTable			// Ui table for view listing
	var descInput;
	var viewList;			// JSON Data from Maxumo on the list of curtrent views
	var currentView;		// Index of currently selected view in teh UI table
 
	this.viewer         = viewer;			
	this.parent         = viewer.container;
	this.imagePath      = imagePath;
	this.heading        = null;
	this.selectedRow    = null;
	this.eventsDisabled = false;
	

	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "TRIRIGA-ShowMarkup-DLG", IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );

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

		var url = this.forgeViewer.contextRoot + IBM.LMV._downloadDataURL;
		url += "/" + view.viewerState.contentID;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onViewerState( this ); };;
		xmlReq.open( "GET", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/json; charset=utf-8" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );
		xmlReq.send();
		
		if( view.markup && view.markup.contentID && view.markup.contentID != "" )
		{
			var url = this.forgeViewer.contextRoot + IBM.LMV._downloadDataURL;
			url += "/" + view.markup.contentID;
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { _self.onMarkup( this ); };;
			xmlReq.open( "GET", url, true );
			xmlReq.setRequestHeader( "Content-Type", "application/json; charset=utf-8" );
			xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
			xmlReq.send();
		}

		this.uninitialize();
	};
	
	this.loadMarkup = function(
		state
	) {
		var view = this.viewList[ this.selectedRow.rowIndex - 1 ];
		_markupMgr.showMarkup( this.viewer, state, view.description );
	}

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
		var msg = IBM.LMV.Strings.MSG_CONFIRM_DELETE_MAKRUP.replace( "{0}",  this.viewList[ this.currentView ].description );
		var messageBox = new IBM.LMV.MessageBox( this.parent, msg, IBM.LMV.Strings.DLG_TITLE_CONFIRM_DELETE, [ "OK", "CANCEL" ], 
		                                         function( button ) {_self.onDeleteResponse( button ); } );
		messageBox.setVisible( true );
		
	}
	
	this.onDeleteResponse = function(
		buttonId
	) {
		var _self = this;

		if( buttonId == "OK" )
		{
			var view = this.viewList[ this.selectedRow.rowIndex - 1 ];

			var url = this.forgeViewer.contextRoot + IBM.LMV._deleteDataURL;
			url += "/" + view.viewerState.contentID;
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { _self.onDeleteState( this ); };
			xmlReq.open( "DELETE", url, true );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );
			xmlReq.send();

			if( view.markup && view.markup.contentID && view.markup.contentID != "" )
			{
				var url = this.forgeViewer.contextRoot + IBM.LMV._deleteDataURL;
				url += "/" + view.markup.contentID;
				var xmlReq = new XMLHttpRequest();
				xmlReq.onreadystatechange = function() { _self.onDeleteMarkup( this ); };
				xmlReq.open( "DELETE", url, true );
				IBM.LMV.Auth.addAuthHeaders( xmlReq );
				xmlReq.send();
			}
	
			this.viewTable.deleteRow( this.currentView + 1 );
			this.viewList.splice( this.currentView, 1 );
		}
		else
		{
			this.eventsDisabled = false;
		}
	}
	
	this.lookupMarkup = function() 
	{
		var _self = this;

		this.makeTable();

		var query = {
				"page":	{"from":0,"size":20},
  				"filters":[
						{
							name     : "urn",
						 	operator : "equals",
						 	value    : this.forgeViewer.docURN,
						 },
						 {operator: "and"},
						{
							name     : "linkID",
						 	operator : "equals",
						 	value    : _wonum,
						 },
				],
				"sorts": [
					{
						"name":"description",
						"desc":false
					}
				]
			}

		if( this.filter && this.filter.length > 0 )
		{
			query.filters.push( {operator: "and"} );
			query.filters.push( {
				name     : "description",
			 	operator : "contains",
			 	value    : this.filter
			 } );
		}

		var url = this.forgeViewer.contextRoot + IBM.LMV.Markup._lookupURL;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onViews( this ); };
		xmlReq.open( "POST", url, true );
		xmlReq.setRequestHeader( "content-type", "application/json" );
		xmlReq.setRequestHeader( "Accept", "application/json" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( JSON.stringify( query ) );
	};
	
	this.makeTable = function()
	{
		if( this.viewTable && this.viewTable.parentNode )
		{
			this.viewTable.parentNode.removeChild( this.viewTable );
		}
		
		// Setup header row. Labels are filled async when they are downloaded
		this.viewTable           = document.createElement("TABLE");
		this.viewTable.className = "maxlmv_DlgTable";
		this.viewTable.name      = "TRIRIGA-BIMField-LOADVIEW-Table";
		var thead                = this.viewTable.createTHead();
		this.header              = thead.insertRow( 0 );
		var cell                 = this.header.insertCell( 0 );
		cell                     = this.header.insertCell( 1 );
		cell                     = this.header.insertCell( 2 );
		cell                     = this.header.insertCell( 3 );

		this.scrollContainer.appendChild( this.viewTable );
	}

	this.onApplyRow = function( row )
	{
		if( this.eventsDisabled )
		{
			return;
		}

		this.selectedRow = row;
		this.applyView();
	};


	this.onDeleteMarkup = function( request )
	{
		var _self = this;

		if( request.readyState != 4 )  
		{ 
			return; 
		}

		this.eventsDisabled = false;

		if( request.status == 403 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_UNAUTHORIZED, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
			return;
		}
		if( request.status != 200 && request.status != 404 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
			return;
		}
	}

	this.onDeleteState = function( request )
	{
		var _self = this;

		if( request.readyState != 4 )  
		{ 
			return; 
		}

		this.eventsDisabled = false;

		if( request.status == 403 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_UNAUTHORIZED, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
			return;
		}
		if( request.status != 200 && request.status != 404 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
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

		if( request.status == 403 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_UNAUTHORIZED, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
			return;
		}
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
		
		var text = this.viewList[ this.currentView ].detail;
		var detailsDlg = new IBM.LMV.DetailsDlg( this.parent, text,IBM.LMV.Strings.DLG_LABEL_LONG_DESC );
		detailsDlg.setVisible( true );
	}

	this.onSearchButton = function()
	{
		var value = this.ctrlSearch.value;
		if( this.filter == value ) return;
		this.filter = value;
		this.lookupMarkup();
	};
	
	/**********************************************************************/
	// Catch ENTER key in search field
	/**********************************************************************/
	this.onSearchKeyPress = function( evt )
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
			this.onSearchButton();;
		}
		return false;
	};

	this.onMarkup = function( request )
	{
		var _self = this;
		
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		this.eventsDisabled = false;

		if( request.status == 403 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_UNAUTHORIZED, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
			return;
		}
		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
			return;
		}

		setTimeout( function() { _self.loadMarkup( request.responseText ); }, 1000 );
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

		if( request.status == 403 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_UNAUTHORIZED, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
			return;
		}
		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );
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
	}
	
	this.populateList = function()
	{
		var _self = this;
		
		this.header.cells[0].innerHTML = IBM.LMV.Strings.DLG_LABEL_DESCRIPTION;
		this.header.cells[1].innerHTML = IBM.LMV.Strings.DLG_LABEL_CREATOR;
		this.header.cells[2].innerHTML = IBM.LMV.Strings.DLG_LABEL_LONG_DESC;

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
			cell.innerHTML = this.viewList[i].creator;

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
			var ctrl       = document.createElement("IMG");
			ctrl.src       = IBM.LMV.PATH_IMAGES + "360_delete.png";
			ctrl.alt       = IBM.LMV.Strings.DLG_BTN_DELETE_MARKUP;
			ctrl.title     = IBM.LMV.Strings.DLG_BTN_DELETE_MARKUP;
			ctrl.className = "maxlmv_clickableImage";
			cell.onclick   = function( evt ) { _self.onDeleteButton( this, evt ); };
			cell.appendChild( ctrl );
		}
		
		this.sizeAndPosition();
	};

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

IBM.LMV.Markup.ShowDlg.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.Markup.ShowDlg.prototype.constructor = IBM.LMV.Markup.ShowDlg;

IBM.LMV.Markup.ShowDlg.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );
	
	var _self = this;
	var searchBar    = document.createElement("DIV");
	searchBar.className = "maxlmv_propertyTitle";
	searchBar.innerHTML = IBM.LMV.Strings.TOOLBAR_SEARCH
	
	this.ctrlSearch = document.createElement("INPUT");
	this.ctrlSearch.className  = "maxlmv_search";
	this.ctrlSearch.onkeypress = function( evt ) { _self.onSearchKeyPress( evt ); };
	searchBar.appendChild( this.ctrlSearch );

	var ctrl       = document.createElement("IMG");
	ctrl.src       = IBM.LMV.PATH_IMAGES + "tb_find.png";
	ctrl.alt       = IBM.LMV.Strings.TOOLBAR_SEARCH;
	ctrl.title     = IBM.LMV.Strings.TOOLBAR_SEARCH;
	ctrl.className = "maxlmv_search";
	ctrl.onclick   = function( evt ) { _self.onSearchButton( this, evt ); };
	searchBar.appendChild( ctrl );
	this.container.appendChild( searchBar );

	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
};

IBM.LMV.Markup.ShowDlg.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		this.scrollContainer.innerHTML = "";

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

		this.lookupMarkup();
	}
};

/**
  *****************************************************************************************************
  * Create Save Markup Dialog
  *****************************************************************************************************
  */
IBM.LMV.Markup.SaveDlg = function(
	markupMgr,
	workOrderID
) {
	if( !workOrderID ) workOrderID = "000000";

	var scrollContainer;
	var descInput;
	var detailsInput;
	var priorityInput;
 
	this.parent        = IBM.LMV.viewer.container;
	this.markupMgr     = markupMgr;
	this.forgeViewer   = this.markupMgr.forgeViewer;
	this.contextRoot   = this.forgeViewer.contextRoot;
	this.workOrderID   = workOrderID;
	
	var sharedCB     = null;
	var descInput    = null;
	var detailsInput = null;
	
	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "TRIRIGA-SaveMarkup-DLG", IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "auto";
	this.container.style.width  = "418px";
	this.container.style.resize = "none";
	
	this.saveMarkup = function()
	{
		if( this.descInput.value == null || this.descInput.value == "" )
		{
			var messageBox = new IBM.LMV.MessageBox( IBM.LMV.viewer.container, 
					                                 IBM.LMV.Strings.DLG_LABEL_DESCRIPTION, 
													 IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD );
			messageBox.setVisible( true );
			return;
		}


		// Save viewer state
		var state       = IBM.LMV.viewer.getState();
		var stateJSON   = JSON.stringify( state ) ;

		this.markup      = null;
		this.viewerState = null;

		var _self = this;

		var url = this.contextRoot + IBM.LMV.Markup._uploadStateURL;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onSaveState( this ); };;
		xmlReq.open( "PUT", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/json; charset=utf-8" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( stateJSON );

		// Save Markup.  There may be no markup in which case this defaults to a saved view
		// assoicated with the record and any description
		if( IBM.LMV.markup.markups.length > 0 )
		{
			var markupData = IBM.LMV.markup.generateData();
			
			var url = this.contextRoot + IBM.LMV.Markup._uploadMarkupURL;
			var xmlReq = new XMLHttpRequest();
			xmlReq.onreadystatechange = function() { _self.onSaveMarkup( this ); };;
			xmlReq.open( "PUT", url, true );
			xmlReq.setRequestHeader( "Content-Type", "application/json; charset=utf-8" );
			xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
			IBM.LMV.Auth.addAuthHeaders( xmlReq );

			xmlReq.send( markupData );
		}
		else
		{
			this.markup = {};
		}
	}

	// Called when both the viewer state and the markup json have been successfully saved.
	// Saves the triForgeMarkup record with the descriptive data and the state and markup references.
	this.saveMarkupRecord = function()
	{
		var _self = this;

		var body = {
				data :{
					creator     : this.forgeViewer.currentUser._userAccount,
					linkID      : this.workOrderID,
					description : this.descInput.value,
					detail      : this.detailsInput.value,
					urn         : this.forgeViewer.docURN,
					viewerState : 
						{
							contentID : this.viewerState.contentID,
							fileName  : this.viewerState.fileName
						},
					markup : 
						{
							contentID : this.markup.contentID,
							fileName  : this.markup.fileName
						},
				}
			};

		var url = this.contextRoot + IBM.LMV.Markup._saveURL;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onSaveRecord( this ); };;
		xmlReq.open( "POST", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/json; charset=utf-8" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( JSON.stringify( body ) );
	}
	
	this.makeMarkupViewScreen = function() 
	{
		var _self = this;
		var btnBar       = document.createElement("TABLE");
		btnBar.name      = "MessageBoxBtnBar";
		btnBar.style.cssFloat = "right";
		var row          = btnBar.insertRow( 0 );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CREATE;
		cell.onclick   = function() { _self.saveMarkup( this ); };

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CLOSE;
		this.initializeCloseHandler( cell );

		this.container.appendChild( btnBar );
		
		this.addEventListener( cell, 'click', function (e) {
			_self.uninitialize();
		}, false );
		
		this.scrollContainer.innerHTML = "";
		
		var dlgTable       = document.createElement("TABLE");
		dlgTable.name      = "TRIRIGA-BIMField-SAVEMARKUP-Table";
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
		cell.innerHTML   = IBM.LMV.Strings.DLG_LABEL_MARKUP_DESC;

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
		this.descInput.id = "TRIRIGA-Field-WO-Description";
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
		this.detailsInput.id = "TRIRIGA-Field-SAVEMARKUP-Detail";
		this.detailsInput.style.width = "400px";
		cell.appendChild( this.detailsInput );

		this.scrollContainer.appendChild( dlgTable );
	}
	
	this.onSaveState = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status == 403 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_UNAUTHORIZED, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP );
			return;
		}
		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP );
			return;
		}

		var json = JSON.parse( request.responseText );
		this.viewerState = json;
		if( this.viewerState && this.markup )
		{
			this.saveMarkupRecord( json );
		}
	};

	this.onSaveMarkup = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status == 403 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_UNAUTHORIZED, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP );
			return;
		}
		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP );
			return;
		}

		var json = JSON.parse( request.responseText );
		this.markup = json;
		if( this.viewerState && this.markup )
		{
			this.saveMarkupRecord( json );
		}
	};

	this.onSaveRecord = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status == 403 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_UNAUTHORIZED, request.responseText, 
					           IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP);
			return;
		}
		if( request.status != 200 )
		{
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText,
					           IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP);
			return;
		}
		this.markupMgr.cancelMarkup();
		this.uninitialize();
	};
};

IBM.LMV.Markup.SaveDlg.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.Markup.SaveDlg.prototype.constructor = IBM.LMV.Markup.SaveDlg;

IBM.LMV.Markup.SaveDlg.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );
	
	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
};

IBM.LMV.Markup.SaveDlg.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		this.makeMarkupViewScreen();

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