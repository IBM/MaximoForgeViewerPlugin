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
package psdi.app.bim.viewer.dataapi;

public class FileReference
{
	private final String _urn;
	private final String _key;
	
	public FileReference(
  		String urn,
		String key
    ) {
		_urn = urn;
		_key = key;
	}

	public String getUrn()
	{
		return _urn;
	}

	public String getKey()
	{
		return _key;
	}
}

