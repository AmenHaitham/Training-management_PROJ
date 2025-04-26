import java.util.ArrayList;
import java.util.List;

public class Trainee {

    private List<Material> materials;
    private List<Feedback> feedbacks;
    private List<Certificate> certificates;

    public Trainee() {
        this.materials = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.certificates = new ArrayList<>();
    }

    public ArrayList<Material> viewMaterial() {
        return new ArrayList<>(materials);
    }

    public ArrayList<Feedback> viewFeedback() {
        return new ArrayList<>(feedbacks);
    }

    public boolean giveFeedback(Feedback feedback) {
        return feedbacks.add(feedback);
    }

    public ArrayList<Certificate> downloadCertificate() {
        return new ArrayList<>(certificates);
    }
}
