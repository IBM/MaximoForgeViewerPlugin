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
package psdi.app.bim.viewer.lmv.virtual;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMbo;

public class BucketDeleteApprove extends NonPersistentMbo implements BucketDeleteApproveRemote
{
	static final long serialVersionUID = 7195841498189578582L;
	
    public final static String FIELD_MODELSDELETE    = "MODELSDELETE";
    public final static String FIELD_VIEWABLESDELETE = "VIEWABLESDELETE"; 
    public final static String FIELD_LINKEDLOC       = "LINKEDLOC"; 
	
	/**
	 * Constructor.
	 * @param ms The NonPersistentMboSet.
	 */
	public BucketDeleteApprove(MboSet ms) throws RemoteException
	{
		super(ms);
	}

}
