package io.github.oliviercailloux.jlp.mp;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static io.github.oliviercailloux.jlp.elements.Objective.ZERO;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multiset;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableKind;

/**
 * <p>
 * A modifiable mathematical program.
 * </p>
 * <p>
 * This object forbids adding two variables with the same description (e.g., "x"
 * int and "x" real).
 * </p>
 *
 * @author Olivier Cailloux
 * @see IMP
 * @see Variable
 *
 */
public class MPBuilder implements IMP {

	/**
	 * Returns an MP that is a copy of the given source, in the sense that it
	 * contains the same data, but is not linked to the source, in the sense that
	 * modifying the resulting MP will not change the source. (Variables are shared
	 * however, so this is only true if the variables are immutable.)
	 *
	 * @param source not <code>null</code>.
	 */
	public static MPBuilder copyOf(IMP source) {
		requireNonNull(source);

		final MPBuilder mp = new MPBuilder();
		mp.setName(source.getName());
		/**
		 * Add variables first! Otherwise, some may already exist, and add will fail.
		 * Or, we could use addIfNew.
		 */
		mp.getVariables().addAll(source.getVariables());
		mp.setObjective(source.getObjective());
		mp.getConstraints().addAll(source.getConstraints());

		return mp;
	}

	/**
	 * Returns a new empty writeable MP with an empty name and the
	 * {@link Objective#ZERO ZERO} objective.
	 *
	 * @return a new writable MP.
	 */
	static MPBuilder create() {
		return new MPBuilder();
	}

	private final List<Constraint> constraints;

	private final ConstraintsInMP constraintsFacade;

	private final BiMap<String, Variable> descrToVar = HashBiMap.create();

	/**
	 * Not <code>null</code>.
	 */
	private String mpName;

	/**
	 * Not <code>null</code>.
	 */
	private Objective objective;

	private final Multiset<VariableKind> varCount = EnumMultiset.create(VariableKind.class);

	private final List<Variable> variables;

	private final VariablesInMP variablesFacade;

	private MPBuilder() {
		mpName = "";
		objective = Objective.ZERO;
		final ArrayList<Variable> variablesArrayList = new ArrayList<>();
		variables = variablesArrayList;
		variablesFacade = new VariablesInMP(this, variablesArrayList);
		final ArrayList<Constraint> constraintsArrayList = new ArrayList<>();
		constraints = constraintsArrayList;
		constraintsFacade = new ConstraintsInMP(this, constraintsArrayList);
	}

	@Override
	public String getName() {
		return mpName;
	}

	@Override
	public List<Constraint> getConstraints() {
		return constraintsFacade;
	}

	@Override
	public boolean containsVariable(String description) {
		return descrToVar.containsKey(description);
	}

	@Override
	public Variable getVariable(String description) {
		checkArgument(descrToVar.containsKey(description));
		return descrToVar.get(description);
	}

	/**
	 * Returns a writable list of variables in this MP. The returned list will allow
	 * removal of a variable only if it is not used in any constraint or objective
	 * function. The returned list reads-through this object, thus it changes for
	 * example when a constraint is added to this MP.
	 */
	@Override
	public VariablesInMP getVariables() {
		return variablesFacade;
	}

	@Override
	public Objective getObjective() {
		return objective;
	}

	@Override
	public MPDimension getDimension() {
		return MPDimension.of(varCount, getConstraints().size());
	}

	/**
	 * Removes all the variables and constraints, objective function, name set in
	 * this MP. As a result of this call, this MP has the same visible state as a
	 * newly created, empty MP.
	 */
	public void clear() {
		mpName = "";
		variables.clear();
		descrToVar.clear();
		varCount.clear();
		constraints.clear();
		objective = Objective.ZERO;
	}

	/**
	 * Sets or removes the name of this MP.
	 *
	 * @param name <code>null</code> or empty string for no name. A
	 *             <code>null</code> string is converted to an empty string.
	 * @return this object.
	 *
	 */
	public MPBuilder setName(String name) {
		final String newName = Strings.nullToEmpty(name);
		this.mpName = newName;
		return this;
	}

	/**
	 * Appends the specified variable to the end of the list of variables contained
	 * in this MP.
	 *
	 * @param variable not <code>null</code>, may not already exist in this list.
	 * @return this object.
	 */
	public MPBuilder addVariable(Variable variable) {
		getVariables().add(variable);
		return this;
	}

