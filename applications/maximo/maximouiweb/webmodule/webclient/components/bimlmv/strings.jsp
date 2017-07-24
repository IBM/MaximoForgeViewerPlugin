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
<%@page import ="java.rmi.RemoteException"%>
<%@page import ="psdi.server.MXServer"%>
<%@page import ="psdi.util.MXException"%>

<%!
private static class StringTable
{
	private static final String group  = "bimlmv";
	
	final String catAddress; 
	final String catCost;
	final String catBase;
	final String catDetail;
	final String catDown;
	final String catFacility;
	final String catModify;
	final String catPurchase;
	final String catOther;
	final String catSpec;

	final String keyOf;
	final String keyDash;
	final String keyOpenFilter;
	final String keyCloseFilter;
	final String keyFilterTable;
	final String keyPrevPage;
	final String keyNextPage;
	final String keyClose;
	final String keyLoadingProperties;
	final String keyColorRed;
	final String keyColorGreen;
	final String keyColorBlue;

	final String toolbarHideSelection;
	final String toolbarSearch;
	final String toolbarSelectionMode;
	final String toolbarZoomMode;
	final String toolbarIsolateSel;

	final String dlgBtnApply;
	final String dlgBtnCreate;
	final String dlgBtnCancel;
	final String dlgBtnDeleteView;
	final String dlgBtnDeleteMarkup;
	final String dlgBtnOk;
	final String dlgBtnView;
	final String dlgBtnLocate;
	
	final String dlgTTSaveView;
	final String dlgTTApplyView;
	final String dlgLabelSaveView;
	final String dlgLableApplyView;
	final String dlgLabelMarkupDesc;
	final String dlgLabelLineWeight;
	final String dlgLabelFill;
	final String dlgLabelFontSize;
	final String dlgLabelItalics;
	final String dlgLabelBold;
	final String dlgTextConfirmDelete;
	final String dlgTextDeleteView;
	final String dlgTitleSaveView;
	final String dlgTitleSaveMarkup;
	final String dlgTitleApplyView;
	final String dlgTitleDisplayMarkup;
	final String dlgTitleMarkupStyle;

	
	final String errREST;

	final String mapBtnModelList;

	final String msgNoGUID;
	final String msgNotAsset;
	final String msgTitleReqFields;
	final String msgConfirmDeleteMarkup;

	final String keyError;
	final String keyDisableAutoZoom;
	final String keyEnableAUtoZoom;
	final String keyAttachments;
	final String keyClassStruct;
	final String keyParent;

	final String keyAssetPropTitle;
	final String keyCompanyPropTitle;
	final String keyLocPropTitle;
	final String keyProductPropTitle;
	final String keyWOPropTitle;
	
	final String keyMarkupStart;
	final String keyMarkupCancel;
	final String keyMarkupSave;
	final String keyMarkupShow;
	final String keyMarkupArrow;
	final String keyMarkupCloud;
	final String keyMarkupFreehand;
	final String keyMarkupLine;
	final String keyMarkupOval;
	final String keyMarkupRectangle;
	final String keyMarkupText;
	
