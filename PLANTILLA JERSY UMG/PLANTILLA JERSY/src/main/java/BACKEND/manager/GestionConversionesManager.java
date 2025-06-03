package BACKEND.manager;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import BACKEND.model.Ejemplo;
import BACKEND.model.Municipio;
import BACKEND.model.Sucursal;
import BACKEND.utils.Config;
import BACKEND.utils.ConnectionsPool;
import BACKEND.utils.jsonResult;
import oracle.jdbc.OracleTypes;

public class GestionConversionesManager {
    String SCHEMA = new Config().getDBSchema();

    public String ConvertirMoneda(String Fecha, String Monto, String TipoConversion) throws Exception {
        Date fechaSql = Date.valueOf(Fecha);
        BigDecimal montoDecimal = new BigDecimal(Monto);
        int tipoConv = Integer.parseInt(TipoConversion);

        Connection conn = null;
        CallableStatement call = null;
        String resultado;

        try {
            conn = new ConnectionsPool().conectar();
            String callString = "{ call " + SCHEMA + ".PKG_CONVERTIDOR_MONEDA.PROC_CONVERTIR_MONEDA(?,?,?,?) }";
            call = conn.prepareCall(callString);
            call.setDate(1, fechaSql);
            call.setBigDecimal(2, montoDecimal);
            call.setInt(3, tipoConv);
            call.registerOutParameter(4, OracleTypes.NUMBER);
            call.execute();
            BigDecimal salidaDecimal = call.getBigDecimal(4);
            resultado = (salidaDecimal != null) ? salidaDecimal.toPlainString() : "0";
        } finally {
            if (call != null) {
                try { call.close(); } catch (Exception e) {}
            }
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }

        return resultado;
    }

