/*
 * @Author: Matilde Pato (mpato)
 * @Date: 2023-11-27 09:30:00
 * @Last Modified time: 2023-11-27 09:30:00
 * ISEL - DEETC
 * Introdução a Sistemas de Informação
 * MPato, 2023-2024
 * 
 * NOTA:
 * This class requires that all constraints outlined in the instructions are met. 
 * Constraints described in the statement that have not been implemented in the DB.
 * For instance:
 * 1. System of bicycle gears, which can take {1, 6, 18, 24} as values. 
 *  It can be NULL if CLASSICA has no gears. The description can be changed or added to.
 * 2. Maintenance bike will not be available. 
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Restriction {

    // Restrição: uma bicicleta que se encontra em manutenção não pode ser reservada
    public static boolean verifyRestriction1(Bike bike) {
        return !bike.getEstado().equalsIgnoreCase("em manutenção");
    }

    // Restrição: o sistema de mudanças pode ser alterado dinamicamente
    public static boolean verifyRestriction2() {
        return true; // Sem restrição para alteração do sistema de mudanças
    }
}
