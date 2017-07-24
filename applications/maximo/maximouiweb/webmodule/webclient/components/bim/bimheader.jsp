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
<%@ page contentType="text/html;charset=UTF-8" buffer="none"%>
<%@page import="java.rmi.RemoteException"%>
<%@page import="psdi.server.MXServer"%>
<%@page import="psdi.util.MXException"%>

<%@page import="org.w3c.dom.*, psdi.mbo.*, psdi.util.*, psdi.webclient.system.controller.*, psdi.webclient.system.beans.*, psdi.webclient.system.runtime.*"%>
<%@page import="psdi.webclient.servlet.*, psdi.webclient.system.session.*, psdi.webclient.controls.*, psdi.webclient.components.*"%>
<%@page import="psdi.webclient.controls.*, psdi.webclient.components.*, java.util.*, java.io.*, com.ibm.json.java.JSONObject"%>
<%@ include file="../../common/constants.jsp" %>

<%!
private static class ViewerStringTable
{
	final String msgNoViewer;
	final String msgNoModel;
	final String msgSearchFail;
	final String msgNoID;
	final String msgNoInspect;
	final String newWOBtn;
	final String displayWOBtn;
	final String createTicketBtn;
	final String inspectAssetBtn;
	final String newSystemBtn;
	final String displaySystemBtn;
	final String updateSystemBtn;
	final String addSelectionBtn;
	final String removeSelectionBtn;
	final String resizeBtn;
	final String resizeTitle;
	final String defaultSize;

	final String autoZoomModeBtn;
	final String gotoLocBtn;
	final String loadModel;
	final String lableModel;
	final String refreshBtn;
		

	private static final String group = "bimviewer";
	
		
	ViewerStringTable(
		String langCode
	) 
		throws MXException, RemoteException
	{
		MXServer server = MXServer.getMXServer();
		
		// No viewer is installed. You can download the Autodesk BIM260 plug-in from {0} 
		// or you can acquire a viewer from  a 3rd party viewer vendor.
		msgNoViewer        = server.getMessage(group, "msg_no_viewer", langCode );
		// There is no model available for the asset or location you are viewing.
		msgNoModel         = server.getMessage(group, "msg_no_model", langCode);
		// Item not found:
		msgSearchFail      = server.getMessage(group, "msg_search_failed", langCode);
		// Select asset does not have a Maximo asset ID
		msgNoID            = server.getMessage(group, "msg_not_in_maximo", langCode);
		// The current Maximo item was not found in the model
		msgNoInspect       = server.getMessage(group, "msg_not_in_model", langCode);
		// Create a work order with the selected asset
		newWOBtn		   = server.getMessage(group, "new_wo_btn", langCode);
		// Search for and display work orders and preventive maintenance
		displayWOBtn       = server.getMessage(group, "display_wo_btn", langCode);
		// Create new service request
		createTicketBtn    = server.getMessage(group, "create_sr_btn", langCode);
		// Inspect asset details
		inspectAssetBtn    = server.getMessage(group, "inspect_asset_btn", langCode);
		// Create a new system from the current selection 
		newSystemBtn       = server.getMessage(group, "system_new_btn", langCode);
		// Display the components of a system as the current selection
		displaySystemBtn   = server.getMessage(group, "system_display_btn", langCode);
		// Update or replace the members of the selected system with the current selection
		updateSystemBtn    = server.getMessage(group, "system_update_btn", langCode);
		// Add selected assets and locations to the work order or ticket
		addSelectionBtn    = server.getMessage(group, "selection_add_btn", langCode);
		// Remove selected assets and locations from the work order or ticket
		removeSelectionBtn = server.getMessage(group, "selection_remove_btn", langCode);
		// Resize
		resizeBtn          = server.getMessage(group, "resize_btn", langCode);
		// Select size
		resizeTitle        = server.getMessage(group, "resize_title", langCode);
		// Default
		defaultSize        = server.getMessage(group, "resize_default", langCode);
		// Enable or disable auto zoom to selection
		autoZoomModeBtn    = server.getMessage(group, "auto_zoom_mode_btn", langCode);
		// Set Maximo location to the model location
		gotoLocBtn         = server.getMessage(group, "goto_loc_btn", langCode);
		// Specify a model for this location
		loadModel          = server.getMessage(group, "model_load", langCode);
		// Models
		lableModel         = server.getMessage(group, "lable_model", langCode);
		// Refresh view
		refreshBtn         = server.getMessage(group, "refresh_btn", langCode);
	}
}
%>


<%
String id = "";
String IMAGE_PATH = "";
String CSS_PATH = "";
String _Id             = request.getParameter("id");
String _renderId       = request.getParameter("rid");
String uiSessionId    = request.getParameter("uisessionid");
WebClientSessionManager wcsm = WebClientSessionManager.getWebClientSessionManager(session);
WebClientSession wcs = wcsm.getWebClientSession(uiSessionId);
String servletBase = wcs.getMaximoRequestContextURL() +  "/webclient";
ControlInstance ci       = wcs.getControlInstance( _Id );
BaseInstance comp     = BIMViewer.findByRenderId( ci, _renderId );

