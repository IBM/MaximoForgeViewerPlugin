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

IBM.createNS( "IBM.Maximo.BIMField" );
IBM.ewNS = IBM.createNS( "IBM.Maximo.BIMEveryWhere" );

IBM.ewNS.Strings = {
	KEY_OF                  : "-of",
	KEY_DASH                : "--",
	KEY_OPEN_FILTER         : "-Open Filter",
	KEY_CLOSE_FILTER        : "-Close Filter",
	KEY_FILTER_TABLE        : "-Filter Table",
	KEY_PREV_PAGE           : "-Previous Page",
	KEY_NEXT_PAGE           : "-Next Page",
	
	KEY_WO_CREATE_TITLE     : "-Create Work Order",
	KEY_WO_NO_SELECTION     : "-Nothing selected",
	KEY_WO_ATTACH_VIEW      : "-Attach current view?",
	KEY_WO_FIELD_VIEW       : "-Field view",
	KEY_TITLE_LOGIN_MSG     : "-Login",
	KEY_LOGIN_FAILURE       : "-Login failure",
	KEY_ASSET_NOT_BOUND     : "-Asset not bound to the model",
	KEY_NO_MODEL_FOUND      : "-No model found for asset: ",
	KEY_NO_ASSETS_FOUND     : "-No Assets found for asset UID:",
	KEY_NO_MODELS_FOR_QUERY : "-No models match the search criteria",
	KEY_MODEL_LOOKUP        : "-Model Lookup",
	KEY_CREATE_WO           : "-Create Work Order",
	KEY_WORK_HISTORY        : "-Work History",
	KEY_WO_HIST_TITLE       : "-Work History",

	// Not in Message Catalog	
};


/**
  *****************************************************************************************************
  * Create Work Order Dialog
  *****************************************************************************************************
  */
IBM.ewNS.WorkOrderDlg = function (
	parent, assetId
) {
	var scrollContainer;
	var descInput;
	var detailsInput;
	var priorityInput;
 
 	this.asset     = null;
	this.labels    = null;		
	this.parent    = parent;
	this.assetId   = assetId;
	
	Autodesk.Viewing.UI.DockingPanel.call( this, parent, "Maximo-WorkorderCreate-DLG", IBM.ewNS.Strings.KEY_WO_CREATE_TITLE );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "auto";
	this.container.style.width  = "418px";
	this.container.style.resize = "none";
	
	this.createWorkOrder = function()
	{
		if( this.descInput.value == null || this.descInput.value == "" )
		{
			var messageBox = new IBM.LMV.MessageBox( IBM.LMV.viewer.container, 
													  this.labels.DESCRIPTION,
													  IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD );
			messageBox.setVisible( true );
			return;
		}
		var that = this;
		var query = "";
		query = query + "assetnum"     + "=" + this.asset.ASSETNUM.content;
		query = query + "&siteid"      + "=" + this.asset.SITEID.content;
		query = query + "&description" + "=" + this.descInput.value;
		if( this.detailsInput.value && this.detailsInput.value != "" )
		{
			query = query + "&DESCRIPTION_LONGDESCRIPTION" + "=" + this.detailsInput.value;
		}
		if( this.priorityInput.value && this.priorityInput.value != "" )
		{
			query = query + "&WOPRIORITY" + "=" + this.priorityInput.value;
		}
		
		this.attachView = this.attachViewCB.checked;

		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/workorder";
		url = url + "?" + query;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onWorkOrderCreate( this ); };;
		xmlReq.open( "POST", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	}

	this.makeButtonBar = function(
		asset
	) {
		var _self = this;
		var btnBar       = document.createElement("TABLE");
		btnBar.name      = "maximo_WOBtnBar";
		btnBar.style.cssFloat = "right";
		var row          = btnBar.insertRow( 0 );

		if( asset )
		{
			var cell         = row.insertCell();
			cell.className = "maxlmv_DlgBoxButton";
			cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CREATE;
			cell.onclick   = function() { _self.createWorkOrder( this ); };
		}

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CLOSE;
		this.initializeCloseHandler( cell );

		this.container.appendChild( btnBar );
		
		this.addEventListener( cell, 'click', function (e) {
			_self.uninitialize();
		}, false );
	}
 
	this.makeWCreateWOScreen = function( asset )
	{
		var that = this;
		
		this.makeButtonBar( asset );
		
		if( asset == null )
		{
			this.scrollContainer.innerHTML = IBM.ewNS.Strings.MSG_NOT_ASSET;
			return;
		}
		
		this.scrollContainer.innerHTML = "";
		
		var dlgTable       = document.createElement("TABLE");
		dlgTable.name      = "Maximo-BIMField-CreateWO-Table";
		dlgTable.className = "maxlmv_DlgTable";

		var i = 0;
		var row          = dlgTable.insertRow( i++ );
		var cell         = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "auto";
		cell.innerHTML   = this.labels.ASSETNUM;
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "50%";
		cell.innerHTML   = asset.ASSETNUM.content;
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "auto";
		cell.innerHTML   = this.labels.SITEID;
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.style.width = "50%";
		cell.innerHTML   = asset.SITEID.content;
		if( asset.DESCRIPTION && asset.DESCRIPTION.content )
		{
			row              = dlgTable.insertRow( i++ );
			cell             = row.insertCell();
			cell.className   = "maxlmv_DlgText";
			cell.colSpan     = 4;
			cell.innerHTML   = asset.DESCRIPTION.content;
		}

		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgHeading";
		cell.colSpan     = 4;
		cell.innerHTML   = "Work Order";

		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.colSpan     = 4;
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this.labels.DESCRIPTION;

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
		cell.innerHTML   = this.labels.DESCRIPTION_LONGDESCRIPTION;

		row                  = dlgTable.insertRow( i++ );
		cell                 = row.insertCell();
		cell.colSpan         = 4;
		cell.className       = "maxlmv_DlgText";
		this.detailsInput    = document.createElement("TExTAREA");
		this.detailsInput.rows = 4;
		this.detailsInput.id = "Maximo-Field-WO-Detail";
		this.detailsInput.style.width = "400px";
		cell.appendChild( this.detailsInput );

		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this.labels.WOPRIORITY;
		
		cell                  = row.insertCell();
		cell.className        = "maxlmv_DlgText";
		this.priorityInput    = document.createElement("INPUT");
		this.priorityInput.id = "Maximo-Field-WO-Priority";
		this.priorityInput.style.width = "50px";
		this.priorityInput.type = "number";
		cell.appendChild( this.priorityInput );
		
		cell                   = row.insertCell();
		cell.innerHTML         = IBM.ewNS.Strings.KEY_WO_ATTACH_VIEW;
		cell.colSpan           = 2;
		cell.className         = "maxlmv_DlgText";
		this.attachViewCB      = document.createElement("INPUT");
		this.attachViewCB.type = "checkbox";
		this.attachViewCB.id   = "Maximo-Field-WO-Attach";
		cell.appendChild( this.attachViewCB );

		this.scrollContainer.appendChild( dlgTable );
	}

	this.lookupAssetByModelId = function(
		guid
	) {
		var that = this;
		
		this.currentAsset = null;
		if( guid == null || guid.length == 0 )
		{
			this.scrollContainer.innerHTML = IBM.LMV.Strings.ERR_NO_GUID;
			this.makeButtonBar( null );
			return;
		}
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/Asset";
		url = url + "?MODELID=~eq~" + guid;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onAsset( this ); };
		xmlReq.open( "GET", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	};

	this.onAsset = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			this.asset = null;
			this.controler.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
			this.uninitialize();
			return;
		}

		var mboSet = JSON.parse( request.responseText );
		var assets = mboSet.ASSETMboSet.ASSET;
		if( assets.length == 0 )
		{
			this.makeWCreateWOScreen( null );
			return;
		}
		this.asset = assets[0].Attributes;

		if( this.labels )		// Labels have to also have loaded
		{
			this.makeWCreateWOScreen( this.asset );
		}
	};
	
	this.onLabels = function(
 		mbo, labels
 	) {
		this.labels = labels;
		if( this.asset )		// Ths asset also hs to have loaded
		{
			this.makeWCreateWOScreen( this.asset );
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

		var mbo = JSON.parse( request.responseText );
		var messageBox = new IBM.LMV.MessageBox( IBM.LMV.viewer.container, this.woNum, IBM.ewNS.Strings.KEY_WO_CREATE_TITLE );
		messageBox.setVisible( true );
		this.uninitialize();
	};

	this.onWorkOrderCreate = function( request )
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
		
		var mbo = JSON.parse( request.responseText );
		this.woNum = mbo.WORKORDER.Attributes.WONUM.content;
		if( this.attachView )
		{
			this.saveView();
			return;
		}

		var messageBox = new IBM.LMV.MessageBox( IBM.LMV.viewer.container, this.woNum, IBM.ewNS.Strings.KEY_WO_CREATE_TITLE );
		messageBox.setVisible( true );
		this.uninitialize();
	};

	this.saveView = function()
	{
		var state = IBM.LMV.viewer.getState();
		var stateJSON = JSON.stringify( state ) ;
		var viewerState = btoa( stateJSON );

		var that = this;
		var form = "";
		form = form + "BUILDINGMODELID"     + "=" + IBM.LMV.modelId;
		form = form + "&WONUM"       + "=" + this.woNum;
		form = form + "&siteid"      + "=" + IBM.LMV.siteId;
		form = form + "&description" + "=" + IBM.ewNS.Strings.KEY_WO_FIELD_VIEW;
		form = form + "&viewerstate" + "=" + viewerState;

		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/bimlmvworkview";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onSaveView( this ); };;
		xmlReq.open( "POST", url, true );
		xmlReq.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" );
		xmlReq.setRequestHeader( "Accept", "application/json; charset=utf-8" );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send( form );
	}
};