	/**
	 * Removes the specified variable from this MP, if it is present.
	 *
	 * @param variable not <code>null</code>, must not be referred to by any
	 *                 constraint in this MP or by the objective.
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff the given variable was not in this MP.
	 */
	boolean removeVariable(Variable variable) {
		requireNonNull(variable);
		if (!descrToVar.containsValue(variable)) {
			return false;
		}

		final SumTerms objectiveFunction = objective.getFunction();
		if (objectiveFunction.getVariables().contains(variable)) {
			throw new IllegalArgumentException(
					"Can’t remove " + variable + " used in objective function " + objectiveFunction + ".");
		}
		final List<Constraint> constraintsUsingVariable = constraints.stream()
				.filter(c -> c.getLhs().getVariables().contains(variable)).collect(Collectors.toList());
		if (!constraintsUsingVariable.isEmpty()) {
			throw new IllegalArgumentException(
					"Can’t remove " + variable + " used in constraints: " + constraintsUsingVariable + ".");
		}

		final boolean removed = variables.remove(variable);
		assert removed;
		final String removedDescr = descrToVar.inverse().remove(variable);
		assert removedDescr != null;
		final boolean removedKind = varCount.remove(variable.getKind());
		assert removedKind;
		return true;
	}

	private boolean putVariables(SumTerms sumTerms) {
		boolean added = false;
		for (Term term : sumTerms) {
			final Variable variable = term.getVariable();
			final boolean nowAdded = putVariable(variables.size(), variable, false);
			added = added || nowAdded;
		}
		return added;
	}

	/**
	 * Adds the variable to this MP if it is not already in.
	 *
	 * @param index     an appropriate index.
	 * @param variable  not <code>null</code>.
	 * @param expectNew <code>true</code> to ensure that the variable is added
	 *                  (throws an exception if the variable exists already),
	 *                  <code>false</code> to do nothing when the variable exists
	 *                  already.
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff the given variable was already in this MP.
	 */
	boolean putVariable(int index, Variable variable, boolean expectNew) {
		checkPositionIndex(index, variables.size());
		requireNonNull(variable);
		final String descr = variable.getDescription();
		requireNonNull(descr);

		final boolean hasDescr = descrToVar.containsKey(descr);
		final boolean hasVar = descrToVar.containsValue(variable);
		/** We know: hasVar ⇒ hasDescr. */
		assert !hasVar || hasDescr;
		/**
		 * We want to check: hasDescr ⇒ hasVar, otherwise, already has descr but no
		 * corresponding variable.
		 */
		checkArgument(!hasDescr || hasVar, "This MP already contains the variable '" + descrToVar.get(descr)
				+ "'. It is forbidden to add a different variable with the same description: '" + variable + "'.");
		assert hasVar == hasDescr;

		if (hasDescr && !expectNew) {
			return false;
		}
		if (hasDescr) {
			assert expectNew;
			throw new IllegalArgumentException(String.format("Variable %s already exists.", descr));
		}

		descrToVar.put(descr, variable);
		varCount.add(variable.getKind());
		variables.add(index, variable);

		return true;
	}

	/**
	 * Appends the specified constraint to the end of the list of constraints
	 * contained in this MP.
	 *
	 * @param constraint not <code>null</code>.
	 * @return this object.
	 */
	public MPBuilder addConstraint(Constraint constraint) {
		getConstraints().add(constraint);
		return this;
	}

	/**
	 * Adds the constraint to this MP.
	 *
	 * @param index      an appropriate index.
	 * @param constraint not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff the given constraint was already in this MP.
	 */
	boolean putConstraint(int index, Constraint constraint) {
		checkPositionIndex(index, constraints.size());
		requireNonNull(constraint);

		final SumTerms sumTerms = constraint.getLhs();
		final boolean addedV = putVariables(sumTerms);
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
	 * <p>
	 * Sets or removes the objective bound to this MP. The variables used in the
	 * objective function are added to this MP, if not present already, in the order
	 * they are found in the given objective function.
	 * </p>
	 * <p>
	 * If set to <code>null</code>, the objective of this MP is replaced by the
	 * {@link Objective#ZERO ZERO} objective.
	 * </p>
	 *
	 * @param objective <code>null</code> or (preferably) {@link Objective#ZERO
	 *                  ZERO} to remove a possibly set objective.
	 */
	public void setObjective(Objective objective) {
		final Objective effObj;
		if (objective == null) {
			effObj = ZERO;
		} else {
			effObj = objective;
		}
		putVariables(effObj.getFunction());
		assert effObj != null;
		this.objective = effObj;
	}

	/**
	 * Returns an immutable MP that contains the data currently in this MP.
	 *
	 * @return not <code>null</code>.
	 */
	public MP build() {
		return MP.copyOf(this);
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof IMP)) {
			return false;
		}
		if (o2 == this) {
			return true;
		}
		final IMP p2 = (IMP) o2;
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
	public int hashCode() {
		return Objects.hash(mpName, getVariables(), getConstraints(), getObjective());
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.add("name", mpName);
		helper.addValue(objective);
		helper.addValue(getDimension());
		helper.add("variables", variables);
		helper.add("constraints", constraints);
		return helper.toString();
	}

}
