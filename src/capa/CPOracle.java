package capa;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.esportsapp.persistencia.IPersistencia;
import p1.t6.model.romeumusetelena.Adreca;
import p1.t6.model.romeumusetelena.Categoria;
import p1.t6.model.romeumusetelena.Equip;
import p1.t6.model.romeumusetelena.GestorBDEsportsException;
import p1.t6.model.romeumusetelena.Jugador;
import p1.t6.model.romeumusetelena.Membre;
import p1.t6.model.romeumusetelena.Temporada;
import p1.t6.model.romeumusetelena.TipusEquip;
import p1.t6.model.romeumusetelena.TipusMembre;
import p1.t6.model.romeumusetelena.Usuari;

public class CPOracle implements IPersistencia {
    private Connection conn;
    
    private PreparedStatement psAfegirEquip;
    private PreparedStatement psObtenirEquip;
    private PreparedStatement psObtenirTotsEquips;
    private PreparedStatement psModificarEquip;
    private PreparedStatement psEliminarEquip;
    private PreparedStatement psAfegirJugador;
    private PreparedStatement psBuscarNomJugador;
    private PreparedStatement psBuscarPerNIF;
    private PreparedStatement psBuscarPerDataNaix;
    private PreparedStatement psOrdenarPerCognom;
    private PreparedStatement psObtenirJugador;
    private PreparedStatement psObtenirTotsJugadors;
    private PreparedStatement psModificarJugador;
    private PreparedStatement psEliminarJugador;
    private PreparedStatement psAfegirMembre;
    private PreparedStatement psEliminarMembre;
    private PreparedStatement psModificarMembre;
    private PreparedStatement psObtenirMembresDEquip;
    private PreparedStatement psObtenirCategoria;
    private PreparedStatement psObtenirTotesCategories;
    private PreparedStatement psAfegirTemporada;
    private PreparedStatement psObtenirTemporada;
    private PreparedStatement psObtenirTotesTemporades;
    private PreparedStatement psEliminarTemporada;
    private PreparedStatement psValidarUsuari;

    
    // Constructor per establir la connexió amb Oracle
    public CPOracle() throws GestorBDEsportsException {
        String nomFitxerPropietats = "db.properties"; 
        Properties p = new Properties();

        try {
            p.load(new FileInputStream(nomFitxerPropietats));

            // Llegir les propietats del fitxer
            String url = p.getProperty("url");
            String user = p.getProperty("user");
            String pwd = p.getProperty("pwd");

            if (url == null || user == null || pwd == null) {
                throw new GestorBDEsportsException(
                    "Al fitxer " + nomFitxerPropietats + " li manca alguna de les propietats url/user/pwd");
            }

            // Establir la connexió amb la base de dades
            conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false); // Control manual de transaccions

        } catch (FileNotFoundException e) {
            throw new GestorBDEsportsException("No es troba el fitxer " + nomFitxerPropietats, e);
        } catch (IOException e) {
            throw new GestorBDEsportsException("No es pot recuperar les propietats del fitxer " + nomFitxerPropietats, e);
        } catch (SQLException e) {
            throw new GestorBDEsportsException("Error al connectar amb la base de dades"+ e.getMessage());
        }
    }
    
    public void close() throws GestorBDEsportsException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new GestorBDEsportsException("Error al tancar la connexió amb la base de dades.", e);
        }
    }


    // Mètode per afegir un nou Equip
    @Override
    public boolean afegirEquip(Equip equip) throws GestorBDEsportsException {
        if (psAfegirEquip == null) {
            try {
                // Preparar la sentència per inserir un equip
                psAfegirEquip = conn.prepareStatement("INSERT INTO equip (nom, tipus, anytemporada, idcategoria) VALUES (?, ?, ?, ?)");
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psAfegirEquip", ex);
            }
        }

        try {
            psAfegirEquip.setString(1, equip.getNom());
            psAfegirEquip.setString(2, equip.getTipus().name());
            psAfegirEquip.setInt(3, equip.getAnyTemporada());
            psAfegirEquip.setInt(4, equip.getIdCategoria());

            int rowsAffected = psAfegirEquip.executeUpdate();
            return rowsAffected > 0; // Si es va afegir correctament, es retornarà true
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en afegir l'equip", ex);
        }
    }

    // Mètode per obtenir un equip per ID
    @Override
    public Equip obtenirEquip(int idEquip) throws GestorBDEsportsException {
        if (psObtenirEquip == null) {
            try {
                // Preparar la sentència per obtenir un equip per ID
                psObtenirEquip = conn.prepareStatement("SELECT * FROM equip WHERE id = ?");
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirEquip", ex);
            }
        }

        try {
            psObtenirEquip.setInt(1, idEquip);
            // Executar la consulta i obtenir el resultat
            ResultSet rs = psObtenirEquip.executeQuery();
            if (rs.next()) {
                String nom = rs.getString("nom");
                TipusEquip tipus = TipusEquip.valueOf(rs.getString("tipus"));
                int anyTemporada = rs.getInt("anytemporada");
                int idCategoria = rs.getInt("idcategoria");

                return new Equip(idEquip, nom, tipus, anyTemporada, idCategoria); // Crear i retornar l'objecte Equip
            } else {
                throw new GestorBDEsportsException("Equip no trobat amb ID: " + idEquip);
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir l'equip", ex);
        }
    }

    // Mètode per obtenir tots els equips
    @Override
    public List<Equip> obtenirTotsEquips() throws GestorBDEsportsException {
        if (psObtenirTotsEquips == null) {
            try {
                // Preparar la sentència per obtenir tots els equips
                psObtenirTotsEquips = conn.prepareStatement("SELECT * FROM equip");
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirTotsEquips", ex);
            }
        }

        List<Equip> equips = new ArrayList<>();
        try {
            // Executar la consulta i obtenir els resultats
            ResultSet rs = psObtenirTotsEquips.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                TipusEquip tipus = TipusEquip.valueOf(rs.getString("tipus"));
                int anyTemporada = rs.getInt("anytemporada");
                int idCategoria = rs.getInt("idcategoria");

                equips.add(new Equip(id, nom, tipus, anyTemporada, idCategoria)); // Afegir a la llista
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir tots els equips", ex);
        }

        return equips; // Retornar la llista d'equips
    }

    // Mètode per modificar un equip existent
    @Override
    public void modificarEquip(Equip equip) throws GestorBDEsportsException {
        if (psModificarEquip == null) {
            try {
                // Preparar la sentència per modificar un equip
                psModificarEquip = conn.prepareStatement("UPDATE equip SET nom = ?, tipus = ?, anytemporada = ?, idcategoria = ? WHERE id = ?");
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psModificarEquip", ex);
            }
        }

        try {
            // Establir els valors per a la sentència preparada
            psModificarEquip.setString(1, equip.getNom());
            psModificarEquip.setString(2, equip.getTipus().name());
            psModificarEquip.setInt(3, equip.getAnyTemporada());
            psModificarEquip.setInt(4, equip.getIdCategoria());
            psModificarEquip.setInt(5, equip.getId()); // Afegir l'ID per identificar quin equip modificar

            // Executar la sentència
            if(psModificarEquip.executeUpdate()<1){
                throw new GestorBDEsportsException("No s'ha pogut modificar res");
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en modificar l'equip", ex);
        }
    }

    // Mètode per eliminar un equip
    @Override
    public void eliminarEquip(int idEquip) throws GestorBDEsportsException {
        if (psEliminarEquip == null) {
            try {
                // Preparar la sentència per eliminar un equip per ID
                psEliminarEquip = conn.prepareStatement("DELETE FROM equip WHERE id = ?");
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psEliminarEquip", ex);
            }
        }

        try {
            // Establir l'ID per a la sentència preparada
            psEliminarEquip.setInt(1, idEquip);

            // Executar la sentència
            psEliminarEquip.executeUpdate();
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en eliminar l'equip", ex);
        }
    }

    // Mètodes per a la gestió de Jugadors
    // Mètode per afegir un jugador
    @Override
    public void afegirJugador(Jugador jugador) throws GestorBDEsportsException {
        if (psAfegirJugador == null) {
            try {
                psAfegirJugador = conn.prepareStatement(
                    "INSERT INTO jugador (nom, cognoms, direccio, codiPostal, poblacio, foto, anyFiRevisioMedica, IBAN, idLegal, dataNaix, sexe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psAfegirJugador", ex);
            }
        }

        try {
            psAfegirJugador.setString(1, jugador.getNom());
            psAfegirJugador.setString(2, jugador.getCognoms());
            psAfegirJugador.setString(3, jugador.getAdreca().getDireccio());
            psAfegirJugador.setString(4, jugador.getAdreca().getCodiPostal());
            psAfegirJugador.setString(5, jugador.getAdreca().getPoblacio());
            psAfegirJugador.setString(6, jugador.getFoto());
            psAfegirJugador.setInt(7, jugador.getAnyFiRevisioMedica());
            psAfegirJugador.setString(8, jugador.getIBAN());
            psAfegirJugador.setString(9, jugador.getIdLegal());
            psAfegirJugador.setDate(10, new java.sql.Date(jugador.getDataNaix().getTime()));
            psAfegirJugador.setString(11, String.valueOf(jugador.getSexe()));

            psAfegirJugador.executeUpdate();
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en afegir el jugador", ex);
        }
    }
    
    // Mètode per buscar jugadors per NIF
    @Override
    public List<Jugador> buscarPerNIFJugador(String nif) throws GestorBDEsportsException {
    if (psBuscarPerNIF == null) {
        try {
            psBuscarPerNIF = conn.prepareStatement(
                "SELECT id, nom, cognoms, direccio, codipostal, poblacio, foto, anyfirevisiomedica, iban, idlegal, datanaix, sexe " +
                "FROM jugador WHERE LOWER(id_legal) = LOWER(?)" // Comparació exacta del NIF
            );
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en preparar la sentència psBuscarPerNIF", ex);
        }
    }

    List<Jugador> jugadors = new ArrayList<>();

    try {
        psBuscarPerNIF.setString(1, nif); // Buscar per NIF exactament
        ResultSet rs = psBuscarPerNIF.executeQuery();

        while (rs.next()) { // Recorrem tots els resultats
            // Recuperar els valors de la base de dades
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            String cognoms = rs.getString("cognoms");
            String direccio = rs.getString("direccio");
            String codiPostal = rs.getString("codipostal");
            String poblacio = rs.getString("poblacio");
            String foto = rs.getString("foto");
            int anyFiRevisioMedica = rs.getInt("anyfirevisiomedica");
            String IBAN = rs.getString("iban");
            String idLegal = rs.getString("idlegal");
            Date dataNaix = rs.getDate("datanaix");
            char sexe = rs.getString("sexe").charAt(0);

            // Crear l'objecte Adreca
            Adreca adreca = new Adreca(direccio, codiPostal, poblacio);

            // Crear l'objecte Jugador i afegir-lo a la llista
            jugadors.add(new Jugador(id, nom, cognoms, adreca, foto, anyFiRevisioMedica, IBAN, idLegal, dataNaix, sexe));
        }
    } catch (SQLException ex) {
        throw new GestorBDEsportsException("Error en buscar jugadors pel NIF", ex);
    }

    return jugadors; // Retornar la llista de jugadors
}

    
    @Override
    public List<Jugador> buscarPerDataNaixJugador(Date dataNaix) throws GestorBDEsportsException {
        if (psBuscarPerDataNaix == null) {
            try {
                psBuscarPerDataNaix = conn.prepareStatement(
                    "SELECT id, nom, cognoms, direccio, codipostal, poblacio, foto, anyfirevisiomedica, iban, idlegal, datanaix, sexe " +
                    "FROM jugador WHERE datanaix = ?"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psBuscarPerDataNaix", ex);
            }
        }

        try {
            psBuscarPerDataNaix.setDate(1, (java.sql.Date) dataNaix); // Establir la data de naixement
            ResultSet rs = psBuscarPerDataNaix.executeQuery();

            List<Jugador> jugadors = new ArrayList<>();
            while (rs.next()) {
                // Recuperar els valors de la base de dades
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String cognoms = rs.getString("cognoms");
                String direccio = rs.getString("direccio");
                String codiPostal = rs.getString("codipostal");
                String poblacio = rs.getString("poblacio");
                String foto = rs.getString("foto");
                int anyFiRevisioMedica = rs.getInt("anyfirevisiomedica");
                String IBAN = rs.getString("iban");
                String idLegal = rs.getString("idlegal");
                Date dataNaixRecuperada = rs.getDate("datanaix");
                char sexe = rs.getString("sexe").charAt(0);

                // Crear l'objecte Adreca
                Adreca adreca = new Adreca(direccio, codiPostal, poblacio);

                // Crear l'objecte Jugador
                Jugador jugador = new Jugador(id, nom, cognoms, adreca, foto, anyFiRevisioMedica, IBAN, idLegal, dataNaixRecuperada, sexe);

                // Afegir el jugador a la llista
                jugadors.add(jugador);
            }

            return jugadors;
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en buscar jugadors per data de naixement", ex);
        }
    }
    
    @Override
    public List<Jugador> buscarJugadorsOrdenatsPerCognom(boolean ordenarPerCognom) throws GestorBDEsportsException {
        String consulta = "SELECT id, nom, cognoms, direccio, codipostal, poblacio, foto, anyfirevisiomedica, iban, idlegal, datanaix, sexe "
                        + "FROM jugador";

        // Si ordenarPerCognom és true, afegim l'ORDER BY
        if (ordenarPerCognom) {
            consulta += " ORDER BY cognoms";
        }

        try {
            // Si el PreparedStatement no ha estat inicialitzat, l'inicialitzem
            if (psOrdenarPerCognom == null) {
                psOrdenarPerCognom = conn.prepareStatement(consulta);
            }

            ResultSet rs = psOrdenarPerCognom.executeQuery();

            List<Jugador> jugadors = new ArrayList<>();
            while (rs.next()) {
                // Recuperem els valors de la base de dades
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String cognoms = rs.getString("cognoms");
                String direccio = rs.getString("direccio");
                String codiPostal = rs.getString("codipostal");
                String poblacio = rs.getString("poblacio");
                String foto = rs.getString("foto");
                int anyFiRevisioMedica = rs.getInt("anyfirevisiomedica");
                String IBAN = rs.getString("iban");
                String idLegal = rs.getString("idlegal");
                Date dataNaixRecuperada = rs.getDate("datanaix");
                char sexe = rs.getString("sexe").charAt(0);

                // Crear l'objecte Adreca
                Adreca adreca = new Adreca(direccio, codiPostal, poblacio);

                // Crear l'objecte Jugador
                Jugador jugador = new Jugador(id, nom, cognoms, adreca, foto, anyFiRevisioMedica, IBAN, idLegal, dataNaixRecuperada, sexe);

                // Afegir el jugador a la llista
                jugadors.add(jugador);
            }

            return jugadors;
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en buscar jugadors ordenats per cognom", ex);
        }
    }
    

    // Mètode per buscar jugadors pel nom
    @Override
    public List<Jugador> buscarNomJugador(String nom) throws GestorBDEsportsException {
        if (psBuscarNomJugador == null) {
            try {
                // Preparar la sentència SQL per buscar jugadors pel nom
                psBuscarNomJugador = conn.prepareStatement(
                    "SELECT id, nom, cognoms, direccio, codipostal, poblacio, foto, anyfirevisiomedica, iban, idlegal, datanaix, sexe " +
                    "FROM jugador WHERE LOWER(nom) LIKE LOWER(?)"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psBuscarNomJugador", ex);
            }
        }

        try {
            psBuscarNomJugador.setString(1, "%" + nom + "%"); // Afegir els percentatges per buscar per coincidència parcial
            ResultSet rs = psBuscarNomJugador.executeQuery();

            List<Jugador> jugadors = new ArrayList<>();
            while (rs.next()) {
                // Recuperar els valors de la base de dades
                int id = rs.getInt("id");
                String nomJugador = rs.getString("nom");
                String cognoms = rs.getString("cognoms");
                String direccio = rs.getString("direccio");
                String codiPostal = rs.getString("codipostal");
                String poblacio = rs.getString("poblacio");
                String foto = rs.getString("foto");
                int anyFiRevisioMedica = rs.getInt("anyfirevisiomedica");
                String IBAN = rs.getString("iban");
                String idLegal = rs.getString("idlegal");
                Date dataNaix = rs.getDate("datanaix");
                char sexe = rs.getString("sexe").charAt(0);

                // Crear l'objecte Adreca
                Adreca adreca = new Adreca(direccio, codiPostal, poblacio);

                // Crear l'objecte Jugador
                Jugador jugador = new Jugador(id, nomJugador, cognoms, adreca, foto, anyFiRevisioMedica, IBAN, idLegal, dataNaix, sexe);

                // Afegir el jugador a la llista
                jugadors.add(jugador);
            }
            return jugadors;
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en buscar jugadors pel nom", ex);
        }
    }



    // Mètode per obtenir un jugador
    @Override
    public Jugador obtenirJugador(int idJugador) throws GestorBDEsportsException {
        if (psObtenirJugador == null) {
            try {
                psObtenirJugador = conn.prepareStatement("SELECT * FROM jugador WHERE id = ?");
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirJugador", ex);
            }
        }

        try {
            psObtenirJugador.setInt(1, idJugador);
            try (ResultSet rs = psObtenirJugador.executeQuery()) {
                if (rs.next()) {
                    Adreca adreca = new Adreca(
                        rs.getString("direccio"),
                        rs.getString("codiPostal"),
                        rs.getString("poblacio")
                    );

                    return new Jugador(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("cognoms"),
                        adreca,
                        rs.getString("foto"),
                        rs.getInt("anyFiRevisioMedica"),
                        rs.getString("IBAN"),
                        rs.getString("idLegal"),
                        rs.getDate("dataNaix"),
                        rs.getString("sexe").charAt(0)
                    );
                } else {
                    throw new GestorBDEsportsException("Jugador no trobat amb id: " + idJugador);
                }
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir un jugador", ex);
        }
    }

    // Mètode per obtenir tots els jugadors
    @Override
    public List<Jugador> obtenirTotsJugadors() throws GestorBDEsportsException {
        if (psObtenirTotsJugadors == null) {
            try {
                psObtenirTotsJugadors = conn.prepareStatement("SELECT * FROM jugador ORDER BY id");
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirTotsJugadors", ex);
            }
        }

        List<Jugador> jugadors = new ArrayList<>();
        try (ResultSet rs = psObtenirTotsJugadors.executeQuery()) {
            while (rs.next()) {
                Adreca adreca = new Adreca(
                    rs.getString("direccio"),
                    rs.getString("codiPostal"),
                    rs.getString("poblacio")
                );

                jugadors.add(new Jugador(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("cognoms"),
                    adreca,
                    rs.getString("foto"),
                    rs.getInt("anyFiRevisioMedica"),
                    rs.getString("IBAN"),
                    rs.getString("idLegal"),
                    rs.getDate("dataNaix"),
                    rs.getString("sexe").charAt(0)
                ));
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir tots els jugadors", ex);
        }
        return jugadors;
    }

    // Mètode per modificar un jugador
    @Override
    public void modificarJugador(Jugador jugador) throws GestorBDEsportsException {
        if (psModificarJugador == null) {
            try {
                psModificarJugador = conn.prepareStatement(
                    "UPDATE jugador SET nom = ?, cognoms = ?, direccio = ?, codiPostal = ?, poblacio = ?, foto = ?, anyFiRevisioMedica = ?, IBAN = ?, idLegal = ?, dataNaix = ?, sexe = ? WHERE id = ?"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psModificarJugador", ex);
            }
        }

        try {
            psModificarJugador.setString(1, jugador.getNom());
            psModificarJugador.setString(2, jugador.getCognoms());
            psModificarJugador.setString(3, jugador.getAdreca().getDireccio());
            psModificarJugador.setString(4, jugador.getAdreca().getCodiPostal());
            psModificarJugador.setString(5, jugador.getAdreca().getPoblacio());
            psModificarJugador.setString(6, jugador.getFoto());
            psModificarJugador.setInt(7, jugador.getAnyFiRevisioMedica());
            psModificarJugador.setString(8, jugador.getIBAN());
            psModificarJugador.setString(9, jugador.getIdLegal());
            psModificarJugador.setDate(10, new java.sql.Date(jugador.getDataNaix().getTime()));
            psModificarJugador.setString(11, String.valueOf(jugador.getSexe()));
            psModificarJugador.setInt(12, jugador.getId());

            psModificarJugador.executeUpdate();
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en modificar el jugador", ex);
        }
    }

    // Mètode per eliminar un jugador
    @Override
    public void eliminarJugador(int idJugador) throws GestorBDEsportsException {
        if (psEliminarJugador == null) {
            try {
                psEliminarJugador = conn.prepareStatement("DELETE FROM jugador WHERE id = ?");
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psEliminarJugador", ex);
            }
        }

        try {
            psEliminarJugador.setInt(1, idJugador);
            psEliminarJugador.executeUpdate();
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en eliminar el jugador", ex);
        }
    }

    // Mètodes per a la gestió de Membres (relació N:M entre Jugador i Equip)

    // Mètode per afegir un membre
    @Override
    public void afegirMembre(Membre membre) throws GestorBDEsportsException {
        if (psAfegirMembre == null) {
            try {
                psAfegirMembre = conn.prepareStatement(
                    "INSERT INTO Membre (equip, jugador, tipus) VALUES (?, ?, ?)"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psAfegirMembre", ex);
            }
        }

        try {
            // Obtenim l'ID del jugador
            int idJugador = membre.getJugador().getId(); // Assumint que 'getId()' retorna l'ID del jugador

            // Inserim les dades del membre
            psAfegirMembre.setInt(1, membre.getEquip());  // ID de l'equip
            psAfegirMembre.setInt(2, idJugador);          // ID del jugador
            psAfegirMembre.setString(3, membre.getTipus().name()); // Tipus de membre (Titular/Convidat)

            // Executem la inserció
            psAfegirMembre.executeUpdate();
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en afegir un membre a l'equip", ex);
        }
    }


    // Mètode per eliminar un membre
    @Override
    public void eliminarMembre(Membre membre) throws GestorBDEsportsException {
        if (psEliminarMembre == null) {
            try {
                psEliminarMembre = conn.prepareStatement(
                    "DELETE FROM Membre WHERE equip = ? AND jugador = ?"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psEliminarMembre", ex);
            }
        }

        try {
            // Obtenim l'ID del jugador
            int idJugador = membre.getJugador().getId(); // Assumint que 'getId()' retorna l'ID del jugador

            // Eliminem el membre de l'equip
            psEliminarMembre.setInt(1, membre.getEquip());  // ID de l'equip
            psEliminarMembre.setInt(2, idJugador);          // ID del jugador

            // Executem l'eliminació
            psEliminarMembre.executeUpdate();
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en eliminar un membre de l'equip", ex);
        }
    }

    // Mètode per obtenir els membres d'un equip
    @Override
    public List<Membre> obtenirMembresDEquip(int idEquip) throws GestorBDEsportsException { 
        if (psObtenirMembresDEquip == null) {
            try {
                psObtenirMembresDEquip = conn.prepareStatement(
                    "SELECT m.IDJUGADOR, m.IDEQUIP, m.TITULAR_CONVIDAT " +
                    "FROM Membre m " +
                    "WHERE m.IDEQUIP = ?"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirMembresDEquip", ex);
            }
        }

        List<Membre> membres = new ArrayList<>();
        try {
            psObtenirMembresDEquip.setInt(1, idEquip);
            try (ResultSet rs = psObtenirMembresDEquip.executeQuery()) {
                while (rs.next()) {
                    // Obtenim el jugador amb el mètode obtenirJugador
                    Jugador jugador = obtenirJugador(rs.getInt("IDJUGADOR"));

                    // Assignem el tipus de membre (Titular o Convidat)
                    TipusMembre tipus = TipusMembre.valueOf(rs.getString("TITULAR_CONVIDAT"));

                    // Creem el Membre amb l'ID de l'equip, el Jugador i el tipus de membre
                    membres.add(new Membre(
                        rs.getInt("IDEQUIP"),  // IDEQUIP
                        jugador,                // Jugador
                        tipus                   // Tipus de membre (Titular o Convidat)
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir els membres de l'equip", ex);
        }
        return membres;
    }

    // Mètodes per a la gestió de Categories

    // Mètode per obtenir una categoria
    @Override
    public Categoria obtenirCategoria(int idCategoria) throws GestorBDEsportsException {
        if (psObtenirCategoria == null) {
            try {
                psObtenirCategoria = conn.prepareStatement(
                    "SELECT * FROM Categoria WHERE id = ?"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirCategoria", ex);
            }
        }

        try {
            psObtenirCategoria.setInt(1, idCategoria);
            try (ResultSet rs = psObtenirCategoria.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("edatMin"),
                        rs.getInt("edatMax")
                    );
                } else {
                    throw new GestorBDEsportsException("Categoria no trobada amb id: " + idCategoria);
                }
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir una categoria", ex);
        }
    }

    // Mètode per obtenir totes les categories
    @Override
    public List<Categoria> obtenirTotesCategories() throws GestorBDEsportsException {
        if (psObtenirTotesCategories == null) {
            try {
                psObtenirTotesCategories = conn.prepareStatement(
                    "SELECT * FROM Categoria"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirTotesCategories", ex);
            }
        }

        List<Categoria> categories = new ArrayList<>();
        try (ResultSet rs = psObtenirTotesCategories.executeQuery()) {
            while (rs.next()) {
                categories.add(new Categoria(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getInt("edatMin"),
                    rs.getInt("edatMax")
                ));
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir totes les categories", ex);
        }
        return categories;
    }

    // Mètodes per a la gestió de Temporades

    // Mètode per afegir una temporada
    @Override
    public boolean afegirTemporada(Temporada temporada) throws GestorBDEsportsException {
        if (psAfegirTemporada == null) {
            try {
                psAfegirTemporada = conn.prepareStatement(
                    "INSERT INTO Temporada (any_temporada) VALUES (?)"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psAfegirTemporada", ex);
            }
        }

        try {
            psAfegirTemporada.setInt(1, temporada.getAny());
            int filesAfectades = psAfegirTemporada.executeUpdate();
            return filesAfectades > 0; // Retorna true si s'ha afegit la temporada
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) { // SQL State 23505 = Unique violation
                return false; // No afegida perquè ja existeix
            } else {
                throw new GestorBDEsportsException("Error en afegir una temporada", ex);
            }
        }
    }


    // Mètode per obtenir una temporada
    @Override
    public Temporada obtenirTemporada(int any) throws GestorBDEsportsException {
        if (psObtenirTemporada == null) {
            try {
                psObtenirTemporada = conn.prepareStatement(
                    "SELECT * FROM Temporada WHERE any_temporada = ?"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirTemporada", ex);
            }
        }

        try {
            psObtenirTemporada.setInt(1, any);
            try (ResultSet rs = psObtenirTemporada.executeQuery()) {
                if (rs.next()) {
                    return new Temporada(rs.getInt("any_temporada"));
                } else {
                    throw new GestorBDEsportsException("Temporada no trobada amb any: " + any);
                }
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir una temporada", ex);
        }
    }

    // Mètode per obtenir totes les temporades
    @Override
    public List<Temporada> obtenirTotesTemporades() throws GestorBDEsportsException {
        if (psObtenirTotesTemporades == null) {
            try {
                psObtenirTotesTemporades = conn.prepareStatement(
                    "SELECT * FROM Temporada"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psObtenirTotesTemporades", ex);
            }
        }

        List<Temporada> temporades = new ArrayList<>();
        try (ResultSet rs = psObtenirTotesTemporades.executeQuery()) {
            while (rs.next()) {
                temporades.add(new Temporada(rs.getInt("any_temporada")));
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en obtenir totes les temporades", ex);
        }
        return temporades;
    }

    // Mètode per eliminar una temporada
    @Override
    public boolean eliminarTemporada(int any) throws GestorBDEsportsException {
        if (psEliminarTemporada == null) {
            try {
                psEliminarTemporada = conn.prepareStatement(
                    "DELETE FROM Temporada WHERE any_temporada = ?"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psEliminarTemporada", ex);
            }
        }

        try {
            psEliminarTemporada.setInt(1, any);
            int filesAfectades = psEliminarTemporada.executeUpdate();
            return filesAfectades > 0; // Retorna true si s'ha eliminat la temporada
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en eliminar una temporada", ex);
        }
    }

    // Mètodes per a la gestió d'Usuaris
    // Validació d'usuari
    @Override
    public boolean validarUsuari(String login, String contrasenya) throws GestorBDEsportsException {
        if (psValidarUsuari == null) {
            try {
                psValidarUsuari = conn.prepareStatement(
                    "SELECT COUNT(*) FROM Usuari WHERE login = ? AND password = ?"
                );
            } catch (SQLException ex) {
                throw new GestorBDEsportsException("Error en preparar la sentència psValidarUsuari", ex);
            }
        }

        try {
            psValidarUsuari.setString(1, login);
            psValidarUsuari.setString(2, contrasenya);
            ResultSet rs = psValidarUsuari.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el compte existeix amb el login i la contrasenya correctes
            } else {
                return false; // No s'ha trobat cap resultat
            }
        } catch (SQLException ex) {
            throw new GestorBDEsportsException("Error en validar l'usuari", ex);
        }
    }


    
    // Mètode per tancar la connexió
    @Override
    public void tancarConnexio() throws GestorBDEsportsException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new GestorBDEsportsException("Error en tancar la connexió amb la base de dades", e);
        }
    }

    // Confirmació de canvis a la base de dades
    @Override
    public void confirmarCanvis() throws GestorBDEsportsException {
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new GestorBDEsportsException("Error en confirmar els canvis", e);
        }
    }

    // Desfer canvis en cas d’error
    @Override
    public void desferCanvis() throws GestorBDEsportsException {
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new GestorBDEsportsException("Error en desfer els canvis", e);
        }
    }
}