IBM.ewNS.WorkOrderDlg.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.ewNS.WorkOrderDlg.prototype.constructor = IBM.ewNS.WorkOrderDlg;

IBM.ewNS.WorkOrderDlg.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );

	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.innerHTML = IBM.ewNS.Strings.KEY_ASSET_LOADIN_MSG;
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
};

IBM.ewNS.WorkOrderDlg.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		var _self = this;
		IBM.LMV.dataDictionary.getLabels( "workorder", function( mbo, labels ) { _self.onLabels(  mbo, labels ); } );
		this.lookupAssetByModelId( this.assetId );
	
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
  * Model details
  *****************************************************************************************************
  */
IBM.LMV.ModelDetails = function (
	parent, modelList, model
) {
	this.parent       = parent;
	this.modelList    = modelList;
	this.model        = model;
	this.xLoc         = -1;
	this.yLoc         = -1;
	
	this.displayModel = function()
	{
		this.modelList.displayModel( this.model );
		this.uninitialize();
	}
	
	this.makeButtonBar = function() 
	{
		var _self = this;
		var btnBar       = document.createElement("TABLE");
		btnBar.name      = "maximo_ModelFilterBtnBar";
		btnBar.style.cssFloat = "right";
		var row          = btnBar.insertRow( 0 );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.ewNS.Strings.DLG_BTN_VIEW;
		cell.onclick   = function() { _self.displayModel( this ); };
		this.initializeCloseHandler( cell );

		this.addEventListener( cell, 'click', function (e) {
			_self.uninitialize();
		}, false );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CLOSE;
		this.initializeCloseHandler( cell );
		
		this.addEventListener( cell, 'click', function (e) {
			_self.uninitialize();
		}, false );

		this.container.appendChild( btnBar );
	}

	this.makeModelScreen = function() 
	{
		var _self = this;
		
		var dlgTable       = document.createElement("TABLE");
		dlgTable.name      = "Maximo-BIMField-ModelDetails-Table";
		dlgTable.className = "maxlmv_DlgTable";

		var i = 0;
		var row;
		var cell;
		if( this.model.TITLE )
		{
			row              = dlgTable.insertRow( i++ );
			cell             = row.insertCell();
			cell.colSpan     = 4;
			cell.className   = "maxlmv_DlgText";
			cell.innerHTML   = this.model.TITLE;
		}
		
		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this.labels.LOCATION;
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this.model.LOCATION;
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this.labels.SITEID;
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.innerHTML   = this.model.SITEID;
		
		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.colSpan     = 4;
		cell.innerHTML   = this.model.FORMATTEDADDRESS;
		
		if( this.model.DESCRIPTION  )
		{
			row              = dlgTable.insertRow( i++ );
			cell             = row.insertCell();
			cell.className   = "maxlmv_DlgText";
			cell.colSpan     = 4;
			cell.innerHTML   = this.model.DESCRIPTION;
		}

		if( this.model.DESCRIPTION_LONGDESCRIPTION  )
		{
			row              = dlgTable.insertRow( i++ );
			cell             = row.insertCell();
			cell.className   = "maxlmv_DlgText";
			cell.colSpan     = 4;
			cell.innerHTML   = this.model.DESCRIPTION_LONGDESCRIPTION;
		}
		
		row              = dlgTable.insertRow( i++ );
		cell             = row.insertCell();
		cell.className   = "maxlmv_DlgText";
		cell.colSpan     = 3;

		this.container.appendChild( dlgTable );

		this.makeButtonBar();
	}

	this.onButtonClick = function(
		e
	) {
		this.uninitialize();
		if( this.callback )
		{
			this.callback( e.srcElement.id );
		}
	};
	
	this.onHeadings = function( 
		mbo, labels 
	) {
		this.labels = labels;
		this.makeModelScreen();
	};

	Autodesk.Viewing.UI.DockingPanel.call( this, parent, "Maximo-ModelDetails", "Model Details" );
};

