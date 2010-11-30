package com.oe.ourvillage;


/**
 * A class which performs matrix math in an efficient manner.
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *<P>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *<P>
 *<A HREF="http://www.gnu.org/licenses/gpl.txt">GNU General Public License</A>
 * 
 * @author Steven Wallace
 * @version 20041105
 */
public class Matrix {
    private final int cols;
    private final double data[][];
    private final int rows;

    /**
     * Creates a matrix represented by the two dimensional array provided.
     * 
     * @param data
     *        Two dimentional array to convert to a matrix.
     */
    public Matrix(double data[][]) {
        rows = data.length;
        cols = data[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    /**
     * Creates a vector (1xN matrix) represented by the array provided.
     * 
     * @param data
     *        Array to convert to a 1xN matrix.
     */
    public Matrix(double data[]) {
        rows = 1;
        cols = data.length;
        this.data = new double[rows][cols];
        for (int i = 0; i < data.length; i++) {
            this.data[0][i] = data[i];
        }
    }

    /**
     * Creates a matrix represented by the two dimensional array provided.
     * 
     * @param data
     *        Two dimentional array to convert to a matrix.
     */
    public Matrix(int data[][]) {
        rows = data.length;
        cols = data[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] = (double) data[i][j];
            }
        }
    }

    /**
     * Creates a vector (1xN matrix) represented by the array provided.
     * 
     * @param data
     *        Array to convert to a 1xN matrix.
     */
    public Matrix(int data[]) {
        rows = 1;
        cols = data.length;
        this.data = new double[rows][cols];
        for (int i = 0; i < data.length; i++) {
            this.data[0][i] = data[i];
        }
    }

