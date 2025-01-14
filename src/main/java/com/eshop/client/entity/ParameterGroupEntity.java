package com.eshop.client.entity;

import lombok.Data;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;


@Data
@Entity
@Table(name = "tbl_parameter_group")
@Where(clause = "deleted=false")
@Audited
public class ParameterGroupEntity extends BaseEntity<Long> implements LogicalDeleted{
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name="seq_parameter_group",sequenceName="seq_parameter_group",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_parameter_group")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "TITLE")
    private String title;

    private boolean deleted;

    @Override
    public String getSelectTitle() {
        return title;
    }
}
