/*
 * @Author: Matilde Pato (mpato)
 * @Date: 2023-11-27 09:30:00
 * @Last Modified time: 2023-12-06 18:30:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2023-2024
 * 
 * Class for the dispositivo object referring to the DISPOSITIVO table.
 * Already implemented.
 */

public class GPSDevice {
    private int serialNumber;
    private double latitude;
    private double longitude;
    private int battery;


    // Constructors
    public GPSDevice() {
        serialNumber = 0;
        latitude = 0.0;
        longitude = 0.0;
        battery = 0;
    }

    public GPSDevice(int serialNumber, double latitude, double longitude, int battery) {
        serialNumber = serialNumber;
        latitude = latitude;
        longitude = longitude;
        battery = battery;
    }

    public GPSDevice(String[] attr){
       // Convert values to integer and double
       this.serialNumber = Integer.parseInt(attr[0]); 
       this.latitude = Double.parseDouble(attr[1]);
       this.longitude = Double.parseDouble(attr[2]);
       this.battery = Integer.parseInt(attr[3]);
    }  

    // Getters and Setters
    public int getSerialNumber() { return serialNumber; }

    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getBattery() { return battery; }

    public void setBattery(int battery) { this.battery = battery; }

    // Additional methods or functionality can be added as needed
}
