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

import psdi.configure.CommonShell;
import psdi.script.AutoUpgradeTemplate;

/**
 * Add database info to menu
 * @see #process
 */
public class V7607_04 extends AutoUpgradeTemplate
{
	private static final String MENU_UPDATE = "update maxmenu set position={0} where keyvalue = '{1}' AND moduleapp = 'BIM'";
 
	public V7607_04(Connection con) throws Exception {
		super(con);
	}

	public V7607_04(Connection con, PrintStream ps) throws Exception {
		super(con, ps);
	}

	public V7607_04(Connection con, HashMap params, PrintStream ps)
			throws Exception {
		super(con, params, ps);
	}

    /**
	 * Initialize.
	 */
    @Override
    public void init() throws Exception
	{
    	// Name of the script file
    	scriptFileName = "V7607_04";
	} //init

	/**
	 * Add menu action
	 */

	@Override
    protected void process() throws Exception 
	{
		updateBIMMenu( "BIMMODELS" );
		updateBIMMenu( "PRODUCT" );
		updateBIMMenu( "DESIGNSPEC" );
	} // end process
	
	private void updateBIMMenu(
		String appName
	)
		throws Exception
	{
		String newPos = menuGetNextPosition("MODULE", "BIM", "88898" );
		String stmt = MENU_UPDATE.replace( "{0}", newPos );
		stmt = stmt.replace( "{1}", appName );
		String sql = stmt;
        if (util.dbOut == DB2)
        {
        	sql = CommonShell.reformatForDB2(con, stmt);
        }
        else if(util.dbOut == SQLSERVER)
        {
        	sql = CommonShell.reformatForSqlsvr(con, stmt);
        }        	
		executeSql(sql);
	}

} //end of class V7600_06
