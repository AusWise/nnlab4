import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author auswise
 */
public class NeuralNetwork {
    
    JFrame frame;
    
    double [][][] ws;
    
    int N,B,C;
    BiFunction<double[], double[], Double> d;
    Function<Double, Double> f;
    
    double alpha_max, alpha_min;
    double R_max, R_min;
    double t_max;
    int t;
    
    private int arg_min_i, arg_min_j;
    private double [][] distanceMatrix;
    
    public NeuralNetwork(int B, int C, int N, BiFunction<double[], double[], Double> d, Function<Double, Double> f){
        this(B, C, N);
        this.d = d;
        this.f = f;
    }
    
    public NeuralNetwork(int B, int C, int N){
        this.N = N;
        this.B = B;
        this.C = C;
        ws = new double [B][C][N];
//        for(int i=0;i<B;i++)
//            for(int j=0;j<C;j++)
//                for(int k=0;k<N;k++)
//                    ws[i][j][k] = 1.0D;
        
        double [] white = {1,1,0};
        double [] red = {1.0D, 0.0D, 0.0D};
        double [] green = {0.0D, 1.0D, 0.0D};
        double [] blue = {0.0D, 0.0D, 1.0D};
        
        ws[0][0] = white;
        ws[0][C-1] = red;
        ws[B-1][0] = green;
        ws[B-1][C-1] = blue;
                
        d = new EuclidesDistance();
        f = d -> Math.exp(-d*d/(2*R(t)*R(t)));
//        f = d -> d<R(t) ? 1.0 : 0;
    }
    
    public double[][][] getMap(){
        return ws;
    }
    
    public double[][] evaluate(double [] x){
        x = normalize(x);
        double [][] y = new double [B][C];
    
        for(int i=0;i<B;i++)
            for(int j=0;j<C;j++)
                y[i][j] = dot(x, ws[i][j]);
        
        return y;
    } 
    
    public void learn(List<double []> xs, double t_max, double alpha_min, double alpha_max, double R_min, double R_max){
        this.t_max = t_max;
        this.alpha_min = alpha_min;
        this.alpha_max = alpha_max;
        this.R_min = R_min;
        this.R_max = R_max;
        
        double [] x;
        for(t=0;t<t_max;t++){
            for(double [] xi : xs){
//                Collections.shuffle(xs);
                x = normalize(xi);
                distanceMatrix = distanceMatrix(x);
                argMinDistance(distanceMatrix);
                for(int i=0;i<B;i++)
                    for(int j=0;j<C;j++)
                        for(int k=0;k<N;k++)
                            if(distance(i,j,arg_min_i, arg_min_j)<R(t)){
                                ws[i][j][k] += alpha(t) * f.apply(distance(i,j,arg_min_i, arg_min_j)) * (xi[k]-ws[i][j][k]);
                                ws[i][j][k] = Math.max(Math.min(ws[i][j][k], 1.0D),0.0D);
                            }
            }
            
            if(frame!=null)
                frame.repaint();
        }
//        normalize();
    }
    
    private void normalize(){
        for(int i=0;i<B;i++)
            for(int j=0;j<C;j++)
                ws[i][j] = normalize(ws[i][j]);
    }
    
    private double[] normalize(double [] x){
        double norm = norm(x);
        double [] xprim = new double [x.length];
        
        for(int i=0;i<x.length;i++)
            xprim[i] = x[i]/norm;
        
        return xprim;
    }
    
    private double norm(double [] x){
        return Math.sqrt(this.dot(x, x));
    }
   
    private double alpha(double t){
        double a = alpha_min/alpha_max;
        double n = t/t_max;
        
        return alpha_max*Math.pow(a, n);
    }
    
    private double R(double t){
        double a = R_min/R_max;
        double n = t/t_max;
        
        return R_max*Math.pow(a, n);
    }
    
    private double distance(int x1, int y1, int x2, int y2){
        double x = x1 - x2;
        double y = y1 - y2;
        return Math.sqrt(x*x + y*y);
    }
    
    private double dot(double [] x, double [] y){
        if(x.length != y.length)
            new RuntimeException();
        
        double dot = 0;
        for(int i=0;i<x.length;i++)
            dot += x[i]*y[i];
        
        return dot;
    }
    
    private double[][] distanceMatrix(double [] x){
       double [][] distanceMatrix = new double[B][C];
       
       for(int i=0;i<B;i++)
            for(int j=0;j<C;j++)
                distanceMatrix[i][j] = d.apply(x, ws[i][j]);
       
       return distanceMatrix;
    }
    
    private void argMinDistance(double [][] distanceMatrix){
        arg_min_i=0;
        arg_min_j=0;
        
        double min_d = distanceMatrix[0][0];
        for(int i=0;i<B;i++)
            for(int j=0;j<C;j++)
                if(min_d > distanceMatrix[i][j]){
                    min_d = distanceMatrix[i][j];
                    arg_min_i = i;
                    arg_min_j = j;
                }
                else if(min_d == distanceMatrix[i][j] && Math.random()<0.5){
                    arg_min_i = i;
                    arg_min_j = j;
                }
    }
    
    private class EuclidesDistance implements BiFunction<double[], double [], Double>{

        @Override
        public Double apply(double[] x, double[] y) {
            if(x.length != y.length)
                new RuntimeException();
        
            double dot = 0.0;
            double t;
            for(int i=0;i<x.length;i++){
                t = x[i]-y[i];
                dot += t*t;
            }
            
            return Math.sqrt(dot);
        }
    }
    
    public void setFrame(JFrame frame){
        this.frame = frame;
    }
}