IBM.LMV.ModelDetails.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.ModelDetails.prototype.constructor = IBM.LMV.ModelDetails;

IBM.LMV.ModelDetails.prototype.initialize = function()
{
	var _self = this;
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );		// Don't call this to avoid titlebar create

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height   = "auto";
	this.container.style.width    = "auto";
	this.container.style.maxWidth = "300px";
	this.container.style.resize   = "none";
	this.container.style.zIndex   = "200";
};

IBM.LMV.ModelDetails.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		var _self = this;
		IBM.LMV.dataDictionary.getLabels( "BUILDINGMODEL",   
		                                  function( mbo, labels ) { _self.onHeadings(  mbo, labels  ); } );
		
		var parentHeight = this.parent.clientHeight;
		var parentWidth  = this.parent.clientWidth;
		var height       = this.container.clientHeight;
		var width        = this.container.clientWidth;

		if( width > parentWidth )
		{
			this.container.style.left = 0;
			this.container.style.width = "" + parentWidth + "px";
			width = parentWidth;
		}
		
		var left  = 0;
		if( this.xLoc < 0 )		// No xLoc specifed so just center
		{
			left = (parentWidth  - width ) / 2;
		}
		else		// Center on xLoc
		{
			left = this.xLoc - width / 2;
			if( left < 0 ) left = 0;
		}
		this.container.style.left = "" + left + "px";
		
		var top = 0;
		if( height > parentHeight )
		{
			this.container.style.top = 0;
			this.container.style.height = "" + parentHeight + "px";
		}
		if( this.yLoc < 0 )		// No yLoc specifed so just center
		{
			var top  = (parentHeight - height ) /2;
		}
		else
		{
			top = this.yLoc - height / 2;
			if( top < 0 ) top = 0;
		} 
		this.container.style.top = "" + top + "px";
	}
};

/**
  *****************************************************************************************************
  * Model filter
  *****************************************************************************************************
  */
 IBM.LMV.ModelFilter = function (
	parent, modelList, filter
) {
	this.parent       = parent;
	this.modelList    = modelList;
	if( !filter ) filter = {};
	this.filter       = filter;
	this.inputField   = {};
	this.xLoc         = -1;
	this.yLoc         = -1;
	
	this.filterFields = [
		"TITLE",
		"DESCRIPTION",
		"DESCRIPTION_LONGDESCRIPTION",
		"FORMATTEDADDRESS",
		"LOCATION",
		"SITEID" ];

	
	this.applyFilter = function()
	{
		var filter = {}
		var i = 0;
		for( var i = 0; i < this.filterFields.length; i++ )
		{
			var fieldName = this.filterFields[i];
			if( this.inputField[ fieldName ] )
			{
				var value = this.inputField[ fieldName ].value;
				if( value && value.length > 0 )
				{
					filter[ fieldName ] = value;
				}
			}
		}
		this.modelList.setFilter( filter );
		this.uninitialize();
	}
	
	this.makeButtonBar = function() 
	{
		var _self = this;
		var btnBar       = document.createElement("TABLE");
		btnBar.name      = "maximo_ModelFilterBtnBar";
		btnBar.style.cssFloat = "right";
		var row          = btnBar.insertRow( 0 );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_APPLY;
		cell.onclick   = function() { _self.applyFilter( this ); };
		this.initializeCloseHandler( cell );

		this.addEventListener( cell, 'click', function (e) {
			_self.uninitialize();
		}, false );

		var cell         = row.insertCell();
		cell.className = "maxlmv_DlgBoxButton";
		cell.innerHTML = IBM.LMV.Strings.DLG_BTN_CLOSE;
		this.initializeCloseHandler( cell );
		
		this.addEventListener( cell, 'click', function (e) {
			_self.uninitialize();
		}, false );

		this.container.appendChild( btnBar );
	}
	
	this.makeRow = function(
		dlgTable, idx, fieldName
	) {
		var _self = this;
		
		var row           = dlgTable.insertRow( idx );
		var cell          = row.insertCell();
		cell.className    = "maxlmv_DlgText";
		cell.innerHTML    = this.labels[ fieldName ];
		cell              = row.insertCell();
		cell.className    = "maxlmv_DlgText";
		var input         = document.createElement("INPUT");
		input.id          = "Maximo-Field-Filter-" + fieldName;
		input.onkeypress  = function( evt ) { _self.onFilterKeyPress( evt ); };
		if( this.filter[ fieldName ] )
		{
			input.value = this.filter[ fieldName ];
		}
		cell.appendChild( input );
		this.inputField[ fieldName ] = input;
		return input;
	}

	this.makeModelScreen = function() 
	{
		var _self = this;
		
		var dlgTable       = document.createElement("TABLE");
		dlgTable.name      = "Maximo-BIMField-ModelFilter-Table";
		dlgTable.className = "maxlmv_Dlg";
		
		var i = 0;
		for( var i = 0; i < this.filterFields.length; i++ )
		{
			this.makeRow( dlgTable, i, this.filterFields[i] );
		}

		this.container.appendChild( dlgTable );
		
		this.makeButtonBar();
	}

	this.onButtonClick = function(
		e
	) {
		this.uninitialize();
	};
	
	this.onHeadings = function( 
		mbo, labels 
	) {
		this.labels = labels;
		this.makeModelScreen();
	};
	
	this.onFilterKeyPress = function(
		evt
	) {
		var keynum = 0;
		
		if(window.event) 	// IE
		{
			keynum = window.event.keyCode;
		}
		else if( evt.which )	 // Netscape/Firefox/Opera
		{
			keynum = evt.which;
		}
		
		if( keynum == 13 )
		{
			this.applyFilter();
		}
		return true;
	}

	Autodesk.Viewing.UI.DockingPanel.call( this, parent, "Maximo-ModelFilter", "Model Details" );
};

