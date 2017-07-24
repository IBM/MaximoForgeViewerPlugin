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
<%@ page import="psdi.server.MXServer"%>
<%@ page import="psdi.util.*,psdi.webclient.system.runtime.*,psdi.webclient.system.session.*,psdi.webclient.system.websession.*,psdi.webclient.system.dojo.*,psdi.server.*,java.util.*,java.rmi.RemoteException" %>
  
<%@ include file="header.jsp" %>
  
<%
	MXServer server    = MXServer.getMXServer();
	String adHost      = server.getProperty( "bim.viewer.LMV.host" );
	String lmvversion  = server.getProperty( "bim.viewer.LMV.viewer.version" );
	if( lmvversion == null ) lmvversion = "";
	// ?v=v1.2.15
  
	String style     = "https://" + adHost + "/viewingservice/v1/viewers/style.css" + lmvversion;
	String viewer3D  = "https://" + adHost + "/viewingservice/v1/viewers/viewer3D.js" + lmvversion;
	String lmvworker = "https://" + adHost + "/viewingservice/v1/viewers/lmvworker.js" + lmvversion;
%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>  
 
	<title>Maximo Building Viewer</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <link rel="stylesheet" type="text/css" href="<%=style%>" />
    <link rel ="stylesheet" type="text/css" href="viewer.css" title="Style"/>
    <link rel ="stylesheet" type="text/css" href="<%=CSS_PATH%>/LMV.css" title="Style"/>

    <script type = "text/javascript" 
        src="<%=viewer3D%>">
    </script>
    <script  type = "text/javascript" 
        src="<%=lmvworker%>">
    </script>
    <script type = "text/javascript" 
		src="../javascript/Forge.js" >
	</script>
    <script type = "text/javascript" 
		src="../javascript/LMV.js" >
	</script>
    <script type = "text/javascript" 
		src="../javascript/LMV_Markup.js" >
	</script>
    <script type = "text/javascript" 
		src="bimeverywhere.js" >
	</script>
    <script type = "text/javascript" 
        src="https://maps.googleapis.com/maps/api/js?v=3&sensor=false" >
    </script>

	<script type="text/javascript" >
		"use strict";
		
		IBM.viewerNS = IBM.createNS( "IBM.Maximo.BIMField" );
		IBM.Maximo.BIMField.controler        = null;

		<%@ include file="../components/bimlmv/strings.jsp" %>
		
		// Load strings fro the Maximo message table (See Strings.jsp)
		IBM.ewNS.Strings.KEY_OF                  = "<%=stringTable.keyOf%>";
		IBM.ewNS.Strings.KEY_DASH                = "<%=stringTable.keyDash%>";
		IBM.ewNS.Strings.KEY_OPEN_FILTER         = "<%=stringTable.keyOpenFilter%>";
		IBM.ewNS.Strings.KEY_CLOSE_FILTER        = "<%=stringTable.keyCloseFilter%>";
		IBM.ewNS.Strings.KEY_FILTER_TABLE        = "<%=stringTable.keyFilterTable%>";
		IBM.ewNS.Strings.KEY_PREV_PAGE           = "<%=stringTable.keyPrevPage%>";
		IBM.ewNS.Strings.KEY_NEXT_PAGE           = "<%=stringTable.keyNextPage%>";
		IBM.ewNS.Strings.MSG_NOT_ASSET           = "<%=stringTable.msgNotAsset%>";
		IBM.ewNS.Strings.DLG_BTN_VIEW            = "<%=stringTable.dlgBtnView%>";
		IBM.ewNS.Strings.DLG_BTN_LOCATE          = "<%=stringTable.dlgBtnLocate%>";       
		IBM.ewNS.Strings.MAP_BTN_MODELLIST       = "<%=stringTable.mapBtnModelList%>";     
		
		IBM.ewNS.Strings.KEY_ERROR               = "<%=stringTable.keyError%>";  

		IBM.ewNS.Strings.KEY_TT_ASSET            = "Asset Detail";
		IBM.ewNS.Strings.KEY_TT_LOCATION         = "Location Detail";
		IBM.ewNS.Strings.KEY_TT_WORK_HIST        = "Show Work History";
		IBM.ewNS.Strings.KEY_TT_CREATE_WO        = "Create Work Orders";
		
		IBM.LMV.PATH_IMAGES                      = "<%=IMAGE_PATH%>bim/";
     
		function initialize()
		{
			IBM.Maximo.BIMField.controler      = new IBM.Maximo.BIMField.bimField();
			IBM.Maximo.BIMField.controler.initialize();

		 /* 
		  *****************************************************************************************************
		  * Un comment to caputre the console in a debug windows on the screen
		  *****************************************************************************************************
		  */
		  /*
			var mainPage      = document.getElementById( "mainPage" );
			var logWin        = document.createElement("DIV");
			logWin.className = "maxlmv_map";
			logWin.style.width = "500px";
			logWin.style.height = "300px";
			logWin.style.top = "0";
			logWin.style.left = "400px";
			logWin.style.zIndex = 3000;
			logWin.style.fontSize = "12pt";

			mainPage.appendChild( logWin );

			(function(){
				var oldLog = console.log;
				console.log = function (message) {
					// DO MESSAGE HERE.
					logWin.innerHTML = logWin.innerHTML + "<p>" + message + "<p/>";
					oldLog.apply(console, arguments);
				};
				console.info = function (message) {
					// DO MESSAGE HERE.
					oldLog.apply(console, arguments);
				};
				console.warn = function (message) {
					// DO MESSAGE HERE.
					oldLog.apply(console, arguments);
				};
			})();
			*/
		};

		/**
		  *****************************************************************************************************
		  * Controler class for the viewer.
		  *
		  * This class is a singleton.  It instance is created by the body.onload event.
		  * It Creates references to:
		  * - LoginManager
		  * - MapManager
		  * - ModelList
		  * Adds extensions to the BIM360 viewer toolbar and manages launch of Maximo specifice options from
		  * toolbar selection
		  * 
		  * Initiall the LoginManager is created and the Login screen is displayed.
		  * On successful Login:
		  * - THe Maximo UserInfo record is queried for the logged in user
		  * - The Maximo MapManager configureation is queried.
		  * Waen both have returned:
		  * - A singleton instance of the ModelList class is created which downloads the models definitions for
		  * the default insert site for the user
		  * - If Any type of MapManager is enabled in Maximo, and instance of the MapManager is created.  It 
		  * places pins for any model in the model list that has GPS coordinates.
		  *****************************************************************************************************
		  */
		IBM.Maximo.BIMField.bimField = function()
		{
			var _forgeViewer        = null;

			this.mainPage           = null;			// Element reference to main window
			this.imagePath          = "<%=IMAGE_PATH%>";
			this.currentSelection   = null;			// Used By the toolbar launch of the Work Order dialog
			this.mapInfoDwonloaded = false;			// Synchonize load of UserInfo and MapManager
			this.mapInfo            = null;			// List of MapManager records formMaximo	
			this.userInfo           = null;			// Maximo user record for logged in user
			this.mapsActive         = false;		// True if Maps are configured in Maximo - Causes the Map to be displayed
			
			this.loginMgr           = null;
			this.mapMgr             = null;
			this.modelList	        = null;
			this.propertySheet      = null;			// Singleton property sheet for model locations
					
			this.initialize = function()
			{
				var _self             = this;
				this.mainPage         = document.getElementById( "mainPage" );

				// Build base URL for rest calls
				var l = location;
				var path = location.pathname;
				var idx = path.lastIndexOf( "/webclient" );
				path = path.substring( 0, idx );
				var url = location.protocol + "//" + location.hostname;
				if( location.port != null && location.port.length > 0 )
				{
					url = url + ":" + location.port;
				}
				if( path != null && path.length > 0 )
				{
					url = url + path;
				}
				IBM.LMV.Auth.contextRoot = "/rest";
				IBM.LMV.AUTH_REST_CONTEXT = url + IBM.LMV.Auth.contextRoot + "/ss/BIMLMV/getAuthToken";

				IBM.LMV.ctrlContainer = this.mainPage;
				IBM.LMV.extensions    = new IBM.Maximo.BIMField.ExtensionManager( this );
				IBM.LMV.displayError  = function( msg ) { _self.displayError( msg ) };
				IBM.LMV.RESTError     = function( status, source, responseText ) { _self.RESTError( status, source, responseText); };

				IBM.LMV.ToolBar.onToolbarCreate = function() { _self.onToolbarCreate(); };
				
				IBM.LMV.addModelLoadistener(  function() { _self.onModelLoad(); } );

				_forgeViewer   = new IBM.LMV.ForgeViewer;
				_forgeViewer.addSelectionListener( function( selection ) { _self.onSelect( selection ); } );

				this.loginMgr  = new LoginManager( this );
			};
			
			// BIM260 Viewer toolbar event to display the Work Order dialog
			this.displayCreateWODlg = function()
			{
				if( this.currentSelection  == null || this.currentSelection.length == 0 )
				{
					this.displayMessageBox( IBM.ewNS.Strings.KEY_WO_NO_SELECTION, IBM.ewNS.Strings.KEY_CREATE_WO );
					return;
				}
				var woDlg = new IBM.ewNS.WorkOrderDlg( IBM.LMV.viewer.container, 
													   this.currentSelection[0].guid );
				woDlg.setVisible( true );
			}

			// USed to override tbe LMV default error handinling
			this.displayError = function( msg )
			{
				if( msg instanceof Object )
				{
					var temp = "";
					for( prop in msg ) 
					{
						temp = temp + prop + ": " + msg[ prop ]; + "  ";
					}
					msg = temp;
				}

				this.displayMessageBox( msg, IBM.ewNS.Strings.KEY_ERROR );
				console.log( msg );
			};

			this.RESTError = function(
				status, 			// HTTP Status
				source, 			// Text identifying operation that generated the error
				responseText		// HTTP Error text
			) {
				if( status == 401 )
				{
					try
					{
						this.controler.displayMessageBox( IBM.ewNS.Strings.KEY_LOGIN_FAILURE, 
														 IBM.ewNS.Strings.KEY_TITLE_LOGIN_MSG );
					}
					catch( e )
					{ /* Ignore */ }
					location.reload( true );
//					window.open( window.location.href );
//					this.hide();
//					this.loginMgr.displayLoginForm();
					return;
				}
				this.displayMessageBox( source + status + "\n" + responseText, source );
			};
			
			this.displayMessageBox = function( msg, title )
			{
				if( msg != null && msg != "" )
				{
					var messageBox;
					if(  IBM.LMV.viewer )
					{
						messageBox = new IBM.LMV.MessageBox( IBM.LMV.viewer.container, msg, title );
					}
					else
					{
						messageBox = new IBM.LMV.MessageBox( this.mainPage, msg, title );
					}
					messageBox.setVisible( true );
				}
			};
   
			// BIM260 Viewer toolbar event to display the location Details pannel
			this.displayProperties = function(
				mbo
			) {
				if( IBM.LMV.propertyPannel == null )
				{
					IBM.LMV.propertyPannel = new IBM.LMV.MaximoPropertyPanel( IBM.LMV.viewer.container, _forgeViewer );
				}
				else
				{
					if( IBM.LMV.propertyPannel.isVisible() )
					{
						if( IBM.LMV.propertyPannel.mboName == mbo )
						{
							IBM.LMV.propertyPannel.setVisible( false );
							return;
						}
					}
				}
				IBM.LMV.propertyPannel.baseMbo = mbo;
				IBM.LMV.propertyPannel.reset( mbo );
				IBM.LMV.propertyPannel.setVisible( true );
				IBM.LMV.propertyPannel.requestProperties();
			}
			
			this.displayWorkHistDlg = function()
			{
				if( this.currentSelection  == null || this.currentSelection.length == 0 )
				{
					this.displayMessageBox( IBM.ewNS.Strings.KEY_WO_NO_SELECTION, IBM.ewNS.Strings.KEY_WORK_HISTORY );
					return;
				}
				var woDlg = new IBM.ewNS.WorkHistDlg( _forgeViewer, IBM.LMV.viewer.container, 
													  this.currentSelection[0].guid );
				woDlg.setVisible( true );
			}
			
			this.hide = function()
			{
				if( this.mapMgr ) 
				{
					this.mapMgr.hide();
				}
				if( this.modelList ) 
				{
					this.modelList.hide();
				}
				if( this.propertySheet ) 
				{
					this.propertySheet.setVisible( false );
				}
			}

			// Maximo REST call to retrieve the UserInfo record for the current user
			// Used for Default insert site
			this.lookupUserInfo = function(
				userName
			) {
				var _self = this;
				
				var url = IBM.LMV.Auth.getRestURL();
				url = url + "/mbo/MAXUSER" ;
				url = url + "?USERID=~eq~" + userName.toUpperCase() + "&_compact=1";
				var xmlReq = new XMLHttpRequest();
				xmlReq.onreadystatechange = function() { _self.onUerInfo( this ); };
				xmlReq.open( "GET", url, true );
				IBM.LMV.Auth.setRequestHeaders( xmlReq );
				IBM.LMV.Auth.addAuthHeaders( xmlReq );
		
				xmlReq.send();
			};
  
			this.lookupMapInfo = function(
				userName
			) {
				var _self = this;
				
				var url = IBM.LMV.Auth.getRestURL();
				url = url + "/mbo/MAPMANAGER" ;
				url = url + "?_compact=1";
				var xmlReq = new XMLHttpRequest();
				xmlReq.onreadystatechange = function() { _self.onMapInfo( this ); };
				xmlReq.open( "GET", url, true );
				IBM.LMV.Auth.setRequestHeaders( xmlReq );
				IBM.LMV.Auth.addAuthHeaders( xmlReq );
		
				xmlReq.send();
			};
			
			// Maximo maximo logo - Displayed on Bottom left of map to avoid other decorations and top
			// left of viewer
			this.makeLogo = function(
				parent, viewer
			) {
				var logo        = document.createElement("DIV");
				logo.className  = "maxlmv_Logo";
				if( viewer )		// If displayed on the viewer
				{
					logo.style.top  = 0;
					logo.style.left = 0;
				}
				
				var ctrl        = document.createElement("IMG");
				ctrl.src        = this.imagePath + "bim/IBM_maximo_logo.PNG";
				ctrl.className  = "maxlmv_Logo";
				ctrl.width      = 160;
				ctrl.height     = 40;
				logo.appendChild( ctrl );
		
				parent.appendChild( logo );
			}

			this.onModelLoad = function()
			{
				this.makeLogo( IBM.LMV.viewer.container, IBM.LMV.viewer );
				new IBM.Maximo.BIMField.HomeScreen( this, IBM.LMV.viewer.container );
			}

			// Callback wen MapManager record is loaded
			this.onMapInfo = function( request )
			{
				if( request.readyState != 4 )  
				{ 
					return; 
				}

				this.mapInfoDwonloaded = true;
				if( request.status == 404 )
				{
					this.setupStartScreen();
					return;		// No MapManager records
				}
				if( request.status != 200 )
				{
					this.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
					this.setupStartScreen();
					return;
				}

				var mboSet = JSON.parse( request.responseText );
				var mapSet = mboSet.MAPMANAGERMboSet.MAPMANAGER;
				if( mapSet && mapSet.length > 0 )
				{
					this.mapInfo = mapSet;
				}
				if( this.mapInfo != null )
				{
					for( var i = 0; i < this.mapInfo.length; i++ )
					{
						var mapInfo = this.mapInfo[i];
						if( mapInfo[ "ACTIVE" ] && mapInfo.ACTIVE )
						{
							// For now if any Map Manage is active show the map in the future we may want to check
							// That its Google and look at the active sites
							this.mapsActive = true;
						}
					}
				}
					
				this.setupStartScreen();
			};

			// Called from the Login Manager on sunncessful authenitcation.
			this.onLogin = function(
				userName
			) {
				this.lookupMapInfo();
				// userName can be null of an existing session token us used for login.
				// Mau need to get the user later
				if( userName )						
				{
					this.lookupUserInfo( userName );
				}
				else
				{
					this.userInfo = {};
				}
				IBM.LMV.dataDictionary.getLabels( "workorder" );
			};

			// Callback when UserINfo is loaded
			this.onUerInfo = function( request )
			{
				if( request.readyState != 4 )  
				{ 
					return; 
				}
		
				if( request.status != 200 )
				{
					this.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
					return;
				}

				var mboSet = JSON.parse( request.responseText );
				this.userInfo = mboSet.MAXUSERMboSet.MAXUSER[0];
				this.setupStartScreen();
			};

			// Called from the viewer each time the selection set changes
			this.onSelect = function(
				selection
			) {
				this.currentSelection = selection;
			};
	
			// Called each time a viewer instance is created which happens for each model file load
			this.onToolbarCreate = function()
			{
				var _self = this;
				
				var mainToolbar = IBM.LMV.viewer.getToolbar(true);     // get the main toolbar from the viewer
				console.assert(mainToolbar != null);
				
				var maximoSubToolbar = mainToolbar.getControl( IBM.LMV.ToolBar.ID_TOOLBAR_GROUP );
				var selectSubMenu    = maximoSubToolbar.getControl( IBM.LMV.ToolBar.ID_TOOLBAR_SELECT );
				var maximoSubMenu    = maximoSubToolbar.getControl( IBM.LMV.ToolBar.ID_TOOLBAR_MAXIMO );
	
				// Asset property sheet
				var buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolbar_submenu.asset");
				buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_findAsset.png" + ")";
				buttonMaximoOpt.setToolTip( IBM.ewNS.Strings.KEY_TT_ASSET );
				buttonMaximoOpt.onClick = function() { _self.displayProperties( "ASSET" ); };
				maximoSubMenu.addControl( buttonMaximoOpt );
	
				// Location Propery Sheet
				var buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolbar_submenu.location");
				buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_locationProperties.png" + ")";
				buttonMaximoOpt.setToolTip( IBM.ewNS.Strings.KEY_TT_LOCATION );
				buttonMaximoOpt.onClick = function() { _self.displayProperties( "LOCATIONS" ); };
				maximoSubMenu.addControl( buttonMaximoOpt );
	
				// Display Work History dialog
				buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolbar_submenu.showWOs");
				buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_viewWOsandPMs.png" + ")";
				buttonMaximoOpt.setToolTip( IBM.ewNS.Strings.KEY_TT_WORK_HIST );
				buttonMaximoOpt.onClick = function() { _self.displayWorkHistDlg(); };
				maximoSubMenu.addControl( buttonMaximoOpt );
	
				// Display create WO dialog
				buttonMaximoOpt = new Autodesk.Viewing.UI.Button("Maximo_toolbar_submenu.showWorkHistory");
				buttonMaximoOpt.icon.style.backgroundImage = "url(" + IBM.LMV.PATH_IMAGES + "tb_createWO.png" + ")";
				buttonMaximoOpt.setToolTip( IBM.ewNS.Strings.KEY_TT_CREATE_WO );
				buttonMaximoOpt.onClick = function() { _self.displayCreateWODlg(); };
				maximoSubMenu.addControl( buttonMaximoOpt );
			};
			
			this.reset = function()
			{
				this.currentSelection  = null;
				IBM.LMV.propertyPannel = null;
			};
			
			// Wait for both the userInfo and the MapManager query to return, then create the suppored objects
			this.setupStartScreen = function()
			{
				if( this.userInfo != null && this.mapInfoDwonloaded )
				{
					if( this.mapsActive )
					{
						this.mapMgr = new IBM.Maximo.BIMField.mapControl( this );
					}

					this.modelList = new IBM.Maximo.BIMField.ModelList( this, _forgeViewer, this.imagePath );
					if( this.mapsActive )
					{
						var mapMgr = this.mapMgr;
						this.modelList.onmodel = function( modelList ) { mapMgr.onModelList( modelList ) };
						var maxMap = this.mapInfo[0];
						var zoom = 12;
						if( maxMap.MAPSITES )
						{
							// Look for site level configureation for default insert site
							// Rightnow, just take the first map manager found.  Might searcg for Google in the future
							for( var i = 0; i < maxMap.MAPSITES.length && this.userInfo.DEFSITE; i++ )
							{
								if( maxMap.MAPSITES[i].SITEID == this.userInfo.DEFSITE )
								{
									if( maxMap.MAPSITES[i].ZOOMLEVEL )
									{
										this.mapMgr.mapOptions.zoom = maxMap.MAPSITES[i].ZOOMLEVEL
									}
									if( maxMap.MAPSITES[i].INITIALX && maxMap.MAPSITES[i].INITIALY )
									{
										this.mapMgr.mapOptions.center = { lat: maxMap.MAPSITES[i].INITIALY, 
										                                  lng: maxMap.MAPSITES[i].INITIALX };
									}
									break;
								}
							}
						}
					}

					if( !this.mapsActive )
					{
						new IBM.Maximo.BIMField.DisplayModeListButton( this.mainPage, this.modelList );
					}

					var filter = {};
					if( this.userInfo.DEFSITE )
					{
						filter.SITEID = this.userInfo.DEFSITE;
					}
					this.modelList.display( filter );
					if( this.mapsActive )
					{
						this.mapMgr.initialize();
					}
				}
			}
			
			this.showStartScreen = function()
			{
				if( this.mapMgr )
				{
					this.mapMgr.show();
				}
				this.modelList.show();
			}
		};
		
		/**
		  *****************************************************************************************************
		  * ContextMenuExtension
		  * Context menu extension - Add option to display the model list
		  *****************************************************************************************************
		  */
		IBM.LMV.ExtensionManager = function()
		{
			this.lookupString = function(
				key
			) {
				return key;
			};
			
			this.buildContextMenu = function(
				event,
				menu, 
				selectedId					// null if not on an item
			) {
			};
		};

		IBM.Maximo.BIMField.ExtensionManager = function ( controler, viewer, options) 
		{
			IBM.LMV.ExtensionManager.call( this );
		
			var _self = this;
			this.controler = controler;
		
			IBM.Maximo.BIMField.ExtensionManager = function() 
			{
				IBM.LMV.ExtensionManager.call( this );
			};
		
			IBM.Maximo.BIMField.ExtensionManager.prototype =	Object.create( IBM.LMV.ExtensionManager.prototype );
		
			IBM.Maximo.BIMField.ExtensionManager.prototype.constructor = IBM.LMV.ExtensionManager;
		
			this.buildContextMenu = function(
				event,
				menu, 
				selectedId					// null if not on an item
			) {
				if( selectedId != null && selectedId.length > 0 )
				{
				
				}
				menu.push({
						title: "Display Model List",
						target: function() { controler.showStartScreen(); }
					});
			}
		}		// ExtensionManager
				
		var MAX_REST = {
			USERNAME          : "j_username",
			PASSWORD          : "j_password",

			userId            : null,
			passWord          : null,
			appServerAuth     : <%=formAuth%>,
		
			// Used to override viewer auth
			setAuth : function( xmlReq )
			{
				var authString = MAX_REST.userId + ":" + MAX_REST.passWord;
				var encoded = btoa( authString );
			},
		};

