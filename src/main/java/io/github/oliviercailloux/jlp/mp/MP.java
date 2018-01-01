package io.github.oliviercailloux.jlp.mp;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.oliviercailloux.jlp.elements.Objective.ZERO;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
 * A modifiable mathematical program.
 *
 * This object forbids adding two different variables with the same description
 * (e.g., "x" int and "x" real).
 *
 * @author Olivier Cailloux
 * @see IMP
 * @see Variable
 *
 */
public class MP implements IMP {

	/**
	 * Returns an MP that is a copy of the given source, in the sense that it
	 * contains the same data, but is not linked to the source, in the sense that
	 * modifying the resulting MP will not change the source. (Variables are shared
	 * however, so this is only true if the variables are immutable.)
	 *
	 * @param source
	 *            not <code>null</code>.
	 */
	static public MP copyOf(IMP source) {
		requireNonNull(source);

		final MP mp = new MP();
		mp.setName(source.getName());
		mp.setObjective(source.getObjective());
		for (Variable variable : source.getVariables()) {
			mp.addVariable(variable);
		}
		for (Constraint constraint : source.getConstraints()) {
			mp.add(constraint);
		}

		return mp;
	}

	/**
	 * Returns an empty MP with an empty name and the {@link Objective#ZERO ZERO}
	 * objective.
	 *
	 * @return a new writable MP.
	 */
	static public MP create() {
		return new MP();
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

	private final List<Variable> variables = new ArrayList<>();

	private MP() {
		mpName = "";
		objective = Objective.ZERO;
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
		final String descr = variable.getDescription();
		requireNonNull(descr);

		final boolean hasDescr = descrToVar.containsKey(descr);
		final boolean hasVar = variables.contains(variable);
		/** We know: hasVar ⇒ hasDescr. */
		assert !hasVar || hasDescr;
		/**
		 * We want to check: hasDescr ⇒ hasVar, otherwise, already has descr but no
		 * corresponding variable.
		 */
		checkArgument(!hasDescr || hasVar, "This MP already contains the variable '" + descrToVar.get(descr)
				+ "'. It is forbidden to add a different variable with the same description: '" + variable + "'.");
		assert hasVar == hasDescr;

		if (hasVar) {
			return false;
		}

		descrToVar.put(descr, variable);
		varCount.add(variable.getKind());
		variables.add(variable);

		return true;
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
		addVariables(effObj.getFunction());
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
