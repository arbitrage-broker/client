package com.arbitragebroker.client.dto;

import com.arbitragebroker.client.entity.CountryEntity;
import com.arbitragebroker.client.entity.RoleEntity;
import com.arbitragebroker.client.entity.UserEntity;
import com.arbitragebroker.client.model.CountryModel;
import com.arbitragebroker.client.model.RoleModel;
import com.arbitragebroker.client.model.UserModel;
import com.arbitragebroker.client.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDetailDto implements UserDetails {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private LocalDateTime modifiedDate;
    private LocalDateTime createdDate;
    private String uid;
    private String referralCode;
    private String userName;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Boolean active;
    private Set<RoleModel> roles;
    private Boolean emailVerified;
    private String role;
    private String walletAddress;
    private String profileImageUrl;
    private String treePath;
    private int childCount;
    private CountryModel country;
    private UserDetailDto parent;
    private List<UUID> parents;

    public UserDetailDto(UserEntity entity) {
        this.id = entity.getId();
        this.createdDate = entity.getCreatedDate();
        this.modifiedDate = entity.getModifiedDate();
        this.uid = entity.getUid();
        this.referralCode = entity.getUid();
        this.userName = entity.getUserName();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.active = entity.isActive();
        this.roles = toRoleModels(entity.getRoles());
        this.emailVerified = entity.getEmailVerified();
        this.role = entity.getRole();
        this.profileImageUrl = entity.getProfileImageUrl();
        this.childCount = entity.getChildCount();
        this.treePath = entity.getTreePath();
        this.walletAddress = entity.getWalletAddress();
        this.country = toCountryModel(entity.getCountry());
        if(entity.getParent()!=null)
            this.parent = new UserDetailDto(entity.getParent());
    }

    private CountryModel toCountryModel(CountryEntity country) {
        var output = new CountryModel();
        output.setId(country.getId());
        output.setName(country.getName());
        return output;
    }

    private Set<RoleModel> toRoleModels(Set<RoleEntity> roleEntities){
        if(!CollectionUtils.isEmpty(roleEntities)){
            return roleEntities.stream().map(m->new RoleModel(m.getId(),m.getRole(),m.getTitle())).collect(Collectors.toSet());
        }
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return userName;
    }

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
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (UserDetailDto) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public UserModel toUserModel(UserDetailDto userDetail){
        var model = new UserModel();
        model.setId(userDetail.getId());
        model.setCreatedDate(userDetail.getCreatedDate());
        model.setModifiedDate(userDetail.getModifiedDate());
        model.setUserName(userDetail.getUsername());
        model.setEmail(userDetail.getEmail());
        model.setFirstName(userDetail.getFirstName());
        model.setLastName(userDetail.getLastName());
        model.setActive(userDetail.getActive());
        model.setUid(userDetail.getUid());
        model.setReferralCode(userDetail.getReferralCode());
        if(userDetail.getParent()!=null)
            model.setParent(toUserModel(userDetail.getParent()));
        model.setTreePath(userDetail.getTreePath());
        model.setChildCount(userDetail.getChildCount());
        model.setWalletAddress(userDetail.getWalletAddress());
        model.setProfileImageUrl(userDetail.getProfileImageUrl());
        model.setCountry(userDetail.getCountry());
        model.setRoles(userDetail.getRoles());
        model.setEmailVerified(userDetail.getEmailVerified());
        model.setRole(userDetail.getRole());
        return model;
    }
    public UserModel toUserModel(){
        return toUserModel(this);
    }
}
