import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;

import LoadCSV;

import java.io.IOException;
import java.util.List;

public class LogisticRegressionModel{
    private RealVector weights;
    private double learningRate;
    private int iterations;
    private static String csvPath = "Your path";


    public LogisticRegressionModel(double lr, int i){
        this.learningRate = lr;
        this.iterations = i;
    }

    private double sigmoid(double z){
        return 1 / (1 + Math.exp(-z));
    }

    public void train(double[][] X, double[] y){
        int m = X.length;
        int n = X[0].length;
        weights = new ArrayRealVector(n);

        for (int iter = 0; iter < iterations; iter++){
            RealMatrix XMatrix = new Array2DRowRealMatrix(X);
            RealVector predictions = XMatrix.operate(weights).mapToSelf(this::sigmoid);
            RealVector errors = new ArrayRealVector(y).subtract(predictions);

            RealVector gradient = XMatrix.transpose().operate(errors).mapDivideToSelf(m);
            weights = weights.add(gradient.mapMultiply(learningRate));
        }
    }

    public double[] predictProbabilities(double[][] X){
        RealMatrix XMatrix = new Array2DRowRealMatrix(X);
        RealVector predictions = XMatrix.operate(weights).mapToSelf(this::sigmoid);
        return predictions.toArray();
    }

    public int[] predict(double[][] X){
        double[] probabilities = predictProbabilities(X);
        int[] predictions = new int[probabilities.length];
        for (int i = 0; i < probabilities.length; i++){
            predictions[i] = probabilities[i] > 0.5 ? 1 : 0;
        }
        return predictions;
    }

    public double computeAccuracy(int[] actual, int[] predictions){
        int correct = 0;
        for (int i = 0; i < actual.length; i++){
            if (actual[i] == predictions[i]){
                correct++;
            }
        }
        return (double) correct / actual.length;
    }

    public static double[][] normalize(double[][] X){
        int m = X.length;
        int n = X[0].length;
        double[][] normalized = new double[m][n];

        for (int j = 0; j < n; j++){
            double mean = 0, std = 0;

            for (int i = 0; i < m; i++){
                mean += X[i][j];
            }
            mean /= m;

            for (int i = 0; i < m; i++){
                std += Math.pow(X[i][j] - mean, 2);
            }
            std = Math.sqrt(std / m);

            for (int i = 0; i < m; i++){
                normalized[i][j] = (X[i][j] - mean) / std;
            }
        }
        return normalized;
    }

    private static int[] toIntArray(double[] array){
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++){
            result[i] = (int) array[i];
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        List<double[]> temp = LoadCSV.loadData(csvPath);
        double[][] data = temp.toArray(new double[0][]);
        double[][] X = new double[data.length][data[0].length-1];
        double[] y = new double[data.length];

        for (int i = 0; i < data.length; i++){
            System.arraycopy(data[i], 0, X[i], 0, data[i].length-1);
            y[i] = data[i][data[i].length-1];
        }

        X = normalize(X);

        int trainSize = (int) (0.8 * X.length);
        double[][] X_train = new double[trainSize][];
        double[] y_train = new double[trainSize];
        double[][] X_test = new double[X.length-trainSize][];
        double[] y_test = new double[X.length-trainSize];

        System.arraycopy(X, 0, X_train, 0, trainSize);
        System.arraycopy(y, 0, y_train, 0, trainSize);
        System.arraycopy(X, trainSize, X_test, 0, X.length-trainSize);
        System.arraycopy(y, trainSize, y_test, 0, y.length-trainSize);

        LogisticRegressionModel model = new LogisticRegressionModel(0.1, 1000);
        model.train(X_train, y_train);

        int[] predictions = model.predict(X_test);

        double accuracy = model.computeAccuracy(toIntArray(y_test), predictions);
        System.out.println("Model Accuracy: " + accuracy);
    }
}