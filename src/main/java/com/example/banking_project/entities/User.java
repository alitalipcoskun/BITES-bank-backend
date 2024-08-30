package com.example.banking_project.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Date;
import java.util.List;


@Data // Data creates automatically Getters and Setters for the attributes of the class.
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_user")
public class User implements UserDetails {
    //The implemented class is used in JWT processes.

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//JPA controls the generation of this attribute
    private Long id;

    @CreationTimestamp
    private Date created_at;
    @UpdateTimestamp
    private Date updated_at;
    private String name;
    private String surname;
    @Column(unique = true)
    private String mail;
    @Column(unique= true)
    private String phone;
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //This method should return list of roles
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    /*
        It is for overriding the subject of our project. The default subject for the library is username. For this case,
        it is phone.
    */
    @Override
    public String getUsername() {
        return phone;
    }

    //The below function override operations is required to use UserDetails class.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword(){
        return password;
    }
}
