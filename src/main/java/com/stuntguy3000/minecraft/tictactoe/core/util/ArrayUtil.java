/*
 * MIT License
 *
 * Copyright (c) 2020 Luke Anderson (stuntguy3000)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.stuntguy3000.minecraft.tictactoe.core.util;

public class ArrayUtil {

    /**
     * Tests if all elements in an array are identical
     *
     * Adapted from https://stackoverflow.com/questions/21170608/in-java-how-can-i-test-if-an-array-contains-the-same-value
     *
     * @param acceptNull boolean true to accept null objects for comparison, false to instant-reject the array
     * @param array array the input array
     * @return true if identical
     */
    public static boolean testIfElementsIdentical(boolean acceptNull, Object... array) {
        boolean isFirstElementNull = array[0] == null;
        for (int i = 1; i < array.length; i++) {
            if (isFirstElementNull) {
                if (!acceptNull) {
                    return false;
                }

                if (array[i] != null) {
                    return false;
                }
            } else {
                if (!array[0].equals(array[i])) {
                    return false;
                }
            }
        }

        return true;
    }
}
