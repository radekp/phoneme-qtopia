/*
 *
 *
 * Copyright  1990-2009 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package com.sun.midp.pki;

import java.io.ByteArrayOutputStream;

/**
 * A packed array of booleans.
 */

public class BitArray {

    private byte[] repn;
    private int length;

    private static final int BITS_PER_UNIT = 8;

    private static int subscript(int idx) {
        return idx / BITS_PER_UNIT;
    }

    private static int position(int idx) { // bits big-endian in each unit
        return 1 << (BITS_PER_UNIT - 1 - (idx % BITS_PER_UNIT));
    }

    /**
     * Creates a BitArray of the specified size, initialized to zeros.
     */
    public BitArray(int length) throws IllegalArgumentException {
        if (length < 0) {
            throw new IllegalArgumentException("Negative length for BitArray");
        }

        this.length = length;

        repn = new byte[(length + BITS_PER_UNIT - 1)/BITS_PER_UNIT];
    }


    /**
     * Creates a BitArray of the specified size, initialized from the
     * specified byte array.  The most significant bit of a[0] gets
     * index zero in the BitArray.  The array a must be large enough
     * to specify a value for every bit in the BitArray.  In other words,
     * 8*a.length <= length.
     */
    public BitArray(int length, byte[] a) throws IllegalArgumentException {

        if (length < 0) {
            throw new IllegalArgumentException("Negative length for BitArray");
        }
        if (a.length * BITS_PER_UNIT < length) {
            throw new IllegalArgumentException("Byte array too short to represent " +
                                               "bit array of given length");
        }

        this.length = length;

        int repLength = ((length + BITS_PER_UNIT - 1)/BITS_PER_UNIT);
        int unusedBits = repLength*BITS_PER_UNIT - length;
        byte bitMask = (byte) (0xFF << unusedBits);

        /*
         normalize the representation:
          1. discard extra bytes
          2. zero out extra bits in the last byte
         */
        repn = new byte[repLength];
        System.arraycopy(a, 0, repn, 0, repLength);
        if (repLength > 0) {
            repn[repLength - 1] &= bitMask;
        }
    }

    /**
     * Create a BitArray whose bits are those of the given array
     * of Booleans.
     */
    public BitArray(boolean[] bits) {
        length = bits.length;
        repn = new byte[(length + 7)/8];

        for (int i=0; i < length; i++) {
            set(i, bits[i]);
        }
    }


    /**
     *  Copy constructor (for cloning).
     */
    private BitArray(BitArray ba) {
        length = ba.length;
        repn = copyOf(ba.repn, ba.repn.length);
    }