IBM.LMV.ModelFilter.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.ModelFilter.prototype.constructor = IBM.LMV.ModelFilter;

IBM.LMV.ModelFilter.prototype.initialize = function()
{
	var _self = this;
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height   = "auto";
	this.container.style.width    = "auto";
	this.container.style.maxWidth = "300px";
	this.container.style.resize   = "none";
	this.container.style.zIndex   = "200";
};

IBM.LMV.ModelFilter.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		this.modelList.enable( false );
		var _self = this;
		IBM.LMV.dataDictionary.getLabels( "BUILDINGMODEL",   
		                                  function( mbo, labels ) { _self.onHeadings(  mbo, labels  ); } );
		
		var parentHeight = this.parent.clientHeight;
		var parentWidth  = this.parent.clientWidth;
		var height       = this.container.clientHeight;
		var width        = this.container.clientWidth;

		if( width > parentWidth )
		{
			this.container.style.left = 0;
			this.container.style.width = "" + parentWidth + "px";
			width = parentWidth;
		}
		
		var left  = 0;
		if( this.xLoc < 0 )		// No xLoc specifed so just center
		{
			left = (parentWidth  - width ) / 2;
		}
		else		// Center on xLoc
		{
			left = this.xLoc - width / 2;
			if( left < 0 ) left = 0;
		}
		this.container.style.left = "" + left + "px";
		
		var top = 0;
		if( height > parentHeight )
		{
			this.container.style.top = 0;
			this.container.style.height = "" + parentHeight + "px";
		}
		if( this.yLoc < 0 )		// No yLoc specifed so just center
		{
			var top  = (parentHeight - height ) /2;
		}
		else
		{
			top = this.yLoc - height / 2;
			if( top < 0 ) top = 0;
		} 
		this.container.style.top = "" + top + "px";
	}
};

IBM.LMV.ModelFilter.prototype.uninitialize = function() 
{
	this.modelList.enable( true );
	Autodesk.Viewing.UI.DockingPanel.prototype.uninitialize.call( this );
};

/**
  *****************************************************************************************************
  * Map Controler
  *****************************************************************************************************
  */
IBM.Maximo.BIMField.mapControl = function(
	controler
) {
	this.controler    = controler;
	this.parent       = controler.mainPage;
	this.mapContainer = null;
	this.map          = null;
	this.overlay      = null;
	this.modelList    = null;
	this.markers      = [];						// Marker currently on the map
	
	this.mapOptions   = {
							center : { lat: -34.397, lng: 150.644 },
							zoom   : 8
					    }
	
	this.hide = function()
	{
		this.mapContainer.style.visibility = "hidden";
	};

	this.initialize = function()
	{
		var _self = this;
		this.makeMap();

		if( navigator.geolocation ) 
		{
			navigator.geolocation.getCurrentPosition( function( position ) { _self.onPosition( position ); },
			                                          function( error ) { _self.onPositionError( error  ); } );
		} 
	};  
	
	this.locate = function(
		model
	) {
		var coord = {};
		coord.lat = model.LATITUDEY;
		coord.lng = model.LONGITUDEX;
		this.map.setCenter( coord );
	}
	
	this.onModelList = function(
		modelList
	) {
		var _self = this;
		for( var i = 0; i < this.markers.length; i++ )
		{
			this.markers[i].setMap( null );
		}
		
		this.markers = [];
		this.modelList = modelList;
		
		for( var i = 0; i < modelList.length; i++ )
		{
			var url = modelList[i].URL;
			if( url.slice( 0, 4 ) == "http" ) 
			{
				continue;
			}

			var coord = {};
			coord.lat = modelList[i].LATITUDEY;
			coord.lng = modelList[i].LONGITUDEX;
			if( coord.lat == null || coord.lng == null )
			{
				continue;
			}
			
			var marker = new google.maps.Marker({
				position : coord,
				map      : this.map,
				title    : modelList[i].TITLE,
			});
			
			this.addMarkerListener( marker,  modelList[i] );
			
			this.markers.push( marker );
		}
	}
	
	this.addMarkerListener = function(
		marker, model
	) {
		var modelRef = new IBM.Maximo.BIMField.ModelReference( this, controler.modelList, model );
		marker.addListener( 'click', function() { modelRef.details( this ); }  );
	}

	this.makeMap = function(
	) {
		this.mapContainer = document.createElement("DIV");
		this.mapContainer.className = "maxlmv_map";
		
		this.map = new google.maps.Map( this.mapContainer, this.mapOptions );
		this.parent.appendChild( this.mapContainer );
		this.controler.makeLogo( this.mapContainer );
		
		this.overlay = new google.maps.OverlayView();
		this.overlay.draw = function() {};
		this.overlay.setMap( this.map ); 
		
		var mapToolBar = new IBM.ewNS.MapToolBar( this.controler.modelList , this );
		mapToolBar.createToolbar();
	};
	
	this.onMarkerSelect = function(
		marker, model
	) {
		this.controler.hide();
		controler.modelList.displayModel( model );
	}

	this.onPosition = function(
		position
	) {
		var coord = {};
		coord.lat = position.coords.latitude;
		coord.lng = position.coords.longitude;
		this.mapOptions.center.lat = position.coords.latitude;
		this.mapOptions.center.lng = position.coords.longitude;
		this.map.setCenter( coord );
	}
	
	this.onPositionError = function(
		error
	) {
		if( error.message )
		{
			this.controler.displayError( error.message );
		}
		else
		{
			this.controler.displayError( error );
		}
	}
	
	this.show = function()
	{
		this.mapContainer.style.visibility = "visible";
	};

};
 
