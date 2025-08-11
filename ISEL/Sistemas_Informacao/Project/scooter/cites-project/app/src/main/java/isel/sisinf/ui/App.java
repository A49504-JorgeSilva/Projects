/*
MIT License

Copyright (c) 2025, Nuno Datia, Matilde Pato, ISEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package isel.sisinf.ui;

import java.util.Scanner;

import jakarta.persistence.*;

import java.math.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 
 * Didactic material to support 
 * to the curricular unit of 
 * Introduction to Information Systems 
 *
 * The examples may not be complete and/or totally correct.
 * They are made available for teaching and learning purposes and 
 * any inaccuracies are the subject of debate.
 */

interface DbWorker
{
    void doWork();
}
class UI
{
    private enum Option
    {
        // DO NOT CHANGE ANYTHING!
        Unknown,
        Exit,
        createCostumer,
        listCostumer,
        listDocks,
        startTrip,
        parkScooter,
        about
    }
    private static UI __instance = null;
  
    private HashMap<Option,DbWorker> __dbMethods;

    private UI()
    {
        // DO NOT CHANGE ANYTHING!
        __dbMethods = new HashMap<Option,DbWorker>();
        __dbMethods.put(Option.createCostumer, () -> UI.this.createCostumer());
        __dbMethods.put(Option.listCostumer, () -> UI.this.listCostumer()); 
        __dbMethods.put(Option.listDocks, () -> UI.this.listDocks());
        __dbMethods.put(Option.startTrip, new DbWorker() {public void doWork() {UI.this.startTrip();}});
        __dbMethods.put(Option.parkScooter, new DbWorker() {public void doWork() {UI.this.parkScooter();}});
        __dbMethods.put(Option.about, new DbWorker() {public void doWork() {UI.this.about();}});
    }

    public static UI getInstance()
    {
        // DO NOT CHANGE ANYTHING!
        if(__instance == null)
        {
            __instance = new UI();
        }
        return __instance;
    }

    private Option DisplayMenu()
    {
        Option option = Option.Unknown;
        Scanner s = new Scanner(System.in); //Scanner closes System.in if you call close(). Don't do it
        try
        {
            // DO NOT CHANGE ANYTHING!
            System.out.println("CITES Manadgement DEMO");
            System.out.println();
            System.out.println("1. Exit");
            System.out.println("2. Create Costumer");
            System.out.println("3. List Existing Costumer");
            System.out.println("4. List Docks");
            System.out.println("5. Start Trip");
            System.out.println("6. Park Scooter");
            System.out.println("8. About");
            System.out.print(">");
            int result = s.nextInt();
            option = Option.values()[result];
        }
        catch(RuntimeException ex)
        {
            //nothing to do.
        }
        
        return option;

    }
    private static void clearConsole() throws Exception
    {
        // DO NOT CHANGE ANYTHING!
        for (int y = 0; y < 25; y++) //console is 80 columns and 25 lines
            System.out.println("\n");
    }

    public void Run() throws Exception
    {
        // DO NOT CHANGE ANYTHING!
        Option userInput;
        do
        {
            clearConsole();
            userInput = DisplayMenu();
            clearConsole();
            try
            {
                __dbMethods.get(userInput).doWork();
                System.in.read();
            }
            catch(NullPointerException ex)
            {
                //Nothing to do. The option was not a valid one. Read another.
            }

        }while(userInput!=Option.Exit);
    }

    /**
    To implement from this point forward. 
    -------------------------------------------------------------------------------------     
        IMPORTANT:
    --- DO NOT MESS WITH THE CODE ABOVE. YOU JUST HAVE TO IMPLEMENT THE METHODS BELOW ---
    --- Other Methods and properties can be added to support implementation. 
    ---- Do that also below                                                         -----
    -------------------------------------------------------------------------------------
    
    */

    private static final int TAB_SIZE = 24;
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("dal-lab");

    

    public void createCostumer() {
        EntityManager em = emf.createEntityManager();  
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter tax number (integer): ");
        int taxNumber = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter registration date (yyyy-MM-dd): ");
        String dtRegisterStr = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dtRegisterStr, formatter);
        LocalDateTime dtRegister = localDate.atStartOfDay();

