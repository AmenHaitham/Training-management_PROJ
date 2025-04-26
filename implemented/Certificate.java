
import java.sql.Blob;
import java.util.Date;
public class Certificate {
    private int certificateId;
    private int traineeId;
    private int trainingId;
    private Blob file;
    private Date dateIssued;

    public Certificate(int certificateId,int trainingId,int traineeId,Blob file,Date dateIssued){
        this.file=file;
        this.certificateId=certificateId;
        this.dateIssued=dateIssued;
        this.trainingId=trainingId;
        this.traineeId=traineeId;
    }

    public int getTraineeId(){
        return traineeId;
    }
    public void setTraineeId(int traineeId){
        this.traineeId=traineeId;
    }

    public int getTrainingId(){
        return trainingId;
    }
    public void setTrainingId(int trainingId){
        this.trainingId=trainingId;
    }

    public int getCertificateId(){
        return certificateId;
    }
    public void setCertificateId(int certificateId){
        this.certificateId=certificateId;
    }

    public Blob getFile(){
        return file;
    }
    public void setFile(Blob file){
        this.file=file;
    }


    public Date getDateIssued(){
        return dateIssued;
    }
    public void setDateIssued(Date dateIssued){
        this.dateIssued=dateIssued;
    }




}
