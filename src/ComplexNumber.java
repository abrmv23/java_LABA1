import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

final class ComplexNumber {

    private static final DecimalFormat FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        FORMAT = new DecimalFormat("#.##", symbols);
    }

    private final double real;
    private final double imaginary;

    public ComplexNumber() {
        this(0.0, 0.0);
    }

    public ComplexNumber(double real) {
        this(real, 0.0);
    }

    public ComplexNumber(double real, double imaginary) {
        this.real = real == 0.0 ? 0.0 : real;
        this.imaginary = imaginary == 0.0 ? 0.0 : imaginary;
    }

    public double getReal() { return real; }
    public double getImaginary() { return imaginary; }

    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(this.real + other.real, this.imaginary + other.imaginary);
    }

    public ComplexNumber sub(ComplexNumber other) {
        return new ComplexNumber(this.real - other.real, this.imaginary - other.imaginary);
    }

    public ComplexNumber mul(ComplexNumber other) {
        return new ComplexNumber(
                this.real * other.real - this.imaginary * other.imaginary,
                this.real * other.imaginary + this.imaginary * other.real);
    }

    public ComplexNumber div(ComplexNumber other) {
        if (other.real == 0 && other.imaginary == 0) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        double d = other.real * other.real + other.imaginary * other.imaginary;
        return new ComplexNumber(
                (this.real * other.real + this.imaginary * other.imaginary) / d,
                (this.imaginary * other.real - this.real * other.imaginary) / d);
    }

    private static String formatNumber(double value) {
        return FORMAT.format(value);
    }


    @Override
    public String toString() {
        double r = Math.abs(real) < 1e-12 ? 0.0 : real;
        double i = Math.abs(imaginary) < 1e-12 ? 0.0 : imaginary;
        double absImaginary = Math.abs(i);
        String sign = i > 0 ? " + " : " - ";

        if (r == 0 && i == 0) return "0";
        if (r == 0) return formatNumber(i) + "i";
        if (i == 0) return formatNumber(r);
        return formatNumber(r) + sign + formatNumber(absImaginary) + "i";
    }
}