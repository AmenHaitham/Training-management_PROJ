import java.sql.*;
import java.util.ArrayList;
import java.util.List;

    public class CourseDAO {

        public boolean addCourse(Course course) {
            String sql = "INSERT INTO courses (courseId, title, description, assignedTrainer, trainingId, status) VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement Cour = conn.prepareStatement(sql)) {

                Cour.setInt(1, course.getCourseId());
                Cour.setString(2, course.getTitle());
                Cour.setString(3, course.getDescription());
                Cour.setInt(4, course.getAssignedTrainer());
                Cour.setInt(5, course.getTrainingId());
                Cour.setString(6, course.getStatus());

                return Cour.executeUpdate() > 0;

            } catch (SQLException Cu) {
                System.out.println("Error adding course: " + Cu.getMessage());
                return false;
            }
        }

        public boolean updateCourse(Course course) {
            String sql = "UPDATE courses SET title=?, description=?, assignedTrainer=?, trainingId=?, status=? WHERE courseId=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement UCour = conn.prepareStatement(sql)) {

                UCour.setString(1, course.getTitle());
                UCour.setString(2, course.getDescription());
                UCour.setInt(3, course.getAssignedTrainer());
                UCour.setInt(4, course.getTrainingId());
                UCour.setString(5, course.getStatus());
                UCour.setInt(6, course.getCourseId());

                return UCour.executeUpdate() > 0;

            } catch (SQLException UC) {
                System.out.println("Error updating course: " + UC.getMessage());
                return false;
            }
        }


        public boolean deleteCourse(int courseId) {
            String sql = "DELETE FROM courses WHERE courseId=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement dCour = conn.prepareStatement(sql)) {

                dCour.setInt(1, courseId);
                return dCour.executeUpdate() > 0;

            } catch (SQLException dC) {
                System.out.println("Error deleting course: " + dC.getMessage());
                return false;
            }
        }


        public Course getCourseById(int courseId) {
            String sql = "SELECT * FROM courses WHERE courseId=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement gCour = conn.prepareStatement(sql)) {

                gCour.setInt(1, courseId);
                ResultSet r = gCour.executeQuery();

                if (r.next()) {
                    return new Course(
                            r.getInt("courseId"),
                            r.getString("title"),
                            r.getString("description"),
                            r.getInt("assignedTrainer"),
                            r.getInt("trainingId"),
                            r.getString("status")
                    );
                }

            } catch (SQLException gC) {
                System.out.println("Error fetching course: " + gC.getMessage());
            }
            return null;
        }


        public List<Course> getAllCourses() {
            List<Course> courses = new ArrayList<>();
            String sql = "SELECT * FROM courses";

            try (Connection conn = DatabaseConnection.getConnection();
                 Statement LCour = conn.createStatement();
                 ResultSet r = LCour.executeQuery(sql)) {

                while (r.next()) {
                    Course course = new Course(
                            r.getInt("courseId"),
                            r.getString("title"),
                            r.getString("description"),
                            r.getInt("assignedTrainer"),
                            r.getInt("trainingId"),
                            r.getString("status")
                    );
                    courses.add(course);
                }

            } catch (SQLException LC) {
                System.out.println("Error fetching courses: " + LC.getMessage());
            }
            return courses;
        }
    }


