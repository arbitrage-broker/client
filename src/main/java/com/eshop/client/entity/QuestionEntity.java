package com.eshop.client.entity;

import com.eshop.client.enums.AnswerType;
import com.eshop.client.enums.QuestionType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_question")
public class QuestionEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq_question", sequenceName="seq_question",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_question")
	private Long id;
	@NotNull
	private String title;
	@Column(name="display_order")
	private Integer displayOrder;
	@Enumerated(value = EnumType.STRING)
	private QuestionType type;
	@Column(name = "answer_type", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private AnswerType answerType;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	private List<AnswerEntity> answers = new ArrayList<>();

	private boolean active;

	@Override
	public String getSelectTitle() {
		return title;
	}
}