    public List<Map<String, Object>> ObtenerCatalogoEjemplo() throws Exception {
        List<Map<String, Object>> salida = new ArrayList<Map<String, Object>>();

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call =
            conn.prepareCall("call " + SCHEMA + ".PKG_EJEMPLOS.OBTENER_DEPARTAMENTOS(?)");

        call.registerOutParameter("p_cursor", OracleTypes.CURSOR);
        call.execute();
        ResultSet rset = (ResultSet) call.getObject("p_cursor");

        ResultSetMetaData meta = rset.getMetaData();

        while (rset.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i).toString();
                String value = Objects.toString(rset.getString(key), "");
                map.put(key, value);
            }
            salida.add(map);
        }

        rset.close();
        call.close();
        conn.close();

        return salida;
    }

    public List<Map<String, Object>> ObtenerSucursales() throws Exception {
        List<Map<String, Object>> salida = new ArrayList<Map<String, Object>>();

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call =
            conn.prepareCall("call " + SCHEMA + ".PKG_EJEMPLOS.OBTENER_SUCURSALES(?)");

        call.registerOutParameter("p_cursor", OracleTypes.CURSOR);
        call.execute();
        ResultSet rset = (ResultSet) call.getObject("p_cursor");

        ResultSetMetaData meta = rset.getMetaData();

        while (rset.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i).toString();
                String value = Objects.toString(rset.getString(key), "");
                map.put(key, value);
            }
            salida.add(map);
        }

        rset.close();
        call.close();
        conn.close();

        return salida;
    }

    public jsonResult AgregarEjemplo(Ejemplo item) throws Exception {
        jsonResult salida = new jsonResult();

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call =
            conn.prepareCall("call " + SCHEMA + ".PKG_EJEMPLO.PROC_INS_TT_EJEMPLO_TRANSACCIO(?,?,?,?,?)");

        call.setString("p_id_ejemplo_catalogo", item.ID_EJEMPLO_TRANSACCIONAL);
        call.setString("p_observaciones", item.OBSERVACIONES);
        call.setString("p_id_estado", item.ID_ESTADO);
        call.registerOutParameter("p_id_salida", OracleTypes.NUMBER);
        call.registerOutParameter("p_msj", OracleTypes.VARCHAR);
        call.execute();

        int id = call.getInt("p_id_salida");
        String msj = call.getString("p_msj");
        salida.id = id;
        salida.msj = msj;
        if (salida.id != -1) {
            salida.result = "OK";
        }

        call.close();
        conn.close();

        return salida;
    }

    public jsonResult AgregarSucursal(Sucursal item) throws Exception {
        jsonResult salida = new jsonResult();

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call =
            conn.prepareCall("call " + SCHEMA + ".PKG_EJEMPLOs.AGREGAR_SUCURSAL(?,?,?,?,?,?)");

        call.setString("p_id_sucursal", item.ID_SUCURSAL);
        call.setString("p_nombre", item.NOMBRE);
        call.setString("p_direccion", item.DIRECCION);
        call.setString("p_estado", item.ESTADO);
        call.registerOutParameter("p_id_salida", OracleTypes.NUMBER);
        call.registerOutParameter("p_msj", OracleTypes.VARCHAR);
        call.execute();

        int id = call.getInt("p_id_salida");
        String msj = call.getString("p_msj");
        salida.id = id;
        salida.msj = msj;
        if (salida.id != -1) {
            salida.result = "OK";
        }

        call.close();
        conn.close();

        return salida;
    }

    public jsonResult AgregarDetalleTransaccional(Municipio item, Connection conn) throws Exception {
        jsonResult salida = new jsonResult();

        try {
            CallableStatement call =
                conn.prepareCall("call " + SCHEMA + ".PKG_EJEMPLOS.AGREGAR_MUNICIPIO(?,?,?,?)");
            call.setString("p_id_depto", item.ID_UBICACION_GEOGRARAFICA_PADRE);
            call.setString("p_nombre", item.NOMBRE);
            call.registerOutParameter("p_id_salida", OracleTypes.NUMBER);
            call.registerOutParameter("p_msj", OracleTypes.VARCHAR);
            call.execute();

            int id = call.getInt("p_id_salida");
            salida.id = id;
            salida.result = "ERROR";
            salida.msj = call.getString("p_msj");
            if (salida.id != -1) {
                salida.result = "OK";
            } else {
                throw new Exception(salida.msj);
            }

            call.close();
        } catch (Exception e) {
            salida.id = -1;
            salida.msj = e.getMessage();
        }
        return salida;
    }

    public Map<String, Object> ObtenerEjemplo(String id_ejemplo) throws Exception {
        Map<String, Object> salida = new LinkedHashMap<String, Object>();

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call =
            conn.prepareCall("call " + SCHEMA + ".PKG_EJEMPLO.PROC_OBTENER_EJEMPLO(?,?)");

        call.setString("p_id_ejemplo_transaccional", id_ejemplo);
        call.registerOutParameter("p_cur_dataset", OracleTypes.CURSOR);
        call.execute();
        ResultSet rset = (ResultSet) call.getObject("p_cur_dataset");

        ResultSetMetaData meta = rset.getMetaData();

        while (rset.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i).toString();
                String value = Objects.toString(rset.getString(key), "");
                map.put(key, value);
            }
            salida = map;
        }

        rset.close();
        call.close();
        conn.close();

        return salida;
    }

    public List<Map<String, Object>> ObtenerMunicipiosDepto(String id_ubicacion_geografica) throws Exception {
        List<Map<String, Object>> salida = new ArrayList<Map<String, Object>>();

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call =
            conn.prepareCall("call " + SCHEMA + ".PKG_EJEMPLOS.OBTENER_MUNICIPIOS_DEPTO(?,?)");

        call.setString("p_id_ubicacion_geografica", id_ubicacion_geografica);
        call.registerOutParameter("p_cursor", OracleTypes.CURSOR);
        call.execute();
        ResultSet rset = (ResultSet) call.getObject("p_cursor");

        ResultSetMetaData meta = rset.getMetaData();

        while (rset.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i).toString();
                String value = Objects.toString(rset.getString(key), "");
                map.put(key, value);
            }
            salida.add(map);
        }

        rset.close();
        call.close();
        conn.close();

        return salida;
    }

    public List<Map<String, Object>> ObtenerTasasCambio() throws Exception {
        List<Map<String, Object>> resultado = new ArrayList<>();

        try (
            Connection conn = new ConnectionsPool().conectar();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT TO_CHAR(fecha,'YYYY-MM-DD') AS fecha, "
              + "tasa_cambio_dollar, tasa_cambio_quetzales "
              + "FROM TAZA_DE_CAMBIO ORDER BY fecha")
        ) {
            ResultSetMetaData md = rs.getMetaData();
            int columnas = md.getColumnCount();

            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                for (int i = 1; i <= columnas; i++) {
                    fila.put(md.getColumnName(i), rs.getObject(i));
                }
                resultado.add(fila);
            }
        }

        return resultado;
    }
}
