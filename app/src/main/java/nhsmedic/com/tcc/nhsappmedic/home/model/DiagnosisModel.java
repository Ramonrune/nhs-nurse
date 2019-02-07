package nhsmedic.com.tcc.nhsappmedic.home.model;


import com.rabbitmq.client.Channel;

public class DiagnosisModel {

    private String idDiagnosis;
    private String dateDiagnosis;
    private String annotation;
    private String idPatient;
    private String idPhysician;
    private String patientName;
    private String patientPhoto;

    private long deliveryTag;
    private Channel channel;

    public String getIdDiagnosis() {
        return idDiagnosis;
    }

    public void setIdDiagnosis(String idDiagnosis) {
        this.idDiagnosis = idDiagnosis;
    }

    public String getDateDiagnosis() {
        return dateDiagnosis;
    }

    public void setDateDiagnosis(String dateDiagnosis) {
        this.dateDiagnosis = dateDiagnosis;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(String idPatient) {
        this.idPatient = idPatient;
    }

    public String getIdPhysician() {
        return idPhysician;
    }

    public void setIdPhysician(String idPhysician) {
        this.idPhysician = idPhysician;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhoto() {
        return patientPhoto;
    }

    public void setPatientPhoto(String patientPhoto) {
        this.patientPhoto = patientPhoto;
    }

    public long getDeliveryTag() {
        return deliveryTag;
    }

    public void setDeliveryTag(long deliveryTag) {
        this.deliveryTag = deliveryTag;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
