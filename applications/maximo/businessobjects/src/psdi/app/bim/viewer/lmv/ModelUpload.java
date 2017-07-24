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
package psdi.app.bim.viewer.lmv;

import java.rmi.RemoteException;

import psdi.app.bim.project.ImportBase;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class ModelUpload extends ImportBase implements ModelUploadRemote
{
	public static final String TABLE_NAME                 = "BIMLMVMODELUPLOAD";
	public static final String RELATIONSHIP_USEWITHLIST   = "USEWITHLIST";
	
	public static final String FIELD_BIMLMVMODELUPLOADID  = "BIMLMVMODELUPLOADID";
	public static final String FIELD_BIMLMVMODELID        = "BIMLMVMODELID";
	public static final String FIELD_FILENAME             = "FILENAME";
	
	public static final String STATUS_LINKED  = "LINKED";
	public static final String STATUS_EXPIRED = "EXPIRED";

	
	/**
    * Construct the operating location object
    */
    public ModelUpload(
    	MboSet ms
	) 
    	throws MXException, RemoteException
    {
        super(ms);
        
		String alwaysReadOnly[] = { FIELD_IMPORTEDBY };
		try
		{
			if( !toBeAdded() )
			{
				setFieldFlag( alwaysReadOnly, READONLY, true );
			}
		}
		catch( Exception e )
		{
			// this will never fail...
		}
    }
	
	@Override
	public void init() 
		throws MXException
	{
		super.init();
		MboRemote owner = getOwner();
		if( owner != null && owner instanceof ModelRemote )
		{
			try
            {
	            setValue( FIELD_BIMLMVMODELID, owner.getString( FIELD_BIMLMVMODELID ), MboValue.NOACCESSCHECK );
	            setValue( FIELD_FILENAME, owner.getString( Model.FIELD_FILENAME ),  MboValue.NOACCESSCHECK  );
            }
            catch( RemoteException e )
            {
	            e.printStackTrace();
            }
		}
	}
	
 	@Override
    public boolean isStatusCompelete() 
		throws RemoteException, 
		       MXException
	{
		String value = getString( FIELD_STATUS ); 
		String internalValue = getTranslator().toInternalString( DOMAIN_BIMIMPORTSTATUS, value );
		if( internalValue.equals(  "LINKED" )) return true;
		if( internalValue.equals(  "EXPIRED" )) return true;
		return super.isStatusCompelete();
	}
}
