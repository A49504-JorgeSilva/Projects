import java.sql.*;
import java.util.Objects;

public class RT {
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement pst = null;
    private static ResultSet rs = null;

    Queries queries = new Queries();
    Update update = new Update();

    public void rt1() {
        try {
            String jdbcURL = "jdbc:postgresql://10.62.73.73:5432/";
            String username = "mp25";
            String password = "mp25";
            Connection conn = DriverManager.getConnection(jdbcURL, username, password);
            Statement sta = conn.createStatement();
            ResultSet rs = sta.executeQuery(queries.t1);

            while (rs.next()) {
                if (rs.getInt("n_veiculos") > 20) {
                    Statement stm = conn.createStatement();
                    stm.executeUpdate(update.delete_veiculos(rs.getInt("idpessoa")));
                }
            }

            rs.close();
            sta.close();
            conn.close();

        } catch (SQLException E) {
            E.printStackTrace();
        }
    }

    public void rt2() {
        try {
            String jdbcURL = "jdbc:postgresql://10.62.73.73:5432/";
            String username = "mp25";
            String password = "mp25";
            Connection conn = DriverManager.getConnection(jdbcURL, username, password);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(queries.t2);

            while (rs.next()) {
                Statement stm = conn.createStatement();
                stm.executeUpdate(update.delete_condutor(rs.getInt("idpessoa")));
            }
            rs.close();
            st.close();
            conn.close();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void rt4() {
        try {
            String jdbcURL = "jdbc:postgresql://10.62.73.73:5432/";
            String username = "mp25";
            String password = "mp25";
            Connection conn = DriverManager.getConnection(jdbcURL, username, password);
            Statement st = conn.createStatement();
            ResultSet verify = st.executeQuery(queries.check_veiculo_old);
            if (Objects.equals(verify.getString(0), "false"))
                st.executeQuery(queries.create_veiculo_old);
            ResultSet rs = st.executeQuery(queries.t3);
            Integer id = 0;
            int n_viagens = 0;
            double totalDistance = 0.0;
            while (rs.next()) {
                Integer vId = rs.getInt("id");
                if (id.equals(vId)) {
                    totalDistance += DistanceCalculator.distance(rs.getDouble("latinicio"),
                            rs.getDouble("longinicio"), rs.getDouble("latfim"),
                            rs.getDouble("longfim"), "K");
                    n_viagens++;
                } else {
                    st.executeQuery(update.transfer_values_veiculos(rs.getInt("id"),
                            rs.getString("matricula"), rs.getInt("tipo"), rs.getString("modelo"),
                            rs.getString("marca"), rs.getInt("ano"), rs.getInt("proprietario"),
                            n_viagens, totalDistance));
                    id = vId;
                    n_viagens = 1;
                    totalDistance = DistanceCalculator.distance(rs.getDouble("latinicio"),
                            rs.getDouble("longinicio"), rs.getDouble("latfim"),
                            rs.getDouble("longfim"), "K");
                }
            }
            verify.close();
            rs.close();
            st.close();
            conn.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}