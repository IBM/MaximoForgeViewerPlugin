package psdi.app.bim.viewer.dataapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class ResultDownload
    extends Result
{

	public ResultDownload()
	{
		// TODO Auto-generated constructor stub
	}

	public ResultDownload(
	    Result result 
    ) {
		super( result );
		// TODO Auto-generated constructor stub
	}

	public ResultDownload(
	    HttpURLConnection connection,
	    OutputStream      os
    ) {
		super();
		
    	try
    	{
    		setHttpStatus( connection.getResponseCode() );
            if( getHttpStatus() > 299 )
            {
            	_errorType = ERROR_TYPE.HTTP;
                _rawError = stream2string( connection.getErrorStream() );
                parseError( _rawError );
            	return;
            }
            _rawData = "";
            dowloadFile( connection.getInputStream(), os );
    	}
    	catch( IOException ioe )
    	{
    		parseException( ioe );
    	}

	}

	public ResultDownload(
	    Exception e 
    ) {
		super( e );
	}

	protected void dowloadFile(
	    InputStream  is,
	    OutputStream os
	)
	    throws IOException
	{
		try
		{
			byte[] b = new byte[1024];
			int noOfBytes = 0;

			while( (noOfBytes = is.read(b)) != -1 )
			{
				os.write(b, 0, noOfBytes);
			}
		}
		finally
		{
			is.close();
			os.close();                   
		}
	}

}