/**
  *****************************************************************************************************
  * Instance data for each map marker
  *****************************************************************************************************
  */
IBM.Maximo.BIMField.ModelReference = function(
	mapControler, modelList, model
) {
	this.mapControler = mapControler;
	this.modelList    = modelList;
	this.model        = model;
	
	this.details = function( marker )
	{
	    var projection = this.mapControler.overlay.getProjection(); 
    	var pixel      = projection.fromLatLngToContainerPixel(marker.getPosition());
		/*
		var xLoc = null;
		var yLoc = null;
		if( e.clientX && e.clientY )
		{
			xLoc = e.clientX;
			yLoc = e.clientY;
		}
		else if( e.targetTouches  && e.targetTouches.length > 0 )
		{
			xLoc = e.targetTouches[0].clientX;
			yLoc = e.targetTouches[0].clientY;
		}
		else if( e.changedTouches  && e.changedTouches.length > 0 )
		{
			xLoc = e.changedTouches[0].clientX;
			yLoc = e.changedTouches[0].clientY;
		}
		*/
		var popup = new IBM.LMV.ModelDetails( this.mapControler.parent, this.modelList, this.model );
		popup.xLoc = pixel.x;
		popup.yLoc = pixel.y;
		popup.setVisible( true );
	}
	
	this.display = function()
	{
		this.modelList.displayModel( this.model );
	}
}

/**
  *****************************************************************************************************
  * Map toolbar
  *
  * Used the LMV toolbar to display a toolbar on the map home screen
  *****************************************************************************************************
  */
IBM.ewNS.MapToolBar = function(
	modelList,
	mapMgr
) {
	this.modelList = modelList;
	this.mapMgr    = mapMgr;
	
	this.createToolbar = function(
	) {
		var _self = this;
		
		var topToolBar = new Autodesk.Viewing.UI.ToolBar( "Maximo_map_toolbar" );
		topToolBar.container.style.top = "25px";
		
		var topSubToolbar = new Autodesk.Viewing.UI.ControlGroup( "Maximo_map_toolabr_submenu" );


		// Goto currnet GPS position
		var buttonGPSLocate = new Autodesk.Viewing.UI.Button("Maximo_map_toolbar_GPLLocate");
		buttonGPSLocate.icon.style.backgroundImage = "url(" + this.mapMgr.controler.imagePath + "/bim/360_tb_GPS.png" + ")";
		buttonGPSLocate.setToolTip( IBM.ewNS.Strings.DLG_BTN_LOCATE );
		buttonGPSLocate.onClick = function() { _self.onLocateButton( this ); };
		topSubToolbar.addControl( buttonGPSLocate );
		
		// Display model list
		var buttonModelList = new Autodesk.Viewing.UI.Button("Maximo_top_toolbar_DisplayModelList");
		buttonModelList.icon.style.backgroundImage = "url(" + this.modelList.imagePath + "/bim/tb_viewModel.png" + ")";
		buttonModelList.setToolTip( IBM.ewNS.Strings.MAP_BTN_MODELLIST );
		buttonModelList.onClick = function() { _self.onDisplayButton( this ); };
		topSubToolbar.addControl( buttonModelList );
		
		topToolBar.addControl( topSubToolbar );

		mapMgr.mapContainer.appendChild( topToolBar.container );
	}

	this.onLocateButton = function()
	{
		var _self = this.mapMgr;
		if( navigator.geolocation ) 
		{
			navigator.geolocation.getCurrentPosition( function( position ) { _self.onPosition( position ); },
			                                          function( error ) { _self.onPositionError( error  ); } );
		} 
	};

	this.onDisplayButton = function()
	{
		this.modelList.show();
	};
};

/**
  *****************************************************************************************************
  * Model Display button on the map
  *
  * From the map - redisplay the model list if it has been closed
  *****************************************************************************************************
  */
IBM.Maximo.BIMField.DisplayModeListButton = function(
	parent,	modelList
) {
	this.parent = parent;
	this.modelList = modelList;
	
	this.onDisplayButton = function()
	{
		this.modelList.show();
	};
    
	this.makeDisplay = function()
    {
		var _self = this;
		this.gpsButton = document.createElement("DIV");
		this.gpsButton.className = "maxlmv_ModelListDisplay_Tool";
		
        var ctrl       = document.createElement("IMG");
        ctrl.src       = this.modelList.imagePath + "/bim/tb_viewModel.png";
        ctrl.alt       = IBM.ewNS.Strings.MAP_BTN_MODELLIST;
        ctrl.title     = IBM.ewNS.Strings.MAP_BTN_MODELLIST;
        ctrl.className = "maxlmv_clickableImage";
        this.gpsButton.onclick   = function() { _self.onDisplayButton( this ); };
        this.gpsButton.appendChild( ctrl );

		this.parent.appendChild( this.gpsButton );
    };
    
    this.makeDisplay();
}
		
/**
  *****************************************************************************************************
  * Home screen
  *
  * Icon to got to the map (if active) or the model list from the model
  *****************************************************************************************************
  */
IBM.Maximo.BIMField.HomeScreen = function(
	controler, parent
) {
	this.controler = controler;
	this.parent    = parent;
	
	this.onHomeButton = function()
	{
		var _self = this.mapMgr;
		if( navigator.geolocation ) 
		{
			this.controler.showStartScreen();
		} 
	};
    
	this.makeDisplay = function()
    {
		var _self = this;
		this.gpsButton = document.createElement("DIV");
		this.gpsButton.className = "maxlmv_Home";
		
        var ctrl       = document.createElement("IMG");
		if( this.controler.mapsActive )
		{
	        ctrl.src  = this.controler.imagePath + "bim/map.jpg";
		}
		else
		{
	        ctrl.src   = this.controler.imagePath + "bim/tb_viewModel.png";
		}
        ctrl.alt       = IBM.ewNS.Strings.DLG_BTN_LOCATE;
        ctrl.title     = IBM.ewNS.Strings.DLG_BTN_LOCATE;
        ctrl.className = "maxlmv_clickableImage";
        this.gpsButton.onclick   = function() { _self.onHomeButton( this ); };
        this.gpsButton.appendChild( ctrl );

		this.parent.appendChild( this.gpsButton );
    };
    
    this.makeDisplay();
}

