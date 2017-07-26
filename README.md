# Maximo BIM Forge Viewer PlugIn
## Overview
Maximo® Extensions for Building Information Models (BIM) - Autodesk Forge Viewer Plug-in provides support for utilizing the Autodesk Forge Viewer in Maximo. It is most easily utilized in conjunction with COBie data that is imported in the BIM Projects application. The Forge Viewer integration provides visualization of Building Information Model (BIM) data in the context of the Maximo Assets, Locations, and Work Order Tracking applications. In this context, it provides the following features: 

### Forge Service Administration
- A UI for administering the Autodesk Forge service as used by Maximo including:
- Managing storage containers (Autodesk Forge Buckets)
- Uploading models to the Autodesk Forge service
- Linking multi-part models
- Translating models into viewable formats

### Maximo Integration
- Model file management - the viewer automatically displays the correct model file(s) for a selected Maximo location or asset. If there are multiple models available, a list is provided, and most of the context is maintained when switching between models.
- Viewer context is synchronized to Maximo (locations and assets) - selecting a record in Maximo selects the corresponding item in the Viewer which zooms and centers the 3D model on that item.
- Maximo context is synchronized to the Viewer (locations and assets) - selecting an item in the Viewer causes the corresponding record in Maximo to become the current Maximo record
- The Viewer can be used as an asset selection lookup anywhere in Maximo where an asset look-up menu is available.
- The Viewer can be used to select a set of assets to add to a service request or work order.
- You can create service requests and work orders directly in the Viewer
- You can search a facility for open work orders, preventative maintenance work, and service requests, and display all or a selected set of the search results as the selection set in the Viewer.
- You can display members of Maximo systems in the Viewer - search from systems and zones either that are either defined for the facility, or for which the selected item is a member. You can display all members of the system as the current selection or drill-down to any member of the system and select it in the Viewer.
- You can create and edit Maximo systems from the Viewer - the selection set in the Viewer can be used to either create a new system or be added as a sub-tree to an existing system.
- You can navigate through a multi-item selection set changing both the model view (zoom and center), and the current Maximo record to the current item in the set.
- You can markup a view in the Viewer, save the markup with a Work Order, and then display the markup any time the work order is viewed.

Forge Viewer features that are exposed in the Viewer toolbar include: Full 3D navigation
- Basic search
- Model properties
- Model tree
- Sectioning of a model
- Model walk through
- Save and restore views

