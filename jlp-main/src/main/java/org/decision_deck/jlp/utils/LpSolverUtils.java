/**
 * Copyright © 2010-2012 Olivier Cailloux
 *
 * 	This file is part of JLP.
 *
 * 	JLP is free software: you can redistribute it and/or modify it under the
 * 	terms of the GNU Lesser General Public License version 3 as published by
 * 	the Free Software Foundation.
 *
 * 	JLP is distributed in the hope that it will be useful, but WITHOUT ANY
 * 	WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * 	FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * 	more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public License
 * 	along with JLP. If not, see <http://www.gnu.org/licenses/>.
 */
package org.decision_deck.jlp.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpSolverException;
import org.decision_deck.jlp.LpTerm;
import org.decision_deck.jlp.parameters.LpParameters;
import org.decision_deck.jlp.parameters.LpParametersUtils;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.problem.LpProblemWithTransformedBoolsView;
import org.decision_deck.jlp.problem.LpProblems;
import org.decision_deck.jlp.problem.LpVariableType;
import org.decision_deck.jlp.result.LpSolution;
import org.decision_deck.jlp.result.LpSolutionAlone;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalences;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.collect.Iterables;

/**
 * <p>
 * This class defines static methods that should be mainly useful for internal use in this project and to implement
 * underlying solvers.
 * </p>
 * <p>
 * Usage examples: <code>
	    final Set<V> bools = LpProblems.getVariables(solution.getProblem(),
		    LpVariableType.BOOL);
final Predicate<Number> isBool = new IsBoolValue(1e-6);
	    final FunctionGetValue<V> fctGetValue = new LpSolverUtils.FunctionGetValue<V>(
		    solution);
	    final Predicate<V> hasBoolValue = Predicates.compose(isBool,
 fctGetValue);
	    final Set<V> wrong = Sets.filter(bools, Predicates.not(hasBoolValue));
	    if (!wrong.isEmpty()) {
		final SetBackedMap<V, Number> variablesAndValues = new SetBackedMap<V, Number>(
			wrong, fctGetValue);
		throw new IllegalStateException("Found some bool variables with a non-bool value: "
			+ variablesAndValues + ".");
	    }
</code>
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpSolverUtils {

    static public class FunctionGetValue<V> implements Function<V, Number> {
	private final LpSolutionAlone<V> m_solution;

	public FunctionGetValue(LpSolutionAlone<V> solution) {
	    checkNotNull(solution);
	    m_solution = solution;

	}

	@Override
	public Number apply(V input) {
	    return m_solution.getValue(input);
	}
    }

    /**
     * Tests whether the given numbers correspond to boolean values, plus or minus an allowed epsilon value. The
     * predicate is <code>true</code>, for a number n and a positive or nul epsilon value e, iff its double value is in
     * [-e, e] or in [1-e, 1+e].
     * 
     * @author Olivier Cailloux
     * 
     */
    static public class IsBoolValue implements Predicate<Number> {
	private final double m_epsilon;

	public IsBoolValue(double epsilon) {
	    checkArgument(epsilon >= 0);
	    checkArgument(!Double.isInfinite(epsilon));
	    checkArgument(!Double.isNaN(epsilon));
	    m_epsilon = epsilon;
	}

	@Override
	public boolean apply(Number value) {
	    final boolean clean;
	    if (value == null) {
		clean = false;
	    } else {
		final double val = value.doubleValue();
		if (val < -m_epsilon) {
		    clean = false;
		} else if (val > m_epsilon && val < 1 - m_epsilon) {
		    clean = false;
		} else if (val > 1 + m_epsilon) {
		    clean = false;
		} else {
		    clean = true;
		}
	    }
	    return clean;
	}
    }

    /**
     * Ensures that the given parameters are conform to the given mandatory values. That is, for each parameter value
     * that is mandatory, ensures that the given parameters have an associated value (which may be the default value)
     * that is equal to the mandatory value.
     * 
     * @param parameters
     *            not <code>null</code>.
     * @param mandatoryValues
     *            not <code>null</code>, no <code>null</code> key. The values must be meaningful.
     * @throws LpSolverException
     *             if the parameters are not conform.
     */
    static public void assertConform(LpParameters parameters, Map<Enum<?>, Object> mandatoryValues)
	    throws LpSolverException {
	for (Enum<?> parameter : LpParametersUtils.getParameters()) {
	    if (mandatoryValues.containsKey(parameter)) {
		final Object mandatoryValue = mandatoryValues.get(parameter);
		final Object value = parameters.getValueAsObject(parameter);
		if (!Equivalences.equals().equivalent(value, mandatoryValue)) {
		    throw new LpSolverException("Unsupported parameter value: " + parameter + ", " + value + ".");
		}
	    }
	}
    }

    /**
     * Ensures that the given problem represents a zero-one problem, thus that each variable in the problem either has
     * type {@link LpVariableType#BOOL} or has type {@link LpVariableType#INT} with bounds defined between 0 and 1
     * (inclusive).
     * 
     * @param <V>
     *            the type of the variables in the problem.
     * 
     * @param problem
     *            not <code>null</code>.
     * @throws LpSolverException
     *             iff the problem is not zero-one.
     */
    static public <V> void assertIntZeroOne(final LpProblem<V> problem) throws LpSolverException {
	final LpProblem<V> problemNoBool = LpSolverUtils.getViewWithTransformedBools(problem);
	for (V variable : problemNoBool.getVariables()) {
	    LpVariableType type = problemNoBool.getVariableType(variable);
	    if (type == LpVariableType.REAL) {
		throw new LpSolverException("Variable " + variable
			+ " is not an integer variable, this is not a zero-one problem.");
	    }
	    final Number lowerBound = problemNoBool.getVariableLowerBound(variable);
	    final Number upperBound = problemNoBool.getVariableUpperBound(variable);
	    if (lowerBound.doubleValue() < 0d) {
		throw new LpSolverException("Variable " + variable
			+ " has an inadequate lower bound, this is not a zero-one problem.");
	    }
	    if (upperBound.doubleValue() > 1d) {
		throw new LpSolverException("Variable " + variable
			+ " has an inadequate upper bound, this is not a zero-one problem.");
	    }
	}
    }

    static public boolean equivalent(LpConstraint<?> a, LpConstraint<?> b) {
	return getConstraintEquivalence().equivalent(a, b);
    }

    static public boolean equivalent(LpLinear<?> a, LpLinear<?> b) {
	return getLinearEquivalence().equivalent(a, b);
    }

    static public boolean equivalent(LpProblem<?> a, LpProblem<?> b) {
	return getProblemEquivalence().equivalent(a, b);
    }

    static public boolean equivalent(LpSolution<?> a, LpSolution<?> b) {
	return getSolutionEquivalence().equivalent(a, b);
    }

    static public int getAsInteger(double number) throws LpSolverException {
	final long lValue = Math.round(number);
	if (lValue > Integer.MAX_VALUE) {
	    throw new LpSolverException("Number " + number + " does not fit into an integer (too big).");
	}
	final int iValue = (int) lValue;

	if (Math.abs(number - iValue) > 1e-6) {
	    throw new LpSolverException("Number " + number + " does not round to an integer.");
	}

	return iValue;
    }

    /**
     * Provides an implementation of toString for debugging use.
     * 
     * @param <V>
     *            the type of variable.
     * @param constraint
     *            not <code>null</code>.
     * @return a debug description.
     */
    static public <V> String getAsString(LpConstraint<V> constraint) {
	final ToStringHelper helper = Objects.toStringHelper(constraint);
	helper.addValue('\'' + constraint.getIdAsString() + '\'');
	helper.addValue(constraint.getLhs().toString() + constraint.getOperator() + constraint.getRhs());
	return helper.toString();
    }

    /**
     * Provides an implementation of toString for debugging use. For a more user friendly string description, see class
     * {@link LpProblems}.
     * 
     * @param <V>
     *            the type of variable.
     * @param problem
     *            not <code>null</code>.
     * @return a debug description.
     */
    static public <V> String getAsString(LpProblem<V> problem) {
	final ToStringHelper helper = Objects.toStringHelper(problem);
	helper.addValue('\'' + problem.getName() + '\'');
	if (!problem.getObjective().isEmpty()) {
	    helper.addValue("" + problem.getObjective().getDirection() + " " + problem.getObjective().getFunction());
	}
	helper.addValue("" + problem.getVariables().size() + " variables");
	helper.addValue(problem.getConstraints().size() + " constraints");
	return helper.toString();
    }

    static public <V> String getAsString(LpSolution<V> solution) {
	final ToStringHelper helper = Objects.toStringHelper(solution);
	helper.add("Problem", solution.getProblem());
	helper.add("Objective value", solution.getObjectiveValue());
	helper.add("Valued variables size", Integer.valueOf(solution.getVariables().size()));
	return helper.toString();
    }

    static public Equivalence<LpConstraint<?>> getConstraintEquivalence() {
	return new Equivalence<LpConstraint<?>>() {

	    @Override
	    public boolean doEquivalent(LpConstraint<?> a, LpConstraint<?> b) {
		if (a.getRhs() != b.getRhs()) {
		    return false;
		}
		if (!a.getLhs().equals(b.getLhs())) {
		    return false;
		}
		if (!a.getOperator().equals(b.getOperator())) {
		    return false;
		}
		return true;
	    }

	    @Override
	    public int doHash(LpConstraint<?> c) {
		return Objects.hashCode(c.getLhs(), c.getOperator(), Double.valueOf(c.getRhs()));
	    }
	};
    }

    static public Equivalence<Number> getEquivalenceByDoubleValue() {
	return new Equivalence<Number>() {
	    @Override
	    public boolean doEquivalent(Number a, Number b) {
		return a.doubleValue() == b.doubleValue();
	    }

	    @Override
	    public int doHash(Number t) {
		return Double.valueOf(t.doubleValue()).hashCode();
	    }
	};
    }

    static public Equivalence<LpLinear<?>> getLinearEquivalence() {
	return new Equivalence<LpLinear<?>>() {
	    @Override
	    public boolean doEquivalent(LpLinear<?> a, LpLinear<?> b) {
		return Iterables.elementsEqual(a, b);
	    }

	    @Override
	    public int doHash(LpLinear<?> t) {
		int hashCode = 1;
		for (LpTerm<?> term : t) {
		    hashCode = 31 * hashCode + term.hashCode();
		}
		return hashCode;
	    }
	};
    }

    static public Equivalence<LpProblem<?>> getProblemEquivalence() {
	return new Equivalence<LpProblem<? extends Object>>() {
	    @Override
	    public boolean doEquivalent(LpProblem<? extends Object> a, LpProblem<? extends Object> b) {
		return computeEquivalent(a, b);
	    }

	    private <T1, T2> boolean computeEquivalent(LpProblem<T1> a, LpProblem<T2> b) {
		if (!a.getConstraints().equals(b.getConstraints())) {
		    return false;
		}
		if (!Objects.equal(a.getObjective(), b.getObjective())) {
		    return false;
		}
		if (!a.getVariables().equals(b.getVariables())) {
		    return false;
		}
		for (T1 variable : a.getVariables()) {
		    if (!b.getVariables().contains(variable)) {
			return false;
		    }
		    @SuppressWarnings("unchecked")
		    final T2 varTyped = (T2) variable;

		    if (!getEquivalenceByDoubleValue().equivalent(a.getVariableLowerBound(variable),
			    b.getVariableLowerBound(varTyped))) {
			return false;
		    }
		    if (!Objects.equal(a.getVariableType(variable), b.getVariableType(varTyped))) {
			return false;
		    }
		    if (!getEquivalenceByDoubleValue().equivalent(a.getVariableUpperBound(variable),
			    b.getVariableUpperBound(varTyped))) {
			return false;
		    }
		}
		return true;
	    }

	    @Override
	    public int doHash(LpProblem<? extends Object> t) {
		final int hashCode = Objects.hashCode(t.getObjective());
		return hashCode + t.getConstraints().hashCode() + t.getVariables().hashCode();
	    }
	};
    }

    static public Equivalence<LpSolution<?>> getSolutionEquivalence() {
	return new Equivalence<LpSolution<? extends Object>>() {
	    @Override
	    public boolean doEquivalent(LpSolution<? extends Object> a, LpSolution<? extends Object> b) {
		return computeEquivalent(a, b);
	    }

	    private <T1, T2> boolean computeEquivalent(LpSolution<T1> a, LpSolution<T2> b) {
		if (!getEquivalenceByDoubleValue().equivalent(a.getObjectiveValue(), b.getObjectiveValue())) {
		    return false;
		}
		if (!a.getProblem().equals(b.getProblem())) {
		    return false;
		}
		for (T1 variable : a.getVariables()) {
		    if (!b.getVariables().contains(variable)) {
			return false;
		    }
		    @SuppressWarnings("unchecked")
		    final T2 varTyped = (T2) variable;

		    if (!getEquivalenceByDoubleValue().equivalent(a.getValue(variable), b.getValue(varTyped))) {
			return false;
		    }
		}
		for (LpConstraint<T1> constraint : a.getConstraints()) {
		    if (!b.getConstraints().contains(constraint)) {
			return false;
		    }
		    @SuppressWarnings("unchecked")
		    final LpConstraint<T2> constraintTyped = (LpConstraint<T2>) constraint;

		    if (!getEquivalenceByDoubleValue().equivalent(a.getDualValue(constraint),
			    b.getDualValue(constraintTyped))) {
			return false;
		    }
		}
		return true;
	    }

	    @Override
	    public int doHash(LpSolution<? extends Object> t) {
		return computeHash(t);
	    }

	    private <T> int computeHash(LpSolution<T> solution) {
		int hashCode = Objects.hashCode(solution.getProblem(), solution.getProblem());
		for (T variable : solution.getVariables()) {
		    hashCode += solution.getValue(variable).hashCode();
		}
		for (LpConstraint<T> constraint : solution.getConstraints()) {
		    hashCode += solution.getDualValue(constraint).hashCode();
		}
		return hashCode;
	    }
	};
    }

    static public <V> BiMap<V, Integer> getVariablesIds(LpProblem<V> problem, int startId) {
	Preconditions.checkNotNull(problem);
	final Builder<V, Integer> builder = ImmutableBiMap.builder();
	{
	    int i = startId;
	    for (V variable : problem.getVariables()) {
		builder.put(variable, Integer.valueOf(i));
		++i;
	    }
	}
	final ImmutableBiMap<V, Integer> variableIds = builder.build();
	return variableIds;
    }

    /**
     * <p>
     * Retrieves the bound of the variable from the given problem, with a possible modification if the variable type is
     * {@link LpVariableType#BOOL}: the bound is itself <em>bounded</em> to zero.
     * </p>
     * <p>
     * Consider a variable defined in the delegate problem having the type {@link LpVariableType#BOOL} and a lower bound
     * <em>l</em>. This method will return as its lower bound 0 if l is <code>null</code>, 0 if l.doubleValue() is lower
     * than zero, and l otherwise. E.g. this method returns zero as the lower bound of a {@link LpVariableType#BOOL}
     * variable having a lower bound of -1 in the given problem.
     * </p>
     * 
     * @see #getViewWithTransformedBools(LpProblem)
     * 
     * @param <V>
     *            the type of variables used in the problem.
     * @param problem
     *            not <code>null</code>.
     * @param variable
     *            must exist in the problem.
     * @return the bound of the variable according to the given problem, not <code>null</code>. The bound is greater
     *         than or equal to zero if the variable has the type {@link LpVariableType#BOOL} according to the given
     *         problem.
     */
    static public <V> Number getVarLowerBoundBounded(LpProblem<V> problem, V variable) {
	Preconditions.checkArgument(problem.getVariables().contains(variable));
	final LpVariableType type = problem.getVariableType(variable);
	if (type != LpVariableType.BOOL) {
	    return problem.getVariableLowerBound(variable);
	}
	final Number low = problem.getVariableLowerBound(variable);
	if (low.doubleValue() < 0d) {
	    return Double.valueOf(0d);
	}
	return low;
    }

    /**
     * <p>
     * Retrieves the bound of the variable from the given problem, with a possible modification if the variable type is
     * {@link LpVariableType#BOOL}: the bound is itself <em>bounded</em> to one.
     * </p>
     * <p>
     * Consider a variable defined in the delegate problem having the type {@link LpVariableType#BOOL} and an upper
     * bound <em>u</em>. This method will return as its upper bound 1 if u.doubleValue() is greater than one (including
     * if it is positive infinity), and u otherwise. E.g. this method returns 1 as the upper bound of a
     * {@link LpVariableType#BOOL} variable having an upper bound of 1.5 in the given problem.
     * </p>
     * 
     * @see #getViewWithTransformedBools(LpProblem)
     * 
     * @param <V>
     *            the type of variables used in the problem.
     * @param problem
     *            not <code>null</code>.
     * @param variable
     *            must exist in the problem.
     * @return the bound of the variable according to the given problem, not <code>null</code>. The bound is greater
     *         than or equal to zero if the variable has the type {@link LpVariableType#BOOL} according to the given
     *         problem.
     */
    static public <V> Number getVarUpperBoundBounded(LpProblem<V> problem, V variable) {
	Preconditions.checkArgument(problem.getVariables().contains(variable));
	final LpVariableType type = problem.getVariableType(variable);
	if (type != LpVariableType.BOOL) {
	    return problem.getVariableUpperBound(variable);
	}
	final Number up = problem.getVariableUpperBound(variable);
	/** TODO seems strange! (and see doc). */
	if (up.doubleValue() > 0d) {
	    return Double.valueOf(1d);
	}
	return up;
    }

    static public <V> LpProblem<V> getViewWithTransformedBools(LpProblem<V> problem) {
	return new LpProblemWithTransformedBoolsView<V>(problem);
    }

    static public <T1, T2> boolean equivalent(LpSolution<T1> a, LpSolution<T2> b, double epsilon) {
	if (a == null || b == null) {
	    return a == b;
	}
	if (!equivalent(a.getObjectiveValue(), b.getObjectiveValue(), epsilon)) {
	    return false;
	}
	if (!a.getProblem().equals(b.getProblem())) {
	    return false;
	}
	for (T1 variable : a.getVariables()) {
	    if (!b.getVariables().contains(variable)) {
		return false;
	    }
	    @SuppressWarnings("unchecked")
	    final T2 varTyped = (T2) variable;

	    if (!equivalent(a.getValue(variable), b.getValue(varTyped), epsilon)) {
		return false;
	    }
	}
	for (LpConstraint<T1> constraint : a.getConstraints()) {
	    if (!b.getConstraints().contains(constraint)) {
		return false;
	    }
	    @SuppressWarnings("unchecked")
	    final LpConstraint<T2> constraintTyped = (LpConstraint<T2>) constraint;

	    if (!equivalent(a.getDualValue(constraint), b.getDualValue(constraintTyped), epsilon)) {
		return false;
	    }
	}
	return true;
    }

    public static boolean equivalent(final Number value1, final Number value2, double epsilon) {
	return Math.abs(value1.doubleValue() - value2.doubleValue()) <= epsilon;
    }
}