	final String keyResizeBtn;


		
	StringTable(
		String langCode
	) 
		throws MXException, RemoteException
	{
		MXServer server  = MXServer.getMXServer();
		
		errREST               = server.getMessage(group, "ERR_REST", langCode);

	    keyAssetPropTitle     = server.getMessage(group, "KEY_ASSET_PROP_TITLE", langCode);
		keyOf                 = server.getMessage(group, "KEY_OF", langCode);
		keyDash               = server.getMessage(group, "KEY_DASH", langCode);
	    keyOpenFilter         = server.getMessage(group, "KEY_OPEN_FILTER", langCode);
	    keyCloseFilter        = server.getMessage(group, "KEY_CLOSE_FILTER", langCode);
	    keyFilterTable        = server.getMessage(group, "KEY_FILTER_TABLE", langCode);
	    keyPrevPage           = server.getMessage(group, "KEY_PREV_PAGE", langCode);
	    keyNextPage           = server.getMessage(group, "KEY_NEXT_PAGE", langCode);
	    keyClose              = server.getMessage(group, "KEY_CLOSE", langCode);
        toolbarHideSelection  = server.getMessage(group, "TOOLBAR_HIDE_SELECTION", langCode);
		toolbarIsolateSel     = server.getMessage(group, "TOOLBAR_ISOLATE_SELECTION", langCode);
        toolbarSearch         = server.getMessage(group, "TOOLBAR_SEARCH", langCode);
        toolbarSelectionMode  = server.getMessage(group, "TOOLBAR_SELECTION_MODE", langCode);
        toolbarZoomMode       = server.getMessage(group, "TOOLBAR_ZOOM_MODEL", langCode);
        
        dlgBtnApply           = server.getMessage(group, "DLG_BTN_APPLY", langCode);
        dlgBtnCreate          = server.getMessage(group, "DLG_BTN_CREATE", langCode);
        dlgBtnCancel          = server.getMessage(group, "DLG_BTN_CANCEL", langCode);
        dlgBtnDeleteView      = server.getMessage(group, "DLG_BTN_DELETE_VIEW", langCode);
        dlgBtnOk              = server.getMessage(group, "DLG_BTN_OK", langCode);
    	
        dlgTTSaveView         = server.getMessage(group, "DLG_TT_SAVE_VIEW", langCode);
        dlgTTApplyView        = server.getMessage(group, "DLG_TT_APPLY_VIEW", langCode);
        dlgLabelSaveView      = server.getMessage(group, "DLG_LABEL_SAVE_VIEW", langCode);
        dlgLableApplyView     = server.getMessage(group, "DLG_LABEL_APPLY_VIEW", langCode);
		dlgTextDeleteView     = server.getMessage(group, "DLG_TXT_DELETE_VIEW", langCode);
        dlgTitleSaveView      = server.getMessage(group, "DLG_TITLE_SAVE_VIEW", langCode);
        dlgTitleApplyView     = server.getMessage(group, "DLG_TITLE_APPLY_VIEW", langCode);

        msgTitleReqFields     = server.getMessage(group, "MSG_TITLE_REQUIRED_FIELD", langCode);

		msgNoGUID             = server.getMessage(group, "ERR_NO_GUID", langCode);
		msgNotAsset           = server.getMessage(group, "MSG_NOT_ASSET", langCode);
		dlgBtnView            = server.getMessage(group, "DLG_BTN_VIEW", langCode);
		dlgBtnLocate          = server.getMessage(group, "DLG_BTN_LOCATE", langCode);
		mapBtnModelList       = server.getMessage(group, "MAP_BTN_MODELLIST", langCode);
		
		keyLocPropTitle       = server.getMessage(group, "KEY_LOCATION_PROP_TITLE", langCode);
		keyLoadingProperties  = server.getMessage(group, "KEY_PROP_LOADIN_MSG", langCode);
		catAddress            = server.getMessage(group, "CAT_ADDRESS", langCode); 
		catCost               = server.getMessage(group, "CAT_COST", langCode);
		catBase               = server.getMessage(group, "CAT_BASE", langCode);
		catDetail             = server.getMessage(group, "CAT_DETAIL", langCode);
		catDown               = server.getMessage(group, "CAT_DOWN", langCode);
		catFacility           = server.getMessage(group, "CAT_FACILITY", langCode);
		catModify             = server.getMessage(group, "CAT_MODIFY", langCode);
		catPurchase           = server.getMessage(group, "CAT_PURCHASE", langCode);
		catOther              = server.getMessage(group, "CAT_OTHER", langCode);
		catSpec               = server.getMessage(group, "CAT_SPEC", langCode);

		keyError              = server.getMessage(group, "KEY_ERROR", langCode);
		keyDisableAutoZoom    = server.getMessage(group, "KEY_DISABLE_AUTO_ZOOM", langCode);
		keyEnableAUtoZoom     = server.getMessage(group, "KEY_ENABLE_AUTO_ZOOM", langCode);
		keyAttachments        = server.getMessage(group, "KEY_ATTACHMENT", langCode);
		keyClassStruct        = server.getMessage(group, "KEY_CLASS_STRUCT", langCode);
		keyParent             = server.getMessage(group, "KEY_PARENT", langCode);
		keyCompanyPropTitle   = server.getMessage(group, "KEY_COMPANY_PROP_TITLE", langCode);
		keyProductPropTitle   = server.getMessage(group, "KEY_PRODUCT_PROP_TITLE", langCode);
		keyWOPropTitle        = server.getMessage(group, "KEY_WO_PROP_TITLE", langCode);
		keyResizeBtn          = server.getMessage(group, "resize_btn", langCode);
		
		dlgTitleSaveMarkup     = server.getMessage(group, "DLG_TITLE_SAVE_MARKUP", langCode); 
		dlgTextConfirmDelete   = server.getMessage(group, "DLG_TITLE_CONFIRM_DELETE", langCode); 
		dlgTitleDisplayMarkup  = server.getMessage(group, "DLG_TITLE_DISPLAY_MARKUP", langCode); 
		dlgTitleMarkupStyle    = server.getMessage(group, "DLG_TITLE_MARKUP_STYLE", langCode); 
        dlgBtnDeleteMarkup     = server.getMessage(group, "DLG_BTN_DELETE_MARKUP", langCode); 
        dlgLabelMarkupDesc     = server.getMessage(group, "DLG_LABEL_MARKUP_DESC", langCode); 
		dlgLabelLineWeight     = server.getMessage(group, "DLG_LABEL_LINE_WEIGHT", langCode); 
		dlgLabelFill           = server.getMessage(group, "DLG_LABLE_FILL", langCode); 
		dlgLabelFontSize       = server.getMessage(group, "DLG_LABLE_FONT_SIZE", langCode); 
		dlgLabelItalics        = server.getMessage(group, "DLG_LABLE_ITALICS", langCode); 
		dlgLabelBold           = server.getMessage(group, "DLG_LABLE_BOLD", langCode);


		msgConfirmDeleteMarkup = server.getMessage(group, "MSG_CONFIRM_DELETE_MAKRUP", langCode); 

 		keyMarkupStart         = server.getMessage(group, "MARKUP_START_EDIT", langCode); 
 		keyMarkupCancel        = server.getMessage(group, "MARKUP_CANCEL", langCode); 
 		keyMarkupSave          = server.getMessage(group, "MARKUP_SAVE", langCode); 
 		keyMarkupShow          = server.getMessage(group, "MARKUP_SHOW", langCode); 
 		keyMarkupArrow         = server.getMessage(group, "MARKUP_ARROW", langCode); 
 		keyMarkupCloud         = server.getMessage(group, "MARKUP_CLOUD", langCode); 
 		keyMarkupFreehand      = server.getMessage(group, "MARKUP_FREEHAND", langCode); 
 		keyMarkupLine          = server.getMessage(group, "MARKUP_LINE", langCode); 
 		keyMarkupOval          = server.getMessage(group, "MARKUP_OVAL", langCode); 
 		keyMarkupRectangle     = server.getMessage(group, "MARKUP_RECTANGLE", langCode); 
 		keyMarkupText          = server.getMessage(group, "MARKUP_TEXT", langCode); 
		
		keyColorRed            = server.getMessage(group, "NAKRUP_RED", langCode); 
		keyColorGreen          = server.getMessage(group, "MAKRUP_GREEN", langCode); 
		keyColorBlue           = server.getMessage(group, "MARKUP_BLUE", langCode); 

	}
}
%>
<%
	StringTable stringTable  = new StringTable(langcode);
