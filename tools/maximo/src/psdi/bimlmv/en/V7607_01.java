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
package psdi.bimlmv.en;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.HashMap;

import psdi.script.MsgProcSwitchOver;

public class V7607_01 extends MsgProcSwitchOver {
	public V7607_01(Connection con, HashMap params, PrintStream ps)
			throws Exception {
		super(con, params, ps);
	}
}
