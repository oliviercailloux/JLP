package io.github.oliviercailloux.jlp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.OptimizationDirection;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsImmutable;
import io.github.oliviercailloux.jlp.elements.ObjectiveFunction;
import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableType;
import io.github.oliviercailloux.jlp.problem.MPs.DefaultConstraintsNamer;
import io.github.oliviercailloux.jlp.utils.SolverUtils;

/**
 * A simple mutable implementation of {@link MP}.
 *
 * @author Olivier Cailloux
 *
 */
class MPImpl implements MP {

	private final Set<Constraint> constraints = Sets.newLinkedHashSet();

	private Function<Constraint, String> constraintsNamer;

	private final DefaultConstraintsNamer defaultConstraintsNamer = new DefaultConstraintsNamer();

	/**
	 * Never <code>null</code>.
	 */
	private String name;

	private SumTerms objectiveFunction;

	private OptimizationDirection optType;

	private final Multiset<VariableType> varCount = EnumMultiset.create(VariableType.class);

	/**
	 * Missing entries correspond to minus infinity bound.
	 */
	private final Map<Variable, Number> varLowerBound = Maps.newHashMap();

	/**
	 * Never <code>null</code>.
	 */
	private Function<Variable, String> varNamer;

	/**
	 * Contains no <code>null</code> keys, no <code>null</code> values, no empty
	 * string values.
	 */
	private final Map<Variable, String> varNames = Maps.newHashMap();

	/**
	 * No <code>null</code> key or value. Each variable in this problem has a type,
	 * thus this map contains all the variables in the problem.
	 */
	private final Map<Variable, VariableType> varType = Maps.newLinkedHashMap();

	/**
	 * Missing entries correspond to positive infinity bound.
	 */
	private final Map<Variable, Number> varUpperBound = Maps.newHashMap();

	public MPImpl() {
		name = "";
		objectiveFunction = null;
		optType = null;
		varNamer = MP.TO_STRING_NAMER;
		constraintsNamer = defaultConstraintsNamer;
	}

	/**
	 * A copy constructor, by value. No reference is shared between the new problem
	 * and the given one.
	 *
	 * @param problem
	 *            not <code>null</code>.
	 */
	public MPImpl(MP problem) {
		Preconditions.checkNotNull(problem);
		MPs.copyTo(problem, this);
	}

	@Override
	public boolean add(Constraint constraint) {
		return addInternal(constraint);
	}

	@Override
	public boolean add(String id, SumTerms lhs, ComparisonOperator operator, double rhs) {
		Preconditions.checkNotNull(lhs, "" + operator + rhs);
		Preconditions.checkNotNull(operator, "" + lhs + rhs);
		Preconditions.checkArgument(!Double.isNaN(rhs) && !Double.isInfinite(rhs));
		final Constraint constraint = new Constraint(id, lhs, operator, rhs);
		return addInternal(constraint);
	}

	@Override
	public boolean addVariable(Variable variable) {
		if (varType.containsKey(variable)) {
			return false;
		}
		setVarTypeInternal(variable, VariableType.REAL);
		varLowerBound.put(variable, Double.valueOf(0));
		return true;
	}

