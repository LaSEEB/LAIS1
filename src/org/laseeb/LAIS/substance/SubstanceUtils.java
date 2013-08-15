package org.laseeb.LAIS.substance;

import org.laseeb.LAIS.utils.random.IRng;

/**
 * This class contains various methods for manipulating bit strings. 
 * <p>
 * Bit strings are represented by <code>long</code> numerical values.
 * <p>
 * These methods are used primarily by the {@link Substance Substance} class for cloning,
 * mutation and affinity determination.
 * 
 * @author Nuno Fachada
 *
 */
public class SubstanceUtils {
	
	/* Regular expression indicating the terminating substring of every substance name. */
	public static final String SUB_NAME_APPEND_REGEXP = " \\{[\\p{XDigit}]+\\}";
	
	/* Length of the bit string. */
	public static final int BITSTRINGSIZE = Long.SIZE;
	
	/**
	 * Adds the terminating substring to the name of the given substance.
	 * @param sub The substance to get the terminating substring for its name.
	 * @return The substance name with a terminating substring.
	 */
	public static String subNameAddAppend(Substance sub) {
		return sub.getName() + " {" + Long.toHexString(sub.getBitIdentifier()) + "}";
	}
	
	/**
	 * Removes the terminating substring of the name of the given substance.
	 * @param sub The substance from which to remove the terminating substring of its name.
	 * @return The substance name without a terminating substring.
	 */
	public static String subNameRemoveAppend(Substance sub) {
		 return sub.getName().replaceAll(SubstanceUtils.SUB_NAME_APPEND_REGEXP, "");
	}
	
	/**
	 * Performs a shift right operation on a bit string.
	 * 
	 * @param value Bit string where to perform shift right operation.
	 * @param shift Number of bits to shift.
	 * @return The bit string after shift right operation.
	 * @throws SubstanceException If not possible to perform shift.
	 */
	public static long shiftRight(long value, int shift) throws SubstanceException {
		if ((shift > BITSTRINGSIZE) || (shift < 0)) {
			throw new SubstanceException("The number " + shift + " is not a legal shift! Value must be between 0 and " + BITSTRINGSIZE + "!");
		}
		if (shift == BITSTRINGSIZE)
			return 0l;
		else
			return value >>> shift;
	}
	
	/**
	 * Performs a shift left operation on a bit string.
	 * 
	 * @param value Bit string where to perform shift left operation.
	 * @param shift Number of bits to shift.
	 * @return The bit string after shift left operation.
	 * @throws SubstanceException If not possible to perform shift.
	 */
	public static long shiftLeft(long value, int shift) throws SubstanceException {
		if ((shift > BITSTRINGSIZE) || (shift < 0)) {
			throw new SubstanceException("The number " + shift + " is not a legal shift! Value must be between 0 and " + BITSTRINGSIZE + "!");
		}
		if (shift == BITSTRINGSIZE)
			return 0l;
		else
			return value << shift;
	}

	/**
	 * Performs a XOR operation with the given bit strings between startBit and endBit.
	 * Bits out of this range are given the value zero.
	 *  
	 * @param bitId1 The first bit string.
	 * @param bitId2 The second bit string.
	 * @param startBit The start bit.
	 * @param endBit The end bit.
	 * @return A new bit string.
	 * @throws SubstanceException If not possible to perform XOR operation.
	 */
	public static long rangedXOR(long bitId1, long bitId2, int startBit, int endBit) throws SubstanceException {
		if (startBit > endBit) return 0l;
		/* Determine mask. */
		long mask = shiftLeft(-1l, startBit) & ~shiftLeft(-1l, endBit + 1);
		/* Perform XOR operation. */
		return (bitId1 ^ bitId2) & mask;			
	}
	
	/**
	 * Merge two substances bitId's, given start and stop bits, and
	 * return bitId of eventual new substance.
	 * @param sub1 First substance.
	 * @param sub2 Second substance.
	 * @param startBit Start bit of merging.
	 * @param endBit End bit of merging.
	 * @return Merged bitId of eventual new substance.
	 * @throws SubstanceException If not possible to perform merge operation.
	 */
	public static long merge(Substance sub1, Substance sub2, int startBit, int endBit) throws SubstanceException {
		/* Mask of first substance bit id. */
		long mask1;
		mask1 = shiftRight(-1l, BITSTRINGSIZE - startBit);
		/* Mask of second substance bit id. */
		long mask2 = -1l;
		mask2 = shiftLeft(-1l, endBit + 1);
		/* Return new substance bit id. */
		return (sub1.getBitIdentifier() & mask1)
			| (sub2.getBitIdentifier() & mask2)
			| (rangedXOR(sub1.getBitIdentifier(), sub2.getBitIdentifier(), startBit, endBit));
	}
	
	/**
	 * Returns the given <code>long</code> binary string as a formated <code>String</code>.
	 * Method used for debugging purposes. 
	 * @param x The long value to transform into a <code>String</code>.
	 * @return A <code>String</code> representing the given <code>long</code> binary string.
	 */
	public static String binStr(long x) {
		String str = Long.toBinaryString(x);
		if (Long.numberOfLeadingZeros(x) > 0) {
			char[] charSeq = new char[Long.numberOfLeadingZeros(x)];
			java.util.Arrays.fill(charSeq, '0');
			if (charSeq.length < BITSTRINGSIZE)
				str = new String(charSeq) + str;
			else
				str = new String(charSeq);
		}
		StringBuffer strBuf = new StringBuffer(str);
		int insertionPoint = 8;
		while (insertionPoint < strBuf.length()) {
			strBuf.insert(insertionPoint, "-");
			insertionPoint += 9;
		}
		return strBuf.toString();
	}

	/**
	 * Returns a new substance bit identifier based on the given substance bit identifier,
	 * but with possible mutations.
	 * 
	 * @param bitIdentifier Given bit identifier.
	 * @param rate Mutation rate.
	 * @param startBit The bit where the mutation is allowed to start.
	 * @param endBit The bit where the mutation must end.
	 * @param rng An instance of a random number generator.
	 * @return A new substance bit identifier.
	 * @throws SubstanceException If not possible to perform mutate operation.
	 */
	public static long mutate(long bitIdentifier, float rate, int startBit, int endBit, IRng rng) throws SubstanceException {
		/* Clone the bit identifier */
		long newBitId = bitIdentifier;
		/* Cycle through bits and mutate them accordingly to rate. */
		long mask = 1l;
		mask = shiftLeft(mask, startBit);
		for (int j = startBit; j <= endBit; j++) {
			if (rng.nextFloatFromTo(0, 1) < rate) {
				/* Mutate! */
				newBitId = newBitId ^ mask;
			}
			mask = shiftLeft(mask, 1);
		}
		return newBitId;
	}
}
