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
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.Translate;
import psdi.util.MXException;

public class ModelUploadSet extends MboSet implements ModelUploadSetRemote
{

    /**
     * Construct the set
     */
    public ModelUploadSet(
    	MboServerInterface ms
	) 
    	throws MXException, RemoteException
    {
        super(ms);
    }


	@Override
    /**
     * Generate a new operating location object
     *
     * @param        ms  mboset
     * @return       Mbo object
     */
    protected Mbo getMboInstance(
    	MboSet ms
	) 
    	throws MXException, RemoteException
    {
        return new ModelUpload(ms);
    }
	
	@Override
	/**
	* Return the translator object which can be used to convert
	* locale sensitive strings
	*/
	public Translate getTranslator()
	{
		return getMboServer().getMaximoDD().getTranslator();
	}
}