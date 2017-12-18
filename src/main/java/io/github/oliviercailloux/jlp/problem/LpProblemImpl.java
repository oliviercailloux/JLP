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

import io.github.oliviercailloux.jlp.elements.LpConstraint;
import io.github.oliviercailloux.jlp.elements.LpDirection;
import io.github.oliviercailloux.jlp.elements.LpLinear;
import io.github.oliviercailloux.jlp.elements.LpLinearImmutable;
import io.github.oliviercailloux.jlp.elements.LpObjective;
import io.github.oliviercailloux.jlp.elements.LpOperator;
import io.github.oliviercailloux.jlp.elements.LpTerm;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.problem.LpProblems.DefaultConstraintsNamer;
import io.github.oliviercailloux.jlp.utils.LpSolverUtils;

/**
 * A simple mutable implementation of {@link LpProblem}.
 *
 * @author Olivier Cailloux
 *
 */
class LpProblemImpl implements LpProblem {

	private final Set<LpConstraint> m_constraints = Sets.newLinkedHashSet();

	private Function<LpConstraint, String> m_constraintsNamer;

	private final DefaultConstraintsNamer m_defaultConstraintsNamer = new DefaultConstraintsNamer();

	/**
	 * Never <code>null</code>.
	 */
	private String m_name;

	private LpLinear m_objectiveFunction;

	private LpDirection m_optType;

	private final Multiset<LpVariableType> m_varCount = EnumMultiset.create(LpVariableType.class);

	/**
	 * Missing entries correspond to minus infinity bound.
	 */
	private final Map<Variable, Number> m_varLowerBound = Maps.newHashMap();

	/**
	 * Never <code>null</code>.
	 */
	private Function<Variable, String> m_varNamer;

	/**
	 * Contains no <code>null</code> keys, no <code>null</code> values, no empty
	 * string values.
	 */
	private final Map<Variable, String> m_varNames = Maps.newHashMap();

	/**
	 * No <code>null</code> key or value. Each variable in this problem has a type,
	 * thus this map contains all the variables in the problem.
	 */
	private final Map<Variable, LpVariableType> m_varType = Maps.newLinkedHashMap();

	/**
	 * Missing entries correspond to positive infinity bound.
	 */
	private final Map<Variable, Number> m_varUpperBound = Maps.newHashMap();

	public LpProblemImpl() {
		m_name = "";
		m_objectiveFunction = null;
		m_optType = null;
		m_varNamer = LpProblem.TO_STRING_NAMER;
		m_constraintsNamer = m_defaultConstraintsNamer;
	}

	/**
	 * A copy constructor, by value. No reference is shared between the new problem
	 * and the given one.
	 *
	 * @param problem
	 *            not <code>null</code>.
	 */
	public LpProblemImpl(LpProblem problem) {
		Preconditions.checkNotNull(problem);
		LpProblems.copyTo(problem, this);
	}

	@Override
	public boolean add(LpConstraint constraint) {
		return addInternal(constraint);
	}

	@Override
	public boolean add(String id, LpLinear lhs, LpOperator operator, double rhs) {
		Preconditions.checkNotNull(lhs, "" + operator + rhs);
		Preconditions.checkNotNull(operator, "" + lhs + rhs);
		Preconditions.checkArgument(!Double.isNaN(rhs) && !Double.isInfinite(rhs));
		final LpConstraint constraint = new LpConstraint(id, lhs, operator, rhs);
		return addInternal(constraint);
	}

	@Override
	public boolean addVariable(Variable variable) {
		if (m_varType.containsKey(variable)) {
			return false;
		}
		setVarTypeInternal(variable, LpVariableType.REAL);
		m_varLowerBound.put(variable, Double.valueOf(0));
		return true;
	}

