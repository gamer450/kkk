package ru.runa.wfe.chat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.ForeignKey;
import ru.runa.wfe.user.Actor;

@Entity
@Table(name = "CHAT_MESSAGE")
public class ChatMessage {

    private Long id;
    private String text;
    private String ierarchyMessage;
    private Integer chatId;
    private Timestamp date;
    private Actor actor;
    private Boolean haveFiles = false;
    private Boolean active = true;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
    @SequenceGenerator(name = "sequence", sequenceName = "SEQ_CHAT_MESSAGE", allocationSize = 1)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "TEXT")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "QUOTED_MESSAGE_IDS")
    public String getIerarchyMessage() {
        return ierarchyMessage;
    }

    public void setIerarchyMessage(String ierarchyMessage) {
        this.ierarchyMessage = ierarchyMessage;
    }

    @Transient
    public List<Integer> getIerarchyMessageArray() {
        ArrayList<Integer> ierarchyMessage0 = new ArrayList<Integer>();

        String messagesIds[] = ierarchyMessage.split(":");
        for (int i = 0; i < messagesIds.length; i++) {
            if (!(messagesIds[i].isEmpty())) {
                ierarchyMessage0.add(Integer.parseInt(messagesIds[i]));
            }
        }

        return ierarchyMessage0;
    }

    @Transient
    public void setIerarchyMessageArray(List<Integer> ierarchyMessage) {
        StringBuilder newierarchyMessage = new StringBuilder(ierarchyMessage.size() * 4);
        if (ierarchyMessage.size() > 0) {
            for (int i = 0; i < ierarchyMessage.size() - 1; i++) {
                newierarchyMessage.append(ierarchyMessage.get(i).toString()).append(':');
            }
            newierarchyMessage.append(ierarchyMessage.get(ierarchyMessage.size() - 1).toString());
        }
        this.ierarchyMessage = newierarchyMessage.toString();
    }

    @Column(name = "CHAT_ID")
    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    @Column(name = "MESSAGE_DATE")
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Transient
    public long getUserId() {
        return actor.getId();
    }

    @Transient
    public String getUserName() {
        return actor.getName();
    }

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @ForeignKey(name = "FK_EXECUTOR_ID")
    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Column(name = "HAVE_FILES")
    public Boolean getHaveFiles() {
        return haveFiles;
    }

    public void setHaveFiles(Boolean haveFiles) {
        this.haveFiles = haveFiles;
    }

    @Column(name = "IS_ACTIVE")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
