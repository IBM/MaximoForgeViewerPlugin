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

IBM.LMV.Markup.EVENT_SHOW = "EVENT_SHOW";
IBM.LMV.Markup.EVENT_HIDE = "EVENT_HIDE";
IBM.LMV.Markup.data       = null;

IBM.LMV.Markup.MarkupMgr = function() 
{
	this.modeChangeListeners = [];
	this.viewer              = null;
	this.markup              = null;
	this.toolbar             = null;
	this.wonum               = null;
	
	this.styleDlg            = null;
	this.activeTool          = null;
	this.currentStyle        = null;
	this.defaultStyle        = null;
	this.isText              = false;
	
	this.init = function(
		viewer
	) {
		var _self = this;
		
		this.viewer = viewer;
		var markup = IBM.LMV.viewer.getExtension("Autodesk.Viewing.MarkupsCore");
		if( markup == null )
		{
			try
			{
				IBM.LMV.viewer.loadExtension( 'Autodesk.Viewing.MarkupsCore' );
			}
			catch( e )
			{ 
				return;
				/* Fails if  viewer isn't fullly loaded - retry when needed */ 
			}
			
			var markup = IBM.LMV.viewer.getExtension("Autodesk.Viewing.MarkupsCore");
			if( markup != null )
			{
				IBM.LMV.markup = markup;
			}
		}
		if( this.markup == null || this.markup != markup )
		{
			if( markup != null )
			{
				this.markup = markup;
				this.markup.addEventListener( Autodesk.Viewing.Extensions.Markups.Core.EVENT_EDITMODE_ENTER, 
											  function( evt ) {_self.onEditModeEnter( evt ) } );
				this.markup.addEventListener( Autodesk.Viewing.Extensions.Markups.Core.EVENT_EDITMODE_LEAVE, 
											  function( evt ) {_self.onEditModeLeave( evt ) } );
				this.markup.addEventListener( Autodesk.Viewing.Extensions.Markups.Core.EVENT_MARKUP_SELECTED, 
											  function( evt ) {_self.onMarkupSelect( evt ) } );
			}
		}
	}
	
	this.addModeChangeListerner = function(
		listener
	) {
		this.modeChangeListeners.push( listener );
	}
	
	this.createMarkup = function(
		viewer, wonum
	) {
		this.wonum  = wonum;
		
		this.init( viewer );
		
		// Persumable the viewer is not fully initialized.
		if( this.markup == null ) return;
		
		if( this.toolbar == null )
		{
			this.toolbar = new IBM.LMV.Markup.Toolbar( this );
		}
		try
		{
			this.markup.enterEditMode();
		}
		catch( e )
		{
			return;
		}
		this.toolbar.show( true );
		try
		{
			this.toolbar.setMarkupTool( "CIRCLE" );
		}
		catch( e ) 
		{
			IBM.LMV.displayError( e );
		}
	}
	
	this.showMarkup = function(
		viewer,
		markupData,
		layerName
	) {
		this.init( viewer );

		// Persumable the viewer is not fully initialized.
		if( this.markup == null ) return;
		
		try
		{
			var camera = IBM.LMV.viewer.getCamera();
			this.markup.show();
		}
		catch( e )
		{
			return;
		}

		if( markupData != null && markupData != "" )
		{
			this.markup.loadMarkups( markupData, layerName );
		}
		new IBM.LMV.Markup.CancelMarkupViewButton( IBM.LMV.viewer.container, this );
		
		var evt = { target : this.markup, type : IBM.LMV.Markup.EVENT_SHOW };
		this.fireEditModeEvent( evt );
	}

	this.hideMarkup = function()
	{
		this.markup.hide();
		var evt = { target : this.markup, type : IBM.LMV.Markup.EVENT_HIDE };
		this.fireEditModeEvent( evt );
	}
	
	// Called from ToolBar Button
	this.cancelMarkup = function()
	{
		this.markup.leaveEditMode();
		this.markup.hide();
		this.toolbar.show( false );
		if( this.styleDlg != null )
		{
			this.styleDlg.uninitialize();
			this.styleDlg = null;
		}
	}
	
	this.displayMarkupStyleDlg = function( )
	{
		if( this.styleDlg == null )
		{
			this.styleDlg = new IBM.LMV.Markup.StyleDlg( IBM.LMV.viewer.container, this );
		}
		this.styleDlg.setVisible( true );
		if( this.isText )
		{
			this.styleDlg.setValue( this.currentStyle, true );
		}
		else
		{
			this.styleDlg.setValue( this.currentStyle, false  );
		}
	}
	
	this.onStyleChange = function(
		style
	) {
		this.currentStyle = []
		for( var entry in style ) 
		{
			this.currentStyle[ entry ] = style[ entry ];
		}
		this.setMarkupStyle( style );
	}
	
	this.setMarkupTool = function(
		tool
	) {
		this.isText = false;
		var drawingTool = null;
		switch( tool )
		{
			case "ARROW":
				drawingTool = new Autodesk.Viewing.Extensions.Markups.Core.EditModeArrow( this.markup );
				break;
			case "CLOUD":
				drawingTool = new Autodesk.Viewing.Extensions.Markups.Core.EditModePolycloud( this.markup );
				break;
			case "FREEHAND":
				drawingTool = new Autodesk.Viewing.Extensions.Markups.Core.EditModeFreehand( this.markup );
				break;
			case "LINE":
				drawingTool = new Autodesk.Viewing.Extensions.Markups.Core.EditModePolyline( this.markup );
				break;
			case "CIRCLE":
				drawingTool = new Autodesk.Viewing.Extensions.Markups.Core.EditModeCircle( this.markup );
				break;
			case "RECTANGLE":
				drawingTool = new Autodesk.Viewing.Extensions.Markups.Core.EditModeRectangle( this.markup );
				break;
			case "TEXT":
				drawingTool = new Autodesk.Viewing.Extensions.Markups.Core.EditModeText( this.markup );
				this.isText = true;
				break;
		}
		
		if( drawingTool == null ) return;
		
		this.markup.changeEditMode( drawingTool );
		this.defaultStyle = drawingTool.editor.defaultStyle;
		if( this.currentStyle != null )
		{
			this.setMarkupStyle( this.currentStyle ); 
		}
		else
		{
			this.currentStyle = []
		    for( var entry in this.defaultStyle ) 
			{
				this.currentStyle[ entry ] = this.defaultStyle[ entry ];
			}
			var nsu = Autodesk.Viewing.Extensions.Markups.Core.Utils; 
			this.currentStyle['stroke-width'] = nsu.MARKUP_DEFAULT_STROKE_WIDTH_IN_PIXELS;
			this.currentStyle['font-size']    = nsu.MARKUP_DEFAULT_FONT_WIDTH_IN_PIXELS;
		}
		this.activeTool = drawingTool;
		
		if( this.styleDlg )
		{
			this.styleDlg.setValue( this.currentStyle, this.isText );
		}
	}
	
	this.setMarkupStyle = function(
		style
	) {
		if( this.activeTool == null ) return;
		
		var makrupStyle = [];
		for( var entry in style ) 
		{
			makrupStyle[ entry ] = style[ entry ];
		}
		
		try { 
			// line weight and font size are some internal float value between 0 and 1
			// It can be caculated from default values
			if( this.defaultStyle != null )
			{
				var nsu = Autodesk.Viewing.Extensions.Markups.Core.Utils; 
				
				if( this.defaultStyle[ "stroke-width" ] )
				{
					var defaulWidth       = nsu.MARKUP_DEFAULT_STROKE_WIDTH_IN_PIXELS;
					var newLineWeight     = this.defaultStyle[ "stroke-width" ] / defaulWidth * style[ "stroke-width" ];
					makrupStyle['stroke-width'] = newLineWeight;
				}

				if(  this.defaultStyle[ "font-size" ] )
				{
					var defaultFontSize  = nsu.MARKUP_DEFAULT_FONT_WIDTH_IN_PIXELS;
					var newFontSize      = this.defaultStyle[ "font-size" ] / defaultFontSize * style[ "font-size" ];
					makrupStyle['font-size'] = newFontSize;
				}
				
				if( this.isText && makrupStyle['fill-opacity'] )
				{
					makrupStyle[ "fill-opacity" ] = 0;
				}
			}

			this.markup.setStyle( makrupStyle ); 
		} 
		catch(ex)
		{ 
			console.log(ex); 
		} 
	} 
	
	this.onEditModeEnter = function(
		evt
	) {
		this.fireEditModeEvent( evt );
	}

	this.onEditModeLeave = function(
		evt
	) {
		this.fireEditModeEvent( evt );
	}
	
	this.onMarkupSelect = function(
		evt
	) {
		if( this.styleDlg )
		{
			var nsu = Autodesk.Viewing.Extensions.Markups.Core.Utils; 
			var selectedMarkup = evt.target;
			var defaultStyle   = selectedMarkup.editor.defaultStyle;

			if( selectedMarkup )
			{
				if( selectedMarkup.type == "label" )
				{
					this.isText = true;
				}
				else
				{
					this.isText = false;
				}

				this.toolbar.highlightTool( selectedMarkup.type );
				for( var entry in  selectedMarkup.style ) 
				{
					if( this.isText && entry == 'fill-opacity' ) continue;
					this.currentStyle[ entry ] =  selectedMarkup.style[ entry ];
				}
				
				// Caculate stroke width for a 1 pixel line
				if( selectedMarkup.style['font-size'] )
				{
					var scaleFactor  = defaultStyle[ "font-size" ] / nsu.MARKUP_DEFAULT_FONT_WIDTH_IN_PIXELS;
					this.currentStyle['font-size']  = selectedMarkup.style['font-size'] / scaleFactor;
				}
				
				// Caculate stroke width for a 1 pixel line
				if( selectedMarkup.style['stroke-width'] )
				{
					scaleFactor = defaultStyle[ "stroke-width" ] / nsu.MARKUP_DEFAULT_STROKE_WIDTH_IN_PIXELS;
					this.currentStyle['stroke-width']  = selectedMarkup.style['stroke-width'] / scaleFactor;
				}
				
				this.styleDlg.setValue( this.currentStyle, this.isText );
			}
		}
	}
	
	this.fireEditModeEvent = function(
		evt
	) {
		for( var i = 0; i < this.modeChangeListeners.length; i++ )
		{
			try
			{
				this.modeChangeListeners[i]( evt );
			}
			catch( e )
			{
				console.log( e );
			}
		}
	}
};

