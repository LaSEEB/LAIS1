/*   
 * This file is part of LAIS (LaSEEB Agent Interaction Simulator).
 * 
 * LAIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LAIS is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LAIS.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.laseeb.LAIS.substance;

import java.awt.Color;

import org.laseeb.LAIS.utils.random.IRng;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Validate;

import uchicago.src.sim.engine.CustomProbeable;


/**
 * This class represents substances in the LAIS model. Substances are uniquely identified
 * by their {@link #name}, {@link #bitIdentifier} and {@link #family}.
 * 
 * @author Nuno Fachada
 *
 */
@Root(name="substance")
public class Substance implements CustomProbeable, Comparable<Substance> {

	/** 
	 * <strong>XML Attribute (float &gt;= 0)</strong>
	 * <p>
	 * Diffusion coefficient.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Attribute
	float kDif;
	
	/** 
	 * <strong>XML Attribute (float &gt;= 0)</strong>
	 * <p>
	 * Degradation coefficient.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Attribute
	float kDeg;

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Substance name.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	String name;

	/** 
	 * <strong>XML Element (long)</strong>
	 * <p>
	 * Substance 64 bit identifier, given as a long integer. This value is used
	 * to distinguish substances of the same family with the same name, 
	 * determine affinity and determine the {@link #bitIdentifier} of substances 
	 * created by merging rules. This value does not need to be specified if
	 * affinity is irrelevant.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	Long bitIdentifier = 0l;

	/** 
	 * <strong>XML Element ({@link SubstanceFamily})</strong>
	 * <p>
	 * Substance family. 
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	SubstanceFamily family;
	
	/** 
	 * <strong>XML Element (boolean)</strong>
	 * <p>
	 * Indicates if substance is mutable. Default value is <code>false</code>, i.e.,
	 * substance is immutable. 
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	boolean mutate = false;

	/** 
	 * <strong>XML Element (0 &gt;= integer &gt;= 63)</strong>
	 * <p>
	 * If substance is mutable (i.e., if {@link #mutate} is <code>true</code>, this
	 * is the start bit for {@link #bitIdentifier} mutation. If not given, default 
	 * value is 0.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	int startBit = 0;

	/** 
	 * <strong>XML Element (0 &gt;= integer &gt;= 63)</strong>
	 * <p>
	 * If substance is mutable (i.e., if {@link #mutate} is <code>true</code>, this
	 * is the end bit for {@link #bitIdentifier} mutation. If not given, default 
	 * value is 63.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	int endBit = 63;
	
	/** 
	 * <strong>XML Element (boolean)</strong>
	 * <p>
	 * Indicates if substance can be merged. Default value is <code>false</code>, i.e.,
	 * substance cannot be merged. 
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	boolean mergeable = false;

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * The RGB color of the substance. For example, if the user wants a substance 
	 * to have the color red, the usage will be:
	 * <p>
	 * <code>
	 * &lt;color&gt;255,0,0&lt;/color&gt;
	 * </code>
	 * <p>
	 * If not given, substance color will default to the substance family color.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	String color;
	
	/* Color of substance - this should be moved to a substance data class, like in the agents case. */
	private Color colorObject;
	
	/* Hash code of substance object. */
	private int hashCodeValue;
	
	/**
	 * Constructor used to create mutated clones of substances and merged substances.
	 * 
	 * @param kDif Diffusion coefficient.
	 * @param kDeg Degradation coefficient.
	 * @param name Name of substance.
	 * @param bitIdentifier Unique bit identifier.
	 * @param family Substance family.
	 */
	public Substance(float kDif, float kDeg, String name, long bitIdentifier, SubstanceFamily family) {
		this.kDif = kDif;
		this.kDeg = kDeg;
		this.name = name;
		this.bitIdentifier = bitIdentifier;
		this.family = family;
		validateName();
	}
	
	/**
	 * Public constructor used to create new substances from XML file.
	 */
	public Substance () {}
	
	/**
	 * The hash code of a substance depends on the substance {@link #bitIdentifier},
	 * {@link #family} and {@link #name}. 
	 * 
	 * @see java.lang.Object#hashCode()
	 * @return The hash code of the substance.
	 */
	public int hashCode() {
		return hashCodeValue;
	}
	
	/** For a substance to equal to another the {@link #bitIdentifier}, 
	 * {@link #family} and {@link #name} properties must be equal.
	 * 
	 * @see java.lang.Object#equals(Object)  
	 * @param obj Object representing substance to compare to current substance.
	 * @return True if the given object is the same substance as current substance; false otherwise.*/
	public boolean equals(Object obj) {
		Substance sub = (Substance) obj;
		if ((bitIdentifier.equals(sub.bitIdentifier))
				&& family.equals(sub.family)
				&& name.equals(sub.name))
			return true;
		else
			return false;
	}
	
	/**
	 * Returns the substance's family.
	 * 
	 * @return The substance's family.
	 */
	public SubstanceFamily getFamily() {
		return family;
	}

	/**
	 * Returns the substance's diffusion coefficient.
	 * 
	 * @return The substance's diffusion coefficient.
	 */
	public float getKDif() {
		return kDif;
	}

