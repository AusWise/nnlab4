import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
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
public class Colors {
    
    private static final Random RANDOM = new Random();
    
    private NeuralNetwork network;
    
    public Colors(){
        int B = 25;
        int C = 25;
        int N = 3;
        network = new NeuralNetwork(B, C, N);
    }
    
    public static void main(String [] args) throws IOException{
        Colors colors = new Colors();
        
        NeuralNetwork network = colors.getNetwork();
        
        double R_min = 1;
        double R_max = 17;
        double alpha_min = 1.0;
        double alpha_max = 2.0;
        double t_max = 10;
        
        List<double []> trainingSet = colors.trainingSet();
        
        double[][][] map = network.getMap();
        
        int a = 10;
        JFrame frame = new JFrame(){
            
            @Override
            public void paint(Graphics g){
                Graphics2D g2d = (Graphics2D)g;
                for(int i=0;i<map.length;i++)
                    for(int j=0;j<map[i].length;j++){
                        g2d.setColor(colors.color(map[i][j]));
                        g2d.fillRect(a*i, a*j, a, a);
                    }
            }
        };
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        network.setFrame(frame);
        network.learn(trainingSet, t_max, alpha_min, alpha_max, R_min, R_max);
        
        BufferedImage image = new BufferedImage(map[0].length*a, map.length*a, BufferedImage.TYPE_INT_ARGB);
        
        Graphics g = image.getGraphics();
        Graphics2D g2d = (Graphics2D)g;
        
        
        for(int i=0;i<map.length;i++)
            for(int j=0;j<map[i].length;j++){
                g2d.setColor(colors.color(map[i][j]));
                g2d.fillRect(j*a, i*a, a, a);
            }
        
        ImageIO.write(image, "png", new File("/home/auswise/Documents/NetBeansProjects/nnlab4/image.png"));
    }
    
    public NeuralNetwork getNetwork(){
        return network;
    }
    
    public List<double[]> trainingSet(){
        List<double []> trainingSet = new LinkedList<double []>();
        
        trainingSet.add(pattern(255,0,0));
        trainingSet.add(pattern(0,255,0));
        trainingSet.add(pattern(0,0,255));
        trainingSet.add(pattern(255,255,0));

        return trainingSet;
    }
    
    public double [] pattern(double r, double g, double b){
        double [] color = {r,g,b};
        return color;
    }
    
    public Color color(double [] pattern){
        int r = (int)(255.0*pattern[0]);
        int g = (int)(255.0*pattern[1]);
        int b = (int)(255.0*pattern[2]);
        
        return new Color(r, g, b);
    }
}
