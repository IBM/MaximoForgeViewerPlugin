package psdi.app.bim.viewer.dataapi.cli;

import java.io.IOException;
import java.net.URISyntaxException;

import psdi.app.bim.viewer.dataapi.DataRESTAPI;
import psdi.app.bim.viewer.dataapi.Result;

public class ViewableDownload
{

	// urn:adsk.viewing:fs.file:dXJuOmFkc2sub2JqZWN0czpvcy5vYmplY3Q6dmFsbGV5X2ZvcmdlN2FwZmtvd3RvbHN4ZXJnY2FicWp2ZzNvYmdndW5oZGEvYmFydG9ua2VlcC56aXA/output/Resource/

	private DataRESTAPI _service;
	public ViewableDownload()
	{
		_service = new APIImpl();
	}
	
	public DataRESTAPI getService()
	{
		return _service;
	}

	public Result viewableDownload(
		String derivitiveURN,
		String dirName,
		String fileName
	) 	
		throws IOException, 
			   URISyntaxException 
	{
		return _service.viewableDownload( derivitiveURN, dirName, fileName );
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
		if( arg.length < 1 )
		{
			System.out.println( "Usage: viewableDownload urn derivitive" );
			return;
		}
		ViewableDownload download = new ViewableDownload();
		
		Result result = download.viewableDownload( arg[0], arg[1], arg[2] );
		if( result.isError() )
		{
			System.out.println( result.toString() );
			return;
		}
		System.out.println( result.toString() );
	}
}
