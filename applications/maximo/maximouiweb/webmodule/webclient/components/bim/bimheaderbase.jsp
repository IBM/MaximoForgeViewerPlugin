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
<%@page import="org.w3c.dom.*, psdi.mbo.*, psdi.util.*, psdi.webclient.system.controller.*, psdi.webclient.system.beans.*, psdi.webclient.system.runtime.*"%>
<%@page import="psdi.webclient.servlet.*, psdi.webclient.system.session.*, psdi.webclient.controls.*, psdi.webclient.components.*"%>
<%@page import="psdi.webclient.system.dojo.Dojo"%>

<%
String id = "";
String IMAGE_PATH = "";
String CSS_PATH = "";

/*
String uiSessionId           = request.getParameter("uisessionid");
WebClientSessionManager wcsm = WebClientSessionManager.getWebClientSessionManager(session);
WebClientSession wcs         = wcsm.getWebClientSession(uiSessionId);
String servletBase           = wcs.getMaximoRequestContextURL() +  "/webclient";
*/
String servletBase           = WebClientRuntime.getMaximoRequestContextURL( request )  +  "/webclient";
String skinDir               = Dojo.getSkinsDirectory(request);

// String skin         = wcs.getSkin();
String skin = "";
String defaultAlign ="left";
String reverseAlign ="right";
boolean rtl         = false;

psdi.util.MXSession _session = psdi.webclient.system.runtime.WebClientRuntime.getMXSession(session);

/*
String langcode              = _session.getUserInfo().getLangCode();

if( langcode.equalsIgnoreCase("AR") || langcode.equalsIgnoreCase("HE") )
{
	defaultAlign = "right";
	reverseAlign = "left";
	rtl = true;
}
*/

// IMAGE_PATH = servletBase + "/"+skin+"images/"+ (rtl?"rtl/":"") + wcs.getImagePath();
// CSS_PATH   = servletBase + "/"+skin+"css/"   + (rtl?"rtl":"")  + wcs.getCssPath();
%>
