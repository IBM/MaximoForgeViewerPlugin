# Maximo BIM Forge Viewer PlugIn
## Overview
Maximo® Extensions for Building Information Models (BIM) - Autodesk Forge Viewer Plug-in provides support for utilizing the Autodesk Forge Viewer in Maximo. It is most easily utilized in conjunction with COBie data that is imported in the BIM Projects application. The Forge Viewer integration provides visualization of Building Information Model (BIM) data in the context of the Maximo Assets, Locations, and Work Order Tracking applications. In this context, it provides the following features: Forge Service Administration
- A UI for administering the Autodesk Forge service as used by Maximo including:
- Managing storage containers (Autodesk Forge Buckets)
- Uploading models to the Autodesk Forge service
- Linking multi-part models
- Translating models into viewable formats

Maximo Integration
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

## How to build
The project is setup to build in Eclipse

The Java portion of the project needs to be built against Maximo.  Either the .EAR file, or the Maximo install directory stature can be used.
### To use the EAR file
- Uncompress the .EAR file
- Unclompress maximouiweb.war
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
- The JSP files inculed several Maximo JSP files that are not part included in the project.  So the JSPs may show many build errors.  These can be ignored since JSPs are compiled at runtime.However, they can be resolved by coping the required files from the Maximo EAR or install directory into the build tree.
