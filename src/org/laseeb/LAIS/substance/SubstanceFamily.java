package org.laseeb.LAIS.substance;

import java.awt.Color;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.core.Validate;
import org.simpleframework.xml.Root;

import uchicago.src.sim.engine.CustomProbeable;

/**
 * This class represents families of substances.
 * 
 * @author Nuno Fachada
 */
@Root(name="family")
public class SubstanceFamily implements Comparable<SubstanceFamily>, CustomProbeable {
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Substance family name.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	String name;
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * The RGB color of the substance family. For example, if the user wants a 
	 * substance family to have the color red, the usage will be:
	 * <p>
	 * <code>
	 * &lt;color&gt;255,0,0&lt;/color&gt;
	 * </code>
	 * <p>
	 * If not given, substance family color will be black.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	String color;
	
	/** 
	 * <strong>XML Element (float &gt;= 0)</strong>
	 * <p>
	 * The concentration which gives the maximum color impact. If not given, 
	 * the default value is 0, i.e., any substance concentration will appear completely
	 * opaque in the simulation display.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	float subMaxColorImpactCon = 0;
	
	/* The substance family color. */
	private Color colorObject;
	
	/**
	 * Auxiliary constructor, not expected to be used directly in code.
	 * Substance families should in principle be created automatically 
	 * using the XML model.
	 * 
	 * @param name The name for this substance family.
	 * @param subMaxColorImpactCon Concentration for maximum color impact.
	 * @param color The color for this substance family.
	 */
	public SubstanceFamily(String name, float subMaxColorImpactCon, Color color) {
		this.name = name;
		this.subMaxColorImpactCon = subMaxColorImpactCon;
		this.colorObject = color;
	}
	
	/**
	 * Public constructor used to create new substance families from XML file.
	 */
	public SubstanceFamily() {}
	
	/**
	 * Returns the color of this substance family.
	 * determineFamilyMaps
	 * @return The color of this substance family.
	 */
	public Color getColor() {
		return colorObject;
	}

	/**
	 * Returns the name of this substance family.
	 * 
	 * @return The name of this substance family.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the concentration which gives the maximum color impact of this substance family.
	 * 
	 * @return The concentration which gives the maximum color impact of this substance family.
	 */
	public float getSubMaxColorImpactCon() {
		return subMaxColorImpactCon;
	}

	/**
	 * Implementation of the {@link java.lang.Comparable} interface.
	 * Two substance families are distinguished by their name.
	 * 
	 * @see java.lang.Comparable
	 */
	public int compareTo(SubstanceFamily otherFam) {
		return getName().compareTo(otherFam.getName());
	}
	
	/**
	 * Post-XML read validation. Used to determine the color from the separate RGB components.
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
		} else {
			this.colorObject = new Color(0, 0, 0);
		}
	}

	/**
	 * @see uchicago.src.sim.engine.CustomProbeable
	 */
	public String[] getProbedProperties() {
		String properties[] = {"name","color","subMaxColorImpactCon"};
		return properties;
	}
	
	/**
	 * Returns the name of the substance family (same as {@link #getName()}).
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}

}
