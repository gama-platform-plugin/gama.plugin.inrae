package gama.plugin.webcam.types;

import java.awt.Dimension;

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
import gama.api.types.pair.GamaPair;
import gama.api.types.pair.GamaPairFactory;
import gama.api.utils.json.IJson;
import gama.api.utils.json.IJsonValue;
import gama.core.util.json.Json;
import gama.core.util.json.JsonValue;

@vars({ @variable(name = "id", type = IType.INT),@variable(name = "resolutions", type = IType.LIST) })
public class GamaWebcam implements IValue {

	private WebcamCustom webcam;
	private Integer id;
	
	
	@getter ("resolutions")
	public IList<GamaPair<Integer, Integer>> getResolutions() {
		Dimension[] dims =  webcam.getViewSizes();
		IList<GamaPair<Integer, Integer>> resolutions = GamaListFactory.create();
		for (Dimension d : dims) {
			resolutions.add((GamaPair<Integer, Integer>) GamaPairFactory.createWith(d.width, d.height, Types.INT, Types.INT));
		}
		return resolutions;
	}
	
	public WebcamCustom getWebcam() {
		return webcam;
	}

	public void setWebcam(WebcamCustom webcam) {
		this.webcam = webcam;
	}

	protected GamaWebcam(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	} 
	
	
	public void setType(Integer id) {
		this.id = id;
	}
	

	@Override
	public IType<?> getGamlType() {
		return Types.get(GamaWebcamType.id);
	}
	
	
	@Override
	public String toString() {
		return serialize(true);
	}

	public String serialize(final boolean includingBuiltIn) {
		return ""+id;
	}
	
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "" +id;
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return null;
	}

	
	@Override
	public IJsonValue serializeToJson(IJson json) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