/**
  *****************************************************************************************************
  * Select Save View Dialog
  * Gets the viewer state, conversts to JSON, Base 64 encodes and saves to Maximo
  *
  * EditModeArrow
  * EditModeCircle
  * EditModeCloud
  * EditModeFreehand
  * EditModePolyline
  * EditModePolycloud
  * EditModeRectangle
  * EditModeText
  * Autodesk.Viewing.Extensions.Markups.Core
  *****************************************************************************************************
  */
IBM.LMV.Markup.Toolbar = function(
	markupMgr
) {
	this.markupMgr     = markupMgr;
	this.markup        = markupMgr.markup;
	
	this.toolBarDiv    = null;
	
	this.toolActive    = null;
	this.toolArrow     = null;
	this.toolCloud     = null;
	this.toolFreehand  = null;
	this.toolLine      = null;
	this.toolOval      = null;
	this.toolRectangle = null;
	this.toolText      = null;
	
	this.makeToolBar = function()
	{
		var opts = { collapsible: true };
		var markupBar = new Autodesk.Viewing.UI.ToolBar( "Markup_top_toolbar", opts );
		
		this.makeToolSubToolbar( markupBar );
		
		this.makeStyleSubToolbar( markupBar );
		
		this.makeFileSubToolbar( markupBar );
		
		this.makeResizeSubToolbar( markupBar );
		
		var parent = IBM.LMV.viewer.container;
		var viewerWidth = parent.clientWidth;
		var tbDiv = document.createElement("DIV");
		tbDiv.id = "Markup-top_toolbar-div";
		tbDiv.style.position="absolute";
		tbDiv.style.top = 0;
		tbDiv.style.width = "100%";
		tbDiv.style.display = "";
		tbDiv.appendChild( markupBar.container );
		parent.appendChild( tbDiv );

		var tbCont  = markupBar.container;
		var width = tbDiv.clientWidth;
		tbCont.style.width = "" + width + "px";
		tbCont.style.left = "calc( 50% - " + width  / 2 + "px )";
		tbCont.style.top = "25px";
		this.toolBarDiv = tbDiv;

		tbDiv.style.display = "none";
		this.highlightTool( this.toolArrow );		// Default tool
	}
	
	this.makeToolSubToolbar = function(
		toolBar
	) {
		var _self = this;

		var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Markup_toolabr_submenu_tools" );
		
		this.toolArrow = new Autodesk.Viewing.UI.Button("Markup_toolabr_submenu.tool_arrow");
		this.toolArrow.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_arrow.png )";
		this.toolArrow.setToolTip( IBM.LMV.Strings.MARKUP_ARROW );
		this.toolArrow.onClick = function() { _self.setMarkupTool( "ARROW" ); };
		subToolbar.addControl( this.toolArrow );

		this.toolCloud = new Autodesk.Viewing.UI.Button("Markup_toolabr_submenu.tool_cloud");
		this.toolCloud.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_cloud.png )";
		this.toolCloud.setToolTip( IBM.LMV.Strings.MARKUP_CLOUD );
		this.toolCloud.onClick = function() { _self.setMarkupTool( "CLOUD" ); };
		subToolbar.addControl( this.toolCloud );

		this.toolFreehand = new Autodesk.Viewing.UI.Button("Markup_toolabr_submenu.tool_freehand");
		this.toolFreehand.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_free.png )";
		this.toolFreehand.setToolTip( IBM.LMV.Strings.MARKUP_FREEHAND );
		this.toolFreehand.onClick = function() { _self.setMarkupTool( "FREEHAND" ); };
		subToolbar.addControl( this.toolFreehand );

		this.toolLine = new Autodesk.Viewing.UI.Button("Markup_toolabr_submenu.tool_line");
		this.toolLine.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_line.png )";
		this.toolLine.setToolTip( IBM.LMV.Strings.MARKUP_LINE );
		this.toolLine.onClick = function() { _self.setMarkupTool( "LINE" ); };
		subToolbar.addControl( this.toolLine );

		this.toolOval = new Autodesk.Viewing.UI.Button("Markup_toolabr_submenu.tool_oval");
		this.toolOval.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_oval.png )";
		this.toolOval.setToolTip( IBM.LMV.Strings.MARKUP_OVAL );
		this.toolOval.onClick = function() { _self.setMarkupTool( "CIRCLE" ); };
		subToolbar.addControl( this.toolOval );

		this.toolRectangle = new Autodesk.Viewing.UI.Button("Markup_toolabr_submenu.tool_rectangle");
		this.toolRectangle.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_rect.png )";
		this.toolRectangle.setToolTip( IBM.LMV.Strings.MARKUP_RECTANGLE );
		this.toolRectangle.onClick = function() { _self.setMarkupTool( "RECTANGLE" ); };
		subToolbar.addControl( this.toolRectangle );
		
		this.toolText = new Autodesk.Viewing.UI.Button("Markup_toolabr_submenu.toolarrow");
		this.toolText.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_text.png )";
		this.toolText.setToolTip( IBM.LMV.Strings.MARKUP_TEXT );
		this.toolText.onClick = function() { _self.setMarkupTool( "TEXT" ); };
		subToolbar.addControl( this.toolText );

		toolBar.addControl( subToolbar );
		
		return subToolbar;
	}
	
	this.makeStyleSubToolbar = function(
		toolBar
	) {
		var _self = this;

		var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Markup_stylebar_submenu_style" );

		var button = new Autodesk.Viewing.UI.Button("Markup_stylepbar_submenu.style");
		button.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_redline_style.png )";
		button.setToolTip( IBM.LMV.Strings.MARKUP_STYLE );
		button.onClick = function() { _self.markupMgr.displayMarkupStyleDlg(); };
		subToolbar.addControl( button );
		
		toolBar.addControl( subToolbar );

		return subToolbar;
	}

	this.makeFileSubToolbar = function(
		toolBar
	) {
		var _self = this;

		var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Markup_toolabr_submenu_file" );

		var buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Markup_fileabr_submenu.saveMarkup");
		buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_save.png )";
		buttonMaximoOpt.setToolTip( IBM.LMV.Strings.MARKUP_SAVE );
		buttonMaximoOpt.onClick = function() { _self.saveMarkup(); };
		subToolbar.addControl( buttonMaximoOpt );
		
		buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Markup_fileabr_submenu.cancelMarkup");
		buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "360_cancel.png )";
		buttonMaximoOpt.setToolTip( IBM.LMV.Strings.MARKUP_CANCEL );
		buttonMaximoOpt.onClick = function() { _self.markupMgr.cancelMarkup(); };
		subToolbar.addControl( buttonMaximoOpt );

		toolBar.addControl( subToolbar );

		return subToolbar;
	}
	
	this.makeResizeSubToolbar = function(
		toolBar
	) {
		var viewer = this.markupMgr.viewer;
		
		var subToolbar = new Autodesk.Viewing.UI.ControlGroup( "Markup_toolabr_submenu_resize" );
		
        if (viewer.canChangeScreenMode()) 
		{
            var fullscreenButton = new Autodesk.Viewing.UI.Button('markup-fullscreenTool', {collapsible: false});
            fullscreenButton.setToolTip('Full screen');
            fullscreenButton.setIcon("adsk-icon-fullscreen");
            fullscreenButton.onClick = function (e) {
                viewer.nextScreenMode();
            };
			subToolbar.addControl( fullscreenButton );
        }

		toolBar.addControl( subToolbar );

		return subToolbar;
	}
	
	this.highlightTool = function(
		tool
	) {
		if( this.toolActive != null )
		{
			this.toolActive.icon.style.backgroundColor = "";
		}
		
		var newTool = null;

		switch( tool )
		{
			case "ARROW":
			case "arrow":
				newTool = this.toolArrow;
				break;
			case "CLOUD":
			case "polycloud":
				newTool = this.toolCloud;
				break;
			case "FREEHAND":
			case "freehand":
				newTool = this.toolFreehand;
				break;
			case "LINE":
			case "polyline":
				newTool = this.toolLine;
				break;
			case "CIRCLE":
			case "ellipse":
				newTool = this.toolOval;
				break;
			case "RECTANGLE":
			case "rectangle":
				newTool = this.toolRectangle;
				break;
			case "TEXT":
			case "label":
				newTool = this.toolText;
				break;
		}

		this.toolActive = newTool;
		if( this.toolActive != null )
		{
			this.toolActive.icon.style.backgroundColor = "rgba(000, 53, 104, 0.7)";
		}
	}
	
	this.setMarkupTool = function(
		tool
	) {
		this.markupMgr.setMarkupTool( tool );
		this.highlightTool( tool );
	}

	// Called from ToolBar Button
	this.saveMarkup = function() 
	{
		var saveMarkupDlg = new IBM.LMV.Markup.SaveDlg( this.markupMgr, this.markupMgr.wonum );
		saveMarkupDlg.setVisible( true );
	}
	
	this.show = function(
		isShown
	) {
		if( isShown )
		{
			this.toolBarDiv.style.display = "";
		}
		else
		{
			this.toolBarDiv.style.display = "none";
		}
	}
		
	this.makeToolBar();
}


