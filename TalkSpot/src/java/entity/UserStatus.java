package entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_status")
public class UserStatus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "media_url", length = 500, nullable = false)
    private String mediaUrl;

    public UserStatus() {}

    public UserStatus(User user, String mediaUrl) {
        this.user = user;
        this.mediaUrl = mediaUrl;
        this.setCreatedAt(new Date());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}