![](https://github.com/IBM/MaximoForgeViewerPlugin/blob/master/images/viewer1.jpg "Model viewer embedded into the Maximo Locations application")

In addition, the 3D model can be used in conjunction with the normal Maximo lookup mechanism to provide direct visual selection of assets from a building model.

![](https://github.com/IBM/MaximoForgeViewerPlugin/blob/master/images/viewer1.jpg "The Model viewer for work order dispatch")

## Target audience
There are many possible audiences for this project if you want to:

### Just use the viewer plug-in
Running the make_package.cmd script creates the plugin package for install into Maximo.  The pre-built package is also available [here](https://www.ibm.com/developerworks/community/wikis/home?lang=en#!/wiki/IBM%20Maximo%20Asset%20Management/page/Maximo%20BIM%20Forge%20Viewer%20PlugIn)

### Extend the Forge viewer implementation

The JavaScript files that wrap the Forge viewer API are [here](https://github.com/IBM/MaximoForgeViewerPlugin/tree/master/applications/maximo/maximouiweb/webmodule/webclient/javascript) These can be extended to add viewer features.  Possible accessible via one of the toolbars or the context menu.  This can be done with little or no knowledge of Maximo.

The Maximo integration may be extended, however this requires a good working knowledge of Maximo development

### Create a plug-in for your own viewer

The code in this project can be cloned to start a new viewer plug-in
The full viewer API is documented [here](https://github.com/IBM/MaximoForgeViewerPlugin/blob/master/Doc/Maximo%20BIM%20-%20Viewer%20Integration%20Framework.pdf).  A functioning viewer implementation can be created with a mimimal of Maximo knowledge by cloning the JSP file [here](https://github.com/IBM/MaximoForgeViewerPlugin/tree/master/applications/maximo/maximouiweb/webmodule/webclient/components/bimlmv) or those in the bimvendor directory of your Maximo install.  

Richer integrations that display additional Maximo data or store viewer data in the Maximo database can be accomplished by using the appropiate Forge viewer feature as a model.

These aspect may be of particular interest:
- The implementation of saved views and markup which uses the Maximo database and Maximo Integration Framework (MIF) REST calls to store view specific data in the Maximo database, and provides a Maximo UI to manage the data.
- BIMField, and the asset and location property sheets in the Forge viewer.  these use the MIF to directly display Maximo data within the viewer

## How to build
The project is setup to build in Eclipse

The Java portion of the project needs to be built against Maximo.  Either the .EAR file, or the Maximo install directory structure can be used.
### To use the EAR file
- Uncompress the .EAR file
- Uncompress maximouiweb.war
- Add businessobjects.jar to the build path
- Add maximouiweb/WEB-INF-classes to the build path
- The tools directory is not included in the EAR and can't be built using this method

### To use the Maximo install directory
This is the directory structure laid down by the Maximo install and used to build and update the Maximo EAR file.  Depending on how Maximo was installed, it may not be on the Maximo server.
- Add <InstallRoot>/applications/maximo/businessobjects/classes to the build path
- Add <InstallRoot>/applications/maximo/maximouiweb/webmodule/WEB-INF/classes
- Add <InstallRoot>/tools/maximo/classes

### Notes
- Set the compiler compliance level to Java 1.7
- It should not be necessary to build the files in the tools directory.  The class files have been checked in and changes here are very unusual
- The JSP files incule several Maximo JSP files that are not part included in the project.  So the JSPs may show many build errors.  These can be ignored since JSPs are compiled at runtime. However, they can be resolved by coping the required files from the Maximo EAR or install directory from <InstallRoot>/applications/maximo/maximouiweb/webmodule/webclient/components into the build tree.

## What's included

### Forge viewer Plug-in:
This implements the Maximo viewer API for the Forge viewer.  It consists of JavaScritp and JSP files.

It also include 2 Maximo Business Objects (Mbos) for storing saved views and markup.  As well as a simple dialog added to the "Manage BIM Viewer" applcation to manage saved views

### Forge Service Java wrapper:
This is a set of Java classes that wrap the Forge REST service to make it easy to use from within a Java application.  This includes result classes that parse the JSON returned by many of the Forge APIs into easily consumable Java Data Objects.  It also include simple classes with main() methods, and Windows .cmd files to enable it to be used as a CLI interface to Forge.

### Forge Model Administration application:
This is s set of Maximo Business Objects (Mbos), and Maximo UI the provides an administrative UI to:
- Manage Forge service buckets
- Upload and manage files in the Forge Service
- Request translation of model files into viewable derivatives, and manage the derivatives.
The UI extends the Maximo "Manage BIM Viewer" application.  

The Mbos are hybrid objects storing some data in the Maximo database and retraining some data from the Forge service.

When Forges service are accessed from within Maximo, Maximo site security can be applied to Forge objects.

### BIMField application:
A stand-alone Web application that is map and viewer centric.  It is intended for use on connected mobile devices

### Sample Models:
Two sample models including Revit files and COBie data

### Test viewer
A very simple blank viewer implementation is included.  It has no viewer function, but enables the 3D view tabs to display in the Maximo UI.

### Product applcation UI
int the past, the BIM extensions have incldued a product applcation for that implements a simple product catalog and is populated from the COBie Type table.  The applcation was not officially included in Maximo when the BIM extensions moved to Maximo core.  However the database tables and code for the applcation is pressent.  Only the UI is missing.  THe UI is included in this package.

## Project Structure
The project structure follows the Maximo code and deployment structure. This section assumes knowledge of Maximo development

### Maximo Business Objects (Mbos)
- LMV Maximo service implementation
- Forge Service wrapper (and CLI).  Used by the LMV serive
- Mbo implementations for the Forge service UI.  These access the Forge Service through the LMV Service. They are somewhat complicated as they merge data from the Maximo database with date retrieved live from the Forge service
- Mbo implementations for saved views and markup (work views).  These are very simple objects and are a good place to start for extensions that want to utilize the Maximo database.

### DataBeans
The databeans that implement the Forge service Management UI.  Most of these are somewhat complicated becasue updates to the Forge service can't be wrapped into Maximo database transactions so all changes are commited immeditatly.

### UI Control extensions
The entire viewer is presented to Maximo as a UI control similare to a textbox.  The xml files that define the control interface are included [here](https://github.com/IBM/MaximoForgeViewerPlugin/tree/master/applications/maximo/properties/registry-extensions) for reference. It should not be necessary to modify these, and they should not be redistributed.

The Java class that implements the viewer control is [here](https://github.com/IBM/MaximoForgeViewerPlugin/tree/master/applications/maximo/maximouiweb/src/psdi/webclient/components).  This is mostly included for reference so the mechanism for the viewer communicating with Maximo can be understood and the communication can be followed in a debugger.  However, this could be extended to enhance the viewer API function.  For Example: to add a call to set a list of IDs to s specified color.  Redistributing this should be done with care as it will overwrite the version included with Maximo.

### Product definition
Maximo uses a product definitions file to define the current product version and name, and to control which scripts are run by maxinst or updatedb.  The bimlmv product definition is found [here](https://github.com/IBM/MaximoForgeViewerPlugin/tree/master/applications/maximo/properties/product). New viewer plug-in which require database, UI, or message catalog scripts should copy and rename this file.  If scripts are added to the Forge viewer plug-in, this file must be updated for those scripts to run.

### Database scripts (.dbc files)
Maximo use XML scripts to define and update database tables.  The scripts for the Forge Viewer Plug-in can be found [here](https://github.com/IBM/MaximoForgeViewerPlugin/tree/master/tools/maximo/en/bimlmv). Existing scripts are never changed.  All updates are made by adding new scripts.

### Presntation files and scripts (.mxs files)
The Maximo presentation update process uses XML files that describe the changes that must be applied to the existing UI to update it to a new version or add a new feature.  These scripts are tolerant of most UI customization.  The scripts are in the same directory as the database scripts and run the same way.

There is a tool included in the Maximo expanded EAR for automatically generating the UI update file by comparing the current and new presentation XML files.  It is located at <Maximo Install Dir>/tools/maximo/internal/mxdiff

### Message catalogs (.msg files)
Maximo uses a database based message catalog which support internationalization and translation.  Extensions make additions to the message catalog through an XML file that is located in the same directory as the database scripts and run the same way.  In normal Maximo development these files are generated from and IBM internal tool.  However, the files included with the project can be copied and hand edited to create new message catalog scripts.
