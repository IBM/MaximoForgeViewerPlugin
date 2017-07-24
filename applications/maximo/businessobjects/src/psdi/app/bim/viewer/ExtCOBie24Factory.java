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
**/
package psdi.app.bim.viewer;
import java.rmi.RemoteException;
import java.util.Locale;

import psdi.app.bim.loader.BuildingCommissioner;
import psdi.app.bim.loader.COBie24Factory;
import psdi.app.bim.loader.ClassificationOmniClassLogger;
import psdi.app.bim.loader.CommissioningLogger;
import psdi.app.bim.loader.LoaderAttributeType;
import psdi.app.bim.loader.LoaderCompany;
import psdi.app.bim.loader.LoaderComponent;
import psdi.app.bim.loader.LoaderComponentAssembly;
import psdi.app.bim.loader.LoaderContact;
import psdi.app.bim.loader.LoaderFacility;
import psdi.app.bim.loader.LoaderFloor;
import psdi.app.bim.loader.LoaderJob;
import psdi.app.bim.loader.LoaderProduct;
import psdi.app.bim.loader.LoaderSpace;
import psdi.app.bim.loader.LoaderSpec;
import psdi.app.bim.loader.LoaderSystemZone;
import psdi.app.bim.loader.LoaderTools;
import psdi.app.bim.loader.ModelExporter;
import psdi.app.bim.loader.ModelLoader;
import psdi.app.bim.loader.ModelLoaderBase;
import psdi.app.bim.loader.ModelLoaderOptions;
import psdi.app.bim.loader.ModelValidator;
import psdi.app.bim.loader.ProgressLogger;
import psdi.app.bim.parser.cobie.BIMProjectParser;
import psdi.app.bim.parser.cobie.IdFactory;
import psdi.app.bim.parser.cobie.ItemCOMPONENT;
import psdi.app.bim.parser.cobie.ItemFACILITY;
import psdi.app.bim.parser.cobie.ItemSPACE;
import psdi.app.bim.parser.cobie.ItemSYSTEM;
import psdi.app.bim.parser.cobie.ItemZONE;
import psdi.app.bim.parser.cobie.MessageLogger;
import psdi.app.bim.project.BIMCommissionRemote;
import psdi.app.bim.project.BIMProjectRemote;
import psdi.app.bim.project.BIMSessionRemote;
import psdi.mbo.MboRemote;
import psdi.security.UserInfo;
import psdi.util.MXException;


public class ExtCOBie24Factory extends COBie24Factory
{

	@Override
    public ClassificationOmniClassLogger makeOmniClassLogger(
        MboRemote mbo,
        long sessionId,
        String messageBundleName ) throws RemoteException
    {
		System.out.println( "Ext Factry: makeOmniClassLogger" );
	    return super.makeOmniClassLogger( mbo, sessionId, messageBundleName );
    }

	@Override
    public ModelLoaderOptions makeOptions(
        BIMSessionRemote sessionMbo,
        int updateMode ) throws RemoteException, MXException
    {
   		System.out.println( "Ext Factry: makeOptions" );
	    return super.makeOptions( sessionMbo, updateMode );
    }

	@Override
    public ProgressLogger<ItemFACILITY> makeModelLogger(
        UserInfo userInfo,
        long sessionId,
        String messageBundleName ) throws RemoteException, MXException
    {
   		System.out.println( "Ext Factry: makeModelLogger" );
	    return super.makeModelLogger( userInfo, sessionId, messageBundleName );
    }

	@Override
    public ProgressLogger<ItemFACILITY> makeExportLogger(
        UserInfo userInfo,
        long sessionId,
        String messageBundleName ) throws RemoteException, MXException
    {
   		System.out.println( "Ext Factry: makeExportLogger" );
	    return super.makeExportLogger( userInfo, sessionId, messageBundleName );
    }

	@Override
    public ModelValidator makeModelValidator(
        BIMProjectRemote projectMbo,
        ModelLoaderOptions options,
        ProgressLogger<ItemFACILITY> logger ) throws RemoteException, MXException
    {
   		System.out.println( "Ext Factry: makeModelValidator" );
	    return super.makeModelValidator( projectMbo, options, logger );
    }

	@Override
    public ModelLoader makeModelLoader(
        BIMProjectRemote projectMbo,
        ModelLoaderOptions options,
        ProgressLogger<ItemFACILITY> logger,
        int sessionType ) throws RemoteException, MXException
    {
   		System.out.println( "Ext Factry: makeModelLoader" );
	    return super.makeModelLoader( projectMbo, options, logger, sessionType );
    }

