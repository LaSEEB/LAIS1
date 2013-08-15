package org.laseeb.LAIS.substance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Validate;

/**
 * This class contains updated information about substances concentration in the simulation.
 * 
 * @author Nuno Fachada
 *
 */
@Root
public class SubstanceManager implements SubstanceProvider {
	
	/** 
	 * <strong>XML ElementList (elements of type {@link SubstanceFamily})</strong>
	 * <p>
	 * The list of substance families.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@ElementList
	HashSet<SubstanceFamily> families;
	
	/* The backup list of substance families. */
	private HashSet<SubstanceFamily> familiesBak;
	
	/** 
	 * <strong>XML ElementList (elements of type {@link Substance})</strong>
	 * <p>
	 * The list of substances.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@ElementList
	HashSet<Substance> substances;
	
	/* The backup list of substances. */
	private HashSet<Substance> substancesBak;
	
	/** 
	 * <strong>XML ElementList (elements of type {@link SubFamilyMergeRule})</strong>
	 * <p>
	 * Family rules for merging substances.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@ElementList
	Vector<SubFamilyMergeRule> subFamilyMergeRules;
	
	/* The backup list of family rules for merging substances. */
	private Vector<SubFamilyMergeRule> subFamilyMergeRulesBak;
	
	/* Map of substance concentration. */
	private HashMap<Substance, Float> concentrations;
	
	/* Map of family concentrations. */
	private HashMap<SubstanceFamily, Float> famConcentrations;
	
	/* Map of family diversity. */
	private HashMap<SubstanceFamily, Integer> famDiversity;

	/* Substance merging rules. */
	private Vector<SubMergeRule> subMergeRules = new Vector<SubMergeRule>();

	/* ************************************************************************ */
	/* ************************* INTERNAL CLASSES ***************************** */ 
	/* ************************************************************************ */
		
	/**
	 * This internal class is a data source regarding the concentration of substances in the simulation 
	 * for data export classes (charts, sequences, output to file...).
	 *  
	 * @author Nuno Fachada
	 */
	public class SubstanceConcentrationSource {
		
		/* Substance to get concentration of. */
		private Substance mySubstance;
		
		/**
		 * Specify the substance to get concentration of.
		 * 
		 * @param mySubstance Substance to get concentration of.
		 */
		public SubstanceConcentrationSource(Substance mySubstance) {
			this.mySubstance = mySubstance;
		}
		
		/**
		 * Get substance concentration.
		 * 
		 * @return Substance concentration.
		 */
		public float getConcentration() {
			Float con = concentrations.get(mySubstance);
			if (con == null) con = 0.0f;
			return con;
		}
	}

	/**
	 * This internal class is a data source regarding the concentration of substance families in the 
	 * simulation for data export classes (charts, sequences, output to file...).
	 *  
	 * @author Nuno Fachada
	 */
	public class FamilyConcentrationSource {
		
		/* Substance family to get concentration of. */
		private SubstanceFamily myFamily;
		
		/**
		 * Specify the substance family to get concentration of.
		 * 
		 * @param myFamily Substance family to get concentration of.
		 */
		public FamilyConcentrationSource(SubstanceFamily myFamily) {
			this.myFamily = myFamily;
		}
		
		/**
		 * Get substance family concentration.
		 * 
		 * @return Substance family concentration.
		 */
		public float getConcentration() {
			/* Populate family concentrations map if necessary. Only do this once for each simulation
			 * step. */
			if (famConcentrations.isEmpty()) {
				float con;
				Iterator<Substance> iterSub = concentrations.keySet().iterator();
				while (iterSub.hasNext()) {
					Substance sub = iterSub.next();
					SubstanceFamily subFam = sub.getFamily();
					if (famConcentrations.containsKey(subFam))
						con = famConcentrations.get(subFam);
					else
						con = 0;
					con += concentrations.get(sub);
					famConcentrations.put(subFam, con);
				}
			}
			/* Return family concentration. */
			if (famConcentrations.containsKey(this.myFamily))
				return famConcentrations.get(this.myFamily);
			else
				return 0;
		}
	}

	/**
	 * This internal class is a data source regarding the number of substances in a given family, used 
	 * for data export classes (charts, sequences, output to file...).
	 *  
	 * @author Nuno Fachada
	 */
	public class FamilyDiversitySource {
		
		/* Substance family to get diversity (number of substances) of. */
		private SubstanceFamily myFamily;
		
		/**
		 * Specify the substance family to get diversity (number of substances) of.
		 * 
		 * @param myFamily Substance family to get diversity (number of substances) of.
		 */
		public FamilyDiversitySource(SubstanceFamily myFamily) {
			this.myFamily = myFamily;
		}
		