/**
  *****************************************************************************************************
  * Model list dialog
  *****************************************************************************************************
  */
IBM.Maximo.BIMField.ModelList = function(
	controler,
	forgeViewer,
	imagePath
) {
	const _forgeViewer = forgeViewer;
	this.parent       = controler.mainPage;
	this.onmodel      = null;
	this.controler    = controler;
	this.headerLoaded = false;
	this.imagePath    = imagePath;
	
	this.modelList    = [];
	this.filter      = {};
	this.pageSize     = 8;
	this.rowStart     = 0;
	this.rsCount      = 0;
	this.rsStart      = 0;
	this.rsTotal      = 0;
	this.hasGuid      = false;
	this.assetUID     = null;
	
	this.disableCover = null;
	
	this.currentModel = null;	// The currently displayed model.  A request to redisplay this just closes the model list
	
	this.columns = [ "LOCATION", "TITLE" ];		// List of fields from BuildingModel record to display
	
	this.scrollContainer = null;
	
	this.centerDlg = function()
	{
		var height  = window.innerHeight;
		var width   = window.innerWidth;
		if( this.container.clientWidth > width )
		{
			this.container.style.width = "" + width  + "px";
			this.container.style.left = 0;
		}
		else
		{
			var left = (width  - this.container.clientWidth ) / 2;
			this.container.style.left = "" + left + "px";
		}
		
		if( this.container.clientHeight > height )
		{
			this.container.style.height = "" + height + "px";
			this.container.style.top = 0;
		}
		else
		{
			var top  = (height - this.container.clientHeight ) /2;
			this.container.style.top = "" + top + "px";
		}
	};
	
	this.display = function( 
		filter 
	) {
		var _self = this;
		this.filter = filter;
		IBM.LMV.dataDictionary.getLabels( "BUILDINGMODEL",   
		                                  function( mbo, labels ) { _self.onHeadings(  mbo, labels  ); } );
		if( this.assetUID != null )
		{
			this.hasGuid = true;
			this.lookupModelByAssetUID(  this.assetUID );
		}
		this.lookupModelList( filter );
	}
	
	this.displayModel = function( 
		model 
	) {
		if( this.currentModel == model )
		{
			this.controler.hide()
			return;
		}
		this.currentModel = model;

		this.controler.reset();
		
		_forgeViewer.createViewer( this.controler.mainPage );
//		_forgeViewer.viewer.loadExtension( 'IBM.LMV.ContextMenuExtension' );
		IBM.LMV.toolBar = new IBM.LMV.toolBarExtension( _forgeViewer );
		_forgeViewer.viewer.addEventListener( Autodesk.Viewing.TOOLBAR_CREATED_EVENT, 
										function() { IBM.LMV.toolBar.onCreate() } );
//		_forgeViewer.ctrlViewer.style.top = "<%=toolbar_height%>" + "px";

		IBM.LMV.modelId  = model.BUILDINGMODELID;
		IBM.LMV.location = model.LOCATION;
		IBM.LMV.title    = model.TITLE;
		IBM.LMV.siteId   = model.SITEID;

		var searchAttrib = "Guid";
		if( model.ATTRIBUTENAME != null )
		{
			searchAttrib = model.ATTRIBUTENAME;
		}

		_forgeViewer.loadDocument( model.URL, searchAttrib );		

		this.displayedModel = model;
		this.controler.hide()
	};
	
	this.enable = function(
		state
	) {
		if( state )
		{
			if( this.disableCover )
			{
				this.disableCover.style.visibility = "hidden";
			}
		}
		else
		{
			if( this.disableCover == null )
			{
				this.disableCover = document.createElement( "DIV" );
				this.disableCover.className = "maxlmv_DlgDisable"
				this.container.appendChild( this.disableCover );
			}
			this.disableCover.style.visibility = "visible";
		}
	}
	
	this.hide = function()
	{
		this.setVisible( false );
	};

	// Check and see if a starting asset UID is specified on the URL.  
	this.hasStartingAsset = function()
	{
		var search = location.search;
		if( search.length > 0 )
		{
			var idx = search.indexOf( "?" );
			if( idx >= 0 )
			{
				search = search.substring( idx + 1 );
			}
			var searchList = search.split( "&" );
			for( var i = 0; i < searchList.length; i++ )
			{
				var term = searchList[i].split( "=" );
				if( term[0] == "assetuid" )
				{
					if( term.length >= 2 )
					{
						this.assetUID = term[1];
						return true;
					}
				}
			}
		}
	};
	
	/**
	  * REST request of all models that contain the asset with the specified UID
	  */
	this.lookupModelByAssetUID = function( 
		assetUID
	) {
		var _self = this;
		
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/os/BIMASSETMODELS/" + assetUID  + "?_compact=1";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onAssetModel( this ); };
		xmlReq.open( "GET", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	};

	/**
	  * REST request of all models (possible filtered)
	  */
	this.lookupModelList = function(
		filter
	) {
		var _self = this;
		
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/mbo/BuildingModel?_compact=1&_maxItems=" + this.pageSize + "&_rsStart=" + this.rowStart + "&_tc=1";
		if( filter )
		{
			this.filter = filter;
		}
		if( this.filter == null )
		{
			this.filter = {};
		}
		
		var query = "";
		for( var fieldName in filter )
		{
			query = query + "&" + fieldName + "=" + filter[ fieldName ];
		}
		url = url + query;
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onModelListLoad( this ); };
		xmlReq.open( "GET", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	};

	this.makeControlBar = function()
	{
		var _self = this;

		var ctrlBar       = document.createElement("TABLE");
		ctrlBar.name      = "modelCtrlBar";
		ctrlBar.className = "modelListControlBar";
		var row = ctrlBar.insertRow( 0 );
		
		var cell       = row.insertCell();
		var ctrl       = document.createElement("IMG");
		ctrl.src       = this.imagePath + "/bim/tb_find.png";
		ctrl.alt       = IBM.ewNS.Strings.KEY_FILTER_TABLE;
		ctrl.title     = IBM.ewNS.Strings.KEY_FILTER_TABLE;
		ctrl.className = "button";
		ctrl.onclick   = function( evt ) { _self.onDisplayFilter( this, evt ); };
		cell.appendChild( ctrl );
		
		cell           = row.insertCell();
		ctrl           = document.createElement("IMG");
		ctrl.src       = this.imagePath + "/bim/tb_previous.png";
		ctrl.alt       = IBM.ewNS.Strings.KEY_PREV_PAGE;
		ctrl.title     = IBM.ewNS.Strings.KEY_PREV_PAGE;
		ctrl.className = "button";
		ctrl.onclick   = function( evt ) { _self.onPrevPage( this, evt ); };
		cell.appendChild( ctrl );
		
		this.cellFirst = row.insertCell();
		cell = row.insertCell();
		cell.innerHTML = IBM.ewNS.Strings.KEY_DASH;
		this.cellLast  = row.insertCell();
		cell = row.insertCell();
		cell.innerHTML = IBM.ewNS.Strings.KEY_OF;
		this.cellTotal = row.insertCell();

		cell           = row.insertCell();
		ctrl           = document.createElement("IMG");
		ctrl.src       = this.imagePath + "/bim/tb_next.png";
		ctrl.alt       = IBM.ewNS.Strings.KEY_NEXT_PAGE;
		ctrl.title     = IBM.ewNS.Strings.KEY_NEXT_PAGE;
		ctrl.className = "button";
		ctrl.onclick   = function( evt ) { _self.onNextPage( this, evt ); };
		cell.appendChild( ctrl );
		
		cell           = row.insertCell();
		
		cell                    = row.insertCell();
		this.closeBtn           = document.createElement("IMG");
		this.closeBtn.src       = this.imagePath + "/bim/closebutton.png";
		ctrl.alt                = IBM.LMV.Strings.DLG_BTN_CLOSE;
		ctrl.title              = IBM.LMV.Strings.DLG_BTN_CLOSE;
		this.closeBtn.className = "button";
		this.closeBtn.onclick   = function() { _self.hide(); };
		this.closeBtn.style.display = "none";		// Hidden until redisplayed on top of a model
		cell.appendChild( this.closeBtn );
		
		this.container.appendChild( ctrlBar );
	};
	
	this.makeTable = function()
	{
		var _self = this;

		this.table = document.createElement("TABLE");
		this.table.className = "maxlmv_DlgTable";
		var thead = this.table.createTHead();
		this.header = thead.insertRow( 0 );
		for( var i = 0; i < this.columns.length; i++ )
		{
			this.header.insertCell( i );
		}
		if( this.controler.mapMgr && navigator.geolocation )
		{
			this.header.insertCell();
		}
	};
	
	this.onAssetModel = function(
		request
	) {
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			if(  request.status == 404 )
			{
				this.controler.displayError( IBM.ewNS.Strings.KEY_NO_ASSETS_FOUND + this.assetUID );
				this.lookupModelList();
				return;
			}
			this.controler.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
			return;
		}

		var objectStruct = JSON.parse( request.responseText );
		var assets = objectStruct.QueryBIMASSETMODELSResponse.BIMASSETMODELSSet.ASSET;
		if( assets == null || assets.lenght == 0 )
		{
			this.controler.displayError( IBM.ewNS.Strings.KEY_NO_ASSETS_FOUND + this.assetUID );
			return;
		}
		var modelId;
		if( assets.MODELID != null )
		{
			modelId = assets.MODELID;
		}
		var models = assets.BUILDINGMODEL;
		if( models == null || models.length == 0 )
		{
			this.controler.displayError( IBM.ewNS.Strings.KEY_NO_MODEL_FOUND  + assets[0].Attributes.ASSETNUM.content );
			return;
		}
		var modelList = [];
		for( var i = 0; i < models.length; i++ )
		{
			// Test for a NavisWOrks model and remove
			var url = models[i].URL;
			if( url.slice( 0, 4 ) != "http" ) 
			{
				modelList.push( models[i] )
			}
		}
		
		if( modelList.length == 0 )
		{
			this.controler.displayError( IBM.ewNS.Strings.KEY_NO_MODEL_FOUND  + assets[0].Attributes.ASSETNUM.content );
			return;
		}

		IBM.LMV.modelId  = modelList[ 0 ].BUILDINGMODELID;
		IBM.LMV.location = modelList[ 0 ].LOCATION;
		IBM.LMV.title    = modelList[ 0 ].TITLE;
		IBM.LMV.siteId   = modelList[ 0 ].SITEID;

		_forgeViewer.loadDocument( modelList[ 0 ].URL, modelList[ 0 ].ATTRIBUTENAME );		

		this.currentModel = modelList[ 0 ];
		this.controler.hide();
		if( modelId != null )
		{
			IBM.LMV.Selection.selectByGUID( modelId );
		}
		else
		{
			this.controler.displayError( IBM.ewNS.Strings.KEY_ASSET_NOT_BOUND );
		}
	};
	
	/**
	  * REST load of heading lables from MAXATTRIBUTES
	  */
	this.onHeadings = function( mbo, headings )
	{
		for( var i = 0; i < this.columns.length; i++ )
		{
			this.header.cells[i].innerHTML = headings[ this.columns[i] ];
		}
		this.headerLoaded = true;
	};

	/**
	  * REST load list of available models
	  */
	this.onModelListLoad = function( request ) 
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			this.controler.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
			return;
		}

		this.populateModelList( request.responseText );
	};
	
	this.onNextPage = function()
	{
		if( this.rowStart + this.pageSize > this.rsTotal )
		{
			return;
		}
		this.rowStart = this.rowStart + this.pageSize;
		this.lookupModelList();
	}

	this.onPrevPage = function()
	{
		if( this.rowStart == 0 )
		{
			return;
		}
		this.rowStart = this.rowStart - this.pageSize;
		if( this.rowStart < 0 )
		{
			this.rowStart = 0;
		}
		this.lookupModelList();
	}

	this.onDisplayFilter = function(
		ctrl, evt
	) {
		var popup = new IBM.LMV.ModelFilter( this.parent, this, this.filter );
		popup.xLoc = evt.clientX;
		popup.yLoc = evt.clientY;
		popup.setVisible( true );
	}

	this.onLocateButton = function(
		cell, evt
	) {
		this.eventsDisabled = true;
		var row          = cell.parentNode;
		var model        = this.modelList[ row.rowIndex -1 ];
		if( evt == null )
		{
			evt = event;
		}
		evt.stopPropagation();
		
		if( this.controler.mapMgr )
		{
			this.controler.mapMgr.locate( model );
		}
	}
  
	this.onLocationLink = function(
		link, evt
	) {
		var cell         = link.parentNode;
		var row          = cell.parentNode;
		var model        = this.modelList[ row.rowIndex -1 ];
		if( evt == null )
		{
			evt = event;
		}
		evt.stopPropagation();
		
		var model = this.modelList[ row.rowIndex - 1 ];
		if( this.controler.propertySheet == null )
		{
			this.controler.propertySheet = new IBM.LMV.MaximoPropertyPanel( this.controler.mainPage, _forgeViewer );
		} 
		IBM.LMV.MaximoPropertyFetchers.LOCATIONS.lookupMboByKey( this.controler.propertySheet, model.LOCATION, model.SITEID );
		this.controler.propertySheet.setVisible( true );
	}

	this.onModelDetail = function(
		row
	) {
		var popup = new IBM.LMV.ModelDetails(  this.parent, this, this.modelList[  row.rowIndex - 1 ] );
		popup.xLoc = event.clientX;
		popup.yLoc = event.clientY;
		popup.setVisible( true );
	}

	this.onModelSelect = function( row )
	{
		var list = document.getElementById( "maximo-modelList" );
		this.displayModel( this.modelList[ row.rowIndex - 1 ] );		
	};
	
	this.populateModelList = function(
		modelJSON
	) {
		var mboSet = JSON.parse( modelJSON );
		
		this.modelList = [];
		if( mboSet.BUILDINGMODELMboSet.rsCount != null )
		{
			this.rsCount   = mboSet.BUILDINGMODELMboSet.rsCount;
			this.rsStart   = mboSet.BUILDINGMODELMboSet.rsStart;
			this.rsTotal   = mboSet.BUILDINGMODELMboSet.rsTotal;

			this.cellFirst.innerHTML = this.rsStart + 1;
			this.cellLast.innerHTML  = this.rsStart + this.rsCount;
			this.cellTotal.innerHTML = this.rsTotal;
		}

		var modelList = mboSet.BUILDINGMODELMboSet.BUILDINGMODEL;
		
		while( this.table.rows.length > 1 )		// Leaver header and filter rows
		{
			this.table.deleteRow( 1 );
		}
		
		for( var i = 0; i < modelList.length; i++ )
		{
			// Test for a NavisWOrks model and remove
			var url = modelList[i].URL;
			if( url.slice( 0, 4 ) != "http" ) 
			{
				this.modelList.push( modelList[i] )
			}
		}
		
		if( this.onmodel )
		{
			this.onmodel( this.modelList );
		}
		
		if( this.modelList.length == 0 )
		{
			this.controler.displayMessageBox( IBM.ewNS.Strings.KEY_NO_MODELS_FOR_QUERY, IBM.LMV.Strings.KEY_MODEL_LOOKUP );
		}

		var _self = this;
		
		for( var i = 0; i < this.modelList.length; i++ )
		{
			var model = this.modelList[i];
			var row = this.table.insertRow( i + 1 );	// Skip heading and filter
			row.onclick  = function() { _self.onModelSelect( this ) };
			row.className   = "maxlmv_selectable";

			for( var col in this.columns )
			{
				var cell = row.insertCell( col );
				cell.className = "maxlmv_DlgText";
				if( model[ this.columns[ col ] ] != null )
				{
					if( this.columns[ col ] == "LOCATION" )
					{
						var ref = document.createElement("A");
						ref.className = "maxlmv_DlgProperties";
						ref.onclick   = function( evt ) { _self.onLocationLink( this, evt ); };
						ref.innerHTML = model[ this.columns[ col ] ];
						cell.appendChild( ref );
					}
					else
					{
						cell.innerHTML = model[ this.columns[ col ] ];
					}
				}
			}
			if( this.controler.mapMgr && navigator.geolocation )
			{
				var cell = row.insertCell();
				cell.className = "maxlmv_DlgText";
				if( model.LATITUDEY && model.LONGITUDEX )
				{
					var ctrl       = document.createElement("IMG");
					ctrl.src       = this.imagePath + "/bim/360_GPS.png";
					ctrl.alt       = IBM.ewNS.Strings.DLG_BTN_LOCATE;
					ctrl.title     = IBM.ewNS.Strings.DLG_BTN_LOCATE;
					ctrl.className = "maxlmv_clickableImage";
					cell.onclick   = function( evt ) { _self.onLocateButton( this, evt ); };
					cell.appendChild( ctrl );
				}
			}
		}
		
		this.scrollContainer.appendChild( this.table );

		if( !this.hasGuid )
		{
			this.show()
		}
		this.hasGuid = false;
	};
	
	this.setFilter = function(
		filter
	) {
		this.filter       = filter;
		this.rowStart     = 0;
		this.rsCount      = 0;
		this.rsStart      = 0;
		this.rsTotal      = 0;
		this.lookupModelList( filter );
	}
	
	this.show = function()
	{
		this.setVisible( true );
		if( this.controler.mapMgr == null )
		{
			this.centerDlg()
		}
	};
	
	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "", "Model List" );
}

IBM.Maximo.BIMField.ModelList.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.Maximo.BIMField.ModelList.prototype.constructor = IBM.Maximo.BIMField.ModelList;

IBM.Maximo.BIMField.ModelList.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );

	this.makeControlBar();

	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
	
	this.makeTable();

	this.container.style.height   = "auto";
	this.container.style.maxWidth = "600px";
	this.container.style.zIndex   = 200;

	this.hasStartingAsset();
};