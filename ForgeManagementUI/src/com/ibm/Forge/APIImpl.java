package com.ibm.Forge;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import psdi.app.bim.viewer.dataapi.DataRESTAPI;

public class APIImpl
    extends DataRESTAPI
{

	public final static String PROP_KEY          = "bim.viewer.LMV.key";
	public final static String PROP_SECRET       = "bim.viewer.LMV.secret";
	public final static String PROP_HOST         = "bim.viewer.LMV.host";
	public final static String PROP_API_VER      = "bim.viewer.LMV.api.version";
	public final static String PROP_API_VER_AUTH = "bim.viewer.LMV.api.version.auth";
	public final static String PROP_VIEWER_VER   = "bim.viewer.LMV.viewer.version";

	private String _host   = "developer.api.autodesk.com";
	private String _key    = "";
	private String _secret = "";


	public APIImpl()
	{
		super();

		InputStream is = null;
	    is = getClass().getClassLoader().getResourceAsStream( "com/ibm/IoT/consumer/simulator/lmv.properties" );
		if( is != null )
		{
			Properties properties = new Properties();
			try
            {
	            properties.load(  is  );
            }
            catch( IOException e )
            {
	            e.printStackTrace();
	            return;
            }
			String prop = properties.getProperty( PROP_HOST );
			if( prop != null )
			{
				_host = prop.trim();
			}
			prop = properties.getProperty( PROP_KEY );
			if( prop != null )
			{
				_key = prop.trim();
			}
			prop = properties.getProperty( PROP_SECRET );
			if( prop != null )
			{
				_secret = prop.trim();
			}
			try 
			{
				is.close();
			} 
			catch( IOException e ) 
			{ /* Ignore */	}
		}
	}

	public String getThumbnailURL(
		String urn
	) 
		throws URISyntaxException 
	{
		String params[] = { urn };
		String frag = makeURN( API_VIEWING, PATT_VIEW_THUMBNAIL, params );
		URI uri = new URI( "https", null, lookupHostname(), -1, frag, null, null );
		return uri.toASCIIString();
	}

	@Override
	public String lookupHostname()
	{
		return _host;
	}

	@Override
	public String lookupKey()
	{
		return _key;
	}

	@Override
	public String lookkupSecret()
	{
		return _secret;
	}
	
	@Override
	public boolean  requestRights( 
		String scope 
	) {
		return true;
	}

	public void setKey(
		String key
	) {
		clearAuthCache();
		_key = key;
	}

	public void setSecret(
		String secret
	) {
		clearAuthCache();
		_secret = secret;
	}
}