		/**
		 * Get substance family diversity (number of substances).
		 * 
		 * @return Substance family diversity (number of substances).
		 */
		public int getDiversity() {
			/* Populate family diversity map if necessary. Only do this once for each simulation
			 * step. */
			if (famDiversity.isEmpty()) {
				int diversity;
				Iterator<Substance> iterSub = concentrations.keySet().iterator();
				while (iterSub.hasNext()) {
					Substance sub = iterSub.next();
					SubstanceFamily subFam = sub.getFamily();
					if (famDiversity.containsKey(subFam))
						diversity = famDiversity.get(subFam);
					else
						diversity = 0;
					diversity++;
					famDiversity.put(subFam, diversity);
				}
			}
			/* Return family concentration. */
			if (famDiversity.containsKey(this.myFamily))
				return famDiversity.get(this.myFamily);
			else
				return 0;
		}
	}

	/* ************************************************************************ */
	/* ************** METHODS AND CONSTRUCTORS OF SubstanceManager ************ */ 
	/* ************************************************************************ */

	/**
	 * This constructor initializes substance set, family set and substance-concentration map.
	 */
	public SubstanceManager() {		
		//this.substances = new HashSet<Substance>();
		//this.families = new HashSet<SubstanceFamily>();
		this.concentrations = new HashMap<Substance, Float>();
		this.famConcentrations = new HashMap<SubstanceFamily, Float>();
		this.famDiversity = new HashMap<SubstanceFamily, Integer>();
	}

	/** 
	 * Merge two substances. Saves how to merge given substances to cache.
	 * 
	 * @param sub1 The first substance to merge.
	 * @param sub2 The second substance to merge.
	 * @return The merged substance according to rule.
	 * @throws SubstanceException If not possible to merge substances.
	 */
	private SubMergeRule mergeSubstances(Substance sub1, Substance sub2) throws SubstanceException {
		/* Check if pair exists in cache. */
		Iterator<SubMergeRule> iterSubC = subMergeRules.iterator();
		while (iterSubC.hasNext()) {
			SubMergeRule sc = iterSubC.next();
			if (sc.isPair(sub1, sub2))
				return sc;
		}
		/* If not, compose substance if such rule exists. */
		Iterator<SubFamilyMergeRule> iterMr = subFamilyMergeRules.iterator();
		while (iterMr.hasNext()) {
			SubFamilyMergeRule mr = iterMr.next();
			SubMergeRule smgd = mr.merge(sub1, sub2);
			if (smgd != null) {
				subMergeRules.add(smgd);
				return smgd;
			}
		}
		return null;
	}
	
//	/**
//	 * Add a substance to the substance set.
//	 * 
//	 * @param substance Substance to add to substance set.
//	 */
//	public void addSubstance(Substance substance) {
//		/* Put substance on the map */
//		substances.add(substance);
//		/* Update family information */
//		if (!families.contains(substance.getFamily())) {
//			families.add(substance.getFamily());
//		}
//	}
	
	/**
	 * Reset substance concentrations.
	 */
	public void resetConcentrations() {
		concentrations.clear();
		famConcentrations.clear();
		famDiversity.clear();
		subMergeRules.clear();
	}
	
	/**
	 * Returns an iterator which cycles through the substances present in the simulation.
	 * 
	 * @return An iterator which cycles through the substances present in the simulation.
	 * @see SubstanceProvider#substanceIterator()
	 */
	public Iterator<Substance> substanceIterator() {
		return concentrations.keySet().iterator();
	}
	
	/**
	 * Return a data source regarding the concentration of a given substance in the simulation.
	 * 
	 * @param substance Substance to get concentration source of.
	 * @return A data source regarding the concentration of a given substance in the simulation.
	 */
	public SubstanceConcentrationSource getSubstanceConcentrationSource(Substance substance) {
		return new SubstanceConcentrationSource(substance);
	}
	
	/**
	 * Return a data source regarding the concentration of a given substance family in the simulation.
	 * 
	 * @param substanceFamily Substance family to get concentration source of.
	 * @return A data source regarding the concentration of a given substance family in the simulation.
	 */
	public FamilyConcentrationSource getFamilyConcentrationSource(SubstanceFamily substanceFamily) {
		return new FamilyConcentrationSource(substanceFamily);
	}
	
	/**
	 * Return a data source regarding the number of substances which belong to the same family.
	 * 
	 * @param substanceFamily Substance family to get number of substances source of.
	 * @return A data source regarding the number of substances of a given family.
	 */
	public FamilyDiversitySource getFamilyDiversitySource(SubstanceFamily substanceFamily) {
		return new FamilyDiversitySource(substanceFamily);
	}
	
