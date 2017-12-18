package io.github.oliviercailloux.jlp.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.collect.Iterables;

import io.github.oliviercailloux.jlp.SolverException;
import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableType;
import io.github.oliviercailloux.jlp.parameters.SolverParameters;
import io.github.oliviercailloux.jlp.parameters.SolverParametersUtils;
import io.github.oliviercailloux.jlp.problem.IMP;
import io.github.oliviercailloux.jlp.problem.MPs;
import io.github.oliviercailloux.jlp.result.Solution;
import io.github.oliviercailloux.jlp.result.SolutionAlone;

/**
 * <p>
 * This class defines static methods that should be mainly useful for internal
 * use in this project and to implement underlying solvers.
 * </p>
 * <p>
 * Usage examples: <code>
	    final Set bools = LpProblems.getVariables(solution.getProblem(),
		    LpVariableType.BOOL);
final Predicate<Number> isBool = new IsBoolValue(1e-6);
	    final FunctionGetValue fctGetValue = new LpSolverUtils.FunctionGetValue(
		    solution);
	    final Predicate hasBoolValue = Predicates.compose(isBool,
 fctGetValue);
	    final Set wrong = Sets.filter(bools, Predicates.not(hasBoolValue));
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
public class SolverUtils {

	static public class FunctionGetValue implements Function<Variable, Number> {
		private final SolutionAlone solution;

		public FunctionGetValue(SolutionAlone solution) {
			checkNotNull(solution);
			this.solution = solution;
		}

		@Override
		public Number apply(Variable input) {
			return solution.getValue(input);
		}
	}

	/**
	 * Tests whether the given numbers correspond to boolean values, plus or minus
	 * an allowed epsilon value. The predicate is <code>true</code>, for a number n
	 * and a positive or nul epsilon value e, iff its double value is in [-e, e] or
	 * in [1-e, 1+e].
	 *
	 * @author Olivier Cailloux
	 *
	 */
	static public class IsBoolValue implements Predicate<Number> {
		private final double epsilon;

		public IsBoolValue(double epsilon) {
			checkArgument(epsilon >= 0);
			checkArgument(!Double.isInfinite(epsilon));
			checkArgument(!Double.isNaN(epsilon));
			this.epsilon = epsilon;
		}

		@Override
		public boolean apply(Number value) {
			final boolean clean;
			if (value == null) {
				clean = false;
			} else {
				final double val = value.doubleValue();
				if (val < -epsilon) {
					clean = false;
				} else if (val > epsilon && val < 1 - epsilon) {
					clean = false;
				} else if (val > 1 + epsilon) {
					clean = false;
				} else {
					clean = true;
				}
			}
			return clean;
		}
	}

	/**
	 * Ensures that the given parameters are conform to the given mandatory values.
	 * That is, for each parameter value that is mandatory, ensures that the given
	 * parameters have an associated value (which may be the default value) that is
	 * equal to the mandatory value.
	 *
	 * @param parameters
	 *            not <code>null</code>.
	 * @param mandatoryValues
	 *            not <code>null</code>, no <code>null</code> key. The values must
	 *            be meaningful.
	 * @throws SolverException
	 *             if the parameters are not conform.
	 */
	static public void assertConform(SolverParameters parameters, Map<Enum<?>, Object> mandatoryValues)
			throws SolverException {
		for (Enum<?> parameter : SolverParametersUtils.getParameters()) {
			if (mandatoryValues.containsKey(parameter)) {
				final Object mandatoryValue = mandatoryValues.get(parameter);
				final Object value = parameters.getValueAsObject(parameter);
				if (!Equivalence.equals().equivalent(value, mandatoryValue)) {
					throw new SolverException("Unsupported parameter value: " + parameter + ", " + value + ".");
				}
			}
		}
	}

	static public boolean equivalent(Constraint a, Constraint b) {
		return getConstraintEquivalence().equivalent(a, b);
	}

	static public boolean equivalent(IMP a, IMP b) {
		return getProblemEquivalence().equivalent(a, b);
	}

	public static boolean equivalent(final Number value1, final Number value2, double epsilon) {
		return Math.abs(value1.doubleValue() - value2.doubleValue()) <= epsilon;
	}

	static public boolean equivalent(Solution a, Solution b) {
		return getSolutionEquivalence().equivalent(a, b);
	}

	static public <T1, T2> boolean equivalent(Solution a, Solution b, double epsilon) {
		if (a == null || b == null) {
			return a == b;
		}
		if (!equivalent(a.getObjectiveValue(), b.getObjectiveValue(), epsilon)) {
			return false;
		}
		if (!a.getProblem().equals(b.getProblem())) {
			return false;
		}
		for (Variable variable : a.getVariables()) {
			final Variable varTyped = variable;

			if (!equivalent(a.getValue(variable), b.getValue(varTyped), epsilon)) {
				return false;
			}
		}
		for (Constraint constraint : a.getConstraints()) {
			final Constraint constraintTyped = constraint;

			if (!equivalent(a.getDualValue(constraint), b.getDualValue(constraintTyped), epsilon)) {
				return false;
			}
		}
		return true;
	}

	static public boolean equivalent(SumTerms a, SumTerms b) {
		return getLinearEquivalence().equivalent(a, b);
	}

	static public int getAsInteger(double number) throws SolverException {
		final long lValue = Math.round(number);
		if (lValue > Integer.MAX_VALUE) {
			throw new SolverException("Number " + number + " does not fit into an integer (too big).");
		}
		final int iValue = (int) lValue;

		if (Math.abs(number - iValue) > 1e-6) {
			throw new SolverException("Number " + number + " does not round to an integer.");
		}

		return iValue;
	}

	/**
	 * Provides an implementation of toString for debugging use. For a more user
	 * friendly string description, see class {@link MPs}.
	 *
	 * @param problem
	 *            not <code>null</code>.
	 * @return a debug description.
	 */
	static public String getAsString(IMP problem) {
		final ToStringHelper helper = Objects.toStringHelper(problem);
		helper.addValue('\'' + problem.getName() + '\'');
		if (!problem.getObjective().isEmpty()) {
			helper.addValue("" + problem.getObjective().getDirection() + " " + problem.getObjective().getFunction());
		}
		helper.addValue("" + problem.getVariables().size() + " variables");
		helper.addValue(problem.getConstraints().size() + " constraints");
		return helper.toString();
	}

	static public <V> String getAsString(Solution solution) {
		final ToStringHelper helper = Objects.toStringHelper(solution);
		helper.add("Problem", solution.getProblem());
		helper.add("Objective value", solution.getObjectiveValue());
		helper.add("Valued variables size", Integer.valueOf(solution.getVariables().size()));
		return helper.toString();
	}

	static public Equivalence<Constraint> getConstraintEquivalence() {
		return new Equivalence<Constraint>() {

			@Override
			public boolean doEquivalent(Constraint a, Constraint b) {
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
			public int doHash(Constraint c) {
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

	static public Equivalence<SumTerms> getLinearEquivalence() {
		return new Equivalence<SumTerms>() {
			@Override
			public boolean doEquivalent(SumTerms a, SumTerms b) {
				return Iterables.elementsEqual(a, b);
			}

			@Override
			public int doHash(SumTerms t) {
				int hashCode = 1;
				for (Term term : t) {
					hashCode = 31 * hashCode + term.hashCode();
				}
				return hashCode;
			}
		};
	}

	static public Equivalence<IMP> getProblemEquivalence() {
		return new Equivalence<IMP>() {
			@Override
			public boolean doEquivalent(IMP a, IMP b) {
				return computeEquivalent(a, b);
			}

			@Override
			public int doHash(IMP t) {
				final int hashCode = Objects.hashCode(t.getObjective());
				return hashCode + t.getConstraints().hashCode() + t.getVariables().hashCode();
			}

			private <T1, T2> boolean computeEquivalent(IMP a, IMP b) {
				if (!a.getConstraints().equals(b.getConstraints())) {
					return false;
				}
				if (!Objects.equal(a.getObjective(), b.getObjective())) {
					return false;
				}
				if (!a.getVariables().equals(b.getVariables())) {
					return false;
				}
				for (Variable variable : a.getVariables()) {
					final Variable varTyped = variable;

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
		};
	}

	static public Equivalence<Solution> getSolutionEquivalence() {
		return new Equivalence<Solution>() {
			@Override
			public boolean doEquivalent(Solution a, Solution b) {
				return computeEquivalent(a, b);
			}

			@Override
			public int doHash(Solution t) {
				return computeHash(t);
			}

			private boolean computeEquivalent(Solution a, Solution b) {
				if (!getEquivalenceByDoubleValue().equivalent(a.getObjectiveValue(), b.getObjectiveValue())) {
					return false;
				}
				if (!a.getProblem().equals(b.getProblem())) {
					return false;
				}
				for (Variable variable : a.getVariables()) {
					@SuppressWarnings("unchecked")
					final Variable varTyped = variable;

					if (!getEquivalenceByDoubleValue().equivalent(a.getValue(variable), b.getValue(varTyped))) {
						return false;
					}
				}
				for (Constraint constraint : a.getConstraints()) {
					final Constraint constraintTyped = constraint;

					if (!getEquivalenceByDoubleValue().equivalent(a.getDualValue(constraint),
							b.getDualValue(constraintTyped))) {
						return false;
					}
				}
				return true;
			}

			private <T> int computeHash(Solution solution) {
				int hashCode = Objects.hashCode(solution.getProblem(), solution.getProblem());
				for (Variable variable : solution.getVariables()) {
					hashCode += solution.getValue(variable).hashCode();
				}
				for (Constraint constraint : solution.getConstraints()) {
					hashCode += solution.getDualValue(constraint).hashCode();
				}
				return hashCode;
			}
		};
	}

	static public BiMap<Variable, Integer> getVariablesIds(IMP problem, int startId) {
		Preconditions.checkNotNull(problem);
		final Builder<Variable, Integer> builder = ImmutableBiMap.builder();
		{
			int i = startId;
			for (Variable variable : problem.getVariables()) {
				builder.put(variable, Integer.valueOf(i));
				++i;
			}
		}
		final ImmutableBiMap<Variable, Integer> variableIds = builder.build();
		return variableIds;
	}

	/**
	 * <p>
	 * Retrieves the bound of the variable from the given problem, with a possible
	 * modification if the variable type is {@link VariableType#BOOL} : the bound is
	 * itself <em>bounded</em> to zero.
	 * </p>
	 * <p>
	 * Consider a variable defined in the delegate problem having the type
	 * {@link VariableType#BOOL} and a lower bound <em>l</em>. This method will
	 * return as its lower bound 0 if l is <code>null</code>, 0 if l.doubleValue()
	 * is lower than zero, and l otherwise. E.g. this method returns zero as the
	 * lower bound of a {@link VariableType#BOOL} variable having a lower bound of
	 * -1 in the given problem.
	 * </p>
	 *
	 * @see #getViewWithTransformedBools(IMP)
	 *
	 * @param problem
	 *            not <code>null</code>.
	 * @param variable
	 *            must exist in the problem.
	 * @return the bound of the variable according to the given problem, not
	 *         <code>null</code>. The bound is greater than or equal to zero if the
	 *         variable has the type {@link VariableType#BOOL} according to the
	 *         given problem.
	 */
	static public Number getVarLowerBoundBounded(IMP problem, Variable variable) {
		Preconditions.checkArgument(problem.getVariables().contains(variable));
		final VariableType type = problem.getVariableType(variable);
		if (type != VariableType.BOOL) {
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
	 * Retrieves the bound of the variable from the given problem, with a possible
	 * modification if the variable type is {@link VariableType#BOOL} : the bound is
	 * itself <em>bounded</em> to one.
	 * </p>
	 * <p>
	 * Consider a variable defined in the delegate problem having the type
	 * {@link VariableType#BOOL} and an upper bound <em>u</em>. This method will
	 * return as its upper bound 1 if u.doubleValue() is greater than one (including
	 * if it is positive infinity), and u otherwise. E.g. this method returns 1 as
	 * the upper bound of a {@link VariableType#BOOL} variable having an upper bound
	 * of 1.5 in the given problem.
	 * </p>
	 *
	 * @see #getViewWithTransformedBools(IMP)
	 *
	 * @param problem
	 *            not <code>null</code>.
	 * @param variable
	 *            must exist in the problem.
	 * @return the bound of the variable according to the given problem, not
	 *         <code>null</code>. The bound is greater than or equal to zero if the
	 *         variable has the type {@link VariableType#BOOL} according to the
	 *         given problem.
	 */
	static public Number getVarUpperBoundBounded(IMP problem, Variable variable) {
		Preconditions.checkArgument(problem.getVariables().contains(variable));
		final VariableType type = problem.getVariableType(variable);
		if (type != VariableType.BOOL) {
			return problem.getVariableUpperBound(variable);
		}
		final Number up = problem.getVariableUpperBound(variable);
		/** TODO seems strange! (and see doc). */
		if (up.doubleValue() > 0d) {
			return Double.valueOf(1d);
		}
		return up;
	}
}
