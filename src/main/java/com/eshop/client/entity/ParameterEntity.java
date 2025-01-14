package com.eshop.client.entity;

import lombok.Data;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_parameter")
@Where(clause = "deleted=false")
@Audited
public class ParameterEntity extends BaseEntity<Long> implements LogicalDeleted{

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="seq_parameter",sequenceName="seq_parameter",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_parameter")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "VALUE")
    private String value;

    @ManyToOne
    @JoinColumn(name = "PARAMETER_GROUP_ID")
    private ParameterGroupEntity parameterGroup;

    private boolean deleted;

    @Override
    public String getSelectTitle() {
        return title;
    }
}