/**
  *****************************************************************************************************
  * Login Manager
  * Gathers user credentials, logs into Maximo then setsup runtime class instances
  *  
  * Login Sequence
  * - Try digest first because if it is digest andy other call results in a browser login prompt
  * - Try basic and MaxAuth together.  It is not necessary to know which it is
  *****************************************************************************************************
  */
function LoginManager( controler )
{
	this.formAuth      = <%=WebAppEnv.useAppServerSecurity() || formAuth%>;
	this.controler     = controler;
	this.appServerAuth = false;
	this.loggedIn      = false;
	this.username      = null;
	this.password      = null;

	this.displayLoginForm = function()
	{
		var _self = this;
		var loginDiv = document.getElementById( "login" );
		loginDiv.style.visibility = "visible";
		var signinBtn = document.getElementById( "loginbutton" );
		signinBtn.onclick = function() { _self.formLogin( this ); }
		var userInput = document.getElementById( "j_username" );		// Form Auth
		if( userInput )
		{
			userInput.onkeypress = function( evt ) { _self.onLoginKeyPress( this, evt ); }
		}
		else
		{
			var userInput = document.getElementById( "username" );
			if( userInput )
			{
				userInput.onkeypress = function( evt ) { _self.onLoginKeyPress( this, evt ); }
			}
		}
		var pwdInput = document.getElementById( "j_password" );		// Form Auth
		if( pwdInput )
		{
			pwdInput.onkeypress = function( evt ) { _self.onLoginKeyPress( this, evt ); }
		}
		else
		{
			var pwdInput = document.getElementById( "password" );
			if( pwdInput )
			{
				pwdInput.onkeypress = function( evt ) { _self.onLoginKeyPress( this, evt ); }
			}
		}

		var height  = window.innerHeight;
		var width   = window.innerWidth;
		if( loginDiv.clientWidth > width )
		{
			loginDiv.style.width = "" + loginDiv.clientWidth + "px";
			loginDiv.style.left = 0;
		}
		else
		{
			var left = (width  - loginDiv.clientWidth ) / 2;
			loginDiv.style.left = "" + left + "px";
		}
		
		if( loginDiv.clientHeight > height )
		{
			loginDiv.style.height = "" + loginDiv.clientHeight + "px";
			loginDiv.style.top = 0;
		}
		else
		{
			var top  = (height - loginDiv.clientHeight ) /2;
			loginDiv.style.top = "" + top + "px";
		}
	}
	
	// Try a form login by calling j_secuity_check
	this.formLogin = function( form )
	{
		this.username = document.getElementById( "<%=userFieldName%>" ).value;
		this.password = document.getElementById( "<%=passwordFieldName%>" ).value;
		
		if( this.username == null || this.username.length == 0 )
		{
			this.controler.displayMessageBox( "Username", IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD  );
			return;
		}

		if( this.password == null || this.password.length == 0 )
		{
			this.controler.displayMessageBox( "Password", IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD  );
			return;
		}

		MAX_REST.userId   = this.username;
		MAX_REST.passWord = this.password;
		
		this.loginFormAuth();
	}
	
	this.hasCrdentials = function()
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
				if( term[0] == "j_username" )
				{
					if( term.length >= 2 )
					{
						this.username = term[1];
					}
				}
				if( term[0] == "j_password" )
				{
					if( term.length >= 2 )
					{
						this.password = term[1];
					}
				}
			}
		}
		if(    this.username != null && this.username.length > 0 
			&& this.password != null && this.password.length > 0 )
		{
			MAX_REST.userId   = this.username;
			MAX_REST.passWord = this.password;
			return true;
		}
		return false;
	};
	
	this.isLoggedIn = function()
	{
		var cookies = document.cookie.split(';');
		for( var i = 0; i < cookies.length; i++ ) 
		{
			var cookie = cookies[i];
			while( cookie.charAt(0)==' ') 
			{
				cookie = cookie.substring(1);
			}
			if (cookie.indexOf( "LtpaToken2" ) == 0 ) 
			{
				this.appServerAuth = true;
				this.loginConplete();
				return true;
			}
			if (cookie.indexOf( "JSESSIONID" ) == 0 ) 
			{
				this.loginConplete();
				return true;
			}
		}
		return false;
	}
	
	this.login = function() 
	{
		var _self = this;
		
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/login";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onLogin( this ); };;
		xmlReq.open( "GET", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		var authString = this.username + ":" + this.password;
		var encoded = btoa( authString );
		xmlReq.setRequestHeader( "Authorization", "Basic " + encoded );
		xmlReq.setRequestHeader( "maxauth", encoded );

		xmlReq.send();
	};
	
	this.loginConplete = function()
	{
		this.loggedIn      = true;
		this.controler.onLogin( this.username );
	}

	this.loginFormAuth = function()
	{
		var _self = this;
		
		var url = location.protocol + "//";
		url = url + location.hostname;
		if( location.port != null && location.port.length > 0 )
		{
			url = url = ":" + location.port;
		}
		var path = location.pathname;
		var idx = path.indexOf( "/webclient" );
		path = path.substring( 0, idx );

		url = url + path + "/j_security_check";

		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { _self.onLoginForm( this ); };;
		xmlReq.open( "POST", url, true );

		var form = "j_username=" + this.username + "&j_password=" + this.password;

		xmlReq.setRequestHeader( "Accept", "text/html,application/xhtml+xml,application/xml" );
		xmlReq.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" );
		xmlReq.withCredentials = true;

		xmlReq.send( form );
	};

	this.onLogin = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			if( request.status == 400  )		// Maximo returns a 400 on a bad password
			{
				this.controler.displayMessageBox( request.responseText, 
												 IBM.ewNS.Strings.KEY_TITLE_LOGIN_MSG );
				this.displayLoginForm();
				return;
			}
			else if( request.status == 401 )
			{
				this.controler.displayMessageBox( IBM.ewNS.Strings.KEY_LOGIN_FAILURE, 
												 IBM.ewNS.Strings.KEY_TITLE_LOGIN_MSG );
				this.displayLoginForm();
				return;
			}
			else
			{
				this.controler.displayError( request.status + ":" + request.responseText );
				return;
			}
		}

		var loginScreen =  document.getElementById( "login" );
		loginScreen.style.visibility = "hidden";
		this.loginConplete();
	}

	// Form login
	this.onLoginForm = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			if( request.status == 401 || (  request.status == 500 && this.formAuth ) )
			{
				this.controler.displayMessageBox( IBM.ewNS.Strings.KEY_LOGIN_FAILURE, 
												  IBM.ewNS.Strings.KEY_TITLE_LOGIN_MSG );
				this.displayLoginForm();
				return;
			}
			if( request.status == 404  )
			{
				this.formAuth = true;
				// Successful login - Drop through
			}
			else if( request.status == 500 || request.status == 0 )		// Form auth not enabled
			{
				this.login();
				return;
			}
			else
			{
				this.controler.displayError( request.status + ":" + request.responseText );
				return;
			}
		}

		var loginScreen =  document.getElementById( "login" );
		loginScreen.style.visibility = "hidden";
		this.loginConplete();
	};

	this.onLoginKeyPress = function(
		ctrl, e
	) {
		var keynum = 0;
		
		if(window.event) 	// IE
		{
			keynum = window.event.keyCode;
		}
		else if( e.which )	 // Netscape/Firefox/Opera
		{
			keynum = e.which;
		}

		if( keynum == 13 )
		{
			this.formLogin();
		}
		return true;
	}

	// Constructor code
	if( this.isLoggedIn() )
	{
		return;
	}
	if( this.hasCrdentials() )
	{
		this.loginFormAuth();
		return;
	}
	this.displayLoginForm();
}
		
		
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
		/*
		var ctrl   = document.createElement("IMG");
		ctrl.src   = this.mapMgr.controler.imagePath + "/bim/360_GPS.png";
        ctrl.alt   = IBM.ewNS.Strings.DLG_BTN_LOCATE;
        ctrl.title = IBM.ewNS.Strings.DLG_BTN_LOCATE;
		ctrl.className = "maxlmv_clickableImage";
		buttonGPSLocate.container.childNodes[0].appendChild( ctrl );
		*/
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
  * Create Work History Dialog
  *****************************************************************************************************
  */
