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
<%@page import="java.util.*"%>
<%@page import="org.w3c.dom.*, psdi.mbo.*, psdi.util.*, psdi.webclient.system.controller.*, psdi.webclient.system.beans.*, psdi.webclient.system.runtime.*"%>
<%@page import="psdi.webclient.servlet.*, psdi.webclient.system.session.*, psdi.webclient.controls.*, psdi.webclient.components.*, psdi.server.*"%>
<%@page import="psdi.webclient.system.dojo.Dojo"%>

<%!
private static class Labels
{
	private static final String group = "login";
	
	final String welcome;
	final String welcomeToMaximo;
	final String enterInfo;
	final String username;
	final String password;
	final String loginButton;
	final String selectLanguage;
	final String onLanguageSelection;
	final String forgotPassword;
	final String newUserLabel;
	final String newUserLink;
	final String mobileLoginLink;
	final String standardLoginLink;
	final String adminLoginLink;
	final String copyright;
	final String tenantId;
	final String federalNotice;
	final String nonFederalNotice;

	Labels(String langCode) throws MXException, RemoteException
	{
		MXServer server = MXServer.getMXServer();

		welcome = server.getMessage(group, "welcome", langCode);
		welcomeToMaximo = server.getMessage(group, "welcomemaximomessage", langCode);
		enterInfo = server.getMessage(group, "enterinfo", langCode);
		username = server.getMessage(group, "username", langCode);
		password = server.getMessage(group, "password", langCode);
		loginButton = server.getMessage(group, "loginbutton", langCode);
		selectLanguage = server.getMessage(group, "languages", langCode);
		onLanguageSelection = server.getMessage(group, "onlanguageselection", langCode);
		forgotPassword = server.getMessage(group, "forgotpassword", langCode);
		newUserLabel = server.getMessage(group, "newuserlabel", langCode);
		newUserLink = server.getMessage(group, "newuserlink", langCode);
		mobileLoginLink = server.getMessage(group, "mobileloginlink", langCode);
		standardLoginLink= server.getMessage(group, "standardloginlink", langCode);
		copyright = server.getMessage(group, "copyright", langCode);
		tenantId  = server.getMessage(group,"tenant",langCode);
		federalNotice = server.getMessage(group, "federalNotice", langCode);
		nonFederalNotice = server.getMessage(group, "nonFederalNotice", langCode);
		adminLoginLink = server.getMessage(group, "adminLogin", langCode);
	}
}
%>

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
String skin                  = WebClientRuntime.getWebClientSystemProperty("mxe.webclient.skin");

// String skin         = wcs.getSkin();
String defaultAlign ="left";
String reverseAlign ="right";
boolean rtl         = false;

psdi.util.MXSession _session = psdi.webclient.system.runtime.WebClientRuntime.getMXSession(session);



// String langcode              = _session.getUserInfo().getLangCode();

Object[] settings = WebClientRuntime.getLocaleFromRequest(request);
String langcode;
if (settings[0] instanceof String)
{
	langcode = (String)settings[0];
}
else
{
	langcode = MXServer.getMXServer().getBaseLang();
}
Map<String,String> langNameToCodeMap = null;
String[][] langs = _session.getLanguageList();
if(langs != null)
{
	if(langs.length == 1)
	{
		langcode = langs[0][0];
	}
	else if(langs.length > 1)
	{
		langNameToCodeMap = new HashMap<String,String>();
		for(int i = 0; i < langs.length; i++)
		{
			langNameToCodeMap.put(langs[i][1], langs[i][0]);
		}
	}
}

Labels labels = new Labels(langcode);

String direction = "ltr";
if(BidiUtils.isGUIMirrored(langcode))
{
	defaultAlign = "right";
	reverseAlign = "left";
	rtl = true;
	direction = "rtl";
}

// IMAGE_PATH = servletBase + "/"+skin+"images/"+ (rtl?"rtl/":"") + wcs.getImagePath();
// CSS_PATH   = servletBase + "/"+skin+"css/"   + (rtl?"rtl":"")  + wcs.getCssPath();
IMAGE_PATH = servletBase + "/" + skinDir + "/" + skin + "/" + "images/" + (rtl?"rtl/":"");
CSS_PATH   = servletBase + "/" + skinDir + "/" + skin + "/" + "css/"    + (rtl?"rtl/":"");


	int branding = _session.getBranding();
	if(branding == MXServerRemote.BRAND_NONE)
	{
		branding = MXServerRemote.BRAND_MAXIMO_AND_TIVOLI;
	}
	
	boolean formAuth = request.getParameter("appservauth") != null;
	String isSaasEnabled = WebClientRuntime.getWebClientSystemProperty("mxe.isSaasEnabled", "0");
	String isFederal = WebClientRuntime.getWebClientSystemProperty("mxe.isFederal", "0");
	
	boolean isMobile      = false;
	boolean everyplace    = false;
	boolean mtEnabled     = false;

	String message;
	MXException loginException = (MXException)session.getAttribute("loginexception");
	if (loginException != null)
	{
		//Issue 10-12120
		message = _session.getTaggedMessage(loginException);
		message = WebClientRuntime.replaceString(message,"\"","\\\"");
		session.removeAttribute("loginexception");
	}
	else
	{
		message = (String)request.getAttribute("signoutmessage");
		if (message == null)
		{
			message = (String)session.getAttribute("signoutmessage");
			if (message != null)
			{
				session.removeAttribute("signoutmessage");
			}
		}
		else
		{
			request.removeAttribute("signoutmessage");
		}
	}
	if (message != null)
	{
		message = WebClientRuntime.removeQuotes(message);
		message = message.replace("\\n"," ");
	}
	
	String userFieldName = "username";
	String passwordFieldName = "password";
	String tenantFieldName = "tenant";
	boolean tokenExpire = false;
	
	String url = WebClientRuntime.getMaximoRequestContextURL(request)+"/ui/login";
	if(WebAppEnv.useAppServerSecurity())
	{
		userFieldName = "j_" + userFieldName;
		passwordFieldName = "j_" + passwordFieldName;
		url="../../j_security_check";

		if ( _session.isConnected()) // This is possible cause for Appserver token expiry
		{
			tokenExpire = true;
		}
	}

	String userName = request.getParameter("username");
	if(userName == null)
	{
		userName = "";
	}

	
	String tenant = request.getParameter(tenantFieldName);
	if(tenant == null)
	{
		tenant = "";
	}

%>
