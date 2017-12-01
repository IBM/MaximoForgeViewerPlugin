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
<%@page import="psdi.server.MXServer"%>
<%
	MXServer server    = MXServer.getMXServer();
	String adHost      = server.getProperty( "bim.viewer.LMV.host" );
	String lmvversion  = server.getProperty( "bim.viewer.LMV.viewer.version" );
	if( lmvversion == null ) lmvversion = "";
	// ?v=v1.2.15
	String lmvtheme    = server.getProperty( "bim.viewer.LMV.theme" );
	if( lmvtheme == null ) lmvtheme = "8"; 
	
	String three     = "https://" + adHost + "/viewingservice/v1/viewers/three.min.js" + lmvversion;
	String style     = "https://" + adHost + "/viewingservice/v1/viewers/style.css" + lmvversion;
	String viewer3D  = "https://" + adHost + "/viewingservice/v1/viewers/viewer3D.js" + lmvversion;
	String lmvworker = "https://" + adHost + "/viewingservice/v1/viewers/lmvworker.js" + lmvversion;
%>

<link rel="stylesheet" type="text/css" href="<%=style%>" />
<link rel="stylesheet" type="text/css" href="<%=CSS_PATH%>LMV.css" />
<script type = "text/javascript" 
	src="<%=three%>">
</script>
<script type = "text/javascript" 
	src="<%=viewer3D%>">
</script>
<!--
<script  type = "text/javascript" 
	src="<%=lmvworker%>">
</script>
-->
<script type = "text/javascript" 
	src  = "<%=servletBase%>/javascript/gunzip.min.js" >
</script>
<script type = "text/javascript" 
    src  = "<%=servletBase%>/javascript/Forge.js">
</script>
<script type = "text/javascript" 
    src  = "<%=servletBase%>/javascript/LMV.js">
</script>
<script type = "text/javascript" 
    src  = "<%=servletBase%>/javascript/LMV_Markup.js">
</script>
