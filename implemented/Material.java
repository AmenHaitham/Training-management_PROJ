
import java.sql.Blob;
import java.util.Date;
public class Material {
    private int materialId;
    private String title;
    private Blob file;
  private String fileType;
    private Date uploadedAt;
    private int  uploadedBy;
    private int sessionId;

    public Material(int materialId,String title,Blob file,String fileType,int sessionId,int uploadedBy,Date uploadedAt){
        this.materialId=materialId;
        this.title=title;
        this.file=file;
        this.fileType=fileType;
        this.sessionId=sessionId;
        this.uploadedAt=uploadedAt;
        this.uploadedBy=uploadedBy;
    }
    public int getMaterialId(){
        return materialId;
    }

    public void setMaterialId( int materialId){
        this.materialId=materialId;
    }

    public int getSessionId(){
        return sessionId;
    }
    public void setSessionId( int sessionId){
        this.sessionId=sessionId;
    }

    public int getUploadedBy(){
        return uploadedBy;
    }
    public void setUploadedBy( int uploadedBy){
        this.uploadedBy=uploadedBy;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getFileType(){
        return fileType;
    }
    public void setFileType( String fileType){
        this.fileType=fileType;
    }

    public Date getUploadedAt(){
        return uploadedAt;
    }
    public void setUploadedAt( Date uploadedAt){
        this.uploadedAt=uploadedAt;
    }

    public Blob getFile(){
        return file;
    }
    public void setFile( Blob file){
        this.file=file;
    }

}