    /**
     * Create a zero matrix of dimensions rows x cols
     * 
     * @param rows
     *        Number of rows in matrix.
     * @param cols
     *        Number of columns in the matrix.
     */
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        data = new double[rows][cols];
    }

    public static Matrix hardlim(Matrix m) {
        Matrix output = new Matrix(m.rows, m.cols);

        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                output.data[i][j] = (m.data[i][j] > 0) ? 1 : 0;
            }
        }

        return output;
    }

    /**
     * Returns the matrix which is the input matrix scaled by the input scalar.
     * 
     * @param m
     *        Matrix to scale.
     * @param s
     *        Scaling factor.
     * @return Matrix m scaled by s.
     */
    public static Matrix multiply(Matrix m, double s) {
        Matrix output = new Matrix(m.rows, m.cols);

        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                output.data[i][j] = m.data[i][j] * s;
            }
        }
        return output;
    }

    /**
     * Returns the matrix which is dot product of m1 and m2.
     * 
     * @param m1
     *        Matrix on the left of dot product.
     * @param m2
     *        Matrix on the right of the dot procut.
     * @return The dot product of m1 and m2;
     * @throws InvalidDimensionsException
     *         when the dot product of the two matrices cannot be taken.
     */
    public static Matrix multiply(Matrix m1, Matrix m2) throws InvalidDimensionsException {
        // Verify correct dimensions
        if (m1.cols != m2.rows) {
            throw new InvalidDimensionsException("Cannot multiply a " + m1.rows + "x"
                                                 + m2.cols + " matrix by a " + m2.rows + "x"
                                                 + m2.cols + " matrix.");
        }

        Matrix output = new Matrix(m1.rows, m2.cols);
        for (int i = 0; i < m1.rows; i++) {
            for (int j = 0; j < m2.cols; j++) {
                for (int k = 0; k < m1.cols; k++) {
                    output.data[i][j] += m1.data[i][k] * m2.data[k][j];
                }
            }
        }

        return output;
    }

    // Like new Matrix(int input[]), except returns an Nx1 matrix.
    public static Matrix transpose(int[] input) {
        Matrix output = new Matrix(input.length, 1);

        for (int i = 0; i < input.length; i++) {
            output.data[i][0] = input[i];
        }
        return output;
    }

    public static Matrix transpose(int[][] input) {
        Matrix output = new Matrix(input[0].length, input.length);

        for (int i = 0; i < input[0].length; i++) {
            for (int j = 0; j < input.length; j++) {
                output.data[j][i] = input[i][j];
            }
        }

        return output;
    }

    /**
     * Returns the transpose of the input matrix.
     * 
     * @param m
     *        The matrix to transpose.
     * @return The transpose of m.
     */
    public static Matrix transpose(Matrix m) {
        Matrix output = new Matrix(m.cols, m.rows);

        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                output.data[j][i] = m.data[i][j];
            }
        }
        return output;
    }

    /**
     * Returns the matrix which is the sum of this and the input matrix.<BR>
     * <B>this</B> is not modified.
     * 
     * @param matrix
     *        the matrix to add to <B>this</B>.
     * @return The sum of <B>this</B> and the input matrix.
     * @throws InvalidDimensionsException
     *         when both matrices have different dimensions.
     */
    public Matrix add(Matrix matrix) throws InvalidDimensionsException {
        // Make sure that they are same dimentions
        if (rows != matrix.rows || cols != matrix.cols) {
            throw new InvalidDimensionsException("Cannot add a " + matrix.rows + "x"
                                                 + matrix.cols + " matrix to a " + rows + "x"
                                                 + cols + " matrix.");
        }

        Matrix output = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                output.data[i][j] = data[i][j] + matrix.data[i][j];
            }
        }
        return output;
    }

    public int binaryVectorToNumericalValue() {
        int output = 0;

        if (rows == 1) {
            for (int i = 0; i < cols; i++) {
                output += data[0][i] * Math.pow(2, i);
            }
        } else if (cols == 1) {
            for (int i = 0; i < rows; i++) {
                output += data[i][0] * Math.pow(2, i);
            }
        } else {
            return -1;
        }
        return output;
    }

    public double get(int row, int col) throws InvalidDimensionsException {
        if (row >= rows || col >= cols) {
            throw new InvalidDimensionsException("Cannot return the value at (" + row + ", "
                                                 + col + ") for a " + rows + "x" + cols
                                                 + " matrix.");
        }

        return data[row][col];
    }

    /**
     * Returns the number of colums in this matrix.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns the number of rows in this matrix.
     */
    public int getRows() {
        return rows;
    }

    // Other useful operations to perform on a matrix
    public Matrix hardlim() {
        Matrix output = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                output.data[i][j] = (data[i][j] > 0) ? 1 : 0;
            }
        }

        return output;
    }

    /**
     * Returns the matrix which is this scaled by the input scalar.<BR>
     * <B>this</B> is not modified.
     * 
     * @param scalar
     *        scaling factor.
     * @return <B>this</B> scaled by the input scalar.
     */
    public Matrix multiply(double scalar) {
        Matrix output = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                output.data[i][j] = data[i][j] * scalar;
            }
        }
        return output;
    }

    /**
     * Returns the matrix which is this scaled by the input scalar.<BR>
     * <B>this</B> is not modified.
     * 
     * @param scalar
     *        scaling factor.
     * @return <B>this</B> scaled by the scaling factor.
     */
    public Matrix multiply(int scalar) {
        Matrix output = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                output.data[i][j] = data[i][j] * scalar;
            }
        }
        return output;
    }

    /**
     * Returns the matrix which is the dot product of this and the input matrix.<BR>
     * <B>this</B> is not modified.
     * 
     * @param matrix
     *        Matrix to multiply by <B>this</B>.
     * @return The dot product of <B>this</B> and the input matrix.
     * @throws InvalidDimensionsException
     *         when the a dot product cannot be taken from the two matrices.
     */
    public Matrix multiply(Matrix matrix) throws InvalidDimensionsException {
        // Make sure that they are of fit dimensions
        if (cols != matrix.rows) {
            throw new InvalidDimensionsException("Cannot multiply a " + rows + "x" + cols
                                                 + " matrix by a " + matrix.rows + "x"
                                                 + matrix.cols + " matrix.");
        }

        Matrix output = new Matrix(rows, matrix.cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < matrix.cols; j++) {
                for (int l = 0; l < cols; l++) {
                    output.data[i][j] += data[i][l] * matrix.data[l][j];
                }
            }
        }
        return output;
    }

    /**
     * Prints contents of the matrix to the console
     */
    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(data[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Sets the element at (row, col) to the input value.
     */
    public void set(int row, int col, double value) throws InvalidDimensionsException {
        if (row >= rows || col >= cols) {
            throw new InvalidDimensionsException("Cannot set the value (" + row + ", " + col
                                                 + ") component of a " + rows + "x" + cols
                                                 + " matrix.");
        }

        data[row][col] = value;
    }

    /**
     * Returns the matrix which is the difference between this and the input
     * matrix.<BR>
     * <B>this</B> is not modified.
     */
    public Matrix subtract(Matrix matrix) throws InvalidDimensionsException {
        // Make sure that they are same direction
        if (rows != matrix.rows || cols != matrix.cols) {
            throw new InvalidDimensionsException("Cannot subtract a " + matrix.rows + "x"
                                                 + matrix.cols + " matrix from a " + rows
                                                 + "x" + cols + " matrix.");
        }

        Matrix output = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                output.data[i][j] = data[i][j] - matrix.data[i][j];
            }
        }
        return output;
    }

    /**
     * Returns the transpose of this.<BR>
     * <B>this</B> is not modified.
     * 
     * @return The transpose of <B>this</B>.
     */
    public Matrix transpose() {
        Matrix output = new Matrix(cols, rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                output.data[j][i] = data[i][j];
            }
        }
        return output;
    }
}


class InvalidDimensionsException extends Exception {
    public InvalidDimensionsException() {
        super();
    }

    public InvalidDimensionsException(String str) {
        super(str);
    }
}


class MethodNotImplementedException extends Exception {
}
