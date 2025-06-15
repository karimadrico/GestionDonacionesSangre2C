package lsi.ubu.solucion;

import java.sql.*;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lsi.ubu.util.PoolDeConexiones;
import lsi.ubu.util.ExecuteScript;
import lsi.ubu.enunciado.GestionDonacionesSangreException;
import lsi.ubu.solucion.Misc;

public class GestionDonacionesSangre {
    private static Logger logger = LoggerFactory.getLogger(GestionDonacionesSangre.class);
    private static final String script_path = "sql/";

    public static void main(String[] args) throws SQLException {
        tests();
        System.out.println("FIN.............");
    }

    public static void realizar_traspaso(int m_ID_Hospital_Origen, int m_ID_Hospital_Destino,
                                         int m_ID_Tipo_Sangre, float m_Cantidad, Date m_Fecha_Traspaso)
            throws SQLException {
        PoolDeConexiones pool = PoolDeConexiones.getInstance();
        Connection con = null;
        PreparedStatement psCheckTipo = null, psCheckHospital = null, psCheckReserva = null, psInsertReserva = null, psUpdateReserva = null, psInsertTraspaso = null;
        ResultSet rs = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);

            // Validar tipo de sangre
            psCheckTipo = con.prepareStatement("SELECT COUNT(*) FROM tipo_sangre WHERE id_tipo_sangre = ?");
            psCheckTipo.setInt(1, m_ID_Tipo_Sangre);
            rs = psCheckTipo.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.TIPO_SANGRE_NO_EXISTE);
            }
            rs.close();

            // Validar hospitales
            psCheckHospital = con.prepareStatement("SELECT COUNT(*) FROM hospital WHERE id_hospital = ?");
            psCheckHospital.setInt(1, m_ID_Hospital_Origen);
            rs = psCheckHospital.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.HOSPITAL_NO_EXISTE);
            }
            rs.close();
            psCheckHospital.setInt(1, m_ID_Hospital_Destino);
            rs = psCheckHospital.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.HOSPITAL_NO_EXISTE);
            }
            rs.close();

            // Validar cantidad
            if (m_Cantidad <= 0) {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.VALOR_CANTIDAD_TRASPASO_INCORRECTO);
            }

            // Comprobar y actualizar reserva origen
            psCheckReserva = con.prepareStatement("SELECT cantidad FROM reserva_hospital WHERE id_tipo_sangre = ? AND id_hospital = ?");
            psCheckReserva.setInt(1, m_ID_Tipo_Sangre);
            psCheckReserva.setInt(2, m_ID_Hospital_Origen);
            rs = psCheckReserva.executeQuery();
            float cantidadOrigen = 0;
            boolean existeReservaOrigen = false;
            if (rs.next()) {
                cantidadOrigen = rs.getFloat(1);
                existeReservaOrigen = true;
            }
            rs.close();
            if (!existeReservaOrigen) {
                // Si no existe, crearla con cantidad 0
                psInsertReserva = con.prepareStatement("INSERT INTO reserva_hospital (id_tipo_sangre, id_hospital, cantidad) VALUES (?, ?, 0)");
                psInsertReserva.setInt(1, m_ID_Tipo_Sangre);
                psInsertReserva.setInt(2, m_ID_Hospital_Origen);
                psInsertReserva.executeUpdate();
                cantidadOrigen = 0;
            }
            if (cantidadOrigen < m_Cantidad) {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.VALOR_RESERVA_INCORRECTO);
            }
            // Decrementar reserva origen
            psUpdateReserva = con.prepareStatement("UPDATE reserva_hospital SET cantidad = cantidad - ? WHERE id_tipo_sangre = ? AND id_hospital = ?");
            psUpdateReserva.setFloat(1, m_Cantidad);
            psUpdateReserva.setInt(2, m_ID_Tipo_Sangre);
            psUpdateReserva.setInt(3, m_ID_Hospital_Origen);
            psUpdateReserva.executeUpdate();

            // Comprobar y actualizar reserva destino
            psCheckReserva.setInt(2, m_ID_Hospital_Destino);
            rs = psCheckReserva.executeQuery();
            boolean existeReservaDestino = false;
            if (rs.next()) {
                existeReservaDestino = true;
            }
            rs.close();
            if (!existeReservaDestino) {
                psInsertReserva.setInt(2, m_ID_Hospital_Destino);
                psInsertReserva.executeUpdate();
            }
            // Incrementar reserva destino
            psUpdateReserva.setFloat(1, m_Cantidad * -1); // Para sumar, pasamos negativo de negativo
            psUpdateReserva.setInt(2, m_ID_Tipo_Sangre);
            psUpdateReserva.setInt(3, m_ID_Hospital_Destino);
            psUpdateReserva.executeUpdate();

            // Insertar traspaso
            psInsertTraspaso = con.prepareStatement("INSERT INTO traspaso (id_traspaso, id_tipo_sangre, id_hospital_origen, id_hospital_destino, cantidad, fecha_traspaso) VALUES (seq_traspaso.nextval, ?, ?, ?, ?, ?)");
            psInsertTraspaso.setInt(1, m_ID_Tipo_Sangre);
            psInsertTraspaso.setInt(2, m_ID_Hospital_Origen);
            psInsertTraspaso.setInt(3, m_ID_Hospital_Destino);
            psInsertTraspaso.setFloat(4, m_Cantidad);
            psInsertTraspaso.setDate(5, new java.sql.Date(m_Fecha_Traspaso.getTime()));
            psInsertTraspaso.executeUpdate();

            con.commit();
        } catch (GestionDonacionesSangreException e) {
            if (con != null) con.rollback();
            throw e;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            logger.error(e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (psCheckTipo != null) psCheckTipo.close();
            if (psCheckHospital != null) psCheckHospital.close();
            if (psCheckReserva != null) psCheckReserva.close();
            if (psInsertReserva != null) psInsertReserva.close();
            if (psUpdateReserva != null) psUpdateReserva.close();
            if (psInsertTraspaso != null) psInsertTraspaso.close();
            if (con != null) con.close();
        }
    }

    public static void realizar_doancion(String m_NIF, float m_Cantidad, int m_ID_Hospital, Date m_Fecha_Donacion)
            throws SQLException {
        PoolDeConexiones pool = PoolDeConexiones.getInstance();
        Connection con = null;
        PreparedStatement psCheckDonante = null, psCheckHospital = null, psCheckTipo = null, psCheckReserva = null, psInsertReserva = null, psUpdateReserva = null, psInsertDonacion = null, psUltimaDonacion = null;
        ResultSet rs = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);

            // Validar donante
            psCheckDonante = con.prepareStatement("SELECT id_tipo_sangre FROM donante WHERE NIF = ?");
            psCheckDonante.setString(1, m_NIF);
            rs = psCheckDonante.executeQuery();
            Integer idTipoSangre = null;
            if (rs.next()) {
                idTipoSangre = rs.getInt(1);
            } else {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.DONANTE_NO_EXISTE);
            }
            rs.close();

            // Validar hospital
            psCheckHospital = con.prepareStatement("SELECT COUNT(*) FROM hospital WHERE id_hospital = ?");
            psCheckHospital.setInt(1, m_ID_Hospital);
            rs = psCheckHospital.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.HOSPITAL_NO_EXISTE);
            }
            rs.close();

            // Validar cantidad
            if (m_Cantidad <= 0 || m_Cantidad > 0.45f) {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.VALOR_CANTIDAD_DONACION_INCORRECTO);
            }

            // Validar frecuencia de donación (no más de una cada 15 días)
            psUltimaDonacion = con.prepareStatement("SELECT MAX(fecha_donacion) FROM donacion WHERE nif_donante = ?");
            psUltimaDonacion.setString(1, m_NIF);
            rs = psUltimaDonacion.executeQuery();
            if (rs.next()) {
                Date ultimaDonacion = rs.getDate(1);
                if (ultimaDonacion != null) {
                    int dias = Misc.howManyDaysBetween(m_Fecha_Donacion, ultimaDonacion);
                    if (dias < 15) {
                        throw new GestionDonacionesSangreException(GestionDonacionesSangreException.DONANTE_EXCEDE);
                    }
                }
            }
            rs.close();

            // Comprobar y actualizar/crear reserva
            psCheckReserva = con.prepareStatement("SELECT cantidad FROM reserva_hospital WHERE id_tipo_sangre = ? AND id_hospital = ?");
            psCheckReserva.setInt(1, idTipoSangre);
            psCheckReserva.setInt(2, m_ID_Hospital);
            rs = psCheckReserva.executeQuery();
            boolean existeReserva = false;
            if (rs.next()) {
                existeReserva = true;
            }
            rs.close();
            if (!existeReserva) {
                psInsertReserva = con.prepareStatement("INSERT INTO reserva_hospital (id_tipo_sangre, id_hospital, cantidad) VALUES (?, ?, 0)");
                psInsertReserva.setInt(1, idTipoSangre);
                psInsertReserva.setInt(2, m_ID_Hospital);
                psInsertReserva.executeUpdate();
            }
            psUpdateReserva = con.prepareStatement("UPDATE reserva_hospital SET cantidad = cantidad + ? WHERE id_tipo_sangre = ? AND id_hospital = ?");
            psUpdateReserva.setFloat(1, m_Cantidad);
            psUpdateReserva.setInt(2, idTipoSangre);
            psUpdateReserva.setInt(3, m_ID_Hospital);
            psUpdateReserva.executeUpdate();

            // Insertar donación
            psInsertDonacion = con.prepareStatement("INSERT INTO donacion (id_donacion, nif_donante, cantidad, fecha_donacion) VALUES (seq_donacion.nextval, ?, ?, ?)");
            psInsertDonacion.setString(1, m_NIF);
            psInsertDonacion.setFloat(2, m_Cantidad);
            psInsertDonacion.setDate(3, new java.sql.Date(m_Fecha_Donacion.getTime()));
            psInsertDonacion.executeUpdate();

            con.commit();
        } catch (GestionDonacionesSangreException e) {
            if (con != null) con.rollback();
            throw e;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            logger.error(e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (psCheckDonante != null) psCheckDonante.close();
            if (psCheckHospital != null) psCheckHospital.close();
            if (psCheckTipo != null) psCheckTipo.close();
            if (psCheckReserva != null) psCheckReserva.close();
            if (psInsertReserva != null) psInsertReserva.close();
            if (psUpdateReserva != null) psUpdateReserva.close();
            if (psInsertDonacion != null) psInsertDonacion.close();
            if (psUltimaDonacion != null) psUltimaDonacion.close();
            if (con != null) con.close();
        }
    }

    public static void consulta_traspasos(String m_Tipo_Sangre)
            throws SQLException {
        PoolDeConexiones pool = PoolDeConexiones.getInstance();
        Connection con = null;
        PreparedStatement psTipo = null, psConsulta = null;
        ResultSet rsTipo = null, rs = null;
        try {
            con = pool.getConnection();

            // Obtener id_tipo_sangre a partir de la descripción
            psTipo = con.prepareStatement("SELECT id_tipo_sangre FROM tipo_sangre WHERE descripcion = ?");
            psTipo.setString(1, m_Tipo_Sangre);
            rsTipo = psTipo.executeQuery();
            Integer idTipoSangre = null;
            if (rsTipo.next()) {
                idTipoSangre = rsTipo.getInt(1);
            } else {
                throw new GestionDonacionesSangreException(GestionDonacionesSangreException.TIPO_SANGRE_NO_EXISTE);
            }
            rsTipo.close();

            // Consulta de traspasos con joins
            String sql = "SELECT t.id_traspaso, t.id_tipo_sangre, ts.descripcion, t.id_hospital_origen, h1.nombre AS hospital_origen, t.id_hospital_destino, h2.nombre AS hospital_destino, t.cantidad, t.fecha_traspaso, " +
                         "rh1.cantidad AS reserva_origen, rh2.cantidad AS reserva_destino " +
                         "FROM traspaso t " +
                         "JOIN tipo_sangre ts ON t.id_tipo_sangre = ts.id_tipo_sangre " +
                         "JOIN hospital h1 ON t.id_hospital_origen = h1.id_hospital " +
                         "JOIN hospital h2 ON t.id_hospital_destino = h2.id_hospital " +
                         "LEFT JOIN reserva_hospital rh1 ON t.id_tipo_sangre = rh1.id_tipo_sangre AND t.id_hospital_origen = rh1.id_hospital " +
                         "LEFT JOIN reserva_hospital rh2 ON t.id_tipo_sangre = rh2.id_tipo_sangre AND t.id_hospital_destino = rh2.id_hospital " +
                         "WHERE t.id_tipo_sangre = ? " +
                         "ORDER BY t.id_hospital_destino, t.fecha_traspaso";
            psConsulta = con.prepareStatement(sql);
            psConsulta.setInt(1, idTipoSangre);
            rs = psConsulta.executeQuery();

            System.out.printf("%-12s %-15s %-20s %-20s %-10s %-15s %-10s %-15s %-15s %-15s\n",
                "ID_TRASPASO", "TIPO_SANGRE", "HOSP_ORIGEN", "HOSP_DESTINO", "CANTIDAD", "FECHA", "RES_ORIGEN", "RES_DESTINO", "ID_ORIGEN", "ID_DESTINO");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-12d %-15s %-20s %-20s %-10.2f %-15s %-10.2f %-15.2f %-15d %-15d\n",
                    rs.getInt("id_traspaso"),
                    rs.getString("descripcion"),
                    rs.getString("hospital_origen"),
                    rs.getString("hospital_destino"),
                    rs.getFloat("cantidad"),
                    rs.getDate("fecha_traspaso"),
                    rs.getFloat("reserva_origen"),
                    rs.getFloat("reserva_destino"),
                    rs.getInt("id_hospital_origen"),
                    rs.getInt("id_hospital_destino"));
            }
        } catch (GestionDonacionesSangreException e) {
            throw e;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (rsTipo != null) rsTipo.close();
            if (psTipo != null) psTipo.close();
            if (psConsulta != null) psConsulta.close();
            if (con != null) con.close();
        }
    }

    static public void creaTablas() {
        ExecuteScript.run(script_path + "gestion_donaciones_sangre.sql");
    }

    static void tests() throws SQLException {
        creaTablas();
        PoolDeConexiones pool = PoolDeConexiones.getInstance();
        CallableStatement cll_reinicia = null;
        Connection conn = null;
        try {
            conn = pool.getConnection();
            cll_reinicia = conn.prepareCall("{call inicializa_test}");
            cll_reinicia.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            if (cll_reinicia != null) cll_reinicia.close();
            if (conn != null) conn.close();
        }
        // Aquí se implementarán las pruebas de las transacciones
    }
} 