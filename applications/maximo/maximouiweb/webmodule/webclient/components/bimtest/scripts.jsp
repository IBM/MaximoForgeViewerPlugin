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
"use strict";


/**********************************************************************/
// Loads the NavisWorks control tryng different versions then sets the
// lable next to the AD logo to the version loaded
/**********************************************************************/
function loadControl(
	parentCtrl,
	versionLableCtrl
) {
	var parentCell  = document.getElementById( parentCtrl );
	var versionCell = document.getElementById( versionLableCtrl );
	var ctrlViewer = document.createElement("DIV");
	ctrlViewer.id = "<%=ctrlId%>";
	ctrlViewer.style.width    = "100%";
	ctrlViewer.style.height   = "100%";
	parentCell.appendChild( ctrlViewer );
}

/**********************************************************************/
// This class contains all the functions needed to interact with the 
// native viewer 
/**********************************************************************/
function ViewerWrapper(
	ctrl	// The control handle
) {
	this.modelMgr  = null;
	this.selMgr    = null;
	this.model;
	this.ctrl      = ctrl;
	
    // Return the version of this interface.  Implementations written to
    // this spec should always return "1.0"
    this.getInterfaceVersion = function()
    {
      return "1.1";
    };
	
    // Called once after all objects are created, but before any methods
    // are called with the exception of setCurrentModel.
    // modelMgr       The ModelManager instance
    // selectionMgr   The SelectionManager instance
    // Return:        Nothing
	this.initialize = function(
		modelMgr,
		selectionMgr
	) {
		this.modelMgr  = modelMgr;
		this.selMgr    = selectionMgr;
	};
	
    // Request that the viewer load the specified file.  Errors should
    // be reported on the viewer status line by calling 
    // setStatus( status )
    // file:          The URL attribute from the Maximo 
    //                BuildingModel table
    // Return:        Nothing
	this.loadFile = function(  
		file
	) {
	};
	
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
	};

	// Called when the current model is changed. This method does not need to load
	// the new model.  loadFile is called for that
    // Return:        Nothing
	this.setCurrentModel = function(
		currentModel
	) {
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
	};
	
	// An array of the currently selected items ids
    this.getSelectionList = function() 
    {
    };
    
    // Return:  THe number of items currently selected
    this.getSelectionCount = function()
    {
    };
    
    // Search the selection list for selected item and return the imdex
    // Return the index of the selected item in the selection list.
    this.getItemIndex = function(
    	selectedItem	// Any item in the list of selected items
 	) {
    };
    
    // Clear all selected item and set the selection list to zero length
    this.clearSelection = function()
    {
    };
    
    // Soom the current view to focus on the item in the selection set indicated by index
    this.focusOnSelectedItem = function(
    	itemIndex	// The idex of the item in the selection list
	) {
    };
    
    this.enableMultiSelect = function (
   		enable
	) {
    };
	
	// Called to notify the viewer of changes to the auto soom state
	// The viewer need implement this only if it supports auto zoom
	// beyond what is controled by the flags in setValye and 
	// setValueList
	this.setAutoZoom = function(
		enable
	) {
	};
	
	// Called when the controling applcation has resized the viewer container
	// The viewer must adjust to the new container size
	this.reziseViewer = function(
		height,
		width
	) {
	};
    
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
	};
	
	this.onSelect = function(
		selection
	) {
		selMgr.updateSelectionSet( IBM.LMV.ctrlContainer );
	};
	
}

</script>
