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

<script id=<%=id%>_container type="text/javascript" >

/**********************************************************************/
// This section defines the interface with Maximo.  The functions in 
// section along with methods on the ModelManager are call from
// bimvier.jsp wither as a result of the initial .jsp load or scripts
// pushed from the maximo control class.
/**********************************************************************/
	var modelMgr   = null;
	var selMgr     = null;
	var viewer     = null;
	var maximoIntf = null;
	function initModelManager()
	{
		/* Cause the whole frame not just the viewer to auto hide 
		var frames = window.top.document.getElementsByTagName("IFRAME");
		var l = frames.length;
		for(var i = 0; i < l; i ++ )
		{
			var frame = frames[i];
			if( frame.id == "<%=id%>_frame" )
			{
				frame.setAttribute( "autoHide", true );
			}
		}
		*/
		 
		var newViewer = false;
		if( viewer == null )
		{
			try 
			{
				viewer = new ViewerWrapper(  <%=ctrlId%> );
			}
			catch( e )
			{
				setStatus( e );
				return null;
			}
			newViewer = true;
		}

		if( modelMgr == null )
		{
			var ctrl   = document.getElementById( "<%=ctrlId%>" );
			if( ctrl == null ) return null;
			modelMgr = new ModelManager( "<%=ctrlId%>", "<%=modelId%>", viewer );
		}

		if( selMgr == null )
		{
			selMgr = new SelectionManager( <%=ctrlId%>, viewer, selectionChanged );

			// List of icons that are only active when at least one item is selected
			<%if(    bldgMdl.getRecordType() ==  BIMViewer.RECORD_LOCATION )
			{%>
				selMgr.AddSelectionSensitiveItem( "<%=inspectAssetId%>" );
	     	<%}%>
			<%if( appType ==  BIMViewer.TYPE_LOCATION )
			{%>
				selMgr.AddSelectionSensitiveItem( "<%=newSystemId%>" );
	     	<%}%>
            <%if( bldgMdl.isMultiSelectAllowed() && bldgMdl.getAppType() == BIMViewer.TYPE_LOOKUP )
			{%>
				selMgr.setMultiSelect( true );
		    <%}%>
		}
		
		if( maximoIntf == null )
		{
			maximoIntf = new maximoInterface( "<%=bldgMdl.getRenderId()%>" );
		}
		
		if( newViewer )
		{
			viewer.initialize( modelMgr, selMgr );
		}
		
		return  modelMgr;
	}
	
	// Selecte between viewing the model and viewing a message indication
	// that no model is avaiaable
	function setModelVisibility( isVisibile )
	{
		var msg   = document.getElementById( "<%=msgTable%>" );
		var model = document.getElementById( "<%=modelTable%>" );
		if( msg == null || model == null )
		{
			return;
		}
		if( isVisibile )
		{
			msg.style.visibility   = "hidden";
			model.style.visibility = "visible";
		}
		else
		{
			msg.style.visibility   = "visible";
			model.style.visibility = "hidden";
		}
	}

	// Call when the control is first displyed or when current item in Maximo
	// changes to update the selected item in the model to match Maximo  
	function select( value )
	{
		var ctrl = document.getElementById( "<%=ctrlId%>" );
		if( ctrl == null || ctrl == "undefined" )
		{
			return;		
		}
		if( selMgr.selection == value )
		{
			return;
		}
		viewer.selectValue( value, modelMgr.isAutoZoom );
	}
	
	// Called from Maximo to set a new multi selection
	function multiSelect( 
		selectionList,		// An array of items to select
		selection,			// The element in the array that matches the current Maximo item
		zoomToContext
	) {
		var ctrl = document.getElementById( "<%=ctrlId%>" );
		if( ctrl == undefined || ctrl == null ) return;


		// Handle an empty selection list
		if( selectionList == null || selectionList.length == 0 )
		{
			viewer.clearSelection( ctrl );
			selMgr.updateSelectionSet( ctrl );
			return;
		}
		
		var count = viewer.selectValueList( selectionList, zoomToContext );

		selMgr.updateSelectionSet( ctrl );
		if( count > 0 )
		{
			<%=modeId%>.value = 1;			// Set navigation mode
			selectMode();
		}
    }

	// Called from Maximo to update the text in the status bar
	function setStatus( status )
	{
		var statusCtrl = document.getElementById( "<%=statusId%>" );
		if( statusCtrl != null )
		{
			statusCtrl.value = status;
		}
	}
	
