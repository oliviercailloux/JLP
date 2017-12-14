package io.github.oliviercailloux.jlp.problem;

/**
 * A view of a problem providing its own problem name. This view delegates
 * everything to the delegate problem except the {@link #setName(String)} and
 * {@link #getName()} methods as it provides its own name. The underlying
 * problem name is not used.
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
class LpProblemOwnName<V> extends LpProblemForwarder<V> implements LpProblem<V> {

	/**
	 * not <code>null</code>, empty if not set.
	 */
	private String m_name;

	public LpProblemOwnName(LpProblem<V> delegate) {
		super(delegate);
		m_name = "";
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public boolean setName(String name) {
		final boolean eq = m_name.equals(name);
		if (eq) {
			return false;
		}
		m_name = name == null ? "" : name;
		return true;
	}

}
