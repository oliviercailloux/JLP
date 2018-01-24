package io.github.oliviercailloux.jlp.mp;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.oliviercailloux.jlp.elements.Objective.ZERO;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * This object forbids adding two different variables with the same description
 * (e.g., "x" int and "x" real).
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
	 * @param source
	 *            not <code>null</code>.
	 */
	static public MPBuilder copyOf(IMP source) {
		requireNonNull(source);

		final MPBuilder mp = new MPBuilder();
		mp.setName(source.getName());
		mp.setObjective(source.getObjective());
		mp.getVariables().addAll(source.getVariables());
		for (Constraint constraint : source.getConstraints()) {
			mp.add(constraint);
		}

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

	private final List<Constraint> constraints = new ArrayList<>();

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
	}

	/**
	 * Adds a constraint, or does nothing if the given constraint is already in this
	 * MP. The variables used in the constraint are added to this MP if not present
	 * already, in the order they are found in the given constraint.
	 *
	 * @param constraint
	 *            not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff the given constraint was already in this MP.
	 */
	public boolean add(Constraint constraint) {
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
	 * Returns an immutable MP that contains the data currently in this MP.
	 *
	 * @return not <code>null</code>.
	 */
	public MP build() {
		return MP.copyOf(this);
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
	public VariablesInMP getVariables() {
		return variablesFacade;
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
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff this MP name was already equal to the given
	 *         name.
	 *
	 */
	public boolean setName(String name) {
		final String newName = Strings.nullToEmpty(name);
		if (mpName.equals(newName)) {
			return false;
		}
		this.mpName = name;
		return true;
	}

	/**
	 * <p>
	 * Sets or removes the objective bound to this problem. The variables used in
	 * the objective function are added to this MP, if not present already, in the
	 * order they are found in the given objective function.
	 * </p>
	 * <p>
	 * If set to <code>null</code>, the objective of this MP is replaced by the
	 * {@link Objective#ZERO ZERO} objective.
	 * </p>
	 *
	 * @param objective
	 *            <code>null</code> or (preferably) {@link Objective#ZERO ZERO} to
	 *            remove a possibly set objective.
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

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.add("name", mpName);
		helper.addValue(objective);
		helper.addValue(getDimension());
		return helper.toString();
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
	 * @param index
	 *            an appropriate index.
	 * @param variable
	 *            not <code>null</code>.
	 * @param expectNew
	 *            <code>true</code> to ensure that the variable is added (throws an
	 *            exception if the variable exists already), <code>false</code> to
	 *            silently do nothing when the variable exists already.
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff the given variable was already in this MP.
	 */
	boolean putVariable(int index, Variable variable, boolean expectNew) {
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
		} else if (hasDescr && expectNew) {
			throw new IllegalArgumentException("Variable already exists.");
		}

		descrToVar.put(descr, variable);
		varCount.add(variable.getKind());
		variables.add(index, variable);

		return true;
	}

	/**
	 * Removes the specified variable from this MP, if it is present.
	 *
	 * @param variable
	 *            not <code>null</code>, must not be referred to by any constraint
	 *            in this MP or by the objective.
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
				.filter((c) -> c.getLhs().getVariables().contains(variable)).collect(Collectors.toList());
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

}
