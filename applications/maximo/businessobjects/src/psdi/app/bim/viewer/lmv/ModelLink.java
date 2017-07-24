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

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class ModelLink extends Mbo implements ModelLinkRemote
{
	public static final String TABLE_NAME          = "BIMLMVMODELLINK";
	public final static String FIELD_BUCKETKEYFULL = "BUCKETKEYFULL";
	public static final String FIELD_PARENTMODELID = "PARENTMODELID";
	public static final String FIELD_CHILDMODELID  = "CHILDMODELID";
	public static final String FIELD_CHILDKEY      = "CHILDKEY";
	public static final String FIELD_CHILDURN      = "CHILDURN";
	public final static String FIELD_ORGID         = "ORGID";
	public final static String FIELD_SITEID        = "SITEID";

	
	/**
    * Construct the operating location object
    */
    public ModelLink(
    	MboSet ms
	) 
    	throws MXException, RemoteException
    {
        super(ms);
        
		String alwaysReadOnly[] = {  };
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
    public void add() 
		throws RemoteException, 
		       MXException
    {
    	super.add();
		MboRemote owner = getOwner();
		if( owner != null && owner instanceof ModelRemote )
		{
			try
            {
	            setValue( FIELD_PARENTMODELID, owner.getString( Model.FIELD_BIMLMVMODELID ) );
	            setValue( FIELD_BUCKETKEYFULL, owner.getString( Model.FIELD_BUCKETKEYFULL ) );
	            setValue( FIELD_ORGID, owner.getString( Model.FIELD_ORGID ) );
	            setValue( FIELD_SITEID, owner.getString( Model.FIELD_SITEID ) );
            }
            catch( RemoteException e )
            {
	            e.printStackTrace();
            }
		}
    }
}