String skin = wcs.getSkin();
String defaultAlign="left";
String reverseAlign="right";
boolean rtl = false;
psdi.util.MXSession s = psdi.webclient.system.runtime.WebClientRuntime.getMXSession(session);
	String langcode = s.getUserInfo().getLangCode();
if(langcode.equalsIgnoreCase("AR")||langcode.equalsIgnoreCase("HE"))
{
	defaultAlign = "right";
	reverseAlign = "left";
	rtl = true;
}

final int VENDOR_OTHER      = 0;
final int VENDOR_NAVISWORKS = 1;
final int VENDOR_A360       = 2;
int viewerVendor = VENDOR_OTHER;

if( !(comp instanceof BIMViewer ) )
{
	return;
} 
BIMViewer bldgMdl = (BIMViewer)comp;
id = _renderId;

int  height     = bldgMdl.getHeight();
String width     = bldgMdl.getWidth();
int  leftOffset = bldgMdl.getLeftOffset();
long appType    = bldgMdl.getAppType();



//Designer mode may put "-" into the ID string which make them invalid for JavaScript 
//idenfiers - Get rid of them after we've gotten the control
id = id.replace( "-", "_" );

IMAGE_PATH = servletBase + "/"+skin+"images/"+(rtl?"rtl/":"")+wcs.getImagePath();
CSS_PATH   = servletBase + "/"+skin+"css/"+(rtl?"rtl":"")+wcs.getCssPath();


/*
* The properties below are loaded from the component definition
* Strored in componet-registry.xml.  All translatable text must
* be handled this way as well a paramenters configurable through
* the prestation XML
*/
String msgTable         = id + "_msgTbl";
String msgToolbarId     = id + "_msgToolbar";

String modelTable       = id + "_modelTbl";
String toolbarId		= id + "_toolbar";
String ribbonId         = id + "_ribbon";

String msgCell          = id + "msgCell";
String ctrlId           = id + "NW";				// Navisworks ActiveX control
String noCtrlId			= id + "_no_ctrl";

String searchId         = id + "_value_text";
String searchFieldId    = id + "_search_field_cb";	 	// Combo box with Navisworks properites options for search
String inspectAssetId   = id + "_instpect_asset_btn";	// Launch inspect asset dialog
String newSystemId		= id + "_new_system_btn";	 	// Launch new system dialog
String displaySystemId	= id + "_display_system_btn";	// Launch Display system dialog
String updateSystemId	= id + "_update_system_btn";	// Launch Update system dialog
String addSelectionId	= id + "_add_selection_btn";	// Launch Update system dialog
String removeSelectionId = id + "_remove_selection_btn";	// Launch Update system dialog
String modeId           = id + "_mode_cb";
String modelId          = id + "_model_cb";			 	// Combo box with list of available models
String viewId           = id + "_view_cb";				// Combo box with list of available saved view in current model

String selIdxId	   	    = id + "_selection_idx";		// Report Current Selection index
String selCountId	    = id + "_selection_count";		// Report number of items in the current selection
String autoZoomMode1Id  = id + "_auto_zoom_mode_1";		// Enable or disable auto zoom
// String autoZoomMode2Id  = id + "_auto_zoom_mode_2";		// Enable or disable auto zoom

String statusId         = id + "_msg_text";				// Message line at bottom of the screen

ViewerStringTable strings = new ViewerStringTable(langcode);


//****************************************************************************************
// Load tralsateable stringa nd user settings from the control definition
//****************************************************************************************
// Setup geometry
String foreground        = bldgMdl.getForegroundColor();
String background        = bldgMdl.getBackgroundColor();
String bordercolor       = bldgMdl.getBoarderColor();
String highlightcolor    = bldgMdl.getHighlightColor();
String tmp        		 = bldgMdl.getProperty("toolbar_height");
int toolbar_height    = Integer.parseInt( tmp );
if( toolbar_height <= 0 )
{
	toolbar_height = 34;
}

// Messages and Errors
String msgNoActiveX  = bldgMdl.getProperty( "msg_no_activex" ).trim().replace( "\n", " " );
String msgLoading    = bldgMdl.getProperty( "msg_loading_file" ).trim();
String msgMemory     = bldgMdl.getProperty( "msg_memory" ).trim();
String msgNotInModel = bldgMdl.getProperty( "msg_not_in_model" ).trim(); 

String msgInitErr    = bldgMdl.getProperty( "msg_not_init" ).trim();
String msgOf         = bldgMdl.getProperty( "msg_of" ).trim();

// Force a reload of the model file if the control is being redrawn
bldgMdl.setModelListChanged( true );
bldgMdl.setValueChanged( true );

// Shorth cuts
String BIM_IMAGE_PATH = IMAGE_PATH + "bim";
String TOOLBAR_IMG = "url('" + BIM_IMAGE_PATH + "/white_grad.png')"; //"/toolbar-bg.jpg')";
int    V_INSET     = 2 * toolbar_height + 30;

%>