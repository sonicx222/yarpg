package de.pho.descent.shared.model;

import de.pho.descent.shared.auth.SecurityTools;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author pho
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Player.findAll, query = "select p from Player as p")
    ,
    @NamedQuery(name = Player.findAllByUsername, query = "select p from Player as p "
            + "where LOWER(p.username) = LOWER(:" + Player.paramUsername + ") ")})
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String findAll = "de.pho.descent.shared.model.Player.findAll";
    public static final String findAllByUsername = "de.pho.descent.shared.model.Player.findAllByUsername";
    public static final String paramUsername = "usernameParam";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    @XmlTransient
    @Column(length = 400)
    private String password;

    @Column(name = "DEACTIVE")
    private boolean deactive;

    public Player() {

    }

    public Player(String username, String password) {
        this.username = username;
        this.password = SecurityTools.createHash(password, false);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDeactive() {
        return deactive;
    }

    public void setDeactive(boolean deactive) {
        this.deactive = deactive;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.username);
        hash = 59 * hash + Objects.hashCode(this.password);
        hash = 59 * hash + (this.deactive ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        if (this.deactive != other.deactive) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        return true;
    }



    @Override
    public String toString() {
        return "de.pho.descent.shared.model.Player[ id=" + id + " ]";
    }

}
