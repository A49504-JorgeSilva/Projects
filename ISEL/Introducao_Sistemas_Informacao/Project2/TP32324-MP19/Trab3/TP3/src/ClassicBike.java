/*
 * @Author: Matilde Pato (mpato)
 * @Date: 2023-11-27 09:30:00
 * @Last Modified time: 2023-12-06 18:30:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2023-2024
 * 
 * Class for the classic bike object in the CLASSICA table
 * Already implemented.
 */

public class ClassicBike extends Bike {
    private int nomudanca;

    // Constructor
    public ClassicBike(){
        super();
        nomudanca = 0;
    }

    public ClassicBike(int id, double peso, int raio, String modelo, String marca,
                        int mudanca, String estado, char atrdisc, int dispositivo,
                        int nomudanca) {
        super(id, peso, raio, modelo, marca, mudanca, estado, atrdisc, dispositivo);
        this.nomudanca = nomudanca;
    }

    public ClassicBike(String[] attr, int dispositivo){
        super(Integer.parseInt(attr[0]), Double.parseDouble(attr[1]), Integer.parseInt(attr[2]), 
            attr[3], attr[4], Integer.parseInt(attr[5]), attr[6], 'C', dispositivo);
        this.nomudanca = Integer.parseInt(attr[7]);
    } 

    // Getters and Setters specific to ElectricBike
    public int getNoMudanca() { return nomudanca; }

    public void setNoMudanca(int nomudanca) { this.nomudanca = nomudanca; }

    // Additional methods or functionality can be added as needed
}