IBM.ewNS.WorkHistDlg = function (
	forgeViewer, parent, assetId
) {
	const _forgeViewer = forgeViewer;
	var scrollContainer;
	var descInput;
	var detailsInput;
	var priorityInput;
 
 	this.asset     = null;
	this.labels    = null;		
	this.parent    = parent;
	this.assetId   = assetId;
	
	this.columns = [ "DESCRIPTION", "STATUS", "STATUSDATE" ];		// List of fields from Work Order record to display

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
	
	this.lookupWorkHistByModelId = function(
		guid
	) {
		var that = this;
		
		this.currentAsset = null;
		if( guid == null || guid.length == 0 )
		{
			this.scrollContainer.innerHTML = IBM.LMV.Strings.ERR_NO_GUID;
			return;
		}
		var url = IBM.LMV.Auth.getRestURL();
		url = url + "/os/MXBIMASSETWO";
		url = url + "?MODELID=~eq~" + guid + "&_compact=1";
		var xmlReq = new XMLHttpRequest();
		xmlReq.onreadystatechange = function() { that.onWorkHist( this ); };
		xmlReq.open( "GET", url, true );
		IBM.LMV.Auth.setRequestHeaders( xmlReq );
		IBM.LMV.Auth.addAuthHeaders( xmlReq );

		xmlReq.send();
	};

	this.onWorkHist = function( request )
	{
		if( request.readyState != 4 )  
		{ 
			return; 
		}

		if( request.status != 200 )
		{
			this.asset = null;
			IBM.LMV.RESTError( request.status, IBM.LMV.Strings.ERR_REST, request.responseText );
			this.uninitialize();
			return;
		}

		var mboSet = JSON.parse( request.responseText );
		var assets = mboSet.QueryMXBIMASSETWOResponse.MXBIMASSETWOSet.ASSET;
		if( assets.length == 0 )
		{
			this.makeWCreateWOScreen( null );
			return;
		}
		this.asset      = assets[0];
		this.workOrders = assets[0].WORKORDER;

		var _self = this;
		IBM.LMV.dataDictionary.getLabels( "WORKORDER",   
		                                  function( mbo, labels ) { _self.onHeadings(  mbo, labels  ); } );
		this.populateModelList();
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

	this.onWorkOrderSelect = function( row )
	{
		var mbo = "WORKORDER";
		var wo = [];
		wo[0] = this.workOrders[ row.rowIndex - 1 ];		
		if( IBM.LMV.propertyPannel == null )
		{
			IBM.LMV.propertyPannel = new IBM.LMV.MaximoPropertyPanel( IBM.LMV.viewer.container, _forgeViewer );
		}
		IBM.LMV.propertyPannel.baseMbo = mbo;
		IBM.LMV.propertyPannel.reset( mbo );
		IBM.LMV.propertyPannel.fetcher.displayWorkOrder( IBM.LMV.propertyPannel, wo );
		if( !IBM.LMV.propertyPannel.isVisible() )
		{
			IBM.LMV.propertyPannel.setVisible( true );
		}
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
	};

	this.populateModelList = function() 
	{
		while( this.table.rows.length > 1 )		// Leaver header and filter rows
		{
			this.table.deleteRow( 1 );
		}
		
		if( !this.asset )
		{
			var row = this.table.insertRow( 1 );	// Skip heading and filter
			row.className   = "maxlmv_selectable";
			var cell = row.insertCell( col );
			cell.className = "maxlmv_DlgText";
			cell.innerHTML = IBM.ewNS.Strings.KEY_NO_ASSETS_FOUND;
		}

		if( this.workOrders.length == 0 )
		{
			var row = this.table.insertRow( 1 );	// Skip heading and filter
			row.className   = "maxlmv_selectable";
			var cell = row.insertCell( col );
			cell.className = "maxlmv_DlgText";
			cell.innerHTML = BM.ewNS.Strings.KEY_NO_MODELS_FOR_QUERY;
		}

		var _self = this;
		
		for( var i = 0; i < this.workOrders.length; i++ )
		{
			var wo = this.workOrders[i];
			var row = this.table.insertRow( i + 1 );	// Skip heading and filter
			row.onclick  = function() { _self.onWorkOrderSelect( this ) };
			row.className   = "maxlmv_selectable";

			for( var col in this.columns )
			{
				var cell = row.insertCell( col );
				cell.className = "maxlmv_DlgText";
				if( wo[ this.columns[ col ] ] != null )
				{
					cell.innerHTML = wo[ this.columns[ col ] ];
				}
			}
		}
		
		this.scrollContainer.appendChild( this.table );
	};

	Autodesk.Viewing.UI.DockingPanel.call( this, parent, "Maximo-WorkorderCreate-DLG", IBM.ewNS.Strings.KEY_WO_HIST_TITLE );
};

IBM.ewNS.WorkHistDlg.prototype = Object.create( Autodesk.Viewing.UI.DockingPanel.prototype );
IBM.ewNS.WorkHistDlg.prototype.constructor = IBM.ewNS.WorkHistDlg;

IBM.ewNS.WorkHistDlg.prototype.initialize = function()
{
	Autodesk.Viewing.UI.DockingPanel.prototype.initialize.call( this );

	this.scrollContainer = this.createScrollContainer( {} );
	this.scrollContainer.className = "maxlmv_DlgScroll";
	this.container.appendChild( this.scrollContainer );
	
	this.makeTable();

	this.container.style.height   = "auto";
	this.container.style.maxWidth = "600px";
	this.container.style.zIndex   = 600;
};

IBM.ewNS.WorkHistDlg.prototype.setVisible = function(
	show
) {
	Autodesk.Viewing.UI.DockingPanel.prototype.setVisible.call(this,show);
	if( show )
	{
		var _self = this;
		this.lookupWorkHistByModelId( this.assetId );
	
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
	</script>
</head>

<body class="startScreen" onload="initialize()">
	<div id="mainPage" class="mainPage" >
    </div>
    
   	<div id="login" role="main" style="position:absolute; top:0; visibility:hidden; z-index:10000">
		<table id="main_tbl" class="main_tbl" cellpadding="0" cellspacing="3" role="presentation">
			<tr role="banner">
				<td>
<%					//Branding image
					if(branding == MXServerRemote.BRAND_TIVOLI || branding == MXServerRemote.BRAND_MAXIMO_AND_TIVOLI)
					{
%>					<img class="defaultbrandinglogo" src="../login/images/tivoli_brandmark.png" alt="Tivoli" />
<%					}
%>				</td>
				<td align="<%=reverseAlign%>">
					<img class="defaultibmlogo" src="../login/images/ibm-logo-white.gif" alt="IBM" />
				</td>
			</tr>
			<tr>
				<td class="dialog" colspan="2">
<%			if(branding == MXServerRemote.BRAND_MAXIMO || branding == MXServerRemote.BRAND_MAXIMO_AND_TIVOLI)
			{
				//  Welcome to MAXIMO
%>				<h1 class="prod_name"><%=labels.welcomeToMaximo%></h1>
<%			}
			else
			{
				// Welcome,
%>				<h1 class="prod_name"><%=labels.welcome%></h1>
<%			}
			if(!isMobile && message != null)
			{
%>				<div class="errorText">
					<img id="error_img" src="../login/images/st16_critical_24.png" alt="Error" align="absmiddle"/>
					<%=message%>
				</div>
<%			}
%>				<table cellpadding="0" cellspacing="0" role="presentation">
						<tr>
							<td colspan="5" align="<%=reverseAlign%>">
							<%	if(branding == MXServerRemote.BRAND_MAXIMO || branding == MXServerRemote.BRAND_MAXIMO_AND_TIVOLI)
								{
									//  Welcome to MAXIMO
					%>				<h1 class="prod_name ext_prod_name" style="display:none"><%=labels.welcomeToMaximo%></h1>
					<%			}
								else
								{
									// Welcome,
					%>				<h1 class="prod_name ext_prod_name" style="display:none"><%=labels.welcome%></h1>
					<%			} %>
							</td>
						</tr>
						<tr>
							<td valign="top"><img src="../login/images/mx_icon<%=isMobile?"_ev":""%>.png" alt="" /></td>
							<td class="input_pad">
								<label for="<%=userFieldName%>" dir="<%=direction%>"><%=labels.username%></label><br />
<%								String attrs = "";
								if(BidiUtils.isBidiEnabled())
								{
									attrs = BidiUtils.buildTagAttribute("",BidiUtils.getMboTextDirection("MAXUSER","LOGINID",true),"",true);
									attrs += "onkeyup='processBidiKeys(null,this)' onfocus='input_bidi_onfocus(null, this)' onblur='input_bidi_onblur(null, this)' onchange='input_bidi_onfocus(null, this)' ";
								}
%>									<input <%=attrs%> style="width:<%=isMobile?145:195%>px" name="<%=userFieldName%>" id="<%=userFieldName%>" langcode="<%=langcode%>" type="text" value="<%=HTML.encode(userName)%>" />
									<br /><br />
									<label for="<%=passwordFieldName%>" dir="<%=direction%>"><%=labels.password%></label><br />
									<input style="width:<%=isMobile?145:195%>px" name="<%=passwordFieldName%>" id="<%=passwordFieldName%>" type="password"  value=""/>
									<br /><br />
								<%  if(mtEnabled)
									{ %>
									<div id="tenantinfo" style="display:none" aria-hidden="true">
										<label for="<%=tenantFieldName%>"><%=labels.tenantId%></label>
										<br />
									 	<input <%=attrs%> style="width:<%=isMobile?145:195%>px" name="<%=tenantFieldName%>" id="<%=tenantFieldName%>" langcode="<%=langcode%>" type="text" value="<%=HTML.encode(tenant)%>"/>
										<br /><br />
									</div>
								<%	} %>										
									<div style="text-align: <%=reverseAlign%>">
										<button class="tiv_btn" type="submit" id="loginbutton" value="1" >
											<%=labels.loginButton%>
										</button>
									</div>
							</td>
						</tr>
<%
					if ("1".equals( _session.getProperty(WebClientConstants.WEBCLIENT_GUEST_LOGIN)) && !mtEnabled)          
					{
						String guestLoginLabel = MXServer.getMXServer().getMessage("login", "guestloginlabel", langcode);
						String guestLoginURL = _session.getProperty(WebClientConstants.WEBCLIENT_GUEST_LOGIN_URL);
%>
						<tr>
							<td align="<%=reverseAlign%>" colspan="2">
								<div style="text-align: <%=reverseAlign%>">
								</div>
								<br />
							</td>
						</tr>
<%					}
					if(mtEnabled){ %>	
						<tr>
							<td align="<%=reverseAlign%>" height="18" colspan="2">
							</td>
						</tr>				
				<%	}
					if((!formAuth || "1".equals( _session.getProperty("mxe.AllowUserMgmt"))) && isMobile)
					{
%>						<tr>
							<td align="<%=reverseAlign%>" height="18" colspan="2">
							</td>
						</tr>
						<tr>
							<td align="<%=reverseAlign%>" height="18" colspan="2">
							</td>
						</tr>
<%					}
					if(langNameToCodeMap != null)
					{	
						String[] langNames = langNameToCodeMap.keySet().toArray(new String[langNameToCodeMap.size()]);
						Arrays.sort(langNames);
%>						<tr height="<%=isMobile?"104":"30"%>px">
							<td nowrap="nowrap" align="<%=reverseAlign%>" colspan="2">
							</td>
						</tr>
<%					}
					else if(isMobile)
					{
%>						<tr height="104px"><td colspan="2">&nbsp;</td></tr>
<%					}
					if((!formAuth || "1".equals( _session.getProperty("mxe.AllowUserMgmt"))) && !isMobile)
					{
						if(everyplace)
						{
%>						<tr>
							<td align="<%=reverseAlign%>" height="18" colspan="2">
							</td>
						</tr>
<%						}
%>						<tr>
							<td align="<%=reverseAlign%>" height="18" colspan="2">
							</td>
						</tr>
						<tr>
							<td align="<%=reverseAlign%>" height="18" colspan="2">
							</td>
						</tr>
<%					}
%>				</table>
				</td>
			</tr>
			<%
			if (MXFormat.stringToBoolean(isSaasEnabled))
			{
%>			
			<tr style="height:10px;">
			</tr>
			<tr>
				<td colspan="2" style="text-align:justify;" >
				<%	if(MXFormat.stringToBoolean(isFederal)){ %>
				<span><%=labels.federalNotice%></span>
				<%} else {%>
				<span><%=labels.nonFederalNotice%></span>
<%
			}
%>	
				</td>
			</tr>
<%
			}
%>						
			<tr>
				<td colspan="2" class="copyright" role="contentinfo"><img alt="IBM" src="../login/images/ibm-logo-white.gif" style="display: none"><p><%=labels.copyright%></p></td>
			</tr>
		</table>
	</div>

</body>
</html>