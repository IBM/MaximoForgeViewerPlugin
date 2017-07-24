<%--
* Licensed Materials - Property of IBM
* Restricted Materials of IBM
* 
* (C) COPYRIGHT IBM CORP. 2010 All Rights Reserved.
* US Government Users Restricted Rights - Use, duplication or
* disclosure restricted by GSA ADP Schedule Contract with
* IBM Corp.
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
	
	String style     = "https://" + adHost + "/viewingservice/v1/viewers/style.css" + lmvversion;
	String viewer3D  = "https://" + adHost + "/viewingservice/v1/viewers/viewer3D.js" + lmvversion;
	String lmvworker = "https://" + adHost + "/viewingservice/v1/viewers/lmvworker.js" + lmvversion;
%>

<link rel="stylesheet" type="text/css" href="<%=style%>" />
<link rel="stylesheet" type="text/css" href="<%=CSS_PATH%>LMV.css" />
<script type = "text/javascript" 
	src="<%=viewer3D%>">
</script>
<script  type = "text/javascript" 
	src="<%=lmvworker%>">
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