	@Override
    public ModelExporter makeModelExporter(
        BIMSessionRemote sessionMbo,
        String facilityName,
        ModelLoaderOptions options,
        ProgressLogger<ItemFACILITY> logger ) throws RemoteException, MXException
    {
   		System.out.println( "Ext Factry: makeModelExporter" );
	    return super.makeModelExporter( sessionMbo, facilityName, options, logger );
    }

	@Override
    public BIMProjectParser makeParser(
        IdFactory idFactory,
        ModelLoaderOptions options,
        MessageLogger logger,
        Locale locale,
        long flags )
    {
   		System.out.println( "Ext Factry: makeParser" );
	    return super.makeParser( idFactory, options, logger, locale, flags );
    }

	@Override
    public LoaderAttributeType makeAttributeTypeLoader(
        ModelLoaderBase loader )
    {
   		System.out.println( "Ext Factry: makeAttributeTypeLoader" );
	    return super.makeAttributeTypeLoader( loader );
    }

	@Override
    public LoaderContact makeContactLoader(
        ModelLoaderBase loader )
    {
   		System.out.println( "Ext Factry: makeContactLoader" );
	    return super.makeContactLoader( loader );
    }

	@Override
    public LoaderCompany makeCompanyLoader(
        ModelLoaderBase loader )
    {
   		System.out.println( "Ext Factry: makeCompanyLoader" );
	    return super.makeCompanyLoader( loader );
    }

	@Override
    public LoaderFacility makeFacilityLoader(
        ModelLoaderBase loader )
    {
   		System.out.println( "Ext Factry: makeFacilityLoader" );
	    return super.makeFacilityLoader( loader );
    }

	@Override
    public LoaderSpec makeDesignSpecLoader(
        ModelLoaderBase loader )
    {
   		System.out.println( "Ext Factry: makeDesignSpecLoader" );
	    return super.makeDesignSpecLoader( loader );
    }

	@Override
	public LoaderProduct makeProductLoader(
	    ModelLoaderBase loader )
	{
   		System.out.println( "Ext Factry: makeProductLoader" );
	    return super.makeProductLoader( loader );
    }

	@Override
	public LoaderTools makeToolsLoader(
	    ModelLoaderBase loader )
	{
		System.out.println( "Ext Factry: makeToolsLoader" );
		return super.makeToolsLoader( loader );
	}

	@Override
	public LoaderJob makeJobLoader(
	    ModelLoaderBase loader )
	{
		System.out.println( "Ext Factry: makeJobLoader" );
		return super.makeJobLoader( loader );
	}

	@Override
	public LoaderFloor makeFloorLoader(
	    ModelLoaderBase loader )
	{
		System.out.println( "Ext Factry: makeJobLoader" );
		return super.makeFloorLoader( loader );
	}

	@Override
	public LoaderSpace makeSpaceLoader(
	    ModelLoaderBase loader )
	{
		System.out.println( "Ext Factry: makeSpaceLoader" );
		return super.makeSpaceLoader( loader );
	}

	@Override
	public LoaderComponent makeComponentLoader(
	    ModelLoaderBase loader )
	{
		System.out.println( "Ext Factry: makeComponentLoader" );
		return super.makeComponentLoader( loader );
	}

	@Override
	public LoaderSystemZone<ItemZONE, ItemSPACE> makeZoneLoader(
	    ModelLoaderBase loader )
	{
		System.out.println( "Ext Factry: makeZoneLoader" );
		return super.makeZoneLoader( loader );
	}

	@Override
	public LoaderSystemZone<ItemSYSTEM, ItemCOMPONENT> makeSystemLoader(
	    ModelLoaderBase loader )
	{
		System.out.println( "Ext Factry: makeSystemLoader" );
		return super.makeSystemLoader( loader );
	}

	@Override
	public LoaderComponentAssembly makeAssemblyLoader(
	    ModelLoaderBase loader )
	{
		System.out.println( "Ext Factry: makeAssemblyLoader" );
		return super.makeAssemblyLoader( loader );
	}

	@Override
	public CommissioningLogger makeCommissioningLogger(
	    UserInfo userInfo,
	    long commissionId,
	    String messageBundleName ) throws RemoteException
	{
		System.out.println( "Ext Factry: makeCommissioningLogger" );
		return super.makeCommissioningLogger( userInfo, commissionId, messageBundleName );
	}

	@Override
	public BuildingCommissioner makeBuildingCommissioner(
	    BIMCommissionRemote commissionMbo,
	    CommissioningLogger logger ) throws RemoteException, MXException
	{
		System.out.println( "Ext Factry: makeBuildingCommissioner" );
		return super.makeBuildingCommissioner( commissionMbo, logger );
	}

}