%>

		IBM.LMV.Strings.TOOLBAR_HIDE_SELECTION     = "<%=stringTable.toolbarHideSelection%>";
		IBM.LMV.Strings.TOOLBAR_ISOLATE_SELECTION  = "<%=stringTable.toolbarIsolateSel%>";
		IBM.LMV.Strings.TOOLBAR_SEARCH             = "<%=stringTable.toolbarSearch%>";
		IBM.LMV.Strings.TOOLBAR_SELECTION_MODE     = "<%=stringTable.toolbarSelectionMode%>";
		IBM.LMV.Strings.TOOLBAR_ZOOM_MODEL         = "<%=stringTable.toolbarZoomMode%>";
		IBM.LMV.Strings.TOOLBAR_RESIZE             = "<%=stringTable.keyResizeBtn%>";
		
		IBM.LMV.Strings.DLG_BTN_APPLY              = "<%=stringTable.dlgBtnApply%>";
        IBM.LMV.Strings.DLG_BTN_CREATE             = "<%=stringTable.dlgBtnCreate%>";      
        IBM.LMV.Strings.DLG_BTN_CANCEL             = "<%=stringTable.dlgBtnCancel%>";     
		IBM.LMV.Strings.DLG_BTN_CLOSE              = "<%=stringTable.keyClose%>";
        IBM.LMV.Strings.DLG_BTN_DELETE_VIEW        = "<%=stringTable.dlgBtnDeleteView%>";     
        IBM.LMV.Strings.DLG_BTN_DELETE_MARKUP      = "<%=stringTable.dlgBtnDeleteMarkup%>";     
        IBM.LMV.Strings.DLG_BTN_OK                 = "<%=stringTable.dlgBtnOk%>"; 
    	
        IBM.LMV.Strings.DLG_TT_SAVE_VIEW           = "<%=stringTable.dlgTTSaveView%>";
        IBM.LMV.Strings.DLG_TT_APPLY_VIEW          = "<%=stringTable.dlgTTApplyView%>";
        IBM.LMV.Strings.DLG_LABEL_SAVE_VIEW        = "<%=stringTable.dlgLabelSaveView%>";
        IBM.LMV.Strings.DLG_LABEL_APPLY_VIEW       = "<%=stringTable.dlgLableApplyView%>";
        IBM.LMV.Strings.DLG_LABEL_MARKUP_DESC      = "<%=stringTable.dlgLabelMarkupDesc%>";
        IBM.LMV.Strings.DLG_LABEL_LINE_WEIGHT      = "<%=stringTable.dlgLabelLineWeight%>";
        IBM.LMV.Strings.DLG_LABLE_FILL             = "<%=stringTable.dlgLabelFill%>";
        IBM.LMV.Strings.DLG_LABLE_FONT_SIZE        = "<%=stringTable.dlgLabelFontSize%>";
        IBM.LMV.Strings.DLG_LABLE_ITALICS          = "<%=stringTable.dlgLabelItalics%>";
        IBM.LMV.Strings.DLG_LABLE_BOLD             = "<%=stringTable.dlgLabelBold%>";
        IBM.LMV.Strings.DLG_TXT_DELETE_VIEW        = "<%=stringTable.dlgTextDeleteView%>";
        IBM.LMV.Strings.DLG_TITLE_CONFIRM_DELETE   = "<%=stringTable.dlgTextConfirmDelete%>";
        IBM.LMV.Strings.DLG_TITLE_SAVE_VIEW        = "<%=stringTable.dlgTitleSaveView%>";
        IBM.LMV.Strings.DLG_TITLE_SAVE_MARKUP      = "<%=stringTable.dlgTitleSaveMarkup%>";
        IBM.LMV.Strings.DLG_TITLE_APPLY_VIEW       = "<%=stringTable.dlgTitleApplyView%>";
        IBM.LMV.Strings.DLG_TITLE_DISPLAY_MARKUP   = "<%=stringTable.dlgTitleDisplayMarkup%>";
        IBM.LMV.Strings.DLG_TITLE_MARKUP_STYLE     = "<%=stringTable.dlgTitleMarkupStyle%>";

        IBM.LMV.Strings.MSG_TITLE_REQUIRED_FIELD   = "<%=stringTable.msgTitleReqFields%>";
		IBM.LMV.Strings.ERR_NO_GUID                = "<%=stringTable.msgNoGUID%>";
		IBM.LMV.Strings.ERR_REST                   = "<%=stringTable.errREST%>";  
	
		IBM.LMV.Strings.KEY_ATTACHMENT             = "<%=stringTable.keyAttachments%>";
		IBM.LMV.Strings.KEY_CLASS_STRUCT           = "<%=stringTable.keyClassStruct%>";
		IBM.LMV.Strings.KEY_PROP_LOADIN_MSG        = "<%=stringTable.keyLoadingProperties%>";
		IBM.LMV.Strings.KEY_PREV_PAGE              = "<%=stringTable.keyPrevPage%>";
		IBM.LMV.Strings.KEY_DISABLE_AUTO_ZOOM      = "<%=stringTable.keyDisableAutoZoom%>";  
		IBM.LMV.Strings.KEY_ENABLE_AUTO_ZOOM       = "<%=stringTable.keyEnableAUtoZoom%>";  
		IBM.LMV.Strings.KEY_PARENT                 = "<%=stringTable.keyParent%>";  

		IBM.LMV.Strings.KEY_ASSET_PROP_TITLE       = "<%=stringTable.keyAssetPropTitle%>";
		IBM.LMV.Strings.KEY_COMPANY_PROP_TITLE     = "<%=stringTable.keyCompanyPropTitle%>";
		IBM.LMV.Strings.KEY_LOCATION_PROP_TITLE    = "<%=stringTable.keyLocPropTitle%>";
        IBM.LMV.Strings.KEY_PRODUCT_PROP_TITLE     = "<%=stringTable.keyProductPropTitle%>";
		IBM.LMV.Strings.KEY_WO_PROP_TITLE          = "<%=stringTable.keyWOPropTitle%>";  
		
		IBM.LMV.Strings.CAT_ADDRESS                = "<%=stringTable.catAddress%>";
		IBM.LMV.Strings.CAT_COST                   = "<%=stringTable.catCost%>";
		IBM.LMV.Strings.CAT_BASE                   = "<%=stringTable.catBase%>";
		IBM.LMV.Strings.CAT_DETAIL		           = "<%=stringTable.catDetail%>";
		IBM.LMV.Strings.CAT_DOWN		           = "<%=stringTable.catDown%>";
		IBM.LMV.Strings.CAT_FACILITY		       = "<%=stringTable.catFacility%>";
		IBM.LMV.Strings.CAT_MODIFY		           = "<%=stringTable.catModify%>";
		IBM.LMV.Strings.CAT_PURCHASE		       = "<%=stringTable.catPurchase%>";
		IBM.LMV.Strings.CAT_OTHER		           = "<%=stringTable.catOther%>";
		IBM.LMV.Strings.CAT_SPEC		           = "<%=stringTable.catSpec%>";
        
        IBM.LMV.Strings.MARKUP_START_EDIT          = "<%=stringTable.keyMarkupStart%>";
        IBM.LMV.Strings.MARKUP_CANCEL              = "<%=stringTable.keyMarkupCancel%>";
        IBM.LMV.Strings.MARKUP_SAVE                = "<%=stringTable.keyMarkupSave%>";
        IBM.LMV.Strings.MARKUP_SHOW                = "<%=stringTable.keyMarkupShow%>";
        
        IBM.LMV.Strings.MARKUP_ARROW               = "<%=stringTable.keyMarkupArrow%>";
        IBM.LMV.Strings.MARKUP_CLOUD               = "<%=stringTable.keyMarkupCloud%>";
        IBM.LMV.Strings.MARKUP_FREEHAND            = "<%=stringTable.keyMarkupFreehand%>";
        IBM.LMV.Strings.MARKUP_LINE                = "<%=stringTable.keyMarkupLine%>";
        IBM.LMV.Strings.MARKUP_OVAL                = "<%=stringTable.keyMarkupOval%>";
        IBM.LMV.Strings.MARKUP_RECTANGLE           = "<%=stringTable.keyMarkupRectangle%>";
        IBM.LMV.Strings.MARKUP_TEXT                = "<%=stringTable.keyMarkupText%>";

        IBM.LMV.Strings.MARKUP_RED                 = "<%=stringTable.keyColorRed%>";
        IBM.LMV.Strings.MARKUP_GREEN               = "<%=stringTable.keyColorGreen%>";
        IBM.LMV.Strings.MARKUP_BLUE                = "<%=stringTable.keyColorBlue%>";
        
        IBM.LMV.Strings.MSG_CONFIRM_DELETE_MAKRUP  = "<%=stringTable.msgConfirmDeleteMarkup%>";