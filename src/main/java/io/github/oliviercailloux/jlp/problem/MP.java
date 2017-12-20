package io.github.oliviercailloux.jlp.problem;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.ObjectiveFunction;
import io.github.oliviercailloux.jlp.elements.OptimizationDirection;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableType;
import io.github.oliviercailloux.jlp.utils.SolverUtils;

/**
 * A simple mutable implementation of {@link IMP}.
 *
 * @author Olivier Cailloux
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
		mp.setObjective(source.getObjective().getFunction(), source.getObjective().getDirection());
		for (Constraint constraint : source.getConstraints()) {
			mp.add(constraint);
		}

		return mp;
	}

	static public MP create() {
		return new MP();
	}

	public Function<Variable, String> TO_STRING_NAMER = Variable::toString;

	private final Set<Constraint> constraints = Sets.newLinkedHashSet();

	private BiMap<String, Variable> descrToVar;

	/**
	 * Never <code>null</code>.
	 */
	private String mpName;

	private SumTerms objectiveFunction;

	private OptimizationDirection optType;

	private final Multiset<VariableType> varCount = EnumMultiset.create(VariableType.class);

	public MP() {
		mpName = "";
		objectiveFunction = null;
		optType = null;
		final HashBiMap<String, Variable> b = HashBiMap.create();
		descrToVar = b;
	}

	/**
	 * Adds a constraint, or does nothing if the given constraint is already in the
	 * problem. The variables used in the objective are added to this problem.
	 *
	 * @param constraint
	 *            the constraint to be added. Not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object.
	 *         Equivalently, returns <code>false</code> iff the given constraint
	 *         already was in the problem.
	 */
	public boolean add(Constraint constraint) {
		Preconditions.checkNotNull(constraint);
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
	 * Adds the variable to this problem if it is not already in.
	 *
	 * @param variable
	 *            not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object.
	 */
	public boolean addVariable(Variable variable) {
		requireNonNull(variable);
		final String descr = variable.toString();
		requireNonNull(descr);
		if (descrToVar.containsKey(descr)) {
			return false;
		}
		descrToVar.put(descr, variable);
		varCount.add(variable.getType());
		return true;
	}

	/**
	 * Removes all the variables and constraints, objective function, name set in
	 * this problem. As a result of this call, this problem has the same visible
	 * state as a newly created, empty problem.
	 */
	public void clear() {
		mpName = "";
		objectiveFunction = null;
		optType = null;
		constraints.clear();
		varCount.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IMP)) {
			return false;
		}
		IMP p2 = (IMP) obj;
		return SolverUtils.equivalent(this, p2);
	}

	@Override
	public Set<Constraint> getConstraints() {
		return Collections.unmodifiableSet(constraints);
	}

	@Override
	public MPDimension getDimension() {
		return MPDimension.of(varCount.count(VariableType.BOOL), varCount.count(VariableType.INT),
				varCount.count(VariableType.REAL), getConstraints().size());
	}

	@Override
	public String getName() {
		return mpName;
	}

	@Override
	public ObjectiveFunction getObjective() {
		return ObjectiveFunction.of(objectiveFunction, optType);
	}

	@Override
	public Optional<Variable> getVariable(String description) {
		return Optional.ofNullable(descrToVar.get(description));
	}

	@Override
	public Set<Variable> getVariables() {
		/**
		 * TODO add getVarByName! (So that the problem is self-contained: I get a
		 * problem, I want to add a cstr including vars x and y, how? Better: how to
		 * retrieve a namedVar according to its name and refs? Better: change variable
		 * to namedvariable, because anyway using own domain objects as vars would be
		 * inappropriate as domain objects do not have bounds or domains.
		 *
		 * We could also use Guava’s interner, but we probably need to wait that it gets
		 * richer.
		 */
		return Collections.unmodifiableSet(descrToVar.values());
	}

	@Override
	public int hashCode() {
		return SolverUtils.getProblemEquivalence().hash(this);
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
	 * @return <code>true</code> iff the call modified the state of this object.
	 */
	public boolean setObjective(SumTerms objectiveFunction, OptimizationDirection direction) {
		final boolean equivFct = Equivalence.equals().equivalent(this.objectiveFunction, objectiveFunction);
		if (!equivFct) {
			if (objectiveFunction != null) {
				addVariables(objectiveFunction);
			}
			this.objectiveFunction = objectiveFunction;
		}
		final boolean equalDirs = optType == direction;
		optType = direction;
		return !equivFct || !equalDirs;
	}

	/**
	 * Sets or removes the optimization direction. The objective function itself
	 * (without direction), if set, is unchanged: only the direction possibly
	 * changes.
	 *
	 * @param dir
	 *            <code>null</code> to remove, if set, the optimization direction
	 *            information.
	 * @return <code>true</code> iff the call modified the state of this object.
	 */
	public boolean setObjectiveDirection(OptimizationDirection optType) {
		final boolean equalDir = this.optType == optType;
		this.optType = optType;
		return !equalDir;
	}

	/**
	 * Retrieves a long description, with line breaks, of this problem.
	 *
	 * @return not <code>null</code>, not empty.
	 */
	public String toLongDescription() {
		String N = System.getProperty("line.separator");
		final String name = getName().equals("") ? "" : " " + getName();
		String s = "Problem" + name + N;

		if (!getObjective().isEmpty()) {
			s += getObjective().getDirection() + N;
			s += " " + getObjective().getFunction() + N;
		} else {
			s += "Find one solution" + N;
		}
		s += "Subject To" + N;
		for (Constraint constraint : getConstraints()) {
			s += "\t" + constraint + N;
		}
		s += "Bounds" + N;
		for (Variable variable : getVariables()) {
			final double lb = variable.getLowerBound();
			final double ub = variable.getUpperBound();

			if (lb != Double.NEGATIVE_INFINITY || ub != Double.POSITIVE_INFINITY) {
				s += "\t";
				if (lb != Double.NEGATIVE_INFINITY) {
					s += lb + " ≤ ";
				}
				s += variable;
				if (ub != Double.POSITIVE_INFINITY) {
					s += " ≤ " + ub;
				}
				s += N;
			}
		}

		s += "Variables" + N;
		for (Variable variable : getVariables()) {
			s += "\t" + variable + " " + variable.getType() + N;
		}

		return s;

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
