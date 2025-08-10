/*
 * @Author: Matilde Pato (mpato)
 * @Date: 2023-11-27 09:30:00
 * @Last Modified time: 2023-12-06 18:30:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2023-2024
 * 
 * Class for the electric bike object in the ELECTRICA table
 * Already implemented.
 */

public class ElectricBike extends Bike {
    private double autonomia;
    private double velocidadeMaxima;

    // Constructor
    public ElectricBike(){
        super();
        autonomia = 0;
        velocidadeMaxima = 0;
    }

    public ElectricBike(int id, double peso, int raio, String modelo, String marca,
                        int mudanca, String estado, char atrdisc, int dispositivo,
                        double autonomia, double velocidadeMaxima) {
        super(id, peso, raio, modelo, marca, mudanca, estado, atrdisc, dispositivo);
        this.autonomia = autonomia;
        this.velocidadeMaxima = velocidadeMaxima;
    }

    public ElectricBike(String[] attr, int dispositivo){
        super(Integer.parseInt(attr[0]), Double.parseDouble(attr[1]), Integer.parseInt(attr[2]), 
            attr[3], attr[4], Integer.parseInt(attr[5]), attr[6], 'E', dispositivo);
        this.autonomia = Double.parseDouble(attr[7]);
        this.velocidadeMaxima = Double.parseDouble(attr[8]);
    } 

    // Getters and Setters specific to ElectricBike
    public double getAutonomia() { return autonomia; }

    public void setAutonomia(double autonomia) { this.autonomia = autonomia; }

    public double getVelocidadeMaxima() { return velocidadeMaxima; }

    public void setVelocidadeMaxima(double velocidadeMaxima) { this.velocidadeMaxima = velocidadeMaxima; }

    // Additional methods or functionality can be added as needed
}
