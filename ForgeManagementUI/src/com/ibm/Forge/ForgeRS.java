/**
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
**/
package com.ibm.Forge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.ResultAuthentication;
import psdi.app.bim.viewer.dataapi.ResultBucketList;

@javax.ws.rs.ApplicationPath("resource")
@Path("/proxy")

public class   ForgeRS 
       extends javax.ws.rs.core.Application
{
    @Context
    private UriInfo context;
    
    //===========================================================================================
    //  Authenitcate
    //===========================================================================================
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("token")
    public Response authenticate(
    	@Context HttpServletRequest request,
    	@QueryParam("key") String key,
		@QueryParam("secret") String secret
	) 
		throws Exception 
    {
    	HttpSession session = request.getSession( true );
    	APIImpl impl = new APIImpl();
    	session.setAttribute( "forge-service-impl", impl );
    	
    	impl.setKey( key );
    	impl.setSecret( secret );
    	
    	if( key != null && key.length() > 0 )
    	{
        	impl.setKey( key );
    	}
    	if( secret != null && secret.length() > 0 )
    	{
        	impl.setSecret( secret );
    	}

		String token = getToken( impl );
    	 
        return Response.status( Response.Status.OK )
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")                
                .entity( token )
                .build();     
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("auth/token")
    public Response getAuthToken(
    	@Context HttpServletRequest request
	) 
		throws Exception 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	
		String token = getToken( impl );
    	 
        return Response.status( Response.Status.OK )
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")                
                .entity( token )
                .build();     
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("auth/key")
    public Response getAppKey(
    	@Context HttpServletRequest request
    ) 
		throws Exception 
    {
    	APIImpl impl = null;
    	HttpSession session = request.getSession();
    	Object o = session.getAttribute( "forge-service-impl" );
    	if( o != null && o instanceof APIImpl )
    	{
    		impl = (APIImpl)o;
    	}
    	if( impl == null )
    	{
			String json = "{ \"key\" : \"\", \"authenticated\" : \"false\" }";
            return Response.status( Response.Status.OK )
                    .header("Pragma", "no-cache")
                    .header("Cache-Control", "no-cache")                
                    .entity( json )
                    .build();     
    	}
    	
		String hasToken = impl.hasAuthToken() ? "true" : "false";
    	String json = "{ \"key\" : \"" + impl.lookupKey() + "\", \"authenticated\" : \"" + hasToken + "\" }";
    	 
        return Response.status( Response.Status.OK )
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")                
                .entity( json )
                .build();     
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("auth/logout")
    public Response doAppLogout(
    	@Context HttpServletRequest request
	) 
		throws Exception 
    {
    	HttpSession session = request.getSession( false );
    	if( session != null )
    	{
        	session.setAttribute( "forge-service-impl", null );
        	session.invalidate();
    	}
    	 
        return Response.status( Response.Status.OK )
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")                
                .build();     
    }

    //===========================================================================================
    //  Bucket
    //===========================================================================================

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bucket")
    public Response bucketList(
     	@Context HttpServletRequest request,
     	@QueryParam("region") String region
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}

    	ResultBucketList result = impl.bucketList( region );
    	
    	return formatReturn( result );
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bucket/{bucketKey}")
    public Response bucketDetails(
     	@Context HttpServletRequest request,
     	@PathParam("bucketKey") String bucketKey
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	Result result = impl.bucketQueryDetails( bucketKey );
    	return formatReturn( result );
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bucket/{bucketKey}")
    public Response bucketCreate(
     	@Context HttpServletRequest request,
		@PathParam("bucketKey") String bucketKey,
		@QueryParam("policy") String policy,
		@QueryParam("region") String region
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	Result result = impl.bucketCreate( bucketKey, policy, region );
    	return formatReturn( result );
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bucket/{bucketKey}")
    public Response bucketDelete(
     	@Context HttpServletRequest request,
     	@PathParam("bucketKey") String bucketKey
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	Result result = impl.bucketDelete( bucketKey );
    	return formatReturn( result );
    }
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bucket/{bucketKey}/rights/{serviceId}")
    public Response bucketGrant(
     	@Context HttpServletRequest request,
    	@PathParam("bucketKey") String bucketKey,
    	@PathParam("serviceId") String serviceId,
    	@QueryParam("access")   String access
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	Result result = impl.bucketGrantRightsV2( bucketKey, serviceId, access );
    	return formatReturn( result );
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bucket/{bucketKey}/rights/{serviceId}")
    public Response bucketRevoke(
     	@Context HttpServletRequest request,
    	@PathParam("bucketKey") String bucketKey,
    	@PathParam("serviceId") String serviceId
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	Result result = impl.bucketRevokeRightsV2( bucketKey, serviceId );
    	return formatReturn( result );
    }

    //===========================================================================================
    //  Model
    //===========================================================================================

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("model/{bucketKey}")
    public Response modelList(
     	@Context HttpServletRequest request,
    	@PathParam("bucketKey") String bucketKey,
    	@QueryParam("name") String name,
    	@QueryParam("start") String start,
    	@QueryParam("pagesize") String pagesize
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	Result result = impl.objectListPaged( bucketKey, name, start, pagesize );
    	return formatReturn( result );
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("model/{bucketKey}/{objectKey}")
    public Response modeDetails(
     	@Context HttpServletRequest request,
    	@PathParam("bucketKey") String bucketKey,
    	@PathParam("objectKey") String objectKey
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	Result result = impl.objectQueryDetails( bucketKey, objectKey );
    	return formatReturn( result );
    }
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("model/{bucketKey}/{objectKey}")
    public Response modelUpload(
     	@Context HttpServletRequest request,
    	@PathParam("bucketKey") String bucketKey,
    	@PathParam("objectKey") String objectKey,
    	@Context HttpHeaders headers,
     	InputStream is
	) 
		throws IOException, 
		       URISyntaxException,
		       GeneralSecurityException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	
    	List<String> value = headers.getRequestHeader( HttpHeaders.CONTENT_LENGTH );
    	String data = value.get( 0 );
    	long length = Long.parseLong( data );

    	Result result = impl.objectUploadStream( bucketKey, objectKey, is, length, null );
    	return formatReturn( result );
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("model/{bucketKey}/{objectKey}")
    public Response modelDelete(
     	@Context HttpServletRequest request,
    	@PathParam("bucketKey") String bucketKey,
    	@PathParam("objectKey") String objectKey
	) 
		throws IOException, 
		       URISyntaxException,
		       GeneralSecurityException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	Result result = impl.objectDelete( bucketKey, objectKey );
    	return formatReturn( result );
    }

    //===========================================================================================
    //  Bubble
    //===========================================================================================

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bubble")
    @Consumes("*/*") 
    public Response bubbleDetails(
    	@Context HttpServletRequest request,
    	InputStream is
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	String urn = stream2string( is );
    	Result result = impl.viewableQuery( urn );
    	return formatReturn( result );
    }
    
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bubble")
    @Consumes("*/*") 
    public Response bubbleDelete(
     	@Context HttpServletRequest request,
     	InputStream is
	) 
		throws IOException, 
		       URISyntaxException,
		       GeneralSecurityException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	String urn = stream2string( is );
    	Result result = impl.viewableDeregister( urn );
    	return formatReturn( result );
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bubble/translate")
    @Consumes("*/*") 
    public Response bubbleTranslate(
    	@Context HttpServletRequest request,
	   	@QueryParam("rootFileName") String rootFileName,
		@QueryParam("region") String region,
    	InputStream is
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	
    	boolean compressedURN = false;
    	if( rootFileName != null && rootFileName.length() > 0 )
    	{
    		compressedURN = true;
    	}
    	
    	String urn = stream2string( is );
    	Result result = impl.viewableRegister( urn, region, compressedURN, rootFileName, false, true );
    	return formatReturn( result );
    }
    

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("bubble/{urn}/thumbnail")
    public Response bubbleThumbnail(
    	@Context HttpServletRequest request,
    	@PathParam("urn") String urn 
	) 
		throws IOException, 
		       URISyntaxException 
    {
    	APIImpl impl = getAPIImpl( request );
    	if( impl == null )
    	{
            return Response.status( Response.Status.UNAUTHORIZED ).build();     
    	}
    	
    	String scope[] = { DataRESTAPI.SCOPE_VIEWABLE_READ };
    	ResultAuthentication result = impl.authenticate( scope );
    	
    	if( result.isError() )
    	{
        	return formatReturn( result );
    	}
    	
		URL url = new URL( impl.getThumbnailURL( urn ) );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod( "GET" );
		result.setAuthHeader( connection );
		
        int httpStatus = connection.getResponseCode();
        if( httpStatus > 299 )
        {
	        return Response.status( httpStatus )
	                .header("Pragma", "no-cache")
	                .header("Cache-Control", "no-cache")                
	                .entity( connection.getErrorStream() )
	                .build();     
        }

        // Copy Autodesk headers describing thumbnail
        Map<String, List<String>> headers = connection.getHeaderFields();
        Iterator<String> itr = headers.keySet().iterator();
        Response.ResponseBuilder builder = Response.status( httpStatus );
        while( itr.hasNext() )
        {
        	String key = itr.next();
        	if( key == null ) continue;
        	if( !key.startsWith( "x-ads" )) continue;
        	String value = connection.getHeaderField( key );
        	builder.header( key, value );                
        }
        
        return builder.entity( connection.getInputStream() )
                .header("content-encoding", connection.getContentEncoding() )      
                .header("content-type", connection.getContentType() )      
                .build();     
    }
    
    //===========================================================================================
    //  Helpers
    //===========================================================================================

    protected String getToken(
    	APIImpl impl
    ) 
		throws IOException, 
		       URISyntaxException
	{
		String scope[] = { DataRESTAPI.SCOPE_VIEWABLE_READ};
		ResultAuthentication result = impl.authenticate( scope );
		return result.getAuthTokenJSOM();
	}
    
    protected APIImpl getAPIImpl(
    	HttpServletRequest request
	) {
    	HttpSession session = request.getSession( false );
    	if( session == null ) return null;
    		
    	Object o = session.getAttribute( "forge-service-impl" );
    	if( o != null && o instanceof APIImpl )
    	{
    		return (APIImpl)o;
    	}
    	APIImpl impl = new APIImpl();
    	session.setAttribute( "forge-service-impl", impl );
    	return impl;
    }

    private Response formatReturn(
    	Result result
	) {
    	if( result.isError() )
		{
    		if( result.getHttpStatus() > 299 )
    		{
				if( result.getErrorCode().startsWith( "AUTH" ))
				{
	    	        return Response.status( Response.Status.UNAUTHORIZED )
	    	                .header("Pragma", "no-cache")
	    	                .header("Cache-Control", "no-cache")                
	    	                .entity( result.getRawError() )
	    	                .build();     
				}
    	        return Response.status( result.getHttpStatus() )
    	                .header("Pragma", "no-cache")
    	                .header("Cache-Control", "no-cache")                
    	                .entity( result.getRawError() )
    	                .build();     
    		}
	        return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
	                .header("Pragma", "no-cache")
	                .header("Cache-Control", "no-cache")                
	                .entity( result.getRawError() )
	                .build();     
		}
    	
        return Response.status( Response.Status.OK )
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")                
                .entity( result.getRawData() )
                .build();     
    }

    protected String stream2string(
    	InputStream is
	) 
		throws IOException 
	{
        if (is == null)
        {
            return "";
        }
            
        try
        {
            BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
            String output;
            StringBuffer buf = new StringBuffer();
                
            while( (output = br.readLine()) != null )
            {
                buf.append( output );
            }
            return buf.toString();
        }
        finally
        {
       	 is.close();
        }
    }
 }