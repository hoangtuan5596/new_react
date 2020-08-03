package com.moso.springkeycloak.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Users {
    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "MY_SEQ_GEN", initialValue = 205, allocationSize = 12)
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    private int id;
    private String username;
    private String password;
}