	/**
	 * Return a copy of the current global substance concentrations.
	 * This method is not thread-safe and should only be invoked when
	 * the simulation is paused.
	 * 
	 * @return A copy of the current global substance concentrations.
	 */
	public Map<Substance, Float> getSubConMapCopy() {
		Map<Substance, Float> conCopy = new TreeMap<Substance, Float>(); 
		Iterator<Substance> subIter = concentrations.keySet().iterator();
		while (subIter.hasNext()) {
			Substance sub = subIter.next();
			conCopy.put(sub, concentrations.get(sub));
		}
		return conCopy;
	}

	/**
	 * Return substance by name.
	 * 
	 * @param name Name of substance.
	 * @return Substance with given name.
	 * @throws SubstanceException If there is no substance with the given name.
	 * @see SubstanceProvider#getSubstanceByName(String)
	 */
	public Substance getSubstanceByName(String name) throws SubstanceException {
		Iterator<Substance> iter = substances.iterator();
		while (iter.hasNext()) {
			Substance sub = iter.next();
			if (sub.getName().compareTo(name) == 0)
				return sub;
		}
		iter = substances.iterator();
		while (iter.hasNext()) {
			Substance sub = iter.next();
			String subName = SubstanceUtils.subNameRemoveAppend(sub);
			if (subName.compareTo(name) == 0)
				return sub;
		}
		throw new SubstanceException("Substance '" + name + "' does not exist!");
	}
	
	/**
	 * Return substance family by name.
	 * 
	 * @param name Name of substance family.
	 * @return Substance family with given name.
	 * @throws SubstanceException If it's not possible to get substance family by name.
	 */
	public SubstanceFamily getSubstanceFamilyByName(String name) throws SubstanceException {
		Iterator<SubstanceFamily> iter = families.iterator();
		while (iter.hasNext()) {
			SubstanceFamily subFam = iter.next();
			if (subFam.getName().compareTo(name) == 0)
				return subFam;
		}
		throw new SubstanceException("Substance family '" + name + "' does not exist!");
	}

	/**
	 * Update concentration map with substance concentrations in a given cell.
	 * 
	 * @param localSubCon The concentration of substances of a given cell.
	 */
	public void updateConcentrations(Map<Substance, Float> localSubCon) {
		Iterator<Substance> iter = localSubCon.keySet().iterator();
		while (iter.hasNext()) {
			Substance sub = (Substance) iter.next();
			synchronized(substances) {
				Float con = concentrations.get(sub);
				if (con == null) {
					con = 0.0f;
					if (!substances.contains(sub)) {
						substances.add(sub);
					}
				}
				concentrations.put(sub, con + localSubCon.get(sub));
			}
		}
	}
	
	/**
	 * Update cache of substance merge rules, to conform with possible new substances.
	 * @throws SubstanceException If not possible to update merge rules.
	 */
	public void updateMergeRules() throws SubstanceException {
		//TODO Could only check new substances, thus saving time.
		Vector<Substance> mergeables = new Vector<Substance>(substances);
		while (!mergeables.isEmpty()) {
			Substance sub = mergeables.firstElement();
			if (sub.isMergeable()) {
				for (int i = 1; i < mergeables.size(); i++) {
					mergeSubstances(sub, mergeables.get(i));
				}
			}
			mergeables.remove(0);
		}
	}
	
	/**
	 * Returns an iterator over the currently existing substance merging rules.
	 * @return An iterator over the currently existing substance merging rules.
	 */
	public Iterator<SubMergeRule> mergeRuleIterator() {
		return subMergeRules.iterator();
	}
	
	/**
	 * Resets substance manager to initial state in order to begin a new
	 * simulation.
	 */
	public void resetAll() {
		resetConcentrations();
		families.clear();
		families.addAll(familiesBak);
		substances.clear();
		substances.addAll(substancesBak);
		subFamilyMergeRules.clear();
		subFamilyMergeRules.addAll(subFamilyMergeRulesBak);
		System.gc();
	}
	
	/**
	 * Called after XML setup of SubstanceManager.
	 * Creates a backup of the SubstanceManager initial state.
	 */
	@Validate
	void validate() {
		familiesBak = new HashSet<SubstanceFamily>();
		familiesBak.addAll(families);
		substancesBak = new HashSet<Substance>();
		substancesBak.addAll(substances);
		subFamilyMergeRulesBak = new Vector<SubFamilyMergeRule>();
		subFamilyMergeRulesBak.addAll(subFamilyMergeRules);	
	}
}
