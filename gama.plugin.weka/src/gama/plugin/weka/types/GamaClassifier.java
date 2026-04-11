package gama.plugin.weka.types;

import java.util.Map;

import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.Types;
import gama.api.runtime.scope.IScope;
import gama.api.types.list.IList;
import gama.api.types.misc.IValue;
import gama.api.utils.json.IJson;
import gama.api.utils.json.IJsonValue;
import gama.core.util.json.Json;
import gama.core.util.json.JsonValue;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class GamaClassifier  implements IValue{
	private Classifier classifier;
	private Instances dataset;

	private Map<String,IList<String>> valsNominal;
	
	
	
	@Override
	public IType<?> getGamlType() {
		return Types.get(GamaClassifierType.id);
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		if (classifier == null) return "empty classifier";
		return classifier.toString();
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public Instances getDataset() {
		return dataset;
	}

	public void setDataset(Instances dataset) {
		this.dataset = dataset;
	}

	public Map<String, IList<String>> getValsNominal() {
		return valsNominal;
	}

	public void setValsNominal(Map<String, IList<String>> valsNominal) {
		this.valsNominal = valsNominal;
	}


	@Override
	public IJsonValue serializeToJson(IJson json) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
