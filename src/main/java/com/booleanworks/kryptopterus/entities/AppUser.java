/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.booleanworks.kryptopterus.entities;

import com.booleanworks.kryptopterus.application.MainHibernateUtil;
import com.booleanworks.kryptopterus.application.WebAppBootstrapper;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.hibernate.Session;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement

public class AppUser extends AppPerson implements Serializable {

    static public final int hashAndSaltSize = 32;

    private static final long serialVersionUID = 1L;

    public AppUser() {
        super();
    }

    @XmlElement
    protected String username;

    @XmlElement
    protected String secret1;
    @XmlElement
    protected String secret2;
    @XmlElement
    protected String secret3;

    @XmlElement
    protected int securityIndex;

    @XmlElement
    protected boolean disabled;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastLogin;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastLoginFailure;

    @XmlElement
    @OneToMany(mappedBy = "appUser", cascade = {CascadeType.ALL})
    protected Set<AppUserGroupMembership> memberships;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppUser)) {
            return false;
        }
        AppUser other = (AppUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppUser[ id=" + id + " ]";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecret1() {
        return secret1;
    }

    public void setSecret1(String secret1) {
        this.secret1 = secret1;
    }

    public String getSecret2() {
        return secret2;
    }

    public void setSecret2(String secret2) {
        this.secret2 = secret2;
    }

    public String getSecret3() {
        return secret3;
    }

    public void setSecret3(String secret3) {
        this.secret3 = secret3;
    }

    public int getSecurityIndex() {
        return securityIndex;
    }

    public void setSecurityIndex(int securityIndex) {
        this.securityIndex = securityIndex;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public static byte[] genSalt() throws UnsupportedEncodingException {
        // tuning parameters

        // these sizes are relatively arbitrary
        int seedBytes = AppUser.hashAndSaltSize;
        int hashBytes = AppUser.hashAndSaltSize;

        // increase iterations as high as your performance can tolerate
        // since this increases computational cost of password guessing
        // which should help security
        int iterations = 1000;

        // to save a new password:
        SecureRandom rng = new SecureRandom();
        byte[] salt = rng.generateSeed(seedBytes);

        // now save salt and hash
        return salt;

    }

    public static byte[][] hash(String stringToHash) throws UnsupportedEncodingException {
        // tuning parameters

        // these sizes are relatively arbitrary
        int seedBytes = AppUser.hashAndSaltSize;
        int hashBytes = AppUser.hashAndSaltSize;

        // increase iterations as high as your performance can tolerate
        // since this increases computational cost of password guessing
        // which should help security
        int iterations = 1000;

        // to save a new password:
        SecureRandom rng = new SecureRandom();
        byte[] salt = rng.generateSeed(seedBytes);

        PKCS5S2ParametersGenerator kdf = new PKCS5S2ParametersGenerator();
        kdf.init(stringToHash.getBytes("UTF-8"), salt, iterations);

        byte[] hash
                = ((KeyParameter) kdf.generateDerivedMacParameters(8 * hashBytes)).getKey();

        // now save salt and hash
        return new byte[][]{hash, salt};

    }

    public static byte[][] hash(String stringToHash, byte[] sourceSalt) throws UnsupportedEncodingException {
        // tuning parameters

        // these sizes are relatively arbitrary
        int seedBytes = AppUser.hashAndSaltSize;
        int hashBytes = AppUser.hashAndSaltSize;

        // increase iterations as high as your performance can tolerate
        // since this increases computational cost of password guessing
        // which should help security
        int iterations = 1000;

        // to save a new password:
        SecureRandom rng = new SecureRandom();
        byte[] salt = sourceSalt;

        PKCS5S2ParametersGenerator kdf = new PKCS5S2ParametersGenerator();
        kdf.init(stringToHash.getBytes("UTF-8"), salt, iterations);

        byte[] hash
                = ((KeyParameter) kdf.generateDerivedMacParameters(8 * hashBytes)).getKey();

        // now save salt and hash
        return new byte[][]{hash, salt};

    }

    public static boolean checkHash(String sourceString, byte[] sourceHash, byte[] sourceSalt) throws UnsupportedEncodingException {
        // tuning parameters

        // these sizes are relatimmodificationDateodificationDatevely arbitrary
        int seedBytes = AppUser.hashAndSaltSize;
        int hashBytes = AppUser.hashAndSaltSize;

        // increase iterations as high as your performance can tolerate
        // since this increases computational cost of password guessing
        // which should help security
        int iterations = 1000;

        // to save a new password:
        SecureRandom rng = new SecureRandom();
        byte[] salt = sourceSalt;

        PKCS5S2ParametersGenerator kdf = new PKCS5S2ParametersGenerator();
        kdf.init(sourceString.getBytes("UTF-8"), salt, iterations);

        byte[] hash
                = ((KeyParameter) kdf.generateDerivedMacParameters(8 * hashBytes)).getKey();

        if (hash.length != sourceHash.length) {
            return false;
        }

        for (int cByte = 0; cByte < hash.length; cByte++) {
            if (hash[cByte] != sourceHash[cByte]) {
                return false;
            }
        }

        return true;

    }

    public void encodeSecret(String secret) throws UnsupportedEncodingException {

        String newSecret1 = "";
        String newSecret2 = "";
        String newSecret3 = "";

        byte[] salt1 = AppUser.genSalt();
        byte[] salt2 = AppUser.genSalt();
        byte[] hash = AppUser.hash(secret, salt1)[0];

        for (int cByte = 0; cByte < AppUser.hashAndSaltSize; cByte++) {
            newSecret1 += new Formatter().format("%02x", hash[cByte]).toString();
            newSecret2 += new Formatter().format("%02x", salt1[cByte]).toString();
            newSecret3 += new Formatter().format("%02x", salt2[cByte]).toString();

        }

        for (int rotCount = 0; rotCount < new Random().nextInt(12); rotCount++) {
            String tmp = newSecret3;
            newSecret3 = newSecret2;
            newSecret2 = newSecret1;
            newSecret1 = tmp;
        }

        this.setSecret1(newSecret1);
        this.setSecret2(newSecret2);
        this.setSecret3(newSecret3);

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public boolean checkSecret(String secret) throws UnsupportedEncodingException {

        String secret1 = this.getSecret1();
        String secret2 = this.getSecret2();
        String secret3 = this.getSecret3();

        for (int rotCount = 0; rotCount < 6; rotCount++) {
            String tmp = secret3;
            secret3 = secret2;
            secret2 = secret1;
            secret1 = tmp;

            byte[] hash = new byte[AppUser.hashAndSaltSize];
            byte[] salt = new byte[AppUser.hashAndSaltSize];

            hash = hexStringToByteArray(secret1);
            salt = hexStringToByteArray(secret2);

            if (AppUser.checkHash(secret, hash, salt)) {
                return true;
            }

        }

        return false;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getLastLoginFailure() {
        return lastLoginFailure;
    }

    public void setLastLoginFailure(Date lastLoginFailure) {
        this.lastLoginFailure = lastLoginFailure;
    }

    public Set<AppUserGroupMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<AppUserGroupMembership> memberships) {
        this.memberships = memberships;
    }

    public static AppUser QuickCreateNewAppUser(String username, String secret, String email) {

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();

        main:
        {
            Session session = mhu.getNewSession();

            List<Object> appUsers = mhu.executeQuery(session, "SELECT u FROM AppUser u WHERE u.username = :username", new Object[][]{{"username", username}}, 0, 1);

            if (appUsers.isEmpty()) {
                AppUser newUser = new AppUser();
                newUser.setUsername(username);
                newUser.setSecurityIndex(10);
                newUser.setCreationDate(new Date());
                newUser.setEmail(email);
                try {
                    newUser.encodeSecret(secret);
                    System.out.println("adminUser.checkSecret(\"*****\")=" + newUser.checkSecret(secret));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WebAppBootstrapper.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
                mhu.SimpleSaveOrUpdate(newUser, session);
                return newUser;

            } else {

                return (AppUser) appUsers.get(0);
            }

        }

    }

    public static AppUser findUserOrNull(String username) {

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();

        return (AppUser) mhu.findOrNull(mhu.getNewSession(), "SELECT u FROM AppUser u WHERE u.username = :username", new Object[][]{{"username", username}});

    }

    public boolean isMemberOf(String groupSecurityLabel) {

        for (AppUserGroupMembership augm : this.getMemberships()) {
            if (augm.getAppUserGroup().getSecurityLabel().equals(groupSecurityLabel)) {
                return true;
            }
        }

        return false;

    }

    public static boolean isMemberOf(String username, String groupSecurityLabel) {
        AppUser foundUser = AppUser.findUserOrNull(username);

        if (foundUser == null) {
            return false;
        } else {
            for (AppUserGroupMembership augm : foundUser.getMemberships()) {
                if (augm.getAppUserGroup().getSecurityLabel().equals(groupSecurityLabel)) {
                    return true;
                }
            }
        }

        return false;
    }

}
