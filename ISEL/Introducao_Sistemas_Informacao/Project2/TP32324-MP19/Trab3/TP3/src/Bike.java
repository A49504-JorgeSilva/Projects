/*
 * @Author: Matilde Pato (mpato)
 * @Date: 2023-11-27 09:30:00
 * @Last Modified time: 2023-12-06 18:30:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2023-2024
 * 
 * Class for the bike object in the BICICLETA table
 * Already implemented.
 */

class Bike {
    private int id;
    private double peso;
    private int raio;
    private String modelo;
    private String marca;
    private int mudanca;
    private String estado;
    private char atrdisc;
    private int dispositivo;
    
    // Constructors
    public Bike() {
        id = 0;
        peso = 0.0;
        raio = 0;
        modelo = new String();
        marca = new String();
        mudanca = 0;
        estado = new String();
        atrdisc = ' ';
        dispositivo = 0;
    }

    public Bike(int id, double peso, int raio, String modelo, String marca,
                int mudanca, String estado, char atrdisc, int dispositivo) {
        this.id = id;
        this.peso = peso;
        this.raio = raio;
        this.modelo = modelo;
        this.marca = marca;
        this.mudanca = mudanca;
        this.estado = estado;
        this.atrdisc = atrdisc;
        this.dispositivo = dispositivo;
    }      

    // Getters and Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public double getPeso() { return peso; }

    public void setPeso(double peso) { this.peso = peso; }

    public int getRaio() { return raio; }

    public void setRaio(int raio) { this.raio = raio; }

    public String getModelo() { return modelo; }

    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMarca() { return marca; }

    public void setMarca(String marca) { this.marca = marca; }

    public int getMudanca() { return mudanca; }

    public void setMudanca(int mudanca) { this.mudanca = mudanca; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public char getAtrdisc() { return atrdisc; }

    public void setAtrdisc(char atrdisc) { this.atrdisc = atrdisc; }

    public int getDispositivo() { return dispositivo; }

    public void setDispositivo(int dispositivo) { this.dispositivo = dispositivo; }

}
