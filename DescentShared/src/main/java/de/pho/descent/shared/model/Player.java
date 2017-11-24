package de.pho.descent.shared.model;

import de.pho.descent.shared.auth.SecurityTools;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Player)) {
            return false;
        }
        Player other = (Player) object;

        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "de.pho.descent.shared.model.Player[ id=" + id + " ]";
    }

}
