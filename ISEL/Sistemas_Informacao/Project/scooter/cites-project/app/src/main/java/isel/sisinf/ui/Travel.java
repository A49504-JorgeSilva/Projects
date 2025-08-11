package isel.sisinf.ui;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRAVEL")
public class Travel {

	@Id
	@Column(name = "dinitial")
	private LocalDateTime dinitial;

	@Column(name = "comment", length = 100)
	private String comment;

	@Column(name = "evaluation")
	private Integer evaluation;

	@Column(name = "dfinal")
	private LocalDateTime dfinal;

	@ManyToOne
	@JoinColumn(name = "client", nullable = false)
	private Client client;

	@ManyToOne
	@JoinColumn(name = "scooter", nullable = false)
	private Scooter scooter;

	@ManyToOne
	@JoinColumn(name = "stinitial", nullable = false)
	private Station stinitial;

	@ManyToOne
	@JoinColumn(name = "stfinal")
	private Station stfinal;

	// Getters and Setters

	public LocalDateTime getDinitial() {
		return dinitial;
	}

	public void setDinitial(LocalDateTime dinitial) {
		this.dinitial = dinitial;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Integer evaluation) {
		this.evaluation = evaluation;
	}

	public LocalDateTime getDfinal() {
		return dfinal;
	}

	public void setDfinal(LocalDateTime dfinal) {
		this.dfinal = dfinal;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Scooter getScooter() {
		return scooter;
	}

	public void setScooter(Scooter scooter) {
		this.scooter = scooter;
	}

	public Station getStinitial() {
		return stinitial;
	}

	public void setStinitial(Station stinitial) {
		this.stinitial = stinitial;
	}

	public Station getStfinal() {
		return stfinal;
	}

	public void setStfinal(Station stfinal) {
		this.stfinal = stfinal;
	}
}