package gama.plugin.argumentation.species;

import gama.annotations.getter;
import gama.annotations.setter;
import gama.annotations.species;
import gama.annotations.variable;
import gama.annotations.vars;
import gama.api.gaml.types.IType;
import gama.api.kernel.agent.IAgent;
import gama.api.kernel.agent.IPopulation;
import gama.api.types.list.IList;
import gama.core.agent.GamlAgent;
import gama.plugin.argumentation.types.GamaArgument;
import gama.plugin.argumentation.types.GamaArgumentType;

@species (
		name = "abstract_communication_channel")
@vars ({
	 @variable(name = "type", type = IType.STRING),
		@variable(name = "arguments", type = IType.LIST, of = GamaArgumentType.id)
				
})
public class CommunicationChannel extends GamlAgent {

	static final String TYPE = "type";
	static final String ARGUMENTS = "arguments";
	
	
	public CommunicationChannel(IPopulation<? extends IAgent> s, int index) {
		super(s, index);
	}
	

	@getter(TYPE)
	public String getType(final IAgent agent) {
		return (String) agent.getAttribute(TYPE);
	}

	@setter(TYPE)
	public void setType(final IAgent agent, final String v) {
		agent.setAttribute(TYPE, v);
	}
	
	@getter(ARGUMENTS)
	public static IList<GamaArgument> getArguments(final IAgent agent) {
		return (IList<GamaArgument>) agent.getAttribute(ARGUMENTS);
	}

	@setter(ARGUMENTS)
	public void setArguments(final IAgent agent, final IList<GamaArgument> v) {
		agent.setAttribute(ARGUMENTS, v);
	}
	

}
