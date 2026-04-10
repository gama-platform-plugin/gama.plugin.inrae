package gama.plugin.bayesiannetwork.types;

import gama.annotations.type;
import gama.annotations.support.IConcept;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.GamaType;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.ITypesManager;
import gama.api.runtime.scope.IScope;

@type(name = "bayesian_network", id = GamaBayesianNetworkType.id, wraps = { GamaBayesianNetwork.class }, concept = { IConcept.TYPE, "Bayesian Network" })
public class GamaBayesianNetworkType extends GamaType<GamaBayesianNetwork> {

	public GamaBayesianNetworkType(ITypesManager typesManager) {
		super(typesManager);
		
	}

	public final static int id = IType.BEGINNING_OF_CUSTOM_TYPES + 1231029875;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public GamaBayesianNetwork cast(IScope scope, Object obj, Object param, boolean copy) throws GamaRuntimeException {
		if (obj instanceof GamaBayesianNetwork) {
			return (GamaBayesianNetwork) obj;
		} 
		if (obj instanceof String) {
			return new GamaBayesianNetwork((String)obj);
		}
		return null;
	}

	@Override
	public GamaBayesianNetwork getDefault() {
		return null;
	}

}