	/**
	 * Returns the substance's degradation coefficient.
	 * 
	 * @return The substance's degradation coefficient.
	 */
	public float getKDeg() {
		return kDeg;
	}

	/**
	 * Returns the substance's name.
	 * 
	 * @return The substance's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Same as getName.
	 * 
	 * @return The substance's name.
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * Return the substance bit identifier.
	 * @return The substance bit identifier.
	 */
	public Long getBitIdentifier() {
		return bitIdentifier;
	}
	
	/**
	 * Return an hexadecimal string representing the substance's bit identifier.
	 * @return An hexadecimal string representing the substance's bit identifier.
	 */
	public String getBitIdentifierAsHexString() {
		return Long.toHexString(bitIdentifier) + "H";
	}
	
	/**
	 * Returns true if the substance can be merged with another substance, false otherwise.
	 * 
	 * @return True if the substance can be merged with another substance, false otherwise.
	 */
	public boolean isMergeable() {
		return mergeable;
	}
	
	/**
	 * Clone current substance with given mutation rates.
	 * 
	 * @param mutationRate The mutation rate for the unique bit identifier (>0 and <=1).
	 * @param rng An instance of a random number generator.
	 * @return A mutated clone of the current substance.
	 * @throws SubstanceException If not possible to clone substance.
	 */
	public Substance cloneWithMutation(float mutationRate, IRng rng) throws SubstanceException {
		if (!this.mutate) return this;
		if (mutationRate == 0) return this;
		/* Clone bitId. */
		long newBitId = SubstanceUtils.mutate(this.bitIdentifier, mutationRate, startBit, endBit, rng);
		/* If no mutation took place, then return the same substance. */
		if (newBitId == this.bitIdentifier)
			return this;
		/* Clone kDif. */
		float newKDif = this.kDif * (1 + rng.nextFloatFromTo(-mutationRate/2, mutationRate/2));
		/* Clone kDeg. */
		float newKDeg = this.kDeg * (1 + rng.nextFloatFromTo(-mutationRate/2, mutationRate/2));
		/* Clone name. */
		String newName = this.name; //  + "_" + Long.toHexString(newBitId);
		/* Create and return new substance. */
		Substance newSub = new Substance(newKDif, newKDeg, newName, newBitId, this.family);
		newSub.validateName();
		newSub.startBit = this.startBit;
		newSub.endBit = this.endBit;
		newSub.mergeable = this.mergeable;
		newSub.mutate = this.mutate;
		newSub.colorObject = this.colorObject;
		return newSub;
	}
	
	/**
	 * Calculates the affinity of a given substance with the current substance.
	 * 
	 * @param sub Given substance to determine affinity with.
	 * @param startBit Bit from where to start determining the affinity.
	 * @param endBit Bit where to end determining the affinity.
	 * @return The affinity of the current substance with the given substance.
	 * @throws SubstanceException If not possible to determine affinity.
	 */
	public float affinityWith(Substance sub, int startBit, int endBit) throws SubstanceException {
		long affinBitId = SubstanceUtils.rangedXOR(this.bitIdentifier, sub.bitIdentifier, startBit, endBit);
		/* Affinity is given by the Hamming distance. */
		int bitFits = Long.bitCount(affinBitId);			
		/* Determine affinity (percentage of "bit fits" comparing with number of bits compared). */
		float affinity = ((float) bitFits) / ((float) (endBit - startBit + 1));
		/* Return affinity. */
		return affinity;
	}
	
	/**
	 * Returns the substance color, or the substance family color, if substance color is null.
	 * 
	 * @return The substance color.
	 */
	public Color getColor() {
		if (colorObject != null)
			return colorObject;
		return
			family.getColor();
	}
	
	/**
	 * Certifies that the last part of the substance name is the hexadecimal value of
	 * the bit id. 
	 */
	private void validateName() {
		/* Remove previous name appendix. */
		this.name = SubstanceUtils.subNameRemoveAppend(this);
		/* Add new name appendix. */
		this.name = SubstanceUtils.subNameAddAppend(this);
	}


	/**
	 * Initialization method used by the Simple XML framework.
	 */
	@SuppressWarnings("unused")
	@Validate
	private void validate() {
		/* Validate color. */
		if (color != null) {
			String[] rgb = this.color.split(",");
			this.colorObject = new Color(
					Integer.parseInt(rgb[0]), 
					Integer.parseInt(rgb[1]), 
					Integer.parseInt(rgb[2]));
		}
		/* Validate last part of name. */
		validateName();
		/* Create hash code. */
		hashCodeValue = bitIdentifier.hashCode() ^ name.hashCode() ^ family.hashCode();
	}

	/**
	 * @see uchicago.src.sim.engine.CustomProbeable
	 */
	public String[] getProbedProperties() {
		String properties[] = {"name","bitIdentifierAsHexString","family","kDif","kDeg"};
		return properties;
	}

	/**
	 * For ordering purposes, substances are compared using their names. As such,
	 * substance ordering will be performed alphabetically.
	 * 
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Substance otherSub) {
		return name.compareTo(otherSub.getName());
	}
	

}
