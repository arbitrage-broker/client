package com.arbitragebroker.client.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_telegram")
public class TelegramEntity extends BaseEntity<Long> {

	@Id
	@SequenceGenerator(name="seq_telegram",sequenceName="seq_telegram",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_telegram")
	private Long id;

	@Column(name = "chat_id", nullable = false, unique = true)
	private String chatId;
	@Column(nullable = false)
	private String phone;
	private String role;

	@Override
	public String getSelectTitle() {
		return String.format("%s:%s",phone,chatId);
	}
}
