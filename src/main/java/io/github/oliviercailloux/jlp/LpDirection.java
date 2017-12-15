package io.github.oliviercailloux.jlp;

/**
 * The optimization direction to be followed to improve the objective. Together
 * with an optimization function <em>f</em> , a {@link #MAX} direction means
 * that the objective improves when the value of <em>f</em> is higher.
 * 
 * @author Olivier Cailloux
 * 
 */
public enum LpDirection {
	MAX, MIN;
}
