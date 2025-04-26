public class Course {
    private int courseId;
    private String title;
    private String description;
    private int assignedTrainer;
    private int TrainingId;
    private String Status;

    public Course(int courseId, String title, String description, int assignedTrainer, int trainingId, String status){
        this.courseId=courseId;
        this.title=title;
        this.description=description;
        this.assignedTrainer=assignedTrainer;
        this.Status=status;
        this.TrainingId=trainingId;
    }

    public int getCourseId(){
        return courseId;
    }
    public void setCourseId( int courseId){
        this.courseId=courseId;
    }

    public int getAssignedTrainer(){
        return assignedTrainer;
    }
    public void setAssignedTrainer( int assignedTrainer){
        this.assignedTrainer=assignedTrainer;
    }

    public int getTrainingId(){
        return TrainingId;
    }
    public void setTrainingId( int trainingId){
        this.TrainingId=TrainingId;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle( String title){
        this.title=title;
    }

    public String getDescription(){
        return description;
    }
    public void setDescription( String description){
        this.description=description;
    }
    public String getStatus(){
        return Status;
    }

    public void setStatus(String status){
        this.Status=status;
    }
}

