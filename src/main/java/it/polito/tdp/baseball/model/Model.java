package it.polito.tdp.baseball.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.baseball.db.BaseballDAO;

public class Model {
	
	private BaseballDAO dao;
	private Graph<People, DefaultEdge> grafo;
	private Map<Integer, Team> teamsIDMap;
	private Map<String, People> playersIDMap;
	
	private Map<People, Double> salariesIDMap;
	private Map<People, List<Team>> playerTeamsMap;
	private double salarioDreamTeam;
	private List<People> dreamTeam;
	

	public Model() {
		this.dao = new BaseballDAO();
		this.teamsIDMap = new HashMap<Integer, Team>();
		List<Team> squadre = this.dao.readAllTeams();
		for (Team t : squadre) {
			this.teamsIDMap.put(t.getID(), t);
		}
		
		this.playersIDMap = new HashMap<String, People>();
		List<People> giocatori = this.dao.readAllPlayers();
		for (People p : giocatori) {
			this.playersIDMap.put(p.getPlayerID(), p);
		}
		
		this.salariesIDMap = new HashMap<People, Double>();
		this.playerTeamsMap = new HashMap<People, List<Team>>();
		
	}
	
	
	
	/**
	 * Primo metodo per fare il grafo, calcolando gli archi direttamente con una query
	 * @param anno
	 * @param salario
	 */
	public void creaGrafo_con_aggregazione_via_query(int anno, double salario) {
		/*
		 *  Creiamo il grafo in questa funzione, così che se la richiamiamo di nuovo il 
		 *  grafo precedente viene cancellato
		 */
		this.grafo = new SimpleGraph<People, DefaultEdge>(DefaultEdge.class);
			
		// Aggiunta VERTICI 
		List<People> vertici = this.dao.getVertices(anno, salario);
		Graphs.addAllVertices(this.grafo, vertici);
		
		// Aggiunta ARCHI gli archi. Qui abbiamo fatto 3 metodi diversi per leggere dal DB
		List<Arco> archi = this.dao.getEdges_v1(anno, salario, this.playersIDMap);  //<- VERSIONE 1
//		List<Arco> archi = this.dao.getEdges_v2(anno, salario, this.playersIDMap);  //<- VERSIONE 2
		for (Arco a : archi) {
			this.grafo.addEdge(a.getPlayer1(), a.getPlayer2());
		}
		
		/*
		 * Creato il grafo, possiamo registrarci in una mappa il salario dei giocatori che ne fanno parte
		 * perché ci servirà per fare il punto 2 della simulazione
		 * Registriamoci di questi giocatori il loro salario in quell'anno. In una seconda mappa
		 * ci registriamo in quali squadre ha giocato ogni giocatore. Questo non è necessario,
		 * lo facciamo solo per visualizzare la squadra di appartenenza dei giocatori del dream team,
		 * per debugging.
		 */
		salariesIDMap = new HashMap<People, Double>();
		for (People p : vertici) {
			salariesIDMap.put(p, this.dao.getPlayerSalaryInYear(anno, p));
		}
		
		playerTeamsMap = new HashMap<People, List<Team>>();
		for (People p : vertici) {
			List<Team> squadre = this.dao.getPlayerTeamsInYear(anno, p, teamsIDMap);
			playerTeamsMap.put(p, squadre);
		}
		
	}
	
	
	/**
	 * Secondo metodo per creare il grafo, calcolando gli archi in java
	 * @param anno
	 * @param salario
	 */
	public void creaGrafo_con_aggregazione_in_java(int anno, double salario) {
		/*
		 *  Creiamo il grafo in questa funzione, così che se la richiamiamo di nuovo il
		 * grafo precedente viene cancellato
		 */
		this.grafo = new SimpleGraph<People, DefaultEdge>(DefaultEdge.class);
		
		
		// Aggiungiamo i vertici. 
		List<People> vertici = this.dao.getVertices(anno, salario);
		Graphs.addAllVertices(this.grafo, vertici);
		
		/* 
		 * In questo caso, prima leggiamo le apparizioni fatte da tutti i giocatori in un anno.
		 * Poi, prendiamo da questa lista solo le apparizioni dei giocatori che sono effetivamente vertici del grafo.
		 * Infine, cicliamo sulla lista per prendere le coppie di giocatori che hanno giocato nella stessa squadra
		 */
		// LETTURA DI TUTTE LE APPEARANCES
		List<Appearances> apps = this.dao.getAppearancesInYear(anno);
		//FILTRAGGIO< PER TENERE SOLO LE APPEARANCES DI GIOCATORI CHE SONO VERTICI DEL GRAFO
		List<Appearances> apps_filtered = new ArrayList<Appearances>(apps);
		for (Appearances a : apps) {
			if (!vertici.contains(this.playersIDMap.get(a.getPlayerID())) ) {
				apps_filtered.remove(a);
			}
		}
		//CICLO PER TROVARE LE COPPIE DI GIOCATORI CHE HANNO GIOCATE NELLA STESSA SQUADRA
		for (int i =0; i<apps_filtered.size(); i++) {
			for (int j = i+1; j<apps_filtered.size(); j++) {
				Appearances a1 = apps_filtered.get(i);
				Appearances a2 = apps_filtered.get(j);
				String id1 = a1.getPlayerID();
				String id2 = a2.getPlayerID();
				People p1 = this.playersIDMap.get(id1);
				People p2 = this.playersIDMap.get(id2);
				if ( !id1.equals(id2) && a1.getTeamID().equals(a2.getTeamID())) {
					this.grafo.addEdge(p1, p2);
				}
			}
		}
		
		/*
		 * Creato il grafo, possiamo registrarci in una mapp il salario dei giocatori che ne fanno parte
		 * perché ci servirà per fare il punto 2 della simulazione
		 * Registriamoci di questi giocatori il loro salario in quell'anno. In una seconda mappa
		 * ci registriamo in quali squadre ha giocato ogni giocatore. Questo non è necessario,
		 * lo facciamo solo per visualizzare la squadra di appartenenza dei giocatori del dream team,
		 * per debugging.
		 */
		salariesIDMap = new HashMap<People, Double>();
		for (People p : vertici) {
			salariesIDMap.put(p, this.dao.getPlayerSalaryInYear(anno, p));
		}
		
		playerTeamsMap = new HashMap<People, List<Team>>();
		for (People p : vertici) {
			List<Team> squadre = this.dao.getPlayerTeamsInYear(anno, p, teamsIDMap);
			playerTeamsMap.put(p, squadre);
		}
	}
	

	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	
	/**
	 * Metodo per calcolare le componenti connesse
	 * @return
	 */
	public List<Set<People>> calcolaComponentiConnesse(){
		ConnectivityInspector<People, DefaultEdge> inspect = new ConnectivityInspector<People, DefaultEdge>(this.grafo);
		return inspect.connectedSets();
	}
	
	
	/**
	 * Metodo che calcola tutti i vertici di grado massimo nel grafo
	 * @return
	 */
	public List<Grado> calcolaGradoMassimo() {
		List<Grado> verticiGradoMassimo = new ArrayList<Grado>();
		for (People p : this.grafo.vertexSet()) {
			int grado = Graphs.neighborListOf(this.grafo, p).size();
			if (verticiGradoMassimo.isEmpty() || grado == verticiGradoMassimo.get(0).getGrado()) {
				verticiGradoMassimo.add(new Grado (p, grado) );
			} else if(grado > verticiGradoMassimo.get(0).getGrado()) {
				verticiGradoMassimo.clear();
				verticiGradoMassimo.add(new Grado (p, grado) );
			}
		}		
		return verticiGradoMassimo;
	}
	
	
	
