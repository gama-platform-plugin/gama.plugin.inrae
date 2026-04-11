package gama.plugin.weka.types;

import gama.annotations.type;
import gama.annotations.support.IConcept;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.GamaType;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.ITypesManager;
import gama.api.runtime.scope.IScope;

@type(name = "classifier", id = GamaClassifierType.id, wraps = { GamaClassifier.class }, concept = { IConcept.TYPE, IConcept.STATISTIC })
public class GamaClassifierType extends GamaType<GamaClassifier> {

	public GamaClassifierType(ITypesManager typesManager) {
		super(typesManager);
	}

	public final static int id = IType.BEGINNING_OF_CUSTOM_TYPES + 54736255;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public GamaClassifier cast(IScope scope, Object obj, Object param, boolean copy) throws GamaRuntimeException {
		if (obj instanceof GamaClassifier) {
			return (GamaClassifier) obj;
		}
		return null;
	}

	@Override
	public GamaClassifier getDefault() {
		return null;
	}

}