/**********************************************************************/
// Messages back to the Maximo server
//
// Note: some actions are sent driectly from HTML button definitions
/**********************************************************************/
	//Sends and event to the server to display the "addmodel" dialog
	function maxAssetInspect()
	{
		if( modelMgr == null ) return;
		var model = modelMgr.getCurrentModel();
		if( model == null ) return;

		if( selMgr.selection == "" )
		{
			setStatus( "<%=strings.msgNoInspect%>" );
			return;
		}

		window.parent.sendEvent(  "BIM_IAD", "<%=bldgMdl.getRenderId()%>", model.location );
	}
	
	// When multi-select is enabled (for lookup mode) sends a message to
	// Maximo each time the selection set is changed
	var curMultiSelect = null;

	function maxMultiSelectDelay()
	{
		if( parent.working )
		{
			setTimeout( 'maxMultiSelectDelay();', 200 );
			return;
		}
		if( curMultiSelect != null )
		{
			<%
				String altRenderId = "NO MATCH";
				int idx = id.indexOf( '_' );
				if( idx > 0 )
				{
					altRenderId = id.replaceFirst( "_", "-" );;
				}
				else
				{
					altRenderId = id;
				}
			%>
			window.parent.sendEvent(  "eventSelect", "<%=bldgMdl.getRenderId()%>", curMultiSelect );
			curMultiSelect = null;
		}
	}

	
/**********************************************************************/
// Used by iFrames to get values from the .jsp
/**********************************************************************/
	function getValue( key )
	{
		switch( key )
		{
			case "image_path":			return "<%=BIM_IMAGE_PATH%>";
			case "toolbar_img":         return "<%=TOOLBAR_IMG%>";
			case "foreground": 			return "<%=foreground%>";
			// Rezise popup
			case "resize_title":		return "<%=strings.resizeTitle%>";
			case "resize_default":		return "<%=strings.defaultSize%>";
		}
		return "";
	}

	function getControl()
	{
		if( <%=ctrlId%> == undefined ) return null;
		return <%=ctrlId%>;
	}

	
/**********************************************************************/
// Event function for the selection manager
/**********************************************************************/
	function selectionChanged( 
		ctrl,
		selectionList,
		selection,
		count,
		index 
	) {
		setStatus( "" );
		maximoIntf.maxMultiSelect( selectionList, selection );
	}

