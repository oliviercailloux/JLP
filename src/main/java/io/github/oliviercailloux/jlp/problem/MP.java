package io.github.oliviercailloux.jlp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Equivalence;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableKind;
import io.github.oliviercailloux.jlp.utils.MPUtils;
import io.github.oliviercailloux.jlp.utils.SolverUtils;

/**
 * A modifiable mathematical program.
 *
 * This object forbids adding two different variables with the same description
 * (e.g., "x" int and "x" real).
 *
 * @author Olivier Cailloux
 * @see {@link IMP}, {@link Variable}.
 *
 */
public class MP implements IMP {

	/**
	 * A copy constructor, by value. No reference is shared between the new problem
	 * and the given one.
	 * <p>
	 * The variables and constraints sets iteration order will be the same as the
	 * sets iteration order of the source.
	 * </p>
	 *
	 * @param source
	 *            not <code>null</code>.
	 */
	static public MP copyOf(IMP source) {
		requireNonNull(source);

		final MP mp = new MP();
		mp.setName(source.getName());
		for (Variable variable : source.getVariables()) {
			mp.addVariable(variable);
		}
		mp.setObjective(source.getObjective());
		for (Constraint constraint : source.getConstraints()) {
			mp.add(constraint);
		}

		return mp;
	}

	static public MP create() {
		return new MP();
	}

	private final List<Constraint> constraints = Lists.newLinkedList();

	private BiMap<String, Variable> descrToVar;

	/**
	 * Never <code>null</code>.
	 */
	private String mpName;

	private Objective objective;

	private final Multiset<VariableKind> varCount = EnumMultiset.create(VariableKind.class);

	private final List<Variable> variables = Lists.newLinkedList();

	private MP() {
		mpName = "";
		objective = Objective.ZERO;
		descrToVar = HashBiMap.create();
	}

	/**
	 * Adds a constraint, or does nothing if the given constraint is already in this
	 * MP. The variables used in the constraint are added to this MP if not present
	 * yet, in the order they are found in the given constraint.
	 *
	 * @param constraint
	 *            not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff the given constraint was already in this MP.
	 */
	public boolean add(Constraint constraint) {
		requireNonNull(constraint);
		final SumTerms sumTerms = constraint.getLhs();
		final boolean addedV = addVariables(sumTerms);
		final boolean addedC = constraints.add(constraint);
		/**
		 * We want to check addedV ⇒ addedC, thus, exclude the contradictory case, where
		 * addedV but not addedC.
		 *
		 * Equiv: addedC iff addedV || addedC.
		 */
		assert !(addedV && !addedC);
		return addedC;
	}

	/**
	 * Adds the variable to this MP if it is not already in.
	 *
	 * @param variable
	 *            not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff the given variable was already in this MP.
	 */
	public boolean addVariable(Variable variable) {
		requireNonNull(variable);
		final String descr = variable.toString();
		requireNonNull(descr);
		final boolean hasDescr = descrToVar.containsKey(descr);
		final boolean hasVar = variables.contains(variable);
		/** We know: hasVar ⇒ hasDescr. */
		assert !hasVar || hasDescr;
		/**
		 * We want to check: hasDescr ⇒ hasVar, otherwise, already has descr but no corr
		 * var.
		 */
		checkArgument(!hasDescr || hasVar, "This MP already contains the variable '" + descrToVar.get(descr)
				+ "'. It is forbidden to add a different variable with the same description: '" + variable + "'.");
		assert hasVar == hasDescr;
		if (hasDescr) {
			return false;
		}
		descrToVar.put(descr, variable);
		varCount.add(variable.getKind());
		variables.add(variable);
		return true;
	}

	/**
	 * Removes all the variables and constraints, objective function, name set in
	 * this problem. As a result of this call, this problem has the same visible
	 * state as a newly created, empty problem.
	 */
	public void clear() {
		mpName = "";
		variables.clear();
		descrToVar.clear();
		varCount.clear();
		constraints.clear();
		objective = Objective.ZERO;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IMP)) {
			return false;
		}
		final IMP p2 = (IMP) obj;
		if (p2 == obj) {
			return true;
		}
		if (!getName().equals(p2.getName())) {
			return false;
		}
		if (!getVariables().equals(p2.getVariables())) {
			return false;
		}
		if (!getConstraints().equals(p2.getConstraints())) {
			return false;
		}
		return getObjective().equals(p2.getObjective());
	}

	@Override
	public List<Constraint> getConstraints() {
		return Collections.unmodifiableList(constraints);
	}

	@Override
	public MPDimension getDimension() {
		return MPDimension.of(varCount, getConstraints().size());
	}

	@Override
	public String getName() {
		return mpName;
	}

	@Override
	public Objective getObjective() {
		return objective;
	}

	@Override
	public Optional<Variable> getVariable(String description) {
		return Optional.ofNullable(descrToVar.get(description));
	}

	@Override
	public List<Variable> getVariables() {
		return Collections.unmodifiableList(variables);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mpName, getVariables(), getConstraints(), getObjective());
	}

	/**
	 * Sets or removes the name of this problem.
	 *
	 * @param name
	 *            <code>null</code> or empty string for no name. A <code>null</code>
	 *            string is converted to an empty string.
	 * @return <code>true</code> iff the call modified the state of this object.
	 *         Equivalently, returns <code>false</code> iff the given name was
	 *         different than this problem name.
	 *
	 */
	public boolean setName(String name) {
		final String newName;
		if (name == null) {
			newName = "";
		} else {
			newName = name;
		}
		final boolean equivalent = Equivalence.equals().equivalent(this.mpName, newName);
		if (equivalent) {
			return false;
		}
		this.mpName = name;
		return true;
	}

	/**
	 * Sets or removes the objective bound to this problem. The variables used in
	 * the objective function are added to this problem. Setting both parameters to
	 * <code>null</code> is legal.
	 *
	 * @param objectiveFunction
	 *            <code>null</code> to remove a possibly set objective function.
	 * @param direction
	 *            <code>null</code> for not set (removes a possibly set optimization
	 *            direction).
	 */
	public void setObjective(Objective obj) {
		addVariables(obj.getFunction());
		this.objective = requireNonNull(obj);
	}

	/**
	 * Retrieves a long description, with line breaks, of this problem.
	 *
	 * @return not <code>null</code>, not empty.
	 */
	public String toLongDescription() {
		return MPUtils.getLongDescription(this);
	}

	@Override
	public String toString() {
		return SolverUtils.getAsString(this);
	}

	private boolean addVariables(SumTerms sumTerms) {
		boolean added = false;
		for (Term term : sumTerms) {
			final Variable variable = term.getVariable();
			final boolean nowAdded = addVariable(variable);
			added = added || nowAdded;
		}
		return added;
	}

}
