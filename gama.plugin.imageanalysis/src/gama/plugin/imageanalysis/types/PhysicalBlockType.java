package gama.plugin.imageanalysis.types;

import gama.annotations.doc;
import gama.annotations.operator;
import gama.annotations.type;
import gama.annotations.constants.IKeyword;
import gama.annotations.support.IConcept;
import gama.annotations.support.IOperatorCategory;
import gama.annotations.support.ITypeProvider;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.expressions.IExpression;
import gama.api.gaml.types.GamaType;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.ITypesManager;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.GamaPoint;
import gama.api.types.geometry.IShape;


@type(name = "block", id = PhysicalBlockType.id, wraps = { PhysicalBlock.class }, concept = { IConcept.TYPE, "block" })
public class PhysicalBlockType extends GamaType<PhysicalBlock> {
public PhysicalBlockType(ITypesManager typesManager) {
		super(typesManager);
		// TODO Auto-generated constructor stub
	}


	//	public final static int id = IType.AVAILABLE_TYPES + 175769875;
	public final static int id = IType.BEGINNING_OF_CUSTOM_TYPES + 190720223;

	@Override
	public PhysicalBlock getDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canCastToConst() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PhysicalBlock cast(IScope scope, Object obj, Object param, boolean copy) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		if(obj instanceof PhysicalBlock) {
			return (PhysicalBlock) obj;
		}

		return null;
	}


/**
 * Matrix with.
 *
 * @param scope
 *            the scope 
 * @param size
 *            the size
 * @param init
 *            the init
 * @return the i matrix
 */
@operator (
		value = "block_with",
		content_type = ITypeProvider.SECOND_CONTENT_TYPE_OR_TYPE,
		can_be_const = true,
		category = { IOperatorCategory.CASTING },
		concept = { IConcept.CAST, IConcept.CONTAINER })
@doc (
		value = "creates a block with a size provided by the first operand, and filled with the second operand",
		comment = "Note that both components of the right operand point should be positive, otherwise an exception is raised.",
		see = { IKeyword.MATRIX, "as_matrix" })
public static PhysicalBlock matrix_with(final IScope scope, final String typeName, final GamaPoint size, final IExpression init, final IShape shape, final boolean parallel) {
	if (size == null) throw GamaRuntimeException.error("A nil size is not allowed for blocks", scope);
	
	return new PhysicalBlock(scope, typeName,(int)size.x, (int)size.y, init, shape, parallel);
}
	
}

