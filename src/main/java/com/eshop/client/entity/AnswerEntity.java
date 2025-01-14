package com.eshop.client.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "tbl_answer")
public class AnswerEntity  extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name="seq_answer", sequenceName="seq_answer",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_answer")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;
    @NotNull
    private String title;
    @Column(name="display_order")
    private Integer displayOrder;
    private boolean active;

    @Override
    public String getSelectTitle() {
        return title;
    }
}