/**********************************************************************/
// Resize button and called from Maximo
/**********************************************************************/
	var _height     = <%=height%>;
	var _width      = "<%=width%>";
	var _resolution = 0;
	function resizeBtn( evt )
	{
		if( !evt ) evt = event;
		resizePopup( evt.clientX, evt.clientY );
	}

	function resize(
		id, name
	) {
		if( id == _resolution ) return;
		_resolution = id;

		switch( id )
		{
		case "-1":			// Off Screen Storage
			_width  = 400;
			_height = 300;
			break;
		case "0":			// Default
			_width  = "<%=width%>";
			_height = <%=height%>;
			break;
		case "1":			// 720x1200
			_width  = 1130;
			_height = 430;
			break;
		case "2":			// 1024x768
			_width  = 954;
			_height = 468;
			break;
		case "3":			// 1024x1280
			_width  = 1210;
			_height = 724;
			break;
		case "4":			// 1080x1920
			_width  = 1850;
			_height = 780;
			break;
		case "5":			// 1200x1600
			_width  = 1530;
			_height = 900;
			break;
		case "6":			// 1200x1920
			_width  = 1850;
			_height = 900;
			break;
		case "7":			// 800x1200
			_width  = 1130;
			_height = 500;
			break;
		}
		
		var tmp = Number( _width );
		if( "" + tmp != "NaN"  )
		{
			_width = _width - <%=leftOffset%>;
			if( _width < 954 ) _width = 954;
		}
		setSize( );

		if( id < 0 || id > 7 ) return;

		if( maximoIntf != null )
		{
			maximoIntf.maxSetResizeOption( id );
		}
	}

	function setSize()
	{
		var cssWidth = _width;
		var tmp = Number( _width );
		if( "" + tmp != "NaN"  )
		{
			cssWidth = "" + (tmp - 8) + "px"
		}
		
		var height = _height;
		<% if( viewerVendor == VENDOR_A360 )
		{ %>
			height = height + <%=V_INSET%> + <%=toolbar_height%>;
		<%}%>



		var frames = window.top.document.getElementsByTagName("IFRAME");
		var l = frames.length;
		for(var i = 0; i < l; i ++ )
		{
			var f = frames[i];
			if( f.id == "<%=id%>_frame" )
			{
				f.style.height = "" + _height + "px";;
				f.style.width  = cssWidth;
				break;
			}
		}

		var tbl = document.getElementById( "<%=modelTable%>" );		// Main table
		if( tbl != null )
		{
			tbl.style.height  = "" + _height + "px";
		}

		var ctrl = document.getElementById( "<%=ctrlId%>" );			// NavisWorks control
		if( ctrl != null )
		{
			var ctrlHeight = _height - <%=V_INSET%>;
			<% if( viewerVendor == VENDOR_A360 )
			{ %>
				ctrlHeight = ctrlHeight + 2 * <%=toolbar_height%>;
			<%}%>
			ctrl.style.height = "" + ctrlHeight + "px";

			if( viewer != null && viewer.reziseViewer != null )
			{
				viewer.reziseViewer( height, ctrl.clientWidth );
			}
		}

		ctrl = document.getElementById( "<%=noCtrlId%>" );			// Control failed to load
		if( ctrl != null )
		{
			ctrl.style.height = "" + (_height - <%=V_INSET%>) + "px";
		}
	}

	function resizePopup(
		mouseX,
		mouseY
	) {

		var ss = document.getElementById( "<%=id%>_selectSize" );
		if( ss != undefined )
		{
			var xOffset = 0;
			var yOffset = 0;
			if( document.body.scrollLeft ) xOffset = document.body.scrollLeft;
			if( document.body.scrollTop )  yOffset = document.body.scrollTop;
			ss.style.left = mouseX - ss.width + xOffset  + 2;
			ss.style.top  = mouseY + yOffset  - 2;
			ss.style.visibility = "visible";
			var frame = window.frames.<%=id%>_selectSize; 
			if( frame != undefined )     
			{
				if( frame.setResolution != null )	// IE
				{
					frame.setResolution( _resolution );
				}
				else
				{
					frame.contentWindow.setResolution( _resolution );
				}
			} 
			return;  
		}
	}

	function resizeDlgBtn()
	{
		var sizeOpt;
		if( _height == <%=height%> )
		{
			_height = (<%=height%> * 2)/3;
			sizeOpt = 1;
		}
		else if( _height == (<%=height%> * 2)/3 )
		{
			_height = <%=height%>/3;
			sizeOpt = 2;
		}
		else
		{
			_height = <%=height%>;
			sizeOpt = 0;
		}
		setSize();
		maximoIntf.maxSetResizeDlgOption( sizeOpt );
	}

	function resizeDlg(
			opt
	) {
		switch( opt )
		{
			case "0":
				_height = <%=height%>;
				break;
			case "1":
				_height = (<%=height%> * 2)/3;
				break;
			case "2":
				_height = <%=height%>/3;
				break;
		}
		setSize();
	}

	/**********************************************************************/
	// Auto Zoom button script
	/**********************************************************************/
	function toggleAutoZoomMode()
	{
		//TODO need to set css for selected/unselected

		var btn1 = document.getElementById( "<%=autoZoomMode1Id%>" );
		var image;
		var image = "";
		if( modelMgr.isAutoZoom )
		{
			modelMgr.setAutoZoom( false );
		}
		else
		{
			modelMgr.setAutoZoom( true );
			image = "url('<%=BIM_IMAGE_PATH%>/tb_toggle_bg.png')";
		}
		if( btn1 != null )
		{
			btn1.style.backgroundImage = image;
		}
	}
	
</script>