    /**
     *  Returns the indexed bit in this BitArray.
     */
    public boolean get(int index) throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
        }

        return (repn[subscript(index)] & position(index)) != 0;
    }

    /**
     *  Sets the indexed bit in this BitArray.
     */
    public void set(int index, boolean value)
            throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
        }
        int idx = subscript(index);
        int bit = position(index);

        if (value) {
            repn[idx] |= bit;
        } else {
            repn[idx] &= ~bit;
        }
    }

    /**
     * Returns the length of this BitArray.
     */
    public int length() {
        return length;
    }

    /**
     * Returns a Byte array containing the contents of this BitArray.
     * The bit stored at index zero in this BitArray will be copied
     * into the most significant bit of the zeroth element of the
     * returned byte array.  The last byte of the returned byte array
     * will be contain zeros in any bits that do not have corresponding
     * bits in the BitArray.  (This matters only if the BitArray's size
     * is not a multiple of 8.)
     */
    public byte[] toByteArray() {
        if (repn == null) {
            return null;
        }
        return copyOf(repn, repn.length);
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || !(obj instanceof BitArray)) return false;

        BitArray ba = (BitArray) obj;

        if (ba.length != length) return false;

        for (int i = 0; i < repn.length; i += 1) {
            if (repn[i] != ba.repn[i]) return false;
        }
        return true;
    }

    /**
     * Return a boolean array with the same bit values a this BitArray.
     */
    public boolean[] toBooleanArray() {
        boolean[] bits = new boolean[length];

        for (int i=0; i < length; i++) {
            bits[i] = get(i);
        }
        return bits;
    }

    /**
     * Returns a hash code value for this bit array.
     *
     * @return  a hash code value for this bit array.
     */
    public int hashCode() {
        int hashCode = 0;

        for (int i = 0; i < repn.length; i++)
            hashCode = 31*hashCode + repn[i];

        return hashCode ^ length;
    }


    public Object clone() {
        return new BitArray(this);
    }


    private static final byte[][] NYBBLE = {
        { (byte)'0',(byte)'0',(byte)'0',(byte)'0'},
        { (byte)'0',(byte)'0',(byte)'0',(byte)'1'},
        { (byte)'0',(byte)'0',(byte)'1',(byte)'0'},
        { (byte)'0',(byte)'0',(byte)'1',(byte)'1'},
        { (byte)'0',(byte)'1',(byte)'0',(byte)'0'},
        { (byte)'0',(byte)'1',(byte)'0',(byte)'1'},
        { (byte)'0',(byte)'1',(byte)'1',(byte)'0'},
        { (byte)'0',(byte)'1',(byte)'1',(byte)'1'},
        { (byte)'1',(byte)'0',(byte)'0',(byte)'0'},
        { (byte)'1',(byte)'0',(byte)'0',(byte)'1'},
        { (byte)'1',(byte)'0',(byte)'1',(byte)'0'},
        { (byte)'1',(byte)'0',(byte)'1',(byte)'1'},
        { (byte)'1',(byte)'1',(byte)'0',(byte)'0'},
        { (byte)'1',(byte)'1',(byte)'0',(byte)'1'},
        { (byte)'1',(byte)'1',(byte)'1',(byte)'0'},
        { (byte)'1',(byte)'1',(byte)'1',(byte)'1'}
    };

    private static final int BYTES_PER_LINE = 8;

    /**
     *  Returns a string representation of this BitArray.
     */
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (int i = 0; i < repn.length - 1; i++) {
            out.write(NYBBLE[(repn[i] >> 4) & 0x0F], 0, 4);
            out.write(NYBBLE[repn[i] & 0x0F], 0, 4);

            if (i % BYTES_PER_LINE == BYTES_PER_LINE - 1) {
                out.write('\n');
            } else {
                out.write(' ');
            }
        }

        // in last byte of repn, use only the valid bits
        for (int i = BITS_PER_UNIT * (repn.length - 1); i < length; i++) {
            out.write(get(i) ? '1' : '0');
        }

        return new String(out.toByteArray());

    }

    public BitArray truncate() {
        for (int i=length-1; i>=0; i--) {
            if (get(i)) {
                return new BitArray(i+1,
                        copyOf(repn, (i + BITS_PER_UNIT)/BITS_PER_UNIT));
            }
        }
        return new BitArray(1);
    }

    /**
     * Copies the specified array, truncating or padding with zeros
     * (if necessary) so the copy has the specified length. For all
     * indices that are valid in both the original array and the copy,
     * the two arrays will contain identical values. For any indices
     * that are valid in the copy but not the original, the copy will
     * contain (byte)0. Such indices will exist if and only if the
     * specified length is greater than that of the original array.
     * 
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded
     *         with zeros to obtain the specified length
     * @throws NullPointerException if original is null
     * @throws IllegalArgumentException if newLength is negative
     */
    private static byte[] copyOf(byte[] original, int newLength) {
        if (original == null) {
            throw new NullPointerException();
        }
        if (newLength < 0) {
            throw new IllegalArgumentException();
        }

        byte[] res = new byte[newLength];
        if (newLength <= original.length) {
            System.arraycopy(original, 0, res, 0, newLength);
        } else {
            System.arraycopy(original, 0, res, 0, original.length);
            for (int i = original.length; i < newLength; i++) {
                res[i] = 0;
            }
        }

        return res;
    }
}