	@Override
	public void clear() {
		m_name = "";
		setConstraintsNamer(null);
		setVariablesNamer(null);
		m_objectiveFunction = null;
		m_optType = null;
		m_constraints.clear();
		m_varCount.clear();
		m_varNames.clear();
		m_varType.clear();
		m_varLowerBound.clear();
		m_varUpperBound.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LpProblem)) {
			return false;
		}
		LpProblem p2 = (LpProblem) obj;
		return LpSolverUtils.equivalent(this, p2);
	}

	@Override
	public Set<LpConstraint> getConstraints() {
		return Collections.unmodifiableSet(m_constraints);
	}

	@Override
	public Function<LpConstraint, String> getConstraintsNamer() {
		return m_constraintsNamer;
	}

	@Override
	public LpDimension getDimension() {
		return new LpDimension(m_varCount.count(LpVariableType.BOOL), m_varCount.count(LpVariableType.INT),
				m_varCount.count(LpVariableType.REAL), getConstraints().size());
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public LpObjective getObjective() {
		return new LpObjective(m_objectiveFunction, m_optType);
	}

	@Override
	public Number getVariableLowerBound(Variable variable) {
		checkArgument(m_varType.containsKey(variable));
		return Objects.firstNonNull(m_varLowerBound.get(variable), Double.valueOf(Double.NEGATIVE_INFINITY));
	}

	@Override
	public String getVariableName(Variable variable) {
		return Strings.nullToEmpty(m_varNamer.apply(variable));
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
		return Collections.unmodifiableSet(m_varType.keySet());
	}

	@Override
	public Function<Variable, String> getVariablesNamer() {
		return m_varNamer;
	}

	@Override
	public LpVariableType getVariableType(Variable variable) {
		Preconditions.checkArgument(m_varType.containsKey(variable));
		return m_varType.get(variable);
	}

	@Override
	public Number getVariableUpperBound(Variable variable) {
		checkArgument(m_varType.containsKey(variable));
		return Objects.firstNonNull(m_varUpperBound.get(variable), Double.valueOf(Double.POSITIVE_INFINITY));
	}

	@Override
	public int hashCode() {
		return LpSolverUtils.getProblemEquivalence().hash(this);
	}

	@Override
	public void setConstraintsNamer(Function<LpConstraint, String> namer) {
		if (namer == null) {
			m_constraintsNamer = m_defaultConstraintsNamer;
		} else {
			m_constraintsNamer = namer;
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
		final boolean equivalent = Equivalence.equals().equivalent(m_name, newName);
		if (equivalent) {
			return false;
		}
		m_name = name;
		return true;
	}

	@Override
	public boolean setObjective(LpLinear objectiveFunction, LpDirection direction) {
		final boolean equivFct = Equivalence.equals().equivalent(m_objectiveFunction, objectiveFunction);
		if (!equivFct) {
			if (objectiveFunction == null) {
				m_objectiveFunction = null;
			} else {
				assertVariablesExist(objectiveFunction);
				m_objectiveFunction = new LpLinearImmutable(objectiveFunction);
			}
		}
		final boolean equalDirs = m_optType == direction;
		m_optType = direction;
		return !equivFct || !equalDirs;
	}

	@Override
	public boolean setObjectiveDirection(LpDirection optType) {
		final boolean equalDir = m_optType == optType;
		m_optType = optType;
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
		if (m_varType.containsKey(variable)) {
			added = false;
		} else {
			setVarTypeInternal(variable, LpVariableType.REAL);
			added = true;
		}

		final Number previousLower;
		if (newLower.equals(Double.valueOf(Double.NEGATIVE_INFINITY))) {
			previousLower = m_varLowerBound.remove(variable);
		} else {
			previousLower = m_varLowerBound.put(variable, lowerBound);
		}
		final boolean changedLower = (previousLower == null
				&& newLower.equals(Double.valueOf(Double.NEGATIVE_INFINITY))) || Objects.equal(previousLower, newLower);

		final Number previousUpper;
		if (newUpper.equals(Double.valueOf(Double.POSITIVE_INFINITY))) {
			previousUpper = m_varUpperBound.remove(variable);
		} else {
			previousUpper = m_varUpperBound.put(variable, upperBound);
		}
		final boolean changedUpper = (previousUpper == null
				&& newUpper.equals(Double.valueOf(Double.POSITIVE_INFINITY))) || Objects.equal(previousUpper, newUpper);

		return added || changedLower || changedUpper;
	}

	@Override
	public void setVariablesNamer(Function<Variable, String> namer) {
		if (namer == null) {
			m_varNamer = TO_STRING_NAMER;
		} else {
			m_varNamer = namer;
		}
	}

	@Override
	public boolean setVariableType(Variable variable, LpVariableType type) {
		checkNotNull(variable, type);
		checkNotNull(type, variable);
		final LpVariableType previous = setVarTypeInternal(variable, type);
		if (previous == null) {
			m_varLowerBound.put(variable, Double.valueOf(0));
		}
		return previous == null || previous != type;
	}

	public boolean setVarNameOld(Variable variable, String name) {
		Preconditions.checkNotNull(variable, "" + name);
		final boolean added = addVariable(variable);

		final String previous;
		final boolean changed;
		if (name == null || name.isEmpty()) {
			previous = m_varNames.remove(variable);
			changed = previous != null;
		} else {
			previous = m_varNames.put(variable, name);
			changed = !name.equals(previous);
		}
		return added || changed;
	}

	@Override
	public String toString() {
		return LpSolverUtils.getAsString(this);
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
	private boolean addInternal(LpConstraint constraint) {
		Preconditions.checkNotNull(constraint);
		for (LpTerm term : constraint.getLhs()) {
			final Variable variable = term.getVariable();
			if (!m_varType.containsKey(variable)) {
				// setVarTypeInternal(variable, IlpVariableType.REAL);
				throw new IllegalArgumentException(
						"Unknown variable: " + variable + " in constraint " + constraint + ".");
			}
		}
		return m_constraints.add(constraint);
	}

	private void assertVariablesExist(LpLinear linear) {
		for (LpTerm term : linear) {
			final Variable variable = term.getVariable();
			Preconditions.checkArgument(m_varType.containsKey(variable));
		}
	}

	private LpVariableType setVarTypeInternal(Variable variable, LpVariableType type) {
		Preconditions.checkNotNull(type);
		final LpVariableType previous = m_varType.put(variable, type);
		if (previous != null && previous != type) {
			final boolean removed = m_varCount.remove(previous);
			assert removed;
		}
		if (previous == null || previous != type) {
			m_varCount.add(type);
		}
		return previous;
	}

}
