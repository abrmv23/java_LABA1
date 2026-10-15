public class ComplexMatrix {
    private final ComplexNumber[][] matrix;
    private final int rows;
    private final int cols;

    public ComplexMatrix(int rows, int cols) {
        if (rows < 0 || cols < 0) {
            throw new IllegalArgumentException("Matrix dimensions cannot be negative");
        }
        this.rows = rows;
        this.cols = cols;
        this.matrix = new ComplexNumber[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = new ComplexNumber(0, 0);
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public ComplexNumber getValue(int row, int col) {
        checkBounds(row, col);
        return matrix[row][col];
    }

    public void setValue(int row, int col, ComplexNumber value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        checkBounds(row, col);
        matrix[row][col] = new ComplexNumber(value.getReal(), value.getImaginary());
    }

    private void checkBounds(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Index [" + row + "][" + col
                    + "] is out of bounds for matrix " + rows + "x" + cols);
        }
    }

    private void requireSameSize(ComplexMatrix other, String operation) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrices must have the same dimensions for the "
                    + operation + " operation: " + this.rows + "x" + this.cols
                    + " and " + other.rows + "x" + other.cols);
        }
    }

    public ComplexMatrix add(ComplexMatrix other) {
        requireSameSize(other, "addition");
        ComplexMatrix result = new ComplexMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.setValue(i, j, getValue(i, j).add(other.getValue(i, j)));
            }
        }
        return result;
    }

    public ComplexMatrix sub(ComplexMatrix other) {
        requireSameSize(other, "subtraction");
        ComplexMatrix result = new ComplexMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.setValue(i, j, getValue(i, j).sub(other.getValue(i, j)));
            }
        }
        return result;
    }

    public ComplexMatrix mul(ComplexMatrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("The number of columns of the first matrix (" + this.cols
                    + ") must equal the number of rows of the second (" + other.rows + ")");
        }
        ComplexMatrix result = new ComplexMatrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                ComplexNumber sum = new ComplexNumber(0, 0);
                for (int k = 0; k < this.cols; k++) {
                    sum = sum.add(getValue(i, k).mul(other.getValue(k, j)));
                }
                result.setValue(i, j, sum);
            }
        }
        return result;
    }

    public ComplexMatrix mul(ComplexNumber scalar) {
        ComplexMatrix result = new ComplexMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.setValue(i, j, getValue(i, j).mul(scalar));
            }
        }
        return result;
    }

    public ComplexMatrix transpose() {
        ComplexMatrix result = new ComplexMatrix(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.setValue(j, i, getValue(i, j));
            }
        }
        return result;
    }

    public ComplexNumber determinant() {
        if (rows != cols) {
            throw new IllegalArgumentException("The determinant exists only for square matrices");
        }
        return determinantRec();
    }

    private ComplexNumber determinantRec() {
        int n = rows;
        if (n == 0) return new ComplexNumber(1, 0);
        if (n == 1) return matrix[0][0];
        ComplexNumber sum = new ComplexNumber(0, 0);
        for (int j = 0; j < n; j++) {
            ComplexNumber term = matrix[0][j].mul(minor(0, j).determinantRec());
            sum = (j % 2 == 0) ? sum.add(term) : sum.sub(term);
        }
        return sum;
    }

    private ComplexMatrix minor(int row, int col) {
        ComplexMatrix result = new ComplexMatrix(rows - 1, cols - 1);
        for (int i = 0, mi = 0; i < rows; i++) {
            if (i == row) continue;
            for (int j = 0, mj = 0; j < cols; j++) {
                if (j == col) continue;
                result.setValue(mi, mj++, matrix[i][j]);
            }
            mi++;
        }
        return result;
    }

    private void swapRows(int r1, int r2) {
        ComplexNumber[] tmp = matrix[r1];
        matrix[r1] = matrix[r2];
        matrix[r2] = tmp;
    }

    public ComplexMatrix inverse() {
        if (rows != cols) {
            throw new IllegalArgumentException("The inverse matrix exists only for square matrices");
        }
        int n = rows;
        if (n == 0) return new ComplexMatrix(0, 0);

        ComplexMatrix aug = new ComplexMatrix(n, 2 * n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aug.setValue(i, j, getValue(i, j));
            }
            aug.setValue(i, n + i, new ComplexNumber(1, 0));
        }

        for (int col = 0; col < n; col++) {
            int pivotRow = col;
            double best = 0;
            for (int r = col; r < n; r++) {
                ComplexNumber cand = aug.getValue(r, col);
                double mag = Math.hypot(cand.getReal(), cand.getImaginary());
                if (mag > best) {
                    best = mag;
                    pivotRow = r;
                }
            }
            if (best < 1e-12) {
                throw new ArithmeticException("The matrix is singular, no inverse exists");
            }
            if (pivotRow != col) {
                aug.swapRows(pivotRow, col);
            }

            ComplexNumber pivot = aug.getValue(col, col);
            for (int j = 0; j < 2 * n; j++) {
                aug.setValue(col, j, aug.getValue(col, j).div(pivot));
            }

            for (int r = 0; r < n; r++) {
                if (r == col) continue;
                ComplexNumber factor = aug.getValue(r, col);
                if (factor.getReal() == 0 && factor.getImaginary() == 0) continue;
                for (int j = 0; j < 2 * n; j++) {
                    aug.setValue(r, j, aug.getValue(r, j).sub(factor.mul(aug.getValue(col, j))));
                }
            }
        }

        ComplexMatrix result = new ComplexMatrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result.setValue(i, j, aug.getValue(i, n + j));
            }
        }
        return result;
    }

    public ComplexMatrix div(ComplexMatrix other) {
        return this.mul(other.inverse());
    }

    
    public ComplexMatrix copy() {
        ComplexMatrix result = new ComplexMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.setValue(i, j, getValue(i, j));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String[][] cells = new String[rows][cols];
        int width = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = matrix[i][j].toString();
                width = Math.max(width, cells[i][j].length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(cells[i][j]);
                if (j < cols - 1) {
                    sb.append(" ".repeat(Math.max(1, width - cells[i][j].length() + 2)));
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}