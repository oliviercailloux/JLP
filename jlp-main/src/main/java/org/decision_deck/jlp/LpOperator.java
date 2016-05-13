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
package org.decision_deck.jlp;

/**
 * A boolean operator that can be used to relate the left hand side and the
 * right hand side in a constraint.
 *
 * @author Olivier Cailloux
 *
 */
public enum LpOperator {

	/**
	 * Equal
	 */
	EQ,
	/**
	 * Greater or equal
	 */
	GE,
	/**
	 * Less or equal
	 */
	LE;

	public String toAsciiString() {
		switch (this) {
		case LE:
			return "<=";
		case GE:
			return ">=";
		case EQ:
			return "=";
		default:
			throw new IllegalStateException("Unknown operator.");
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case LE:
			return "" + '\u2264';
		case GE:
			return "" + '\u2265';
		case EQ:
			return "=";
		default:
			throw new IllegalStateException("Unknown operator.");
		}
	}

}
