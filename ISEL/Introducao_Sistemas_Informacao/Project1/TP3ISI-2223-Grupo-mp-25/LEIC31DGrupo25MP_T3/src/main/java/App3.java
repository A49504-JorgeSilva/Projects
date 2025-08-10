
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

interface DbWorker {
    void doWork();
}

class App {
    Model m = new Model();

    private enum Option {
        Unknown,
        NewCondutor,
        DesactivateVehicle,
        VehicleStats,
        MostTravels,
        NeverTraveled,
        NumTravelsPerYear,
        BestPaidDriver,
        Exit,
    }

    private static App __instance = null;
    private String __connectionString;
    private HashMap<Option, DbWorker> __dbMethods;
    private static final String SELECT_CMD = "select name,dateBirth,sex from dbo.Student";

    private App() {
        __dbMethods = new HashMap<Option, DbWorker>();
        __dbMethods.put(Option.NewCondutor, new DbWorker() {public void doWork() {Model.newCondutor();}});
        __dbMethods.put(Option.DesactivateVehicle, new DbWorker() {public void doWork() {Model.DesactivateVehicle();}});
        __dbMethods.put(Option.VehicleStats,new DbWorker(){public void doWork(){Model.VEHICLE_STATS();}});
        __dbMethods.put(Option.MostTravels,new DbWorker(){public void doWork(){Model.MOST_TRAVELS();}});
        __dbMethods.put(Option.NeverTraveled,new DbWorker(){public void doWork(){Model.NEVER_TRAVELED();}});
        __dbMethods.put(Option.NumTravelsPerYear,new DbWorker(){public void doWork(){Model.NUM_TRAVELS_PER_YEAR();}});
        __dbMethods.put(Option.BestPaidDriver,new DbWorker(){public void doWork(){Model.BEST_PAID_DRIVER();}});
    }

    public static App getInstance() {
        if (__instance == null) {
            __instance = new App();
        }
        return __instance;
    }

    private Option DisplayMenu() {
        Option option = Option.Unknown;
        try {
            m.CheckRestrictions();
            System.out.println("Application");
            System.out.println();
            System.out.println("1. Inserir novo condutor");
            System.out.println("2. Por veiculo fora de serviço");
            System.out.println("3. Mostrar Status do veículo");
            System.out.println("4. Mais viagens por ano");
            System.out.println("5. Lista de Condutores que nunca viajaram");
            System.out.println("6. Viagens por ano dos veículos dos proprietarios");
            System.out.println("7. Condutor mais pago por ano");
            System.out.println("8. Exit");
            System.out.print(">");
            Scanner s = new Scanner(System.in);
            int result = s.nextInt();
            option = Option.values()[result];
        } catch (RuntimeException ex) {
            // nothing to do.
        }
        return option;
    }

    private final static void clearConsole() throws Exception {
        for (int y = 0; y < 25; y++)
            System.out.println("\n");
    }

    private void Login() throws java.sql.SQLException {
        Connection con = DriverManager.getConnection(getConnectionString());
        if (con != null) con.close();
    }

    public void Run() throws Exception {
        Login();
        Option userInput = Option.Unknown;
        do {
            clearConsole();
            userInput = DisplayMenu();
            clearConsole();
            try {
                __dbMethods.get(userInput).doWork();
                System.in.read();

            } catch (NullPointerException ex) {
                // Nothing to do. The option was not a valid one. Read another.
            }

        } while (userInput != Option.Exit);
    }

    public String getConnectionString() {
        return __connectionString;
    }

    public void setConnectionString(String s) {
        __connectionString = s;
    }
}

public class    App3 {
    public static void main(String[] args) throws SQLException, Exception {
        String url = "jdbc:postgresql://10.62.73.73:5432/?user=mp25&password=mp25&ssl=false";
        App.getInstance().setConnectionString(url);
        App.getInstance().Run();
    }
}