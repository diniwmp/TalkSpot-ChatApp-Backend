package entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "call_log")
public class CallLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "caller_id", nullable = false)
    private User caller;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

@Column(name = "call_type", columnDefinition = "ENUM('VOICE', 'VIDEO')", nullable = false)
private String callType; // "VOICE" or "VIDEO"

@Column(name = "status", columnDefinition = "ENUM('MISSED', 'ACCEPTED', 'REJECTED')", nullable = false)
private String status; // "MISSED", "ACCEPTED", "REJECTED"

    @Column(name = "channel_name", nullable = false)
    private String channelName;

    public CallLog() {}

    public CallLog(User caller, User receiver, String callType, String status, String channelName) {
        this.caller = caller;
        this.receiver = receiver;
        this.callType = callType;
        this.status = status;
        this.channelName = channelName;
        this.setCreatedAt(new Date());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public User getCaller() { return caller; }
    public void setCaller(User caller) { this.caller = caller; }
    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    public String getCallType() { return callType; }
    public void setCallType(String callType) { this.callType = callType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
}