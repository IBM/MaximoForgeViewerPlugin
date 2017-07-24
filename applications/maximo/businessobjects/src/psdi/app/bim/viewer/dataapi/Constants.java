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

public class Constants
{
	/**
	 * <n2>Transient</h2>
	 * <p>
	 * Think of this type of storage as a cache. Use it for intermittent results; objects that are part of producing 
	 * other persistent artifacts perhaps, but otherwise not required to be available later. For intermittent objects, 
	 * this type of storage is ideal, as you can be sure there is no cost to your service beyond the actual usage.
	 * Objects older than 24 hours are removed automatically. Each upload of an object is considered unique, so if the 
	 * same intermittent rendering for example is uploaded multiple times, each of these will have its own retention 
	 * period of 24 hours. There is no archive option for this type of storage.
	 * <table>
	 * <tr> <td><b>Archival Property</b></td> 	<td><b<Value</b></td> </tr>
	 * <tr> <td>objects retained for</td>		<td>24 hours</td> </tr>
	 * <tr> <td>archivable</td> 				<td>no</td> </tr>
	 * <tr> <td>rolling</td>	 				<td>no</td> </tr>
	 * </table>
	 */
	public static final String BUCKET_POLICY_TRANSIENT = "transient";
	
	/**
	 * <h2>Temporary</h2>
	 * <p>
	 * Temporary storage has a longer shelf life than transient storage - 30 days. This type of storage is suitable for 
	 * artifacts produced for user uploaded content where, after some period of activity, the user may rarely access the 
	 * artifacts. When an object has reached 30 days of age, it is deleted. This type of bucket storage will save your 
	 * service money.
	 * <table>
	 * <tr> <td><b>Archival Property</b></td> 	<td><b<Value</b></td> </tr>
	 * <tr> <td>objects retained for</td> 		<td>30 days</td> </tr>
	 * <tr> <td>archivable</td> 				<td>no</td> </tr>
	 * <tr> <td>rolling</td> 					<td>no</td> </tr>
	 * </table>
	 */
	public static final String BUCKET_POLICY_TEMPORARY = "temporary";
	
	/**
	 * <h2>Persistent</h2>
	 * Persistent storage is intended for user data. When a file is uploaded, the owner should expect this item to be 
	 * around for as long as the owner account is active, or until s/he deletes the item. OSS still retains the right to 
	 * archive items in a persistent bucket that have not been accessed in 2 years. Objects of this age will be archived, 
	 * and the applications using Persistent buckets, will need to handle the archived response and method of retrieving.
	 * <table>
	 * <tr> <td><b>Archival Property</b></td> 	<td><b<Value</b></td> </tr>
	 * <tr> <td>objects retained for</td> 		<td>until removed by owner</td> </tr>
	 * <tr> <td>archivable</td> 				<td>yes</td> </tr>
	 * <tr> <td>rolling</td> 					<td>yes</td> </tr>
	 * </table>
	 */
	public static final String BUCKET_POLICY_PERSISTENT = "persistent";
	
	/**
	 * Create bucket in US region
	 */
	public static final String REGION_US   = "US";

	/**
	 * Create bucket in EMEA region
	 */
	public static final String REGION_EMEA = "EMEA";
}