	/**
	 * Metodo che calcola il Dream Team
	 */
	public void  calcolaDreamTeam() {
		this.salarioDreamTeam = 0.0;
		this.dreamTeam = new ArrayList<People>();
		List<People> rimanenti = new ArrayList<People>(this.grafo.vertexSet());
		
		/*
		 * Questo check non era richiesto nel testo, ma servo ad escludere dal calcolo
		 * del dream team i giocatori che non hanno mai giocato nell'anno (ad esempio perché infortunati).
		 * Per come è stato costruito il grafo questi sono dei vertici isolati.
		 */
		
		List<People> playersInattivi = new ArrayList<People>(this.grafo.vertexSet());
		for (People p : rimanenti) {
			if (!this.playerTeamsMap.get(p).isEmpty()){
				playersInattivi.remove(p);
			}
		}
		rimanenti.removeAll(playersInattivi);
		
		ricorsione(new ArrayList<People>(), rimanenti);
	}
	
	
	
	/**
	 * La ricorsione vera e propria
	 * @param parziale
	 * @param rimanenti
	 */
	private void ricorsione(List<People> parziale, List<People> rimanenti){
		/*
		 * L'idea della ricorsione è di prendere un giocatore, metterlo nella lista parziale,
		 * e rimuovere tutti i suoi compagni di squadra (trovati come i suoi vicini) dalla lista di giocatori rimanenti.
		 * Dopodichè, ripetiamo la ricorsione, usando parziale ed il nuovo insieme ridotto di giocatori rimanenti,
		 * fino a che non li finiamo.
		 */
		// Condizione Terminale
		if (rimanenti.isEmpty()) {
			//calcolo costo
			double salario = getSalarioTeam(parziale);
			if (salario>this.salarioDreamTeam) {
				this.salarioDreamTeam = salario;
				this.dreamTeam = new ArrayList<People>(parziale);
			}
			return;
		}
		
		/*
		 * VERSIONE NON OTTIMIZZATA DELLA RICORSIONE
		 */
		/*
		 * Questa versione riguarda le stesse combinazioni di giocatori più volte, e richiede mooolto tempo.
		 * Riesce a terminare in tempi acettabili solo su grafi molto piccoli, con meno di 10 vertici. La versione 
		 * ottimizzata di sotto riesce a gestire velocemente anche grafi con 40-50 vertici.
		 */
//		for (People p : rimanenti) {
//			List<People> currentRimanenti = new ArrayList<>(rimanenti);
//				parziale.add(p);
//				currentRimanenti.removeAll(Graphs.neighborListOf(this.grafo, p));
//				currentRimanenti.remove(p);
//				ricorsione(parziale, currentRimanenti);
//				parziale.remove(parziale.size()-1);
//		}
		
		
		/*
		 * VERSIONE OTTIMIZZATA DELLA RICORSIONE
		 */
		/*
		 * Rispetto alla versione non ottimizzata, qui l'idea è di ciclare su una squadra alla volta
		 * piuttosto che su tutti i vertici del grafo, rimuovendo così molti casi.
		 * Per selezionare una squadra potremmo prendere un vertice, e poi prendere tutti i suoi vicini.
		 * Però alcuni di questi vertici (giocatori) potrebbero aver giocato per 2 squadre in un anno,
		 * perciò se selezionassimo i suoi vicini prenderemmo due squadre invece di una.
		 * Per evitare questo problema, andiamo prima a prendere un vertice qualsiasi, con tutti i suoi vicini.
		 * Poi, tra questi prendiamo un vertice di grado minimo, e andiamo a calcolare i suoi vicini.
		 * L'alternativa sarebbe di fare, nel metodo calcolaDreamTeam(), un sort dei vertici in 'rimanente' in ordine crescente del loro grado
		 * e poi selezionare sempre il primo.
		 */
		List<People> squadra =  Graphs.neighborListOf(this.grafo, rimanenti.get(0));
		squadra.add( rimanenti.get(0));
		People startP = minDegreeVertex(squadra);
		List<People> squadraMin =  Graphs.neighborListOf(this.grafo, rimanenti.get(0));
		squadraMin.add( rimanenti.get(0));
		
		for (People p : squadraMin) {
			List<People> currentRimanenti = new ArrayList<>(rimanenti);
			parziale.add(p);
			currentRimanenti.removeAll(squadraMin);
			ricorsione(parziale, currentRimanenti);
			parziale.remove(parziale.size()-1);
		}
	}
	
	
	/**
	 * Metodo che calcola il salario nell'anno di una lista di giocatori
	 * Usato nella ricorsione, per calcolare il salario del Dream Team
	 * @param team
	 * @return
	 */
	private double getSalarioTeam(List<People> team) {
		double result = 0.0;
		for (People p : team) {
			result += this.salariesIDMap.get(p);
		}
		return result;
	}
	
	
	/**
	 * Metodo per calcolare il vertice di grado minimo tra un insieme di vertici
	 * @param squadra
	 * @return
	 */
	private People minDegreeVertex(List<People> squadra) {
		People res = null;
		int gradoMin = -1;
		for (People p : squadra) {
			int grado = Graphs.neighborListOf(this.grafo, p).size();
			if (gradoMin==-1 || grado<gradoMin) {
				res = p;
			}
		}		
		return res;
	}
	
	
	public double getSalarioDreamTeam() {
		return this.salarioDreamTeam;
	}
	
	
	public List<People> getDreamTeam() {
		return this.dreamTeam;
	}
	
	public List<Team> getPeopleTeams(People player){
		return this.playerTeamsMap.get(player);

	}
	
	public double getSalarioPlayer(People p) {
		return this.salariesIDMap.get(p);
	}
}