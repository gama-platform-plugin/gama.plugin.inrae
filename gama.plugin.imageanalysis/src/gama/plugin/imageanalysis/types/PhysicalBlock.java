package gama.plugin.imageanalysis.types;

import gama.annotations.getter;
import gama.annotations.variable;
import gama.annotations.vars;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.expressions.IExpression;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.Types;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.IShape;
import gama.api.types.misc.IValue;
import gama.api.utils.json.IJson;
import gama.api.utils.json.IJsonValue;
import gama.core.util.json.Json;

@vars({ @variable(name = "type", type = PatternBlockType.id), 
		@variable(name = "shape", type = IType.GEOMETRY)})
public class PhysicalBlock implements IValue {
	private PatternBlock pattern;
	private IShape shape;

	public PhysicalBlock() {
	}
	

	public PhysicalBlock(IScope scope, String typeName, int cols, int rows, IExpression init, IShape geom, boolean parallel) {
		pattern = new PatternBlock(scope, typeName, cols, rows, init, parallel);
		shape = geom;
	}

		
	
	@getter ("type")
	public PatternBlock getPattern() {
		return pattern;
	}




	public void setPattern(PatternBlock pattern) {
		this.pattern = pattern;
	}



	@getter ("shape")
	public IShape getShape() {
		return shape;
	}


	public void setShape(IShape shape) {
		this.shape = shape;
	}


	@Override
	public IType<?> getGamlType() {
		return Types.get(PhysicalBlockType.id);
	}
	
	
	@Override
	public String toString() {
		return serializeToJson(Json.getNew()).toString();
	}


	
	
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return (pattern == null ? "": pattern.stringValue(scope)) + ":" + (shape == null ? "" : shape.stringValue(scope));
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		PhysicalBlock p = new PhysicalBlock();
		p.setPattern((PatternBlock) pattern.copy(scope));
		p.setShape(shape.copy(scope));
		return p;
	}


	@Override
	public IJsonValue serializeToJson(IJson json) {
		return json.typedObject(getGamlType(), "pattern", pattern, "shape", shape);
	}

	
}