/**
  *****************************************************************************************************
  * Close Markup Display button on the map
  *
  * Close the markup dislpay and return to normal viewer mode
  *****************************************************************************************************
  */
IBM.LMV.Markup.CancelMarkupViewButton = function(
	parent,	
	markupMgr
) {
	this.parent       = parent;
	this.markupMgr    = markupMgr;
	this.cancelButton = null;
	
	this.onDisplayButton = function()
	{
		this.markupMgr.hideMarkup();
		this.parent.removeChild( this.cancelButton );

	};
    
	this.makeDisplay = function()
    {
		var _self = this;
		this.cancelButton = document.createElement("DIV");
		this.cancelButton.className = "maxlmv_CancelMarkup_Tool";
		
        var ctrl          = document.createElement("IMG");
        ctrl.src          = IBM.LMV.PATH_IMAGES+ "360_cancel.png";
        ctrl.alt          = IBM.LMV.Strings.MARKUP_CANCEL;
        ctrl.title        = IBM.LMV.Strings.MARKUP_CANCEL;
		ctrl.style.width  = "24px";
		ctrl.style.height = "24px";
        ctrl.className    = "maxlmv_clickableImage";
        this.cancelButton.onclick   = function() { _self.onDisplayButton( this ); };
        this.cancelButton.appendChild( ctrl );

		this.parent.appendChild( this.cancelButton );
    };
    
    this.makeDisplay();
}

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

