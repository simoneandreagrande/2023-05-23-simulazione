package it.polito.tdp.baseball.model;

import java.util.Objects;

public class Grado implements Comparable<Grado>{
	
	private People player;
	private int grado;
	
	
	public Grado(People player, int grado) {
		super();
		this.player = player;
		this.grado = grado;
	}


	public People getPlayer() {
		return player;
	}


	public int getGrado() {
		return grado;
	}
	
	


	public void setPlayer(People player) {
		this.player = player;
	}


	public void setGrado(int grado) {
		this.grado = grado;
	}


	@Override
	public int hashCode() {
		return Objects.hash(grado, player);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grado other = (Grado) obj;
		return grado == other.grado && Objects.equals(player, other.player);
	}


	@Override
	public String toString() {
		return "Player: " + player + "; \t grado:" + grado;
	}


	@Override
	public int compareTo(Grado o) {
		return o.grado-this.grado;
	}
	

}
