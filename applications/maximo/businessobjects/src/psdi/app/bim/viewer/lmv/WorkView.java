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

import psdi.app.bim.Constants;
import psdi.app.bim.viewer.BuildingModel;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class      WorkView 
       extends    Mbo 
       implements SavedViewRemote
{
	static final long serialVersionUID = 7195841498189578582L;
	
   	public static final String TABLE_NAME           = "BIMLMVSAVEDVIEW";
   	
	public static final String RELATIONSHIP_WORKVIEWS  = "WORKVIEWS";
   	
   	public final static String FIELD_BIMLMVWORKVIEWID  = "BIMLMVWORKVIEWID"; 
   	public final static String FIELD_WONUM             = "WONUM";
    public final static String FIELD_BUILDINGMODELID   = "BUILDINGMODELID";  
    public final static String FIELD_ORGID             = "ORGID";
	public final static String FIELD_SITEID            = "SITEID";
	public final static String FIELD_OWNER             = "OWNER";
	public final static String FIELD_DESCRIPTION       = "DESCRIPTION";
  	public final static String FIELD_VIEWERSTATE       = "VIEWERSTATE";
  	
	public final static String FIELD_LONGDESCRIPTION  = "DESCRIPTION_LONGDESCRIPTION";
	
	/**
	* Used to determine the first modification in the Product object.
	*
	* @see modify
	*/
	boolean isModified = false;

	
	private static final String readOnlyWhenNotNew[] = 
	{
		FIELD_BUILDINGMODELID,
		FIELD_ORGID, 
		FIELD_OWNER,
		FIELD_SITEID,
		FIELD_VIEWERSTATE
	};

	/**
	 * Constructor.
	 * @param ms The NonPersistentMboSet.
	 */
	public WorkView(MboSet ms) throws RemoteException
	{
		super(ms);
	}

	@Override
     public void add() 
     	throws MXException, RemoteException
     {
        super.add();
        
       MboRemote owner = getOwner();

        if( owner != null && owner.getName().equals( BuildingModel.TABLE_NAME))
        {
            setValue( FIELD_BUILDINGMODELID, owner.getString( FIELD_BUILDINGMODELID ), 
                      NOACCESSCHECK|NOVALIDATION_AND_NOACTION);
            setValue( Constants.FIELD_SITEID, owner.getString( Constants.FIELD_SITEID ), 
                      NOACCESSCHECK|NOVALIDATION_AND_NOACTION);
       }
		setValue( FIELD_OWNER, getUserInfo().getUserName(),
		          NOACCESSCHECK | NOVALIDATION_AND_NOACTION );
     } // add
	
	@Override
	public void init() 
		throws MXException
	{
		try
		{
			if( !toBeAdded() )
			{
				setFieldFlag( readOnlyWhenNotNew, READONLY, true) ;
			}
		}
		catch ( Exception e )
		{
			// this will never fail...
		}
	}
}