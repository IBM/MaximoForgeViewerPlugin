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
<%@page import="psdi.webclient.components.*"%>
<%@page import="psdi.server.MXServer"%>
<%@ include file="../common/componentheader.jsp" %>
<%

// when in design mode return some stub html for App Designer
if(designmode) 
{
	IMAGE_PATH = servletBase + "/"+skin+"images/"+(rtl?"rtl/":"")+wcs.getImagePath();
	%>
    <div>
    <img src="<%=IMAGE_PATH%>bim//ViewerDesignerMode.png" alt="BIM 3D viewer" draggable="false"> 
    </div>
    <%
    return;
}

// Must be bound to an instance of NavisWorks to function
if( !(component instanceof BIMViewer ) )
{
	return;
}
String uiSessionId = wcs.getUISessionID();
BIMViewer bldgMdl = (BIMViewer)component;
IMAGE_PATH = servletBase + "/"+skin+"images/"+(rtl?"rtl/":"")+wcs.getImagePath();

// Designer mode may put "-" into the ID string which make them invalid for JavaScript 
// idenfiers - Get rid of them
id = id.replace( "-", "_" );

String containerTable   = id + "container";

String value   = null;

boolean _needsRendered = bldgMdl.needsRender();


if( _needsRendered )
{%>
	<script type="text/javascript" >
		var jsLibrary = document.createElement('SCRIPT' );
		jsLibrary.type = "text/javascript";
		jsLibrary.src  = "<%=servletBase%>/javascript/bimviewerlib.js";
		if( navigator.appName == "Microsoft Internet Explorer" )
		{
			try
			{
				window.top.document.appendChild( jsLibrary );			// 7.5 wants this
			}
			catch( e ) 
			{
				window.top.document.head.appendChild( jsLibrary );		// 7.6 Want this
			}
		}
		else
		{
			var headers = document.getElementsByTagName('head');
			var head = headers[0];
			head.appendChild( jsLibrary );
		}
		
		<%
		String version = MXServer.getMXServer().getMaxupgValue();
		if( version.startsWith( "V7503" ) )
		{%>
			jsLibrary = document.createElement('SCRIPT' );
			jsLibrary.type = "text/javascript";
			jsLibrary.src  = "<%=servletBase%>/javascript/menus.js";
			if( navigator.appName == "Microsoft Internet Explorer" )
			{
				window.top.document.appendChild( jsLibrary );			// 7.5 wants this
			}
			else
			{
				var headers = document.getElementsByTagName('head');
				var head = headers[0];
				head.appendChild( jsLibrary );
			}
		<%}%>
		
	<%if( bldgMdl.getAppType() ==  BIMViewer.TYPE_LOOKUP )
	{%>
		// Need to allow time for the NavisWorks control to initialize or it fails to bind
		// to its event handlers
		addLoadMethod( 'setTimeout( \'<%=bldgMdl.jspScript( id )%>\', 500 );' );
	<%}
	else
	{%>
		addLoadMethod( '<%=bldgMdl.jspScript( id )%>' );
	<%}%>
	
	</script>
	<%
} 
else
{
	if( bldgMdl.getMxVersion() >= BIMViewer.VERSION_75_OR_GREATER )
	{%>
		<component id="<%=id%>_holder"><%="<![CDATA["%>
			<script>
				setTimeout( '<%=bldgMdl.jspScript( id )%>', 10 );
			</script>
		<%="]]>"%></component>
		<%
	}
	else
	{%>
		<%=bldgMdl.jspScript( id )%>
	<%}
}  
  
if( _needsRendered )
{
	// Force a reload of the model file if the control is being redrawn
	bldgMdl.setModelListChanged( true );
	bldgMdl.setValueChanged( true );
	%>

<table id="<%=containerTable%>" name="<%=containerTable%>"  
       style="position:relative;; top:<%=bldgMdl.jspGetViewerTop()%>px; width:100%">

  <script>
	if( "<%=bldgMdl.getWidth()%>" == "100%"  )
	{
		var container = document.getElementById( "<%=containerTable%>" );
		if( container )
		{
			var parent = container.parentNode;
			while( parent != null && parent.scrollWidth == 0 )
			{
	//			alert( parent.id + " " + parent.style.width + "  Left: " + parent.scrollLeft );
				parent = parent.parentNode;
			}
			container.style.width = "" +  (parent.clientWidth - <%=bldgMdl.getLeftOffset()%>) + "px";
		}
	}
  </script>

  <tr>
    <td id=<%=id%>_frameLoc valign="top" >
	  <iframe id=<%=id%>_frame frameborder="0" 
      		  allowFullScreen webkitallowfullscreen mozallowfullscreen 
	          height="<%=bldgMdl.getHeight()%>" width="<%=bldgMdl.getWidth()%>" 
	          marginwidth="0" marginheight="0" scrolling="no"
           	  src="<%=servletBase%>/components/bim<%=bldgMdl.getViewerType()%>/viewerframe.jsp?rid=<%=id%>&id=<%=bldgMdl.getId()%>&uisessionid=<%=uiSessionId%>"
              >
	  </iframe>
    </td>
  </tr>
</table>

<%
}  // Close else if !bldgMdl.needsRender() )
%>

<%@ include file="../common/componentfooter.jsp" %>