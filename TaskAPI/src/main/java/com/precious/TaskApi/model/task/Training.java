package com.precious.TaskApi.model.task;

import java.time.LocalDateTime;
import java.util.List;

import com.precious.TaskApi.model.content.TrainingContent;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("TRAINING")
public class Training {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "training_id")
	private UUID id;
    
	private LocalDateTime duration;
    
	private String instructions;

	@OneToOne
	private Task task

}