/**
  *****************************************************************************************************
  * Create Markup Style Dialog
  *****************************************************************************************************
  */
IBM.LMV.Markup.StyleDlg = function(
	parent,
	markupMgr
) {
	this.parent        = parent;
	this.markupMgr     = markupMgr;
	this.disableUpdate = false;
	this.isText        = false;
	
	this.initialStyle  = null;

	this.scrollContainer;
	this.propertyFrame;

	Autodesk.Viewing.UI.DockingPanel.call( this, this.parent, "Maximo-SaveMarkup-DLG", IBM.LMV.Strings.DLG_TITLE_MARKUP_STYLE );

	// Auto-fit to the content and don't allow resize.  Position at the coordinates given.
	this.container.style.height = "auto";
	this.container.style.width  = "auto";
	this.container.style.resize = "none";
	
	this.onLoad = function()
	{
		var _self = this;
		
		this.propertyFrame.contentWindow.setup( IBM.LMV.PATH_IMAGES + "ColorWheel.jpg",
		                                              IBM.LMV.Strings.MARKUP_RED,
													  IBM.LMV.Strings.MARKUP_GREEN,
													  IBM.LMV.Strings.MARKUP_BLUE,
													  IBM.LMV.Strings.DLG_LABEL_LINE_WEIGHT,
													  IBM.LMV.Strings.DLG_LABLE_FILL, 
													  IBM.LMV.Strings.DLG_LABLE_FONT_SIZE,
													  IBM.LMV.Strings.DLG_LABLE_ITALICS,
													  IBM.LMV.Strings.DLG_LABLE_BOLD);
		this.propertyFrame.contentWindow.addModeChangeListerner( function( style ) { _self.markupMgr.onStyleChange( style ); } ); 
		if( this.initialStyle != null )
		{
			this.propertyFrame.contentWindow.setValue( this.initialStyle, this.isText );
			this.initialStyle = null;
		}
	}
	
	this.makeStyleScreen = function() 
	{
		var _self = this;
		
		this.scrollContainer.innerHTML = "";
		this.scrollContainer.style.backgroundColor = "white";
		
		/*
		 * Color selection frmae - Loads ColorPicker
		 */
		this.propertyFrame = document.createElement("IFRAME");
		this.propertyFrame.src = "ColorPicker.html";
		this.propertyFrame.style.float = "center";
		this.propertyFrame.overflow = "hidden";
		this.propertyFrame.scrolling="no";
		this.propertyFrame.height = 380;
		this.propertyFrame.onload =  function(  ) { _self.onLoad(  ); };
		
		this.scrollContainer.overflow = "hidden";

		this.scrollContainer.appendChild( this.propertyFrame );
		
	}
	
	this.setValue = function(
		style,
		isText
	) {
		this.isText        = isText;
		
		if( !this.propertyFrame.contentWindow.setValue )
		{
			this.initialStyle = [];
			for( var entry in style )
			{
				this.initialStyle[ entry ] = style[ entry ];
			}
			return;
		}
		
		this.disableUpdate = true;
		try
		{
			this.propertyFrame.contentWindow.setValue( style, isText );
		}
		catch( e )
		{
			console.log( e );
		}
		this.disableUpdate = false;
	}
	
	this.show = function()
	{
		this.uninitialize();
	}
	
	this.makeStyleScreen();
};

IBM.LMV.Markup.StyleDlg.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.LMV.Markup.StyleDlg.prototype.constructor = IBM.LMV.Markup.StyleDlg;

IBM.LMV.Markup.StyleDlg.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );
	
	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
	
};

IBM.LMV.Markup.StyleDlg.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		var _self = this;

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
	
	IBM.LMV.Markup.StyleDlg.prototype.uninitialize = function () 
	{
    	Autodesk.Viewing.UI.DockingPanel.prototype.uninitialize.call(this);
		this.markupMgr.styleDlg = null;
		this.markupMgr          = null;
	};
};