        System.out.print("Enter card type reference (resident/tourist): ");
        String cardTypeRef = scanner.nextLine();

        System.out.print("Enter initial credit (decimal): ");
        BigDecimal initialCredit = new BigDecimal(scanner.nextLine());

        System.out.println("Creating customer with:");
        System.out.println(email + ", " + taxNumber + ", " + name + ", " + dtRegister + ", " + cardTypeRef + ", " + initialCredit);

        Person person = new Person();
        person.setEmail(email);
        person.setTaxNumber(taxNumber);
        person.setName(name);
        em.persist(person);

        Client client = new Client();
        client.setPerson(person);
        client.setDtRegister(dtRegister);
        em.persist(client);

        TypeOfCard type = em.find(TypeOfCard.class, cardTypeRef);
        if (type == null) {
            throw new IllegalArgumentException("Card type not found: " + cardTypeRef);
        }

        Card card = new Card();
        card.setCredit(initialCredit);
        card.setTypeOfCard(type);
        card.setClient(client);
        em.persist(card);
    }

  
    public void listCostumer() {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c", Client.class);
        List<Client> clients = query.getResultList();

        for (Client client : clients) {
            Person p = client.getPerson();
            System.out.println("Customer ID: " + p.getId());
            System.out.println("Name: " + p.getName());
            System.out.println("Email: " + p.getEmail());
            System.out.println("Tax Number: " + p.getTaxNumber());
            System.out.println("Registration Date: " + client.getDtRegister());

            if (client.getCards() != null && !client.getCards().isEmpty()) {
                System.out.println("Cards:");
                client.getCards().forEach(card -> {
                    System.out.println("  Card ID: " + card.getId() +
                                       ", Type: " + card.getTypeOfCard() +
                                       ", Credit: " + card.getCredit());
                });
            } else {
                System.out.println("No cards found.");
            }
            System.out.println("-----------------------------");
        }
    }

    public void listDocks() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Dock> docks = em.createQuery("SELECT d FROM Dock d", Dock.class).getResultList();
            for (Dock dock : docks) {
                System.out.println("Dock #" + dock.getNumber());
                System.out.println("  State: " + dock.getState());
                Station station = dock.getStation();
                System.out.println("  Station: ID " + station.getId() + 
                                   " (Lat: " + station.getLatitude() + 
                                   ", Lon: " + station.getLongitude() + ")");
                if ("occupy".equals(dock.getState()) && dock.getScooter() != null) {
                    System.out.println("  Scooter ID: " + dock.getScooter().getId());
                }
                System.out.println();
            }
        } finally {
            em.close();
        }
    }


    public void startTrip() {
        EntityManager em = emf.createEntityManager();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Client ID: ");
            int clientId = scanner.nextInt();

            System.out.print("Station ID to start trip from: ");
            int stationId = scanner.nextInt();

            em.getTransaction().begin();

            Client client = em.find(Client.class, clientId);
            if (client == null) {
                System.out.println("Client not found.");
                em.getTransaction().rollback();
                return;
            }

            TypedQuery<Card> cardQuery = em.createQuery(
                    "SELECT c FROM Card c WHERE c.client = :clientId", Card.class);
            cardQuery.setParameter("clientId", clientId);
            List<Card> cards = cardQuery.getResultList();

            if (cards.isEmpty()) {
                System.out.println("Client has no card.");
                em.getTransaction().rollback();
                return;
            }

            Card card = cards.get(0);

            ServiceCost serviceCost = em.find(ServiceCost.class, 1);
            if (serviceCost == null) {
                System.out.println("Service cost not set.");
                em.getTransaction().rollback();
                return;
            }

            if (card.getCredit().compareTo(serviceCost.getUnlock()) < 0) {
                System.out.println("Insufficient credit to start trip.");
                em.getTransaction().rollback();
                return;
            }

            TypedQuery<Dock> dockQuery = em.createQuery(
                    "SELECT d FROM Dock d WHERE d.station.id = :stationId AND d.state = 'occupy'",
                    Dock.class);
            dockQuery.setParameter("stationId", stationId);
            List<Dock> docks = dockQuery.getResultList();

            if (docks.isEmpty()) {
                System.out.println("No available scooters at this station.");
                em.getTransaction().rollback();
                return;
            }

            Dock dock = docks.get(0);
            Scooter scooter = dock.getScooter();

            Travel travel = new Travel();
            travel.setDinitial(LocalDateTime.now());
            travel.setClient(client);
            travel.setScooter(scooter);
            travel.setStinitial(dock.getStation());

            em.persist(travel);

            card.setCredit(card.getCredit().subtract(serviceCost.getUnlock()));
            em.merge(card);

            dock.setScooter(null);
            dock.setState("free");
            em.merge(dock);

            em.getTransaction().commit();
            System.out.println("Trip started successfully.");
        } finally {
            em.close();
        }
    }



    public void parkScooter() {
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("\tEnter scooter ID: ");
            int scooterId = Integer.parseInt(scanner.nextLine());

            System.out.print("\tEnter final station ID: ");
            int stationId = Integer.parseInt(scanner.nextLine());

            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            tx.begin();

            try {
                TypedQuery<Travel> query = em.createQuery(
                        "SELECT t FROM Travel t WHERE t.scooter.id = :scooterId AND t.dfinal IS NULL",
                        Travel.class);
                query.setParameter("scooterId", scooterId);

                List<Travel> results = query.getResultList();
                if (results.isEmpty()) {
                    System.out.println("\tNo ongoing trip found for scooter ID: " + scooterId);
                    tx.rollback();
                    em.close();
                    return;
                }

                Travel trip = results.get(0);
                trip.setDfinal(LocalDateTime.now());
                trip.setStfinal(em.find(Station.class, stationId));

                TypedQuery<Dock> dockQuery = em.createQuery(
                        "SELECT d FROM Dock d WHERE d.station.id = :stationId AND d.state = 'free'",
                        Dock.class);
                dockQuery.setParameter("stationId", stationId);
                dockQuery.setMaxResults(1);

                List<Dock> docks = dockQuery.getResultList();
                if (docks.isEmpty()) {
                    System.out.println("\tNo free docks available at station ID: " + stationId);
                    tx.rollback();
                    em.close();
                    return;
                }

                Dock dock = docks.get(0);
                dock.setState("occupy");
                dock.setScooter(trip.getScooter());

                long durationMinutes = java.time.Duration.between(trip.getDinitial(), trip.getDfinal()).toMinutes();

                TypedQuery<Card> cardQuery = em.createQuery(
                        "SELECT c FROM Card c WHERE c.client = :clientId", Card.class);
                cardQuery.setParameter("clientId", trip.getClient().getPerson());
                List<Card> cards = cardQuery.getResultList();
                if (cards.isEmpty()) {
                    System.out.println("\tClient has no card.");
                    tx.rollback();
                    em.close();
                    return;
                }
                Card card = cards.get(0);

                ServiceCost serviceCost = em.find(ServiceCost.class, 1);
                if (serviceCost == null) {
                    System.out.println("\tService cost not set.");
                    tx.rollback();
                    em.close();
                    return;
                }

                BigDecimal usageCost = serviceCost.getUsable().multiply(new BigDecimal(durationMinutes));

                if (card.getCredit().compareTo(usageCost) < 0) {
                    System.out.println("\tInsufficient credit to end trip. Please top up your card.");
                    tx.rollback();
                    em.close();
                    return;
                }

                card.setCredit(card.getCredit().subtract(usageCost));

                em.merge(trip);
                em.merge(dock);
                em.merge(card);

                tx.commit();
                System.out.println("\tTrip ended and scooter parked successfully.");
                System.out.println("\tUsage cost deducted: " + usageCost);
                System.out.println("\tRemaining credit: " + card.getCredit());
            } catch (Exception e) {
                if (tx.isActive())
                    tx.rollback();
                System.out.println("\tError ending trip: " + e.getMessage());
            } finally {
                em.close();
            }
        }
    }



    private void about()
    {
        System.out.println("Group 54T41N");
        System.out.println("51532 - Bruno Saldanha");
        System.out.println("51283 - João Chuço");
        System.out.println("49504 - Jorge Palácios da Silva");
        System.out.println("DAL version:"+ isel.sisinf.jpa.Dal.version());
        System.out.println("Core version:"+ isel.sisinf.model.Core.version());
        
    }
}

public class App{
    public static void main(String[] args) throws Exception{
        UI.getInstance().Run();
    }
}


