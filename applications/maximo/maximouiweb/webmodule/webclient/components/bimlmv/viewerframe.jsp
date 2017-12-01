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
<%@ include file="../bim/bimheader.jsp" %>
    
<html>
<head>
    <link rel="stylesheet" href="<%=CSS_PATH%>/maximo.css">
    <script type = "text/javascript" 
            src  = "<%=servletBase%>/javascript/bimviewer.js">
    </script>
    <%@ include file="header.jsp" %>

    <style>
        .toolbar UL
        {
            white-space: nowrap; 
            height:30px;
        }
        .toolbar LI
        {
            white-space : nowrap;
            list-style  : none;
             display     : inline-block;
             float       : none;
         }
        .toolbar LI A.on:focus,
        .toolbar LI A.on:hover,	
        .toolbar LI A.onhover {
            background-image:url(<%=IMAGE_PATH%>toolbar/tb_button_hover.gif);
        }
    </style>
</head>  
<body oncontextmenu="return false;" style="width:100%; height:100%;">

<%@ include file="scripts.jsp" %>
<%@ include file="../bim/script-common.jsp" %>
	
<%--  Message table   --%>	
<table id=<%=msgTable%> bgcolor="<%=background%>" 
	   style="position:absolute;visibility:visible;color:<%=foreground%>; width:100%; height:100%">
    <tr> 
     <td style="height:<%=toolbar_height%>; background-color: #dfdfdf; background-size: <%=toolbar_height%>; background-image:<%=TOOLBAR_IMG%>">
        <table id=<%=msgToolbarId%> style="width:100%">
	      <tr>
	        <td align="left" >
		      <table bgcolor="transparent" style="width: 100%">
		        <tr>
                  <td><img id=<%=id%> src="<%=BIM_IMAGE_PATH%>/logo.png" height="<%=toolbar_height%>" border="0" /></td>
			      <td style="padding:4"></td> 
			      <%if( appType ==  BIMViewer.TYPE_LOCATION )
			      {%>
				    <td align="left">
					  <img id=<%=id%> name=btnModel title="<%=strings.loadModel%>" 
						   src="<%=BIM_IMAGE_PATH%>/tb_viewModel.png" height="30" width="30" border="0"
			               onClick="window.parent.sendEvent(  'bim_addmod', '<%=bldgMdl.getRenderId()%>' )" >
			        </td>
				  <%}%>  
		          <td style="width:100%"></td> 
		          <td style="padding:4"></td> 
                </tr>
              </table>
		    </td>

            <td align="right">
              <table><tr>
                <td style="padding:2px"></td> 
                <td> 
				    <ul class="toolbar">
					    <li>
                  <%if( appType !=  BIMViewer.TYPE_LOOKUP ) 
                  {%>
						    <a id="btnRestore" title="<%=strings.resizeBtn%>" onClick="resizeBtn()" href="javascript:void(null);" class="on"
						    	onfocus="setCurrentfocusId(event, this);appendClass(this,'onhover')" onBlur="removeClass(this,'onhover')">
						    	<img id="btnRestorei" src="<%=BIM_IMAGE_PATH%>/tb_resize.png" name="btnRestorei" 
						    	alt="<%=strings.resizeBtn%>" tabindex="-1" draggable="false">
						    </a>

                  <%} else {%>  
						    <a id="btnDlgResize" title="<%=strings.resizeBtn%>" onClick="resizeBtn()" href="javascript:void(null);" class="on"
						    	onfocus="setCurrentfocusId(event, this);appendClass(this,'onhover')" onBlur="removeClass(this,'onhover')">
						    	<img id="btnDlgResizei" src="<%=BIM_IMAGE_PATH%>/tb_resize.png" name="btnDlgResizei" 
						    	alt="<%=strings.resizeBtn%>" tabindex="-1" draggable="false">
						    </a>
					
                  <%}%>
					    </li>
				    </ul>
                </td>
                <td style="padding:2px"></td> 
              </tr></table>
            </td> 
		  </tr>
        </table>
      </td>
    </tr>

    <tr bgColor="#FFFFFF" style="height:1"><td></td></tr>

    <tr><td id=<%=msgCell%> style="width:100%;height:100%">
      <em><%=strings.msgNoModel%></em>
    </td></tr>

    <tr bgColor="#FFFFFF" style="height:1;"><td></td></tr>
   	
   	<tr style="background-image:<%=TOOLBAR_IMG%>; background-color: #dfdfdf; background-size: <%=toolbar_height%>; ">
		<td  valign="bottom" style="height:<%=toolbar_height%>"></td>
	</tr>
    <tr bgcolor="<%=background%>" valign="bottom" style="color:<%=foreground%>;width:100%;">
      <td style="height:1">
        <input type=text readonly style="background-color:<%=background%>;color:<%=foreground%>;width:100%">
      </td>
    </tr>
  </table>

<%--  Model table   --%>	
<table id=<%=modelTable%> bgcolor="<%=background%>" cellpadding="0" cellspacing="0"
	       style="visibility:hidden;color:<%=foreground%>; width:100%; height:100%">
    <tr bgColor="#FFFFFF"><td style="height:1;  width:100%"></td></tr>
    <tr>
	   <td id=<%=id%>_Parent  name="<%=id%>_Parent" style="width:100%; height: 100%;">
			<script id=<%=id%>_container type="text/javascript" >
                loadControl( "<%=id%>_Parent", "<%=id%>_version" );
            </script>
       </td>
    </tr>
    <tr bgColor="#FFFFFF" valign="bottom"><td style="height:1;  width:100%">
    
	<%@ include file="toolbar.jsp" %>

    <tr bgcolor="<%=background%>" style="color:<%=foreground%>;width:100%">
      <td  valign="bottom" >
        <input type=text id="<%=statusId%>" name="<%=statusId%>" readonly 
               style="background-color:<%=background%>;color:<%=foreground%>;width:100%">
      </td>
    </tr>
  </table>  <%-- Close control table --%>

  <iframe id=<%=id%>_selectSize  frameborder="3"
          style="position:absolute;borderColor:#888888;background:#FFFFFF;visibility:hidden;z-index:20000"
          marginwidth="0" marginheight="0" scrolling="no"
		  src="<%=servletBase%>/components/bim/bimresizepopup.html">
  </iframe>
  
  <%@ include file="footer.jsp" %>
  
</body>
</html>

<%@ include file="../bim/bimfooter.jsp" %>