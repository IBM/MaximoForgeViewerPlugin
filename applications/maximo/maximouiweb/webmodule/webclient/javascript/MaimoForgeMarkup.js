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

/**
  *****************************************************************************************************
  * Select Save View Dialog
  * Gets the viewer state, conversts to JSON, Base 64 encodes and saves to Maximo
  *****************************************************************************************************
  */
IBM.LMV.Markup.ShowDlg = function(
	markupMgr, 		// View Wrapper
	viewer, 		// Autodesk LMV class
	imagePath,
	wonum
) {
	var _markupMgr = markupMgr;
	var _wonum     = wonum;
	
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
	

	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "Maximo-ShowMarkup-DLG", IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP );

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
		this.viewer.restoreState( state ); 
		
		if( view.MARKUP != null && view.MARKUP != "" )
		{
			var _self = this;
			setTimeout( function() { _self.loadMarkup(); }, 1000 );
		}

		this.uninitialize();
	};
	
	this.loadMarkup = function()
	{
		var view = this.viewList[ this.selectedRow.rowIndex - 1 ];
		_markupMgr.showMarkup( this.viewer, view.MARKUP, view.DESCRIPTION );
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
		var msg = IBM.LMV.Strings.MSG_CONFIRM_DELETE_MAKRUP.replace( "{0}",  this.viewList[ this.currentView ].DESCRIPTION );
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
			url = url + "/mbo/bimlmvworkview/" + this.viewList[ this.currentView ].BIMLMVWORKVIEWID;
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
		var _self = this;

		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/bimlmvworkview" ;
        url = url +  "?BUILDINGMODELID=~eq~" + IBM.LMV.modelId;
        url = url +  "&wonum=~eq~"           + _wonum;
        url = url +  "&siteId=~eq~"          + IBM.LMV.siteId;
		url = url + "&_compact=1";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onViews( this ); };
		xmlReq.open( "GET", url, true );
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
		this.viewList = mboSet.BIMLMVWORKVIEWMboSet.BIMLMVWORKVIEW;
		
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

		var _self = this;
		IBM.LMV.dataDictionary.getLabels( "BIMLMVWORKVIEW", function( mbo, labels ) { _self.onHeadings(  mbo, labels  ); } );
		
		this.lookupSavedViews();
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
	var scrollContainer;
	var descInput;
	var detailsInput;
	var priorityInput;
 
	this.parent        = IBM.LMV.viewer.container;
	this.markupMgr     = markupMgr;
	this.workOrderID   = workOrderID;
	
	var sharedCB     = null;
	var descInput    = null;
	var detailsInput = null;
	
	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "Maximo-SaveMarkup-DLG", IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "auto";
	this.container.style.width  = "418px";
	this.container.style.resize = "none";
	
	this.saveView = function()
	{
		if( this.descInput.value == null || this.descInput.value == "" )
		{
			var messageBox = new IBM.LMV.MessageBox( IBM.LMV.viewer.container, 
													 this[ "BIMLMVWORKVIEW" ].DESCRIPTION, 
													 IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD );
			messageBox.setVisible( true );
			return;
		}
		var state = IBM.LMV.viewer.getState();
		var stateJSON = JSON.stringify( state ) ;
		var viewerState = btoa( stateJSON );

		var _self = this;
		var form = "";
		form = form + "BUILDINGMODELID" + "=" + IBM.LMV.modelId;
		form = form + "&WONUM"          + "=" + this.workOrderID;
		form = form + "&siteid"         + "=" + IBM.LMV.siteId;
		form = form + "&description"    + "=" + this.descInput.value;
		form = form + "&viewerstate"    + "=" + viewerState;
		if( this.detailsInput.value && this.detailsInput.value != "" )
		{
			form = form + "&DESCRIPTION_LONGDESCRIPTION" + "=" + this.detailsInput.value;
		}

		if( IBM.LMV.markup.markups.length > 0 )
		{
			var markupData = IBM.LMV.markup.generateData();
			
			form = form + "&markup"    + "=" + markupData;
			form = form + "&hasmarkup" + "=" + 1;
			IBM.LMV.Markup.data = markupData;
		}

		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/bimlmvworkview";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onSaveView( this ); };;
		xmlReq.open( "POST", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( form );
	}
	
	this.makeMarkupViewScreen = function(
		attributes	
	) {
		var _self = this;
		var btnBar       = document.createElement("TABLE");
		btnBar.name      = "MessageBoxBtnBar";
		btnBar.style.cssFloat = "right";
		var row          = btnBar.insertRow( 0 );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CREATE;
		cell.onclick   = function() { _self.saveView( this ); };

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
		cell.innerHTML   = IBM.LMV.Strings.DLG_LABEL_MARKUP_DESC;

		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.colSpan     = 4;
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this[ "BIMLMVWORKVIEW" ].DESCRIPTION;

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
		cell.innerHTML   = this[ "BIMLMVWORKVIEW" ].DESCRIPTION_LONGDESCRIPTION;

		row                  = dlgTable.insertRow( i++ );
		cell                 = row.insertCell();
		cell.colSpan         = 4;
		cell.className       = "maxlmv_DlgText";
		this.detailsInput    = document.createElement("TEXTAREA");
		this.detailsInput.rows = 4;
		this.detailsInput.id = "Maximo-Field-SAVEVIEW-Detail";
		this.detailsInput.style.width = "400px";
		cell.appendChild( this.detailsInput );

		this.scrollContainer.appendChild( dlgTable );
	}
	
	this.onLabelLookup = function(
		mbo, labels
	) {
		this[ mbo ] = labels;
		if( this[ "BUILDINGMODEL" ] && this[ "BIMLMVWORKVIEW" ] )
		{
			this.makeMarkupViewScreen( labels );
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
		var _self = this;
		IBM.LMV.dataDictionary.getLabels( "BUILDINGMODEL",   function( mbo, labels ) { _self.onLabelLookup(  mbo, labels  ); } );
		IBM.LMV.dataDictionary.getLabels( "BIMLMVWORKVIEW", function( mbo, labels ) { _self.onLabelLookup(  mbo, labels  ); } );

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