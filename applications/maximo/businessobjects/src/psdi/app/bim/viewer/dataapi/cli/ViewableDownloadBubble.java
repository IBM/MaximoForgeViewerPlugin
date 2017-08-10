package psdi.app.bim.viewer.dataapi.cli;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.ResultViewerService;


public class ViewableDownloadBubble
{

	// urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dmFsbGV5X2ZvcmdlN2FwZmtvd3RvbHN4ZXJnY2FicWp2ZzNvYmdndW5oZGEvYmFydG9ua2VlcC56aXA/output/Resource/

	private DataRESTAPI _service;
	public ViewableDownloadBubble()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result viewableDownload(
		String derivitiveURN,
		String dirName
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		String urn;
		String base64urn;
		if( Base64.isBase64(derivitiveURN) )
		{
			urn       = new String( Base64.decodeBase64( derivitiveURN ) );
			base64urn = derivitiveURN;
		}
		else
		{
			urn       = derivitiveURN;
			base64urn = new String( Base64.encodeBase64URLSafe( derivitiveURN.getBytes() ));
		}
		
		ResultViewerService result = _service.viewableQuery( urn );
		if( result.isError() )
		{
			return result;
		}

		List<String> files = new LinkedList<String>();
		result.listDerivativeFiles( files );
		
		Iterator<String> itr = files.iterator();
		while( itr.hasNext() )
		{
			String fileName = itr.next();
			Result dr = _service.viewableDownload( base64urn, dirName, fileName );
			if( dr.isError() )
			{
				System.out.println( dr.toString() );
			}
			else
			{
				System.out.println( dirName + "/" + fileName );;
			}
		}

		return result;
	}


	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(
	    String[] arg
    )
		throws IOException, 
		   URISyntaxException 
	{
		if( arg.length != 2 )
		{
			System.out.println( "Usage: viewableDownloadBubble urn derivitive fileName" );
			return;
		}
		ViewableDownloadBubble download = new ViewableDownloadBubble();
		
		Result result = download.viewableDownload( arg[0], arg[1] );
		if( result.isError() )
		{
			System.out.println( result.toString() );
			return;
		}
	}
}
