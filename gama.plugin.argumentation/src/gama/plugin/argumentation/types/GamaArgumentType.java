package gama.plugin.argumentation.types;

import gama.annotations.type;
import gama.annotations.support.IConcept;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.GamaType;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.ITypesManager;
import gama.api.kernel.agent.IAgent;
import gama.api.runtime.scope.IScope;
import gama.api.types.map.GamaMap;
import gama.api.types.map.GamaMapFactory;


@type(name = "argument", id = GamaArgumentType.id, wraps = { GamaArgument.class }, concept = { IConcept.TYPE, "Argumentation" })
public class GamaArgumentType extends GamaType<GamaArgument> {

	public GamaArgumentType(ITypesManager typesManager) {
		super(typesManager);
		// TODO Auto-generated constructor stub
	}

	public final static int id = IType.BEGINNING_OF_CUSTOM_TYPES + 175769875;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public GamaArgument cast(IScope scope, Object obj, Object param, boolean copy) throws GamaRuntimeException {
		if (obj instanceof GamaArgument) {
			return (GamaArgument) obj;
		} else if (obj instanceof GamaMap) {
			GamaMap m = (GamaMap) obj;
			GamaArgument arg = new GamaArgument(
					m.containsKey("id") ? (String)m.get("id"): "",
					m.containsKey("option") ? (String)m.get("option"): "",
					m.containsKey("conclusion") ?(String) m.get("conclusion"): "0",
					m.containsKey("statement") ? (String)m.get("statement"): "",
					m.containsKey("rationale") ? (String)m.get("rationale"): "",
					m.containsKey("criteria") ? (GamaMap<String, Double>)m.get("criteria"): GamaMapFactory.create(),
					(IAgent)m.get("actor"),
					m.containsKey("source_type") ? (String)m.get("source_type"): "");
			return arg;
		}
		return null;
	}

	@Override
	public GamaArgument getDefault() {
		return null;
	}

}
