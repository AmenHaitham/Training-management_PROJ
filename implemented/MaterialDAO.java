import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MaterialDAO {

    public boolean addMaterial(Material material) {
        String sql = "INSERT INTO materials (title, file, file_type, session_id, uploaded_by, uploaded_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement Mate = conn.prepareStatement(sql)) {

            Mate.setString(1, material.getTitle());
            Mate.setBlob(2, material.getFile());
            Mate.setString(3, material.getFileType());
            Mate.setInt(4, material.getSessionId());
            Mate.setInt(5, material.getUploadedBy());
            Mate.setTimestamp(6, new Timestamp(material.getUploadedAt().getTime()));

            return Mate.executeUpdate() > 0;

        } catch (SQLException M) {
            System.out.println("Error adding material: " + M.getMessage());
            return false;
        }
    }

    public Material getMaterialById(int materialId) {
        String sql = "SELECT * FROM materials WHERE material_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement gMate = conn.prepareStatement(sql)) {

            gMate.setInt(1, materialId);
            ResultSet r = gMate.executeQuery();

            if (r.next()) {
                return new Material(
                   r.getInt("material_id"),
             r.getString("title"),
                  r.getBlob("file"),
            r.getString("file_type"),
                  r.getInt("session_id"),
                 r.getInt("uploaded_by"),
                    r.getTimestamp("uploaded_at")
                );
            }

        } catch (SQLException GM) {
            System.out.println("Error retrieving material: " + GM.getMessage());
        }
        return null;
    }

    public List<Material> getAllMaterials() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM materials";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement LMate = conn.createStatement();
             ResultSet r = LMate.executeQuery(sql)) {

            while (r.next()) {
                Material m = new Material(
                        r.getInt("material_id"),
                        r.getString("title"),
                        r.getBlob("file"),
                        r.getString("file_type"),
                        r.getInt("session_id"),
                        r.getInt("uploaded_by"),
                        r.getTimestamp("uploaded_at")
                );
                list.add(m);
            }

        } catch (SQLException LM) {
            System.out.println("Error fetching materials: " + LM.getMessage());
        }
        return list;
    }

    public boolean deleteMaterial(int materialId) {
        String sql = "DELETE FROM materials WHERE material_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement dMate = conn.prepareStatement(sql)) {

            dMate.setInt(1, materialId);
            return  dMate.executeUpdate() > 0;

        } catch (SQLException dM) {
            System.out.println("Error deleting material: " + dM.getMessage());
            return false;
        }
    }
}