	@Override
	public void clear() {
		name = "";
		setConstraintsNamer(null);
		setVariablesNamer(null);
		objectiveFunction = null;
		optType = null;
		constraints.clear();
		varCount.clear();
		varNames.clear();
		varType.clear();
		varLowerBound.clear();
		varUpperBound.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MP)) {
			return false;
		}
		MP p2 = (MP) obj;
		return SolverUtils.equivalent(this, p2);
	}

	@Override
	public Set<Constraint> getConstraints() {
		return Collections.unmodifiableSet(constraints);
	}

	@Override
	public Function<Constraint, String> getConstraintsNamer() {
		return constraintsNamer;
	}

	@Override
	public MPDimension getDimension() {
		return new MPDimension(varCount.count(VariableType.BOOL), varCount.count(VariableType.INT),
				varCount.count(VariableType.REAL), getConstraints().size());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ObjectiveFunction getObjective() {
		return new ObjectiveFunction(objectiveFunction, optType);
	}

	@Override
	public Number getVariableLowerBound(Variable variable) {
		checkArgument(varType.containsKey(variable));
		return Objects.firstNonNull(varLowerBound.get(variable), Double.valueOf(Double.NEGATIVE_INFINITY));
	}

	@Override
	public String getVariableName(Variable variable) {
		return Strings.nullToEmpty(varNamer.apply(variable));
	}

	@Override
	public Set<Variable> getVariables() {
		/**
		 * TODO add getVarByName! (So that the problem is self-contained: I get a
		 * problem, I want to add a cstr including vars x and y, how? Better: how to
		 * retrieve a namedVar according to its name and refs? Better: change variable
		 * to namedvariable, because anyway using own domain objects as vars would be
		 * inappropriate as domain objects do not have bounds or domains.
		 */
		return Collections.unmodifiableSet(varType.keySet());
	}

	@Override
	public Function<Variable, String> getVariablesNamer() {
		return varNamer;
	}

	@Override
	public VariableType getVariableType(Variable variable) {
		Preconditions.checkArgument(varType.containsKey(variable));
		return varType.get(variable);
	}

	@Override
	public Number getVariableUpperBound(Variable variable) {
		checkArgument(varType.containsKey(variable));
		return Objects.firstNonNull(varUpperBound.get(variable), Double.valueOf(Double.POSITIVE_INFINITY));
	}

	@Override
	public int hashCode() {
		return SolverUtils.getProblemEquivalence().hash(this);
	}

	@Override
	public void setConstraintsNamer(Function<Constraint, String> namer) {
		if (namer == null) {
			constraintsNamer = defaultConstraintsNamer;
		} else {
			constraintsNamer = namer;
		}
	}

	@Override
	public boolean setName(String name) {
		final String newName;
		if (name == null) {
			newName = "";
		} else {
			newName = name;
		}
		final boolean equivalent = Equivalence.equals().equivalent(this.name, newName);
		if (equivalent) {
			return false;
		}
		this.name = name;
		return true;
	}

	@Override
	public boolean setObjective(SumTerms objectiveFunction, OptimizationDirection direction) {
		final boolean equivFct = Equivalence.equals().equivalent(this.objectiveFunction, objectiveFunction);
		if (!equivFct) {
			if (objectiveFunction == null) {
				this.objectiveFunction = null;
			} else {
				assertVariablesExist(objectiveFunction);
				this.objectiveFunction = new SumTermsImmutable(objectiveFunction);
			}
		}
		final boolean equalDirs = optType == direction;
		optType = direction;
		return !equivFct || !equalDirs;
	}

	@Override
	public boolean setObjectiveDirection(OptimizationDirection optType) {
		final boolean equalDir = this.optType == optType;
		this.optType = optType;
		return !equalDir;
	}

	@Override
	public boolean setVariableBounds(Variable variable, Number lowerBound, Number upperBound) {
		checkNotNull(variable, "" + lowerBound + "; " + upperBound);
		final Number newLower = lowerBound == null ? Double.valueOf(Double.NEGATIVE_INFINITY) : lowerBound;
		final Number newUpper = upperBound == null ? Double.valueOf(Double.POSITIVE_INFINITY) : upperBound;
		checkArgument(newLower.doubleValue() <= newUpper.doubleValue(),
				"Lower bound: " + newLower + " must be less or equal to upper bound: " + newUpper + ".");

		final boolean added;
		if (varType.containsKey(variable)) {
			added = false;
		} else {
			setVarTypeInternal(variable, VariableType.REAL);
			added = true;
		}

		final Number previousLower;
		if (newLower.equals(Double.valueOf(Double.NEGATIVE_INFINITY))) {
			previousLower = varLowerBound.remove(variable);
		} else {
			previousLower = varLowerBound.put(variable, lowerBound);
		}
		final boolean changedLower = (previousLower == null
				&& newLower.equals(Double.valueOf(Double.NEGATIVE_INFINITY))) || Objects.equal(previousLower, newLower);

		final Number previousUpper;
		if (newUpper.equals(Double.valueOf(Double.POSITIVE_INFINITY))) {
			previousUpper = varUpperBound.remove(variable);
		} else {
			previousUpper = varUpperBound.put(variable, upperBound);
		}
		final boolean changedUpper = (previousUpper == null
				&& newUpper.equals(Double.valueOf(Double.POSITIVE_INFINITY))) || Objects.equal(previousUpper, newUpper);

		return added || changedLower || changedUpper;
	}

	@Override
	public void setVariablesNamer(Function<Variable, String> namer) {
		if (namer == null) {
			varNamer = TO_STRING_NAMER;
		} else {
			varNamer = namer;
		}
	}

	@Override
	public boolean setVariableType(Variable variable, VariableType type) {
		checkNotNull(variable, type);
		checkNotNull(type, variable);
		final VariableType previous = setVarTypeInternal(variable, type);
		if (previous == null) {
			varLowerBound.put(variable, Double.valueOf(0));
		}
		return previous == null || previous != type;
	}

	public boolean setVarNameOld(Variable variable, String name) {
		Preconditions.checkNotNull(variable, "" + name);
		final boolean added = addVariable(variable);

		final String previous;
		final boolean changed;
		if (name == null || name.isEmpty()) {
			previous = varNames.remove(variable);
			changed = previous != null;
		} else {
			previous = varNames.put(variable, name);
			changed = !name.equals(previous);
		}
		return added || changed;
	}

	@Override
	public String toString() {
		return SolverUtils.getAsString(this);
	}

	/**
	 * NB no defensive copy of the given constraint is done. Adds a constraint, or
	 * does nothing if the given constraint is already in the problem. The variables
	 * used in the objective must have been added to this problem already.
	 *
	 * @param constraint
	 *            the constraint to be added. Not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object.
	 *         Equivalently, returns <code>false</code> iff the given constraint
	 *         already was in the problem.
	 */
	private boolean addInternal(Constraint constraint) {
		Preconditions.checkNotNull(constraint);
		for (Term term : constraint.getLhs()) {
			final Variable variable = term.getVariable();
			if (!varType.containsKey(variable)) {
				// setVarTypeInternal(variable, IlpVariableType.REAL);
				throw new IllegalArgumentException(
						"Unknown variable: " + variable + " in constraint " + constraint + ".");
			}
		}
		return constraints.add(constraint);
	}

	private void assertVariablesExist(SumTerms linear) {
		for (Term term : linear) {
			final Variable variable = term.getVariable();
			Preconditions.checkArgument(varType.containsKey(variable));
		}
	}

	private VariableType setVarTypeInternal(Variable variable, VariableType type) {
		Preconditions.checkNotNull(type);
		final VariableType previous = varType.put(variable, type);
		if (previous != null && previous != type) {
			final boolean removed = varCount.remove(previous);
			assert removed;
		}
		if (previous == null || previous != type) {
			varCount.add(type);
		}
		return previous;
	}

}
