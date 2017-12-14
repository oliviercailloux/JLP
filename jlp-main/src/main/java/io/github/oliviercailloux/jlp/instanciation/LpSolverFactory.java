package io.github.oliviercailloux.jlp.instanciation;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.jlp.LpSolver;

/**
 * This class contains facility methods to build a new solver instance. The
 * simplest way to use it is to call the static {@link #newSolver(LpSolverType)}
 * method.
 *
 * @author Olivier Cailloux
 *
 */
public class LpSolverFactory {

	private static final String CLASS_NAME_CBC = "io.github.oliviercailloux.jlp.coin.cbc.SolverCbc";

	private static final String CLASS_NAME_CPLEX = "io.github.oliviercailloux.jlp.cplex.SolverCPLEX";

	private static final String CLASS_NAME_LP_SOLVE = "io.github.oliviercailloux.jlp.lpsolve.SolverLpSolve";

	/**
	 * <p>
	 * Creates a new solver instance corresponding to the chosen solver type. This
	 * factory will search for the corresponding implementing class in the
	 * classpath, using a class name dependent on the chosen solver type. This will
	 * fail if the classpath does not contain the expected implementing class.
	 * </p>
	 *
	 * @param impl
	 *            not <code>null</code>.
	 * @param <V>
	 *            the type of the variables to be used in the new solver instance.
	 *
	 * @return a new solver instance.
	 *
	 * @throws LpSolverFactoryException
	 *             if anything goes wrong when instanciating the solver: the
	 *             expected class is not found in the classpath, or a security
	 *             policy prevents accessing it or its constructor.
	 */
	static public <V> LpSolver<V> newSolver(LpSolverType impl) throws LpSolverFactoryException {
		checkNotNull(impl);
		final LpSolverFactory factory = new LpSolverFactory(impl);
		return factory.newSolver();
	}

	/**
	 * <code>null</code> for no solver implementation specified.
	 */
	private LpSolverType m_solverImpl;

	public LpSolverFactory() {
		m_solverImpl = null;
	}

	/**
	 * @param impl
	 *            <code>null</code> for not set.
	 */
	public LpSolverFactory(LpSolverType impl) {
		m_solverImpl = impl;
	}

	/**
	 * <p>
	 * A solver implementation must have been specified.
	 * </p>
	 * <p>
	 * This method wraps exceptions that can be thrown because of an instanciation
	 * problem in a higher-level exception.
	 * </p>
	 *
	 * @param <V>
	 *            the type of the variables to be used in the new solver instance.
	 *
	 * @return a new solver instance backed by the implementation previously chosen.
	 *
	 * @throws LpSolverFactoryException
	 *             if anything goes wrong when constructing the solver.
	 */
	public <V> LpSolver<V> newSolver() throws LpSolverFactoryException {
		try {
			return newSolverThrowing();
		} catch (Exception exc) {
			throw new LpSolverFactoryException(exc);
		}
	}

	/**
	 * <p>
	 * A solver implementation must have been specified.
	 * </p>
	 *
	 * <p>
	 * Note that this method propagates any exception thrown by the nullary
	 * constructor, including a checked exception. Use of this method effectively
	 * bypasses the compile-time exception checking that would otherwise be
	 * performed by the compiler. However, the solver implementor is forbidden to do
	 * that, thus it should not happen.
	 * </p>
	 *
	 * @param <V>
	 *            the type of the variables to be used in the new solver instance.
	 *
	 * @return a new solver instance backed by the implementation previously chosen.
	 *
	 * @throws ClassNotFoundException
	 *             if the implementing class is not found. Try an other
	 *             implementation or check your classpath.
	 * @throws InstantiationException
	 *             if the implementing class does not look like a correct
	 *             implementing class (e.g. it does not implement the required
	 *             interface) or can't be instantiated.
	 * @throws IllegalAccessException
	 *             if the class or its nullary constructor is not accessible.
	 * @throws SecurityException
	 *             If a security manager, <i>s</i>, is present and any of the
	 *             following conditions is met:
	 *             <ul>
	 *             <li>invocation of {@link SecurityManager#checkMemberAccess
	 *             s.checkMemberAccess(this, Member.PUBLIC)} denies creation of new
	 *             instances of this class
	 *             <li>the caller's class loader is not the same as or an ancestor
	 *             of the class loader for the current class and invocation of
	 *             {@link SecurityManager#checkPackageAccess s.checkPackageAccess()}
	 *             denies access to the package of this class
	 *             </ul>
	 */
	public <V> LpSolver<V> newSolverThrowing()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException {
		/**
		 * Ideally we should prevent possible checked exceptions thrown by the
		 * constructor to propagate. We could check using reflection that the
		 * constructor does not do that (it should not!), but this is not implemented
		 * yet.
		 */
		if (m_solverImpl == null) {
			throw new IllegalStateException("Solver implementation has not been specified.");
		}

		final String className = getClassName(m_solverImpl);
		Class<?> c = Class.forName(className);
		final Object inst = c.newInstance();
		if (!(inst instanceof LpSolver)) {
			throw new InstantiationException("Class " + className + " found but is not an instance of "
					+ LpSolver.class.getCanonicalName() + ".");
		}
		@SuppressWarnings("unchecked")
		final LpSolver<V> inst2 = (LpSolver<V>) inst;
		return inst2;
	}

	/**
	 * Sets the solver type that this factory will instantiate when asked for.
	 *
	 * @param impl
	 *            <code>null</code> for no solver type set.
	 */
	public void setImpl(LpSolverType impl) {
		m_solverImpl = impl;
	}

	private String getClassName(LpSolverType solverImpl) {
		switch (solverImpl) {
		case CPLEX:
			return CLASS_NAME_CPLEX;
		case LP_SOLVE:
			return CLASS_NAME_LP_SOLVE;
		case CBC:
			return CLASS_NAME_CBC;
		default:
			throw new IllegalStateException("Unknown impl: " + solverImpl + ".");
		}
	}

}
