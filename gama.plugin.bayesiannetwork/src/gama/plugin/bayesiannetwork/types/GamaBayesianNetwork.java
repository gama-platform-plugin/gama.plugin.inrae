package gama.plugin.bayesiannetwork.types;

import java.util.List;

import cc.kave.repackaged.jayes.BayesNet;
import cc.kave.repackaged.jayes.BayesNode;
import cc.kave.repackaged.jayes.inference.IBayesInferer;
import cc.kave.repackaged.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import gama.annotations.getter;
import gama.annotations.variable;
import gama.annotations.vars;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.Types;
import gama.api.runtime.scope.IScope;
import gama.api.types.list.GamaListFactory;
import gama.api.types.list.IList;
import gama.api.types.misc.IValue;
import gama.api.utils.json.IJson;
import gama.api.utils.json.IJsonValue;
 
@vars({ @variable(name = "id", type = IType.STRING), 
	@variable(name = "nodes", type = IType.LIST) })
public class GamaBayesianNetwork  implements IValue{
	private BayesNet network;
	private IBayesInferer inference ;
	
	public GamaBayesianNetwork(String id) {
		super();
		network = new BayesNet();
		inference = new JunctionTreeAlgorithm();
		network.setName(id);
	}
	
	public GamaBayesianNetwork(BayesNet net) {
		super();
		network = net;
		inference = new JunctionTreeAlgorithm();
		inference.setNetwork(network);
	}

	@getter("id")
	public String getId() {
		return network.getName();
	}

	@getter("nodes")
	public IList<String> getNodes() {
		List<BayesNode> nodes = network.getNodes();
		IList nodesName = GamaListFactory.EMPTY_LIST;
		for (BayesNode n : nodes) nodesName.add(n.getName());
		return nodesName;
	}
	
	
	public BayesNet getNetwork() {
		return network;
	}

	public void setNetwork(BayesNet network) {
		this.network = network;
	}
	

	public IBayesInferer getInference() {
		return inference;
	}

	public void setInference(IBayesInferer inference) {
		this.inference = inference;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return getId() + " -> " + getNodes();
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		GamaBayesianNetwork bayes = new GamaBayesianNetwork(this.network);
		return bayes;
	}


	@Override
	public IType<?> getGamlType() {
		return Types.get(GamaBayesianNetworkType.id);
	}

	@Override
	public IJsonValue serializeToJson(IJson json) {
		// TODO Auto-generated method stub
		return null;
	}
		
}
